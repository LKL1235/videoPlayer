<template>
  <div >
    <span class="span">当前集数为{{url}}</span>
  </div>

  <div>
    <video autoplay controls :src="src"  @pause="pause"  @play="play" @ended="end" style="margin-top: 5%" height="300" id="myVideo"></video>
  </div>

  <div >
    <div>
      <input type="checkbox" class="checkbox1" v-model="isLive">
      <span class="label1">是否为直播流</span>
      <button class="button_load" @click="load">加载视频</button>
    </div>

    <div class="div_select">
      <select id="ldir" class="select1" @change="fdirChange">
        <option v-for="index in option1" :value="index.url">{{index.url}}</option>
      </select>
      <select id="sdir" class="select2">
        <option v-for="index in option" :value="index.url">{{index.url}}</option>
      </select>
      <button class="button_change" @click="change">选集</button>
    </div>
    <div>
      <button class="button_send" @click="send">为房间推送同步</button>
    </div>
    <div>
      <textarea disabled v-model="tips" class="tips1">
      </textarea>
    </div>

  </div>
</template>

<script lang="ts" setup>
import mpegts from 'mpegts.js';
import {onMounted, ref} from "vue";
import {useUserStore} from "@/stores/userStore";
import axios from "axios";

const url=ref("")
const tips=ref("")
const isLive=ref(false)
const src=ref("")
const user=useUserStore()
const isPause=ref(false)
const Fdir=ref("")
var socket
const option=ref([
  {
    url: "mv2.mp4"
  }
])
const option1=ref([
  {
    url: ""
  }
])
const play=()=>{
  isPause.value=false
  send()
}
const pause = () => {
  isPause.value=true
  send()
}
const end = () => {
  var index=document.getElementById("sdir").selectedIndex ;
  document.getElementById("sdir").selectedIndex+=1
  if(Fdir.value!=""){
    url.value=Fdir.value+'/'+option.value[index+1].url
  }else {
    url.value=option.value[index+1].url
  }
  src.value=baseVideoUrl+url.value
  send()
}
const change = () => {
  if(Fdir.value!=""){
    url.value=Fdir.value+'/'+document.getElementById("sdir").value
  }else {
    url.value=document.getElementById("sdir").value
  }
}
const baseVideoUrl="http://139.9.32.27/src/video/"
const baseLiveUrl="http://139.9.32.27/live?port=1935&app=live&stream=video"

const load = () => {
    if(isLive.value){
      createdPlay()
   }else {
      tips.value+="\n当前播放视频为："+url.value
      src.value = baseVideoUrl + url.value
      console.log("src:"+src.value)
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
  console.log("getMessage:收到消息")
  var data = JSON.parse(msg.data)
  tips.value=data.tips
  if(data.isLive){
    createdPlay()
  }else{
     if(url.value===data.url && data.url!=''){

      }else if(data.url!=="") {
         src.value = baseVideoUrl + data.url
    }else {
       console.log("getMessage:发送")
       send()
     }
  }
}
const send= ()=> {
  tips.value=tips.value+"\n房间已同步"
  let Video=document.getElementById("myVideo")
  let message={url:url.value,time:Video.currentTime,isLive:isLive.value,tips:tips.value,isPause:isPause.value}
  socket.send(JSON.stringify(message))
}
const close= ()=>  {
  console.log("socket已经关闭")
  tips.value=tips.value+'\n连接断开了'
}

const fdirChange = () => {
  Fdir.value=document.getElementById("ldir").value
  getDir(Fdir.value)
}

const getDir = (dir:string) => {
  if(dir!="") {
    axios.get("/sdir/" + dir).then((respon) => {
      console.log(respon.data)
      option.value = respon.data
    }).catch(error => {
      console.log(error)
    })
  }else {
    axios.get("/sdir").then((respon) => {
      console.log(respon.data)
      option.value = respon.data
    }).catch(error => {
      console.log(error)
    })
  }
}
const listDir = () => {
  axios.get("/ldir").then((respon)=>{
    console.log(respon.data)
    respon.data.forEach(item=>option1.value.push(item))
  }).catch(error=>{console.log(error)})
  var index=document.getElementById("ldir").selectedIndex
  Fdir.value=option1.value[index].url
  getDir(Fdir.value)
}
onMounted (()=>{
  // 初始化
  init()
  listDir()
})
</script>

<style>
.span{
  margin-left: 35%;
  color: #FFFFFF;
  font-size: 24px;
}
.label1{
  color: #FFFFFF;
  font-size: 20px;
}
.checkbox1{
  margin-left: 15%;
}
.button_load{
  margin-left: 5%;
  height: 50px;
  width: 150px;
  background-image: linear-gradient(to right,#A7E8FF, #3ba7e8);
  border-radius: 5px;
  border-style: none;
}
.div_select{
  margin-top: 5%;
}
.button_change{
  margin-left: 5%;
  height: 50px;
  width: 150px;
  background-image: linear-gradient(to right,#A7E8FF, #3ba7e8);
  border-radius: 5px;
  border-style: none;
}
.button_send{
  margin-top: 5%;
  margin-left: 30%;
  height: 125px;
  width: 300px;
  background-image: linear-gradient(to right,#A7E8FF, #3ba7e8);
  border-radius: 5px;
  border-style: none;
}
.select1{
  margin-left: 15%;

}
.select2{
  margin-left: 5%;
  margin-right: 5%;
}
.tips1{
  margin-left: 20%;
  height: 130px;
  width: 350px
}


@media screen and (min-width: 1200px){
  #myVideo{
    margin-left: 30%;

  }
  .span{
    margin-left: 40%;
    color: #FFFFFF;
    font-size: 24px;
  }
  .label1{
    color: #FFFFFF;
    font-size: 20px;
  }
  .checkbox1{
    margin-left: 35%;
  }
  .button_load{
    margin-left: 5%;
    height: 50px;
    width: 150px;
    background-image: linear-gradient(to right,#A7E8FF, #3ba7e8);
    border-radius: 5px;
    border-style: none;
  }
  .div_select{
    margin-top: 1%;
  }
  .button_change{
    margin-left: 0%;
    height: 50px;
    width: 150px;
    background-image: linear-gradient(to right,#A7E8FF, #3ba7e8);
    border-radius: 5px;
    border-style: none;
  }
  .button_send{
    margin-top: 1%;
    margin-left: 37%;
    height: 125px;
    width: 300px;
    background-image: linear-gradient(to right,#A7E8FF, #3ba7e8);
    border-radius: 5px;
    border-style: none;
  }
  .select1{
    margin-left: 35%;
  }
  .select2{
    margin-left: 5%;
    margin-right: 5%;
  }
  .tips1{
    margin-left: 35%;
    height: 130px;
    width: 350px
  }
}

</style>
