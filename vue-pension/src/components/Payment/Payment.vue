<template>
  <el-dialog
    id="pay"
    :close-on-click-modal="false"
    center
    @close="handleClose"
    width="fit-content"
    :visible.sync="open">
    <div slot="title" class="dialog-header-title">
      <span :class="{'ico_log':true,'ico-1':payType == '1','ico-3':payType == '2'}"></span>
    </div>
    <div :style="{'visibility': !payment?'visible':'hidden'}" class="notify">
      <el-result :icon="notifyIcon" :title="notifyInfo">
      </el-result>
    </div>
    <div :style="{'visibility': payment?'visible':'hidden'}" class="payment">
      <div class="amount">￥{{amount}}</div>
      <img :src="imgUrl"/>
      <div class="order-number"><h1>单号:{{orderNumber}}</h1> </div>
    </div>
    </el-dialog>
</template>

<script>
import axios from 'axios'
import { getToken } from '@/utils/auth'

export default {
  name: 'Payment',
  props:{
    config:{
      type:Object,
      default(){
        return {}
      }
    }
  },
  data(){
    return{
      payType:undefined,
      open: false,
      imgUrl: undefined,
      amount:undefined,
      orderNumber:undefined,
      notifyInfo:undefined,
      payment:true,
      notifyIcon:'success'
    }
  },
  watch:{
    config(){
      this.payType = this.config.payType;
      axios.create({
        // axios中请求配置有baseURL选项，表示请求URL公共部分
        baseURL: process.env.VUE_APP_BASE_API,
        headers: {'Authorization': 'Bearer ' + getToken()},
        // 超时
        timeout: 10000
      }).post(this.config.url,this.config.data,{ responseType: 'blob' })
      .then(response => {
        if(response.data.type === 'application/json'){//异常处理
          const reader = new FileReader();
          reader.readAsText(response.data, "UTF-8");
          reader.onload = () => {
            this.$modal.msgError(JSON.parse(reader.result).msg);
          }
        }
        else {
          const heades = response.headers || {};
          this.amount = heades['pay-amount'];
          this.orderNumber = heades['pay-order'];
          this.imgUrl = URL.createObjectURL(response.data);
          this.payment = true;

          //先触发父组件关闭方法
          this.handleClose();

          this.open = true;
        }
      })
    }
  },
  methods:{
    handleClose(){
      //先触发父组件方法,关闭窗口
      this.$emit('close-dialog')
    },
    paymentNotify(e){
      if(e.payType == 1){ //支付宝
        if(e.tradeState !== 'TRADE_SUCCESS') this.notifyIcon = 'error'
      }
      else if(e.payType == 2){//微信
        if(e.tradeState !== 'SUCCESS') this.notifyIcon = 'error'
      }
      //单号不一致不处理
      if(e.outTradeNo !== this.orderNumber) return;
      this.payment = false
      this.notifyInfo = e.info
    }
  },
  mounted() {
    //全局事件总线绑定事件
    this.$bus.$on('payment-notify', this.paymentNotify)
  },
  beforeDestroy() {
    //解绑事件
    this.$bus.$off('payment-notify');
  }
}
</script>

<style>
.ico_log {
  display: inline-block;
  width: 130px;
  height: 38px;
  vertical-align: middle;
  margin-right: 7px
}
.ico-3 {
  background: url(~@/assets/images/logo_weixin.jpg) no-repeat;
  background-size:cover;
}
.ico-1 {
  background: url(~@/assets/images/logo_alipay.jpg) no-repeat;
  background-size:cover;
}
.order-number h1{
  font-size: 6px;
  text-align: center;
}
.amount{
  user-select: none;
  font-size: 42px;
  text-align: center;
}
#pay .el-dialog__body{
  margin-top: 0;
  padding-top: 0;
}
#pay .el-dialog__header{
  padding-bottom: 0;
}
.notify{
  width: fit-content;
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%,-50%);
}
.payment{
  visibility: hidden;
}
</style>
