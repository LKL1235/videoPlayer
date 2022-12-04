<template>
  <div>
  <button @click="createRoom">创建房间</button>
  </div>
  <div>
  <button @click="joinRoom">加入房间</button>
  </div>
  <div style="margin-top: 15%;">
  <a style="color: #FFFFFF;margin-left: 33%">请输入一个好听的名字</a>
  </div>
  <div>
  <input v-model="username" style="margin-top: 5%;margin-left: 31%">
  </div>
  <div style="margin-top: 15%;">
    <a style="color: #FFFFFF;margin-left: 33%">请输入房间号</a>
  </div>
  <div>
    <input v-model="roomId" style="margin-top: 5%;margin-left: 31%">
  </div>
</template>

<script setup lang="ts">
import {useUserStore} from "@/stores/userStore";
import {ref} from "vue";
import {useRouter} from "vue-router";

const user=useUserStore()
const router=useRouter()
const username=ref("")
const roomId=ref(0)
const createRoom = () => {
  user.$state.username=username.value
  user.$state.roomId=roomId.value
  router.push('/player')
}

const joinRoom=()=>{
  user.$state.username=username.value
  user.$state.roomId=roomId.value
  router.push('/client')
}

</script>

<style scoped>
button{
  margin-left: 30%;
  margin-top: 20%;
  width: 200px;
  height: 100px;
  background-color: #214586;
  border-radius: 5px;
  border-style: none;
  color: #FFFFFF;
}
</style>
