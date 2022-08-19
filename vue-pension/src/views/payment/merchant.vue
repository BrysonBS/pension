<template>
  <div class="app-container">
    <!-- 搜索条件 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item style="line-height: 30px" label="归属" prop="deptId">
        <treeselect style="max-width: 240px" id="payment-dept-id" v-model="queryParams.deptId" :options="deptOptions" :show-count="true" placeholder="请选择归属" />
      </el-form-item>
      <el-form-item label-width="70px" label="APPID" prop="appId">
        <el-input
          v-model="queryParams.appId"
          placeholder="请输入APPID"
          clearable
          @clear="resetQuery"
          style="width: 240px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select
          v-model="queryParams.status"
          placeholder="状态"
          clearable
          @clear="resetQuery"
          style="width: 240px"
        >
          <el-option
            v-for="dict in dict.type.sys_normal_disable"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>
    <!-- 操作 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['payment:merchant:add']"
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
          v-hasPermi="['payment:merchant:edit']"
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
          v-hasPermi="['payment:merchant:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-refresh"
          size="mini"
          @click="handleRefreshCache"
          v-hasPermi="['payment:merchant:remove']"
        >刷新缓存</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>
    <!-- 列表数据 -->
    <el-table v-loading="loading" :data="list" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="序号" width="50px" align="center">
        <template slot-scope="scope">
          <span>{{scope.row.num = scope.$index + (queryParams.pageNum - 1) * queryParams.pageSize + 1}}</span>
        </template>
      </el-table-column>
      <el-table-column label="编号" width="50" align="center" :key="columns[0].key" :prop="columns[0].prop" v-if="columns[0].visible" />
      <el-table-column width="100" label="商户类别" align="center" v-if="columns[1].visible">
        <template slot-scope="scope">
          <el-tag size="small" effect="dark"
                  v-if="scope.row.payType == 1"
          >支付宝商户</el-tag>
          <el-tag type="success" size="small" effect="dark"
                  v-if="scope.row.payType == 2"
          >微信商户</el-tag>
        </template>
      </el-table-column>
      <el-table-column width="auto" label="归属" align="center" :key="columns[2].key" :prop="columns[2].prop" v-if="columns[2].visible"/>
      <el-table-column width="auto" label="APPID" align="center" :key="columns[4].key" :prop="columns[4].prop" v-if="columns[4].visible" :show-overflow-tooltip="true" />
      <el-table-column width="80" label="证书模式" align="center" v-if="columns[3].visible">
        <template slot-scope="scope">
          <el-tag type="success" size="small"
                  v-if="scope.row.certModel"
          >是</el-tag>
          <el-tag type="success" size="small"
                  v-if="!scope.row.certModel"
          >否</el-tag>
        </template>
      </el-table-column>
      <el-table-column width="80" label="状态" align="center" key="status" v-if="columns[5].visible">
        <template v-slot="scope">
          <el-switch
            v-model="scope.row.status"
            active-value="0"
            inactive-value="1"
            @change="handleStatusChange(scope.row)"
          ></el-switch>
        </template>
      </el-table-column>
      <el-table-column width="auto" label="备注" align="center" :key="columns[6].key" :prop="columns[6].prop" v-if="columns[6].visible" :show-overflow-tooltip="true" />
    </el-table>
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改用户配置对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="700px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="类别" prop="payType">
              <el-radio-group :disabled="form.id !== undefined" v-model="form.payType" size="small">
                <el-radio-button label="1">支付宝商户</el-radio-button>
                <el-radio-button label="2">微信商户</el-radio-button>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="归属" prop="deptId">
              <treeselect v-model="form.deptId" :options="deptOptions" :show-count="true" placeholder="请选择归属部门" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="APPID" prop="appId">
              <el-input v-model="form.appId" placeholder="请输入APPID" />
            </el-form-item>
          </el-col>
        </el-row>
        <div v-if="form.payType == 2">
          <el-row>
            <el-col :span="24">
              <el-form-item label="商户号" prop="merchantId">
                <el-input v-model="form.merchantId" placeholder="请输入商户号" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="24">
              <el-form-item label="商户证书序列号" prop="merchantSerialNumber">
                <el-input v-model="form.merchantSerialNumber" placeholder="请输入商户证书序列号" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="24">
              <el-form-item label="APIv3密钥" prop="apiV3Key">
                <el-input v-model="form.apiV3Key" placeholder="请输入APIv3密钥" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="24">
              <el-form-item label="证书上传" prop="attachments">
                <el-upload
                  class="upload-demo"
                  ref="upload"
                  action=""
                  :http-request="handleUpload"
                  :before-remove="beforeRemove"
                  multiple
                  :limit="3"
                  :on-exceed="handleExceed"
                  :auto-upload="false"
                  :file-list="fileList">
                  <el-button size="mini" type="primary" icon="el-icon-upload2"></el-button>
                  <span slot="tip" class="el-upload__tip"> {{wtip}}</span>
                </el-upload>
              </el-form-item>
            </el-col>
          </el-row>
        </div>
        <div v-if="form.payType == 1">
          <el-row>
            <el-col :span="24">
              <el-form-item label="应用私钥" prop="appPrivateKey">
                <el-input v-model="form.appPrivateKey" type="textarea" placeholder="请输入应用私钥" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="12">
              <el-form-item label="证书模式" prop="certModel">
                <el-radio-group :disabled="form.id !== undefined" v-model="form.certModel" size="mini">
                  <el-radio-button :label="true">是</el-radio-button>
                  <el-radio-button :label="false">否</el-radio-button>
                </el-radio-group>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row v-if="!form.certModel">
            <el-col :span="24">
              <el-form-item label="支付宝公钥" prop="alipayPublicKey">
                <el-input v-model="form.alipayPublicKey" type="textarea" placeholder="请输入支付宝公钥" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row v-if="form.certModel">
            <el-col :span="24">
              <el-form-item label="证书上传" prop="attachments">
                <el-upload
                  class="upload-demo"
                  ref="upload"
                  action=""
                  :http-request="handleUpload"
                  :before-remove="beforeRemove"
                  multiple
                  :limit="3"
                  :on-exceed="handleExceed"
                  :auto-upload="false"
                  :file-list="fileList">
                  <el-button size="mini" type="primary" icon="el-icon-upload2"></el-button>
                  <span slot="tip" class="el-upload__tip"> {{tip}}</span>
                </el-upload>
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <el-row>
          <el-col :span="12">
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
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import "@riophae/vue-treeselect/dist/vue-treeselect.css";
import Treeselect from '@riophae/vue-treeselect'
import { treeselect } from '@/api/system/dept'
import {
  addMerchant, changeMerchantStatus,
  delMerchant,
  getMerchant,
  listMerchant,
  refreshMerchantCache,
  updateMerchant
} from '@/api/payment/merchant'

export default {
  name: 'index',
  dicts: ['sys_normal_disable'],
  components: {Treeselect },
  data(){
    return{
      tip: undefined,
      wtip:undefined,
      fileList:[],
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
      formData:undefined,
      // 默认排序
      defaultSort: {prop: 'id', order: 'ascending'},
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        appId:undefined,
        deptId:undefined,
        status:undefined,
        certModel:undefined
      },
      // 列信息
      columns: [
        { key: 0,prop: 'id',overflow:true, label: `编号`, visible: false },
        { key: 1,prop: 'payType',overflow:true, label: `支付类型`, visible: true },
        { key: 2,prop: 'deptName',overflow:true, label: `归属`, visible: true },
        { key: 3,prop: 'certModel',overflow:true, label: `证书模式`, visible: true },
        { key: 4,prop: 'appId',overflow:true, label: `APPID`, visible: true },
        { key: 5,prop: 'status',overflow:true, label: `状态`, visible: true },
        { key: 6,prop: 'remark',overflow:true, label: `备注`, visible: true },
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
    this.getTreeselect();
  },
  methods:{
    handleExceed(files, fileList) {
      this.$message.warning(`当前限制选择 3 个文件，本次选择了 ${files.length} 个文件，共选择了 ${files.length + fileList.length} 个文件`);
    },
    beforeRemove(file, fileList) {
      return this.$confirm(`确定移除 ${ file.name }？`)
        .then(resolve => {
          this.form.del += file.pid;
        })
    },
    //自定义文件上传事件
    handleUpload(params){
      this.formData.append('attachments',params.file);
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
      listMerchant(this.queryParams).then(response => {
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
      this.formData = undefined;
      this.fileList = [];
      this.resetForm("form");
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.tip = "请按顺序添加:支付宝公钥证书>支付宝根证书>应用公钥证书";
      this.wtip = "请按顺序添加:xxx_cert.p12>xxx_cert.pem>xxx_key_pem";
      this.title = "添加商户";
      this.form = {payType:1,certModel:false}
    },

    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids;
      getMerchant(id).then(response => {
        const payment = response.data;
        if(payment.certModel && payment.payType === 1){//证书模式,添加文件
          this.fileList.push(
            {pid:'0', name:this.getFileName(payment.alipayCertPath), url:""},
            {pid:'1', name:this.getFileName(payment.alipayRootCertPath), url:""},
            {pid:'2', name:this.getFileName(payment.appCertPath), url:""}
          );
        }
        if(payment.certModel && payment.payType === 2){ //微信证书模式
          this.fileList.push(
            {pid:'0', name:this.getFileName(payment.wechatpayCertP12Path), url:""},
            {pid:'1', name:this.getFileName(payment.wechatpayCertPemPath), url:""},
            {pid:'2', name:this.getFileName(payment.wechatpayKeyPemPath), url:""}
          );
        }
        this.form = payment;
        this.open = true;
        this.tip = this.wtip = "修改证书必须先移除所有,再重新添加全部证书";
        this.title = "修改商户";
      });
    },
    getFileName(path){
        return path.substring(path.lastIndexOf('/') + 1,path.lastIndexOf('_')) + path.substring(path.lastIndexOf('.'));
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除序号为"' + this.num + '"的数据项？').then(function() {
        return delMerchant(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 刷新缓存按钮操作 */
    handleRefreshCache() {
      refreshMerchantCache().then(() => {
        this.$modal.msgSuccess("刷新成功");
      })
    },
    /** 提交按钮 */
    submitForm: function() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          this.formData = new FormData();
          if(this.$refs.upload !== undefined) this.$refs.upload.submit();
          const json = JSON.stringify(this.form);
          //添加表单内容
          this.formData.append('record', new Blob([json],{type:'application/json'}));
          if(!this.formData.get('attachments')) //没有附件,就创建空对象
            this.formData.append('attachments', new Blob());

          if (this.form.id != undefined) {
            updateMerchant(this.formData).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            //新增
            addMerchant(this.formData).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    // 网关状态修改
    handleStatusChange(row) {
      let text = row.status === "0" ? "启用" : "停用";
      this.$modal.confirm('确认要"' + text + '"序号为"' + row.num + '"的商户吗？').then(function() {
        return changeMerchantStatus(row.id, row.status);
      }).then(() => {
        this.$modal.msgSuccess(text + "成功");
      }).catch(function() {
        row.status = row.status === "0" ? "1" : "0";
      });
    },
  }
}
</script>

<style>
.el-upload__tip{
  margin-left: 5px;
  color:#909399;
}
#payment-dept-id{
  width: 240px;
  height: 30px;
}
#payment-dept-id .vue-treeselect__control{
  height: 30px !important;
  line-height: 30px;
}
#payment-dept-id input{
  font-size: 13px;
}

</style>
