<template>
  <div>
    <h1>w d w d </h1>
    <video autoplay controls width="100%" height="500" id="myVideo"></video>
  </div>
</template>

<script lang="ts" setup>
import flvjs from 'flv.js'
import {onMounted} from "vue";

// 检测浏览器是否支持 flv.js
const createdPlay=()=>{
  if (flvjs.isSupported()) {
    var videoElement = document.getElementById('videoElement');
    var flvPlayer = flvjs.createPlayer({
      type: 'flv',
      url: 'http://139.9.32.27/live?1935&app=live&stream=test'
    });
    flvPlayer.attachMediaElement(videoElement);
    flvPlayer.load();
    flvPlayer.play();
  }
}
onMounted(()=>{
  createdPlay()
})
</script>
