import router from './router'
import store from './store'
import { Message } from 'element-ui'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import { getToken } from '@/utils/auth'
import { isPathMatch } from '@/utils/validate'
import { isRelogin } from '@/utils/request'

NProgress.configure({ showSpinner: false })

const whiteList = ['/login', '/register' ,'/blog', '/blog/detail']

const isWhiteList = (path) => {
  return whiteList.some(pattern => isPathMatch(pattern, path))
}

router.beforeEach((to, from, next) => {
  NProgress.start()
  
  if (getToken()) {
    to.meta.title && store.dispatch('settings/setTitle', to.meta.title)
    const isLock = store.getters.isLock
    
    /* has token*/
    if (to.path === '/login') {
      next({ path: '/' })
      NProgress.done()
    } else if (isWhiteList(to.path)) {
      next()
    } else if (isLock && to.path !== '/lock') {
      next({ path: '/lock' })
      NProgress.done()
    } else if (!isLock && to.path === '/lock') {
      // ========== 修复点1：解锁后根据角色跳转 ==========
      const roles = store.getters.roles
      if (roles.includes('common')) {
        next({ path: '/blog', replace: true })  // 普通用户去博客页
      } else {
        next({ path: '/', replace: true })       // 管理员去首页
      }
      NProgress.done()
      // ===========================================
    } else {
      if (store.getters.roles.length === 0) {
        isRelogin.show = true
        // 判断当前用户是否已拉取完user_info信息
        store.dispatch('GetInfo').then(() => {
          isRelogin.show = false
          store.dispatch('GenerateRoutes').then(accessRoutes => {
            router.addRoutes(accessRoutes)
            
            // ========== 修复点2：首页重定向 ==========
            const roles = store.getters.roles
            if ((to.path === '/' || to.path === '/index') && roles.includes('common')) {
              next({ path: '/blog', replace: true })
              return
            }
            // =========================================
            
            next({ ...to, replace: true })
          })
        }).catch(err => {
          store.dispatch('LogOut').then(() => {
            Message.error(err)
            next({ path: '/' })
          })
        })
      } else {
        // ========== 修复点3：已有角色时的根路径处理 ==========
        const roles = store.getters.roles
        if ((to.path === '/' || to.path === '/index')) {
          if (roles.includes('common')) {
            next({ path: '/blog', replace: true })
            return
          }
        }
        // =================================================
        next()
      }
    }
  } else {
    // 没有token
    if (isWhiteList(to.path)) {
      next()
    } else {
      next(`/login?redirect=${encodeURIComponent(to.fullPath)}`)
      NProgress.done() 
    }
  }
})

router.afterEach(() => {
  NProgress.done()
})