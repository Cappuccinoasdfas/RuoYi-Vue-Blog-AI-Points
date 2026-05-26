<template>
  <div class="home-page">
    <!-- 回到顶部按钮 -->
    <el-backtop target=".content-body" :visibility-height="300" class="custom-backtop">
      <i class="el-icon-caret-top"></i>
    </el-backtop>

    <!-- 顶部功能栏 -->
    <div class="top-bar glass-card">
      <div class="search-wrapper">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索文章..."
          prefix-icon="el-icon-search"
          clearable
          size="medium"
          class="search-input"
          @keyup.enter="handleSearch"
        >
          <el-button slot="append" icon="el-icon-search" @click="handleSearch"></el-button>
        </el-input>
      </div>
      
      <!-- ✅ 分类标签 - 移动端可横向滚动 -->
      <div class="category-tabs-wrapper">
        <div class="category-tabs">
          <div 
            v-for="cat in categories" 
            :key="cat.id"
            class="category-item"
            :class="{ active: activeCategory === cat.id }"
            @click="handleCategoryChange(cat.id)"
          >
            <i :class="cat.icon"></i>
            <span>{{ cat.name }}</span>
          </div>
        </div>
      </div>

      <div class="view-options">
        <el-tooltip content="刷新" placement="bottom">
          <div class="option-btn" @click="refreshArticles">
            <i class="el-icon-refresh"></i>
          </div>
        </el-tooltip>
      </div>
    </div>

    <!-- 主内容区 - 左右布局 -->
    <div class="main-content">
      <!-- 左侧文章列表 -->
      <div class="articles-section">
        <div 
          v-loading="loading"
          element-loading-text="加载文章中..."
          class="article-feed"
        >
          <!-- 文章卡片列表 -->
          <transition-group name="article-list" appear>
            <ArticleCard 
              v-for="article in articleList" 
              :key="article.id"
              :article="article"
              class="glass-article"
              @click="handleArticleClick(article)"
              @like="handleLike(article)"
            />
          </transition-group>
          
          <!-- 加载更多提示 -->
          <div v-if="loading && articleList.length > 0" class="loading-more glass-card">
            <i class="el-icon-loading"></i>
            <span>加载更多文章...</span>
          </div>
          
          <!-- 没有更多数据 -->
          <div v-if="!hasMore && articleList.length > 0" class="no-more glass-card">
            <span>✨ 已经到底啦 ✨</span>
          </div>
          
          <!-- 空状态 -->
          <el-empty 
            v-if="!loading && articleList.length === 0" 
            description="暂无文章，快去发布一篇吧~"
            class="glass-empty"
          >
            <el-button type="primary" round @click="$router.push('/blog/publish')">
              <i class="el-icon-edit"></i> 去发布
            </el-button>
          </el-empty>
        </div>
      </div>

      <!-- 右侧音乐播放器 -->
      <div class="music-section">
        <MusicPlayer class="sticky-player" />
      </div>
    </div>
  </div>
</template>

<script>
import ArticleCard from '../components/ArticleCard.vue'
import MusicPlayer from '../components/MusicPlayer.vue'
import { listArticle, likeArticle } from '../api/article'

export default {
  name: 'BlogHome',
  components: {
    ArticleCard,
    MusicPlayer
  },
  data() {
    return {
      articleList: [],
      loading: false,
      currentPage: 1,
      pageSize: 5,
      total: 0,
      hasMore: true,
      scrollContainer: null,
      searchKeyword: '',
      activeCategory: 'all',
      categories: [
        { id: 'all', name: '全部', icon: 'el-icon-menu' },
        { id: 'tech', name: '技术', icon: 'el-icon-cpu' },
        { id: 'life', name: '生活', icon: 'el-icon-coffee-cup' },
        { id: 'notes', name: '笔记', icon: 'el-icon-notebook-2' },
        { id: 'other', name: '其他', icon: 'el-icon-more' }
      ]
    }
  },
  mounted() {
    this.loadArticles()
    this.$nextTick(() => {
      this.initScrollListener()
    })
  },
  beforeDestroy() {
    this.removeScrollListener()
  },
  methods: {
    /**
     * 初始化滚动监听
     */
    initScrollListener() {
      this.scrollContainer = document.querySelector('.content-body')
      
      if (this.scrollContainer) {
        this.scrollContainer.addEventListener('scroll', this.handleScroll)
      } else {
        window.addEventListener('scroll', this.handleScroll)
        this.scrollContainer = window
      }
    },
    
    /**
     * 移除滚动监听
     */
    removeScrollListener() {
      if (this.scrollContainer) {
        if (this.scrollContainer === window) {
          window.removeEventListener('scroll', this.handleScroll)
        } else {
          this.scrollContainer.removeEventListener('scroll', this.handleScroll)
        }
      }
    },
    
    /**
     * 处理滚动事件
     */
    handleScroll() {
      if (this.loading || !this.hasMore) return
      
      let scrollTop, scrollHeight, clientHeight
      
      if (this.scrollContainer === window) {
        scrollTop = document.documentElement.scrollTop || document.body.scrollTop
        scrollHeight = document.documentElement.scrollHeight || document.body.scrollHeight
        clientHeight = window.innerHeight
      } else {
        scrollTop = this.scrollContainer.scrollTop
        scrollHeight = this.scrollContainer.scrollHeight
        clientHeight = this.scrollContainer.clientHeight
      }
      
      if (scrollHeight - scrollTop - clientHeight < 100) {
        this.loadMore()
      }
    },
    
    /**
     * 加载文章列表
     */
    async loadArticles() {
      if (this.loading) return
      
      this.loading = true
      
      try {
        const params = {
          pageNum: this.currentPage,
          pageSize: this.pageSize,
          category: this.activeCategory !== 'all' ? this.activeCategory : null,
          keyword: this.searchKeyword || null
        }
        
        const res = await listArticle(params)
        
        if (res.code === 0 || res.code === 200) {
          const data = res.rows || res.data || []
          
          if (this.currentPage === 1) {
            this.articleList = data
          } else {
            this.articleList = [...this.articleList, ...data]
          }
          
          this.total = res.total || 0
          this.hasMore = this.articleList.length < this.total
        }
      } catch (error) {
        console.error('加载文章失败:', error)
        this.$message.error('加载文章失败，请稍后重试')
      } finally {
        this.loading = false
      }
    },
    
    /**
     * 加载更多
     */
    loadMore() {
      if (!this.hasMore || this.loading) return
      this.currentPage++
      this.loadArticles()
    },
    
    /**
     * 刷新文章
     */
    refreshArticles() {
      this.currentPage = 1
      this.articleList = []
      this.hasMore = true
      this.loadArticles()
      this.$message.success('刷新成功')
    },
    
    /**
     * 搜索
     */
    handleSearch() {
      this.refreshArticles()
    },
    
    /**
     * 切换分类
     */
    handleCategoryChange(categoryId) {
      if (this.activeCategory === categoryId) return
      this.activeCategory = categoryId
      this.refreshArticles()
    },
    
    /**
     * 点击文章卡片
     */
    async handleArticleClick(article) {
      try {
        this.$router.push({
          name: 'BlogArticleDetail',
          params: { id: article.id }
        })
      } catch (error) {
        console.error('跳转失败:', error)
      }
    },
    
    /**
     * 点赞文章
     */
    async handleLike(article) {
      try {
        const res = await likeArticle(article.id)
        
        if (res.code === 0 || res.code === 200) {
          article.likeCount = (article.likeCount || 0) + 1
          this.$message.success('点赞成功')
        } else {
          this.$message.warning(res.msg || '点赞失败')
        }
      } catch (error) {
        console.error('点赞失败:', error)
        this.$message.error('点赞失败，请稍后重试')
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.home-page {
  min-height: 100%;
  position: relative;
  padding: 20px;
  background: transparent;
  
  // 玻璃态卡片基类
  .glass-card {
    background: rgba(255, 255, 255, 0.15);
    backdrop-filter: blur(20px) saturate(180%);
    -webkit-backdrop-filter: blur(20px) saturate(180%);
    border: 1px solid rgba(255, 255, 255, 0.2);
    border-radius: 20px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1),
                inset 0 1px 0 rgba(255, 255, 255, 0.2);
  }
  
  // 顶部功能栏
  .top-bar {
    position: sticky;
    top: 20px;
    z-index: 10;
    padding: 16px 24px;
    margin-bottom: 24px;
    display: flex;
    align-items: center;
    gap: 20px;
    flex-wrap: wrap;
    
    .search-wrapper {
      flex: 1;
      min-width: 280px;
      
      ::v-deep .search-input {
        .el-input__inner {
          background: rgba(255, 255, 255, 0.1);
          border: 1px solid rgba(255, 255, 255, 0.2);
          color: #fff;
          border-radius: 30px 0 0 30px;
          backdrop-filter: blur(10px);
          
          &::placeholder {
            color: rgba(255, 255, 255, 0.7);
          }
          
          &:focus {
            border-color: rgba(255, 255, 255, 0.5);
            background: rgba(255, 255, 255, 0.15);
          }
        }
        
        .el-input-group__append {
          background: rgba(255, 255, 255, 0.2);
          border: 1px solid rgba(255, 255, 255, 0.2);
          border-left: none;
          border-radius: 0 30px 30px 0;
          
          .el-button {
            background: transparent;
            color: #fff;
            border: none;
            
            &:hover {
              color: #1890ff;
            }
          }
        }
      }
    }
    
    // ✅ 分类标签外层包装 - 用于移动端横向滚动
    .category-tabs-wrapper {
      overflow-x: auto;
      overflow-y: hidden;
      -webkit-overflow-scrolling: touch;
      
      // 隐藏滚动条但保留功能
      &::-webkit-scrollbar {
        display: none;
      }
      -ms-overflow-style: none;
      scrollbar-width: none;
    }
    
    .category-tabs {
      display: flex;
      gap: 8px;
      padding: 2px 0;
      
      .category-item {
        display: flex;
        align-items: center;
        gap: 6px;
        padding: 8px 16px;
        border-radius: 30px;
        color: rgba(255, 255, 255, 0.9);
        cursor: pointer;
        transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
        background: rgba(255, 255, 255, 0.05);
        border: 1px solid rgba(255, 255, 255, 0.1);
        font-size: 14px;
        white-space: nowrap;
        flex-shrink: 0;
        
        i {
          font-size: 16px;
        }
        
        &:hover {
          background: rgba(255, 255, 255, 0.15);
          transform: translateY(-2px);
          border-color: rgba(255, 255, 255, 0.3);
        }
        
        &.active {
          background: rgba(255, 255, 255, 0.25);
          color: #fff;
          border-color: rgba(255, 255, 255, 0.5);
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
        }
      }
    }
    
    .view-options {
      display: flex;
      gap: 8px;
      
      .option-btn {
        width: 40px;
        height: 40px;
        display: flex;
        align-items: center;
        justify-content: center;
        border-radius: 50%;
        background: rgba(255, 255, 255, 0.1);
        border: 1px solid rgba(255, 255, 255, 0.2);
        color: #fff;
        cursor: pointer;
        transition: all 0.3s;
        
        i {
          font-size: 18px;
        }
        
        &:hover {
          background: rgba(255, 255, 255, 0.2);
          transform: rotate(180deg);
          border-color: rgba(255, 255, 255, 0.4);
        }
      }
    }
  }
  
  // 主内容区
  .main-content {
    position: relative;
    z-index: 5;
    display: flex;
    gap: 24px;
    max-width: 1600px;
    margin: 0 auto;
    
    // 左侧文章区
    .articles-section {
      flex: 1;
      min-width: 0;
      
      .article-feed {
        display: flex;
        flex-direction: column;
        gap: 20px;
        
        .glass-article {
          background: rgba(255, 255, 255, 0.12);
          backdrop-filter: blur(20px) saturate(180%);
          -webkit-backdrop-filter: blur(20px) saturate(180%);
          border: 1px solid rgba(255, 255, 255, 0.15);
          border-radius: 24px;
          transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
          
          &:hover {
            background: rgba(255, 255, 255, 0.18);
            border-color: rgba(255, 255, 255, 0.3);
            transform: translateY(-4px);
            box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15),
                        inset 0 1px 0 rgba(255, 255, 255, 0.3);
          }
        }
        
        // 文章列表动画
        .article-list-enter-active {
          transition: all 0.6s cubic-bezier(0.4, 0, 0.2, 1);
        }
        
        .article-list-enter {
          opacity: 0;
          transform: translateY(30px);
        }
        
        .article-list-enter-to {
          opacity: 1;
          transform: translateY(0);
        }
        
        .loading-more,
        .no-more {
          text-align: center;
          padding: 20px;
          color: rgba(255, 255, 255, 0.9);
          
          i {
            margin-right: 8px;
            animation: rotate 1s linear infinite;
          }
        }
        
        .glass-empty {
          background: rgba(255, 255, 255, 0.1);
          backdrop-filter: blur(20px);
          border-radius: 24px;
          padding: 60px 20px;
          border: 1px solid rgba(255, 255, 255, 0.15);
          
          ::v-deep .el-empty__description {
            color: rgba(255, 255, 255, 0.8);
          }
        }
      }
    }
    
    // 右侧音乐区
    .music-section {
      width: 360px;
      flex-shrink: 0;
      
      .sticky-player {
        position: sticky;
        top: 100px;
      }
    }
  }
  
  // 自定义回到顶部按钮
  ::v-deep .custom-backtop {
    .el-backtop {
      background: rgba(255, 255, 255, 0.2);
      backdrop-filter: blur(10px);
      border: 1px solid rgba(255, 255, 255, 0.3);
      color: #fff;
      
      &:hover {
        background: rgba(255, 255, 255, 0.3);
      }
    }
  }
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

// ✅ 响应式设计 - 全面优化
@media (max-width: 1200px) {
  .home-page {
    .main-content {
      .music-section {
        width: 320px;
      }
    }
  }
}

@media (max-width: 968px) {
  .home-page {
    padding: 16px;
    
    .main-content {
      flex-direction: column;
      
      .music-section {
        width: 100%;
        order: -1;
        
        .sticky-player {
          position: relative;
          top: 0;
        }
      }
    }
    
    .top-bar {
      padding: 12px 16px;
      flex-wrap: nowrap;
      
      .search-wrapper {
        min-width: 200px;
        flex-shrink: 1;
      }
      
      .category-tabs-wrapper {
        flex-shrink: 1;
        min-width: 0;
      }
      
      .view-options {
        flex-shrink: 0;
      }
    }
  }
}

// ✅ 移动端适配 (768px 以下)
@media (max-width: 768px) {
  .home-page {
    padding: 12px;
    
    .top-bar {
      position: sticky;
      top: 12px;
      padding: 12px;
      flex-direction: column;
      gap: 12px;
      
      .search-wrapper {
        width: 100%;
        min-width: auto;
        
        ::v-deep .search-input {
          .el-input__inner {
            font-size: 14px;
            height: 40px;
            line-height: 40px;
          }
          
          .el-input-group__append {
            padding: 0 12px;
            
            .el-button {
              padding: 10px 12px;
            }
          }
        }
      }
      
      .category-tabs-wrapper {
        width: 100%;
        
        .category-tabs {
          .category-item {
            padding: 6px 14px;
            font-size: 13px;
            
            i {
              font-size: 14px;
            }
            
            span {
              // 超小屏时隐藏文字只显示图标
              @media (max-width: 480px) {
                display: none;
              }
            }
          }
        }
      }
      
      .view-options {
        width: 100%;
        justify-content: flex-end;
        
        .option-btn {
          width: 36px;
          height: 36px;
          
          i {
            font-size: 16px;
          }
        }
      }
    }
    
    .main-content {
      gap: 16px;
      
      .articles-section {
        .article-feed {
          gap: 16px;
          
          .glass-empty {
            padding: 40px 16px;
          }
        }
      }
    }
  }
}

// ✅ 超小屏适配 (480px 以下)
@media (max-width: 480px) {
  .home-page {
    padding: 8px;
    
    .top-bar {
      top: 8px;
      padding: 10px;
      border-radius: 16px;
      
      .category-tabs {
        gap: 6px;
        
        .category-item {
          padding: 6px 12px;
          
          i {
            margin-right: 0;
          }
        }
      }
    }
    
    .main-content {
      gap: 12px;
    }
  }
}
</style>