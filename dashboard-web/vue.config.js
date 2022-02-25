
module.exports = {
    devServer: {
        disableHostCheck: true,
        port:6060
    },

    //cnd加载依赖方式
    configureWebpack: {
        externals: {
        }
    },

    outputDir: '../starter/src/main/resources/webapp',
    productionSourceMap: false,
    lintOnSave: false
}