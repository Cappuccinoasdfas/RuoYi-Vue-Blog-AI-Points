<!-- UserSidebar.vue -->
<template>
  <div class="user-sidebar">
    <!-- 用户信息区域 -->
    <div class="user-profile" :class="{ 'collapsed': isCollapsed }">
      <div class="avatar-wrapper">
        <el-avatar 
          :size="isCollapsed ? 48 : 64" 
          :src="userInfo.avatar"
          @error="handleAvatarError"
        >
          {{ (userInfo.nickName && userInfo.nickName.charAt(0)) || 'U' }}
        </el-avatar>
      </div>
      
      <transition name="fade">
        <div v-if="!isCollapsed" class="user-info">
          <div class="user-name">{{ userInfo.nickName || '游客' }}</div>
          <div class="user-bio">{{ userInfo.bio || '这个人很懒，什么都没写' }}</div>
          
          <!-- 统计数据 -->
          <div class="user-stats">
            <div class="stat-item">
              <span class="stat-value">{{ stats.articleCount }}</span>
              <span class="stat-label">文章</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">{{ stats.totalViews }}</span>
              <span class="stat-label">浏览</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">{{ stats.totalLikes }}</span>
              <span class="stat-label">点赞</span>
            </div>
          </div>
        </div>
      </transition>
    </div>
    
    <!-- 菜单栏 -->
    <div class="sidebar-menu">
      <div 
        v-for="item in menuItems" 
        :key="item.key"
        class="menu-item"
        :class="{ 
          'active': activeMenu === item.key,
          'collapsed': isCollapsed 
        }"
        @click="handleMenuClick(item.key)"
      >
        <i :class="item.icon"></i>
        <span v-if="!isCollapsed" class="menu-label">{{ item.label }}</span>
      </div>
    </div>
    
    <!-- 底部操作按钮 -->
    <div class="sidebar-footer">
      <div 
        class="collapse-btn"
        @click="$emit('toggle-sidebar')"
      >
        <i :class="isCollapsed ? 'el-icon-s-unfold' : 'el-icon-s-fold'"></i>
        <span v-if="!isCollapsed">收起侧边栏</span>
      </div>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { getStatistics } from '../api/article'

export default {
  name: 'UserSidebar',
  props: {
    isCollapsed: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      activeMenu: 'home',
      menuItems: [
        { key: 'home', label: '首页', icon: 'el-icon-s-home' },
        { key: 'my-articles', label: '我的文章', icon: 'el-icon-document' },
        { key: 'publish', label: '发布', icon: 'el-icon-edit-outline' },
        { key: 'AiChatBox', label: 'ai聊天', icon: 'el-icon-chat-dot-round' },
        { key: 'Recharge', label: '充值积分', icon: 'el-icon-money' },
        {key:'bilibili', label:'b站视频下载', icon:'el-icon-video-camera-solid'},
        {key:'runaway', label:'一键跑路', icon:'el-icon-s-promotion'},
        {key:'ApiDocumentation', label:'接口文档', icon:'el-icon-document-checked'},
        {key:'Purchase', label:'商品', icon:'el-icon-shopping-cart-full'}
      ],
      stats: {
        articleCount: 0,
        totalViews: 0,
        totalLikes: 0
      }
    }
  },
  computed: {
    ...mapGetters(['name', 'avatar']),
    userInfo() {
      return {
        nickName: this.$store.state.user.name || '用户',
        avatar: this.$store.state.user.avatar || '',
        bio: '前端开发爱好者'
      }
    }
  },
  mounted() {
    this.fetchUserStats()
    this.setActiveMenuByRoute()
  },
  watch: {
    '$route'() {
      this.setActiveMenuByRoute()
    }
  },
  methods: {
    handleMenuClick(key) {
      this.activeMenu = key
      
      const routeMap = {
        'home': '/blog/home',
        'my-articles': '/blog/my-articles',
        'publish': '/blog/publish',
        'AiChatBox': '/blog/AiChatBox',
        'Recharge':'/blog/Recharge',
        'bilibili':'/blog/bilibili',
        'runaway':'/blog/runaway',
        'ApiDocumentation':'/blog/api',
        'Purchase':'/blog/PointsProduct'
      }
      
      const targetRoute = routeMap[key]
      if (targetRoute) {
        this.$router.push(targetRoute).catch(err => {
          if (err.name !== 'NavigationDuplicated') {
            console.error('路由跳转失败:', err)
          }
        })
      }
      
      this.$emit('menu-change', key)
    },
    
    setActiveMenuByRoute() {
      const path = this.$route.path
      if (path.includes('/blog/home')) {
        this.activeMenu = 'home'
      } else if (path.includes('/blog/my-articles')) {
        this.activeMenu = 'my-articles'
      } else if (path.includes('/blog/publish')) {
        this.activeMenu = 'publish'
      } else if (path.includes('/blog/AiChatBox')) {
        this.activeMenu = 'AiChatBox'
      }
    },
    
    handleAvatarError() {
      console.log('头像加载失败')
    },
    
    async fetchUserStats() {
      try {
        const res = await getStatistics()
        this.stats = {
          articleCount: res.data.count,
          totalViews: res.data.viewCount,
          totalLikes: res.data.likeCount
        }
      } catch (error) {
        console.error('获取用户统计失败:', error)
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.user-sidebar {
  height: 100%;
  display: flex;
  flex-direction: column;
  
  // ✅ 玻璃态半透明背景 - 关键修改
  background: rgba(255, 255, 255, 0.12);
  backdrop-filter: blur(20px) saturate(180%);
  -webkit-backdrop-filter: blur(20px) saturate(180%);
  border-right: 1px solid rgba(255, 255, 255, 0.15);
  box-shadow: 2px 0 30px rgba(0, 0, 0, 0.08);

  .user-profile {
    padding: 32px 20px 24px;
    text-align: center;
    border-bottom: 1px solid rgba(255, 255, 255, 0.12);
    transition: padding 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    position: relative;
    
    &::after {
      content: '';
      position: absolute;
      bottom: -1px;
      left: 10%;
      width: 80%;
      height: 1px;
      background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
    }

    &.collapsed {
      padding: 24px 12px;
    }

    .avatar-wrapper {
      margin-bottom: 16px;
      position: relative;
      display: inline-block;

      &::before {
        content: '';
        position: absolute;
        top: -4px;
        left: -4px;
        right: -4px;
        bottom: -4px;
        border-radius: 50%;
        background: conic-gradient(from 0deg, #fff, #69c0ff, #bae7ff, #fff);
        opacity: 0;
        transition: opacity 0.4s;
        z-index: 0;
      }

      &:hover::before {
        opacity: 0.3;
        animation: rotate-glow 3s linear infinite;
      }

      ::v-deep .el-avatar {
        border: 3px solid rgba(255, 255, 255, 0.8);
        box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
        transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
        z-index: 1;
        position: relative;
        
        &:hover {
          transform: scale(1.02);
          box-shadow: 0 6px 20px rgba(0, 0, 0, 0.2);
        }
      }
    }

    .user-info {
      .user-name {
        font-size: 18px;
        font-weight: 650;
        color: #fff;
        margin-bottom: 6px;
        letter-spacing: 0.3px;
        text-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
      }

      .user-bio {
        font-size: 13px;
        color: rgba(255, 255, 255, 0.85);
        margin-bottom: 20px;
        line-height: 1.5;
        padding: 0 8px;
        font-weight: 400;
      }

      .user-stats {
        display: flex;
        justify-content: space-around;
        background: rgba(255, 255, 255, 0.08);
        padding: 12px 4px;
        border-radius: 24px;
        backdrop-filter: blur(4px);
        border: 1px solid rgba(255, 255, 255, 0.12);

        .stat-item {
          display: flex;
          flex-direction: column;
          position: relative;

          .stat-value {
            font-size: 20px;
            font-weight: 700;
            color: #fff;
            line-height: 1.3;
            transition: transform 0.2s;
            text-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
          }

          .stat-label {
            font-size: 12px;
            color: rgba(255, 255, 255, 0.75);
            margin-top: 6px;
            font-weight: 450;
            letter-spacing: 0.3px;
            text-transform: uppercase;
            font-size: 11px;
          }

          &:hover .stat-value {
            transform: translateY(-2px);
          }

          &:not(:last-child)::after {
            content: '';
            position: absolute;
            right: -18px;
            top: 20%;
            height: 60%;
            width: 1px;
            background: linear-gradient(to bottom, transparent, rgba(255, 255, 255, 0.3), transparent);
          }
        }
      }
    }
  }

  .sidebar-menu {
    flex: 1;
    padding: 20px 12px;
    overflow-y: auto;

    .menu-item {
      display: flex;
      align-items: center;
      padding: 12px 18px;
      margin-bottom: 6px;
      border-radius: 14px;
      cursor: pointer;
      transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
      color: rgba(255, 255, 255, 0.8);
      position: relative;
      overflow: hidden;

      &::before {
        content: '';
        position: absolute;
        left: 0;
        top: 0;
        bottom: 0;
        width: 4px;
        background: #fff;
        border-radius: 0 4px 4px 0;
        transform: translateX(-100%);
        transition: transform 0.25s;
      }

      i {
        font-size: 22px;
        min-width: 28px;
        transition: all 0.25s;
        filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.1));
      }

      .menu-label {
        margin-left: 14px;
        font-size: 15px;
        font-weight: 500;
        letter-spacing: 0.2px;
        text-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
      }

      &:hover {
        background: rgba(255, 255, 255, 0.12);
        color: #fff;
        transform: translateX(2px);

        i {
          transform: scale(1.05);
        }

        &::before {
          transform: translateX(0);
        }
      }

      &.active {
        background: rgba(255, 255, 255, 0.18);
        color: #fff;
        box-shadow: inset 0 0 0 1px rgba(255, 255, 255, 0.15);

        i {
          filter: drop-shadow(0 2px 6px rgba(0, 0, 0, 0.15));
        }

        .menu-label {
          font-weight: 600;
        }

        &::before {
          transform: translateX(0);
          background: #fff;
          box-shadow: 0 0 12px rgba(255, 255, 255, 0.5);
        }
      }

      &.collapsed {
        justify-content: center;
        padding: 14px;
        border-radius: 16px;

        i {
          font-size: 24px;
        }

        &:hover {
          transform: translateX(0) scale(1.05);
        }
      }
    }
  }

  .sidebar-footer {
    padding: 20px 16px;
    border-top: 1px solid rgba(255, 255, 255, 0.12);
    background: rgba(255, 255, 255, 0.03);
    backdrop-filter: blur(8px);

    .collapse-btn {
      display: flex;
      align-items: center;
      padding: 12px 16px;
      border-radius: 14px;
      cursor: pointer;
      color: rgba(255, 255, 255, 0.8);
      transition: all 0.25s;
      background: rgba(255, 255, 255, 0.05);
      border: 1px solid rgba(255, 255, 255, 0.08);

      i {
        font-size: 20px;
        min-width: 28px;
        transition: transform 0.3s;
        filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.1));
      }

      span {
        margin-left: 14px;
        font-size: 14px;
        font-weight: 500;
        text-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
      }

      &:hover {
        background: rgba(255, 255, 255, 0.12);
        color: #fff;
        border-color: rgba(255, 255, 255, 0.2);

        i {
          transform: translateX(-2px);
        }
      }

      &:active {
        transform: scale(0.98);
      }
    }
  }
}

@keyframes rotate-glow {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.fade-enter,
.fade-leave-to {
  opacity: 0;
}

// 滚动条美化 - 白色半透明
.sidebar-menu::-webkit-scrollbar {
  width: 4px;
}

.sidebar-menu::-webkit-scrollbar-track {
  background: transparent;
}

.sidebar-menu::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.2);
  border-radius: 4px;
  
  &:hover {
    background: rgba(255, 255, 255, 0.35);
  }
}
</style>