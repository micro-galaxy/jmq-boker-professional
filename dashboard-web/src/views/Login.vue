<template>
    <div class="wh-fill">
        <background-img v-bind:img="'/img/jmq-bg.png'"></background-img>
        <div class="wh-fill login-warp flex justify-center" @keyup.enter="login">
            <div class="login-box flex flex-direction shadow radius">
                <div class="icon flex align-center padding padding-top-xl">
                    <img src="/logo.png">
                    <h1 class="margin-left-sm" style="color: #667AFA">Jmq</h1>
                </div>

                <div class="padding-xl flex flex-direction margin-top-xl">
                    <el-input
                            placeholder="用户名"
                            prefix-icon="el-icon-user"
                            v-model="dataForm.user">
                    </el-input>
                    <el-input
                            class="margin-top-lg"
                            placeholder="密码"
                            type="password"
                            prefix-icon="el-icon-lock"
                            v-model="dataForm.password">
                    </el-input>
                    <div style="padding-top: 100px"></div>
                    <div class="flex justify-between align-center">
                        <el-checkbox v-model="rememberMe" @change="rbmChange">自动登录</el-checkbox>
                        <el-button class="self-end padding-lr-xl" type="primary" @click="login">登录</el-button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
    import md5 from 'blueimp-md5'
    import {compile} from "@/utils"
    import BackgroundImg from "@/components/BackgroundImg.vue";

    export default {
        name: "login",
        components: {
            BackgroundImg
        },

        data() {
            return {
                dataForm: {
                    user: "",
                    password: ""
                },
                rememberMe: localStorage.getItem("rbm") === 'true'
            }
        },
        methods: {
            login() {
                let password = md5(this.dataForm.user + this.dataForm.password)
                this.$http({
                    url: this.$http.baseUrl(`/broker/user/login`),
                    method: "POST",
                    data: this.$http.postDataMerge({
                        username: this.dataForm.user,
                        password: password
                    })
                }).then(({data}) => {
                    if (data && data.code === 0) {
                        localStorage.removeItem("user")
                        sessionStorage.setItem("user", compile(JSON.stringify({
                            name: this.dataForm.user,
                            authorization: password
                        })))
                        this.rememberMe && localStorage.setItem("user", compile(JSON.stringify({
                            name: this.dataForm.user,
                            authorization: password
                        })))
                        this.$router.push({name: 'Index'})
                    } else {
                        this.dataForm.user = ""
                        this.dataForm.password = ""
                        return Promise.reject(data.msg)
                    }
                }).catch(msg => {
                    this.$message({
                        showClose: true,
                        message: msg,
                        type: 'error'
                    })
                })
            },
            rbmChange() {
                localStorage.setItem("rbm", this.rememberMe)
            }
        }
    }
</script>

<style lang="less">
    .login-warp {
        z-index: 999
    }

    .login-box {
        border-top: 5px solid #667afa;
        margin-top: 160px;
        width: 560px;
        height: 660px;
        backdrop-filter: blur(30px);
        background: rgba(255, 255, 255, 0.3);

        .icon img {
            width: 80px;
            height: 80px;
        }

        .el-input__inner {
            height: 50px !important;
            line-height: 50px !important;
        }

        .el-input {
            font-size: 16px;
        }

        .el-button {
            font-size: 18px;
            font-weight: bold;
        }

        .el-checkbox {
            font-size: 18px;
        }

        .el-checkbox__inner {
            width: 22px;
            height: 22px;
            border-radius: 4px;
        }

        .el-checkbox__inner::after {
            height: 10px;
            left: 5px;
            width: 8px;
            font-weight: bold;
        }

        .el-checkbox__label {
            font-weight: bold;
            color: rgba(255, 255, 255, 0.95);
        }

        .is-checked + .el-checkbox__label {
            color: rgba(255, 255, 255, 0.95);
        }
    }
</style>
