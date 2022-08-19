<template>
  <div class="app-container">
    <!-- 查询参数-->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="85px">
      <el-form-item style="line-height: 30px" label="归属" prop="deptId">
        <treeselect id="search-dept-id" v-model="queryParams.deptId" :options="deptOptions" :show-count="true" placeholder="请选择归属机构" />
      </el-form-item>
      <el-form-item label="护理项目">
        <el-select style="width:100%"
                   @change="$forceUpdate()"
                   v-model="queryParams.dictValues" multiple filterable placeholder="请选择护理项目">
          <el-option
            v-for="item in dict.type.nusing_service_items"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="价格有效期">
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
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择">
          <el-option
            v-for="dict in dict.type.sys_normal_disable"
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
          v-hasPermi="['nursing:service:add']"
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
          v-hasPermi="['nursing:service:edit']"
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
          v-hasPermi="['nursing:service:remove']"
        >删除</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" :columns="columns"></right-toolbar>
    </el-row>
    <!--列表-->
    <el-table ref="tables" v-loading="loading" :data="list"
              @selection-change="handleSelectionChange"
              :default-sort="defaultSort"
              @sort-change="handleSortChange"
    >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" width="70px" align="center">
        <template slot-scope="scope">
          <span>{{scope.row.num = scope.$index + (queryParams.pageNum - 1) * queryParams.pageSize + 1}}</span>
        </template>
      </el-table-column>
      <el-table-column width="50"  label="编号" align="center" prop="id" v-if="columns[0].visible" />
      <el-table-column width="200" label="归属" align="center" prop="deptName" v-if="columns[1].visible" :show-overflow-tooltip="true" />
      <el-table-column width="150" label="护理项目" align="center" prop="dictLabel" v-if="columns[2].visible" :show-overflow-tooltip="true">
        <template v-slot="scope">
          <el-tag type="success" size="small"
                  v-for="dict in dict.type.nusing_service_items"
                  v-if="dict.value === scope.row.dictValue"
          >{{dict.label}}</el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" label="价格有效期">
        <el-table-column width="180" label="开始时间" align="center" prop="periodStart" v-if="columns[3].visible" :show-overflow-tooltip="true" />
        <el-table-column width="180" label="结束时间" align="center" prop="periodEnd" v-if="columns[4].visible" :show-overflow-tooltip="true" />
      </el-table-column>
      <el-table-column width="100" label="价格(元)" align="center" prop="price" v-if="columns[5].visible" :show-overflow-tooltip="true">
        <template v-slot="scope">
          <span>￥{{scope.row.price}}</span>
        </template>
      </el-table-column>
      <el-table-column width="100" label="折扣" align="center" prop="discount" v-if="columns[5].visible" :show-overflow-tooltip="true">
        <template v-slot="scope">
          <el-tag v-if="scope.row.discount !== undefined" effect="dark" size="mini" type="danger">{{scope.row.discount}}折</el-tag>
        </template>
      </el-table-column>
      <el-table-column width="100" label="折后价(元)" align="center" v-if="columns[5].visible" :show-overflow-tooltip="true">
        <template v-slot="scope">
          <span v-if="scope.row.discount !== undefined">￥{{scope.row.price * scope.row.discount / 10.0}}</span>
          <span v-if="scope.row.discount === undefined">￥{{scope.row.price}}</span>
        </template>
      </el-table-column>
      <el-table-column width="100" label="状态" align="center" v-if="columns[7].visible" :show-overflow-tooltip="true" >
        <template v-slot="scope">
          <el-tag :type="dict.raw.listClass" size="small"
                  v-for="dict in dict.type.sys_normal_disable"
                  v-if="dict.value === scope.row.status"
          >{{dict.label}}</el-tag>
        </template>
      </el-table-column>
      <el-table-column width="100" label="创建人" align="center" prop="createBy" v-if="columns[7].visible" :show-overflow-tooltip="true" />
      <el-table-column width="180" label="创建时间" align="center" prop="createTime" v-if="columns[8].visible" :show-overflow-tooltip="true" />
      <el-table-column width="100" label="修改人" align="center" prop="updateBy" v-if="columns[9].visible" :show-overflow-tooltip="true" />
      <el-table-column width="180" label="修改时间" align="center" prop="updateTime" v-if="columns[10].visible" :show-overflow-tooltip="true" />
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="650px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="归属" prop="deptId">
              <treeselect v-model="form.deptId" :options="deptOptions" :show-count="true" placeholder="请选择归属机构" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="护理项目">
              <el-select style="width: 100%" v-model="form.dictValue" placeholder="请选择护理项目">
                <el-option
                  v-for="dict in dict.type.nusing_service_items"
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
            <el-form-item label="开始时间" prop="periodStart">
              <el-date-picker
                style="width: 100%"
                v-model="form.periodStart"
                value-format="yyyy-MM-dd HH:mm:ss"
                type="datetime"
                placeholder="选择日期时间">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item  label="结束时间" prop="periodEnd">
              <el-date-picker
                style="width: 100%"
                v-model="form.periodEnd"
                value-format="yyyy-MM-dd HH:mm:ss"
                type="datetime"
                placeholder="选择日期时间">
              </el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="价格" prop="price">
              <el-input type="number" min=0 v-model="form.price" placeholder="请输入价格" maxlength="30">
                <template slot="append">元</template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="折扣" prop="discount">
              <el-input type="number" max=10 min=0 v-model="form.discount" placeholder="请输入折扣" >
                <template slot="append">折</template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="状态">
              <el-radio-group v-model="form.status">
                <el-radio
                  v-for="dict in dict.type.sys_normal_disable"
                  :key="dict.value"
                  :label="dict.value"
                >{{dict.label}}</el-radio>
              </el-radio-group>
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
        <el-button type="primary" @click="submitForm">提 交</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { treeselect } from "@/api/system/dept";
import Treeselect from "@riophae/vue-treeselect";
import "@riophae/vue-treeselect/dist/vue-treeselect.css";
import { addTouchmove } from '@/api/tool/adaptor'
import {
  addNursingServicePrice,
  delNursingServicePrice, getNursingServicePrice,
  listNurisngServicePrice,
  updateNursingServicePrice
} from '@/api/nursing/service'

export default {
  name: 'service',
  dicts: ['nusing_service_items','sys_normal_disable'],
  components: { Treeselect },
  data(){
    return{
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
      // 日期范围
      dateRange: [],
      pickerOptions: this.getPickerOptions(),
      showSearch: true,
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 总条数
      total: 0,
      // 默认排序
      defaultSort: {prop: 'applyTime', order: 'descending'},
      // 表格数据
      list: [],
      // 表单参数
      form: {},
      // 列信息
      columns: [
        { key: 0, label: `编号`, visible: false },
        { key: 1, label: `归属`, visible: true },
        { key: 2, label: `护理项目`, visible: true },
        { key: 3, label: `开始时间`, visible: true },
        { key: 4, label: `结束时间`, visible: true },
        { key: 5, label: `价格`, visible: true },
        { key: 6, label: `状态`, visible: true },
        { key: 7, label: `创建人`, visible: true },
        { key: 8, label: `创建时间`, visible: true },
        { key: 9, label: `修改人`, visible: true },
        { key: 10, label: `修改时间`, visible: true }
      ],
      // 表单校验
      rules: {
        periodStart:[{ required: true, message: "开始时间必填!", trigger: "blur" },],
        periodEnd:[{ required: true, message: "结束时间必填!", trigger: "blur" },],
        price:[{ required: true, message: "价格必填!", trigger: "blur" },]
      },
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        deptId: undefined,
        dictValues:[],
        status: undefined,
        periodStart:undefined,
        periodEnd:undefined
      },
    }
  },
  created() {
    this.getList()
    this.getTreeselect()
  },
  methods:{
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
    /** 查询部门下拉树结构 */
    getTreeselect() {
      treeselect().then(response => {
        this.deptOptions = response.data;
      });
    },
    /** 查询列表 */
    getList() {
      this.loading = true;
      this.dateRange = this.dateRange || [];
      this.queryParams.periodStart = this.dateRange[0];
      this.queryParams.periodEnd = this.dateRange[1];
      listNurisngServicePrice(this.queryParams).then(response => {
        this.list = response.rows;
        this.total = response.total;
        this.loading = false;
      })
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
      this.dateRange = [];
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
      this.form.status = '0'
      this.open = true;
      this.title = "添加";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids;
      getNursingServicePrice(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改";
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除序号为"' + this.num + '"的数据项？').then(function() {
        return delNursingServicePrice(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 提交按钮 */
    submitForm: function() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != undefined) {//修改
            updateNursingServicePrice(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            })
          } else {
            addNursingServicePrice(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            })
          }
        }
      });
    },
    //护理项目是否包含元素
    ifContain(row,key){
      let ids = row.dictServiceIds
      if(ids) return ids.includes(key);
      return false;
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
.el-table__header .el-table__cell{
  padding: 0;
}
.el-table .el-table__header-wrapper th{
  height: 20px;
}
</style>
