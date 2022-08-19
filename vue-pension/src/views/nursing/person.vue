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
      <el-form-item label="监护人" prop="guardian">
        <el-input
          v-model="queryParams.guardian"
          placeholder="请输入监护人姓名"
          clearable
          style="width: 240px;"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="监护电话" prop="guardianPhone">
        <el-input
          v-model="queryParams.guardianPhone"
          placeholder="请输入监护人电话"
          clearable
          style="width: 240px;"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="身份证号" prop="idCardId">
        <el-input
          v-model="queryParams.idCardId"
          placeholder="请输入身份证号"
          clearable
          style="width: 240px;"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="地址" prop="address">
        <el-input
          v-model="queryParams.address"
          placeholder="请输入地址"
          clearable
          style="width: 240px;"
          @keyup.enter.native="handleQuery"
        />
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
          v-hasPermi="['nursing:person:add']"
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
          v-hasPermi="['nursing:person:edit']"
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
          v-hasPermi="['nursing:person:remove']"
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
    <el-table ref="tables" v-loading="loading" :data="list" @selection-change="handleSelectionChange" :default-sort="defaultSort" @sort-change="handleSortChange">

      <el-table-column type="expand">
        <template slot-scope="props">
          <el-form label-position="left" inline class="demo-table-expand">
            <el-form-item label="身份证号">
              <span>{{ props.row.idCardId }}</span>
            </el-form-item>
            <el-form-item label="地址">
              <span>{{ (props.row.fullAddress || '').concat(props.row.detailAddress || '') }}</span>
            </el-form-item>
            <el-form-item label="病历信息">
              <span>{{ props.row.medicalHistory }}</span>
            </el-form-item>
            <el-form-item label="备注">
              <span>{{ props.row.remark }}</span>
            </el-form-item>
          </el-form>
        </template>
      </el-table-column>

      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" width="70px" align="center">
        <template slot-scope="scope">
          <span>{{scope.row.num = scope.$index + (queryParams.pageNum - 1) * queryParams.pageSize + 1}}</span>
        </template>
      </el-table-column>
      <el-table-column width="50"  label="编号" align="center" prop="id" v-if="columns[0].visible" />
      <el-table-column width="150" label="姓名" align="center" prop="name" v-if="columns[1].visible" :show-overflow-tooltip="true" />
      <el-table-column width="auto" label="电话" align="center" prop="phone" v-if="columns[2].visible" :show-overflow-tooltip="true" />
      <el-table-column width="180" label="监护人" align="center" prop="guardian" v-if="columns[3].visible" :show-overflow-tooltip="true" />
      <el-table-column width="auto" label="监护人电话" align="center" prop="guardianPhone" v-if="columns[4].visible" :show-overflow-tooltip="true" />
      <el-table-column width="100" label="失能等级" align="center" v-if="columns[5].visible" :show-overflow-tooltip="true" >
        <template slot-scope="scope">
          <el-tag size="small"
                  v-for="dict in dict.type.disability_level"
                  v-if="dict.value === scope.row.dictDisabilityLevel"
          >{{dict.label}}</el-tag>
        </template>
      </el-table-column>
      <el-table-column width="100" label="护理级别" align="center" v-if="columns[5].visible" :show-overflow-tooltip="true" >
        <template slot-scope="scope">
               <el-tag type="success" size="small"
                       v-for="dict in dict.type.nursing_level"
                       v-if="dict.value === scope.row.dictLevelId"
               >{{dict.label}}</el-tag>
        </template>
      </el-table-column>
    </el-table>
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="700px" :modal-append-to-body="false">
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="绑定账号">
              <el-select v-model="form.userId" filterable placeholder="请选择账号" style="width: 100%">
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
            <el-form-item label="电话" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入电话" maxlength="30" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="身份证号" prop="idCardId">
              <el-input v-model="form.idCardId" placeholder="请输入身份证号" maxlength="30" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="失能等级">
              <el-select v-model="form.dictDisabilityLevel" filterable clearable  placeholder="请选择失能等级" style="width: 100%">
                <el-option
                  v-for="item in dict.type.disability_level"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="护理级别">
              <el-select v-model="form.dictLevelId" filterable clearable  placeholder="请选择护理级别" style="width: 100%">
                <el-option
                  v-for="item in dict.type.nursing_level"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="监护人" prop="guardian">
              <el-input v-model="form.guardian" placeholder="请输入监护人" maxlength="30" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="监护电话" prop="guardianPhone">
              <el-input v-model="form.guardianPhone" placeholder="请输入监护电话" maxlength="30" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="地址" prop="address">
              <el-input v-model="form.fullAddress" disabled class="input-with-select">
              <el-button slot="prepend" icon="el-icon-location" circle @click="innerOpen=true"></el-button>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="详细地址" prop="address">
              <el-input v-model="form.detailAddress" type="textarea" placeholder="请输入详细地址"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="病历信息" prop="medicalHistory">
              <el-input v-model="form.medicalHistory" type="textarea" placeholder="请输入病历信息"/>
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


    <MapMaker :dialogVisible = "innerOpen" @map-confirm="handleMapConfirm" @map-cancel="handleMapCancel"></MapMaker>

  </div>
</template>

<script>
import { addPerson, delPerson, getPerson, listPerson, updatePerson } from '@/api/nursing/person'
import { treeselect } from '@/api/system/dept'
import Treeselect from '@riophae/vue-treeselect'
import "@riophae/vue-treeselect/dist/vue-treeselect.css";
import { getUserOptions } from '@/api/device/selectOptions'
import BaiduMap from 'vue-baidu-map/components/map/Map.vue'
import {BmNavigation,BmGeolocation,BmView,BmControl,BmAutoComplete,BmLocalSearch} from 'vue-baidu-map'
import MapMaker from '@/views/nursing/MapMaker'

export default {
  name: 'person',
  dicts: ['nursing_level','disability_level'],
  components: {
    MapMaker, Treeselect,BaiduMap,BmNavigation,BmGeolocation, BmView,BmControl,BmAutoComplete,BmLocalSearch

  },
  data() {
    return {
      keyword:'',
      fullAddress: '',
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
      innerOpen:false,
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
        id: undefined,
        name: undefined,
        guardian: undefined,
        guardianPhone: undefined,
        idCardId: undefined,
        address:undefined
      },
      // 列信息
      columns: [
        { key: 0, label: `编号`, visible: false },
        { key: 1, label: `姓名`, visible: true },
        { key: 2, label: `电话`, visible: true },
        { key: 3, label: `监护人`, visible: true },
        { key: 4, label: `监护人电话`, visible: true },
        { key: 5, label: `护理级别`, visible: true }
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
        ],
        guardianPhone: [
          {
            pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/,
            message: "请输入正确的手机号码",
            trigger: "blur"
          }
        ],
        idCardId: [
          {
            pattern: /(^\d{15}$)|(^\d{17}(\d|X)$)/,
            message: "请输入正确的身份证号",
            trigger: "blur"
          }
        ]
      }
    }
  },
  created() {
    this.getList()
    this.getUserOptions()
    this.getTreeselect()
  },
  methods:{
    handleMapConfirm(choosedLocation){
      this.innerOpen = false
      this.form.province = choosedLocation.province
      this.form.city = choosedLocation.city
      this.form.district = choosedLocation.district
      this.form.address = choosedLocation.addr
      this.form.lat = choosedLocation.lat
      this.form.lng = choosedLocation.lng
      this.form.fullAddress = (this.form.province || '') + (this.form.city || '') + (this.form.district || '') + (this.form.address || '')
    },
    handleMapCancel(showMapComponent){
      this.innerOpen = false
    },

    /** 获取账号下拉菜单 */
    getUserOptions(){
      getUserOptions({"type":"00"}).then(response => {
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
      listPerson(this.queryParams).then(response => {
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
      getPerson(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改人员";
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除序号为"' + this.num + '"的数据项？').then(function() {
        return delPerson(ids);
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
            updatePerson(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addPerson(this.form).then(response => {
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

.map{
  width: 100%;
  height: 65vh;
  max-height: 600px;
}
.tangram-suggestion {
  z-index: 9999;
}

.demo-table-expand {
  font-size: 0;
}
.demo-table-expand span{
  word-break: break-all;
}
.demo-table-expand label {
  width: 90px;
  color: #99a9bf;
}
.demo-table-expand .el-form-item {
  margin-right: 0;
  margin-bottom: 0;
  width: 100%;
}
.demo-table-expand .el-form-item__content{
  width:80%;
}
</style>
