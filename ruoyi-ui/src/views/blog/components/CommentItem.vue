<template>
  <div class="comment-item" :class="{ 'child-comment': isChild }">
    <!-- 用户头像 -->
    <div class="comment-avatar">
      <el-avatar :size="isChild ? 32 : 40" :src="comment.userAvatar">
        {{ getUserInitial(comment.userName) }}
      </el-avatar>
    </div>
    
    <!-- 评论内容区域 -->
    <div class="comment-content">
      <!-- 用户信息和操作 -->
      <div class="comment-header">
        <div class="user-info">
          <span class="user-name">{{ comment.userName || '匿名用户' }}</span>
          <span v-if="comment.replyToUserName" class="reply-to">
            <i class="el-icon-caret-right"></i>
            回复 @{{ comment.replyToUserName }}
          </span>
        </div>
        
        <div class="comment-actions">
          <span class="action-btn" @click="handleLike">
            <i :class="comment.isLiked ? 'el-icon-star-on' : 'el-icon-star-off'"></i>
            {{ comment.likeCount || 0 }}
          </span>
          <span class="action-btn" @click="handleReply">
            <i class="el-icon-chat-line-round"></i>
            回复
          </span>
          <span 
            v-if="isOwnComment" 
            class="action-btn delete-btn" 
            @click="handleDelete"
          >
            <i class="el-icon-delete"></i>
            删除
          </span>
        </div>
      </div>
      
      <!-- 评论正文 -->
      <div class="comment-body">
        <p class="comment-text">{{ comment.content }}</p>
        <span class="comment-time">{{ formatTime(comment.createTime) }}</span>
      </div>
      
      <!-- 子评论区域 - 懒加载 -->
      <div v-if="!isChild && comment.replyCount > 0" class="children-section">
        <!-- 未展开状态：显示查看回复按钮 -->
        <div v-if="!showChildren" class="view-replies-btn" @click="loadChildren">
          <span>
            <i class="el-icon-chat-dot-round"></i>
            查看 {{ comment.replyCount }} 条回复
            <i class="el-icon-arrow-down"></i>
          </span>
        </div>
        
        <!-- 已展开状态：显示子评论列表 -->
        <div v-else>
          <div v-if="childrenLoading" class="children-loading">
            <i class="el-icon-loading"></i> 加载中...
          </div>
          <div v-else>
            <CommentItem 
              v-for="child in childrenList" 
              :key="child.id"
              :comment="child"
              :is-child="true"
              :article-id="articleId"
              @reply="handleChildReply"
              @delete="handleChildDelete"
              @delete-child="handleGrandchildDelete"
              @like="$emit('like', $event)"
              @show-reply-input="handleChildShowReplyInput"
              @close-reply-input="handleChildCloseReplyInput"
              @add-child-reply="handleAddChildReply"
            />
            
            <!-- 加载更多按钮 -->
            <div v-if="hasMore" class="load-more-btn" @click="loadMore">
              <span v-if="!loadingMore">
                查看更多回复 ({{ childrenList.length }}/{{ comment.replyCount }})
                <i class="el-icon-arrow-down"></i>
              </span>
              <span v-else>
                <i class="el-icon-loading"></i> 加载中...
              </span>
            </div>
            
            <!-- 收起按钮 -->
            <div class="collapse-btn" @click="collapseChildren">
              <span>
                <i class="el-icon-arrow-up"></i>
                收起回复
              </span>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 回复输入框 -->
      <div v-if="showReplyInput" class="reply-input-container">
        <el-input
          v-model="replyContent"
          type="textarea"
          :rows="3"
          :placeholder="replyPlaceholder"
          maxlength="500"
          show-word-limit
          @keyup.ctrl.enter.native="submitReply"
        />
        <div class="reply-actions">
          <el-button size="small" @click="cancelReply">取消</el-button>
          <el-button 
            type="primary" 
            size="small" 
            :loading="submitting"
            @click="submitReply"
          >
            回复
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { likeComment, deleteComment, addComment, getChildComments } from '../api/comment'

export default {
  name: 'CommentItem',
  props: {
    comment: {
      type: Object,
      required: true
    },
    isChild: {
      type: Boolean,
      default: false
    },
    articleId: {
      type: [String, Number],
      required: true
    }
  },
  data() {
    return {
      showReplyInput: false,
      replyContent: '',
      submitting: false,
      showChildren: false,
      childrenLoading: false,
      loadingMore: false,
      childrenList: [],
      currentPage: 1,
      pageSize: 5,
      totalLoaded: false
    }
  },
  computed: {
    isOwnComment() {
      const userId = this.$store.state.user?.id
      return userId && userId === this.comment.userId
    },
    replyPlaceholder() {
      return `回复 @${this.comment.userName}：`
    },
    hasMore() {
      return !this.totalLoaded && this.childrenList.length < this.comment.replyCount
    }
  },
  watch: {
    comment: {
      handler(newVal) {
        // 同步外部传入的状态
        if (newVal._showChildren !== undefined) {
          this.showChildren = newVal._showChildren
        }
        if (newVal._childrenList) {
          this.childrenList = newVal._childrenList
        }
      },
      deep: true,
      immediate: true
    }
  },
  methods: {
    getUserInitial(name) {
      return name ? name.charAt(0).toUpperCase() : 'U'
    },
    
    formatTime(time) {
      if (!time) return ''
      const now = new Date()
      const date = new Date(time)
      const diff = now - date
      
      if (diff < 60000) return '刚刚'
      if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
      if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
      if (diff < 604800000) return `${Math.floor(diff / 86400000)}天前`
      
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      const hour = String(date.getHours()).padStart(2, '0')
      const minute = String(date.getMinutes()).padStart(2, '0')
      
      if (year === now.getFullYear()) {
        return `${month}-${day} ${hour}:${minute}`
      }
      return `${year}-${month}-${day} ${hour}:${minute}`
    },
    
    async loadChildren() {
      // 发出展开事件
      this.$emit('toggle-children', { commentId: this.comment.id, isExpanded: true })
      
      this.showChildren = true
      this.comment._showChildren = true
      
      // 如果已经有子评论列表，直接显示，不重新加载
      if (this.childrenList.length > 0) {
        return
      }
      
      this.childrenLoading = true
      this.currentPage = 1
      
      try {
        const res = await getChildComments(this.comment.id, {
          pageNum: this.currentPage,
          pageSize: this.pageSize
        })
        
        if (res.code === 200) {
          this.childrenList = res.data || []
          this.comment._childrenList = this.childrenList
          
          // 更新回复数（以后端返回的为准）
          if (res.total !== undefined) {
            this.comment.replyCount = res.total
          }
          
          this.totalLoaded = (this.childrenList.length === 0 || 
                             this.childrenList.length >= this.comment.replyCount)
        }
      } catch (error) {
        console.error('加载回复失败:', error)
        this.$message.error('加载回复失败')
      } finally {
        this.childrenLoading = false
      }
    },
    
    async loadMore() {
      if (this.loadingMore) return
      this.loadingMore = true
      this.currentPage++
      
      try {
        const res = await getChildComments(this.comment.id, {
          pageNum: this.currentPage,
          pageSize: this.pageSize
        })
        if (res.code === 200 && res.data && res.data.length > 0) {
          this.childrenList = [...this.childrenList, ...res.data]
          this.comment._childrenList = this.childrenList
          this.totalLoaded = (res.data.length < this.pageSize || 
                             this.childrenList.length >= this.comment.replyCount)
        } else {
          this.totalLoaded = true
        }
      } catch (error) {
        console.error('加载更多失败:', error)
        this.$message.error('加载失败')
        this.currentPage--
      } finally {
        this.loadingMore = false
      }
    },
    
    collapseChildren() {
      // 发出收起事件，让父组件知道要刷新数据
      this.$emit('toggle-children', { commentId: this.comment.id, isExpanded: false })
      
      this.showChildren = false
      this.comment._showChildren = false
      
      // 清空子评论列表，这样下次展开时会重新从后端获取最新数据
      this.childrenList = []
      this.comment._childrenList = []
      this.totalLoaded = false
      this.currentPage = 1
    },
    
    async handleLike() {
      if (!this.$store.state.user?.id) {
        this.$message.warning('请先登录')
        return
      }
      try {
        const res = await likeComment(this.comment.id)
        if (res.code === 200) {
          // 如果已经是点赞状态，则取消点赞
          if (this.comment.isLiked) {
            this.comment.likeCount = Math.max(0, (this.comment.likeCount || 0) - 1)
            this.comment.isLiked = false
          } else {
            this.comment.likeCount = (this.comment.likeCount || 0) + 1
            this.comment.isLiked = true
          }
          this.$emit('like', this.comment.id)
        }
      } catch (error) {
        console.error('点赞失败:', error)
      }
    },
    
    handleReply() {
      if (!this.$store.state.user?.id) {
        this.$message.warning('请先登录')
        return
      }
      
      this.$emit('show-reply-input', this.comment.id)
      this.showReplyInput = true
    },
    
    cancelReply() {
      this.showReplyInput = false
      this.replyContent = ''
      this.$emit('close-reply-input')
    },
    
    async submitReply() {
      if (!this.replyContent.trim()) {
        this.$message.warning('请输入回复内容')
        return
      }
      
      this.submitting = true
      
      const replyContent = this.replyContent
      const parentId = this.isChild ? this.comment.parentId : this.comment.id
      
      try {
        const data = {
          articleId: this.articleId,
          content: replyContent,
          parentId: parentId,
          replyToUserId: this.comment.userId
        }
        
        const res = await addComment(data)
        
        if (res.code === 200) {
          const newReply = {
            id: res.data?.id || Date.now(),
            content: replyContent,
            userName: this.$store.state.user?.name || this.$store.state.user?.username || '我',
            userAvatar: this.$store.state.user?.avatar || '',
            userId: this.$store.state.user?.id,
            replyToUserId: this.comment.userId,
            replyToUserName: this.comment.userName,
            createTime: new Date().toISOString(),
            likeCount: 0,
            isLiked: false,
            parentId: parentId
          }
          
          if (!this.isChild) {
            // 如果是主评论的回复
            if (!this.childrenList) {
              this.childrenList = []
            }
            
            // 检查是否已存在
            const exists = this.childrenList.some(child => child.id === newReply.id)
            if (!exists) {
              this.childrenList.unshift(newReply)
              this.comment._childrenList = this.childrenList
              
              // 回复数 +1
              this.comment.replyCount = (this.comment.replyCount || 0) + 1
              
              // 自动展开
              if (!this.showChildren) {
                this.showChildren = true
                this.comment._showChildren = true
              }
            }
          } else {
            // 如果是子评论的回复
            this.$emit('add-child-reply', {
              parentId: this.comment.parentId,
              reply: newReply
            })
          }
          
          this.$emit('reply', {
            parentId: parentId,
            comment: newReply
          })
          
          this.$message.success('回复成功')
          
          this.replyContent = ''
          this.showReplyInput = false
        } else {
          this.$message.error(res.msg || '回复失败')
        }
      } catch (error) {
        console.error('回复失败:', error)
        this.$message.error('回复失败')
      } finally {
        this.submitting = false
      }
    },
    
    async handleDelete() {
      try {
        await this.$confirm('确定要删除这条评论吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        const res = await deleteComment(this.comment.id)
        if (res.code === 200) {
          this.$message.success('删除成功')
          
          const deleteInfo = {
            commentId: this.comment.id,
            parentId: this.isChild ? this.comment.parentId : this.comment.id,
            isChild: this.isChild,
            articleId: this.articleId
          }
          
          if (this.isChild) {
            // 子评论删除：通知父组件
            this.$emit('delete-child', deleteInfo)
          } else {
            // 主评论删除：清空自己的子评论缓存
            this.childrenList = []
            this.comment._childrenList = []
            this.showChildren = false
            this.comment._showChildren = false
            this.totalLoaded = false
            this.currentPage = 1
            
            this.$emit('delete', deleteInfo)
          }
          
          // 删除动画
          this.$el.style.transition = 'all 0.3s'
          this.$el.style.opacity = '0'
          this.$el.style.transform = 'translateX(-20px)'
          
          setTimeout(() => {
            this.$el.style.display = 'none'
          }, 300)
        } else {
          this.$message.error(res.msg || '删除失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除失败:', error)
          this.$message.error('删除失败')
        }
      }
    },
    
    handleChildReply(data) {
      this.$emit('reply', data)
    },
    
    handleChildDelete(deleteInfo) {
      this.$emit('delete-child', deleteInfo)
    },
    
    handleGrandchildDelete(deleteInfo) {
      this.$emit('delete-child', deleteInfo)
    },
    
    handleChildShowReplyInput(commentId) {
      this.$emit('show-reply-input', commentId)
    },
    
    handleChildCloseReplyInput() {
      this.$emit('close-reply-input')
    },
    
    handleAddChildReply(data) {
      this.$emit('add-child-reply', data)
    }
  }
}
</script>

<style scoped>
.comment-item {
  margin-bottom: 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  padding-bottom: 16px;
}

.child-comment {
  margin-left: 48px;
  margin-bottom: 12px;
  border-bottom: none;
  padding-bottom: 0;
}

.comment-avatar {
  flex-shrink: 0;
}

.comment-content {
  flex: 1;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.user-name {
  font-weight: 600;
  color: #a0cfff;
  font-size: 14px;
}

.reply-to {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.6);
}

.reply-to i {
  margin-right: 2px;
}

.comment-actions {
  display: flex;
  gap: 16px;
}

.action-btn {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.5);
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 4px;
  transition: all 0.2s;
}

.action-btn:hover {
  color: #a0cfff;
}

.delete-btn:hover {
  color: #f56c6c;
}

.comment-body {
  margin-bottom: 8px;
}

.comment-text {
  margin: 0 0 6px 0;
  line-height: 1.5;
  word-break: break-word;
  color: rgba(255, 255, 255, 0.9);
}

.comment-time {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.4);
}

.children-section {
  margin-top: 12px;
  padding-left: 12px;
  border-left: 2px solid rgba(255, 255, 255, 0.15);
}

.view-replies-btn {
  cursor: pointer;
  color: #a0cfff;
  font-size: 13px;
  padding: 4px 0;
  transition: all 0.2s;
}

.view-replies-btn:hover {
  opacity: 0.8;
  color: #cce5ff;
}

.children-loading {
  padding: 12px;
  text-align: center;
  color: rgba(255, 255, 255, 0.5);
  font-size: 13px;
}

.load-more-btn {
  cursor: pointer;
  color: #a0cfff;
  font-size: 13px;
  padding: 8px 0;
  text-align: center;
  border-top: 1px dashed rgba(255, 255, 255, 0.15);
  margin-top: 8px;
  transition: all 0.2s;
}

.load-more-btn:hover {
  opacity: 0.8;
  color: #cce5ff;
}

.collapse-btn {
  cursor: pointer;
  color: rgba(255, 255, 255, 0.5);
  font-size: 13px;
  padding: 8px 0 4px;
  display: inline-block;
  transition: all 0.2s;
}

.collapse-btn:hover {
  color: #a0cfff;
}

.reply-input-container {
  margin-top: 12px;
  padding-top: 8px;
}

.reply-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 8px;
}

/* 回复输入框深色主题适配 */
.reply-input-container >>> .el-textarea__inner {
  background-color: rgba(0, 0, 0, 0.3);
  border-color: rgba(255, 255, 255, 0.15);
  color: rgba(255, 255, 255, 0.9);
}

.reply-input-container >>> .el-textarea__inner:focus {
  border-color: #a0cfff;
}

.reply-input-container >>> .el-textarea .el-input__count {
  background-color: transparent;
  color: rgba(255, 255, 255, 0.4);
}
</style>