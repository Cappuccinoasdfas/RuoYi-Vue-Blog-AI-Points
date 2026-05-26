<!-- BlogIndex.vue -->
<template>
  <div class="blog-container">
    <!-- ✅ 全局固定背景层 -->
    <div class="global-background">
      <div class="blur-bg" :style="{ backgroundImage: `url(${globalBgImage})` }"></div>
      <div class="bg-overlay"></div>
    </div>

    <!-- ✅ 移动端遮罩层 - 点击关闭侧边栏 -->
    <transition name="mask-fade">
      <div 
        v-if="isMobile && !isCollapsed" 
        class="sidebar-mask"
        @click="toggleSidebar"
        @touchmove.prevent
      ></div>
    </transition>

    <!-- 左侧伸缩栏 -->
    <div class="blog-sidebar" :class="{ 'sidebar-collapsed': isCollapsed }">
      <UserSidebar 
        :is-collapsed="isCollapsed" 
        @toggle-sidebar="toggleSidebar"
        @menu-change="handleMenuChange"
      />
    </div>
    
    <!-- 右侧内容区 -->
    <div class="blog-content">
      <!-- 顶部导航栏 -->
      <div class="content-header">
        <div class="header-left">
          <!-- 移动端菜单按钮 -->
          <div class="mobile-menu-btn" @click="toggleSidebar">
            <i class="el-icon-s-fold" v-if="!isCollapsed"></i>
            <i class="el-icon-s-unfold" v-else></i>
          </div>
          <h2>{{ currentPageTitle }}</h2>
        </div>
        
        <!-- 用户操作区域 -->
        <div class="header-right">
          <!-- 通知图标 -->
          <div class="notification-icon" v-if="!isMobile">
            <i class="el-icon-bell"></i>
          </div>
          
          <!-- 用户头像下拉菜单 -->
          <el-dropdown @command="handleUserCommand" trigger="click">
            <div class="user-avatar-wrapper">
              <el-avatar 
                :size="36" 
                :src="userAvatar"
                @error="handleAvatarError"
              >
                {{ userInitial }}
              </el-avatar>
              <span class="user-name" v-if="!isMobile">{{ userName }}</span>
              <i class="el-icon-arrow-down" v-if="!isMobile"></i>
            </div>
            
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="profile">
                <i class="el-icon-user"></i>
                个人主页
              </el-dropdown-item>
              <el-dropdown-item command="my-articles">
                <i class="el-icon-document"></i>
                我的文章
              </el-dropdown-item>
              <el-dropdown-item divided command="logout">
                <i class="el-icon-switch-button"></i>
                退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </div>
      
      <!-- 页面内容区 - 动态加载子页面 -->
      <div class="content-body" @click="handleContentClick">
        <transition name="fade" mode="out-in">
          <router-view :key="$route.fullPath"></router-view>
        </transition>
      </div>
    </div>

    <!-- ✅ 全局回到顶部按钮 -->
    <el-backtop target=".content-body" :visibility-height="300" class="global-backtop">
      <i class="el-icon-caret-top"></i>
    </el-backtop>
  </div>
</template>

<script>
import UserSidebar from './components/UserSidebar.vue'
import { mapGetters } from 'vuex'
import globalBgImage from './img/imga.png'

export default {
  name: 'BlogIndex',
  components: {
    UserSidebar
  },
  data() {
    return {
      isCollapsed: false,
      currentPageTitle: '首页',
      isMobile: false,
      // ✅ 全局背景图
      globalBgImage: globalBgImage
    }
  },
  computed: {
    ...mapGetters(['name', 'avatar']),
    
    userAvatar() {
      const baseUrl = process.env.VUE_APP_BASE_API || ''
      const avatar = this.$store.state.user.avatar
      if (!avatar) return ''
      return avatar.startsWith('http') ? avatar : baseUrl + avatar
    },
    
    userName() {
      return this.$store.state.user.name || this.$store.state.user.nickName || '用户'
    },
    
    userInitial() {
      const name = this.userName
      return name ? name.charAt(0).toUpperCase() : 'U'
    }
  },
  watch: {
    '$route'(to) {
      this.updatePageTitle(to.name)
      // ✅ 移动端路由跳转后自动收起侧边栏
      if (this.isMobile && !this.isCollapsed) {
        this.isCollapsed = true
      }
    }
  },
  mounted() {
    this.updatePageTitle(this.$route.name)
    this.checkMobile()
    window.addEventListener('resize', this.checkMobile)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.checkMobile)
  },
  methods: {
    toggleSidebar() {
      this.isCollapsed = !this.isCollapsed
      // ✅ 侧边栏展开时禁止 body 滚动
      if (this.isMobile) {
        document.body.style.overflow = this.isCollapsed ? '' : 'hidden'
      }
    },
    
    handleContentClick() {
      // ✅ 点击内容区时自动收起侧边栏（移动端）
      if (this.isMobile && !this.isCollapsed) {
        this.isCollapsed = true
        document.body.style.overflow = ''
      }
    },
    
    handleMenuChange(menuKey) {
      const routes = {
        'home': '/blog/home',
        'my-articles': '/blog/my-articles',
        'publish': '/blog/publish',
        'AiChatBox': '/blog/AiChatBox',
        'Recharge': '/blog/Recharge',
        'bilibili': '/blog/bilibili',
        'runaway': '/blog/runaway'
      }
      
      if (routes[menuKey]) {
        this.$router.push(routes[menuKey])
      }
      
      // ✅ 移动端选择菜单后自动收起侧边栏
      if (this.isMobile) {
        this.isCollapsed = true
        document.body.style.overflow = ''
      }
    },
    
    updatePageTitle(routeName) {
      const titles = {
        'BlogHome': '首页',
        'BlogMyArticles': '我的文章',
        'BlogPublish': '发布',
        'BlogArticleDetail': '文章详情',
        'BlogAiChatBox': 'AI聊天',
        'BlogRecharge': '充值积分',
        'BlogBilibili': 'B站下载',
        'BlogRunaway': '一键跑路'
      }
      this.currentPageTitle = titles[routeName] || '博客'
    },
    
    checkMobile() {
      this.isMobile = window.innerWidth <= 768
      // ✅ 移动端默认收起侧边栏
      if (this.isMobile) {
        this.isCollapsed = true
        document.body.style.overflow = ''
      } else {
        // 桌面端默认展开
        this.isCollapsed = false
        document.body.style.overflow = ''
      }
    },
    
    handleAvatarError(e) {
      e.target.src = ''
    },
    
    handleUserCommand(command) {
      switch (command) {
        case 'profile':
          this.$router.push('/my/profile')
          break
          
        case 'my-articles':
          this.$router.push('/blog/my-articles')
          break
          
        case 'logout':
          this.handleLogout()
          break
      }
    },
    
    async handleLogout() {
      try {
        await this.$confirm('确定要退出登录吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        await this.$store.dispatch('LogOut')
        this.$message.success('退出成功')
        
        setTimeout(() => {
          location.href = '/index'
        }, 500)
      } catch (error) {
        // 取消退出
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.blog-container {
  display: flex;
  height: 100vh;
  width: 100vw;
  overflow: hidden;
  position: relative;
  
  // ✅ 全局背景层
  .global-background {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    z-index: 0;
    
    .blur-bg {
      width: 100%;
      height: 100%;
      background-size: cover;
      background-position: center;
      background-repeat: no-repeat;
      background-attachment: fixed;
      filter: blur(8px) brightness(0.7);
      transform: scale(1.1);
    }
    
    .bg-overlay {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: linear-gradient(135deg, 
        rgba(0, 0, 0, 0.15) 0%, 
        rgba(0, 0, 0, 0.08) 100%);
      backdrop-filter: blur(2px);
    }
  }
  
  // ✅ 移动端遮罩层
  .sidebar-mask {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.5);
    backdrop-filter: blur(4px);
    -webkit-backdrop-filter: blur(4px);
    z-index: 999;
    cursor: pointer;
  }
  
  .blog-sidebar {
    width: 260px;
    height: 100%;
    position: relative;
    z-index: 1000;  // ✅ 高于遮罩层
    background: transparent;
    box-shadow: 2px 0 8px rgba(0, 0, 0, 0.04);
    transition: width 0.3s ease, transform 0.3s ease;
    flex-shrink: 0;
    
    &.sidebar-collapsed {
      width: 80px;
    }
  }
  
  .blog-content {
    flex: 1;
    height: 100%;
    display: flex;
    flex-direction: column;
    overflow: hidden;
    position: relative;
    z-index: 5;
    
    .content-header {
      height: 60px;
      padding: 0 24px;
      background: rgba(255, 255, 255, 0.15);
      backdrop-filter: blur(20px) saturate(180%);
      -webkit-backdrop-filter: blur(20px) saturate(180%);
      border-bottom: 1px solid rgba(255, 255, 255, 0.15);
      display: flex;
      align-items: center;
      justify-content: space-between;
      flex-shrink: 0;
      
      .header-left {
        display: flex;
        align-items: center;
        
        .mobile-menu-btn {
          display: none;
          margin-right: 16px;
          font-size: 20px;
          cursor: pointer;
          color: rgba(255, 255, 255, 0.9);
          transition: color 0.2s;
          
          &:hover {
            color: #fff;
          }
          
          &:active {
            transform: scale(0.95);
          }
        }
        
        h2 {
          font-size: 20px;
          font-weight: 600;
          color: #fff;
          margin: 0;
          text-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        }
      }
      
      .header-right {
        display: flex;
        align-items: center;
        gap: 16px;
        
        .notification-icon {
          font-size: 20px;
          color: rgba(255, 255, 255, 0.9);
          cursor: pointer;
          transition: color 0.2s;
          
          &:hover {
            color: #fff;
          }
        }
        
        .user-avatar-wrapper {
          display: flex;
          align-items: center;
          cursor: pointer;
          
          .el-avatar {
            border: 2px solid rgba(255, 255, 255, 0.8);
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
          }
          
          .user-name {
            margin: 0 8px;
            font-size: 14px;
            color: #fff;
            max-width: 100px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
            text-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
          }
          
          i {
            color: rgba(255, 255, 255, 0.8);
            font-size: 12px;
          }
        }
      }
    }
    
    .content-body {
      flex: 1;
      overflow-y: auto;
      padding: 24px;
      background: transparent;
      
      &::-webkit-scrollbar {
        width: 6px;
      }
      
      &::-webkit-scrollbar-track {
        background: rgba(255, 255, 255, 0.1);
        border-radius: 3px;
      }
      
      &::-webkit-scrollbar-thumb {
        background: rgba(255, 255, 255, 0.3);
        border-radius: 3px;
        
        &:hover {
          background: rgba(255, 255, 255, 0.5);
        }
      }
    }
  }
}

// ✅ 遮罩层动画
.mask-fade-enter-active,
.mask-fade-leave-active {
  transition: opacity 0.3s ease;
}

.mask-fade-enter,
.mask-fade-leave-to {
  opacity: 0;
}

// ✅ 页面切换动画
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter,
.fade-leave-to {
  opacity: 0;
}

// ✅ 全局回到顶部按钮
::v-deep .global-backtop {
  .el-backtop {
    background: rgba(255, 255, 255, 0.2) !important;
    backdrop-filter: blur(10px);
    -webkit-backdrop-filter: blur(10px);
    border: 1px solid rgba(255, 255, 255, 0.3);
    color: #fff;
    z-index: 100;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    
    &:hover {
      background: rgba(255, 255, 255, 0.3) !important;
    }
  }
}

/* 移动端适配 */
@media (max-width: 768px) {
  .blog-container {
    .blog-sidebar {
      position: fixed;
      left: 0;
      top: 0;
      height: 100vh;
      z-index: 1000;
      box-shadow: 2px 0 20px rgba(0, 0, 0, 0.2);
      
      // ✅ 收起状态完全隐藏
      &.sidebar-collapsed {
        transform: translateX(-100%);
        width: 260px;  // 保持宽度不变，用 transform 隐藏
        box-shadow: none;
      }
    }
    
    .blog-content {
      .content-header {
        padding: 0 16px;
        
        .header-left {
          .mobile-menu-btn {
            display: block;
          }
        }
        
        .header-right {
          gap: 8px;
          
          .user-avatar-wrapper {
            .user-name,
            .el-icon-arrow-down {
              display: none;
            }
          }
        }
      }
      
      .content-body {
        padding: 16px;
      }
    }
  }
}

/* 平板适配 */
@media (min-width: 769px) and (max-width: 1024px) {
  .blog-container {
    .blog-sidebar {
      width: 200px;
      
      &.sidebar-collapsed {
        width: 70px;
      }
    }
    
    .blog-content {
      .content-header {
        padding: 0 20px;
      }
      
      .content-body {
        padding: 20px;
      }
    }
  }
}
</style>