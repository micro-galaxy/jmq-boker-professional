import Vue from 'vue'
import Vuex from 'vuex'

import WebDefaultModule from "./webDefaultModule"

Vue.use(Vuex)

export default new Vuex.Store({
    modules: {
        WebDefaultModule
    },
    state: {
        user: {
            name: "",
            authorization: ""
        },
        menu: {
            curSelectIndex: "1"
        }
    },
    mutations: {
        setUserInfo(state, userInfo) {
            if (userInfo) {
                state.user = userInfo
            } else {
                state.user = {}
            }
        },
        setMenuCurIndex(state, index) {
            state.menu.curSelectIndex = index
        }
    }
})
