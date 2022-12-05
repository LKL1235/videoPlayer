import { createRouter, createWebHistory } from 'vue-router'
import GlobalView from "@/views/GlobalView.vue"
import VideoPlayer from "@/components/VideoPlayer.vue"
import Client from "@/components/Client.vue"
import MainView from "@/views/MainView.vue"
import {useUserStore} from "@/stores/userStore";

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
let store:any=null
router.beforeEach((to, from,next) => {
  if (store === null) {
    store = useUserStore();
  }
  if(to.path === "/"|| to.path === "/main") {
    next()
  }
  if(store.roomId==0&&store.username==''){
    return next({ name: 'main' })
  }else {
    next()
  }

})
export default router
