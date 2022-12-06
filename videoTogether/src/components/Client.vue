<template>
  <div >
    <span class="span">当前集数为{{url.value}}</span>
  </div>
  <div>
  <video autoplay :src="src" style="margin-top: 5%" height="300" id="myVideo"></video>
  </div>
  <div>
    <textarea disabled v-model="tips" class="tips">
    </textarea>
  </div>
</template>

<script setup lang="ts">
import {onMounted, ref} from "vue";
import mpegts from "mpegts.js";
import {useUserStore} from "@/stores/userStore";

const user=useUserStore()

let socket
const baseVideoUrl="http://139.9.32.27/src/video/"
const baseLiveUrl="http://139.9.32.27/live?port=1935&app=live&stream=video"
const url=ref("")
const tips=ref("")
const isLive=ref(false)
const src=ref("")


// 检测浏览器是否支持 flv.js
const createdPlay=()=>{
  if (mpegts.isSupported()) {
    var videoElement = document.getElementById('myVideo');
    var flvPlayer = mpegts.createPlayer({
      isLive: true,
      type: 'flv',
      url: baseLiveUrl,
    });
    flvPlayer.attachMediaElement(videoElement);
    flvPlayer.load();
    flvPlayer.play();
  }
}
const init=()=>{
  if(typeof(WebSocket) === "undefined"){
    alert("您的浏览器不支持socket")
  }else{
    // 实例化socket
    socket = new WebSocket("ws://139.9.32.27:9555/ws/"+user.$state.roomId+'/'+user.$state.username)
    // 监听socket连接
    socket.onopen = open
    // 监听socket错误信息
    socket.onerror = error
    // 监听socket消息
    socket.onmessage = getMessage

    socket.onclose=close
  }
}
const open= ()=> {
  console.log("socket连接成功")
  tips.value=tips.value+"连接房间成功"
}
const error= ()=>  {
  console.log("连接错误")
}
const getMessage= (msg)=>  {
  let data = JSON.parse(msg.data)
  tips.value=data.tips
  if(data.isLive){
    createdPlay()
  }else{
    if(url.value===data.url){
      var Video=document.getElementById("myVideo")
      if(Video.currentTime!=data.time){
        document.getElementById("myVideo").currentTime=data.time
        document.getElementById("myVideo").play()
      }
      if(data.isPause){
        document.getElementById("myVideo").pause()
        console.log("isPause暂停")
      }else {
        document.getElementById("myVideo").play()
        console.log("isPause播放")
      }
    }else {
      if(data.url!="") {
        url.value = data.url
        src.value = baseVideoUrl + data.url
        console.log("src:"+src.value)
        document.getElementById("myVideo").currentTime=data.time
        document.getElementById("myVideo").play()
      }
    }
  }
}

const close= ()=>  {
  console.log("socket已经关闭")
  tips.value=tips.value+'\n连接断开了'
}
onMounted (()=>{
  // 初始化
  init()
})

</script>

<style>
.tips{
  position: fixed;top:51%;left: 20%;height: 130px;width: 350px
}

@media screen and (min-width: 1200px){
  .tips{
    position: relative;
    top:10px;
    left: 33%;
    height: 300px;
    width: 500px;
  }
}
</style>
