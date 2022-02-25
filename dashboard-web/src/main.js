import Vue from 'vue'
import App from '@/App.vue'
import router from '@/router'
import store from '@/vuex/store'
import VueCookie from 'vue-cookie'
import http from '@/utils/http'
import '@/element-ui'
import '@/element-ui/element-ui-theme'
import '@/assets/publicCss/index.css'
import '@/assets/publicCss/animation.css'
import F2 from '@antv/f2'


Vue.use(VueCookie)
Vue.prototype.$http = http
Vue.prototype.$f2 = F2
Vue.config.productionTip = false

new Vue({
    router,
    store,
    render: render => render(App)
}).$mount('#app')
