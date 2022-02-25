<template>
    <transition name="fade-main">
        <router-view></router-view>
    </transition>
</template>
<script>
</script>
<style>
    html body {
        height: 100%;
        margin: 0px 0px 0px 0px;
    }
    /* 设置滚动条的样式 */
    ::-webkit-scrollbar {
        height: 6px;
        width:6px;
    }
    /* 滚动槽 */
    ::-webkit-scrollbar-track {
        -webkit-box-shadow:inset 0 0 6px rgba(0,0,0,0.1);
        border-radius:10px;
    }
    /* 滚动条滑块 */
    ::-webkit-scrollbar-thumb {
        border-radius:10px;
        background:rgba(255,255,255,0.3);
        -webkit-box-shadow:inset 0 0 6px rgba(0,0,0,0.25);
    }

    #app {
        font-family: "Avenir", Helvetica, Arial, sans-serif;
        -webkit-font-smoothing: antialiased;
        -moz-osx-font-smoothing: grayscale;
        text-align: center;
        color: #2c3e50;
        /*margin-top: 60px;*/
        width: 100%;
        /*height: auto;*/
    }

    .routerWarp {
        background-color: #f5f8fc;
    }

</style>
