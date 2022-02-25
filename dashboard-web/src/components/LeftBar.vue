<template>
    <div class="left-bar no-select padding-left">
        <el-menu
                default-active="1"
                class="el-menu-vertical-demo"
                @open="handleOpen"
                @close="handleClose"
                @select="selected">
            <el-menu-item index="1">
                <i class="el-icon padding-right-xs">
                    <img v-if="$store.state.menu.curSelectIndex === '1'" src="../assets/img/performance-active.png"
                         style="width: 18px">
                    <img v-else src="../assets/img/performance-inactive.png" style="width: 18px">
                </i>
                <span slot="title">实时概况</span>
            </el-menu-item>
            <el-menu-item index="2">
                <i class="el-icon padding-right-xs">
                    <img v-if="$store.state.menu.curSelectIndex === '2'" src="../assets/img/cluster-active.png"
                         style="width: 18px">
                    <img v-else src="../assets/img/cluster-inactive.png" style="width: 18px">
                </i>
                <span slot="title">集群信息</span>
            </el-menu-item>
            <el-menu-item index="3">
                <i class="el-icon padding-right-xs">
                    <img v-if="$store.state.menu.curSelectIndex === '3'" src="../assets/img/client-active.png"
                         style="width: 18px">
                    <img v-else src="../assets/img/client-inactive.png" style="width: 18px">
                </i>
                <span slot="title">客户端</span>
            </el-menu-item>
            <el-menu-item index="4">
                <i class="el-icon padding-right-xs">
                    <img v-if="$store.state.menu.curSelectIndex === '4'" src="../assets/img/topic-active.png"
                         style="width: 18px">
                    <img v-else src="../assets/img/topic-inactive.png" style="width: 18px">
                </i>
                <span slot="title">订阅</span>
            </el-menu-item>
            <el-submenu index="5">
                <template slot="title">
                    <i class="el-icon padding-right-xs">
                        <img v-if="Number($store.state.menu.curSelectIndex).toFixed(0) === '5'"
                             src="../assets/img/analysis-active.png"
                             style="width: 16px">
                        <img v-else src="../assets/img/analysis-inactive.png" style="width: 16px">
                    </i>
                    <span>统计分析</span>
                </template>
                <el-menu-item index="5.1">
                    <i class="el-icon padding-right-xs">
                        <img v-if="$store.state.menu.curSelectIndex === '5.1'" src="../assets/img/channel-active.png"
                             style="width: 25px">
                        <img v-else src="../assets/img/channel-inactive.png" style="width: 25px">
                    </i>
                    <span slot="title">主题监控</span>
                </el-menu-item>

            </el-submenu>
            <el-menu-item index="6">
                <i class="el-icon padding-right-xs">
                    <img v-if="$store.state.menu.curSelectIndex === '6'" src="../assets/img/plugin-active.png"
                         style="width: 18px">
                    <img v-else src="../assets/img/plugin-inactive.png" style="width: 18px">
                </i>
                <span slot="title">插件</span>
            </el-menu-item>
            <el-menu-item index="7">
                <i class="el-icon padding-right-xs">
                    <img v-if="$store.state.menu.curSelectIndex === '7'" src="../assets/img/user-active.png"
                         style="width: 18px">
                    <img v-else src="../assets/img/user-inactive.png" style="width: 18px">
                </i>
                <span slot="title">用户</span>
            </el-menu-item>
            <el-menu-item index="8">
                <i class="el-icon padding-right-xs">
                    <img v-if="$store.state.menu.curSelectIndex === '8'" src="../assets/img/setting-active.png"
                         style="width: 18px">
                    <img v-else src="../assets/img/setting-inactive.png" style="width: 18px">
                </i>
                <span slot="title">设置</span>
            </el-menu-item>
        </el-menu>
    </div>
</template>

<script>
    export default {
        name: "LeftBar",
        data() {
            return {
                isCollapse: true,
                selectedIndex: 1
            }
        },
        methods: {
            handleOpen(key, keyPath) {
                console.log(key, keyPath);
            },
            handleClose(key, keyPath) {
                console.log(key, keyPath);
            },
            selected(key, keyPath) {
                //TODO  获取路由mete信息，跳转
                this.$store.commit('setMenuCurIndex', key)
                switch (key) {
                    case '1':
                        this.$router.push({name: 'Overview'})
                        break
                    case '2':
                        this.$router.push({name: 'Cluster'})
                        break
                    case '3':
                        this.$router.push({name: 'Client'})
                        break
                }
            }
        }
    }
</script>

<style lang="less">
    .left-bar {
        .el-menu-vertical-demo:not(.el-menu--collapse) {
            width: 200px;
            min-height: 400px;
        }

        .el-menu {
            border-right: none !important;
            background-color: rgba(0, 0, 0, 0);
        }

        .logo {
            img {
                width: 120px;
                height: 120px;
            }

            b {
                color: #667AFA;
            }
        }

        .el-menu-item, .el-submenu__title {
            font-size: 16px !important;
            height: 40px;
            line-height: 35px;
            margin-top: 20px;
            color: #707070;
            border-left: 3px transparent solid;
            border-radius: 4px;
        }

        .el-menu-item.is-active {
            border-left: 3px #667AFA solid;
            background-color: rgb(240, 242, 255);
            color: #667AFA;
        }
    }
</style>
