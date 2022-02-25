

export default ({
  state: {
    defaultWatchIcon: "/card/watch.png",
    defaulCollectIcon: "/card/collect.png",
    defaulCommentIcon: "/card/comment.png",
    defaulPublishTimeIcon: "/card/time.png"
  },
  mutations: {
    updateNavbarLayoutType (state, type) {    //demo
      state.navbarLayoutType = type
    },        //    this.$store.commit('common/updateNavbarLayoutType', val)
  },
  actions: {

  }
})
