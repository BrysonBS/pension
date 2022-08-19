<template>
  <div class="app-container">
    <!-- 查询参数-->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="85px">
      <el-form-item style="line-height: 30px" label="归属" prop="deptId">
        <treeselect id="search-dept-id" v-model="queryParams.deptId" :options="deptOptions" :show-count="true" placeholder="请选择归属机构" />
      </el-form-item>
      <el-form-item label="用户" prop="userName">
        <el-input
          v-model="queryParams.userName"
          placeholder="请输入用户名"
          clearable
          style="width: 240px;"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="单号" prop="orderSn">
        <el-input
          v-model="queryParams.orderSn"
          placeholder="请输入单号"
          clearable
          style="width: 240px;"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="时间">
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
            v-for="dict in dict.type.order_status"
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
          v-hasPermi="['nursing:order:add']"
        >下单</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-top-right"
          size="mini"
          :disabled="single"
          @click="handleDelivery"
          v-hasPermi="['nursing:order:delivery']"
        >派单</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-bottom-right"
          size="mini"
          :disabled="single"
          @click="handleReceive"
          v-hasPermi="['nursing:order:receive']"
        >接单</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-check"
          size="mini"
          :disabled="single"
          @click="handleComplete"
          v-hasPermi="['nursing:order:complete']"
        >结单</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['nursing:order:remove']"
        >删除</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" :columns="columns"></right-toolbar>
    </el-row>

    <!--列表-->
    <el-table ref="tables" v-loading="loading" :data="list"
              @expand-change="handleExpandChange"
              @selection-change="handleSelectionChange" :default-sort="defaultSort"
              @sort-change="handleSortChange">

      <el-table-column type="expand">
        <template slot-scope="props">
          <el-form label-position="left" inline class="demo-table-expand">
            <el-form-item label="老人">
              <span>{{ props.row.name }}</span>
            </el-form-item>
            <el-form-item label="护理项目">
                <el-tag type="success"
                        style="margin-right: 5px"
                        size="small"
                        v-for="item in props.row.orderItems"
                >{{item.dictLabel}}(￥{{item.discountPrice}})</el-tag>
            </el-form-item>
            <el-form-item label="电话">
              <span>{{ props.row.phone }}</span>
            </el-form-item>
            <el-form-item label="监护人">
              <span>{{ props.row.guardian }}</span>
            </el-form-item>
            <el-form-item label="监护人电话">
              <span>{{ props.row.guardianPhone }}</span>
            </el-form-item>
            <el-form-item label="地址">
              <span>{{ (props.row.fullAddress || '') + (props.row.detailAddress || '') }}</span>
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
      <el-table-column min-width="230" label="订单号" align="center" prop="orderSn" v-if="columns[1].visible" />
      <el-table-column min-width="100" label="用户" align="center" prop="name" v-if="columns[2].visible" />
      <el-table-column min-width="150" label="归属" align="center" prop="deptName" v-if="columns[3].visible" />
      <el-table-column min-width="100" label="原价(元)" align="center" prop="totalAmount" v-if="columns[4].visible" >
        <template v-slot="scope">
          <span>￥{{scope.row.totalAmount}}</span>
        </template>
      </el-table-column>
      <el-table-column min-width="100" label="折后价(元)" align="center" prop="payAmount" v-if="columns[5].visible" >
        <template v-slot="scope">
          <span>￥{{scope.row.payAmount}}</span>
        </template>
      </el-table-column>
      <el-table-column min-width="100" label="已退(元)" align="center" prop="payAmount" v-if="columns[6].visible" >
        <template v-slot="scope">
          <span>￥{{scope.row.refundAmount || 0}}</span>
        </template>
      </el-table-column>
      <el-table-column min-width="100" label="支付方式" align="center" v-if="columns[7].visible" >
        <template slot-scope="scope">
          <el-tag size="small" effect="dark"
                  v-if="scope.row.payType == 1"
          ><i class="icon-alipay"/>支付宝</el-tag>
          <el-tag type="success" size="small" effect="dark"
                  v-if="scope.row.payType == 2"
          ><i class="icon-wechatpay"/>微信</el-tag>
        </template>
      </el-table-column>
      <el-table-column min-width="100" label="订单来源" align="center" v-if="columns[8].visible" >
        <template slot-scope="scope">
          <span v-if="scope.row.sourceType == 0">PC</span>
          <span v-if="scope.row.sourceType == 1">APP</span>
        </template>
      </el-table-column>
      <el-table-column min-width="80" label="状态" align="center" key="status" v-if="columns[9].visible">
        <template v-slot="scope">
          <el-tag :type="dict.raw.listClass" size="small"
                  v-for="dict in dict.type.order_status"
                  v-if="dict.value == scope.row.status"
          >{{dict.label}}</el-tag>
        </template>
      </el-table-column>
      <el-table-column min-width="180" label="预计护理时间" align="center" prop="beginTime" v-if="columns[10].visible" />
      <el-table-column min-width="180" label="创建时间" align="center" prop="createTime" v-if="columns[11].visible" />
      <el-table-column
        fixed="right"
        label="操作"
        align="center"
        width="120"
      >
        <template slot-scope="scope">
          <el-button
            v-if="scope.row.status == 0"
            size="mini"
            type="primary"
            icon="icon-qrpay"
            @click="handlePay(scope.row)"
            >去支付</el-button>
            <el-button
              v-show="(scope.row.status == 1 || scope.row.status == 2 || scope.row.status == 3) && (parseFloat(scope.row.payAmount) || 0) - (parseFloat(scope.row.refundAmount) || 0) > 0"
              size="mini"
              type="warning"
              icon="icon-tuikuan"
              @click="handleRefund(scope.row)"
              v-hasPermi="['nursing:order:refund']"
            >退款</el-button>
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
    <el-dialog :title="title" v-loading.fullscreen.lock="dialogLoading" :close-on-click-modal="false" :visible.sync="open" width="650px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="85px">
        <div v-if="form.btn === 0">
          <el-row>
            <el-col :span="12">
              <el-form-item label="护理时间" prop="beginTime">
                <el-date-picker
                  v-model="form.beginTime"
                  value-format="yyyy-MM-dd HH:mm:ss"
                  type="datetime"
                  placeholder="选择护理开始时间">
                </el-date-picker>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="12">
              <el-form-item label="老人" prop="personId">
                <el-select
                  value-key="id"
                  @change="changeSelect"
                  v-model="personOption"
                  filterable clearable  placeholder="请选择老人">
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
              <el-form-item label="地址" prop="address">
                <el-input v-model="form.fullAddress" disabled class="input-with-select">
                  <el-button slot="prepend" icon="el-icon-location" circle @click="innerOpen=true"></el-button>
                </el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="24">
              <el-form-item label="详细地址" prop="detailAddress">
                <el-input v-model="form.detailAddress" type="textarea" placeholder="请输入详细地址"/>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="24">
              <el-form-item label="护理项目" prop="serviceItemsIds">
                <el-select style="width:100%"
                           @change="$forceUpdate()"
                           v-model="form.serviceItemsIds" multiple filterable placeholder="请选择护理项目">
                  <el-option
                    v-for="item in serviceItems"
                    :key="item.id"
                    :label="item.dictLabel + '·￥' + item.discountPrice"
                    :value="item.id"
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
          <el-row>
            <el-col :span="24">
              <el-form-item label="付款方式:" prop="payType">
                <el-radio-group v-model="form.payType">
                  <el-radio-button size="medium" :label="1">
                    <svg-icon icon-class="pay-alipay"></svg-icon>
                  </el-radio-button>
                  <el-radio-button size="medium" :label="2">
                    <svg-icon icon-class="pay-wechatpay"></svg-icon>
                  </el-radio-button>
                </el-radio-group>
              </el-form-item>
            </el-col>
          </el-row>
        </div>
        <div v-if="form.btn === 1">
          <el-row>
            <el-col :span="12">
              <el-form-item label="护工" prop="workerId">
                <el-select
                  value-key="id"
                  v-model="form.workerId"
                  filterable clearable  placeholder="请选择护工">
                  <el-option
                    v-for="item in workerOptions"
                    :key="item.id"
                    :label="item.name"
                    :value="item.id">
                    <span style="float: left">{{ item.name }}</span>
                    <span style="float: right; color: #8492a6; font-size: 13px">{{ item.phone }}</span>
                  </el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
        </div>
        <div v-if="form.btn === 2">

        </div>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <Payment :config="config" @close-dialog="handleCloseDialog"/>
    <MapMaker :dialogVisible = "innerOpen" @map-confirm="handleMapConfirm" @map-cancel="handleMapCancel"></MapMaker>
  </div>
</template>

<script>
import Payment from '@/components/Payment/Payment'
import { treeselect } from '@/api/system/dept'
import "@riophae/vue-treeselect/dist/vue-treeselect.css";
import Treeselect from '@riophae/vue-treeselect'
import { addTouchmove } from '@/api/tool/adaptor'
import MapMaker from '@/views/nursing/MapMaker'
import {
  delNursingServicePrice,
  getNursingServicePrice,
  listNursingServicePriceOwn,
  updateNursingServicePrice
} from '@/api/nursing/service'
import {listPersonOwn } from '@/api/nursing/person'
import {
  completeOrder,
  deliveryOrder,
  delNursingOrder,
  listNurisngOrder,
  listNursingOrderItems, receiveOrder,
  refundOrder
} from '@/api/nursing/order/list'
import { Loading } from 'element-ui'
import { listWorker } from '@/api/nursing/worker'
import { parseStrEmpty } from '@/utils/ruoyi'

export default {
  name: 'list',
  dicts: ['nusing_service_items','order_status','nursing_level'],
  components: {Payment,Treeselect,MapMaker},
  data() {
    return {
      config:undefined,
      loadingInstance: undefined,
      dialogLoading: false,


      //护工列表
      workerOptions:[],
      //老人下拉列表
      personOptions:[],
      //选中的值
      personOption: undefined,
      //护理项目列表
      serviceItems:[],
      // 部门树选项
      deptOptions: [],
      num: undefined,
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      //选中的行
      rows: [],
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
      innerOpen:false,
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
        { key: 1, label: `订单号`, visible: true },
        { key: 2, label: `用户`, visible: false },
        { key: 3, label: `归属`, visible: false },
        { key: 4, label: `原价(元)`, visible: false },
        { key: 5, label: `折后价(元)`, visible: true },
        { key: 6, label: `已退(元)`, visible: true },
        { key: 7, label: `支付方式`, visible: true },
        { key: 8, label: `订单来源`, visible: false },
        { key: 9, label: `状态`, visible: true },
        { key: 10, label: `预计护理时间`, visible: true },
        { key: 11, label: `创建时间`, visible: true }
      ],
      // 表单校验
      rules: {
        beginTime:[{ required: true,message:"护理时间必填!",trigger: "blur"}],
        personId:[{ required: true, message: "老人必填!", trigger: "blur" },],
        serviceItemsIds:[{required: true, message: "护理项目必填!", trigger: "blur"}],
        payType:[{required: true, message: "付款方式必填!", trigger: "blur"}]
      },
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        deptId: undefined,
        startTime:undefined,
        endTime:undefined,
        name:undefined,
        orderSn:undefined,
        status:undefined
      },


    }
  },
  created() {
    this.getList()
    this.getTreeselect()
    //护理人员下拉菜单
    this.getPersonOptions()
    //护理项目列表
    this.getServiceItems()
  },
  methods:{
    //支付
    handlePay(item){
      //0=PC订单
      let data = {
        sourceType: 0,
        id: item.id
      }
      this.config = {
        url:"/nursing/order/pay",
        data: data,
        payType:item.payType
      }
    },
    //退款
    handleRefund(item){
      this.$prompt('请输入退款金额', '退款', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputValue: (parseFloat(item.payAmount) || 0) - (parseFloat(item.refundAmount) || 0),
        inputType: 'number',
        inputPattern: /^([1-9][\d]{0,6}|0)(\.[\d]{1,2})?$/,
        inputValidator: value => {
          return (parseFloat(item.payAmount) || 0) - (parseFloat(item.refundAmount) || 0) >= parseFloat(value) ? true :
            "退款金额超出限制!"
        },
        inputErrorMessage: '金额输入错误'
      }).then(async option => {
        item.refundAmount = option.value
        this.loading = true;
        let data = {
          deptId: item.deptId,
          userId: item.userId,
          orderSn: item.orderSn,
          payAmount: item.payAmount,
          refundAmount: item.refundAmount,
          payType: item.payType
        }
        refundOrder(data).then(response => {
          this.$modal.msgSuccess("提交成功")
          this.getList()
          this.loading = false;
        }).catch(error =>{
          this.getList()
          this.loading = false;
        })
      }).catch(() => {
        //this.$modal.msgError("提交失败");
      });
    },

    //百度地图相关
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
      this.innerOpen = showMapComponent
    },

    //关闭dialog
    handleCloseDialog(){
      this.open = false
      //this.loadingInstance.close()
      this.dialogLoading = false
      this.getList()
    },
    //老人选择改变事件
    changeSelect(item){
      this.form.dictLevelId = item.dictLevelId;
      this.form.personId = item.id
      this.form.personName = item.name;

      this.form.province = item.province
      this.form.city = item.city
      this.form.district = item.district
      this.form.address = item.address

      this.$set(this.form, "detailAddress", item.detailAddress);
      this.form.lat = item.lat
      this.form.lng = item.lng
      this.form.fullAddress = (this.form.province || '') + (this.form.city || '') + (this.form.district || '') + (this.form.address || '')
    },
    getPersonOptions(){
      listPersonOwn().then(response => {
        this.personOptions = response.data;
      }).then(() => this.getList())
    },
    getServiceItems(){
      listNursingServicePriceOwn().then(response => {
        this.serviceItems = response.data
      }).then(() => this.getList())
    },

    /** 获取护理项目列表 */
    async handleExpandChange(row, expandedRows) {
      //展开时
      if(expandedRows.find(e => e.id === row.id)) {
        let response = await listNursingOrderItems({ orderId: row.id })
        let index = this.list.findIndex(e => e.id === row.id)
        this.list[index].orderItems = response.data
      }
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
      this.queryParams.startTime = this.dateRange[0];
      this.queryParams.endTime = this.dateRange[1];
      listNurisngOrder(this.queryParams).then(response => {
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

      this.rows = [];
      selection.forEach(item => this.rows.push(item))
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
      this.form.btn = 0;

      this.$set(this.form, "beginTime", new Date().format("yyyy-MM-dd hh:mm:ss"));
      this.personOption = {};

      this.open = true;
      this.innerOpen = false
      this.title = "下单";
    },
    /** 派单操作 */
    async handleDelivery(row) {
      if(this.rows[0].status !== 1) {
        this.$modal.msgError("当前订单状态无法进行派单操作")
        return
      }
      this.reset();
      this.form.btn = 1;
      this.form.id = row.id || this.ids[0]
      let response = await listWorker({status: 0});
      this.workerOptions = response.rows;
      this.open = true;
      this.title = "派单";
    },
    /** 接单操作 */
    handleReceive(row){
      if(this.rows[0].status !== 1) {
        this.$modal.msgError("当前订单状态无法进行接单操作")
        return
      }
      receiveOrder({id: this.ids[0]}).then(response => {
        this.$modal.msgSuccess("接单成功");
        this.open = false;
        this.getList();
      })
    },
    /** 结单 */
    handleComplete(row) {
      if(this.rows[0].status !== 2) {
        this.$modal.msgError("当前订单状态无法进行结单操作")
        return
      }
      this.reset();
      this.form.id = this.ids[0]
      this.form.deptId = this.rows[0].deptId
      this.form.workerId = this.rows[0].workerId
      completeOrder(this.form).then(response =>{
        this.$modal.msgSuccess("结单成功");
        this.open = false;
        this.getList();
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除序号为"' + this.num + '"的数据项？').then(function() {
        return delNursingOrder(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 提交按钮 */
    submitForm: function() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.btn === 0) {//下单
            this.form.sourceType = 0; //0=PC订单
            this.config = {
              url:"/nursing/order",
              data: this.form,
              payType:this.form.payType
            }
            this.dialogLoading = true;
          }
          else if(this.form.btn === 1){//派单
            deliveryOrder(this.form).then(response => {
              this.$modal.msgSuccess("派单成功");
              this.open = false;
              this.getList();
            })
          }
        }
      });
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
.el-tag .icon-wechatpay,.el-tag .icon-alipay{
  margin-right: 5px;
  color: #fff
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
