<template>
  <div>
    <el-dialog :title="title" :visible.sync="open" :style="style">
      <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" label-width="85px"
               style="text-align: center"
      >
        <el-form-item prop="keyword">
          <el-input
            v-model="queryParams.keyword"
            :placeholder="searchHolder"
            clearable
            style="min-width: 300px;"
            @keyup.enter.native="handleQuery"
          >
            <el-button slot="append" icon="el-icon-search" @click="handleQuery"></el-button>
          </el-input>
        </el-form-item>
      </el-form>
      <el-table
        :data="list"
        highlight-current-row
        @current-change="handleCurrentChange"
        @cell-dblclick="confirm"
        ref="singleTable"
      >
        <el-table-column label="序号" width="70px" align="center">
          <template slot-scope="scope">
            <span>{{scope.row.num = scope.$index + (queryParams.pageNum - 1) * queryParams.pageSize + 1}}</span>
          </template>
        </el-table-column>
        <el-table-column width="230" label="订单号" align="center" prop="orderSn" v-if="columns[1].visible" />
        <el-table-column width="100" label="原价(元)" align="center" prop="totalAmount" v-if="columns[4].visible" >
          <template v-slot="scope">
            <span>￥{{scope.row.totalAmount}}</span>
          </template>
        </el-table-column>
        <el-table-column width="100" label="折后价(元)" align="center" prop="payAmount" v-if="columns[5].visible" >
          <template v-slot="scope">
            <span>￥{{scope.row.payAmount}}</span>
          </template>
        </el-table-column>
        <el-table-column width="100" label="已退(元)" align="center" prop="payAmount" v-if="columns[6].visible" >
          <template v-slot="scope">
            <span>￥{{scope.row.refundAmount || 0}}</span>
          </template>
        </el-table-column>
        <el-table-column width="100" label="支付方式" align="center" v-if="columns[7].visible" >
          <template slot-scope="scope">
            <el-tag size="small" effect="dark"
                    v-if="scope.row.payType == 1"
            ><i class="icon-alipay"/>支付宝</el-tag>
            <el-tag type="success" size="small" effect="dark"
                    v-if="scope.row.payType == 2"
            ><i class="icon-wechatpay"/>微信</el-tag>
          </template>
        </el-table-column>
        <el-table-column width="80" label="状态" align="center" key="status" v-if="columns[9].visible">
          <template v-slot="scope">
            <el-tag :type="dict.raw.listClass" size="small"
                    v-for="dict in dict.type.order_status"
                    v-if="dict.value == scope.row.status"
            >{{dict.label}}</el-tag>
          </template>
        </el-table-column>
        <el-table-column width="180" label="预计护理时间" align="center" prop="beginTime" v-if="columns[10].visible" />
        <el-table-column width="180" label="创建时间" align="center" prop="createTime" v-if="columns[11].visible" />
      </el-table>
      <pagination
        small
        layout="prev, pager, next"
        v-show="total>0"
        :total="total"
        :page.sync="queryParams.pageNum"
        :limit.sync="queryParams.pageSize"
        @pagination="getList"
      />
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="confirm">确 定</el-button>
        <el-button @click="open = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>

import request from '@/utils/request'

export default {
  name: 'OrderSelect',
  dicts: ['nusing_service_items','order_status','nursing_level'],
  props:{
    form:{
      type:Object,
      default(){return {}}
    },
    config:{
      type:Object,
      default(){return {}}
    }
  },
  watch: {
    config() {
      this.title = this.config.title
      this.open = this.config.open
      this.searchHolder = this.config.searchHolder
      this.url = this.config.url
      this.style = {
        '--min-width':this.config.style.minWidth
      }


      //更新列表
      this.getList()
    }
  },
  data(){
    return{
      title:"选择数据",
      open: false,
      style: undefined,
      searchHolder: '请输入',
      url: undefined,
      num: undefined,
      list: undefined,
      total: 0,
      loading: false,
      currentRow: undefined,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 5,
        keyword:undefined
      },
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
    }
  },
  methods:{
    /** 查询列表 */
    async getList() {
      this.loading = true;
      let response = await request({
        url: this.url,
        method: 'get',
        params: this.queryParams
      })

      this.list = response.rows;
      this.total = response.total;
      this.loading = false;
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 确定按钮 */
    confirm: function() {
      this.open = false
      this.$emit("select-data",this.currentRow)
    },
    handleCurrentChange(row) {
      this.currentRow = row;
    },
  }
}
</script>

<style>
.el-dialog{
  min-width: var(--min-width)
}
.el-dialog__body{
  padding-top: 0;
}
.el-table__body tr.current-row>td {
  background: #ECFAE8FF !important;
}
</style>
