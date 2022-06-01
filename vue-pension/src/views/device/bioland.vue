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
          <el-form-item :label="columns[1].label" :prop="columns[1].prop">
            <el-input
              v-model="queryParams.name"
              placeholder="请输入设备名称"
              clearable
              style="width: 240px"
              @keyup.enter.native="handleQuery"
            />
          </el-form-item>
          <el-form-item label-width="70px" :label="columns[2].label" :prop="columns[2].prop">
            <el-input
              v-model="queryParams.serialNumber"
              placeholder="请输入设备序列号"
              clearable
              style="width: 240px"
              @keyup.enter.native="handleQuery"
            />
          </el-form-item>
          <el-form-item label="设备类别" prop="categoriesId">
            <el-select
              v-model="queryParams.categoriesId"
              placeholder="设备类别"
              filterable clearable
              style="width: 240px"
            >
              <el-option
                v-for="item in classOptions"
                :key="item.id"
                :label="item.name"
                :value="item.id">
                <span style="float: left">{{ item.name }}</span>
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item style="line-height: 30px" :label="columns[3].label" :prop="columns[3].prop">
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
              :disabled="single"
              @click="handleUpdate"
              v-hasPermi="['device:bioland:edit']"
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
              v-hasPermi="['device:bioland:remove']"
            >删除</el-button>
          </el-col>
          <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" :columns="columns"></right-toolbar>
        </el-row>

        <el-table v-loading="loading" :data="deviceList" @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="50" align="center" />
          <el-table-column label="序号" width="50px" align="center">
            <template slot-scope="scope">
              <span>{{scope.row.num = scope.$index + (queryParams.pageNum - 1) * queryParams.pageSize + 1}}</span>
            </template>
          </el-table-column>
          <el-table-column :label="columns[0].label" width="50" align="center" :key="columns[0].key" :prop="columns[0].prop" v-if="columns[0].visible" />
          <el-table-column :label="columns[1].label" align="center" :key="columns[1].key" :prop="columns[1].prop" v-if="columns[1].visible" :show-overflow-tooltip="true" />
          <el-table-column :label="columns[4].label" align="center" :key="columns[4].key" :prop="columns[4].prop" v-if="columns[4].visible" :show-overflow-tooltip="true" />
          <el-table-column :label="columns[2].label" align="center" :key="columns[2].key" :prop="columns[2].prop" v-if="columns[2].visible" :show-overflow-tooltip="true" />
          <el-table-column :label="columns[3].label" align="center" :key="columns[3].key" :prop="columns[3].prop" v-if="columns[3].visible" :show-overflow-tooltip="true" />
        </el-table>

        <pagination
          v-show="total>0"
          :total="total"
          :page.sync="queryParams.pageNum"
          :limit.sync="queryParams.pageSize"
          @pagination="getList"
        />
      </el-col>
    </el-row>
    <!-- 添加或修改用户配置对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="设备名称" prop="name">
              <el-input v-model="form.name" placeholder="设备名称" maxlength="30" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="归属部门" prop="deptId">
              <treeselect v-model="form.deptId" :options="deptOptions" :show-count="true" placeholder="请选择归属部门" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-form-item label="设备类别"
                        v-hasPermi="['device:bioland:edit:class']"
          >
            <el-select v-model="form.categoriesId" filterable placeholder="请选择">
              <el-option
                v-for="item in classOptions"
                :key="item.id"
                :label="item.name"
                :value="item.id">
                <span style="float: left">{{ item.name }}</span>
              </el-option>
            </el-select>
          </el-form-item>
        </el-row>
        <el-row :gutter="5">
          <el-form-item
            v-for="(domain, index) in form.phones"
            :label="'手机号' + index"
            :key="index"
            :prop="'phones.' + index + '.phone'"
            :rules="{required:true,pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/g, message: '请输入正确手机号', trigger: 'blur'}"
          >
            <el-col :span="10">
              <el-input v-model="domain.phone" placeholder="手机号" maxlength="30"></el-input>
            </el-col>
            <el-col :span="3">
              <el-button type="danger" icon="el-icon-delete" circle @click.prevent="removeDomain(domain)"></el-button>
            </el-col>
          </el-form-item>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="addDomain">添加手机号</el-button>
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import Treeselect from '@riophae/vue-treeselect'
import { treeselect } from '@/api/system/dept'
import "@riophae/vue-treeselect/dist/vue-treeselect.css";
import { listBiolandDeviceCategories } from '@/api/device/deviceCategories'
import { delBiolandDevice, getBiolandDevice, listBiolandDevice, updateBiolandDevice } from '@/api/device/bioland'

export default {
  name: 'bioland',
  components: { Treeselect },
  data() {
    return {
      //设备类别
      classOptions: [],
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
      // 总条数
      total: 0,
      // 设备表格数据
      deviceList: null,
      // 弹出层标题
      title: "",
      // 部门树选项
      deptOptions: [],
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
        name: undefined,
        serialNumber: undefined,
        deptId: undefined,
        categoriesId: undefined,
      },
      // 列信息
      columns: [
        { key: 0,prop: 'id',overflow:true, label: `编号`, visible: false },
        { key: 1,prop: 'name',overflow:true, label: `设备名称`, visible: true },
        { key: 2,prop: 'serialNumber',overflow:true, label: `序列号`, visible: true },
        { key: 3,prop: 'deptFullName',overflow:true, label: `归属部门`, visible: true },
        { key: 4,prop: 'typeName',overflow:true, label: `设备类别`, visible: true },
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
    this.getClassOptions();
  },
  methods: {
    removeDomain(item) {
      let index = this.form.phones.indexOf(item)
      if (index !== -1) {
        this.form.phones.splice(index, 1)
        if(Number.isInteger(item.id))
          this.form.phonesId.push(item.id)
      }
    },
    addDomain() {
      this.form.phones.push({
        id:'',
        phone: '',
        deviceId: this.form.id
      });
    },
    /** 获取设备类别 */
    getClassOptions(){
      listBiolandDeviceCategories().then(response => {
        this.classOptions = response.data;
      })
    },

    /** 查询设备列表 */
    getList() {
      this.loading = true;
      listBiolandDevice(this.queryParams).then(response => {
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
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id);
      this.num = selection.map(item => item.num).sort((a,b) => parseInt(a) - parseInt(b))
      this.single = selection.length != 1;
      this.multiple = !selection.length;
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      this.getTreeselect();
      const id = row.id || this.ids;
      getBiolandDevice(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改设备";
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const infoIds = row.id || this.ids;
      this.$modal.confirm('是否确认删除访问序号为"' + this.num + '"的数据项？').then(function() {
        return delBiolandDevice(infoIds);
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
            updateBiolandDevice(this.form).then(response => {
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
        deptId: undefined,
        name: undefined,
        phones: [],
        phonesId:[]
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
</style>
