<template>
    <div class="rt-overview padding">
        <div class="base-info no-select">
            <div class="text-bold text-lg no-select" style="color: rgba(0, 0, 0, 0.4);">系统信息</div>
            <div class="card-container flex justify-between">
                <!--        //系统信息、名称、集群状态、集群数、运行时间、版本、-->
                <div class="left flex flex-direction justify-between radius shadow-blur
                padding-sm flex-sub text-white-theme margin">
                    <div class="l-row padding-lr-xs flex justify-between">
                        <div class="text-bold text-lg">Jmq Broker</div>
                        <div class="round l-row-more flex justify-center align-center shadow"><i class="el-icon-more"></i>
                        </div>
                    </div>
                    <div class="l-row padding-lr-xs flex justify-between">
                        <div>集群状态：</div>
                        <div class="flex align-center">
                            <div class="light round margin-right-sm shadow-blur-weight"></div>
                            运行中
                        </div>
                    </div>
                    <div class="l-row padding-lr-xs flex justify-between">
                        <div>节点数量：</div>
                        <div class="">{{overView.onlineBrokerNum}}/{{overView.brokerMetaNum}}</div>
                    </div>
                    <div class="l-row padding-lr-xs flex justify-between">
                        <div>运行时间：</div>
                        <div>{{overView.earliestClusterTimeStr}}</div>
                    </div>
                    <div class="l-row padding-lr-xs flex justify-between">
                        <div>平台版本：</div>
                        <div>{{overView.brokerVersion}}</div>
                    </div>
                </div>

                <!--        //消息流入（累计）、消息流出（累计）、连接数（累计、历史）、订阅数（累计、历史）-->
                <div class="right flex-sub margin grid justify-between">
                    <div class="num-item text-white-theme">
                        <div class="msg-in item radius flex-sub shadow-blur margin-bottom-xs margin-right-xs">
                            <div class="padding-sm">消息流入</div>
                            <div class="flex align-center padding-lr">
                                <div class="text-xl text-white padding-right-sm">{{overView.inMsgNum}}</div>
                                <div>条</div>
                            </div>
                        </div>
                    </div>
                    <div class="num-item text-white-theme">
                        <div class="msg-out item radius flex-sub shadow-blur margin-bottom-xs margin-left-xs">
                            <div class="padding-sm">消息流出</div>
                            <div class="flex align-center padding-lr">
                                <div class="text-xl text-white padding-right-sm">{{overView.outMsgNum}}</div>
                                <div>条</div>
                            </div>
                        </div>
                    </div>
                    <div class="num-item text-white-theme">
                        <div class="client-num item radius flex-sub shadow-blur margin-top-xs margin-right-xs">
                            <div class="padding-sm">连接数</div>
                            <div class="flex align-center padding-lr">
                                <div class="text-xl text-white padding-right-sm">{{overView.clientNum}}</div>
                                <div>个</div>
                            </div>
                        </div>
                    </div>
                    <div class="num-item text-white-theme">
                        <div class="subscribe-num item radius flex-sub shadow-blur margin-top-xs margin-left-xs">
                            <div class="padding-sm">订阅数</div>
                            <div class="flex align-center padding-lr">
                                <div class="text-xl text-white padding-right-sm">{{overView.subscribeNum}}</div>
                                <div>个</div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>


        <!--        //消息流入、消息流出(折线图)-->
        <div class="broker-info no-select margin-top">
            <div class="text-bold text-lg no-select title" style="color: rgba(0, 0, 0, 0.4);">消息指标</div>
            <div class="card-container flex justify-between">
                <div class="left flex flex-direction margin padding-sm radius flex-sub shadow-blur">
                    <div class="text-white-theme">消息流入</div>
                    <canvas id="msg-in-canvas" class="flex-sub"></canvas>
                </div>
                <div class="right flex flex-direction margin padding-sm radius flex-sub shadow-blur">
                    <div class="text-white-theme">消息流出</div>
                    <canvas id="msg-out-canvas" class="flex-sub"></canvas>
                </div>
            </div>
        </div>

    </div>
</template>

<script>
    import {mergeTimeStr} from "@/utils";

    let reqOverviewInterval;
    let msgInCanvasInterval;
    let msgOutCanvasInterval;
    export default {
        name: "RealTimeOverview",
        data() {
            return {
                overView: {
                    brokerMetaNum: 0,
                    onlineBrokerNum: 0,
                    earliestClusterTime: 0,
                    earliestClusterTimeStr: "",
                    brokerVersion: 0,
                    inMsgNum: 0,
                    outMsgNum: 0,
                    clientNum: 0,
                    subscribeNum: 0
                }
            }
        },
        created() {
            this.reqOverviewData()
            reqOverviewInterval = setInterval(this.reqOverviewData, 1000)
        },
        mounted() {
            this.initMsgInCanvas()
            this.initMsgOutCanvas()
        },
        destroyed() {
            this.clearCustomInterval()
        },
        methods: {
            reqOverviewData() {
                this.$http({
                    url: this.$http.baseUrl(`/broker/overview/real_time`),
                    method: "GET"
                }).then(({data}) => {
                    if (data && data.code === 0) {
                        data.data.preSeconedInNum = this.overView.preSeconedInNum !== undefined ? this.overView.inMsgNum : 0
                        data.data.preSeconedOutNum = this.overView.preSeconedOutNum !== undefined ? this.overView.outMsgNum : 0
                        data.data.earliestClusterTimeStr = mergeTimeStr((new Date().getTime() - new Date(data.data.earliestClusterTime).getTime()) / 1000)
                        this.overView = data.data
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

            initMsgInCanvas() {
                let that = this
                // 自定义线图变更动画
                this.$f2.Animate.registerAnimation('lineUpdate', function (updateShape, animateCfg) {
                    const cacheShape = updateShape.get('cacheShape'); // 该动画 shape 的前一个状态
                    const cacheAttrs = cacheShape.attrs; // 上一个 shape 属性
                    const oldPoints = cacheAttrs.points; // 上一个状态的关键点
                    const newPoints = updateShape.attr('points'); // 当前 shape 的关键点

                    const oldLength = oldPoints.length;
                    const newLength = newPoints.length;
                    const deltaLength = newLength - oldLength;

                    const lastPoint = newPoints[newPoints.length - 1];
                    for (let i = 0; i < deltaLength; i++) {
                        oldPoints.push(lastPoint);
                    }

                    updateShape.attr(cacheAttrs);
                    updateShape.animate().to({
                        attrs: {
                            points: newPoints
                        },
                        duration: 800,
                        easing: animateCfg.easing
                    });
                });

                const data = [];

                // 添加数据，模拟数据，可以指定当前时间的偏移的秒
                function getRecord(offset) {
                    offset = offset || 0;
                    let value = (that.overView.preSeconedInNum === undefined || that.overView.preSeconedInNum === 0)
                        ? 0 : that.overView.inMsgNum - that.overView.preSeconedInNum
                    return {
                        time: new Date().getTime() + offset * 1000,
                        value: value
                    };
                }

                // data.push(getRecord(-2));
                // data.push(getRecord(-1));
                data.push(getRecord());

                const chart = new this.$f2.Chart({
                    id: 'msg-in-canvas',
                    pixelRatio: window.devicePixelRatio
                });

                const defs = {
                    time: {
                        type: 'timeCat',
                        mask: 'HH:mm:ss',
                        range: [0, 1]
                    },
                    value: {
                        tickCount: 10,
                        min: 0,
                    }
                };
                chart.source(data, defs);
                chart.axis('time', {
                    line: null,// 设置坐标轴线的样式，如果值为 null，则不显示坐标轴线，图形属性
                    tickLine: null,
                    labelOffset: 10, // 坐标轴文本距离轴线的距离
                    label: function label(text, index, total) {
                        const textCfg = {
                            text: '',
                        };
                        if (index === 0) {
                            textCfg.textAlign = 'left';
                            textCfg.text = text;
                        } else if (index === total - 1) {
                            textCfg.textAlign = 'right';
                            textCfg.text = text;
                        }
                        textCfg.fill = '#fff';
                        return textCfg;
                    },
                });
                chart.axis('value', {
                    labelOffset: 20, // 坐标轴文本距离轴线的距离
                    label: function label(text) {
                        const cfg = {};
                        cfg.fill = '#fff';
                        return cfg;
                    },
                    grid: (text, index, total) => {
                        return {
                            stroke: 'rgba(255,255,255,0.25)'
                        }
                    },
                });

                chart.line()
                    .position('time*value')
                    .animate({
                        update: {
                            animation: 'lineUpdate'
                        }
                    })
                    .color('#0be78c')
                    .size(3);

                chart.render();

                msgInCanvasInterval = setInterval(function () {
                    data.push(getRecord());
                    chart.changeData(data);
                }, 1000);
            },
            initMsgOutCanvas() {
                let that = this
                // 自定义线图变更动画
                this.$f2.Animate.registerAnimation('lineUpdate', function (updateShape, animateCfg) {
                    const cacheShape = updateShape.get('cacheShape'); // 该动画 shape 的前一个状态
                    const cacheAttrs = cacheShape.attrs; // 上一个 shape 属性
                    const oldPoints = cacheAttrs.points; // 上一个状态的关键点
                    const newPoints = updateShape.attr('points'); // 当前 shape 的关键点

                    const oldLength = oldPoints.length;
                    const newLength = newPoints.length;
                    const deltaLength = newLength - oldLength;

                    const lastPoint = newPoints[newPoints.length - 1];
                    for (let i = 0; i < deltaLength; i++) {
                        oldPoints.push(lastPoint);
                    }

                    updateShape.attr(cacheAttrs);
                    updateShape.animate().to({
                        attrs: {
                            points: newPoints
                        },
                        duration: 800,
                        easing: animateCfg.easing
                    });
                });

                const data = [];

                // 添加数据，模拟数据，可以指定当前时间的偏移的秒
                function getRecord(offset) {
                    offset = offset || 0;
                    let value = (that.overView.preSeconedOutNum === undefined || that.overView.preSeconedOutNum === 0)
                        ? 0 : that.overView.outMsgNum - that.overView.preSeconedOutNum
                    return {
                        time: new Date().getTime() + offset * 1000,
                        value: value
                    };
                }

                // data.push(getRecord(-2));
                // data.push(getRecord(-1));
                data.push(getRecord());

                const chart = new this.$f2.Chart({
                    id: 'msg-out-canvas',
                    pixelRatio: window.devicePixelRatio
                });

                const defs = {
                    time: {
                        type: 'timeCat',
                        mask: 'HH:mm:ss',
                        range: [0, 1]
                    },
                    value: {
                        tickCount: 10,
                        min: 0
                    }
                };
                chart.source(data, defs);
                chart.axis('time', {
                    line: null,// 设置坐标轴线的样式，如果值为 null，则不显示坐标轴线，图形属性
                    tickLine: null,
                    labelOffset: 10, // 坐标轴文本距离轴线的距离
                    label: function label(text, index, total) {
                        const textCfg = {
                            text: '',
                        };
                        if (index === 0) {
                            textCfg.textAlign = 'left';
                            textCfg.text = text;
                        } else if (index === total - 1) {
                            textCfg.textAlign = 'right';
                            textCfg.text = text;
                        }
                        textCfg.fill = '#fff';
                        return textCfg;
                    },
                });
                chart.axis('value', {
                    labelOffset: 20, // 坐标轴文本距离轴线的距离
                    label: function label(text) {
                        const cfg = {};
                        cfg.fill = '#fff';
                        return cfg;
                    },
                    grid: (text, index, total) => {
                        return {
                            stroke: 'rgba(255,255,255,0.25)'
                        }
                    },
                });

                chart.line()
                    .position('time*value')
                    .animate({
                        update: {
                            animation: 'lineUpdate'
                        }
                    })
                    .color('#0be78c')
                    .size(3);

                chart.render();

                msgOutCanvasInterval = setInterval(function () {
                    data.push(getRecord());
                    chart.changeData(data);
                }, 1000);
            },
            clearCustomInterval() {
                clearInterval(reqOverviewInterval)
                clearInterval(msgInCanvasInterval)
                clearInterval(msgOutCanvasInterval)
            }
        }
    }
</script>

<style lang="less">
    .rt-overview {
    }

    .base-info {
        .card-container {
            height: 360px;

            .left {
                background: linear-gradient(45deg, #19bcc2, #0c7cd1);

                .l-row-more {
                    width: 38px;
                    height: 38px;
                    background-color: rgba(255, 255, 255, 0.15);
                    cursor: pointer;
                }

                .light {
                    width: 22px;
                    height: 22px;
                    background-color: #0be78c;
                    animation: fade 2s ease-in-out infinite;
                }
            }

            .right {
                .num-item {
                    width: 50%;
                    height: 50%;
                    display: flex;

                    .item {
                        background-image: linear-gradient(45deg, rgba(117, 177, 170, 0.98) 0%, rgba(0, 147, 233, 0.97) 100%);
                    }
                }

            }
        }
    }

    .broker-info {
        .card-container {
            height: 330px;

            .left {
                background: linear-gradient(45deg, #8aa3d9, #258bbb);
            }

            .right {
                background: linear-gradient(45deg, #8c96e8, #554fd5);
            }
        }
    }
</style>
