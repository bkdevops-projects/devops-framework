import Vue from 'vue'

import 'normalize.css/normalize.css' // A modern alternative to CSS resets

import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'

import Avue from '@smallwei/avue'
import '@smallwei/avue/lib/index.css'

import '@/styles/index.scss' // global css

import App from './App'
import store from './store'
import router from './router'

import '@/icons' // icon
import '@/permission' // permission control
import axios from '@/utils/request'
import crudCommon from '@/layout/mixin/crud'
import basicBlock from '@/components/basic-block/main'
import basicContainer from '@/components/basic-container/main'

window.$crudCommon = crudCommon
window.axios = axios

Vue.config.productionTip = false

Vue.use(ElementUI)
Vue.use(Avue)

// 注册全局容器
Vue.component('basicContainer', basicContainer)
Vue.component('basicBlock', basicBlock)

new Vue({
  el: '#app',
  router,
  store,
  render: h => h(App)
})
