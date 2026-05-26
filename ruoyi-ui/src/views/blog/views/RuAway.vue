<!-- src/views/blog/views/RuAway.vue -->
<template>
  <div class="runaway-container">
    <!-- 顶部标题栏 - 玻璃态 -->
    <div class="page-header glass-header">
      <div class="header-left">
        <svg class="header-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M12 2L2 7L12 12L22 7L12 2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          <path d="M2 17L12 22L22 17" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          <path d="M2 12L12 17L22 12" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
        <span class="header-title">紧急逃生舱</span>
      </div>
      <div class="header-right glass-status">
        <span class="status-badge">⚠️ 危险操作</span>
      </div>
    </div>

    <!-- 主内容区域 -->
    <div class="page-content">
      <!-- 警告卡片 -->
      <div class="warning-card glass-card">
        <div class="warning-icon">
          <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M12 9V14M12 17V17.5M12 2L2 20H22L12 2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
          </svg>
        </div>
        <div class="warning-content">
          <h3 class="warning-title">请注意</h3>
          <p class="warning-text">执行此操作后，您的账户将被标记为"已跑路"状态，此操作不可撤销</p>
        </div>
      </div>

      <!-- 跑路按钮区域 -->
      <div class="button-section">
        <div class="button-wrapper">
          <div
            class="runaway-button glass-card"
            :class="{ 
              'is-pressed': isPressed, 
              'is-filled': fillProgress >= 100,
              'is-success': isSuccess,
              'is-error': isError
            }"
            @mousedown.prevent="startPress"
            @mouseup.prevent="cancelPress"
            @mouseleave.prevent="cancelPress"
            @touchstart.prevent="startPress"
            @touchend.prevent="cancelPress"
            @touchcancel.prevent="cancelPress"
          >
            <!-- 填充水波层 -->
            <div class="fill-wave" :style="{ height: fillProgress + '%' }">
              <div class="wave-effect"></div>
            </div>
            
            <!-- 按钮内容 -->
            <div class="button-content">
              <svg class="button-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M5 13H16.17L11.58 17.59L13 19L20 12L13 5L11.58 6.41L16.17 11H5V13Z" fill="currentColor"/>
              </svg>
              <span class="button-text">{{ buttonText }}</span>
            </div>
            
            <!-- 脉冲光环 -->
            <div class="pulse-ring" v-if="fillProgress >= 100 && !isTriggered"></div>
            <div class="success-ring" v-if="isSuccess"></div>
            <div class="error-ring" v-if="isError"></div>
          </div>
          
          <!-- 进度条 -->
          <div class="progress-bar-wrapper" v-if="isPressed && fillProgress < 100">
            <div class="progress-bar glass-progress">
              <div class="progress-fill" :style="{ width: fillProgress + '%' }"></div>
            </div>
            <span class="progress-text">{{ Math.floor(fillProgress) }}%</span>
          </div>
          
          <!-- 状态提示 -->
          <div class="status-message glass-status" :class="statusClass" v-if="statusMessage">
            <svg v-if="isSuccess" class="status-icon" viewBox="0 0 24 24" fill="none">
              <path d="M20 6L9 17L4 12" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            <svg v-else-if="isError" class="status-icon" viewBox="0 0 24 24" fill="none">
              <path d="M18 6L6 18M6 6L18 18" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            </svg>
            <svg v-else class="status-icon" viewBox="0 0 24 24" fill="none">
              <path d="M12 8V12M12 16H12.01M12 22C17.5228 22 22 17.5228 22 12C22 6.47715 17.5228 2 12 2C6.47715 2 2 6.47715 2 12C2 17.5228 6.47715 22 12 22Z" stroke="currentColor" stroke-width="2"/>
            </svg>
            {{ statusMessage }}
          </div>
        </div>
      </div>

      <!-- 操作说明 -->
      <div class="tips-card glass-card">
        <div class="tip-item">
          <span class="tip-number">1</span>
          <span class="tip-text">长按按钮 2 秒以确认操作</span>
        </div>
        <div class="tip-item">
          <span class="tip-number">2</span>
          <span class="tip-text">松手即可取消操作</span>
        </div>
        <div class="tip-item">
          <span class="tip-number">3</span>
          <span class="tip-text">操作成功后自动跳转首页</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { RunAwayBotton } from '../api/pthon'

export default {
  name: 'RuAway',
  data() {
    return {
      fillProgress: 0,
      isPressed: false,
      statusMessage: '',
      isTriggered: false,
      isError: false,
      isSuccess: false,
      isExecuting: false,
      pressTimer: null,
      progressTimer: null,
      triggerTimer: null
    }
  },
  computed: {
    buttonText() {
      if (this.isSuccess) return '✓ 成功'
      if (this.isError) return '✗ 失败'
      if (this.fillProgress >= 100) return '释放'
      if (this.isPressed) return '松手取消'
      return '一键跑路'
    },
    statusClass() {
      if (this.isSuccess) return 'status-success'
      if (this.isError) return 'status-error'
      return 'status-info'
    }
  },
  methods: {
    startPress(e) {
      if (e) {
        e.preventDefault()
        e.stopPropagation()
      }
      
      if (this.isTriggered || this.isExecuting) {
        this.resetButton()
      }
      
      if (this.isPressed) return
      
      this.isPressed = true
      this.fillProgress = 0
      this.isError = false
      this.isSuccess = false
      this.statusMessage = '⏳ 长按中，请勿松手...'
      
      this.clearTimers()
      
      const stepTime = 20
      const totalSteps = 2000 / stepTime
      const incrementPerStep = 100 / totalSteps
      
      this.progressTimer = setInterval(() => {
        if (this.fillProgress < 100) {
          this.fillProgress = Math.min(this.fillProgress + incrementPerStep, 100)
        }
        
        if (this.fillProgress >= 100 && !this.isTriggered && !this.isExecuting) {
          this.fillProgress = 100
          this.statusMessage = '🎯 已完成！即将执行...'
          
          if (this.progressTimer) {
            clearInterval(this.progressTimer)
            this.progressTimer = null
          }
          
          this.triggerTimer = setTimeout(() => {
            this.executeRunaway()
          }, 800)
        }
      }, stepTime)
    },
    
    cancelPress(e) {
      if (e) {
        e.preventDefault()
        e.stopPropagation()
      }
      
      if (!this.isPressed && !this.isTriggered) return
      
      this.isPressed = false
      
      if (!this.isTriggered && !this.isExecuting) {
        if (this.fillProgress < 100) {
          this.statusMessage = '❌ 已取消'
          setTimeout(() => {
            if (!this.isPressed && !this.isTriggered) {
              this.statusMessage = ''
              this.isError = false
              this.isSuccess = false
            }
          }, 1000)
        }
        this.fillProgress = 0
      }
      
      this.clearTimers()
    },
    
    clearTimers() {
      if (this.pressTimer) {
        clearTimeout(this.pressTimer)
        this.pressTimer = null
      }
      if (this.progressTimer) {
        clearInterval(this.progressTimer)
        this.progressTimer = null
      }
      if (this.triggerTimer) {
        clearTimeout(this.triggerTimer)
        this.triggerTimer = null
      }
    },
    
    async executeRunaway() {
      if (this.isTriggered || this.isExecuting) return
      
      this.isTriggered = true
      this.isExecuting = true
      this.statusMessage = '🚀 正在执行跑路操作...'
      this.isError = false
      this.isSuccess = false
      
      try {
        const response = await RunAwayBotton()
        
        if (response.code === 200) {
          const successMsg = response.msg || '执行成功'
          this.statusMessage = `✅ ${successMsg}`
          this.isSuccess = true
          this.isError = false
          this.$message.success(successMsg)
          
          setTimeout(() => {
            this.$router.push('/blog/home')
          }, 2000)
        } else {
          const errorMsg = response.msg || '执行失败'
          this.statusMessage = `❌ ${errorMsg}`
          this.isError = true
          this.isSuccess = false
          this.$message.error(errorMsg)
          
          setTimeout(() => {
            this.resetButton()
          }, 2000)
        }
        
      } catch (error) {
        let errorMsg = '执行失败，请重试'
        
        if (error.response && error.response.data) {
          errorMsg = error.response.data.msg || errorMsg
        } else if (error.message) {
          errorMsg = error.message
        }
        
        this.statusMessage = `❌ ${errorMsg}`
        this.isError = true
        this.isSuccess = false
        this.$message.error(errorMsg)
        
        setTimeout(() => {
          this.resetButton()
        }, 2000)
      } finally {
        this.isExecuting = false
      }
    },
    
    resetButton() {
      this.isPressed = false
      this.fillProgress = 0
      this.isTriggered = false
      this.isExecuting = false
      this.statusMessage = ''
      this.isError = false
      this.isSuccess = false
      this.clearTimers()
    }
  },
  beforeDestroy() {
    this.clearTimers()
  }
}
</script>

<style scoped>
/* 容器 */
.runaway-container {
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  height: 92vh;
  display: flex;
  flex-direction: column;
  padding: 16px 20px;
  box-sizing: border-box;
  gap: 16px;
  background: transparent;
}

/* ----- 玻璃态通用样式 ----- */
.glass-card {
  background: rgba(255, 255, 255, 0.12);
  backdrop-filter: blur(20px) saturate(180%);
  -webkit-backdrop-filter: blur(20px) saturate(180%);
  border: 1px solid rgba(255, 255, 255, 0.15);
  border-radius: 24px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1),
              inset 0 1px 0 rgba(255, 255, 255, 0.1);
}

.glass-header {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(20px) saturate(180%);
  -webkit-backdrop-filter: blur(20px) saturate(180%);
  border-bottom: 1px solid rgba(255, 255, 255, 0.15);
}

.glass-status {
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 40px;
  padding: 6px 16px;
  color: #fff;
  font-size: 14px;
}

.glass-progress {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.15);
  border-radius: 20px;
}

/* ----- 顶部标题栏 ----- */
.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 24px;
  border-radius: 20px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-icon {
  width: 28px;
  height: 28px;
  color: #fff;
  opacity: 0.9;
}

.header-title {
  font-size: 22px;
  font-weight: 600;
  color: #fff;
  letter-spacing: -0.01em;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.header-right {
  display: flex;
  align-items: center;
}

.status-badge {
  font-weight: 500;
  color: #fbbf24;
}

/* ----- 主内容区域 ----- */
.page-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 24px;
  max-width: 500px;
  margin: 0 auto;
  width: 100%;
}

/* 警告卡片 */
.warning-card {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 20px 24px;
  width: 100%;
  background: rgba(245, 158, 11, 0.15);
  border-color: rgba(245, 158, 11, 0.3);
}

.warning-icon {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  background: rgba(245, 158, 11, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fbbf24;
}

.warning-icon svg {
  width: 24px;
  height: 24px;
}

.warning-content {
  flex: 1;
}

.warning-title {
  font-size: 16px;
  font-weight: 600;
  color: #fbbf24;
  margin-bottom: 6px;
}

.warning-text {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
  line-height: 1.5;
  margin: 0;
}

/* 按钮区域 */
.button-section {
  width: 100%;
  display: flex;
  justify-content: center;
  padding: 20px 0;
}

.button-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
}

.runaway-button {
  position: relative;
  width: 240px;
  height: 240px;
  border-radius: 50%;
  cursor: pointer;
  user-select: none;
  overflow: hidden;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.runaway-button:hover {
  transform: scale(1.02);
  background: rgba(255, 255, 255, 0.15);
}

.runaway-button.is-pressed {
  transform: scale(0.98);
}

.runaway-button.is-filled {
  border-color: rgba(64, 224, 208, 0.5);
}

.runaway-button.is-success {
  border-color: rgba(34, 197, 94, 0.5);
  background: rgba(34, 197, 94, 0.15);
}

.runaway-button.is-error {
  border-color: rgba(239, 68, 68, 0.5);
  background: rgba(239, 68, 68, 0.15);
}

/* 填充水波 */
.fill-wave {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 0%;
  background: linear-gradient(180deg, 
    rgba(64, 224, 208, 0.3) 0%, 
    rgba(64, 224, 208, 0.6) 100%);
  transition: height 0.05s linear;
}

.wave-effect {
  position: absolute;
  top: -10px;
  left: 0;
  width: 200%;
  height: 20px;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  animation: wave 2s ease-in-out infinite;
}

@keyframes wave {
  0%, 100% { transform: translateX(-25%); }
  50% { transform: translateX(0%); }
}

/* 按钮内容 */
.button-content {
  position: relative;
  z-index: 2;
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
}

.button-icon {
  width: 48px;
  height: 48px;
  color: #fff;
  opacity: 0.9;
  filter: drop-shadow(0 2px 8px rgba(0, 0, 0, 0.2));
}

.button-text {
  font-size: 28px;
  font-weight: 700;
  color: #fff;
  text-shadow: 0 2px 12px rgba(0, 0, 0, 0.3);
  letter-spacing: 4px;
}

/* 脉冲光环 */
.pulse-ring {
  position: absolute;
  top: -8px;
  left: -8px;
  right: -8px;
  bottom: -8px;
  border-radius: 50%;
  border: 3px solid rgba(64, 224, 208, 0.6);
  animation: pulse 1.2s ease-in-out infinite;
  pointer-events: none;
}

.success-ring {
  position: absolute;
  top: -8px;
  left: -8px;
  right: -8px;
  bottom: -8px;
  border-radius: 50%;
  border: 3px solid rgba(34, 197, 94, 0.6);
  animation: successPulse 1.5s ease-in-out infinite;
  pointer-events: none;
}

.error-ring {
  position: absolute;
  top: -8px;
  left: -8px;
  right: -8px;
  bottom: -8px;
  border-radius: 50%;
  border: 3px solid rgba(239, 68, 68, 0.6);
  animation: errorShake 0.5s ease-in-out;
  pointer-events: none;
}

@keyframes pulse {
  0%, 100% { 
    opacity: 0.6; 
    transform: scale(1); 
  }
  50% { 
    opacity: 1; 
    transform: scale(1.03); 
  }
}

@keyframes successPulse {
  0%, 100% { 
    opacity: 0.4; 
    transform: scale(1); 
  }
  50% { 
    opacity: 0.8; 
    transform: scale(1.05); 
  }
}

@keyframes errorShake {
  0%, 100% { transform: translateX(0); }
  25% { transform: translateX(-6px); }
  75% { transform: translateX(6px); }
}

/* 进度条 */
.progress-bar-wrapper {
  display: flex;
  align-items: center;
  gap: 12px;
  width: 100%;
}

.progress-bar {
  flex: 1;
  height: 8px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #40e0d0, #5eead4);
  border-radius: 20px;
  transition: width 0.05s linear;
}

.progress-text {
  min-width: 48px;
  text-align: right;
  font-size: 14px;
  font-weight: 600;
  color: #5eead4;
}

/* 状态消息 */
.status-message {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  font-size: 15px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.status-icon {
  width: 20px;
  height: 20px;
}

.status-message.status-success {
  background: rgba(34, 197, 94, 0.2);
  border-color: rgba(34, 197, 94, 0.4);
  color: #86efac;
}

.status-message.status-error {
  background: rgba(239, 68, 68, 0.2);
  border-color: rgba(239, 68, 68, 0.4);
  color: #fca5a5;
}

.status-message.status-info {
  background: rgba(59, 130, 246, 0.2);
  border-color: rgba(59, 130, 246, 0.4);
  color: #93c5fd;
}

/* 操作说明 */
.tips-card {
  display: flex;
  justify-content: space-around;
  padding: 20px 24px;
  width: 100%;
}

.tip-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.tip-number {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.15);
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  color: #fff;
}

.tip-text {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
}

/* 响应式 */
@media (max-width: 480px) {
  .runaway-button {
    width: 200px;
    height: 200px;
  }
  
  .button-text {
    font-size: 24px;
  }
  
  .button-icon {
    width: 40px;
    height: 40px;
  }
  
  .tips-card {
    flex-direction: column;
    gap: 16px;
  }
  
  .tip-item {
    justify-content: center;
  }
}
</style>