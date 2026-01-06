import { createStore } from 'vuex'

const MEMBER = 'MEMBER'
export default createStore({
  state: {
    member: window.SessionStorage.get(MEMBER) || {},
  },
  getters: {},
  //Java对象中的set方法（同步）
  mutations: {
    setMember(state, _member) {
      state.member = _member
      window.SessionStorage.set(MEMBER, _member)
    },
  },
  //Java对象中的set方法（异步）
  actions: {},
  modules: {},
})
