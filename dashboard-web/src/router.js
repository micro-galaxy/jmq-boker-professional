import Vue from 'vue'
import VueRouter from 'vue-router'


// eslint-disable-next-line no-undef
Vue.use(VueRouter)

const indexRouters = [
    {
        path: '/overview',
        name: 'Overview',
        component: resolve => require(['./components/views/RealTimeOverview.vue'], resolve)
    },
    {
        path: '/cluster',
        name: 'Cluster',
        component: resolve => require(['./components/views/NodeClusterMonitor.vue'], resolve)
    },
    {
        path: '/client',
        name: 'Client',
        component: resolve => require(['./components/views/ClientManager.vue'], resolve)
    }
];

export default new VueRouter({
    mode: 'hash',
    scrollBehavior: () => ({y: 0}),
    routes: [
        {
            path: '/',
            name: 'Index',
            component: resolve => require(['./views/Index.vue'], resolve),
            redirect: {name: 'Overview'},
            children: indexRouters
        },
        {
            path: "*",
            redirect: {name: 'Index'}
        },
        {
            path: '/login',
            name: 'Login',
            component: resolve => require(['./views/Login.vue'], resolve),
            // redirect: {name: 'ArticleListContainer'},
            // children: indexRouters
        },
        // {
        //     path: '/about',
        //     name: 'about',
        //     // route level code-splitting
        //     // this generates a separate chunk (about.[hash].js) for this route
        //     // which is lazy-loaded when the route is visited.
        //     component: () => import(/* webpackChunkName: "about" */ './views/About.vue')
        // }
    ]
})


