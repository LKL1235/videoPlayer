<template>
  <input v-model="url" style="margin-left: 15%">
  <input type="checkbox" class="checkbox1" v-model="isLive">
  <span class="label1">是否为直播流</span>
  <button class="button1" @click="load">加载</button>
  <div>
    <video preload controls :src="src"  style="margin-top: 5%" height="300" id="myVideo"></video>
    <button @click="send">send</button>
    <select id="sdir">
      <option v-for="index in option" :value="index.url">{{index.url}}</option>
    </select>
    <button @click="change">log</button>
  </div>
</template>

<script lang="ts" setup>
import mpegts from 'mpegts.js';
import {onMounted, ref} from "vue";
import {useUserStore} from "@/stores/userStore";
import axios from "axios";


const user=useUserStore()
var socket
const option=ref([
    "mv2.mp4"
])
const change = () => {
  url.value=document.getElementById("sdir").value
}
const baseVideoUrl="http://139.9.32.27/src/video/"
const baseLiveUrl="http://139.9.32.27/live?port=1935&app=live&stream=video"
const url=ref("")
const tips=ref("")
const isLive=ref(false)
const src=ref("")
const load = () => {
    if(isLive.value){
      createdPlay()
   }else {
      src.value=baseVideoUrl+url.value
   }
}
// 检测浏览器是否支持 flv.js
const createdPlay=()=>{
  if (mpegts.isSupported()) {
    let videoElement = document.getElementById('myVideo');
    let flvPlayer = mpegts.createPlayer({
      isLive: true,
      type: 'flv',
      url: "http://139.9.32.27/live?port=1935&app=live&stream=video",
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
     if(url===data.url){
          let Video=document.getElementById("myVideo")
          if(Video.currentTime!=data.time){
          Video.currenTime=data.time
       }
      }else {
     src.value=baseVideoUrl+data.url
    }
  }
}
const send= ()=> {
  let Video=document.getElementById("myVideo")
  let message={url:url.value,time:Video.currentTime,isLive:isLive.value}
  socket.send(JSON.stringify(message))
}
const close= ()=>  {
  console.log("socket已经关闭")
}

const getDir = () => {
  axios.get("/sdir").then((respon)=>{
    console.log(respon.data)
    option.value=respon.data
  }).catch(error=>{console.log(error)})
}
onMounted (()=>{
  // 初始化
  init()
  getDir()
})
</script>

<style>
.label1{
  color: #FFFFFF;
}
.checkbox1{

}
.button1{
  margin-left: 5%;
}

</style>
