<template>
    <div class="top-bar no-select">
        <div class="menu flex align-center justify-between">
            <div class="left flex">
                <div class="logo flex align-center justify-around padding-left">
                    <img src="/logo.png">
                    <b class="text-bold padding-left-xs">Jmq Dashboard</b>
                </div>
            </div>

            <div class="right flex">
                <div class="r-item flex align-center justify-around"
                     @click="openUrl('https://github.com/micro-galaxy/jmq-broker')">
                    <img src="../assets/img/github.png">
                    <div class="name text-bold round align-center">
                        GitHub
                    </div>
                </div>

                <el-popover
                        popper-class="c-pointer no-select"
                        placement="top-start"
                        close-delay="500"
                        trigger="hover">
                    <div class="u-menu flex flex-direction align-center padding-tb-xs">
                        <div class="um-item padding-tb-xs" @click="dialogShow = true">
                            <i class="el-icon-lock padding-left-xs"></i>
                            修改密码
                        </div>
                        <div class="um-item padding-tb-xs" @click="loginOut">
                            <i class="el-icon-switch-button padding-left-xs"></i>
                            退出登录
                        </div>
                    </div>
                    <div class="r-item flex align-center justify-around" slot="reference">
                        <img src="../assets/img/top-bar-user.png">
                        <div class="name text-bold round align-center">
                            admin
                            <i class="el-icon-arrow-down"></i>
                        </div>
                    </div>
                </el-popover>
            </div>
        </div>

        <el-dialog title="修改密码"
                   append-to-body
                   width="520px"
                   :close-on-click-modal="false"
                   :visible.sync="dialogShow">
            <el-input
                    placeholder="用户名"
                    prefix-icon="el-icon-user"
                    :disabled="true"
                    :value="form.name">
            </el-input>
            <el-input
                    class="margin-top-xl"
                    placeholder="旧密码"
                    type="password"
                    prefix-icon="el-icon-lock"
                    v-model="form.password">
            </el-input>
            <el-input
                    class="margin-top"
                    placeholder="新密码"
                    type="password"
                    prefix-icon="el-icon-lock"
                    v-model="form.newPassword">
            </el-input>
            <div slot="footer" class="dialog-footer">
                <el-button @click="dialogShow = false,form.password='',form.newPassword=''">取 消</el-button>
                <el-button type="primary" @click="rePassword">确 定</el-button>
            </div>
        </el-dialog>
    </div>
</template>


<script>
    import md5 from 'blueimp-md5'
    import {JSEncrypt} from 'jsencrypt'
    import {clearLoginInfo, getRsaPublicKey} from "@/utils";

    export default {
        data() {
            return {
                form: {
                    name: this.$store.state.user.name,
                    password: "",
                    newPassword: ""
                },
                dialogShow: false
            };
        },
        created() {
        },

        methods: {
            rePassword() {
                let password = md5(this.form.name + this.form.password)
                let encrypt = new JSEncrypt();
                encrypt.setPublicKey(getRsaPublicKey());
                let newPassword = encrypt.encrypt(this.form.newPassword);
                this.$http({
                    url: this.$http.baseUrl(`/broker/user/re_password`),
                    method: "POST",
                    data: this.$http.postDataMerge({
                        username: this.form.name,
                        password: password,
                        newPassword: newPassword
                    })
                }).then(({data}) => {
                    if (data && data.code === 0) {
                        this.dialogShow = false
                        clearLoginInfo()
                        this.$router.push({name: 'Login'})
                    } else {
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
            loginOut() {
                this.$msgbox.confirm('退出登录, 是否继续?', '提示', {
                    confirmButtonText: '退 出',
                    cancelButtonText: '取 消',
                    type: 'info'
                }).then(() => {
                    clearLoginInfo()
                    this.$router.push({name: 'Login'})
                })
            },
            openUrl(url) {
                window.open(url)
            }
        }
    };
</script>

<style lang="less">
    .c-pointer {
        cursor: pointer;
        font-size: 16px;
        padding: 0;

        .um-item {
            width: 100%;
            color: #707070;
        }

        .um-item:hover {
            background-color: rgb(240, 242, 255);
            color: #667AFA;
        }
    }

    .top-bar {
        height: 80px;
        background: hsla(0,0%,100%,.2);
        backdrop-filter: blur(6px);
        .menu {
            height: 100%;
        }

        .logo {
            cursor: default;

            b {
                color: #667AFA;
                font-size: 18px;
            }

            img {
                width: 70px;
            }
        }

        .r-item {
            cursor: pointer;
            .name {
                color: rgba(0, 0, 0, 0.6);
                font-size: 15px;
                padding: 6px 10px 8px 38px;
                background-color: #f6f7f8;
                left: -36px;
                position: relative;
            }

            .name:hover {
                color: rgba(0, 0, 0, 0.7);
                background-color: #f1f2f3;
            }

            img {
                width: 40px;
                z-index: 1;
            }

            svg {
                z-index: 1;
            }

        }
    }
</style>
