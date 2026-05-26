<template>
  <div class="ai-chat-container">
    <!-- 侧边栏遮罩 -->
    <div v-if="showSidebar" class="sidebar-overlay" @click="closeSidebar"></div>

    <!-- 侧边栏 -->
    <div class="chat-sidebar glass-sidebar" :class="{ 'sidebar-open': showSidebar }">
      <div class="sidebar-header">
        <h3>会话记录</h3>
        <button class="sidebar-close-btn" @click="closeSidebar">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
            <path d="M18 6L6 18M6 6L18 18" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
          </svg>
        </button>
      </div>
      <div class="sidebar-content">
        <div v-if="sessionList.length === 0" class="sidebar-empty">
          <p>暂无会话记录</p>
        </div>
        <div
          v-for="session in sessionList"
          :key="session.sessionId"
          class="session-item glass-session-item"
          :class="{ 'session-active': currentSessionId === session.sessionId }"
          @click="selectSession(session)"
        >
          <div class="session-info">
            <div class="session-name">{{ session.sessionName || '新对话' }}</div>
            <div class="session-time">{{ formatSessionTime(session.updateTime) }}</div>
          </div>
          <div class="session-delete" @click.stop="handleDeleteSession(session)">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
              <path d="M4 7h16M10 11v6M14 11v6M5 7l1 14h12l1-14M9 4h6" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            </svg>
          </div>
        </div>
      </div>
    </div>

    <!-- 右下角会话按钮 -->
    <button class="session-toggle-btn glass-toggle-btn" @click="toggleSidebar" :class="{ 'btn-active': showSidebar }">
      <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
        <path d="M3 12h14M3 6h18M3 18h18" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
        <circle cx="20" cy="12" r="2" fill="currentColor"/>
      </svg>
    </button>

    <!-- 当没有消息时：显示居中的兑换框样式 -->
    <div v-if="messages.length === 0" class="welcome-exchange">
      <div class="exchange-card glass-card">
        <div class="exchange-avatar">
          <img :src="aiAvatar" alt="AI" />
        </div>
        <div class="exchange-title">{{ aiName }}</div>
        <div class="exchange-desc">有什么可以帮你的？</div>
        <div class="exchange-input-area">
          <input
            v-model="inputText"
            type="text"
            :placeholder="`发消息给 ${aiName}...`"
            @keypress.enter="sendMessage"
            class="exchange-input"
          />
          <button @click="sendMessage" class="exchange-send-btn" :disabled="!inputText.trim()">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
              <path d="M22 2L11 13M22 2L15 22L11 13M22 2L2 9L11 13" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </button>
        </div>
      </div>
    </div>

    <!-- 有消息后：正常对话框模式 -->
    <div v-else class="chat-dialog">
      <div class="chat-header glass-header">
        <div class="header-ai-info">
          <img :src="aiAvatar" alt="AI" class="header-avatar" />
          <span class="header-name">{{ aiName }}</span>
        </div>
        <div class="header-actions">
          <button class="new-chat-btn glass-new-chat" @click="startNewChat" title="新建会话">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
              <path d="M12 5v14M5 12h14" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            </svg>
            <span>新对话</span>
          </button>
          <div class="header-status glass-status">在线 · 秒回</div>
        </div>
      </div>

      <div class="message-list" ref="messageList">
        <div
          v-for="msg in messages"
          :key="msg.id"
          class="message-row"
          :class="msg.sender === 'user' ? 'user-row' : 'ai-row'"
        >
          <div v-if="msg.sender === 'ai'" class="avatar-col">
            <img :src="aiAvatar" alt="AI" class="msg-avatar" />
          </div>

          <div class="bubble-col">
            <div class="bubble-wrapper" :class="{ 'user-bubble-wrapper': msg.sender === 'user' }">
              <div class="message-bubble" :class="[msg.sender, { 'glass-bubble': msg.sender === 'ai' }]">
                {{ msg.text }}
                <span v-if="msg.isStreaming" class="streaming-cursor">|</span>
              </div>
              <div class="message-time" :class="{ 'time-right': msg.sender === 'user' }">
                {{ formatTime(msg.timestamp) }}
              </div>
            </div>
          </div>

          <div v-if="msg.sender === 'user'" class="avatar-col placeholder-avatar"></div>
        </div>

        <div v-if="isAiTyping" class="message-row ai-row">
          <div class="avatar-col">
            <img :src="aiAvatar" alt="AI" class="msg-avatar" />
          </div>
          <div class="bubble-col">
            <div class="message-bubble ai typing-bubble glass-bubble">
              <span class="dot-flashing"></span>
            </div>
          </div>
        </div>
      </div>

      <div class="chat-footer glass-footer">
        <input
          v-model="inputText"
          type="text"
          placeholder="输入消息..."
          @keypress.enter="sendMessage"
          class="footer-input glass-input"
        />
        <button @click="sendMessage" class="footer-send glass-send" :disabled="!inputText.trim()">
          发送
        </button>
      </div>
    </div>

    <!-- 删除确认弹窗 -->
    <div v-if="showDeleteConfirm" class="modal-overlay" @click="cancelDelete">
      <div class="delete-confirm-modal glass-modal" @click.stop>
        <div class="modal-header">
          <h4>删除会话</h4>
          <button class="modal-close" @click="cancelDelete">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
              <path d="M18 6L6 18M6 6L18 18" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            </svg>
          </button>
        </div>
        <div class="modal-body">
          <p>确定要删除这个会话吗？删除后将无法恢复。</p>
          <p class="session-name-preview">"{{ sessionToDelete ? sessionToDelete.sessionName || '新对话' : '' }}"</p>
        </div>
        <div class="modal-footer">
          <button class="btn-cancel glass-btn" @click="cancelDelete">取消</button>
          <button class="btn-confirm glass-btn-danger" @click="confirmDelete" :disabled="isDeleting">
            {{ isDeleting ? '删除中...' : '确定删除' }}
          </button>
        </div>
      </div>
    </div>

    <!-- 提示消息 -->
    <div v-if="showToast" class="toast-message glass-toast" :class="toastType">
      {{ toastText }}
    </div>
  </div>
</template>

<script>
import { aiChat, loadSession, getSessionChat, deleteSession, aiChatStream } from '../api/pthon'

export default {
  name: 'AiChatBox',
  props: {
    aiAvatarUrl: {
      type: String,
      default: 'https://ts1.tc.mm.bing.net/th/id/OIP-C.GQRRZyECC4S0gswNAWh0YQHaHa?rs=1&pid=ImgDetMain&o=7&rm=3'
    },
    aiName: {
      type: String,
      default: '小咖'
    }
  },
  data() {
    return {
      messages: [],
      inputText: '',
      isAiTyping: false,
      currentSessionId: null,
      sessionList: [],
      showSidebar: false,
      showDeleteConfirm: false,
      sessionToDelete: null,
      isDeleting: false,
      showToast: false,
      toastText: '',
      toastType: 'success',
      toastTimer: null
    }
  },
  computed: {
    aiAvatar() {
      return this.aiAvatarUrl
    }
  },
  mounted() {
    this.loadSessionList()
  },
  beforeDestroy() {
    if (this.toastTimer) {
      clearTimeout(this.toastTimer)
    }
  },
  methods: {
    showToastMessage(text, type = 'success') {
      if (this.toastTimer) {
        clearTimeout(this.toastTimer)
      }
      this.toastText = text
      this.toastType = type
      this.showToast = true
      this.toastTimer = setTimeout(() => {
        this.showToast = false
      }, 3000)
    },

    formatTime(timestamp) {
      if (!timestamp) return ''
      
      const date = new Date(timestamp)
      const now = new Date()
      
      const isSameDay = date.toDateString() === now.toDateString()
      const isSameYear = date.getFullYear() === now.getFullYear()
      
      const hours = date.getHours().toString().padStart(2, '0')
      const minutes = date.getMinutes().toString().padStart(2, '0')
      const timeStr = `${hours}:${minutes}`
      
      if (isSameDay) {
        return timeStr
      } else {
        const month = (date.getMonth() + 1).toString().padStart(2, '0')
        const day = date.getDate().toString().padStart(2, '0')
        
        if (isSameYear) {
          return `${month}-${day} ${timeStr}`
        } else {
          const year = date.getFullYear()
          return `${year}-${month}-${day} ${timeStr}`
        }
      }
    },

    formatSessionTime(timeStr) {
      if (!timeStr) return ''
      const date = new Date(timeStr.replace(/-/g, '/'))
      const now = new Date()
      const diff = now - date
      const days = Math.floor(diff / (1000 * 60 * 60 * 24))
      
      if (days === 0) {
        return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
      } else if (days === 1) {
        return '昨天'
      } else if (days < 7) {
        return `${days}天前`
      } else {
        const year = date.getFullYear()
        const month = (date.getMonth() + 1).toString().padStart(2, '0')
        const day = date.getDate().toString().padStart(2, '0')
        
        if (year === now.getFullYear()) {
          return `${month}-${day}`
        } else {
          return `${year}-${month}-${day}`
        }
      }
    },

    toggleSidebar() {
      this.showSidebar = !this.showSidebar
      if (this.showSidebar) {
        this.loadSessionList()
      }
    },

    closeSidebar() {
      this.showSidebar = false
    },

    async loadSessionList() {
      try {
        const response = await loadSession()
        if (response && response.data) {
          this.sessionList = response.data
        }
      } catch (error) {
        console.error('加载会话列表失败:', error)
        this.showToastMessage('加载会话列表失败', 'error')
      }
    },

    async selectSession(session) {
      try {
        const response = await getSessionChat(session.sessionId)
        if (response && response.data) {
          const chatMessages = []
          
          response.data.forEach((msg) => {
            const msgTime = msg.createTime 
              ? new Date(msg.createTime.replace(/-/g, '/')).getTime()
              : Date.now()
            
            if (msg.role) {
              chatMessages.push({
                id: `${msg.messageId}-user`,
                sender: 'user',
                text: msg.role,
                timestamp: msgTime
              })
            }
            
            if (msg.content) {
              chatMessages.push({
                id: `${msg.messageId}-ai`,
                sender: 'ai',
                text: msg.content,
                timestamp: msgTime
              })
            }
          })
          
          chatMessages.sort((a, b) => a.timestamp - b.timestamp)
          
          this.messages = chatMessages
          this.currentSessionId = session.sessionId
          this.closeSidebar()
          
          this.$nextTick(() => {
            this.scrollToBottom()
          })
        }
      } catch (error) {
        console.error('加载会话记录失败:', error)
        this.showToastMessage('加载会话记录失败', 'error')
      }
    },

    handleDeleteSession(session) {
      this.sessionToDelete = session
      this.showDeleteConfirm = true
    },

    cancelDelete() {
      this.showDeleteConfirm = false
      this.sessionToDelete = null
    },

    async confirmDelete() {
      if (!this.sessionToDelete || this.isDeleting) return
      
      const sessionId = this.sessionToDelete.sessionId
      this.isDeleting = true
      
      try {
        const response = await deleteSession(sessionId)
        
        if (response.code === 200) {
          // 如果删除的是当前会话，清空消息
          if (this.currentSessionId === sessionId) {
            this.messages = []
            this.currentSessionId = null
          }
          
          // 刷新会话列表
          await this.loadSessionList()
          
          this.showDeleteConfirm = false
          this.sessionToDelete = null
          
          this.showToastMessage('删除成功')
        } else {
          this.showToastMessage(response.msg || '删除失败', 'error')
        }
      } catch (error) {
        console.error('删除会话失败:', error)
        this.showToastMessage('删除失败，请稍后再试', 'error')
      } finally {
        this.isDeleting = false
      }
    },

    startNewChat() {
      this.messages = []
      this.currentSessionId = null
      this.inputText = ''
    },

    async sendMessage() {
      const text = this.inputText.trim()
      if (!text) return

      const userMsg = {
        id: Date.now() + '-user',
        sender: 'user',
        text: text,
        timestamp: Date.now()
      }
      this.messages.push(userMsg)
      this.inputText = ''

      this.$nextTick(() => {
        this.scrollToBottom()
      })

      const aiMsg = {
        id: Date.now() + '-ai',
        sender: 'ai',
        text: '',
        timestamp: Date.now(),
        isStreaming: true
      }
      this.messages.push(aiMsg)
      
      this.isAiTyping = false

      try {
        const requestData = {
          question: text
        }
        if (this.currentSessionId) {
          requestData.sessionId = this.currentSessionId
        }

        await aiChatStream(
          requestData,
          (token) => {
            // onToken：收到流式token
            aiMsg.text += token
            this.$nextTick(() => {
              this.scrollToBottom()
            })
          },
          (errorMsg) => {
            // onError：对话出错
            if (aiMsg.text.length > 0) {
              aiMsg.text += ` [${errorMsg}]`
            } else {
              aiMsg.text = errorMsg
            }
            aiMsg.isStreaming = false
            this.$nextTick(() => {
              this.scrollToBottom()
            })
          },
          async () => {
            // onComplete：对话完成
            aiMsg.isStreaming = false
            
            // ========== 优化点：只在新建会话时刷新列表 ==========
            if (!this.currentSessionId) {
              await this.loadSessionList()
              // 取最新的会话作为当前会话
              if (this.sessionList.length > 0) {
                this.currentSessionId = this.sessionList[0].sessionId
              }
            }
            // ====================================================
            
            this.$nextTick(() => {
              this.scrollToBottom()
            })
          }
        )

      } catch (error) {
        console.error('AI调用失败:', error)
        aiMsg.text = '网络开小差了，请稍后再试~'
        aiMsg.isStreaming = false
        this.$nextTick(() => {
          this.scrollToBottom()
        })
      }
    },

    scrollToBottom() {
      const container = this.$refs.messageList
      if (container) {
        container.scrollTop = container.scrollHeight
      }
    }
  }
}
</script>

<style scoped>
/* 全局重置与字体 */
.ai-chat-container {
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
  background: transparent;
  height: 92vh;
  width: 100%;
  display: flex;
  flex-direction: column;
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  position: relative;
}

/* ----- 玻璃态通用样式 ----- */
.glass-card {
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(20px) saturate(180%);
  -webkit-backdrop-filter: blur(20px) saturate(180%);
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1),
              inset 0 1px 0 rgba(255, 255, 255, 0.2);
}

.glass-header {
  background: rgba(255, 255, 255, 0.12);
  backdrop-filter: blur(20px) saturate(180%);
  -webkit-backdrop-filter: blur(20px) saturate(180%);
  border-bottom: 1px solid rgba(255, 255, 255, 0.15);
}

.glass-footer {
  background: rgba(255, 255, 255, 0.12);
  backdrop-filter: blur(20px) saturate(180%);
  -webkit-backdrop-filter: blur(20px) saturate(180%);
  border-top: 1px solid rgba(255, 255, 255, 0.15);
}

.glass-bubble {
  background: rgba(255, 255, 255, 0.15) !important;
  backdrop-filter: blur(10px) saturate(180%);
  -webkit-backdrop-filter: blur(10px) saturate(180%);
  border: 1px solid rgba(255, 255, 255, 0.15) !important;
  color: #fff !important;
}

.glass-status {
  background: rgba(16, 185, 129, 0.2);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid rgba(16, 185, 129, 0.3);
  color: #fff;
}

.glass-input {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2) !important;
  color: #fff;
}

.glass-input::placeholder {
  color: rgba(255, 255, 255, 0.6);
}

.glass-input:focus {
  border-color: rgba(255, 255, 255, 0.4) !important;
  background: rgba(255, 255, 255, 0.15);
}

.glass-send {
  background: rgba(79, 70, 229, 0.8) !important;
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 8px 16px -6px rgba(79, 70, 229, 0.4);
}

.glass-send:hover:not(:disabled) {
  background: rgba(67, 56, 202, 0.9) !important;
}

/* ----- 侧边栏样式 ----- */
.chat-sidebar {
  position: fixed;
  right: 0;
  top: 0;
  bottom: 0;
  width: 320px;
  transform: translateX(100%);
  transition: transform 0.3s ease;
  z-index: 1000;
  display: flex;
  flex-direction: column;
}

.chat-sidebar.sidebar-open {
  transform: translateX(0);
}

.glass-sidebar {
  background: rgba(255, 255, 255, 0.12) !important;
  backdrop-filter: blur(20px) saturate(180%);
  -webkit-backdrop-filter: blur(20px) saturate(180%);
  border-left: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: -8px 0 32px rgba(0, 0, 0, 0.15);
}

.sidebar-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.3);
  backdrop-filter: blur(4px);
  -webkit-backdrop-filter: blur(4px);
  z-index: 999;
}

.sidebar-header {
  padding: 20px 20px 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid rgba(255, 255, 255, 0.15);
}

.sidebar-header h3 {
  margin: 0;
  color: #fff;
  font-size: 18px;
  font-weight: 600;
  text-shadow: 0 1px 4px rgba(0, 0, 0, 0.15);
}

.sidebar-close-btn {
  width: 32px;
  height: 32px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.15);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: rgba(255, 255, 255, 0.9);
  transition: all 0.15s;
}

.sidebar-close-btn:hover {
  background: rgba(255, 255, 255, 0.2);
  color: #fff;
}

.sidebar-content {
  flex: 1;
  overflow-y: auto;
  padding: 16px 12px;
}

.sidebar-content::-webkit-scrollbar {
  width: 4px;
}

.sidebar-content::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 2px;
}

.sidebar-content::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.2);
  border-radius: 2px;
}

.sidebar-empty {
  text-align: center;
  color: rgba(255, 255, 255, 0.7);
  padding: 40px 20px;
  font-size: 14px;
}

.session-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 16px;
  margin-bottom: 8px;
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.15s;
}

.glass-session-item {
  background: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.session-item:hover {
  background: rgba(255, 255, 255, 0.15);
  border-color: rgba(255, 255, 255, 0.2);
}

.session-active {
  background: rgba(79, 70, 229, 0.25) !important;
  border-color: rgba(79, 70, 229, 0.4) !important;
}

.session-info {
  flex: 1;
  min-width: 0;
}

.session-name {
  color: #fff;
  font-size: 15px;
  font-weight: 500;
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  text-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
}

.session-time {
  color: rgba(255, 255, 255, 0.65);
  font-size: 12px;
}

.session-delete {
  width: 28px;
  height: 28px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  color: rgba(255, 255, 255, 0.6);
  transition: all 0.15s;
}

.session-delete:hover {
  background: rgba(239, 68, 68, 0.25);
  color: #fca5a5;
  border-color: rgba(239, 68, 68, 0.35);
}

/* ----- 右下角会话按钮 ----- */
.session-toggle-btn {
  position: fixed;
  right: 20px;
  bottom: 20px;
  width: 56px;
  height: 56px;
  border-radius: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #fff;
  transition: all 0.3s ease;
  z-index: 998;
  border: none;
}

.glass-toggle-btn {
  background: rgba(79, 70, 229, 0.8);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 8px 20px rgba(79, 70, 229, 0.3);
}

.session-toggle-btn:hover {
  transform: scale(1.05);
  background: rgba(67, 56, 202, 0.9);
  box-shadow: 0 10px 24px rgba(79, 70, 229, 0.4);
}

.session-toggle-btn.btn-active {
  background: rgba(67, 56, 202, 0.95);
  transform: rotate(90deg);
}

/* ----- 兑换框样式 (居中，玻璃态) ----- */
.welcome-exchange {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.exchange-card {
  border-radius: 32px;
  padding: 36px 32px 32px;
  max-width: 420px;
  width: 100%;
  text-align: center;
  transition: all 0.2s ease;
}

.exchange-avatar {
  width: 80px;
  height: 80px;
  margin: 0 auto 16px;
  border-radius: 40px;
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.exchange-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.exchange-title {
  font-size: 28px;
  font-weight: 600;
  letter-spacing: -0.01em;
  color: #fff;
  margin-bottom: 6px;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.exchange-desc {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.8);
  margin-bottom: 28px;
  font-weight: 400;
}

.exchange-input-area {
  display: flex;
  align-items: center;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border-radius: 48px;
  padding: 6px 6px 6px 22px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  transition: border 0.15s;
}

.exchange-input-area:focus-within {
  border-color: rgba(255, 255, 255, 0.4);
  background: rgba(255, 255, 255, 0.15);
}

.exchange-input {
  flex: 1;
  border: none;
  background: transparent;
  padding: 12px 0;
  font-size: 16px;
  outline: none;
  color: #fff;
}

.exchange-input::placeholder {
  color: rgba(255, 255, 255, 0.6);
}

.exchange-send-btn {
  background: rgba(79, 70, 229, 0.8);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 40px;
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  cursor: pointer;
  transition: 0.15s;
  box-shadow: 0 6px 16px -4px rgba(79, 70, 229, 0.3);
}

.exchange-send-btn:hover {
  background: rgba(67, 56, 202, 0.9);
}

.exchange-send-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
  box-shadow: none;
}

/* ----- 正常对话框布局 ----- */
.chat-dialog {
  display: flex;
  flex-direction: column;
  height: 100%;
  background: transparent;
}

.chat-header {
  padding: 14px 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
  border-radius: 0 0 0 0;
}

.header-ai-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-avatar {
  width: 36px;
  height: 36px;
  border-radius: 36px;
  object-fit: cover;
  border: 2px solid rgba(255, 255, 255, 0.5);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.header-name {
  font-weight: 600;
  font-size: 18px;
  color: #fff;
  text-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.new-chat-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 14px;
  border-radius: 24px;
  font-size: 13px;
  font-weight: 500;
  color: #fff;
  cursor: pointer;
  transition: all 0.15s;
  border: none;
}

.glass-new-chat {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.15);
}

.new-chat-btn:hover {
  background: rgba(255, 255, 255, 0.2);
  border-color: rgba(255, 255, 255, 0.3);
}

.header-status {
  font-size: 13px;
  padding: 4px 12px;
  border-radius: 30px;
  font-weight: 500;
}

/* 消息列表 */
.message-list {
  flex: 1;
  overflow-y: auto;
  padding: 20px 16px 12px;
  display: flex;
  flex-direction: column;
  gap: 16px;
  scroll-behavior: smooth;
  background: transparent;
}

.message-list::-webkit-scrollbar {
  width: 4px;
}

.message-list::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 2px;
}

.message-list::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.3);
  border-radius: 2px;
}

.message-list::-webkit-scrollbar-thumb:hover {
  background: rgba(255, 255, 255, 0.5);
}

.message-row {
  display: flex;
  align-items: flex-end;
  gap: 10px;
}

.user-row {
  justify-content: flex-end;
}

.avatar-col {
  flex-shrink: 0;
  width: 36px;
}

.placeholder-avatar {
  width: 36px;
}

.msg-avatar {
  width: 36px;
  height: 36px;
  border-radius: 36px;
  object-fit: cover;
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.bubble-col {
  max-width: 76%;
  display: flex;
  flex-direction: column;
}

.bubble-wrapper {
  display: flex;
  flex-direction: column;
}

.message-bubble {
  padding: 12px 18px;
  border-radius: 22px;
  font-size: 15px;
  line-height: 1.5;
  word-break: break-word;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.message-bubble.user {
  background: rgba(79, 70, 229, 0.85);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  color: white;
  border-bottom-right-radius: 6px;
  align-self: flex-end;
  box-shadow: 0 6px 16px -4px rgba(79, 70, 229, 0.4);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.message-bubble.ai {
  border-bottom-left-radius: 6px;
}

.message-time {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.7);
  margin-top: 6px;
  margin-left: 10px;
  margin-right: 6px;
  text-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
}

.time-right {
  text-align: right;
  margin-right: 6px;
}

/* 正在输入的点 */
.typing-bubble {
  padding: 14px 22px;
}

.dot-flashing {
  position: relative;
  width: 6px;
  height: 6px;
  border-radius: 6px;
  background-color: rgba(255, 255, 255, 0.8);
  color: rgba(255, 255, 255, 0.8);
  animation: dot-flashing 1s infinite linear alternate;
  animation-delay: 0.5s;
}

.dot-flashing::before, .dot-flashing::after {
  content: '';
  display: inline-block;
  position: absolute;
  top: 0;
}

.dot-flashing::before {
  left: -12px;
  width: 6px;
  height: 6px;
  border-radius: 6px;
  background-color: rgba(255, 255, 255, 0.8);
  color: rgba(255, 255, 255, 0.8);
  animation: dot-flashing 1s infinite alternate;
  animation-delay: 0s;
}

.dot-flashing::after {
  left: 12px;
  width: 6px;
  height: 6px;
  border-radius: 6px;
  background-color: rgba(255, 255, 255, 0.8);
  color: rgba(255, 255, 255, 0.8);
  animation: dot-flashing 1s infinite alternate;
  animation-delay: 1s;
}

@keyframes dot-flashing {
  0% { background-color: rgba(255, 255, 255, 0.5); }
  50%, 100% { background-color: rgba(255, 255, 255, 0.9); }
}

/* 底部 */
.chat-footer {
  padding: 12px 18px 18px;
  display: flex;
  gap: 12px;
  align-items: center;
}

.footer-input {
  flex: 1;
  border-radius: 48px;
  padding: 14px 20px;
  font-size: 15px;
  outline: none;
  transition: 0.15s;
}

.footer-send {
  border: none;
  border-radius: 40px;
  padding: 12px 24px;
  color: white;
  font-weight: 600;
  font-size: 15px;
  cursor: pointer;
  transition: 0.15s;
}

.footer-send:disabled {
  opacity: 0.4;
  cursor: default;
  box-shadow: none;
}

/* ----- 删除确认弹窗样式 ----- */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
}

.delete-confirm-modal {
  width: 90%;
  max-width: 400px;
  border-radius: 24px;
  padding: 24px;
}

.glass-modal {
  background: rgba(255, 255, 255, 0.12);
  backdrop-filter: blur(20px) saturate(180%);
  -webkit-backdrop-filter: blur(20px) saturate(180%);
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.3);
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.modal-header h4 {
  margin: 0;
  color: #fff;
  font-size: 18px;
  font-weight: 600;
}

.modal-close {
  width: 32px;
  height: 32px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.15);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: rgba(255, 255, 255, 0.8);
  transition: all 0.15s;
}

.modal-close:hover {
  background: rgba(255, 255, 255, 0.2);
  color: #fff;
}

.modal-body {
  margin-bottom: 24px;
}

.modal-body p {
  margin: 0 0 12px 0;
  color: rgba(255, 255, 255, 0.8);
  font-size: 14px;
  line-height: 1.5;
}

.session-name-preview {
  padding: 12px 16px;
  background: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border-radius: 12px;
  color: #fff !important;
  font-weight: 500;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.modal-footer {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

.modal-footer button {
  padding: 10px 20px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.15s;
  border: none;
}

.glass-btn {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.15);
  color: rgba(255, 255, 255, 0.9);
}

.glass-btn:hover {
  background: rgba(255, 255, 255, 0.2);
}

.glass-btn-danger {
  background: rgba(239, 68, 68, 0.7);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid rgba(239, 68, 68, 0.3);
  color: #fff;
}

.glass-btn-danger:hover:not(:disabled) {
  background: rgba(239, 68, 68, 0.9);
}

.glass-btn-danger:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* ----- 提示消息样式 ----- */
.toast-message {
  position: fixed;
  top: 20px;
  left: 50%;
  transform: translateX(-50%);
  padding: 12px 24px;
  border-radius: 40px;
  font-size: 14px;
  font-weight: 500;
  z-index: 3000;
  animation: slideDown 0.3s ease;
}

.glass-toast {
  background: rgba(255, 255, 255, 0.12);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.2);
}

.toast-message.success {
  color: #10b981;
}

.toast-message.error {
  color: #ef4444;
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateX(-50%) translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateX(-50%) translateY(0);
  }
}

/* 响应式调整 */
@media (max-width: 768px) {
  .chat-sidebar {
    width: 280px;
  }
  
  .session-toggle-btn {
    right: 16px;
    bottom: 16px;
    width: 48px;
    height: 48px;
  }
}
</style>