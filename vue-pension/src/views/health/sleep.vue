<template>
  <div class="app-container">
    <!-- 查询参数-->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="设备" >
        <el-select
          v-model="queryParams.ieee"
          placeholder="设备"
          filterable
          style="width: 240px"
        >
          <el-option
            v-for="item in classOptions"
            :key="item.id"
            :label="item.name"
            :value="item.ieee">
            <span style="float: left">{{ item.name }}</span>
            <span style="float: right; color: #8492a6; font-size: 13px">{{ item.displayName }}</span>
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="时间范围">
        <el-date-picker
          v-model="dateRange"
          style="width: 340px"
          value-format="yyyy-MM-dd HH:mm:ss"
          type="datetimerange"
          :picker-options="pickerOptions"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
        ></el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row style="background:#fff;padding:16px 16px 0;margin-bottom:12px;">
      <SleepChart :chart-data="sleepData"/>
    </el-row>
    <!--结果-->
    <el-row style="padding-left: 10px">
      <el-col v-for="e in showList" style="margin: 8px; max-width: 220px">
        <el-card :body-style="{ padding: '0px'}"  shadow="hover">
          <div style="padding: 10px;text-align: center;">
            <el-tag :type="(e.heartRate >= 60 && e.heartRate <= 140) ? 'success' : 'warning'">心率: {{e.heartRate}}</el-tag>
            <el-tag :type="(e.respiratoryRate >= 12 && e.respiratoryRate <= 24) ? 'success' : 'warning'">呼吸: {{e.respiratoryRate}}</el-tag>
            <div class="bottom clearfix">
              <time class="time">{{ e.ts }}</time>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import SleepChart from '@/views/dashboard/health/SleepChart'
import { listSleep } from '@/api/health/sleep'
import { deviceList } from '@/api/device/deviceCategories'
export default {
  name: 'Sleep',
  components: {
    SleepChart
  },
  data(){
    return {
      showList: [],
      sleepData : {
        init: true,
        xAxisData : [],
        heartRateData : [],
        respiratoryRateData : []
      },
      num: undefined,
      // 遮罩层
      loading: true,
      //类别
      classOptions: [],
      list: [],
      // 日期范围
      dateRange: [],
      showSearch: true,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        ieee: undefined,
        beginTime:undefined,
        endTime:undefined
      },
      pickerOptions: this.getPickerOptions()
    }
  },
  methods:{
    /** 日期时间快捷选项 */
    getPickerOptions(){
      return {
        shortcuts: [{
          text: '今天',
          onClick(picker) {
            const end = new Date();
            const start = new Date(end.getFullYear(),end.getMonth(),end.getDate());
            end.setTime(start.getTime() + 3600 * 1000 * 24);
            picker.$emit('pick', [start, end]);
          }
        },{
          text: '最近一周',
          onClick(picker) {
            const end = new Date();
            const start = new Date();
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
            picker.$emit('pick', [start, end]);
          }
        }, {
          text: '最近一个月',
          onClick(picker) {
            const end = new Date();
            const start = new Date();
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
            picker.$emit('pick', [start, end]);
          }
        }, {
          text: '最近三个月',
          onClick(picker) {
            const end = new Date();
            const start = new Date();
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 90);
            picker.$emit('pick', [start, end]);
          }
        }]
      }
    },
    /** 获取设备列表 */
    getClassOptions(){
        deviceList().then(response => {
          let data = response.data;
          this.classOptions = data;
          if(data.length > 0)
            this.queryParams.ieee = data[0].ieee;
        }).then(() => this.getList())
    },
    /** 查询通知列表 */
    getList() {
      this.loading = true;
      this.dateRange = this.dateRange || [];
      this.queryParams.beginTime = this.dateRange[0];
      this.queryParams.endTime = this.dateRange[1];
      listSleep(this.queryParams).then(response => {
        this.list = response.data;

        this.showList = this.list.slice(0,500);

        this.sleepData.init = true;
        this.sleepData.xAxisData.splice(0);
        this.sleepData.heartRateData.splice(0) ;
        this.sleepData.respiratoryRateData.splice(0);
        for(let e of response.data){
          this.sleepData.xAxisData.unshift(e.ts);
          this.sleepData.heartRateData.unshift(e.heartRate);
          this.sleepData.respiratoryRateData.unshift(e.respiratoryRate);
        }
        this.loading = false;
      })
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.dateRange = [];
      this.resetForm("queryForm");
      this.handleQuery();
    },
    addSleep(e){
      if(this.queryParams.ieee !== e.ieee) return;
      //相同设备,则更新
      this.sleepData.init = false;
      this.sleepData.xAxisData.push(e.ts);
      this.sleepData.heartRateData.push(e.heartRate);
      this.sleepData.respiratoryRateData.push(e.respiratoryRate);
    }
  },
  created() {
    this.getClassOptions();
  },
  mounted() {
    //全局事件总线
    this.$bus.$on('sleep', this.addSleep)
  },
  beforeDestroy() {
    //解绑sleep事件
    this.$bus.$off('sleep');
  }
}
</script>

<style scoped>
.el-tag{
  margin-right: 5px;
}
.time {
  font-size: 13px;
  color: #999;
}
.clearfix:before,
.clearfix:after {
  display: table;
  content: "";
}
.clearfix:after {
  clear: both
}
.bottom {
  margin-top: 13px;
  line-height: 12px;
  text-align: right;
}
</style>
