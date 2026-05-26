/**
 * 博客模块路由配置
 * 使用方法：在若依主路由文件中导入此配置
 * 
 * 示例：
 * import blogRouter from '@/views/blog/router'
 * 
 * // 在动态路由中添加
 * const router = new Router({
 *   routes: [
 *     ...constantRoutes,
 *     ...blogRouter  // 添加博客路由
 *   ]
 * })
 */

import Layout from '@/layout'

const blogRouter = [
  {
    path: '/redirect',
    component: Layout,
    hidden: true,
    children: [
      {
        path: '/redirect/:path(.*)',
        component: () => import('@/views/redirect')
      }
    ]
  },
  
  // ==================== 博客主框架路由 ====================
  {
    path: '/blog',
    component: () => import('@/views/blog/index.vue'), // 直接引用，不经过若依Layout
    name: 'Blog',
    meta: { 
      title: '博客',
      roles: ['common']  // 只有普通用户能访问
    },
    children: [
      // 首页 - 文章流
      {
        path: 'home',
        name: 'BlogHome',
        component: () => import('@/views/blog/views/HomePage.vue'),
        meta: { 
          title: '首页',
          keepAlive: true,  // 缓存页面状态
          roles: ['common']
        }
      },
      
      // 我的文章列表
      {
        path: 'my-articles',
        name: 'BlogMyArticles',
        component: () => import('@/views/blog/views/MyArticles.vue'),
        meta: { 
          title: '我的文章',
          requireAuth: true,  // 需要登录
          roles: ['common']
        }
      },
      
      // 发布文章页面
      {
        path: 'publish',
        name: 'BlogPublish',
        component: () => import('@/views/blog/views/PublishPage.vue'),
        meta: { 
          title: '发布文章',
          requireAuth: true,  // 需要登录才能发布
          roles: ['common']
        }
      },
      {
        path: 'AiChatBox',
        name: 'AiChatBox',
        component: () => import('@/views/blog/views/AiChatBox.vue'),
        meta:{
          title: 'AI聊天',
          requireAuth: true,  // 需要登录才能使用
          roles: ['common']
        }
      },
      //充值界面
      {
        path: 'Recharge',
        name: 'Recharge',
        component: () => import('@/views/blog/views/Recharge.vue'),
        meta: { 
          title: '充值',
          requireAuth: true,  // 需要登录才能充值
          roles: ['common']
        }
      },
      //充值记录
      {
        path: '/blog/recharge/orders',
        name: 'RechargeOrders',
        component: () => import('@/views/blog/components/RechargeOrders.vue'),
        meta: {
          title: '充值记录',
          requireAuth: true,
          roles: ['common']
        }
      },
      // 文章详情页（独立页面，不在主框架内）
      {
        path: 'article/:id',
        name: 'BlogArticleDetail',
        component: () => import('@/views/blog/components/ArticleDetail.vue'),
        meta: { 
          title: '文章详情',
          roles: ['common']
        },
        hidden: true  // 不在菜单中显示
      },
      //b站视频下载框
      {
        path: '/blog/bilibili',
        name: 'BilibiliDownloader',
        component: () => import('@/views/blog/views/BilibiliDownloader.vue'),
        meta: { 
          title: 'B站视频下载',
          requireAuth: true,  // 需要登录才能使用
          roles: ['common']
        }
      },
      //一键跑路
        {
          path: '/blog/runaway',
          name: 'RunAway',
          component: () => import('@/views/blog/views/RuAway.vue'),
          meta: { 
            title: '一键跑路',
            requireAuth: true,  // 需要登录才能使用
            roles: ['common']
          }
        },
        //api详情页面
        {
          path: '/blog/api',
          name: 'ApiDocumentation',
          component: () => import('@/views/blog/views/ApiDoc.vue'),
          meta: { 
            title: 'API文档',
            requireAuth: true,  // 需要登录才能查看
            roles: ['common']
          }
        },
        //商品
        {
          path:'/blog/PointsProduct',
          name:'Purchase',
          component: () => import('@/views/blog/views/Purchase.vue'),
          meta: {
            title: '商品',
            requireAuth: true,  // 需要登录才能购买
            roles: ['common']
          }
        },

      {
        path: '',
        redirect: '/blog/home'
      }
    ]
  },
  
  
  // ==================== 独立的文章详情路由（可分享链接） ====================
  {
    path: '/article/:id',
    name: 'ArticleShare',
    component: () => import('@/views/blog/components/ArticleDetail.vue'),
    meta: { 
      title: '文章详情',
      roles: ['common']
    },
    hidden: true
  },

    {
    path: '/my/profile',  // ✅ 改成不冲突的路径
    component: () => import('@/views/blog/views/User.vue'),
    name: 'UserProfile',
    meta: { 
        title: '个人主页',
        requireAuth: true
    }
    }
]




export default blogRouter