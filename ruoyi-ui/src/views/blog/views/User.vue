<template>
  <div class="user-profile-page">
    <!-- 顶部导航栏 -->
    <div class="profile-header">
      <div class="header-left">
        <div class="back-btn" @click="goBack">
          <i class="el-icon-arrow-left"></i>
          <span>返回</span>
        </div>
        <h2>个人主页</h2>
      </div>
      <div class="header-right">
        <el-button type="text" icon="el-icon-setting" @click="goToSettings">
          设置
        </el-button>
      </div>
    </div>

    <!-- 用户信息卡片 -->
    <div class="profile-banner">
      <div class="banner-bg"></div>
      <div class="user-info-wrapper">
        <div class="avatar-section" @click="handleChangeAvatar">
          <el-avatar 
            :size="80" 
            :src="userAvatar"
            @error="handleAvatarError"
          >
            {{ userInitial }}
          </el-avatar>
          <div class="avatar-mask">
            <i class="el-icon-camera"></i>
          </div>
        </div>
        <div class="user-detail">
          <h3 class="user-name">{{ userInfo.nickName || userInfo.userName || '未设置' }}</h3>
          <p class="user-bio">{{ userInfo.remark || '这个人很懒，什么都没写' }}</p>
        </div>
        <el-button 
          type="primary" 
          size="small" 
          plain
          class="edit-btn"
          @click="handleEditProfile"
        >
          编辑资料
        </el-button>
      </div>
    </div>

    <!-- 统计数据 -->
    <div class="stats-grid">
      <div class="stat-card" @click="goToMyArticles">
        <div class="stat-icon article">
          <i class="el-icon-document"></i>
        </div>
        <div class="stat-content">
          <span class="stat-value">{{ stats.articleCount }}</span>
          <span class="stat-label">文章</span>
        </div>
      </div>
      <div class="stat-card" @click="goToLikes">
        <div class="stat-icon like">
          <i class="el-icon-star-on"></i>
        </div>
        <div class="stat-content">
          <span class="stat-value">{{ stats.totalLikes }}</span>
          <span class="stat-label">获赞</span>
        </div>
      </div>
      <div class="stat-card" @click="goToViews">
        <div class="stat-icon view">
          <i class="el-icon-view"></i>
        </div>
        <div class="stat-content">
          <span class="stat-value">{{ formatNumber(stats.totalViews) }}</span>
          <span class="stat-label">浏览</span>
        </div>
      </div>
    </div>

    <!-- 功能菜单 -->
    <div class="menu-section">
      <div class="menu-group">
        <div class="menu-item" @click="handleChangePassword">
          <div class="item-left">
            <i class="el-icon-lock"></i>
            <span>修改密码</span>
          </div>
          <i class="el-icon-arrow-right"></i>
        </div>
        <div class="menu-item" @click="goToMyArticles">
          <div class="item-left">
            <i class="el-icon-document-copy"></i>
            <span>我的文章</span>
          </div>
          <i class="el-icon-arrow-right"></i>
        </div>
        <div class="menu-item" @click="goToDrafts">
          <div class="item-left">
            <i class="el-icon-edit-outline"></i>
            <span>草稿箱</span>
          </div>
          <span class="item-badge">{{ stats.draftCount }}</span>
          <i class="el-icon-arrow-right"></i>
        </div>
      </div>

      <div class="menu-group">
        <div class="menu-item" @click="goToCollection">
          <div class="item-left">
            <i class="el-icon-star-off"></i>
            <span>我的收藏</span>
          </div>
          <i class="el-icon-arrow-right"></i>
        </div>
        <div class="menu-item" @click="goToHistory">
          <div class="item-left">
            <i class="el-icon-time"></i>
            <span>浏览历史</span>
          </div>
          <i class="el-icon-arrow-right"></i>
        </div>
      </div>

      <div class="menu-group">
        <div class="menu-item" @click="goToAbout">
          <div class="item-left">
            <i class="el-icon-info"></i>
            <span>关于</span>
          </div>
          <i class="el-icon-arrow-right"></i>
        </div>
        <div class="menu-item logout-item" @click="handleLogout">
          <div class="item-left">
            <i class="el-icon-switch-button"></i>
            <span>退出登录</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 编辑资料弹窗 -->
    <EditProfile ref="editProfile" :user-info="userInfo" @success="handleProfileSuccess" />
    
    <!-- 头像上传弹窗 -->
    <AvatarUpload ref="avatarUpload" @success="handleAvatarSuccess" />
    
    <!-- 修改密码弹窗 -->
    <ChangePassword ref="changePassword" />
  </div>
</template>

<script>
import EditProfile from '../components/EditProfile.vue'
import AvatarUpload from '../components/AvatarUpload.vue'
import ChangePassword from '../components/ChangePassword.vue'
import { getUserProfile } from '../api/user'
import { getStatistics } from '../api/article'

export default {
  name: 'UserProfile',
  components: {
    EditProfile,
    AvatarUpload,
    ChangePassword
  },
  data() {
    return {
      userInfo: {},
      stats: {
        articleCount: 0,
        draftCount: 0,
        totalLikes: 0,
        totalViews: 0
      }
    }
  },
  computed: {
    userAvatar() {
      const baseUrl = process.env.VUE_APP_BASE_API || ''
      const avatar = this.userInfo.avatar
      if (!avatar) return ''
      return avatar.startsWith('http') ? avatar : baseUrl + avatar
    },
    userInitial() {
      const name = this.userInfo.nickName || this.userInfo.userName || 'U'
      return name.charAt(0).toUpperCase()
    }
  },
  mounted() {
    this.loadUserInfo()
    this.loadUserStats()
  },
  methods: {
    /**
     * 加载用户信息
     */
    async loadUserInfo() {
      try {
        const res = await getUserProfile()
        if (res.code === 0 || res.code === 200) {
          this.userInfo = res.data || {}
        }
      } catch (error) {
        console.error('获取用户信息失败:', error)
      }
    },

    /**
     * 加载用户统计数据
     */
    async loadUserStats() {
      try {
         const res = await getStatistics()

        // TODO: 对接统计接口
        this.stats = {
          articleCount: res.data.count,        // 对应后端的 count
          draftCount: res.data.draftCount,
          totalLikes: res.data.likeCount,       // 对应后端的 likeCount
          totalViews: res.data.viewCount       // 对应后端的 viewCount
        }
      } catch (error) {
        console.error('获取统计数据失败:', error)
      }
    },

    /**
     * 格式化数字
     */
    formatNumber(num) {
      if (num >= 10000) return (num / 10000).toFixed(1) + 'w'
      if (num >= 1000) return (num / 1000).toFixed(1) + 'k'
      return String(num)
    },

    /**
     * 返回上一页
     */
    goBack() {
      if (window.history.length > 1) {
        this.$router.go(-1)
      } else {
        this.$router.push('/blog/home')
      }
    },

    /**
     * 头像加载失败
     */
    handleAvatarError(e) {
      e.target.src = ''
    },

    /**
     * 更换头像
     */
    handleChangeAvatar() {
      this.$refs.avatarUpload.open()
    },

    /**
     * 头像更新成功
     */
    handleAvatarSuccess(avatar) {
      this.userInfo.avatar = avatar
    },

    /**
     * 编辑资料
     */
    handleEditProfile() {
      this.$refs.editProfile.open()
    },

    /**
     * 资料更新成功
     */
    handleProfileSuccess(data) {
      this.userInfo = { ...this.userInfo, ...data }
    },

    /**
     * 修改密码
     */
    handleChangePassword() {
      this.$refs.changePassword.open()
    },

    /**
     * 跳转到我的文章
     */
    goToMyArticles() {
      this.$router.push('/blog/my-articles')
    },

    /**
     * 跳转到草稿箱
     */
    goToDrafts() {
      this.$router.push('/blog/my-articles?tab=draft')
    },

    /**
     * 跳转到获赞页面
     */
    goToLikes() {
      this.$message.info('功能开发中...')
    },

    /**
     * 跳转到浏览记录
     */
    goToViews() {
      this.$message.info('功能开发中...')
    },

    /**
     * 跳转到收藏
     */
    goToCollection() {
      this.$message.info('我的收藏功能开发中...')
    },

    /**
     * 跳转到历史
     */
    goToHistory() {
      this.$message.info('浏览历史功能开发中...')
    },

    /**
     * 跳转到设置
     */
    goToSettings() {
      this.$message.info('设置功能开发中...')
    },

    /**
     * 关于页面
     */
    goToAbout() {
      this.$message.info('博客系统 v1.0.0')
    },

    /**
     * 退出登录
     */
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
.user-profile-page {
  min-height: 100vh;
  background: #f5f7fa;
  
  .profile-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 16px 20px;
    background: #ffffff;
    border-bottom: 1px solid #f0f0f0;
    
    .header-left {
      display: flex;
      align-items: center;
      
      .back-btn {
        display: flex;
        align-items: center;
        margin-right: 16px;
        color: #595959;
        cursor: pointer;
        
        i {
          font-size: 20px;
          margin-right: 4px;
        }
        
        &:hover {
          color: #1890ff;
        }
      }
      
      h2 {
        margin: 0;
        font-size: 18px;
        font-weight: 600;
        color: #1a1a1a;
      }
    }
  }
  
  .profile-banner {
    position: relative;
    background: #ffffff;
    margin-bottom: 16px;
    
    .banner-bg {
      height: 100px;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    }
    
    .user-info-wrapper {
      display: flex;
      align-items: flex-end;
      padding: 0 20px 20px;
      margin-top: -40px;
      
      .avatar-section {
        position: relative;
        cursor: pointer;
        
        .el-avatar {
          border: 4px solid #ffffff;
          box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
        }
        
        .avatar-mask {
          position: absolute;
          bottom: 0;
          right: 0;
          width: 28px;
          height: 28px;
          background: #ffffff;
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;
          box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
          
          i {
            font-size: 14px;
            color: #1890ff;
          }
        }
      }
      
      .user-detail {
        flex: 1;
        margin-left: 16px;
        
        .user-name {
          margin: 0 0 4px;
          font-size: 20px;
          font-weight: 600;
          color: #1a1a1a;
        }
        
        .user-bio {
          margin: 0;
          font-size: 13px;
          color: #8c8c8c;
        }
      }
      
      .edit-btn {
        flex-shrink: 0;
      }
    }
  }
  
  .stats-grid {
    display: flex;
    padding: 16px;
    gap: 12px;
    background: #ffffff;
    margin-bottom: 16px;
    
    .stat-card {
      flex: 1;
      display: flex;
      align-items: center;
      padding: 12px;
      background: #fafafa;
      border-radius: 12px;
      cursor: pointer;
      transition: all 0.3s;
      
      &:hover {
        background: #f0f0f0;
      }
      
      .stat-icon {
        width: 40px;
        height: 40px;
        border-radius: 10px;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 10px;
        
        i {
          font-size: 20px;
        }
        
        &.article {
          background: #e6f7ff;
          color: #1890ff;
        }
        
        &.like {
          background: #fff1f0;
          color: #ff4d4f;
        }
        
        &.view {
          background: #f6ffed;
          color: #52c41a;
        }
      }
      
      .stat-content {
        display: flex;
        flex-direction: column;
        
        .stat-value {
          font-size: 18px;
          font-weight: 600;
          color: #1a1a1a;
        }
        
        .stat-label {
          font-size: 12px;
          color: #8c8c8c;
        }
      }
    }
  }
  
  .menu-section {
    padding: 0 16px;
    
    .menu-group {
      background: #ffffff;
      border-radius: 12px;
      margin-bottom: 16px;
      overflow: hidden;
      
      .menu-item {
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 16px;
        border-bottom: 1px solid #f5f5f5;
        cursor: pointer;
        transition: background 0.3s;
        
        &:last-child {
          border-bottom: none;
        }
        
        &:hover {
          background: #fafafa;
        }
        
        .item-left {
          display: flex;
          align-items: center;
          
          i {
            width: 24px;
            font-size: 20px;
            color: #595959;
            margin-right: 12px;
          }
          
          span {
            font-size: 15px;
            color: #1a1a1a;
          }
        }
        
        .item-badge {
          background: #ff4d4f;
          color: #ffffff;
          font-size: 12px;
          padding: 2px 8px;
          border-radius: 10px;
          margin-right: 8px;
        }
        
        i:last-child {
          color: #bfbfbf;
          font-size: 14px;
        }
        
        &.logout-item {
          .item-left {
            i, span {
              color: #ff4d4f;
            }
          }
        }
      }
    }
  }
}

/* 移动端适配 */
@media (max-width: 768px) {
  .user-profile-page {
    .profile-banner {
      .banner-bg {
        height: 80px;
      }
      
      .user-info-wrapper {
        margin-top: -30px;
        
        .avatar-section {
          .el-avatar {
            width: 60px !important;
            height: 60px !important;
          }
        }
        
        .user-detail {
          .user-name {
            font-size: 18px;
          }
        }
      }
    }
    
    .stats-grid {
      padding: 12px;
      
      .stat-card {
        padding: 8px;
        
        .stat-icon {
          width: 36px;
          height: 36px;
          margin-right: 6px;
        }
      }
    }
  }
}
</style>