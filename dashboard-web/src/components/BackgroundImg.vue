<template>
    <div class="bg-warp">
        <img class="wh-fill" :src="img">
    </div>
</template>

<script>
    export default {
        props:{
            img:String
        }
    }
</script>

<style scoped>
.bg-warp{
    position: fixed;
    top:0;
    left: 0;
    width: 100%;
    height: 100%;
    z-index: -99;
}
</style>