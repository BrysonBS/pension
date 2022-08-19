<template>
  <div class="navbar">
    <hamburger id="hamburger-container" :is-active="sidebar.opened" class="hamburger-container" @toggleClick="toggleSideBar" />

    <breadcrumb id="breadcrumb-container" class="breadcrumb-container" v-if="!topNav"/>
    <top-nav id="topmenu-container" class="topmenu-container" v-if="topNav"/>

    <div class="right-menu">
      <template v-if="device!=='mobile'">
        <search id="header-search" class="right-menu-item" />

        <screenfull id="screenfull" class="right-menu-item hover-effect" />

        <el-tooltip content="布局大小" effect="dark" placement="bottom">
          <size-select id="size-select" class="right-menu-item hover-effect" />
        </el-tooltip>

      </template>

      <el-dropdown class="avatar-container right-menu-item hover-effect" trigger="click">
        <div class="avatar-wrapper">
<!--          <el-avatar :src="avatar" class="user-avatar"> {{avatar}} </el-avatar>-->
          <img :src="avatar" class="user-avatar">
          <i class="el-icon-caret-bottom" />
        </div>
        <el-dropdown-menu slot="dropdown">
          <router-link to="/user/profile">
            <el-dropdown-item>个人中心</el-dropdown-item>
          </router-link>
          <el-dropdown-item @click.native="setting = true">
            <span>布局设置</span>
          </el-dropdown-item>
          <el-dropdown-item divided @click.native="logout">
            <span>退出登录</span>
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import Breadcrumb from '@/components/Breadcrumb'
import TopNav from '@/components/TopNav'
import Hamburger from '@/components/Hamburger'
import Screenfull from '@/components/Screenfull'
import SizeSelect from '@/components/SizeSelect'
import Search from '@/components/HeaderSearch'
import RuoYiGit from '@/components/RuoYi/Git'
import RuoYiDoc from '@/components/RuoYi/Doc'
import { getToken } from '@/utils/auth'
import { Notification } from 'element-ui'

export default {
  components: {
    Breadcrumb,
    TopNav,
    Hamburger,
    Screenfull,
    SizeSelect,
    Search,
    RuoYiGit,
    RuoYiDoc,
    Notification,
  },
  data() {
    return {
      //websocket配置
      uri: "/wss/websocket/message",
      ws: null,
      lockReconnect: false, //是否真正建立连接
      timeout: 60 * 1000, //60秒一次心跳
      timeoutObj: null, //心跳心跳倒计时
      serverTimeoutObj: null, //心跳倒计时
      timeoutNum: null, //断开 重连倒计时
      heartbeat:'1' //心跳信息
    }
  },
  computed: {
    ...mapGetters([
      'sidebar',
      'avatar',
      'device'
    ]),
    setting: {
      get() {
        return this.$store.state.settings.showSettings
      },
      set(val) {
        this.$store.dispatch('settings/changeSetting', {
          key: 'showSettings',
          value: val
        })
      }
    },
    topNav: {
      get() {
        return this.$store.state.settings.topNav
      }
    }
  },
  methods: {
    toggleSideBar() {
      this.$store.dispatch('app/toggleSideBar')
    },
    async logout() {
      this.$confirm('确定注销并退出系统吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$store.dispatch('LogOut').then(() => {
          location.href = '/index';
        })
      }).catch(() => {});
    },
    //websocket相关
    initWs(){
      const protocol = window.location.protocol == 'http:' ? 'ws://' : 'wss://';
      const wsuri = protocol+window.location.host +this.uri+'/'+getToken();
      this.ws = new WebSocket(wsuri);
      this.ws.onopen = this.onopenWs;
      this.ws.onerror = this.onerrorWs;
      this.ws.onmessage = this.onmessageWs;
      this.ws.onclose = this.oncloseWs;
    },
    onopenWs(){
      //开启心跳
      this.start();
    },
    onerrorWs(){
      //失败进行重连
      this.reconnect();
    },
    onmessageWs(){
      let messageBody = event.data;
      //console.log(messageBody);
      if(messageBody.charAt(0) !== '{'){//非NoticeVo对象
        if(messageBody === '登录过期' || messageBody === '用户认证失败') location.reload();
      }
      else {
        let notice = JSON.parse(messageBody);
        //支付回调通知
        if(notice.type === "PENSION_PAYMENT"){
          this.sendPaymentInfo(notice);
          return;
        }
        //设备上报通知
        let tags = notice.tags;
        let message = '';
        for(let tag of tags){
          message += `<span class="notice-tag">${tag}</span>`
        }
        message += `<div class="notice-info">${notice.info}</div>
        <div class="notice-time">${notice.time}</div class="notice-time">`;

        //通知
        Notification.warning({
          title: notice.name,
          dangerouslyUseHTMLString: true,
          message: message,
          duration: 0
        })

        //全局事件总线相关
        //发送数据到睡眠检测
        this.sendSleep(notice)
        //发送数据到血压
        this.sendBlpressure(notice)
        //发送数据到血糖
        this.sendBlglucose(notice)
      }
      //收到信息,心跳重置
      this.reset();
    },
    oncloseWs(){
      //关闭进行重连
      this.reconnect();
    },
    closeWs(){
      if (this.ws) {
        this.ws.close();
        this.ws = null;
      }
    },
    //重连
    reconnect(){
      let that = this;
      if(that.lockReconnect) return;
      that.lockReconnect = true;
      //没连接上会一直重连，设置延迟避免请求过多
      that.timeoutnum && clearTimeout(that.timeoutnum);
      that.timeoutnum = setTimeout(function() {
        //新连接
        that.initWs();
        that.lockReconnect = false;
      }, 5000);
    },
    //重置心跳
    reset(){
      //重置心跳
      let that = this;
      //清除时间
      clearTimeout(that.timeoutObj);
      clearTimeout(that.serverTimeoutObj);
      //重启心跳
      that.start();
    },
    start() {
      //开启心跳
      let self = this;
      self.timeoutObj && clearTimeout(self.timeoutObj);
      self.serverTimeoutObj && clearTimeout(self.serverTimeoutObj);
      self.timeoutObj = setTimeout(function() {
        //这里发送一个心跳，后端收到后，返回一个心跳消息
        if (self.ws.readyState == 1) {
          //如果连接正常
          self.ws.send(self.heartbeat);
        } else {
          //否则重连
          self.reconnect();
        }
        self.serverTimeoutObj = setTimeout(function() {
          //超时关闭
          self.ws.close();
        }, self.timeout);
      }, self.timeout);
    },
    //关闭心跳
    stop(){
      let self = this;
      self.timeoutObj && clearTimeout(self.timeoutObj);
      self.serverTimeoutObj && clearTimeout(self.serverTimeoutObj);
      self.timeoutnum && clearTimeout(self.timeoutnum);
    },

    //全局事件总线相关
    //发送数据到睡眠监测
    sendSleep(notice){
      if((!isNaN(parseFloat(notice.heartRate)) && isFinite(notice.heartRate)) ||
        (!isNaN(parseFloat(notice.respiratoryRate)) && isFinite(notice.respiratoryRate))) {
        let data = {
          "ts": notice.ts,
          "ieee": notice.ieee,
          "heartRate": notice.heartRate,
          "respiratoryRate": notice.respiratoryRate
        };
        this.$bus.$emit('sleep', data)
      }
    },
    //血压数据发送
    sendBlpressure(notice){
      if((!isNaN(parseFloat(notice.hbp)) && isFinite(notice.hbp))
        || (!isNaN(parseFloat(notice.lbp)) && isFinite(notice.lbp))
      ){
        let data = {
          "ts": notice.ts,
          "ieee": notice.ieee,
          "hbp": notice.hbp,
          "lbp": notice.lbp,
          "heartRate": notice.heartRate
        };
        this.$bus.$emit('blpressure', data)
      }
    },
    //血糖数据发送
    sendBlglucose(notice){
      if(!isNaN(parseFloat(notice.glucose)) && isFinite(notice.glucose)){
        let data = {
          "ts": notice.ts,
          "ieee": notice.ieee,
          "glucose": notice.glucose
        };
        this.$bus.$emit('blglucose', data)
      }
    },
    //支付回调数据发送
    sendPaymentInfo(notice){
      if(notice.info !== undefined){
        this.$bus.$emit('payment-notify',notice)
      }
    }
  },
  mounted(){
    this.initWs()
  },
  destroyed() {
    this.stop()
    this.closeWs()
  }
}
</script>
<style>
.notice-tag {
  background-color:#ecf5ff;
  border-color:#d9ecff;
  display:inline-block;
  font-size:12px;
  color:#409EFF;
  border-width:1px;
  border-style:solid;
  border-radius:4px;
  -webkit-box-sizing:border-box;
  box-sizing:border-box;
  white-space:nowrap;
  height:20px;
  padding:0 5px;
  line-height:19px;
/*  background-color:#67c23a;
  border-color:#67c23a;
  color:#fff*/
}
.notice-info{
  margin-top: 5px;
  margin-bottom: 10px;
}
.notice-time{
  font-size: small;
  color: black;
  position: absolute;
  right: 20px;
  bottom: 5px;
}
</style>
<style lang="scss" scoped>
.navbar {
  height: 50px;
  overflow: hidden;
  position: relative;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0,21,41,.08);

  .hamburger-container {
    line-height: 46px;
    height: 100%;
    float: left;
    cursor: pointer;
    transition: background .3s;
    -webkit-tap-highlight-color:transparent;

    &:hover {
      background: rgba(0, 0, 0, .025)
    }
  }

  .breadcrumb-container {
    float: left;
  }

  .topmenu-container {
    position: absolute;
    left: 50px;
  }

  .errLog-container {
    display: inline-block;
    vertical-align: top;
  }

  .right-menu {
    float: right;
    height: 100%;
    line-height: 50px;

    &:focus {
      outline: none;
    }

    .right-menu-item {
      display: inline-block;
      padding: 0 8px;
      height: 100%;
      font-size: 18px;
      color: #5a5e66;
      vertical-align: text-bottom;

      &.hover-effect {
        cursor: pointer;
        transition: background .3s;

        &:hover {
          background: rgba(0, 0, 0, .025)
        }
      }
    }

    .avatar-container {
      margin-right: 30px;

      .avatar-wrapper {
        margin-top: 5px;
        position: relative;

        .user-avatar {
          cursor: pointer;
          width: 40px;
          height: 40px;
          border-radius: 10px;
        }

        .el-icon-caret-bottom {
          cursor: pointer;
          position: absolute;
          right: -20px;
          top: 25px;
          font-size: 12px;
        }
      }
    }
  }
}
</style>
