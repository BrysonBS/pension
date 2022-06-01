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
            :value="item.serialNumber">
            <span style="float: left">{{ item.name }}</span>
            <span style="float: right; color: #8492a6; font-size: 13px">{{ item.typeName }}</span>
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="时间范围">
        <el-date-picker
          @focus="addFocus"
          v-model="dateRange"
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
      <BlglucoseChart :chart-data="chartData"/>
    </el-row>
    <!--结果-->
    <el-row style="padding-left: 10px">
      <el-col v-for="e in showList" style="margin: 8px; max-width: 180px">
        <el-card :body-style="{ padding: '0px'}"  shadow="hover">
          <div style="padding: 10px;text-align: center;">
            <el-tag :type="(e.data3 >= 3.9 && e.data3 <= 6) ? 'success' : 'warning'">血糖: {{e.data3}}</el-tag>
            <div class="bottom clearfix">
              <time class="time">{{ e.checkTime }}</time>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import BlglucoseChart from '@/views/dashboard/health/BlglucoseChart'
import { addTouchmove } from '@/api/tool/adaptor'
import { biolandDeviceList } from '@/api/device/deviceCategories'
import { listBlpressure } from '@/api/health/health'

export default {
  name: 'blglucose',
  components:{
    BlglucoseChart
  },
  data(){
    return{
      showList: [],
      chartData : {
        init: true,
        xAxisData : [],
        data3 : []
      },
      num: undefined,
      // 遮罩层
      loading: true,
      //类别
      classOptions: [],
      // 日期范围
      dateRange: [],
      pickerOptions: this.getPickerOptions(),
      showSearch: true,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        ieee: undefined,
        beginTime:undefined,
        endTime:undefined
      },
    }
  },
  methods: {
    /** 日期时间快捷选项 */
    getPickerOptions() {
      return {
        shortcuts: [{
          text: '今天',
          onClick(picker) {
            const end = new Date();
            const start = new Date(end.getFullYear(), end.getMonth(), end.getDate());
            end.setTime(start.getTime() + 3600 * 1000 * 24);
            picker.$emit('pick', [start, end]);
          }
        }, {
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
    addFocus() {
      addTouchmove(".el-picker-panel")
    },
    /** 获取下拉列表 */
    getClassOptions() {//血糖仪
      biolandDeviceList({ categoriesId: 2 }).then(response => {
        let data = response.data;
        this.classOptions = data;
        if (data.length > 0)
          this.queryParams.ieee = data[0].serialNumber;
      }).then(() => this.getList())
    },
    /** 查询通知列表 */
    getList() {
      this.loading = true;
      this.dateRange = this.dateRange || [];
      this.queryParams.beginTime = this.dateRange[0];
      this.queryParams.endTime = this.dateRange[1];
      listBlpressure(this.queryParams).then(response => {
        this.list = response.data;
        //单位转换
        this.list.map(e => {
          e.data3 = Math.round((e.data3/18)*Math.pow(10, 2))/Math.pow(10, 2);
          return  e;
        })

        this.showList = this.list.slice(0, 500);

        this.chartData.init = true;
        this.chartData.xAxisData.splice(0);
        this.chartData.data3.slice(0);

        for (let e of response.data) {
          this.chartData.xAxisData.unshift(e.checkTime);
          this.chartData.data3.unshift(e.data3);
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
    addBlglucose(e){
      console.log(e)
      if(this.queryParams.ieee !== e.ieee) return;
      //相同设备,则更新
      this.chartData.init = false;
      this.chartData.xAxisData.push(e.ts);
      this.chartData.data3.push(Math.round((e.glucose/18)*Math.pow(10, 2))/Math.pow(10, 2));
    }
  },
  created() {
    this.getClassOptions();
  },
  mounted() {
    //全局事件总线绑定blpressure事件
    this.$bus.$on('blglucose', this.addBlglucose)
  },
  beforeDestroy() {
    //解绑blpressure事件
    this.$bus.$off('blglucose');
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
