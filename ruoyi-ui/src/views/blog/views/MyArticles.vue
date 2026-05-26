<template>
  <div class="my-articles-page">
    <!-- 页面标题和操作 -->
    <div class="page-header glass-header">
      <h2>我的文章</h2>
      <el-button 
        type="primary" 
        icon="el-icon-edit"
        class="glass-btn"
        @click="$router.push('/blog/publish')"
      >
        写文章
      </el-button>
    </div>
    
    <!-- 标签页切换 -->
    <el-tabs v-model="activeTab" @tab-click="handleTabClick" class="glass-tabs">
      <el-tab-pane label="已发布" name="published" />
      <el-tab-pane label="草稿箱" name="draft" />
    </el-tabs>
    
    <!-- 文章列表 -->
    <div 
      v-loading="loading"
      class="article-list"
    >
      <!-- 文章卡片 -->
      <div 
        v-for="article in articleList" 
        :key="article.id"
        class="article-item glass-card"
      >
        <!-- 封面缩略图 -->
        <div v-if="article.cover" class="article-cover">
          <img :src="article.cover" alt="封面" />
        </div>
        
        <!-- 文章信息 -->
        <div class="article-info">
          <div class="info-header">
            <h3 class="article-title">
              <span @click="viewArticle(article)">{{ article.title }}</span>
              <el-tag 
                v-if="article.status === '1'" 
                type="info" 
                size="small"
                class="draft-tag glass-tag"
              >
                草稿
              </el-tag>
            </h3>
            
            <div class="article-meta">
              <span class="meta-item">
                <i class="el-icon-time"></i>
                {{ formatTime(article.createTime) }}
              </span>
              <span class="meta-item">
                <i class="el-icon-view"></i>
                {{ article.viewCount || 0 }} 阅读
              </span>
              <span class="meta-item">
                <i class="el-icon-star-off"></i>
                {{ article.likeCount || 0 }} 点赞
              </span>
              <span class="meta-item">
                <i class="el-icon-chat-dot-round"></i>
                {{ article.commentCount || 0 }} 评论
              </span>
            </div>
          </div>
          
          <!-- 操作按钮 -->
          <div class="article-actions">
            <el-button 
              type="text" 
              icon="el-icon-edit"
              class="action-btn"
              @click="editArticle(article)"
            >
              编辑
            </el-button>
            
            <el-dropdown trigger="click" @command="handleCommand($event, article)">
              <el-button type="text" icon="el-icon-more" class="action-btn">
                更多
              </el-button>
              
              <el-dropdown-menu slot="dropdown" class="glass-dropdown">
                <el-dropdown-item 
                  v-if="article.status === '1'"
                  command="publish"
                >
                  发布
                </el-dropdown-item>
                <el-dropdown-item 
                  v-if="article.status === '0'"
                  command="draft"
                >
                  转为草稿
                </el-dropdown-item>
                <el-dropdown-item command="delete" divided>
                  <span style="color: #ff4d4f;">删除</span>
                </el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>
        </div>
      </div>
      
      <!-- 空状态 -->
      <el-empty 
        v-if="!loading && articleList.length === 0" 
        :description="emptyText"
        class="glass-empty"
      >
        <el-button type="primary" class="glass-btn" @click="$router.push('/blog/publish')">
          去写文章
        </el-button>
      </el-empty>
      
      <!-- 分页 -->
      <div v-if="total > 0" class="pagination-wrapper">
        <el-pagination
          background
          layout="prev, pager, next"
          :total="total"
          :page-size="pageSize"
          :current-page.sync="currentPage"
          @current-change="handlePageChange"
          class="glass-pagination"
        />
      </div>
    </div>
  </div>
</template>

<script>
import { getMyArticles, deleteArticle, updateArticle } from '../api/article'

export default {
  name: 'MyArticles',
  data() {
    return {
      activeTab: 'published',
      articleList: [],
      loading: false,
      currentPage: 1,
      pageSize: 10,
      total: 0
    }
  },
  computed: {
    emptyText() {
      return this.activeTab === 'published' 
        ? '还没有发布过文章，快去写一篇吧~' 
        : '草稿箱是空的'
    }
  },
  mounted() {
    this.loadArticles()
  },
  methods: {
    async loadArticles() {
      this.loading = true
      
      try {
        const params = {
          pageNum: this.currentPage,
          pageSize: this.pageSize,
          status: this.activeTab === 'published' ? '0' : '1'
        }
        
        const res = await getMyArticles(params)
        
        if (res.code === 0 || res.code === 200) {
          this.articleList = res.data || res.rows || []
          this.total = res.total || 0
        } else {
          this.$message.error(res.msg || '加载失败')
        }
      } catch (error) {
        console.error('加载文章失败:', error)
        this.$message.error('加载失败，请稍后重试')
      } finally {
        this.loading = false
      }
    },
    
    formatTime(time) {
      if (!time) return ''
      
      const date = new Date(time)
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      
      return `${year}-${month}-${day}`
    },
    
    handleTabClick() {
      this.currentPage = 1
      this.loadArticles()
    },
    
    handlePageChange(page) {
      this.currentPage = page
      this.loadArticles()
    },
    
    viewArticle(article) {
      if (article.status === '0') {
        this.$router.push(`/blog/article/${article.id}`)
      } else {
        this.$router.push(`/blog/publish?id=${article.id}`)
      }
    },
    
    editArticle(article) {
      this.$router.push(`/blog/publish?id=${article.id}`)
    },
    
    async handleCommand(command, article) {
      switch (command) {
        case 'publish':
          await this.changeStatus(article, '0')
          break
        case 'draft':
          await this.changeStatus(article, '1')
          break
        case 'delete':
          await this.handleDelete(article)
          break
      }
    },
    
    async changeStatus(article, status) {
      try {
        const res = await updateArticle({
          id: article.id,
          status: status
        })
        
        if (res.code === 0 || res.code === 200) {
          this.$message.success(status === '0' ? '发布成功' : '已转为草稿')
          this.loadArticles()
        } else {
          this.$message.error(res.msg || '操作失败')
        }
      } catch (error) {
        console.error('修改状态失败:', error)
        this.$message.error('操作失败，请稍后重试')
      }
    },
    
    async handleDelete(article) {
      try {
        await this.$confirm('确定要删除这篇文章吗？删除后无法恢复。', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        const res = await deleteArticle(article.id)
        
        if (res.code === 0 || res.code === 200) {
          this.$message.success('删除成功')
          this.loadArticles()
        } else {
          this.$message.error(res.msg || '删除失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除失败:', error)
          this.$message.error('删除失败，请稍后重试')
        }
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.my-articles-page {
  max-width: 900px;
  margin: 0 auto;
  background: transparent;
  
  // 玻璃态页面标题
  .glass-header {
    background: rgba(255, 255, 255, 0.12);
    backdrop-filter: blur(20px) saturate(180%);
    -webkit-backdrop-filter: blur(20px) saturate(180%);
    border: 1px solid rgba(255, 255, 255, 0.15);
    border-radius: 16px;
    padding: 16px 24px;
  }
  
  .page-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 24px;
    
    h2 {
      font-size: 24px;
      font-weight: 600;
      color: #fff;
      margin: 0;
      text-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    }
  }
  
  // 玻璃态按钮
  .glass-btn {
    background: rgba(79, 70, 229, 0.8) !important;
    backdrop-filter: blur(10px);
    -webkit-backdrop-filter: blur(10px);
    border: 1px solid rgba(255, 255, 255, 0.2) !important;
    color: #fff !important;
    box-shadow: 0 4px 12px rgba(79, 70, 229, 0.3);
    
    &:hover {
      background: rgba(67, 56, 202, 0.9) !important;
    }
  }
  
  // 玻璃态标签页
  ::v-deep .glass-tabs {
    .el-tabs__header {
      margin-bottom: 20px;
    }
    
    .el-tabs__nav-wrap::after {
      background-color: rgba(255, 255, 255, 0.15);
    }
    
    .el-tabs__item {
      color: rgba(255, 255, 255, 0.7);
      font-weight: 500;
      
      &:hover {
        color: #fff;
      }
      
      &.is-active {
        color: #fff;
        font-weight: 600;
        text-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
      }
    }
    
    .el-tabs__active-bar {
      background-color: #fff;
      box-shadow: 0 2px 8px rgba(255, 255, 255, 0.3);
    }
  }
  
  .article-list {
    min-height: 400px;
    
    // 玻璃态文章卡片
    .glass-card {
      background: rgba(255, 255, 255, 0.12);
      backdrop-filter: blur(20px) saturate(180%);
      -webkit-backdrop-filter: blur(20px) saturate(180%);
      border: 1px solid rgba(255, 255, 255, 0.15);
      border-radius: 16px;
      transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
      
      &:hover {
        background: rgba(255, 255, 255, 0.18);
        border-color: rgba(255, 255, 255, 0.25);
        transform: translateY(-2px);
        box-shadow: 0 12px 24px rgba(0, 0, 0, 0.12);
      }
    }
    
    .article-item {
      display: flex;
      padding: 20px;
      margin-bottom: 16px;
      
      .article-cover {
        width: 120px;
        height: 80px;
        border-radius: 8px;
        overflow: hidden;
        margin-right: 20px;
        flex-shrink: 0;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
        
        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }
      }
      
      .article-info {
        flex: 1;
        display: flex;
        align-items: flex-start;
        justify-content: space-between;
        
        .info-header {
          flex: 1;
          
          .article-title {
            display: flex;
            align-items: center;
            gap: 8px;
            font-size: 18px;
            font-weight: 600;
            color: #fff;
            margin: 0 0 12px 0;
            text-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
            
            span {
              cursor: pointer;
              
              &:hover {
                color: rgba(255, 255, 255, 0.9);
                text-decoration: underline;
                text-underline-offset: 4px;
              }
            }
            
            .glass-tag {
              background: rgba(255, 255, 255, 0.15) !important;
              backdrop-filter: blur(10px);
              -webkit-backdrop-filter: blur(10px);
              border: 1px solid rgba(255, 255, 255, 0.2) !important;
              color: rgba(255, 255, 255, 0.9) !important;
            }
          }
          
          .article-meta {
            display: flex;
            flex-wrap: wrap;
            gap: 16px;
            
            .meta-item {
              display: flex;
              align-items: center;
              gap: 4px;
              font-size: 13px;
              color: rgba(255, 255, 255, 0.7);
              text-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
              
              i {
                font-size: 14px;
              }
            }
          }
        }
        
        .article-actions {
          display: flex;
          gap: 8px;
          flex-shrink: 0;
          margin-left: 20px;
          
          .action-btn {
            color: rgba(255, 255, 255, 0.8) !important;
            
            &:hover {
              color: #fff !important;
            }
          }
        }
      }
    }
    
    // 玻璃态空状态
    ::v-deep .glass-empty {
      background: rgba(255, 255, 255, 0.1);
      backdrop-filter: blur(20px);
      -webkit-backdrop-filter: blur(20px);
      border-radius: 24px;
      padding: 60px 20px;
      border: 1px solid rgba(255, 255, 255, 0.15);
      
      .el-empty__description {
        color: rgba(255, 255, 255, 0.8);
      }
    }
    
    .pagination-wrapper {
      display: flex;
      justify-content: center;
      margin-top: 32px;
      
      ::v-deep .glass-pagination {
        .el-pagination {
          padding: 12px 20px;
          background: rgba(255, 255, 255, 0.1);
          backdrop-filter: blur(20px);
          -webkit-backdrop-filter: blur(20px);
          border-radius: 40px;
          border: 1px solid rgba(255, 255, 255, 0.15);
        }
        
        .el-pager li {
          background: transparent !important;
          color: rgba(255, 255, 255, 0.8) !important;
          
          &:hover {
            color: #fff !important;
          }
          
          &.active {
            background: rgba(79, 70, 229, 0.8) !important;
            color: #fff !important;
            border-radius: 6px;
          }
        }
        
        .btn-prev,
        .btn-next {
          background: transparent !important;
          color: rgba(255, 255, 255, 0.8) !important;
          
          &:hover {
            color: #fff !important;
          }
          
          &:disabled {
            color: rgba(255, 255, 255, 0.3) !important;
          }
        }
      }
    }
  }
}

// 玻璃态下拉菜单
::v-deep .glass-dropdown {
  background: rgba(255, 255, 255, 0.15) !important;
  backdrop-filter: blur(20px) saturate(180%);
  -webkit-backdrop-filter: blur(20px) saturate(180%);
  border: 1px solid rgba(255, 255, 255, 0.2) !important;
  border-radius: 12px !important;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15) !important;
  
  .el-dropdown-menu__item {
    color: rgba(255, 255, 255, 0.9) !important;
    
    &:hover {
      background: rgba(255, 255, 255, 0.15) !important;
      color: #fff !important;
    }
    
    &.el-dropdown-menu__item--divided {
      border-top-color: rgba(255, 255, 255, 0.15) !important;
    }
  }
  
  .popper__arrow {
    border-bottom-color: rgba(255, 255, 255, 0.2) !important;
    
    &::after {
      border-bottom-color: rgba(255, 255, 255, 0.15) !important;
    }
  }
}

/* 移动端适配 */
@media (max-width: 768px) {
  .my-articles-page {
    .article-list {
      .article-item {
        .article-cover {
          display: none;
        }
        
        .article-info {
          flex-direction: column;
          
          .article-actions {
            margin-left: 0;
            margin-top: 12px;
            align-self: flex-end;
          }
        }
      }
    }
  }
}
</style>