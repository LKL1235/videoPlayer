<template>
  <video preload controls :src="src" style="margin-top: 5%" height="300" id="myVideo"></video>
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
    socket = new WebSocket("ws://139.9.32.27:8080/ws/"+user.$state.roomId+'/'+user.$state.username)
    // 监听socket连接
    socket.onopen = open
    // 监听socket错误信息
    socket.onerror = error
    // 监听socket消息
    socket.onmessage = getMessage
  }
}
const open= ()=> {
  console.log("socket连接成功")
}
const error= ()=>  {
  console.log("连接错误")
}
const getMessage= (msg)=>  {
  let data = JSON.parse(msg.data)
  if(data.isLive){
    createdPlay()
  }else{
    if(url.value===data.url){
      let Video=document.getElementById("myVideo")
      if(Video.currentTime!=data.time){
        console.log(Video.currenTime)
        document.getElementById("myVideo").currentTime=data.time
      }
    }else {
      url.value=data.url
      src.value=baseVideoUrl+data.url
    }
  }
}
const send= ()=> {
  let Video=document.getElementById("myVideo")
  let message={url:'url',time:Video.currentTime}
  socket.send(JSON.stringify(message))
}
const close= ()=>  {
  console.log("socket已经关闭")
}
onMounted (()=>{
  // 初始化
  init()
})

</script>

<style>

</style>
