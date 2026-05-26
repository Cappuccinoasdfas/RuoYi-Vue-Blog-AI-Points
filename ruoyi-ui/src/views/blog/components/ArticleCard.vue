<template>
  <div class="article-card" @click="$emit('click')">
    <!-- 作者信息栏 -->
    <div class="card-header">
      <div class="author-info">
        <el-avatar 
          :size="40" 
          :src="article.authorAvatar"
          @error="handleAvatarError"
        >
          {{ (article.authorName && article.authorName.charAt(0)) || 'U' }}
        </el-avatar>
        
        <div class="author-meta">
          <div class="author-name">{{ article.authorName || '匿名用户' }}</div>
          <div class="publish-time">{{ formatTime(article.createTime) }}</div>
        </div>
      </div>
      
      <!-- 分类标签 -->
      <el-tag 
        v-if="article.categoryName" 
        size="small" 
        :type="getCategoryType(article.category)"
      >
        {{ article.categoryName }}
      </el-tag>
    </div>
    
    <!-- 文章主体内容 -->
    <div class="card-body">
      <h3 class="article-title">{{ article.title }}</h3>
      
      <!-- 文章摘要 -->
      <p v-if="article.summary" class="article-summary">{{ article.summary }}</p>
      
      <!-- 封面图（如果有） -->
      <div v-if="article.cover" class="article-cover">
        <el-image 
          :src="article.cover" 
          fit="cover"
          lazy
          :preview-src-list="[article.cover]"
        >
          <div slot="placeholder" class="image-placeholder">
            <i class="el-icon-loading"></i>
          </div>
        </el-image>
      </div>
    </div>
    
    <!-- 互动数据栏 -->
    <div class="card-footer">
      <div class="stats">
        <span class="stat-item" title="浏览量">
          <i class="el-icon-view"></i>
          <span>{{ formatNumber(article.viewCount || 0) }}</span>
        </span>
        
        <span 
          class="stat-item like-btn" 
          :class="{ 'liked': article.isLiked }"
          title="点赞"
          @click.stop="handleLike"
        >
          <i :class="article.isLiked ? 'el-icon-star-on' : 'el-icon-star-off'"></i>
          <span>{{ formatNumber(article.likeCount || 0) }}</span>
        </span>
        
        <span class="stat-item" title="评论数">
          <i class="el-icon-chat-dot-round"></i>
          <span>{{ formatNumber(article.commentCount || 0) }}</span>
        </span>
      </div>
      
      <!-- 更多操作 -->
      <el-dropdown trigger="click" @command="handleCommand" @click.native.stop>
        <span class="more-btn">
          <i class="el-icon-more"></i>
        </span>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item command="share">分享</el-dropdown-item>
          <el-dropdown-item command="report">举报</el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ArticleCard',
  props: {
    article: {
      type: Object,
      required: true,
      default: () => ({})
    }
  },
  methods: {
    /**
     * 格式化时间
     * @param {String|Date} time - 时间
     * @returns {String} 格式化后的时间字符串
     */
    formatTime(time) {
      if (!time) return '未知时间'
      
      const date = new Date(time)
      const now = new Date()
      const diff = now - date
      
      // 小于1分钟
      if (diff < 60000) {
        return '刚刚'
      }
      // 小于1小时
      if (diff < 3600000) {
        return Math.floor(diff / 60000) + '分钟前'
      }
      // 小于24小时
      if (diff < 86400000) {
        return Math.floor(diff / 3600000) + '小时前'
      }
      // 小于7天
      if (diff < 604800000) {
        return Math.floor(diff / 86400000) + '天前'
      }
      
      // 超过7天显示具体日期
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      
      if (year === now.getFullYear()) {
        return `${month}-${day}`
      }
      return `${year}-${month}-${day}`
    },
    
    /**
     * 格式化数字（超过999显示k）
     * @param {Number} num - 数字
     * @returns {String} 格式化后的字符串
     */
    formatNumber(num) {
      if (num >= 10000) {
        return (num / 10000).toFixed(1) + 'w'
      }
      if (num >= 1000) {
        return (num / 1000).toFixed(1) + 'k'
      }
      return String(num)
    },
    
    /**
     * 获取分类标签类型
     * @param {String} category - 分类代码
     * @returns {String} Element UI 标签类型
     */
    getCategoryType(category) {
      const typeMap = {
        'frontend': 'primary',
        'backend': 'success',
        'life': 'warning',
        'ai': 'danger'
      }
      return typeMap[category] || 'info'
    },
    
    /**
     * 头像加载失败处理
     */
    handleAvatarError(e) {
      // 使用默认头像
      e.target.src = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
    },
    
    /**
     * 处理点赞
     */
    handleLike() {
      this.$emit('like', this.article)
    },
    
    /**
     * 处理下拉菜单命令
     * @param {String} command - 命令名称
     */
    handleCommand(command) {
      if (command === 'share') {
        this.$message.info('分享功能开发中...')
      } else if (command === 'report') {
        this.$message.info('举报功能开发中...')
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.article-card {
  background: #ffffff;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  transition: all 0.3s;
  cursor: pointer;
  
  &:hover {
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
    transform: translateY(-2px);
  }
  
  .card-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 16px;
    
    .author-info {
      display: flex;
      align-items: center;
      
      .author-meta {
        margin-left: 12px;
        
        .author-name {
          font-size: 15px;
          font-weight: 500;
          color: #1a1a1a;
          margin-bottom: 4px;
        }
        
        .publish-time {
          font-size: 12px;
          color: #8c8c8c;
        }
      }
    }
  }
  
  .card-body {
    .article-title {
      font-size: 18px;
      font-weight: 600;
      color: #1a1a1a;
      line-height: 1.4;
      margin-bottom: 12px;
      
      &:hover {
        color: #1890ff;
      }
    }
    
    .article-summary {
      font-size: 14px;
      color: #595959;
      line-height: 1.6;
      margin-bottom: 12px;
      display: -webkit-box;
      -webkit-line-clamp: 3;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }
    
    .article-cover {
      margin-top: 12px;
      border-radius: 8px;
      overflow: hidden;
      max-height: 200px;
      
      ::v-deep .el-image {
        width: 100%;
        height: 200px;
        
        .image-placeholder {
          width: 100%;
          height: 100%;
          background: #f5f5f5;
          display: flex;
          align-items: center;
          justify-content: center;
          color: #bfbfbf;
        }
      }
    }
  }
  
  .card-footer {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-top: 16px;
    padding-top: 12px;
    border-top: 1px solid #f0f0f0;
    
    .stats {
      display: flex;
      align-items: center;
      gap: 20px;
      
      .stat-item {
        display: flex;
        align-items: center;
        gap: 4px;
        color: #8c8c8c;
        font-size: 14px;
        
        i {
          font-size: 16px;
        }
        
        &.like-btn {
          cursor: pointer;
          transition: color 0.3s;
          
          &:hover {
            color: #ff4d4f;
          }
          
          &.liked {
            color: #ff4d4f;
          }
        }
      }
    }
    
    .more-btn {
      padding: 4px 8px;
      color: #8c8c8c;
      cursor: pointer;
      border-radius: 4px;
      
      &:hover {
        background: #f5f5f5;
        color: #1a1a1a;
      }
    }
  }
}

/* 移动端适配 */
@media (max-width: 768px) {
  .article-card {
    padding: 16px;
    
    .card-header {
      .author-info {
        .author-meta {
          .author-name {
            font-size: 14px;
          }
        }
      }
    }
    
    .card-body {
      .article-title {
        font-size: 16px;
      }
    }
    
    .card-footer {
      .stats {
        gap: 16px;
      }
    }
  }
}
</style>