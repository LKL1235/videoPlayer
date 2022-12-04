<template>
  <h1 style="color: #FFFFFF;position: relative;left: 25%">当前房间号为{{user.$state.roomId}}</h1>
  <router-view/>
</template>

<script setup lang="ts">
import {useUserStore} from "@/stores/userStore";

const user=useUserStore()
</script>

<style scoped>

</style>
