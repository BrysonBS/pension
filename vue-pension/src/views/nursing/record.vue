<template>
  <div class="app-container">
    <!-- 查询参数-->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="申请人" prop="applyName">
        <el-input
          v-model="queryParams.applyName"
          placeholder="申请人"
          clearable
          style="width: 240px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="单号" prop="orderNumber">
        <el-input
          v-model="queryParams.orderNumber"
          placeholder="单号"
          clearable
          style="width: 240px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="护理人员" prop="personId">
        <el-select
          v-model="queryParams.personId"
          placeholder="护理人员"
          filterable clearable
          style="width: 240px">
          <el-option
            v-for="item in personOptions"
            :key="item.id"
            :label="item.name"
            :value="item.id">
            <span style="float: left">{{ item.name }}</span>
            <span style="float: right; color: #8492a6; font-size: 13px">{{ item.phone }}</span>
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="申请时间">
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
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['nursing:record:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['nursing:record:remove']"
        >删除</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" :columns="columns"></right-toolbar>
    </el-row>

    <!--列表-->
    <el-table ref="tables" v-loading="loading" :data="list"
              @selection-change="handleSelectionChange"
              :default-sort="defaultSort"
              @sort-change="handleSortChange"
              @expand-change="getExpandData"
    >
      <el-table-column type="expand">
        <template slot-scope="props">
          <el-form label-position="left" inline class="demo-table-expand">
            <el-form-item label="开始时间">
              <span>{{ props.row.beginTime }}</span>
            </el-form-item>
            <el-form-item label="结束时间">
              <span>{{ props.row.endTime }}</span>
            </el-form-item>
            <el-form-item label="护理项目">
              <span>
                 <el-tag type="success"
                         size="small"
                    v-for="item in dict.type.nusing_service_items"
                    v-if="ifContain(props.row,item.value)"
                 >{{item.label}}</el-tag>
              </span>
            </el-form-item>
            <el-form-item label="附件"
                v-if="props.row.uploads && props.row.uploads.length > 0"
            >
                <el-link
                  @click="handleDowonload(item)"
                  v-for="item in props.row.uploads"
                  type="primary" style="font-size: 12px;margin-right: 10px;" icon="el-icon-document"
                >{{item.fileName}}</el-link>
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
      <el-table-column width="150" label="单号" align="center" prop="orderNumber" v-if="columns[1].visible" :show-overflow-tooltip="true" />
      <el-table-column width="100" label="申请人" align="center" prop="applyName" v-if="columns[2].visible" :show-overflow-tooltip="true" />
      <el-table-column width="180" label="申请时间" align="center" prop="applyTime" v-if="columns[3].visible" :show-overflow-tooltip="true" />
      <el-table-column width="auto" label="电话" align="center" prop="phone" v-if="columns[4].visible" :show-overflow-tooltip="true" />
      <el-table-column width="auto" label="归属" align="center" prop="deptName" v-if="columns[5].visible" :show-overflow-tooltip="true" />
      <el-table-column width="auto" label="护理人员" align="center" prop="personName" v-if="columns[6].visible" :show-overflow-tooltip="true" />
      <el-table-column width="100" label="护理级别" align="center" v-if="columns[7].visible" :show-overflow-tooltip="true" >
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
    <el-dialog :title="title" :visible.sync="open" width="650px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="申请人" prop="applyName">
              <el-input disabled v-model="form.applyName" maxlength="30" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="申请时间" prop="applyTime">
              <el-date-picker
                disabled
                v-model="form.applyTime"
                value-format="yyyy-MM-dd HH:mm:ss"
                type="datetime"
                placeholder="选择日期时间">
              </el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="所属" prop="deptId">
              <el-input disabled v-model="form.deptName" maxlength="30" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="电话" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入电话" maxlength="30" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="开始时间" prop="beginTime">
              <el-date-picker
                v-model="form.beginTime"
                value-format="yyyy-MM-dd HH:mm:ss"
                type="datetime"
                placeholder="选择日期时间">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束时间" prop="endTime">
              <el-date-picker
                v-model="form.endTime"
                value-format="yyyy-MM-dd HH:mm:ss"
                type="datetime"
                placeholder="选择日期时间">
              </el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="护理人员">
              <el-select
                value-key="id"
                @change="changeSelect"
                v-model="personOption"
                filterable clearable  placeholder="请选择护理人员">
                <el-option
                  v-for="item in personOptions"
                  :key="item.id"
                  :label="item.name"
                  :value="item">
                  <span style="float: left">{{ item.name }}</span>
                  <span style="float: right; color: #8492a6; font-size: 13px">{{ item.phone }}</span>
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="护理级别">
              <el-select v-model="form.dictLevelId" disabled clearable  placeholder="请选择护理级别">
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
          <el-col :span="24">
            <el-form-item label="护理项目">
              <el-select style="width:100%"
                @change="$forceUpdate()"
                v-model="form.dictServiceIds" multiple filterable placeholder="请选择护理项目">
                <el-option
                  v-for="item in dict.type.nusing_service_items"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="附件" prop="attachments">
              <el-upload
                class="upload-demo"
                ref="upload"
                action=""
                :http-request="handleUpload"
                :on-preview="handlePreview"
                :before-remove="beforeRemove"
                multiple
                :limit="10"
                :on-exceed="handleExceed"
                :auto-upload="false"
                :file-list="fileList">
                <el-button size="mini" type="primary" icon="el-icon-upload2"></el-button>
                <span slot="tip" class="el-upload__tip">上传文件总大小不能超过32M</span>
              </el-upload>
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
import { addTouchmove } from '@/api/tool/adaptor'
import { addRecord, addRecordInit, delRecord, getDetail, listRecord } from '@/api/nursing/record'
import { download } from '@/utils/request'
import { listPersonAll } from '@/api/nursing/person'

export default {
  name: 'record',
  dicts: ['nursing_level','nusing_service_items'],
  data(){
    return{
      fileList:[],
      //护理人员下拉列表
      personOptions:[],
      //选中的值
      personOption: undefined,

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
      formData:undefined,
      // 列信息
      columns: [
        { key: 0, label: `编号`, visible: false },
        { key: 1, label: `单号`, visible: true },
        { key: 2, label: `申请人`, visible: true },
        { key: 3, label: `申请时间`, visible: true },
        { key: 4, label: `电话`, visible: true },
        { key: 5, label: `归属`, visible: true },
        { key: 5, label: `护理人员`, visible: true },
        { key: 5, label: `护理级别`, visible: true }
      ],
      // 表单校验
      rules: {
        name: [
          { required: true, message: "姓名称不能为空", trigger: "blur" },
          { min: 2, max: 20, message: '姓名长度必须介于 2 和 20 之间', trigger: 'blur' }
        ],
      },
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        applyName: undefined,
        orderNumber: undefined,
        personId: undefined,

        beginTime:undefined,
        endTime:undefined
      },
    }
  },
  methods:{
    //上传附件相关
    handlePreview(file) {
      //预览
      //console.log(file);
    },
    handleExceed(files, fileList) {
      this.$message.warning(`当前限制选择 10 个文件，本次选择了 ${files.length} 个文件，共选择了 ${files.length + fileList.length} 个文件`);
    },
    beforeRemove(file, fileList) {
      return this.$confirm(`确定移除 ${ file.name }？`);
    },
    //自定义文件上传事件
    handleUpload(params){
      console.log(params);
      this.formData.append('attachments',params.file);
      this.form.totalSize += params.file.size;
    },
    //点击下载文件
    handleDowonload(item){
      let data = {
        rootPath: item.rootPath,
        uri:item.uri,
        fileName:item.fileName
      }
      let url ="/pension/download";
      download(url,data,item.fileName);
    },

    //展开行时获取详细数据
    getExpandData(row,expandedRows){
      getDetail(row.id).then(response => {
        row.dictServiceIds = response.data;
        row.uploads = response.uploads;
      })
    },
    //护理项目是否包含元素
    ifContain(row,key){
      let ids = row.dictServiceIds
      if(ids) return ids.includes(key);
      return false;
    },
    //护理人员选择改变事件
    changeSelect(item){
      this.form.dictLevelId = item.dictLevelId;
      this.form.personId = item.id
      this.form.personName = item.name;
    },
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
    getPersonOptions(){
      listPersonAll().then(response => {
        this.personOptions = response.data;
      }).then(() => this.getList())
    },

    /** 查询设备列表 */
    getList() {
      this.loading = true;
      this.dateRange = this.dateRange || [];
      this.queryParams.beginTime = this.dateRange[0];
      this.queryParams.endTime = this.dateRange[1];
      listRecord(this.queryParams).then(response => {
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
      this.personOption = undefined;
      this.formData = undefined;
      this.fileList = [];
      this.resetForm("form");
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      addRecordInit().then(response => {
        this.form = response.data;
      })

      this.open = true;
      this.title = "添加记录";
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除序号为"' + this.num + '"的数据项？').then(function() {
        return delRecord(ids);
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
/*            updatePerson(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });*/
          } else {
            this.formData = new FormData();
            this.form.totalSize = 0;
            this.$refs.upload.submit();
            //文件大小限制
            if(this.form.totalSize > (1 << 25)) {//32M
              this.$modal.msgError("上传文件总大小超过32M限制!");
              return;
            }
            const json = JSON.stringify(this.form);
            this.formData.append('record', new Blob([json],{type:'application/json'}));
            if(!this.formData.get('attachments')) //没有附件,就创建空对象
              this.formData.append('attachments', new Blob());
            addRecord(this.formData).then(response => {
              this.$modal.msgSuccess("提交成功");
              this.open = false;
              this.getList();
            })
          }
        }
      });
    },
  },
  created() {
    //护理人员下拉菜单
    this.getPersonOptions();
  }
}
</script>

<style>
.el-upload__tip{
  margin-left: 5px;
  color:#909399;
}
.el-date-editor,.el-select{
  width: 100%;
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
.el-tag{
  margin-right: 5px;
}
</style>
