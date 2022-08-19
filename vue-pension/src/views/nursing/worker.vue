<template>
  <div class="app-container">
    <!--搜索栏-->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="姓名" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入姓名"
          clearable
          style="width: 240px;"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="性别">
        <el-select v-model="queryParams.gender" placeholder="请选择性别">
          <el-option
            v-for="dict in dict.type.sys_user_sex"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="电话" prop="phone">
        <el-input
          v-model="queryParams.phone"
          placeholder="请输入电话"
          clearable
          style="width: 240px;"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择">
          <el-option
            v-for="dict in dict.type.worker_status"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['nursing:worker:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['nursing:worker:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['nursing:worker:remove']"
        >删除</el-button>
      </el-col>
      <!--      <el-col :span="1.5">
              <el-button
                type="warning"
                plain
                icon="el-icon-download"
                size="mini"
                @click="handleImport"
                v-hasPermi="['nursing:person:import']"
              >导入</el-button>
            </el-col>
            <el-col :span="1.5">
              <el-button
                type="warning"
                plain
                icon="el-icon-download"
                size="mini"
                @click="handleExport"
                v-hasPermi="['nursing:person:export']"
              >导出</el-button>
            </el-col>-->
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" :columns="columns"></right-toolbar>
    </el-row>
    <!--列表-->
    <el-table class="worker-table" ref="tables" v-loading="loading" :data="list"
              @selection-change="handleSelectionChange"
              :default-sort="defaultSort"
              @sort-change="handleSortChange">

      <el-table-column type="expand" width="1">
        <template slot-scope="props">
          <Certificate :worker-id="workerId"/>
        </template>
      </el-table-column>

      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" width="70px" align="center">
        <template slot-scope="scope">
          <span>{{scope.row.num = scope.$index + (queryParams.pageNum - 1) * queryParams.pageSize + 1}}</span>
        </template>
      </el-table-column>
      <el-table-column width="50"  label="编号" align="center" prop="id" v-if="columns[0].visible" />
      <el-table-column width="100" label="账号" align="center" prop="userName" v-if="columns[1].visible" :show-overflow-tooltip="true" />
      <el-table-column width="150" label="所属" align="center" prop="deptName" v-if="columns[2].visible" :show-overflow-tooltip="true" />
      <el-table-column width="100" label="姓名" align="center" prop="name" v-if="columns[3].visible" :show-overflow-tooltip="true" />
      <el-table-column width="50" label="性别" align="center" v-if="columns[4].visible">
        <template slot-scope="scope">
              <span
                v-if="dict.value === scope.row.gender"
                v-for="dict in dict.type.sys_user_sex"
              >{{dict.label}}</span>
        </template>
      </el-table-column>
      <el-table-column width="150" label="电话" align="center" prop="phone" v-if="columns[5].visible" :show-overflow-tooltip="true" />
      <el-table-column width="80" label="状态" align="center" v-if="columns[6].visible" :show-overflow-tooltip="true" >
        <template slot-scope="scope">
          <el-tag type="success" size="small"
                  v-for="dict in dict.type.worker_status"
                  v-if="dict.value === scope.row.status"
          >{{dict.label}}</el-tag>
        </template>
      </el-table-column>
      <el-table-column width="80" label="证书" align="center" :show-overflow-tooltip="true">
        <template slot-scope="scope">
          <el-button size="small" type="text" @click="handleToogleExpand(scope.row)">
            查看
          </el-button>
        </template>
      </el-table-column>
      <el-table-column width="auto" label="备注" align="center" prop="remark" v-if="columns[7].visible" :show-overflow-tooltip="true" />
    </el-table>
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="绑定账号">
              <el-select v-model="form.userId" filterable placeholder="请选择账号">
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
        <el-row>
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="form.name" placeholder="请输入姓名" maxlength="30" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别">
              <el-select v-model="form.gender" placeholder="请选择性别">
                <el-option
                  v-for="dict in dict.type.sys_user_sex"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="电话" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入电话" maxlength="30" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" v-if="form.id !== undefined">
              <el-select v-model="form.status" placeholder="请选择状态">
                <el-option
                  v-for="dict in dict.type.worker_status"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" type="textarea" placeholder="请输入备注"/>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  addWorker,
  delWorker,
  getWorker,
  listWorker,
  updateWorker
} from '@/api/nursing/worker'
import { getUserOptions } from '@/api/device/selectOptions'
import { treeselect } from '@/api/system/dept'
import "@riophae/vue-treeselect/dist/vue-treeselect.css";
import Treeselect from '@riophae/vue-treeselect'
import Certificate from '@/views/nursing/Certificate'
export default {
  name: 'worker',
  dicts: [ 'sys_user_sex','worker_status'],
  components: { Certificate, Treeselect },
  data(){
    return{
      //账号下拉菜单
      userOptions:[],
      // 部门树选项
      deptOptions: [],
      num: undefined,
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 总条数
      total: 0,
      // 表格数据
      list: [],
      // 表单参数
      form: {},
      // 默认排序
      defaultSort: {prop: 'id', order: 'ascending'},
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        userId:undefined,
        deptId:undefined,
        gender:undefined,
        name:undefined,
        phone:undefined,
        status:undefined
      },
      // 列信息
      columns: [
        { key: 0, label: `编号`, visible: false },
        { key: 1, label: `账号`, visible: true },
        { key: 2, label: `所属`, visible: true },
        { key: 3, label: `姓名`, visible: true },
        { key: 4, label: `性别`, visible: true },
        { key: 5, label: `电话`, visible: true },
        { key: 6, label: `状态`, visible: true },
        { key: 7, label: `备注`, visible: true },
      ],
      // 表单校验
      rules:{
        name: [
          { required: true, message: "姓名称不能为空", trigger: "blur" },
          { min: 2, max: 20, message: '姓名长度必须介于 2 和 20 之间', trigger: 'blur' }
        ],
        phone: [
          {
            pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/,
            message: "请输入正确的手机号码",
            trigger: "blur"
          }
        ]
      }
    }
  },
  created() {
    this.getList();
    this.getUserOptions();
    this.getTreeselect();
  },
  methods:{
    //按钮触发展开行,证书详情
    handleToogleExpand(row) {
      this.workerId = row.id;//workerId传入Certificate组件
      let $table = this.$refs.tables;
      this.list.map((item) => {
        if (row.id != item.id) {
          $table.toggleRowExpansion(item, false);
        }
      })
      $table.toggleRowExpansion(row);
    },
    /** 获取账号下拉菜单 */
    getUserOptions(){
      getUserOptions({"type":"01"}).then(response => {
        this.userOptions = response.data;
      })
    },
    /** 查询部门下拉树结构 */
    getTreeselect() {
      treeselect().then(response => {
        this.deptOptions = response.data;
      });
    },
    /** 查询设备列表 */
    getList() {
      this.loading = true;
      listWorker(this.queryParams).then(response => {
          this.list = response.rows;
          this.total = response.total;
          this.loading = false;
        }
      );
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id);
      this.num = selection.map(item => item.num).sort((a,b) => parseInt(a) - parseInt(b))
      this.single = selection.length != 1;
      this.multiple = !selection.length;
    },
    /** 排序触发事件 */
    handleSortChange(column, prop, order) {
      this.queryParams.orderByColumn = column.prop;
      this.queryParams.isAsc = column.order;
      this.getList();
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {};
      this.resetForm("form");
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加人员";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids;
      getWorker(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改人员";
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除序号为"' + this.num + '"的数据项？').then(function() {
        return delWorker(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 提交按钮 */
    submitForm: function() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != undefined) {
            updateWorker(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addWorker(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
  }
}
</script>

<style>
.worker-table .el-table__expand-icon{
  display: none;
}
</style>
