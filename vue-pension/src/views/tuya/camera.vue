<template>
  <div class="com-container">
    <video controls :style="{height:height,width:width}" ref="remoteVideo" playsinline autoplay>
      <audio controls ref="remoteAudio" autoplay></audio>
    </video>

  </div>
</template>

<script>

import adapter from 'webrtc-adapter'

export default {
  name: 'Camera',
  props: {
    width: {
      type: String,
      default: '640px'
    },
    height: {
      type: String,
      default: '360px'
    }
  },
  data(){
    return{
      operate: "cameraHandler",//"webRTCHandler",
      deviceId: undefined,
      uid: undefined,
      iceServers: undefined,    // ICE服务器地址列表
      startTime: undefined,
      configuration: undefined, // webRTC配置
      pc: undefined,            // PeerConnection对象，处理webRTC业务
      localStream: undefined,   // 本地音视频流
      gSessionId: undefined,    // 每次点击Call生成的新webRTC会话id
      offerOptions: {
        offerToReceiveAudio: 1,
        offerToReceiveVideo: 1
      }
    }
  },
  mounted() {
    // 在组件创建完成之后，进行回调函数的注册
    this.$socket.registerCallBack(this.operate, this.operateHandler)
  },
  destroyed() {
    //先挂断
    this.hangup()
    // 在组件销毁时，进行回调函数取消
    this.$socket.unRegisterCallBack(this.operate)
  },
  methods:{
    //连接
    connect(uid,deviceId){
      //console.log("deviceId",deviceId)
      this.deviceId = deviceId;
      this.uid = uid;
      // 发送数据给服务端，告诉服务端，前端现在需要数据
      this.$socket.send({
        operate: this.operate,
        type: "configs",
        deviceId: this.deviceId,
        uid: this.uid
      })
    },
    //挂断
    hangup(){
      console.log("hangup......")
      this.sendDisconnect()
    },
    operateHandler(message){
      switch (message.type || message.data.header.type){
        case "configs": {
          this.iceServers = typeof message.data === 'string' ? JSON.parse(message.data) : message.data
          this.gSessionId = this.uuid();
          this.call()
          break;
        }
        case "answer": {
          let answer = {}
          answer["type"] = "answer"
          answer["sdp"] = message.data.msg.sdp
          //console.log("answer:   ",answer)
          //if(!this.pc.connectionState)
          //console.log("state======: ",this.pc.currentRemoteDescription)
          this.pc.setRemoteDescription(answer)
            .catch(e => { console.log(e)})
          break
        }
        case "candidate": {
          let candidate = {}
          candidate["candidate"] = message.data.msg.candidate
          candidate["sdpMid"] = '0'
          candidate["sdpMLineIndex"] = 0
          //console.log("receive candidate: ",candidate)
          this.pc.addIceCandidate(candidate).catch(e => {
            console.log("addIceCandidate fail: " + e.name)
          })
        }
        case "disconnect": {
          console.log("get disconnect from OpenAPI")
          //this.sendDisconnect();
        }
        default: { break }
      }
    },
    // 启动webRTC业务
    async call() {
      console.log('call')
      this.startTime = window.performance.now()

      this.configuration = {
        iceServers: this.iceServers
      }

      //console.log('RTCPeerConnection configuration:', this.configuration)

      this.pc = new RTCPeerConnection(this.configuration)

      //return;

      //console.log('Created remote peer connection object pc')

      this.pc.addEventListener('icecandidate', e => this.onIceCandidate(this.pc, e))
      this.pc.addEventListener('iceconnectionstatechange', e => this.onIceStateChange(this.pc, e))
      this.pc.addEventListener('track', this.gotRemoteStream)

      this.pc.addTransceiver('audio', {'direction': 'recvonly'})
      this.pc.addTransceiver('video', {'direction': 'recvonly'})

      try {
        let stream;
        try{
          stream = await navigator.mediaDevices.getUserMedia({audio: true, video: false})
        }catch (e){
          stream = await navigator.mediaDevices.getDisplayMedia({audio:false,video:false})
        }
        //console.log('Received local stream')
        this.localStream = stream
      } catch (e) {
        //alert(`getUserMedia() error: ${e.name}`)
        console.log(e.name,e)
        alert("您的浏览器不支持,推荐使用chrome浏览器!")
        return;
      }


      this.localStream.getTracks().forEach(track => this.pc.addTrack(track, this.localStream))
      //console.log('Added local stream to pc')

      // Since the remote side has no media stream, we need to pass in the right constraints,
      // in order for it to accept the incoming offer with audio and video.
      try {
        //console.log('pc createOffer start')
        const offer = await this.pc.createOffer(this.offerOptions)
        //console.log('Original Offer:', offer)
        await this.onCreateOfferSuccess(offer)
      } catch (e) {
        console.log(`Failed to create session description: ${error.toString()}`)
      }
    },
    // 采集到本地candidate候选地址，发送到WebSocket服务
    async onIceCandidate(pc, event) {
      //console.log(`ICE candidate:\n${event.candidate ? event.candidate.candidate : '(null)'}`)
      this.$socket.send({
        operate: this.operate,
        deviceId: this.deviceId,
        uid: this.uid,
        type: "candidate",
        sessionId: this.gSessionId,
        payload: event.candidate != null ? "a=" + event.candidate.candidate : "",
      })
    },
    // ICE连接状态变更处理函数
    onIceStateChange(pc, event) {
      if (pc) {
        //console.log(`ICE state: ${pc.iceConnectionState}`)
        //console.log('ICE state change event: ', event)
        if (pc.iceConnectionState === 'connected') {
          console.log("webRTC connected")
        }
      }
    },
    // 获取对端音视频流，绑定到网页上的播放控件
    gotRemoteStream(e) {
      //console.log('Debug........ ', e.track.kind)
      if (e.track.kind === 'audio') {
        this.$refs.remoteAudio.srcObject = e.streams[0]
      } else if (e.track.kind === 'video') {
        this.$refs.remoteVideo.srcObject = e.streams[0]
      }
    },
    // PeerConnection生成offer成功，发送到WebSocket服务
    async onCreateOfferSuccess(desc) {
      //console.log(`Offer from pc`)
      //console.log(JSON.stringify(desc))
      //console.log('pc setLocalDescription start')

      try {
        await this.pc.setLocalDescription(desc)
        this.onSetLocalSuccess(this.pc)
      } catch (e) {
        this.onSetSessionDescriptionError()
      }

      //console.log("get answer from OpenAPI")
      this.$socket.send({
        operate: this.operate,
        deviceId: this.deviceId,
        uid: this.uid,
        type: "offer",
        sessionId: this.gSessionId,
        payload: desc.sdp.replace(/\r\na=extmap[^\r\n]*/g, ''),
      })

    },
    sendDisconnect() {
      //console.log("hangup")
      try {
        this.$socket.send({
          operate: this.operate,
          deviceId: this.deviceId,
          uid: this.uid,
          type: "disconnect",
          payload: "",
        })
      } catch (e) {
        //console.log("hangup the call fail: " + e.name)
      }
      if(this.pc) this.pc.close()

      this.deviceId = undefined
    },
    onSetLocalSuccess(pc) {
      //console.log(`setLocalDescription complete`)
    },
    onSetSessionDescriptionError(error) {
      console.log(`Failed to set session description: ${error.toString()}`)
    },
    uuid(){
      return ([1e7] + -1e3 + -4e3 + -8e3 + -1e11).replace(/[018]/g, c =>
        (c ^ (crypto.getRandomValues(new Uint8Array(1))[0] & (15 >> (c / 4)))).toString(16))
    }
  },
}
</script>

<style scoped>

</style>
