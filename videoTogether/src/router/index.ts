import { createRouter, createWebHistory } from 'vue-router'
import GlobalView from "@/views/GlobalView.vue"
import VideoPlayer from "@/components/VideoPlayer.vue"


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
        }
      ]
    },
  ]
})

export default router
