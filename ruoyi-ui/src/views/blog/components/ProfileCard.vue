<template>
  <el-card class="profile-card" shadow="never">
    <!-- 头像区域 -->
    <div class="avatar-section">
      <div class="avatar-wrapper" @click="$emit('change-avatar')">
        <el-avatar 
          :size="100" 
          :src="userInfo.avatar ? baseUrl + userInfo.avatar : ''"
          @error="handleAvatarError"
        >
          <i class="el-icon-user-solid" style="font-size: 40px;"></i>
        </el-avatar>
        <div class="avatar-mask">
          <i class="el-icon-camera"></i>
          <span>更换头像</span>
        </div>
      </div>
      <h3 class="user-name">{{ userInfo.nickName || userInfo.userName || '未设置' }}</h3>
      <p class="user-role">{{ getUserRole() }}</p>
    </div>

    <!-- 统计数据 -->
    <div class="stats-section">
      <div class="stat-item">
        <span class="stat-value">{{ stats.articleCount || 0 }}</span>
        <span class="stat-label">文章</span>
      </div>
      <div class="stat-item">
        <span class="stat-value">{{ stats.followCount || 0 }}</span>
        <span class="stat-label">关注</span>
      </div>
      <div class="stat-item">
        <span class="stat-value">{{ stats.fansCount || 0 }}</span>
        <span class="stat-label">粉丝</span>
      </div>
    </div>

    <!-- 操作按钮 -->
    <div class="action-section">
      <el-button 
        type="primary" 
        icon="el-icon-edit"
        size="small"
        @click="$emit('edit')"
      >
        编辑资料
      </el-button>
    </div>
  </el-card>
</template>

<script>
export default {
  name: 'ProfileCard',
  props: {
    userInfo: {
      type: Object,
      default: () => ({})
    }
  },
  data() {
    return {
      baseUrl: process.env.VUE_APP_BASE_API || '',
      stats: {
        articleCount: 0,
        followCount: 0,
        fansCount: 0
      }
    }
  },
  mounted() {
    this.fetchUserStats()
  },
  methods: {
    /**
     * 获取用户角色名称
     */
    getUserRole() {
      const roles = this.$store.state.user.roles || []
      if (roles.includes('admin')) return '管理员'
      if (roles.includes('common')) return '普通用户'
      return '游客'
    },

    /**
     * 获取用户统计数据
     * TODO: 对接后端统计接口
     */
    async fetchUserStats() {
      try {
        // const res = await getUserStats()
        // this.stats = res.data
        this.stats = {
          articleCount: 12,
          followCount: 28,
          fansCount: 56
        }
      } catch (error) {
        console.error('获取统计数据失败:', error)
      }
    },

    /**
     * 头像加载失败处理
     */
    handleAvatarError(e) {
      e.target.src = ''
    }
  }
}
</script>

<style lang="scss" scoped>
.profile-card {
  text-align: center;
  
  .avatar-section {
    padding: 20px 0;
    
    .avatar-wrapper {
      position: relative;
      display: inline-block;
      cursor: pointer;
      
      .el-avatar {
        border: 3px solid #fff;
        box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
      }
      
      .avatar-mask {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        border-radius: 50%;
        background: rgba(0, 0, 0, 0.5);
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        color: #fff;
        opacity: 0;
        transition: opacity 0.3s;
        
        i {
          font-size: 24px;
          margin-bottom: 4px;
        }
        
        span {
          font-size: 12px;
        }
      }
      
      &:hover .avatar-mask {
        opacity: 1;
      }
    }
    
    .user-name {
      margin: 16px 0 8px;
      font-size: 20px;
      font-weight: 600;
      color: #1a1a1a;
    }
    
    .user-role {
      margin: 0;
      font-size: 14px;
      color: #8c8c8c;
    }
  }
  
  .stats-section {
    display: flex;
    justify-content: space-around;
    padding: 20px 0;
    border-top: 1px solid #f0f0f0;
    border-bottom: 1px solid #f0f0f0;
    
    .stat-item {
      display: flex;
      flex-direction: column;
      
      .stat-value {
        font-size: 22px;
        font-weight: 600;
        color: #1a1a1a;
      }
      
      .stat-label {
        font-size: 13px;
        color: #8c8c8c;
        margin-top: 4px;
      }
    }
  }
  
  .action-section {
    padding-top: 20px;
    
    .el-button {
      width: 100%;
    }
  }
}
</style>