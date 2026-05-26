<template>
  <div class="api-doc-container">
    <!-- 顶部标题栏 - 玻璃态 -->
    <div class="api-header glass-header">
      <div class="header-left">
        <svg class="header-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M4 6H20M4 12H20M4 18H20" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
        </svg>
        <span class="header-title">API 接口文档</span>
      </div>
      <div class="header-right glass-status">
        <span class="api-count">{{ total }} 个接口</span>
      </div>
    </div>

    <!-- 搜索栏 - 玻璃态 -->
    <div class="search-section glass-card">
      <div class="search-row">
        <div class="search-input-wrapper glass-input-wrapper">
          <svg class="search-icon" viewBox="0 0 24 24" fill="none">
            <path d="M21 21L15 15M17 10C17 13.866 13.866 17 10 17C6.13401 17 3 13.866 3 10C3 6.13401 6.13401 3 10 3C13.866 3 17 6.13401 17 10Z" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
          </svg>
          <input
            v-model="queryParams.moduleName"
            type="text"
            placeholder="模块名称"
            @keypress.enter="handleQuery"
            class="glass-input"
          />
        </div>
        <div class="search-input-wrapper glass-input-wrapper">
          <svg class="search-icon" viewBox="0 0 24 24" fill="none">
            <path d="M21 21L15 15M17 10C17 13.866 13.866 17 10 17C6.13401 17 3 13.866 3 10C3 6.13401 6.13401 3 10 3C13.866 3 17 6.13401 17 10Z" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
          </svg>
          <input
            v-model="queryParams.apiName"
            type="text"
            placeholder="接口名称"
            @keypress.enter="handleQuery"
            class="glass-input"
          />
        </div>
        <button @click="handleQuery" class="search-btn glass-btn">
          <svg viewBox="0 0 24 24" fill="none">
            <path d="M21 21L15 15M17 10C17 13.866 13.866 17 10 17C6.13401 17 3 13.866 3 10C3 6.13401 6.13401 3 10 3C13.866 3 17 6.13401 17 10Z" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
          </svg>
          <span>搜索</span>
        </button>
        <button @click="resetQuery" class="reset-btn glass-btn-outline">
          <svg viewBox="0 0 24 24" fill="none">
            <path d="M4 4V10H10M20 20V14H14M4 10C4 14.4183 7.58172 18 12 18C16.4183 18 20 14.4183 20 10C20 5.58172 16.4183 2 12 2C7.58172 2 4 5.58172 4 10Z" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
          </svg>
          <span>重置</span>
        </button>
      </div>
    </div>

    <!-- 接口列表区域 -->
    <div class="api-list-section">
      <div class="api-list" ref="apiList">
        <div
          v-for="item in apiDocList"
          :key="item.id"
          class="api-item glass-card"
          :class="{ 'is-expanded': expandedId === item.id }"
        >
          <!-- 折叠横条 -->
          <div class="api-item-header" @click="toggleExpand(item.id)">
            <div class="api-method-tag" :class="getMethodClass(item.requestMethod)">
              {{ item.requestMethod }}
            </div>
            <div class="api-info">
              <span class="api-name">{{ item.apiName }}</span>
              <code class="api-url">{{ item.requestUrl }}</code>
            </div>
            <div class="api-expand-icon" :class="{ rotated: expandedId === item.id }">
              <svg viewBox="0 0 24 24" fill="none">
                <path d="M6 9L12 15L18 9" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
          </div>

          <!-- 展开详情 -->
          <transition name="slide-fade">
            <div v-if="expandedId === item.id" class="api-item-detail">
              <div class="detail-row">
                <span class="detail-label">模块名称</span>
                <span class="detail-value">{{ item.moduleName }}</span>
              </div>
              <div class="detail-row">
                <span class="detail-label">请求参数</span>
                <div class="detail-json" v-if="item.requestParams">
                  <pre class="json-view">{{ formatJson(item.requestParams) }}</pre>
                </div>
                <span v-else class="detail-empty">无参数</span>
              </div>
              <div class="detail-row">
                <span class="detail-label">返回示例</span>
                <div class="detail-json" v-if="item.responseExample">
                  <pre class="json-view">{{ formatJson(item.responseExample) }}</pre>
                </div>
                <span v-else class="detail-empty">-</span>
              </div>
              <div class="detail-row" v-if="item.remark">
                <span class="detail-label">备注</span>
                <span class="detail-value">{{ item.remark }}</span>
              </div>
            </div>
          </transition>
        </div>

        <!-- 空状态 -->
        <div v-if="apiDocList.length === 0 && !loading" class="empty-state glass-card">
          <svg viewBox="0 0 24 24" fill="none" class="empty-icon">
            <path d="M4 6H20M4 12H20M4 18H20" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
          </svg>
          <span>暂无接口文档</span>
        </div>
      </div>
    </div>

    <!-- 分页 - 玻璃态 -->
    <div class="pagination-section glass-footer" v-if="total > 0">
      <div class="pagination-info">
        第 {{ queryParams.pageNum }} / {{ Math.ceil(total / queryParams.pageSize) }} 页
      </div>
      <div class="pagination-controls">
        <button
          class="page-btn glass-btn-icon"
          :disabled="queryParams.pageNum <= 1"
          @click="changePage(queryParams.pageNum - 1)"
        >
          <svg viewBox="0 0 24 24" fill="none">
            <path d="M15 18L9 12L15 6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </button>
        <span class="page-current glass-status">{{ queryParams.pageNum }}</span>
        <button
          class="page-btn glass-btn-icon"
          :disabled="queryParams.pageNum >= Math.ceil(total / queryParams.pageSize)"
          @click="changePage(queryParams.pageNum + 1)"
        >
          <svg viewBox="0 0 24 24" fill="none">
            <path d="M9 18L15 12L9 6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { listApiDoc } from '../api/pthon'

export default {
  name: 'ApiDoc',
  data() {
    return {
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        moduleName: '',
        apiName: ''
      },
      apiDocList: [],
      total: 0,
      loading: false,
      expandedId: null
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getMethodClass(method) {
      const classMap = {
        'GET': 'method-get',
        'POST': 'method-post',
        'PUT': 'method-put',
        'DELETE': 'method-delete'
      }
      return classMap[method] || 'method-default'
    },

    formatJson(jsonStr) {
      try {
        return JSON.stringify(JSON.parse(jsonStr), null, 2)
      } catch {
        return jsonStr
      }
    },

    getList() {
      this.loading = true
      listApiDoc(this.queryParams).then(res => {
        this.apiDocList = res.rows || []
        this.total = res.total || 0
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },

    handleQuery() {
      this.queryParams.pageNum = 1
      this.expandedId = null
      this.getList()
    },

    resetQuery() {
      this.queryParams.moduleName = ''
      this.queryParams.apiName = ''
      this.handleQuery()
    },

    toggleExpand(id) {
      this.expandedId = this.expandedId === id ? null : id
    },

    changePage(page) {
      this.queryParams.pageNum = page
      this.expandedId = null
      this.getList()
      this.$refs.apiList?.scrollIntoView({ behavior: 'smooth', block: 'start' })
    }
  }
}
</script>

<style scoped>
/* 容器 */
.api-doc-container {
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
  border-radius: 20px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1),
              inset 0 1px 0 rgba(255, 255, 255, 0.1);
}

.glass-header {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(20px) saturate(180%);
  -webkit-backdrop-filter: blur(20px) saturate(180%);
  border-bottom: 1px solid rgba(255, 255, 255, 0.15);
}

.glass-footer {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(20px) saturate(180%);
  -webkit-backdrop-filter: blur(20px) saturate(180%);
  border-top: 1px solid rgba(255, 255, 255, 0.15);
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

.glass-input-wrapper {
  background: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.15);
  border-radius: 40px;
  display: flex;
  align-items: center;
  padding: 0 16px;
  transition: all 0.2s;
}

.glass-input-wrapper:focus-within {
  border-color: rgba(255, 255, 255, 0.4);
  background: rgba(255, 255, 255, 0.12);
}

.glass-input {
  background: transparent;
  border: none;
  padding: 12px 0;
  font-size: 15px;
  outline: none;
  color: #fff;
  width: 100%;
}

.glass-input::placeholder {
  color: rgba(255, 255, 255, 0.5);
}

.glass-btn {
  background: rgba(79, 70, 229, 0.7);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 40px;
  padding: 12px 24px;
  display: flex;
  align-items: center;
  gap: 8px;
  color: white;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: 0 6px 16px -4px rgba(79, 70, 229, 0.3);
}

.glass-btn:hover {
  background: rgba(99, 90, 249, 0.8);
  transform: translateY(-1px);
}

.glass-btn-outline {
  background: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 40px;
  padding: 12px 24px;
  display: flex;
  align-items: center;
  gap: 8px;
  color: #fff;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.glass-btn-outline:hover {
  background: rgba(255, 255, 255, 0.15);
}

.glass-btn-icon {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.15);
  border-radius: 40px;
  width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  cursor: pointer;
  transition: all 0.2s;
}

.glass-btn-icon:hover:not(:disabled) {
  background: rgba(255, 255, 255, 0.2);
}

.glass-btn-icon:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

/* ----- 顶部标题栏 ----- */
.api-header {
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

.api-count {
  font-weight: 500;
}

/* ----- 搜索区域 ----- */
.search-section {
  padding: 20px 24px;
}

.search-row {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}

.search-input-wrapper {
  flex: 1;
  min-width: 200px;
}

.search-icon {
  width: 20px;
  height: 20px;
  color: rgba(255, 255, 255, 0.6);
  margin-right: 8px;
}

.search-btn svg,
.reset-btn svg {
  width: 18px;
  height: 18px;
}

/* ----- 接口列表区域 ----- */
.api-list-section {
  flex: 1;
  overflow-y: auto;
  padding-right: 4px;
}

.api-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.api-list-section::-webkit-scrollbar {
  width: 4px;
}

.api-list-section::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 2px;
}

.api-list-section::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.2);
  border-radius: 2px;
}

/* 接口卡片 */
.api-item {
  padding: 0;
  overflow: hidden;
  transition: all 0.3s ease;
}

.api-item.is-expanded {
  background: rgba(255, 255, 255, 0.15);
}

.api-item-header {
  display: flex;
  align-items: center;
  padding: 16px 20px;
  cursor: pointer;
  user-select: none;
  transition: background 0.2s;
}

.api-item-header:hover {
  background: rgba(255, 255, 255, 0.05);
}

.api-method-tag {
  min-width: 75px;
  padding: 6px 12px;
  border-radius: 30px;
  font-size: 13px;
  font-weight: 600;
  text-align: center;
  letter-spacing: 0.5px;
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
}

.method-get {
  background: rgba(16, 185, 129, 0.25);
  color: #6ee7b7;
  border: 1px solid rgba(16, 185, 129, 0.4);
}

.method-post {
  background: rgba(245, 158, 11, 0.25);
  color: #fcd34d;
  border: 1px solid rgba(245, 158, 11, 0.4);
}

.method-put {
  background: rgba(59, 130, 246, 0.25);
  color: #93c5fd;
  border: 1px solid rgba(59, 130, 246, 0.4);
}

.method-delete {
  background: rgba(239, 68, 68, 0.25);
  color: #fca5a5;
  border: 1px solid rgba(239, 68, 68, 0.4);
}

.method-default {
  background: rgba(255, 255, 255, 0.15);
  color: #e5e7eb;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.api-info {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 20px;
  margin-left: 20px;
}

.api-name {
  font-size: 16px;
  font-weight: 500;
  color: #fff;
  text-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
}

.api-url {
  font-family: 'SF Mono', 'Monaco', 'Inconsolata', 'Fira Code', monospace;
  font-size: 14px;
  background: rgba(0, 0, 0, 0.15);
  padding: 4px 14px;
  border-radius: 30px;
  color: rgba(255, 255, 255, 0.85);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.api-expand-icon {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 32px;
  background: rgba(255, 255, 255, 0.1);
  transition: transform 0.3s;
  color: #fff;
}

.api-expand-icon svg {
  width: 20px;
  height: 20px;
}

.api-expand-icon.rotated {
  transform: rotate(180deg);
}

/* 展开详情 */
.api-item-detail {
  padding: 20px 24px 24px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  background: rgba(0, 0, 0, 0.1);
}

.detail-row {
  margin-bottom: 20px;
}

.detail-row:last-child {
  margin-bottom: 0;
}

.detail-label {
  display: block;
  font-size: 13px;
  font-weight: 500;
  color: rgba(255, 255, 255, 0.6);
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin-bottom: 8px;
}

.detail-value {
  color: #fff;
  font-size: 15px;
}

.detail-empty {
  color: rgba(255, 255, 255, 0.4);
  font-size: 14px;
}

.detail-json {
  background: rgba(0, 0, 0, 0.3);
  border-radius: 16px;
  padding: 16px;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.json-view {
  margin: 0;
  font-family: 'SF Mono', 'Monaco', 'Inconsolata', 'Fira Code', monospace;
  font-size: 13px;
  line-height: 1.6;
  color: #e5e7eb;
  overflow-x: auto;
  white-space: pre-wrap;
  word-break: break-all;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 40px;
  gap: 16px;
  color: rgba(255, 255, 255, 0.5);
}

.empty-icon {
  width: 48px;
  height: 48px;
  opacity: 0.5;
}

/* 分页 */
.pagination-section {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 24px;
  border-radius: 20px;
}

.pagination-info {
  color: rgba(255, 255, 255, 0.8);
  font-size: 14px;
}

.pagination-controls {
  display: flex;
  align-items: center;
  gap: 12px;
}

.page-current {
  min-width: 40px;
  text-align: center;
}

/* 动画 */
.slide-fade-enter-active {
  transition: all 0.3s ease;
}

.slide-fade-leave-active {
  transition: all 0.2s ease;
}

.slide-fade-enter-from,
.slide-fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}
</style>