<template>
    <div class="client-container padding">
        <div class="text-bold text-lg no-select" style="color: rgba(0, 0, 0, 0.4);">客户端管理</div>

        <div class="client-search flex padding justify-between shadow">
            <div class="input flex basis-df">
                <el-input v-model="input.clientId" clearable
                          placeholder="客户端ID"></el-input>
                <el-input v-model="input.username" clearable
                          class="margin-left"
                          placeholder="用户名"></el-input>
            </div>
            <el-button type="primary"
                       icon="el-icon-search"
                       style="width: 120px"
                       @click="searchClient()"
                       class="shadow-blur">搜索
            </el-button>
        </div>

        <div class="client-list shadow">
            <!--            //客户端 ID-->
            <!--            //用户名-->
            <!--            //IP 地址-->
            <!--            //订阅数-->
            <!--            //Keepalive-->
            <!--            //协议-->
            <!--            //连接状态-->
            <!--            //连接时间-->
            <!--            //剔除、删除-->
            <el-table
                    :data="clients"
                    empty-text="暂无客户端"
                    height="590"
                    style="width: 100%">
                <el-table-column
                        prop="deviceId"
                        label="客户端 ID"
                        min-width="290">
                    <template slot-scope="scope">
                        <!--                        <div class="flex justify-center align-center">-->
                        <!--                            <img src="../../assets/img/client-active.png"-->
                        <!--                                 style="width: 23px">-->
                        <span style="color: #667AFA;cursor: pointer">{{ scope.row.deviceId }}</span>
                        <!--                        </div>-->
                    </template>
                </el-table-column>
                <el-table-column
                        prop="username"
                        label="用户名"
                        min-width="180">
                </el-table-column>
                <el-table-column
                        prop="ip"
                        label="IP 地址"
                        min-width="180">
                </el-table-column>
                <el-table-column
                        prop="subscribeNum"
                        label="订阅数"
                        min-width="160">
                </el-table-column>
                <el-table-column
                        prop="keepalive"
                        label="Keepalive（秒）"
                        min-width="160">
                </el-table-column>
                <el-table-column
                        prop="protocol"
                        label="协议"
                        min-width="160">
                </el-table-column>
                <el-table-column
                        prop="status"
                        label="连接状态"
                        min-width="120">
                    <template slot-scope="scope">
                        <div class="flex align-center">
                            <div class="round margin-right-xs" :class="scope.row.status ? 'runLabel' : 'stopLabel'"></div>
                            <span>{{ scope.row.statusStr }}</span>
                        </div>
                    </template>
                </el-table-column>
                <el-table-column
                        prop="time"
                        label="连接时间"
                        min-width="220">
                </el-table-column>
                <el-table-column
                        label="操作">
                    <template slot-scope="scope">
                        <!--                        <el-button @click="handleClick(scope.row)" type="text" size="small">查看</el-button>-->
                        <el-button type="text" size="small">剔除</el-button>
                    </template>
                </el-table-column>
            </el-table>

            <div class="foot flex justify-center align-center">
                <el-pagination
                        @size-change="pageSizeChange"
                        @current-change="pageCurrentChange"
                        :current-page="search.curPage"
                        :page-sizes="[10, 20, 50, 100]"
                        :page-size="search.pageSize"
                        background
                        layout="prev,pager,next,total, jumper,sizes"
                        :total="search.pageTotal">
                </el-pagination>
            </div>
        </div>


    </div>
</template>

<script>
    let reqClientsInterval;
    export default {
        name: "ClientManager",
        data() {
            return {
                input:{
                    clientId: '',
                    username: '',
                },
                search: {
                    clientId: '',
                    username: '',
                    curPage: 1,
                    pageSize: 10,
                    pageTotal: 0
                },
                clients: []
            }
        },
        created() {
            this.reqClientsData()
            reqClientsInterval = setInterval(this.reqClientsData, 3000)
        },
        destroyed() {
            this.clearCustomInterval()
        },


        methods: {

            searchClient(){
                this.search.clientId = this.input.clientId
                this.search.username = this.input.username
                this.reqClientsData()
            },

            reqClientsData() {
                this.$http({
                    url: this.$http.baseUrl(`/broker/client/page`),
                    method: "POST",
                    data: {
                        clientId: this.search.clientId,
                        username: this.search.username,
                        curPage: this.search.curPage,
                        size: this.search.pageSize
                    }
                }).then(({data}) => {
                    if (data && data.code === 0) {
                        this.search.pageTotal = data.total
                        this.clients = data.data.map(v => {
                            // v.status = false
                            v.statusStr = v.status ? "已连接" : "离线"
                            return v
                        })
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

            pageSizeChange(option) {
                this.search.pageSize = option
            },

            pageCurrentChange(option) {
                this.search.curPage = option
            },

            clearCustomInterval() {
                clearInterval(reqClientsInterval)
            }
        }

    }
</script>

<style lang="less">

    .client-search {
        margin: 30px 30px 0 30px;
        border-radius: 6px;
        background: hsla(0, 0%, 80%, .1);
        backdrop-filter: blur(22px);
    }

    .client-list {
        /*position: absolute;*/
        height: 636px;
        margin: 12px 30px 0 30px;
        border-radius: 3px;
        background: hsla(0, 0%, 100%, .1);
        backdrop-filter: blur(16px);
        overflow: hidden;

        table {
            margin: 0 !important;
        }

        .el-table__header {
            tr {
                background: linear-gradient(15deg, #6887c9, #258bbb) !important;
                backdrop-filter: blur(22px) !important;
            }
        }

        .is-leaf {
            background-color: rgba(0, 0, 0, 0) !important;
            color: white;
            border-bottom: 0 !important;
        }

        .el-table__cell.gutter {
            background-color: #258bbb;
        }

        .runLabel {
            width: 10px;
            height: 10px;
            background-color: #34c388;
        }

        .stopLabel {
            width: 10px;
            height: 10px;
            background-color: #e50d0d;
        }

        .foot {
            background-color: white;
            padding: 8px 0 5px 0;
        }
    }
</style>
