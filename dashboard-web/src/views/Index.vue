<template>
    <!-- :style="'min-height:' + height + 'px'" -->
    <div v-if="authed" class="jmq-index wh-fill">
        <background-img v-bind:img="'/img/jmq-bg.png'"></background-img>
        <TopBar class="top-bar"></TopBar>
        <LeftBar class="left-bar"></LeftBar>

        <transition name="fade">
            <keep-alive :include="['ArticleListContainer','writeArticleContainer']">
                <div class="view-container wh-fill">
                    <router-view class="router-view"></router-view>
                </div>
            </keep-alive>
        </transition>

        <!-- TODO: 添加foot -->
    </div>
</template>

<script>
    // @ is an alias to /src
    import BackgroundImg from "@/components/BackgroundImg.vue";
    import TopBar from "@/components/TopBar.vue";
    import LeftBar from "@/components/LeftBar.vue";
    import {getLoginInfo} from "@/utils";

    export default {
        data() {
            return {
                height: document.documentElement.clientHeight,
                authed: false
            };
        },
        components: {
            TopBar,
            BackgroundImg,
            LeftBar
        },

        created() {
            let user = getLoginInfo();
            if (user) {
                this.$store.commit('setUserInfo', user)
                this.authed = true
            } else {
                this.$router.push({name: 'Login'})
            }
        },

        watch: {
            $route: {
                handler(nv, ov) {
                    //TODO 改变已选菜单index
                    debugger
                    this.ifmb = true;
                }
            },
            deep: true
        }
    };
</script>

<style lang="less">
    .jmq-index {
        .top-bar {
            width: 100%;
            top: 0;
            position: fixed;
            box-shadow: 0px 0px 12px 3px rgba(230, 230, 230, 0.2);
            z-index: 100;
        }

        .left-bar {
            height: 100%;
            top: 80px;
            padding-top: 40px;
            left: 0;
            position: fixed;
            z-index: 100;
        }

        .view-container {
            background: hsla(0, 0%, 100%, 0.6);
            backdrop-filter: blur(16px);
            overflow-y: auto;

            > div {
                margin-top: 80px;
                margin-left: 230px;
                min-height: calc(100% - 140px);
                min-width: calc(100% - 290px);
            }
        }

    }

    .el-dialog {
        border-radius: 4px !important;
    }


</style>

