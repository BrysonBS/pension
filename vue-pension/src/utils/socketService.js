import { getToken } from '@/utils/auth'
import { Notification } from 'element-ui'
import modal from '@/plugins/modal'

export default class SocketService {
  OPERATE = {
    PENSION_PAYMENT: 'PENSION_PAYMENT',
    NOTIFICATION: 'NOTIFICATION',
    BIOLAND_BLOOD_PRESSURE: 'BIOLAND_BLOOD_PRESSURE',
    BIOLAND_BLOOD_GLUCOSE: 'BIOLAND_BLOOD_GLUCOSE',
    OWON_SLEEPACE: 'OWON_SLEEPACE',
  }
  // 单例模式
  static instance = null;
  static get Instance () {
    if (!this.instance) {
      this.instance = new SocketService();
    }
    return this.instance;
  }

  uri = "/wss/websocket/message"

  // 实例属性ws和服务端连接的socket对象
  ws = null;

  // 实例对象callBackMapping存储回调函数
  callBackMapping = {};

  // 标识是否连接成功
  connected = false;
  connectTimer = undefined; //定时器: 失败重连

  // 记录重试的次数
  sendRetryCount = 0;

  //重新连接尝试序列
  connectRetryCount = 1;
  heartbeat = 60000;//心跳默认60秒
  heartbeatTimer = undefined; //定时器:心跳

  // 定义连接服务器的方法
  connect() {
    //已经连接则不操作
    if(this.connected) return;
    //先关闭心跳和重连
    clearTimeout(this.heartbeatTimer)
    clearTimeout(this.connectTimer)
    // 连接服务器
    if (!window.WebSocket) {
      return console.log('您的浏览器不支持websocket');
    }
    //协议
    const protocol = window.location.protocol == 'http:' ? 'ws://' : 'wss://';
    //地址
    let wsuri = protocol+window.location.host + this.uri+'/' + (getToken() || '');
    

    if(process.env.NODE_ENV === 'development'){
      wsuri = 'ws://127.0.0.1' + this.uri+'/' + (getToken() || '');
    }

    this.ws = new WebSocket(wsuri);
    // 连接成功的事件
    this.ws.onopen = () => {
      //console.log('连接服务端成功');
      this.connected = true;
      //this.connectRetryCount = 0;
      //开启心跳
      this.restartHeartbeat()
    }
    this.ws.onerror = () => {
      //ignore
    }
    // 连接服务端失败
    // 当连接成功之后，服务端关闭的情况
    this.ws.onclose = () => {
      clearTimeout(this.connectTimer)
      clearTimeout(this.heartbeatTimer)
      console.log('连接服务端失败');
      this.connected = false;
      this.connectRetryCount <<= 1;
      this.connectTimer = setTimeout(() => {
        this.connect();
      }, this.connectRetryCount * 250)
    }
    // 得到服务端发送过来的数据
    this.ws.onmessage = msg => {
      //console.log('从服务端获取到的数据===', msg.data);
      // 真正服务端发送过来的原始数据时在msg中的data字段
      const recvData = JSON.parse(msg.data);
      const operate = recvData.operate
      const code = recvData.code

      if(!operate || operate === '0'){
        //console.log("忽略: ",recvData)
        if(recvData.msg === '登录过期') location.reload()
        return
      }

      //失败直接返回
      if(code !== 200){
        modal.msgError(recvData.msg)
        return
      }

      //回调操作
      if(this.callBackMapping[operate]){
        //console.log("回调: ",recvData.data)
        this.callBackMapping[operate](typeof recvData.data == 'object' ? recvData.data : JSON.parse(recvData.data))
      }
      else console.log("回调操作:["+operate + "]不存在",recvData.data)
    }
  }

  // 回调函数的注册
  registerCallBack (socketType, callBack) {
    this.callBackMapping[socketType] = callBack;
  }

  // 取消某一个回调函数
  unRegisterCallBack (socketType) {
    this.callBackMapping[socketType] = null;
  }

  // 发送数据的方法
  send (data) {
    // 判断现在是否有连接成功
    if (this.connected && this.ws.readyState === 1) {
      this.sendRetryCount = 0;
      this.ws.send(JSON.stringify(data));
      //重置心跳
      this.restartHeartbeat();
    } else {
      this.sendRetryCount++;
      setTimeout(() => {
        this.send(data);
      }, this.sendRetryCount * 500)
    }
  }

  //心跳
  restartHeartbeat(){
    clearTimeout(this.heartbeatTimer)
    this.heartbeatTimer = setTimeout(() => {
      if (this.connected)
        this.ws.send(JSON.stringify({operate:'0'}))
      this.restartHeartbeat()
    },this.heartbeat)
  }

}
