<template>
    <div class="ncm-view padding">
        <div class="cluster-overview">
            <div class="text-bold text-lg no-select" style="color: rgba(0, 0, 0, 0.4);">集群概述</div>
            <div class="overview-container flex justify-around align-center radius shadow-blur margin text-white-theme no-select">
                <div class="o-card">
                    <div class="title">客户端连接数</div>
                    <div class="content">{{nodeDetail.clientNum}}</div>
                </div>
                <div class="split"></div>
                <div class="o-card">
                    <div class="title">主题订阅数</div>
                    <div class="content">{{nodeDetail.subscribeNum}}</div>
                </div>
                <div class="split"></div>
                <div class="o-card">
                    <div class="title">集群节点数</div>
                    <div class="content">{{nodeDetail.onlineBrokerNum}}/{{nodeDetail.brokerNum}}</div>
                </div>
                <div class="split"></div>
                <div class="o-card">
                    <div class="title">Jmq版本</div>
                    <div class="content">{{nodeDetail.brokerVersion}}</div>
                </div>
                <div class="split"></div>
                <div class="o-card">
                    <div class="title">运行时间</div>
                    <div class="content">{{nodeDetail.earliestClusterTimeStr}}</div>
                </div>
            </div>
        </div>

        <div class="base-info no-select">
            <div class="text-bold text-lg no-select" style="color: rgba(0, 0, 0, 0.4);">集群节点</div>
            <div class="node-container grid justify-between">
                <div v-for="node in nodeDetail.nodes" class="basis-df">
                    <div class="node-card flex flex-direction text-white-theme radius shadow-blur padding-xs margin"
                         :class="node.active ? 'run' : 'stop'">
                        <div class="title flex justify-between align-center shadow-blur">
                            <div class="flex align-center">
                                <img src="logo.png">
                                <div class="text-bold text-df padding-left-xs">{{node.brokerId}}</div>
                            </div>
                            <div class="round l-row-more flex justify-center align-center shadow"><i class="el-icon-more"></i>
                            </div>
                        </div>
                        <div class="body flex flex-direction justify-around padding-lr shadow-blur">
                            <div class="b-line">
                                <div>节点地址:</div>
                                <div>{{node.ip}}</div>
                            </div>
                            <div class="b-line">
                                <div>运行状态:</div>
                                <div class="flex">
                                    <div class="round margin-right-sm shadow-blur-weight" :class="node.active ? 'runLabel' : 'stopLabel'"></div>
                                    <div>{{node.runStatusStr}}</div>
                                </div>
                            </div>
                            <div class="b-line">
                                <div v-if="node.active">运行时间:</div>
                                <div v-if="!node.active">离线时长:</div>
                                <div>{{node.runTimeStr}}</div>
                            </div>
                        </div>
                    </div>

                </div>
            </div>
        </div>
    </div>
</template>

<script>
    import {mergeTimeStr} from "@/utils";
    let reqClusterInfoInterval;
    export default {
        name: "NodeClusterMonitor",

        data() {
            return {
                nodeDetail:{
                    brokerNum:0,
                    onlineBrokerNum: 0,
                    earliestClusterTimeStr: "",
                    brokerVersion: "",
                    clientNum: 0,
                    subscribeNum: 0,
                    nodes: [
                        // {
                        //     brokerId: "jmq-broker-001",
                        //     ip: "192.168.0.66",
                        //     active: true,
                        //     runStatusStr: "运行中",
                        //     runTimeStr: "1天5小时30分钟",
                        //
                        // }
                    ]
                }
            }
        },
        created() {
            this.reqClusterInfoData()
            reqClusterInfoInterval = setInterval(this.reqClusterInfoData, 3000)
        },
        destroyed() {
            this.clearCustomInterval()
        },

        methods:{
            reqClusterInfoData() {
                this.$http({
                    url: this.$http.baseUrl(`/broker/cluster/detail`),
                    method: "GET"
                }).then(({data}) => {
                    if (data && data.code === 0) {
                        let sortNodes = data.data.nodes.filter(v =>{
                            v.runStatusStr = v.active ? '运行中' : '离线'
                            v.runTimeStr = mergeTimeStr((new Date().getTime() - new Date(v.occurTime).getTime()) / 1000)
                            return v.active
                        }).sort((a,b) => new Date(a.occurTime).getTime() - new Date(b.occurTime).getTime())
                        if(sortNodes.length > 0){
                            data.data.earliestClusterTimeStr = mergeTimeStr((new Date().getTime() - new Date(sortNodes[0].occurTime).getTime()) / 1000)
                        }
                        data.data.brokerNum = data.data.nodes.length
                        data.data.onlineBrokerNum = sortNodes.length
                        this.nodeDetail = data.data
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

            clearCustomInterval() {
                clearInterval(reqClusterInfoInterval)
            }
        }
    }
</script>

<style lang="less">
    .cluster-overview{
        .overview-container{
            height: 180px;
            background: linear-gradient(25deg, rgba(25, 188, 194, 0.96), #0c7cd1);
            .o-card{
                display: flex;
                flex-direction: column;
                align-items: center;
                .title{
                    font-size: 18px;
                }
                .content{
                    font-size: 22px;
                    font-weight: bold;
                    color: white;
                    margin-top: 25px;
                }
            }
            .split{
                width: 1px;
                height: 20%;
                border-radius: 1000px;
                background: hsla(0,0%,100%,.3);
                -webkit-backdrop-filter: blur(16px);
                backdrop-filter: blur(16px);
            }
        }
    }

    .node-container {
        .run{
            background: linear-gradient(45deg, #35c7ce, #0c7cd1);
        }
        .stop{
            background: linear-gradient(45deg, #76a5a8, #437093);
        }
        .node-item{
            width: 50%;
        }
        .node-card {
            cursor: pointer;
            overflow: hidden;
            .title {
                padding: 20px 20px 20px 0;
                img{
                    width: 46px;
                    height: 46px;
                }
            }

            .body {
                padding-left: 60px;
                .b-line {
                    display: flex;
                    justify-content: space-between;
                    padding: 16px 0 16px 0;

                    .runLabel {
                        width: 22px;
                        height: 22px;
                        background-color: #0be78c;
                    }
                    .stopLabel {
                        width: 22px;
                        height: 22px;
                        background-color: #e50d0d;
                    }
                }
            }
        }

        .l-row-more {
            width: 38px;
            height: 38px;
            background-color: rgba(255, 255, 255, 0.15);
            cursor: pointer;
        }


    }
</style>
