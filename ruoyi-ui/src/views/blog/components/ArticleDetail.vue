<template>
  <div class="article-detail" v-loading="loading">
    <!-- 返回按钮 -->
    <div class="back-btn glass-back" @click="$router.go(-1)">
      <i class="el-icon-arrow-left"></i>
      <span>返回</span>
    </div>
    
    <div v-if="article.id" class="article-container glass-container">
      <!-- 文章标题 -->
      <h1 class="article-title">{{ article.title }}</h1>
      
      <!-- 作者信息栏 -->
      <div class="author-bar">
        <div class="author-info">
          <el-avatar 
            :size="48" 
            :src="article.authorAvatar"
            @error="handleAvatarError"
            class="glass-avatar"
          >
            {{ (article.authorName && article.authorName.charAt(0)) || 'U' }}
          </el-avatar>
          
          <div class="author-meta">
            <div class="author-name">{{ article.authorName || '匿名用户' }}</div>
            <div class="publish-info">
              <span>{{ formatDate(article.createTime) }}</span>
              <span class="divider">·</span>
              <span>阅读 {{ formatNumber(article.viewCount || 0) }}</span>
            </div>
          </div>
        </div>
        
        <!-- 关注按钮 -->
        <el-button 
          type="primary" 
          size="small" 
          plain
          class="glass-follow-btn"
          @click="handleFollow"
        >
          {{ isFollowed ? '已关注' : '+ 关注' }}
        </el-button>
      </div>
      
      <!-- 文章内容 -->
      <div class="article-content">
        <!-- 封面图 -->
        <div v-if="article.cover" class="cover-image glass-cover">
          <el-image 
            :src="article.cover" 
            fit="cover"
            :preview-src-list="[article.cover]"
          />
        </div>
        
        <!-- 正文内容（Markdown渲染） -->
        <div 
          class="content-body markdown-body"
          v-html="renderedContent"
        ></div>
        
        <!-- 分类标签 -->
        <div v-if="article.categoryName" class="article-tags">
          <el-tag :type="getCategoryType(article.category)" class="glass-tag">
            {{ article.categoryName }}
          </el-tag>
        </div>
      </div>
      
      <!-- 互动栏 -->
      <div class="action-bar">
        <div class="action-left">
          <div 
            class="action-btn like-btn" 
            :class="{ 'active': article.isLiked }"
            @click="handleLike"
          >
            <i :class="article.isLiked ? 'el-icon-star-on' : 'el-icon-star-off'"></i>
            <span>点赞 {{ formatNumber(article.likeCount || 0) }}</span>
          </div>
          
          <div class="action-btn" @click="scrollToComment">
            <i class="el-icon-chat-dot-round"></i>
            <span>评论 {{ formatNumber(article.totalCommentCount || 0) }}</span>
          </div>
        </div>
        
        <div class="action-right">
          <el-button 
            type="text" 
            icon="el-icon-share"
            class="action-text-btn"
            @click="handleShare"
          >
            分享
          </el-button>
          <el-button 
            v-if="isAuthor"
            type="text" 
            icon="el-icon-edit"
            class="action-text-btn"
            @click="handleEdit"
          >
            编辑
          </el-button>
        </div>
      </div>
    </div>
    
    <!-- 评论区域 -->
    <div v-if="article.id" class="comment-section glass-section" ref="commentSection">
      <div class="comment-header">
        <h3>评论 ({{ article.totalCommentCount || 0 }})</h3>
      </div>
      
      <!-- 评论输入框 -->
      <div class="comment-input">
        <el-input
          v-model="commentContent"
          type="textarea"
          :rows="3"
          placeholder="写下你的评论..."
          maxlength="500"
          show-word-limit
          class="glass-textarea"
          @keyup.ctrl.enter.native="submitComment"
        ></el-input>
        
        <div class="input-footer">
          <span class="tip">Ctrl + Enter 快捷发送</span>
          <el-button 
            type="primary" 
            size="small"
            :loading="submitting"
            class="glass-submit-btn"
            @click="submitComment"
          >
            发表评论
          </el-button>
        </div>
      </div>
      
      <!-- 评论列表 -->
      <div v-loading="commentLoading" class="comment-list">
        <el-empty 
          v-if="comments.length === 0 && !commentLoading" 
          description="暂无评论，快来抢沙发吧~"
          :image-size="120"
          class="glass-empty"
        />
        
        <div v-else>
          <CommentItem 
            v-for="comment in comments" 
            :key="comment.id"
            :comment="comment"
            :article-id="article.id"
            @delete="handleDeleteComment"
            @delete-child="handleDeleteChildComment"
            @reply="handleAddChildReply"
            @like="handleCommentLike"
            @show-reply-input="handleShowReplyInput"
            @close-reply-input="handleCloseReplyInput"
            @add-child-reply="handleAddChildReplyFromChild"
            @refresh-comments="loadComments"
            @toggle-children="handleToggleChildren"
          />
          
          <!-- 分页 -->
          <div v-if="commentTotal > 10" class="comment-pagination">
            <el-pagination
              background
              layout="prev, pager, next"
              :total="commentTotal"
              :page-size="10"
              :current-page="commentPage"
              @current-change="handlePageChange"
              class="glass-pagination"
            />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getArticleDetail, likeArticle } from '../api/article'
import MarkdownIt from 'markdown-it'
import hljs from 'highlight.js'
import 'highlight.js/styles/github.css'
import CommentItem from './CommentItem.vue'
import { getComments, addComment, getChildComments } from '../api/comment'

const md = new MarkdownIt({
  html: true,
  linkify: true,
  typographer: true,
  breaks: true,
  highlight: function(str, lang) {
    if (lang && hljs.getLanguage(lang)) {
      try {
        return '<pre class="hljs"><code>' +
               hljs.highlight(str, { language: lang, ignoreIllegals: true }).value +
               '</code></pre>';
      } catch (__) {}
    }
    return '<pre class="hljs"><code>' + md.utils.escapeHtml(str) + '</code></pre>';
  }
})

export default {
  name: 'ArticleDetail',
  components: {
    CommentItem
  },
  data() {
    return {
      article: {},
      loading: false,
      isFollowed: false,
      comments: [],
      commentPage: 1,
      commentTotal: 0,
      commentLoading: false,
      commentContent: '',
      submitting: false,
      activeReplyId: null
    }
  },
  computed: {
    isAuthor() {
      const userId = this.$store.state.user?.id
      return userId && userId === this.article.userId
    },
    renderedContent() {
      if (!this.article.content) return ''
      return md.render(this.article.content)
    }
  },
  mounted() {
    this.loadArticle()
  },
  watch: {
    'article.id': {
      handler(newVal) {
        if (newVal) {
          this.commentPage = 1
          this.loadComments()
        }
      },
      immediate: true
    }
  },
  methods: {
    async loadArticle() {
      const articleId = this.$route.params.id
      if (!articleId) {
        this.$router.push('/blog/home')
        return
      }
      
      this.loading = true
      
      try {
        const res = await getArticleDetail(articleId)
        if (res.code === 0 || res.code === 200) {
          this.article = res.data || {}
          document.title = `${this.article.title || '文章'} - 博客`
        }
      } catch (error) {
        console.error('加载文章失败:', error)
      } finally {
        this.loading = false
      }
    },
    
    async loadComments() {
      if (!this.article.id) return
      this.commentLoading = true
      try {
        const res = await getComments(this.article.id, {
          pageNum: this.commentPage,
          pageSize: 10
        })
        if (res.code === 200) {
          this.comments = (res.data || []).map(comment => ({
            ...comment,
            _showChildren: false,
            _childrenList: []
          }))
          this.commentTotal = res.total || 0
          this.article.commentCount = this.commentTotal
          this.article.totalCommentCount = res.totalCommentCount || this.commentTotal
        }
      } catch (error) {
        console.error('加载评论失败:', error)
      } finally {
        this.commentLoading = false
      }
    },
    
    async submitComment() {
      if (!this.$store.state.user?.id) {
        this.$message.warning('请先登录')
        return
      }
      if (!this.commentContent.trim()) {
        this.$message.warning('请输入评论内容')
        return
      }
      
      this.submitting = true
      
      try {
        const res = await addComment({
          articleId: this.article.id,
          content: this.commentContent,
          parentId: 0
        })
        
        if (res.code === 200) {
          this.$message.success('评论成功')
          
          const newComment = {
            id: res.data?.id || Date.now(),
            content: this.commentContent,
            userName: this.$store.state.user?.name || this.$store.state.user?.username || '我',
            userAvatar: this.$store.state.user?.avatar || '',
            userId: this.$store.state.user?.id,
            createTime: new Date().toISOString(),
            likeCount: 0,
            isLiked: false,
            replyCount: 0,
            _childrenList: [],
            _showChildren: false
          }
          
          this.comments.unshift(newComment)
          this.commentContent = ''
          this.commentTotal++
          this.article.commentCount = this.commentTotal
          if (this.article.totalCommentCount !== undefined) {
            this.article.totalCommentCount++
          }
        } else {
          this.$message.error(res.msg || '评论失败')
        }
      } catch (error) {
        console.error('评论失败:', error)
        this.$message.error('评论失败')
      } finally {
        this.submitting = false
      }
    },
    
    handleDeleteComment(deleteInfo) {
      const { commentId } = deleteInfo
      
      const index = this.comments.findIndex(item => item.id === commentId)
      if (index > -1) {
        const deletedComment = this.comments[index]
        // 删除主评论时，要减去主评论本身和它的所有回复
        const totalDeleted = 1 + (deletedComment.replyCount || 0)
        
        this.comments.splice(index, 1)
        this.commentTotal -= totalDeleted
        this.article.commentCount = this.commentTotal
        if (this.article.totalCommentCount !== undefined) {
          this.article.totalCommentCount -= totalDeleted
        }
      }
    },
    
    handleDeleteChildComment(deleteInfo) {
      const { commentId, parentId } = deleteInfo
      
      console.log('删除子评论:', commentId, '父评论:', parentId)
      
      // 重新从后端获取最新的子评论数据
      this.refreshCommentAfterDelete(parentId)
    },
    
    async refreshCommentAfterDelete(parentId) {
      const parentComment = this.comments.find(item => item.id === parentId)
      if (!parentComment) return
      
      try {
        const res = await getChildComments(parentId, {
          pageNum: 1,
          pageSize: 5
        })
        
        if (res.code === 200) {
          // 更新子评论列表
          parentComment._childrenList = res.data || []
          parentComment._showChildren = false
          
          // 使用后端返回的准确回复数
          if (res.total !== undefined) {
            parentComment.replyCount = res.total
          } else if (res.data && res.data.length !== undefined) {
            parentComment.replyCount = res.data.length
          }
          
          // 重新获取总评论数
          await this.updateTotalCommentCount()
          
          // 强制视图更新
          this.$forceUpdate()
        }
      } catch (error) {
        console.error('刷新评论失败:', error)
      }
    },
    
    async handleToggleChildren({ commentId, isExpanded }) {
      // 如果是收起操作（isExpanded === false），重新加载该评论的子评论数据
      if (!isExpanded) {
        await this.refreshCommentChildren(commentId)
      }
    },
    
    async refreshCommentChildren(commentId) {
      const comment = this.comments.find(c => c.id === commentId)
      if (!comment) return
      
      try {
        const res = await getChildComments(commentId, {
          pageNum: 1,
          pageSize: 5
        })
        
        if (res.code === 200) {
          comment._childrenList = res.data || []
          comment._showChildren = false
          
          // 使用后端返回的准确回复数
          if (res.total !== undefined) {
            comment.replyCount = res.total
          } else if (res.data && res.data.length !== undefined) {
            comment.replyCount = res.data.length
          }
          
          // 重新获取总评论数
          await this.updateTotalCommentCount()
        }
      } catch (error) {
        console.error('刷新子评论失败:', error)
      }
    },
    
    async updateTotalCommentCount() {
      try {
        // 重新获取文章详情
        const res = await getArticleDetail(this.article.id)
        if (res.code === 200 || res.code === 0) {
          // 使用后端返回的准确数据
          this.article.totalCommentCount = res.data.totalCommentCount || 0
          this.article.commentCount = res.data.totalCommentCount || 0
          this.commentTotal = res.data.totalCommentCount || 0
        }
      } catch (error) {
        console.error('更新评论总数失败:', error)
      }
    },
    
    // 备用方法：本地重新计算总评论数
    recalculateTotalCommentCount() {
      let total = this.comments.length // 主评论数量
      
      // 加上所有子评论数量
      this.comments.forEach(comment => {
        total += comment.replyCount || 0
      })
      
      this.commentTotal = total
      this.article.commentCount = total
      this.article.totalCommentCount = total
    },
    
    handleAddChildReply({ parentId, comment: newReply }) {
      const parentComment = this.comments.find(item => item.id === parentId)
      if (!parentComment) return
      
      if (!parentComment._childrenList) {
        parentComment._childrenList = []
      }
      
      const exists = parentComment._childrenList.some(child => child.id === newReply.id)
      if (!exists) {
        parentComment._childrenList.unshift(newReply)
        parentComment.replyCount = (parentComment.replyCount || 0) + 1
        parentComment._showChildren = true
        
        // 总评论数 +1
        this.commentTotal++
        this.article.commentCount = this.commentTotal
        if (this.article.totalCommentCount !== undefined) {
          this.article.totalCommentCount++
        }
      }
    },
    
    handleAddChildReplyFromChild({ parentId, reply }) {
      const parentComment = this.comments.find(item => item.id === parentId)
      if (!parentComment) return
      
      if (!parentComment._childrenList) {
        parentComment._childrenList = []
      }
      
      const exists = parentComment._childrenList.some(child => child.id === reply.id)
      if (!exists) {
        parentComment._childrenList.unshift(reply)
        parentComment.replyCount = (parentComment.replyCount || 0) + 1
        parentComment._showChildren = true
        
        this.commentTotal++
        this.article.commentCount = this.commentTotal
        if (this.article.totalCommentCount !== undefined) {
          this.article.totalCommentCount++
        }
      }
    },
    
    handleCommentLike(commentId) {
      console.log('评论点赞:', commentId)
    },
    
    handleShowReplyInput(commentId) {
      if (this.activeReplyId && this.activeReplyId !== commentId) {
        const prevComment = this.findCommentById(this.activeReplyId)
        if (prevComment) {
          prevComment._showReplyInput = false
        }
      }
      this.activeReplyId = commentId
    },
    
    handleCloseReplyInput() {
      this.activeReplyId = null
    },
    
    findCommentById(commentId, list = this.comments) {
      for (let comment of list) {
        if (comment.id === commentId) {
          return comment
        }
        if (comment._childrenList) {
          const found = this.findCommentById(commentId, comment._childrenList)
          if (found) return found
        }
      }
      return null
    },
    
    handlePageChange(page) {
      this.commentPage = page
      this.loadComments()
      this.scrollToComment()
    },
    
    formatDate(date) {
      if (!date) return ''
      const d = new Date(date)
      const year = d.getFullYear()
      const month = String(d.getMonth() + 1).padStart(2, '0')
      const day = String(d.getDate()).padStart(2, '0')
      const hour = String(d.getHours()).padStart(2, '0')
      const minute = String(d.getMinutes()).padStart(2, '0')
      return `${year}-${month}-${day} ${hour}:${minute}`
    },
    
    formatNumber(num) {
      if (num >= 10000) return (num / 10000).toFixed(1) + 'w'
      if (num >= 1000) return (num / 1000).toFixed(1) + 'k'
      return String(num)
    },
    
    getCategoryType(category) {
      const typeMap = {
        'frontend': 'primary',
        'backend': 'success',
        'life': 'warning'
      }
      return typeMap[category] || 'info'
    },
    
    handleAvatarError(e) {
      e.target.src = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
    },
    
    async handleLike() {
      try {
        const res = await likeArticle(this.article.id)
        if (res.code === 0 || res.code === 200) {
          this.article.likeCount = (this.article.likeCount || 0) + 1
          this.article.isLiked = true
          this.$message.success('点赞成功')
        }
      } catch (error) {
        console.error('点赞失败:', error)
      }
    },
    
    handleFollow() {
      this.$message.info('关注功能开发中...')
    },
    
    handleShare() {
      const url = window.location.href
      if (navigator.clipboard) {
        navigator.clipboard.writeText(url).then(() => {
          this.$message.success('链接已复制到剪贴板')
        }).catch(() => {
          this.fallbackCopy(url)
        })
      } else {
        this.fallbackCopy(url)
      }
    },
    
    fallbackCopy(text) {
      const textarea = document.createElement('textarea')
      textarea.value = text
      document.body.appendChild(textarea)
      textarea.select()
      document.execCommand('copy')
      document.body.removeChild(textarea)
      this.$message.success('链接已复制到剪贴板')
    },
    
    handleEdit() {
      this.$router.push(`/blog/publish?id=${this.article.id}`)
    },
    
    scrollToComment() {
      this.$refs.commentSection?.scrollIntoView({ behavior: 'smooth' })
    }
  }
}
</script>
<style lang="scss" scoped>
.article-detail {
  max-width: 800px;
  margin: 0 auto;
  background: transparent;
  
  // 返回按钮
  .glass-back {
    background: rgba(255, 255, 255, 0.12);
    backdrop-filter: blur(20px) saturate(180%);
    -webkit-backdrop-filter: blur(20px) saturate(180%);
    border: 1px solid rgba(255, 255, 255, 0.15);
    color: rgba(255, 255, 255, 0.9);
    
    &:hover {
      background: rgba(255, 255, 255, 0.2);
      color: #fff;
    }
  }
  
  .back-btn {
    display: inline-flex;
    align-items: center;
    padding: 8px 16px;
    margin-bottom: 20px;
    cursor: pointer;
    border-radius: 20px;
    transition: all 0.3s;
    
    i {
      margin-right: 4px;
    }
  }
  
  // 文章容器
  .glass-container {
    background: rgba(255, 255, 255, 0.12);
    backdrop-filter: blur(20px) saturate(180%);
    -webkit-backdrop-filter: blur(20px) saturate(180%);
    border: 1px solid rgba(255, 255, 255, 0.15);
    border-radius: 16px;
    padding: 32px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  }
  
  .article-container {
    .article-title {
      font-size: 32px;
      font-weight: 700;
      line-height: 1.4;
      color: #fff;
      margin-bottom: 24px;
      text-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    }
    
    .author-bar {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding-bottom: 24px;
      margin-bottom: 24px;
      border-bottom: 1px solid rgba(255, 255, 255, 0.15);
      
      .author-info {
        display: flex;
        align-items: center;
        
        .glass-avatar {
          border: 2px solid rgba(255, 255, 255, 0.5);
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
        }
        
        .author-meta {
          margin-left: 12px;
          
          .author-name {
            font-size: 16px;
            font-weight: 500;
            color: #fff;
            margin-bottom: 4px;
            text-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
          }
          
          .publish-info {
            font-size: 13px;
            color: rgba(255, 255, 255, 0.7);
            
            .divider {
              margin: 0 8px;
            }
          }
        }
      }
      
      .glass-follow-btn {
        background: rgba(255, 255, 255, 0.15) !important;
        backdrop-filter: blur(10px);
        -webkit-backdrop-filter: blur(10px);
        border: 1px solid rgba(255, 255, 255, 0.2) !important;
        color: #fff !important;
        
        &:hover {
          background: rgba(255, 255, 255, 0.25) !important;
        }
      }
    }
    
    .article-content {
      .glass-cover {
        margin-bottom: 24px;
        border-radius: 8px;
        overflow: hidden;
        border: 1px solid rgba(255, 255, 255, 0.15);
        
        ::v-deep .el-image {
          width: 100%;
          max-height: 400px;
        }
      }
      
      .content-body {
        font-size: 16px;
        line-height: 1.8;
        color: rgba(255, 255, 255, 0.95);
        
        ::v-deep {
          h1, h2, h3, h4, h5, h6 {
            margin: 24px 0 16px;
            font-weight: 600;
            color: #fff;
          }
          
          p {
            margin-bottom: 16px;
          }
          
          a {
            color: rgba(255, 255, 255, 0.9);
            text-decoration: underline;
            text-underline-offset: 4px;
            
            &:hover {
              color: #fff;
            }
          }
          
          img {
            max-width: 100%;
            border-radius: 8px;
          }
          
          code {
            background: rgba(0, 0, 0, 0.2);
            padding: 2px 6px;
            border-radius: 4px;
            color: rgba(255, 255, 255, 0.9);
          }
          
          pre {
            background: rgba(0, 0, 0, 0.3);
            padding: 16px;
            border-radius: 8px;
            overflow-x: auto;
            border: 1px solid rgba(255, 255, 255, 0.1);
            
            code {
              background: none;
              color: #abb2bf;
            }
          }
          
          blockquote {
            border-left: 4px solid rgba(255, 255, 255, 0.5);
            padding-left: 16px;
            margin: 16px 0;
            color: rgba(255, 255, 255, 0.8);
          }
          
          table {
            border-collapse: collapse;
            
            th, td {
              border: 1px solid rgba(255, 255, 255, 0.2);
              padding: 8px 12px;
            }
            
            th {
              background: rgba(255, 255, 255, 0.1);
            }
          }
        }
      }
      
      .article-tags {
        margin-top: 32px;
        
        .glass-tag {
          background: rgba(255, 255, 255, 0.15) !important;
          backdrop-filter: blur(10px);
          -webkit-backdrop-filter: blur(10px);
          border: 1px solid rgba(255, 255, 255, 0.2) !important;
          color: #fff !important;
        }
      }
    }
    
    .action-bar {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-top: 40px;
      padding-top: 24px;
      border-top: 1px solid rgba(255, 255, 255, 0.15);
      
      .action-left {
        display: flex;
        gap: 24px;
        
        .action-btn {
          display: flex;
          align-items: center;
          gap: 6px;
          padding: 8px 16px;
          border-radius: 20px;
          cursor: pointer;
          color: rgba(255, 255, 255, 0.8);
          transition: all 0.3s;
          
          i {
            font-size: 18px;
          }
          
          &:hover {
            background: rgba(255, 255, 255, 0.15);
            color: #fff;
          }
          
          &.like-btn.active {
            color: #ff6b6b;
          }
        }
      }
      
      .action-right {
        .action-text-btn {
          color: rgba(255, 255, 255, 0.8) !important;
          
          &:hover {
            color: #fff !important;
          }
        }
      }
    }
  }
}

// 评论区域
.glass-section {
  background: rgba(255, 255, 255, 0.12);
  backdrop-filter: blur(20px) saturate(180%);
  -webkit-backdrop-filter: blur(20px) saturate(180%);
  border: 1px solid rgba(255, 255, 255, 0.15);
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.comment-section {
  margin-top: 40px;
  
  .comment-header {
    margin-bottom: 20px;
    
    h3 {
      font-size: 18px;
      font-weight: 600;
      color: #fff;
      text-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
    }
  }
  
  .comment-input {
    margin-bottom: 24px;
    
    ::v-deep .glass-textarea {
      textarea {
        background: rgba(255, 255, 255, 0.1) !important;
        backdrop-filter: blur(10px);
        -webkit-backdrop-filter: blur(10px);
        border: 1px solid rgba(255, 255, 255, 0.2) !important;
        color: #fff !important;
        
        &::placeholder {
          color: rgba(255, 255, 255, 0.5);
        }
        
        &:focus {
          border-color: rgba(255, 255, 255, 0.4) !important;
          background: rgba(255, 255, 255, 0.15) !important;
        }
      }
      
      .el-input__count {
        background: transparent;
        color: rgba(255, 255, 255, 0.7);
      }
    }
    
    .input-footer {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-top: 12px;
      
      .tip {
        font-size: 12px;
        color: rgba(255, 255, 255, 0.5);
      }
      
      .glass-submit-btn {
        background: rgba(79, 70, 229, 0.8) !important;
        backdrop-filter: blur(10px);
        -webkit-backdrop-filter: blur(10px);
        border: 1px solid rgba(255, 255, 255, 0.2) !important;
        color: #fff !important;
        
        &:hover {
          background: rgba(67, 56, 202, 0.9) !important;
        }
      }
    }
  }
  
  .comment-list {
    min-height: 200px;
    
    ::v-deep .glass-empty {
      .el-empty__description {
        color: rgba(255, 255, 255, 0.7);
      }
    }
  }
  
  .comment-pagination {
    display: flex;
    justify-content: center;
    margin-top: 24px;
    padding-top: 24px;
    border-top: 1px solid rgba(255, 255, 255, 0.15);
    
    ::v-deep .glass-pagination {
      .el-pagination {
        padding: 8px 16px;
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
      }
    }
  }
}

@media (max-width: 768px) {
  .article-detail {
    .article-container {
      padding: 20px;
      
      .article-title {
        font-size: 24px;
      }
      
      .content-body {
        font-size: 15px;
      }
    }
  }
  
  .comment-section {
    padding: 20px;
  }
}
</style>