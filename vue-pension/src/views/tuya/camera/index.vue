<template>
  <div class="app-container">
    <el-row :gutter="20">
      <!--部门数据-->
      <el-col :span="4" :xs="24">
        <div class="head-container">
          <el-input
            v-model="deptName"
            placeholder="请输入部门名称"
            clearable
            size="small"
            prefix-icon="el-icon-search"
            style="margin-bottom: 20px"
          />
        </div>
        <div class="head-container">
          <el-tree
            :data="deptOptions"
            :props="defaultProps"
            :expand-on-click-node="false"
            :filter-node-method="filterNode"
            ref="tree"
            default-expand-all
            @node-click="handleNodeClick"
          />
        </div>
      </el-col>
      <!--设备数据-->
      <el-col :span="20" :xs="24">
        <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
          <el-form-item label="名称" prop="name">
            <el-input
              v-model="queryParams.name"
              placeholder="请输入设备名称"
              clearable
              style="width: 240px"
              @keyup.enter.native="handleQuery"
            />
          </el-form-item>
          <el-form-item label="状态" prop="online">
            <el-select
              v-model="queryParams.online"
              placeholder="状态"
              clearable
              style="width: 240px"
            >
              <el-option
                v-for="dict in dict.type.sys_online_status"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item label-width="70px" label="序列号" prop="deviceId">
            <el-input
              v-model="queryParams.deviceId"
              placeholder="请输入设备序列号"
              clearable
              style="width: 240px"
              @keyup.enter.native="handleQuery"
            />
          </el-form-item>
          <el-form-item style="line-height: 30px" label="归属" prop="deptId">
            <treeselect id="search-dept-id" v-model="queryParams.deptId" :options="deptOptions" :show-count="true" placeholder="请选择归属部门" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
            <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>

        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button
              type="success"
              plain
              icon="el-icon-edit"
              size="mini"
              :disabled="checkList.length !== 1"
              @click="handleUpdate"
              v-hasPermi="['tuya:device:edit']"
            >修改</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button
              type="danger"
              plain
              icon="el-icon-delete"
              size="mini"
              :disabled="checkList.length === 0"
              @click="handleDelete"
              v-hasPermi="['tuya:device:remove']"
            >删除</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button
              type="danger"
              plain
              icon="el-icon-refresh"
              size="mini"
              @click="handleRefreshCache"
              v-hasPermi="['tuya:device:refresh']"
            >刷新</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button
              type="warning"
              plain
              icon="el-icon-edit"
              size="mini"
              @click="handleSelect"
            >{{ this.checkList.length === this.deviceList.length ? '取消' : '全选' }}</el-button>
          </el-col>
          <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>

        <el-row >
          <el-checkbox-group v-model="checkList">
          <el-col v-for="e in deviceList" style="margin: 0px 8px 8px 0; max-width: 220px;text-align: center">
            <el-card
              shadow="hover"
              @click.native="selectCard(e)"
              :class="{active:checkList.includes(e.id)}"
              :body-style="{ padding: '0px',position: 'relative' }">
              <div style="position: absolute;left: 3px;top: 2px">
                <el-checkbox :label="e.id"><br/></el-checkbox>
              </div>
                <div style="padding-top: 8px">
                  <span style="font-size: 14px">{{e.name}}</span>
                  <vue-loaders name="ball-scale-multiple" :color="e.online ? 'green' : 'red'" scale=".4"></vue-loaders>
                </div>
                <img style="width: 100px;height: 100px;user-select: none" :src="'https://images.tuyacn.com/' + e.icon" class="image">
                <div style="padding: 14px;">
                  <el-button type="primary" size="mini" icon="el-icon-video-play" @click.stop="connect(e.uid,e.deviceId,e.name)">连接</el-button>
                </div>
              </el-card>
          </el-col>
          </el-checkbox-group>
        </el-row>
        <pagination
          v-show="total>0"
          :hide-on-single-page="true"
          :total="total"
          :page.sync="queryParams.pageNum"
          :limit.sync="queryParams.pageSize"
          @pagination="getList"
        />
      </el-col>
    </el-row>

    <!-- 添加或修改对话框 -->
    <el-dialog :title="title"
               :close-on-click-modal="false"
               :visible.sync="open" width="700px"
               :modal-append-to-body="false">
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
      <el-row>
        <el-col :span="12">
          <el-form-item label="绑定账号">
            <el-select v-model="form.userId" filterable clearable placeholder="请选择账号" style="width: 100%">
              <el-option
                v-for="item in userOptions"
                :key="item.key"
                :label="item.label"
                :value="item.value">
                <span style="float: left">{{ item.label }}</span>
                <span style="float: right; color: #8492a6; font-size: 13px">{{ item.info }}</span>
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="归属" prop="deptId">
            <treeselect v-model="form.deptId" :options="deptOptions" :show-count="true" placeholder="请选择归属部门" />
          </el-form-item>
        </el-col>
      </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 监控窗口 -->
    <el-dialog :title="title"
               :before-close="handleClose"
               :close-on-click-modal="false"
               :visible.sync="connectOpen" append-to-body>
      <Camera ref="ref_camera"/>
    </el-dialog>

  </div>
</template>

<script>
import Treeselect from '@riophae/vue-treeselect'
import { treeselect } from '@/api/system/dept'
import "@riophae/vue-treeselect/dist/vue-treeselect.css";
import { delTuyaDevice, getById, list, refresh, updateTuyaDevice } from '@/api/tuya/tuyaDevice'
import 'vue-loaders/dist/vue-loaders.css';
import Camera from '@/views/tuya/camera'
import { getUserOptions } from '@/api/device/selectOptions'

export default {
  name: 'index',
  dicts: ['sys_online_status'],
  components: { Treeselect,Camera },
  data() {
    return {
      //选中列表
      checkList: [],
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 设备表格数据
      deviceList: [],
      // 弹出层标题
      title: "",
      //账号下拉菜单
      userOptions:[],
      // 部门树选项
      deptOptions: [],
      connectOpen: false,
      // 是否显示弹出层
      open: false,
      // 部门名称
      deptName: undefined,
      // 表单参数
      form: {},
      defaultProps: {
        children: "children",
        label: "label"
      },
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        category: 'sp',
        name: undefined,
        online: undefined,
        deviceId: undefined,
        deptId: undefined,
      },
      // 列信息
      columns: [
        { key: 0,prop: 'id',overflow:true, label: `编号`, visible: false },
        { key: 1,prop: 'deptId',overflow:true, label: `归属`, visible: true },
        { key: 2,prop: 'name',overflow:true, label: `设备名称`, visible: true },
        { key: 3,prop: 'deviceId',overflow:true, label: `序列号`, visible: true },
        { key: 4,prop: 'online',overflow:true, label: `状态`, visible: true },
      ],
      // 表单校验
      rules: {
        name: [
          { required: true, message: "设备名称不能为空", trigger: "blur" },
          { min: 2, max: 32, message: '用户名称长度必须介于 2 和 32 之间', trigger: 'blur' }
        ]
      }
    }
  },
  watch: {
    // 根据名称筛选部门树
    deptName(val) {
      this.$refs.tree.filter(val);
    }
  },
  created() {
    this.getList();
    this.getTreeselect();
  },
  methods: {
    handleClose(done) {
      done()
      this.disconnect()
    },
    connect(uid,deviceId,name){
      //console.log("connect: ",uid,deviceId)
      this.connectOpen = true
      this.title = name
      this.$nextTick(() => {
        this.$refs.ref_camera.connect(uid,deviceId)
      })
    },
    disconnect(){
      //console.log("disconnect: ",this.open)
      this.$nextTick(() => {
        this.$refs.ref_camera.hangup()
      })
    },
    handleSelect(){
      this.checkList = this.checkList.length === this.deviceList.length ? [] : this.deviceList.map(e => e.id)
    },
    selectCard(item){
      if(this.checkList.includes(item.id))
        this.checkList = this.checkList.filter( id => id !== item.id)
      else this.checkList.push(item.id)
    },
    /** 获取账号下拉菜单 */
    getUserOptions(){
      getUserOptions().then(response => {
        this.userOptions = response.data;
      })
    },
    /** 查询设备列表 */
    getList() {
      this.loading = true;
      list(this.queryParams).then(response => {
          this.deviceList = response.rows;
          this.total = response.total;
          this.loading = false;
        }
      );
    },
    /** 查询部门下拉树结构 */
    getTreeselect() {
      treeselect().then(response => {
        this.deptOptions = response.data;
      });
    },
    // 筛选节点
    filterNode(value, data) {
      if (!value) return true;
      return data.label.indexOf(value) !== -1;
    },
    // 节点单击事件
    handleNodeClick(data) {
      this.queryParams.deptId = data.id;
      this.handleQuery();
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.dateRange = [];
      this.resetForm("queryForm");
      this.handleQuery();
    },
    /** 修改按钮操作 */
    handleUpdate() {
      this.reset()
      this.getTreeselect()
      this.getUserOptions()
      const id = this.checkList[0]
      getById(id).then(response => {
        this.form = response.data
        this.open = true;
        this.title = "修改设备";
      });
    },
    /** 删除按钮操作 */
    handleDelete() {
      const infoIds = this.checkList;
      this.$modal.confirm('是否确认删除？').then(function() {
        return delTuyaDevice(infoIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
        this.checkList = []
      }).catch(() => {});
    },
    /** 刷新按钮操作 */
    handleRefreshCache(){
      refresh().then(() => {
        this.$modal.msgSuccess("刷新成功");
        this.getList()
      })
    },
    /** 提交按钮 */
    submitForm: function() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != undefined) {
            updateTuyaDevice(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            this.$modal.msgSuccess("修改失败");
            this.open = false;
          }
        }
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        id: undefined,
        userId: undefined,
        deptId: undefined,
        name: undefined,
      };
      this.resetForm("form");
    },
  }
}
</script>

<style>
#search-dept-id{
  width: 240px;
  height: 30px;
}
#search-dept-id .vue-treeselect__control{
  height: 30px !important;
  line-height: 30px;
}
#search-dept-id input{
  font-size: 13px;
}
.vue-loaders{
  position: absolute;
  top: 2px;
  width: 14px !important;
  height: 14px !important;
}
.el-dialog{
  width: fit-content;
}
.active {
  border: 1px solid #409EFF;
}
.el-checkbox{
  pointer-events:none;
}
.el-card{
  cursor: pointer;
}
</style>
