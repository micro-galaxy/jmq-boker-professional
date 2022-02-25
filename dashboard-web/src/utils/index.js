import Vue from 'vue'

/**
 * 获取uuid
 */
export function getUUID() {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, c => {
        return (c === 'x' ? (Math.random() * 16 | 0) : ('r&0x3' | '0x8')).toString(16)
    })
}

/**
 * 获取uuid
 */
export function getRandomLetter(length) {
    let randomLetter = ''
    for (let i = 0; i < length; i++) {
        randomLetter += (Math.random() * 16 | 0).toString(16)
    }
    return randomLetter
}

/**
 * 是否有权限
 * @param {*} key
 */
export function isAuth(key) {
    return JSON.parse(sessionStorage.getItem('permissions') || '[]').indexOf(key) !== -1 || false
}

/**
 * 是否已登录
 */
export function checkToken() {
    let user = Vue.cookies.get("user")
    return user && JSON.parse(unescape(user)).token && JSON.parse(unescape(user)).expire > new Date().getTime()
}

/**
 * 树形数据转换
 * @param {*} data
 * @param {*} id
 * @param {*} pid
 */
export function treeDataTranslate(data, id = 'id', pid = 'parentId') {
    var res = []
    var temp = {}
    for (var i = 0; i < data.length; i++) {
        temp[data[i][id]] = data[i]
    }
    for (var k = 0; k < data.length; k++) {
        if (temp[data[k][pid]] && data[k][id] !== data[k][pid]) {
            if (!temp[data[k][pid]]['children']) {
                temp[data[k][pid]]['children'] = []
            }
            if (!temp[data[k][pid]]['_level']) {
                temp[data[k][pid]]['_level'] = 1
            }
            data[k]['_level'] = temp[data[k][pid]]._level + 1
            temp[data[k][pid]]['children'].push(data[k])
        } else {
            res.push(data[k])
        }
    }
    return res
}

// 加密函数
export function compile(code) {
    var c = String.fromCharCode(code.charCodeAt(0) + code.length);
    for (var i = 1; i < code.length; i++) {
        c += String.fromCharCode(code.charCodeAt(i) + code.charCodeAt(i - 1));
    }
    return escape(c);
}

// 解密函数
export function uncompile(code) {
    code = unescape(code);
    var c = String.fromCharCode(code.charCodeAt(0) - code.length);
    for (var i = 1; i < code.length; i++) {
        c += String.fromCharCode(code.charCodeAt(i) - c.charCodeAt(i - 1));
    }
    return c;
}

export function getLoginInfo() {
    let auth = window.sessionStorage.getItem("user") || window.localStorage.getItem("user")
    if (!auth) {
        return undefined
    }
    return JSON.parse(uncompile(auth))
}

/**
 * 清除登录信息
 */
export function clearLoginInfo() {
    window.localStorage.removeItem("user")
    window.sessionStorage.removeItem("user")
}

export function getRsaPublicKey() {
    return "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAuXwBXbqzDCraizA2SUfR\n" +
        "B/1j493F/tjQnGm7vkKt8CM4JAdRx0dip8Zsnh3AaQL1deAQCE1gSxYhT5SdcBbE\n" +
        "nlK7+P66reRkPmu48PxOxm6GlFapBiRw4NL1AzGYAT9tTB1y2xRvcgDOuLMTCpUs\n" +
        "he+KrUYsTDXug02WkQZv/rCw/p5BzUC66nuuYPeM7ZRu6VNaeklc33EoUc+PoS+Z\n" +
        "hqu6j04YHovX8VfRCp4CUG4/+2DneMp/kQLjBz+YJ2mBOfKr7bkK2ULkReVE77n+\n" +
        "W8mW2WpR7RTOM1P/SLnB4BkUUTYGkElGif3ndSNFObzhB5gfNyi2sJZazb5NmVUa\n" +
        "3wIDAQAB\n"
}

export function mergeTimeStr(seconds) {
        let daySec = 24 *  60 * 60;
        let hourSec=  60 * 60;
        let minuteSec=60;
        let dd = Number(Math.floor(seconds / daySec).toFixed(0));
        let hh = Number(Math.floor((seconds % daySec) / hourSec).toFixed(0));
        let mm = Number(Math.floor((seconds % hourSec) / minuteSec).toFixed(0));
        let ss = Number((seconds%minuteSec).toFixed(0));
        if(dd > 0){
            return dd + "天" + hh + "小时" + mm + "分钟"+ss+"秒";
        }else if(hh > 0){
            return hh + "小时" + mm + "分钟"+ss+"秒";
        } else if (mm > 0){
            return mm + "分钟"+ss+"秒";
        }else{
            return ss+"秒";
        }
}
