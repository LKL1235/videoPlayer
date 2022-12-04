import { createRouter, createWebHistory } from 'vue-router'
import GlobalView from "@/views/GlobalView.vue"
import VideoPlayer from "@/components/VideoPlayer.vue"
import Client from "@/components/Client.vue"
import MainView from "@/views/MainView.vue"

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/global',
      name: 'global',
      component: GlobalView,
      children:[
        {
          path:'/player',
          name:'player',
          component:VideoPlayer,
        },
        {
          path:'/client',
          name:'client',
          component:Client,
        },
      ]
    },
    {
        path:'/main',
        name:'main',
        component:MainView,
    },
    {
      path:'/',
      redirect:'/main',
    }
  ]
})

export default router
