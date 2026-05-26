<template>
  <div class="orders-container">
    <!-- 页面标题与返回 -->
    <div class="page-header glass-header">
      <div class="header-left">
        <el-button 
          type="text" 
          icon="el-icon-arrow-left" 
          class="back-btn"
          @click="goBack"
        >
          返回
        </el-button>
        <h1 class="page-title">充值记录</h1>
      </div>
      <div class="header-right">
        <el-button 
          type="primary" 
          size="small"
          icon="el-icon-plus"
          class="glass-recharge-btn"
          @click="goToRecharge"
        >
          立即充值
        </el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-cards">
      <div class="stat-card glass-stat-card">
        <div class="stat-icon total">
          <i class="el-icon-coin"></i>
        </div>
        <div class="stat-content">
          <div class="stat-label">累计充值</div>
          <div class="stat-value">¥{{ totalAmount }}</div>
        </div>
      </div>
      
      <div class="stat-card glass-stat-card">
        <div class="stat-icon points">
          <i class="el-icon-star-on"></i>
        </div>
        <div class="stat-content">
          <div class="stat-label">获得积分</div>
          <div class="stat-value">{{ totalPoints }}</div>
        </div>
      </div>
      
      <div class="stat-card glass-stat-card">
        <div class="stat-icon success">
          <i class="el-icon-circle-check"></i>
        </div>
        <div class="stat-content">
          <div class="stat-label">成功订单</div>
          <div class="stat-value">{{ successCount }}</div>
        </div>
      </div>
      
      <div class="stat-card glass-stat-card">
        <div class="stat-icon pending">
          <i class="el-icon-time"></i>
        </div>
        <div class="stat-content">
          <div class="stat-label">处理中</div>
          <div class="stat-value">{{ pendingCount }}</div>
        </div>
      </div>
    </div>

    <!-- 筛选区域 -->
    <div class="filter-section glass-section">
      <div class="filter-row">
        <div class="filter-item">
          <label>订单状态：</label>
          <el-select 
            v-model="filters.status" 
            placeholder="全部状态" 
            size="small"
            clearable
            class="glass-select"
            @change="handleFilterChange"
          >
            <el-option label="全部" value=""></el-option>
            <el-option label="充值成功" value="success"></el-option>
            <el-option label="处理中" value="pending"></el-option>
            <el-option label="已取消" value="cancelled"></el-option>
            <el-option label="已退款" value="refunded"></el-option>
          </el-select>
        </div>
        
        <div class="filter-item">
          <label>充值金额：</label>
          <el-select 
            v-model="filters.amount" 
            placeholder="全部金额" 
            size="small"
            clearable
            class="glass-select"
            @change="handleFilterChange"
          >
            <el-option label="全部" value=""></el-option>
            <el-option label="1元" value="1"></el-option>
            <el-option label="10元" value="10"></el-option>
            <el-option label="50元" value="50"></el-option>
          </el-select>
        </div>
        
        <div class="filter-item date-range">
          <label>充值时间：</label>
          <el-date-picker
            v-model="filters.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            size="small"
            value-format="yyyy-MM-dd"
            class="glass-date-picker"
            @change="handleFilterChange"
          >
          </el-date-picker>
        </div>
        
        <div class="filter-actions">
          <el-button 
            type="primary" 
            size="small"
            icon="el-icon-search"
            class="glass-filter-btn"
            @click="handleFilterChange"
          >
            查询
          </el-button>
          <el-button 
            size="small"
            icon="el-icon-refresh"
            class="glass-reset-btn"
            @click="resetFilters"
          >
            重置
          </el-button>
        </div>
      </div>
      
      <!-- 快捷筛选 -->
      <div class="quick-filters">
        <span class="quick-label">快捷筛选：</span>
        <el-button 
          :type="filters.quickDate === 'today' ? 'primary' : ''"
          size="mini"
          plain
          class="glass-quick-btn"
          @click="setQuickFilter('today')"
        >
          今日
        </el-button>
        <el-button 
          :type="filters.quickDate === 'week' ? 'primary' : ''"
          size="mini"
          plain
          class="glass-quick-btn"
          @click="setQuickFilter('week')"
        >
          本周
        </el-button>
        <el-button 
          :type="filters.quickDate === 'month' ? 'primary' : ''"
          size="mini"
          plain
          class="glass-quick-btn"
          @click="setQuickFilter('month')"
        >
          本月
        </el-button>
      </div>
    </div>

    <!-- 订单列表 -->
    <div class="orders-list glass-section">
      <!-- 表格视图 -->
      <div class="table-view">
        <el-table 
          :data="displayOrders" 
          style="width: 100%"
          class="glass-table"
          :header-cell-style="{ background: 'rgba(255,255,255,0.1)', color: '#fff', fontWeight: '600' }"
          :row-class-name="tableRowClassName"
          v-loading="loading"
        >
          <el-table-column 
            prop="orderNo" 
            label="订单号" 
            min-width="180"
          >
            <template slot-scope="scope">
              <div class="order-no-cell">
                <span class="order-no">{{ scope.row.orderNo }}</span>
                <el-tooltip 
                  content="复制订单号" 
                  placement="top"
                >
                  <i 
                    class="el-icon-document-copy copy-icon"
                    @click="copyOrderNo(scope.row.orderNo)"
                  ></i>
                </el-tooltip>
              </div>
            </template>
          </el-table-column>
          
          <el-table-column 
            prop="amount" 
            label="充值金额" 
            width="100"
            align="center"
          >
            <template slot-scope="scope">
              <span class="amount-text">¥{{ scope.row.amount }}</span>
            </template>
          </el-table-column>
          
          <el-table-column 
            prop="points" 
            label="获得积分" 
            width="160"
            align="center"
          >
            <template slot-scope="scope">
              <div class="points-cell">
                <span class="base-points">{{ scope.row.basePoints }}</span>
                <span v-if="scope.row.bonusPoints > 0" class="bonus-points">
                  +{{ scope.row.bonusPoints }}
                </span>
                <span class="total-points">
                  = {{ scope.row.totalPoints }}
                </span>
              </div>
            </template>
          </el-table-column>
          
          <el-table-column 
            prop="paymentMethod" 
            label="支付方式" 
            width="120"
            align="center"
          >
            <template slot-scope="scope">
              <div class="payment-method">
                <i :class="getPaymentIcon(scope.row.paymentMethod)"></i>
                <span>{{ getPaymentName(scope.row.paymentMethod) }}</span>
              </div>
            </template>
          </el-table-column>
          
          <el-table-column 
            prop="status" 
            label="状态" 
            width="100"
            align="center"
          >
            <template slot-scope="scope">
              <el-tag 
                :type="getStatusType(scope.row.status)"
                size="small"
                effect="plain"
                class="glass-status-tag"
              >
                {{ getStatusText(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          
          <el-table-column 
            prop="createTime" 
            label="充值时间" 
            min-width="170"
          >
            <template slot-scope="scope">
              <span class="time-text">{{ scope.row.createTime }}</span>
            </template>
          </el-table-column>
          
          <el-table-column 
            label="操作" 
            width="150"
            fixed="right"
            align="center"
          >
            <template slot-scope="scope">
              <div class="action-buttons">
                <el-button 
                  type="text" 
                  size="small"
                  class="action-btn"
                  @click="viewOrderDetail(scope.row)"
                >
                  详情
                </el-button>
                <span class="divider" v-if="scope.row.status === 'pending' || scope.row.status === 'success'">|</span>
                <el-button 
                  v-if="scope.row.status === 'pending'"
                  type="text" 
                  size="small"
                  class="action-btn warning-btn"
                  @click="cancelOrder(scope.row)"
                >
                  取消
                </el-button>
                <el-button 
                  v-if="scope.row.status === 'success'"
                  type="text" 
                  size="small"
                  class="action-btn"
                  @click="handleInvoice(scope.row)"
                >
                  发票
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
  
      <!-- 空状态 -->
      <div v-if="displayOrders.length === 0 && !loading" class="empty-state">
        <i class="el-icon-document"></i>
        <p>暂无充值记录</p>
        <el-button type="primary" size="small" class="glass-empty-btn" @click="goToRecharge">
          立即充值
        </el-button>
      </div>

      <!-- 分页 -->
      <div v-if="displayOrders.length > 0" class="pagination-wrapper">
        <el-pagination
          background
          :current-page="pagination.currentPage"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="pagination.pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="pagination.total"
          class="glass-pagination"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        >
        </el-pagination>
      </div>
    </div>

    <!-- 订单详情弹窗 -->
    <transition name="dialog-fade">
      <div 
        v-if="detailDialogVisible" 
        class="custom-dialog-mask"
        @click.self="detailDialogVisible = false"
      >
        <div class="custom-dialog glass-dialog-box">
          <div class="dialog-header">
            <span class="dialog-title">订单详情</span>
            <i class="el-icon-close dialog-close" @click="detailDialogVisible = false"></i>
          </div>
          
          <div class="dialog-body">
            <div v-if="currentOrder" class="detail-content">
              <div class="detail-header">
                <div class="order-status-badge" :class="currentOrder.status">
                  {{ getStatusText(currentOrder.status) }}
                </div>
              </div>
              
              <div class="detail-section">
                <div class="section-title">订单信息</div>
                <div class="detail-row">
                  <span class="label">订单号：</span>
                  <span class="value">{{ currentOrder.orderNo }}</span>
                </div>
                <div class="detail-row">
                  <span class="label">创建时间：</span>
                  <span class="value">{{ currentOrder.createTime }}</span>
                </div>
                <div v-if="currentOrder.payTime" class="detail-row">
                  <span class="label">支付时间：</span>
                  <span class="value">{{ currentOrder.payTime }}</span>
                </div>
              </div>
              
              <div class="detail-section">
                <div class="section-title">充值信息</div>
                <div class="detail-row highlight">
                  <span class="label">充值金额：</span>
                  <span class="value amount">¥{{ currentOrder.amount }}</span>
                </div>
                <div class="detail-row">
                  <span class="label">基础积分：</span>
                  <span class="value">{{ currentOrder.basePoints }}</span>
                </div>
                <div v-if="currentOrder.bonusPoints > 0" class="detail-row">
                  <span class="label">赠送积分：</span>
                  <span class="value bonus">+{{ currentOrder.bonusPoints }}</span>
                </div>
                <div class="detail-row total-row">
                  <span class="label">总计获得：</span>
                  <span class="value total">{{ currentOrder.totalPoints }} 积分</span>
                </div>
              </div>
              
              <div class="detail-section">
                <div class="section-title">支付信息</div>
                <div class="detail-row">
                  <span class="label">支付方式：</span>
                  <span class="value">{{ getPaymentName(currentOrder.paymentMethod) }}</span>
                </div>
                <div v-if="currentOrder.transactionId" class="detail-row">
                  <span class="label">交易号：</span>
                  <span class="value transaction-id">{{ currentOrder.transactionId }}</span>
                </div>
              </div>
            </div>
          </div>
          
          <div class="dialog-footer">
            <el-button class="cancel-btn" @click="detailDialogVisible = false">关闭</el-button>
            <el-button 
              v-if="currentOrder && currentOrder.status === 'pending'"
              type="primary"
              class="confirm-btn"
              @click="continuePay"
            >
              继续支付
            </el-button>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>

<script>
// TODO: 后续对接的API接口
// import { getOrderList, getOrderDetail, cancelOrder, getOrderStatistics } from '@/api/order'

export default {
  name: 'RechargeOrders',
  data() {
    return {
      loading: false,
      
      // 筛选条件
      filters: {
        status: '',
        amount: '',
        dateRange: null,
        quickDate: ''
      },
      
      // 分页
      pagination: {
        currentPage: 1,
        pageSize: 10,
        total: 0
      },
      
      // 统计数据（静态）
      statistics: {
        totalAmount: 1860,
        totalPoints: 2150,
        successCount: 8,
        pendingCount: 1
      },
      
      // 订单列表（静态数据，预留接口）
      orderList: [
        {
          id: 1,
          orderNo: 'RCH202601010001',
          amount: 10,
          basePoints: 10,
          bonusPoints: 5,
          totalPoints: 15,
          paymentMethod: 'wechat',
          status: 'success',
          createTime: '2026-01-01 10:30:25',
          payTime: '2026-01-01 10:31:12',
          transactionId: 'WX202601010001'
        },
        {
          id: 2,
          orderNo: 'RCH202601020001',
          amount: 50,
          basePoints: 50,
          bonusPoints: 30,
          totalPoints: 80,
          paymentMethod: 'alipay',
          status: 'success',
          createTime: '2026-01-02 14:22:10',
          payTime: '2026-01-02 14:23:05',
          transactionId: 'ALI202601020001'
        },
        {
          id: 3,
          orderNo: 'RCH202601030001',
          amount: 1,
          basePoints: 1,
          bonusPoints: 1,
          totalPoints: 2,
          paymentMethod: 'wechat',
          status: 'success',
          createTime: '2026-01-03 09:15:30',
          payTime: '2026-01-03 09:16:20',
          transactionId: 'WX202601030001'
        },
        {
          id: 4,
          orderNo: 'RCH202601040001',
          amount: 10,
          basePoints: 10,
          bonusPoints: 5,
          totalPoints: 15,
          paymentMethod: 'card',
          status: 'pending',
          createTime: '2026-01-04 16:45:22',
          payTime: null,
          transactionId: null
        }
      ],
      
      // 详情弹窗
      detailDialogVisible: false,
      currentOrder: null,
      
      // 接口预留
      apiEndpoint: {
        getOrderList: '/api/recharge/orders',
        getOrderDetail: '/api/recharge/order/detail',
        cancelOrder: '/api/recharge/order/cancel',
        getStatistics: '/api/recharge/statistics'
      }
    }
  },
  
  computed: {
    /**
     * 显示的订单列表（支持筛选和分页）
     */
    displayOrders() {
      let filtered = this.filterOrders(this.orderList)
      
      // 更新总数
      this.pagination.total = filtered.length
      
      // 分页
      const start = (this.pagination.currentPage - 1) * this.pagination.pageSize
      const end = start + this.pagination.pageSize
      
      return filtered.slice(start, end)
    },
    
    /**
     * 累计充值金额
     */
    totalAmount() {
      return this.statistics.totalAmount
    },
    
    /**
     * 累计获得积分
     */
    totalPoints() {
      return this.statistics.totalPoints
    },
    
    /**
     * 成功订单数
     */
    successCount() {
      return this.statistics.successCount
    },
    
    /**
     * 处理中订单数
     */
    pendingCount() {
      return this.statistics.pendingCount
    }
  },
  
  mounted() {
    // TODO: 后续接入真实接口
    // this.fetchOrderList()
    // this.fetchStatistics()
    
    console.log('订单页面已加载，预留接口:', this.apiEndpoint)
  },
  
  methods: {
    /**
     * 返回上一页
     */
    goBack() {
      this.$router.go(-1)
    },
    
    /**
     * 跳转到充值页面
     */
    goToRecharge() {
      this.$router.push('/blog/recharge')
    },
    
    /**
     * 筛选订单
     */
    filterOrders(orders) {
      return orders.filter(order => {
        // 状态筛选
        if (this.filters.status && order.status !== this.filters.status) {
          return false
        }
        
        // 金额筛选
        if (this.filters.amount && order.amount !== parseInt(this.filters.amount)) {
          return false
        }
        
        // 日期范围筛选
        if (this.filters.dateRange && this.filters.dateRange.length === 2) {
          const orderDate = order.createTime.split(' ')[0]
          const startDate = this.filters.dateRange[0]
          const endDate = this.filters.dateRange[1]
          
          if (orderDate < startDate || orderDate > endDate) {
            return false
          }
        }
        
        return true
      })
    },
    
    /**
     * 处理筛选变化
     */
    handleFilterChange() {
      this.pagination.currentPage = 1
      // TODO: 重新请求数据
      // this.fetchOrderList()
    },
    
    /**
     * 重置筛选条件
     */
    resetFilters() {
      this.filters = {
        status: '',
        amount: '',
        dateRange: null,
        quickDate: ''
      }
      this.pagination.currentPage = 1
      this.handleFilterChange()
    },
    
    /**
     * 设置快捷筛选
     */
    setQuickFilter(type) {
      this.filters.quickDate = type
      
      const today = new Date()
      const dateRange = []
      
      switch(type) {
        case 'today':
          const todayStr = this.formatDate(today)
          dateRange.push(todayStr, todayStr)
          break
        case 'week':
          const weekStart = new Date(today)
          weekStart.setDate(today.getDate() - today.getDay() + (today.getDay() === 0 ? -6 : 1))
          dateRange.push(this.formatDate(weekStart), this.formatDate(today))
          break
        case 'month':
          const monthStart = new Date(today.getFullYear(), today.getMonth(), 1)
          dateRange.push(this.formatDate(monthStart), this.formatDate(today))
          break
      }
      
      this.filters.dateRange = dateRange
      this.handleFilterChange()
    },
    
    /**
     * 格式化日期
     */
    formatDate(date) {
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      return `${year}-${month}-${day}`
    },
    
    /**
     * 分页大小改变
     */
    handleSizeChange(size) {
      this.pagination.pageSize = size
      this.pagination.currentPage = 1
    },
    
    /**
     * 当前页改变
     */
    handleCurrentChange(page) {
      this.pagination.currentPage = page
    },
    
    /**
     * 表格行样式
     */
    tableRowClassName({ row }) {
      if (row.status === 'pending') {
        return 'pending-row'
      }
      return ''
    },
    
    /**
     * 复制订单号
     */
    copyOrderNo(orderNo) {
      const input = document.createElement('input')
      input.value = orderNo
      document.body.appendChild(input)
      input.select()
      document.execCommand('copy')
      document.body.removeChild(input)
      this.$message.success('订单号已复制')
    },
    
    /**
     * 获取支付方式图标
     */
    getPaymentIcon(method) {
      const icons = {
        'wechat': 'el-icon-chat-round',
        'alipay': 'el-icon-bank-card',
        'card': 'el-icon-credit-card'
      }
      return icons[method] || 'el-icon-money'
    },
    
    /**
     * 获取支付方式名称
     */
    getPaymentName(method) {
      const names = {
        'wechat': '微信支付',
        'alipay': '支付宝',
        'card': '银行卡'
      }
      return names[method] || method
    },
    
    /**
     * 获取状态类型
     */
    getStatusType(status) {
      const types = {
        'success': 'success',
        'pending': 'warning',
        'cancelled': 'info',
        'refunded': 'danger'
      }
      return types[status] || 'info'
    },
    
    /**
     * 获取状态文本
     */
    getStatusText(status) {
      const texts = {
        'success': '充值成功',
        'pending': '处理中',
        'cancelled': '已取消',
        'refunded': '已退款'
      }
      return texts[status] || status
    },
    
    /**
     * 查看订单详情
     */
    viewOrderDetail(order) {
      this.currentOrder = order
      this.detailDialogVisible = true
      
      // TODO: 获取订单详情
      // this.fetchOrderDetail(order.id)
    },
    
    /**
     * 取消订单
     */
    cancelOrder(order) {
      this.$confirm('确定要取消该订单吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // TODO: 调用取消订单接口
        console.log('取消订单（预留接口）:', {
          endpoint: this.apiEndpoint.cancelOrder,
          orderId: order.id
        })
        
        // 模拟取消成功
        order.status = 'cancelled'
        this.$message.success('订单已取消')
      }).catch(() => {
        // 取消操作
      })
    },
    
    /**
     * 申请发票
     */
    handleInvoice(order) {
      this.$message.info('发票功能开发中')
      console.log('申请发票（预留功能）:', order.orderNo)
    },
    
    /**
     * 继续支付
     */
    continuePay() {
      this.detailDialogVisible = false
      this.$message.info('跳转到支付页面')
      // TODO: 跳转到支付页面
    },
    
    /**
     * 获取订单列表
     * TODO: 后续对接接口
     */
    async fetchOrderList() {
      this.loading = true
      
      console.log('获取订单列表，接口地址:', this.apiEndpoint.getOrderList)
      
      // 构建请求参数
      const params = {
        page: this.pagination.currentPage,
        pageSize: this.pagination.pageSize,
        status: this.filters.status,
        amount: this.filters.amount,
        startDate: this.filters.dateRange?.[0],
        endDate: this.filters.dateRange?.[1]
      }
      
      console.log('请求参数:', params)
      
      // try {
      //   const res = await getOrderList(params)
      //   if (res.code === 200) {
      //     this.orderList = res.data.list
      //     this.pagination.total = res.data.total
      //   }
      // } catch (error) {
      //   this.$message.error('获取订单列表失败')
      // } finally {
      //   this.loading = false
      // }
      
      // 模拟请求延迟
      setTimeout(() => {
        this.loading = false
      }, 500)
    },
    
    /**
     * 获取统计数据
     * TODO: 后续对接接口
     */
    async fetchStatistics() {
      console.log('获取统计数据，接口地址:', this.apiEndpoint.getStatistics)
      
      // try {
      //   const res = await getOrderStatistics()
      //   if (res.code === 200) {
      //     this.statistics = res.data
      //   }
      // } catch (error) {
      //   console.error('获取统计数据失败:', error)
      // }
    }
  }
}
</script>

<style lang="scss" scoped>
.orders-container {
  padding: 24px;
  background: transparent;
  min-height: 100vh;
  
  // 页面标题
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
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    
    .header-left {
      display: flex;
      align-items: center;
      gap: 16px;
      
      .back-btn {
        font-size: 14px;
        color: rgba(255, 255, 255, 0.8);
        padding: 0;
        
        &:hover {
          color: #fff;
        }
      }
      
      .page-title {
        font-size: 24px;
        font-weight: 600;
        color: #fff;
        margin: 0;
        text-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
      }
    }
    
    .glass-recharge-btn {
      background: linear-gradient(135deg, rgba(102, 126, 234, 0.9), rgba(118, 75, 162, 0.9)) !important;
      backdrop-filter: blur(10px);
      -webkit-backdrop-filter: blur(10px);
      border: 1px solid rgba(255, 255, 255, 0.2) !important;
      color: #fff !important;
      
      &:hover {
        opacity: 0.9;
      }
    }
  }
  
  // 统计卡片
  .stats-cards {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 20px;
    margin-bottom: 24px;
    
    .glass-stat-card {
      background: rgba(255, 255, 255, 0.12);
      backdrop-filter: blur(20px) saturate(180%);
      -webkit-backdrop-filter: blur(20px) saturate(180%);
      border: 1px solid rgba(255, 255, 255, 0.15);
      
      &:hover {
        background: rgba(255, 255, 255, 0.18);
        border-color: rgba(255, 255, 255, 0.25);
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
      }
    }
    
    .stat-card {
      border-radius: 12px;
      padding: 20px;
      display: flex;
      align-items: center;
      gap: 16px;
      transition: all 0.3s;
      
      .stat-icon {
        width: 56px;
        height: 56px;
        border-radius: 12px;
        display: flex;
        align-items: center;
        justify-content: center;
        
        i {
          font-size: 28px;
        }
        
        &.total {
          background: linear-gradient(135deg, rgba(102, 126, 234, 0.8), rgba(118, 75, 162, 0.8));
          color: white;
        }
        
        &.points {
          background: linear-gradient(135deg, rgba(240, 147, 251, 0.8), rgba(245, 87, 108, 0.8));
          color: white;
        }
        
        &.success {
          background: linear-gradient(135deg, rgba(79, 172, 254, 0.8), rgba(0, 242, 254, 0.8));
          color: white;
        }
        
        &.pending {
          background: linear-gradient(135deg, rgba(255, 165, 2, 0.8), rgba(255, 99, 72, 0.8));
          color: white;
        }
      }
      
      .stat-content {
        flex: 1;
        
        .stat-label {
          font-size: 14px;
          color: rgba(255, 255, 255, 0.7);
          margin-bottom: 8px;
        }
        
        .stat-value {
          font-size: 28px;
          font-weight: 700;
          color: #fff;
          text-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        }
      }
    }
  }
  
  // 筛选区域
  .glass-section {
    background: rgba(255, 255, 255, 0.12);
    backdrop-filter: blur(20px) saturate(180%);
    -webkit-backdrop-filter: blur(20px) saturate(180%);
    border: 1px solid rgba(255, 255, 255, 0.15);
    border-radius: 16px;
    padding: 20px;
    margin-bottom: 24px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  }
  
  .filter-section {
    .filter-row {
      display: flex;
      align-items: center;
      flex-wrap: wrap;
      gap: 20px;
      
      .filter-item {
        display: flex;
        align-items: center;
        gap: 8px;
        
        label {
          font-size: 14px;
          color: rgba(255, 255, 255, 0.8);
          white-space: nowrap;
        }
        
        &.date-range {
          .el-date-editor {
            width: 280px;
          }
        }
        
        ::v-deep .glass-select {
          .el-input__inner {
            background: rgba(255, 255, 255, 0.1);
            backdrop-filter: blur(10px);
            -webkit-backdrop-filter: blur(10px);
            border: 1px solid rgba(255, 255, 255, 0.2);
            color: #fff;
            
            &::placeholder {
              color: rgba(255, 255, 255, 0.5);
            }
          }
          
          .el-select__caret {
            color: rgba(255, 255, 255, 0.7);
          }
        }
        
        ::v-deep .glass-date-picker {
          .el-range-input {
            background: transparent;
            color: #fff;
          }
          
          .el-range-separator {
            color: rgba(255, 255, 255, 0.7);
          }
        }
      }
      
      .filter-actions {
        display: flex;
        gap: 12px;
        margin-left: auto;
        
        .glass-filter-btn {
          background: rgba(102, 126, 234, 0.8) !important;
          backdrop-filter: blur(10px);
          -webkit-backdrop-filter: blur(10px);
          border: 1px solid rgba(255, 255, 255, 0.2) !important;
          color: #fff !important;
          
          &:hover {
            background: rgba(102, 126, 234, 0.9) !important;
          }
        }
        
        .glass-reset-btn {
          background: rgba(255, 255, 255, 0.1) !important;
          backdrop-filter: blur(10px);
          -webkit-backdrop-filter: blur(10px);
          border: 1px solid rgba(255, 255, 255, 0.2) !important;
          color: rgba(255, 255, 255, 0.8) !important;
          
          &:hover {
            background: rgba(255, 255, 255, 0.2) !important;
            color: #fff !important;
          }
        }
      }
    }
    
    .quick-filters {
      margin-top: 16px;
      padding-top: 16px;
      border-top: 1px solid rgba(255, 255, 255, 0.15);
      display: flex;
      align-items: center;
      gap: 12px;
      
      .quick-label {
        font-size: 13px;
        color: rgba(255, 255, 255, 0.6);
      }
      
      .glass-quick-btn {
        background: rgba(255, 255, 255, 0.1) !important;
        backdrop-filter: blur(10px);
        -webkit-backdrop-filter: blur(10px);
        border: 1px solid rgba(255, 255, 255, 0.2) !important;
        color: rgba(255, 255, 255, 0.8) !important;
        
        &:hover {
          background: rgba(255, 255, 255, 0.2) !important;
          color: #fff !important;
        }
        
        &.el-button--primary {
          background: rgba(102, 126, 234, 0.8) !important;
          color: #fff !important;
        }
      }
    }
  }
  
  // 订单列表
  .orders-list {
    .table-view {
      width: 100%;
      overflow-x: auto;
      
      ::v-deep .glass-table {
        background: transparent;
        
        .el-table__header {
          th {
            background: rgba(255, 255, 255, 0.1) !important;
            backdrop-filter: blur(10px);
            -webkit-backdrop-filter: blur(10px);
            color: #fff;
            border-bottom: 1px solid rgba(255, 255, 255, 0.15);
          }
        }
        
        .el-table__body {
          tr {
            background: transparent;
            color: rgba(255, 255, 255, 0.9);
            
            &:hover > td {
              background: rgba(255, 255, 255, 0.08) !important;
            }
            
            td {
              border-bottom: 1px solid rgba(255, 255, 255, 0.1);
            }
            
            &.pending-row {
              background: rgba(255, 251, 230, 0.15);
            }
          }
        }
        
        &::before {
          background-color: transparent;
        }
      }
      
      .order-no-cell {
        display: flex;
        align-items: center;
        gap: 8px;
        
        .order-no {
          font-family: monospace;
          color: rgba(255, 255, 255, 0.8);
        }
        
        .copy-icon {
          color: rgba(255, 255, 255, 0.5);
          cursor: pointer;
          opacity: 0;
          transition: opacity 0.3s;
          font-size: 14px;
          
          &:hover {
            color: #fff;
          }
        }
        
        &:hover .copy-icon {
          opacity: 1;
        }
      }
      
      .amount-text {
        font-weight: 600;
        color: #fff;
      }
      
      .points-cell {
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 4px;
        white-space: nowrap;
        
        .base-points {
          color: rgba(255, 255, 255, 0.8);
        }
        
        .bonus-points {
          color: #ffa502;
          font-weight: 600;
        }
        
        .total-points {
          color: #52c41a;
          font-weight: 600;
          margin-left: 4px;
        }
      }
      
      .payment-method {
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 6px;
        white-space: nowrap;
        
        i {
          font-size: 16px;
          color: #a29bfe;
        }
        
        span {
          font-size: 13px;
          color: rgba(255, 255, 255, 0.8);
        }
      }
      
      .time-text {
        color: rgba(255, 255, 255, 0.6);
        font-size: 13px;
        white-space: nowrap;
      }
      
      .action-buttons {
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 0;
        height: 100%;
        
        .action-btn {
          padding: 0;
          margin: 0;
          font-size: 13px;
          color: rgba(255, 255, 255, 0.8);
          line-height: 1;
          
          &:hover {
            color: #fff;
          }
          
          &.warning-btn {
            color: #ffa502;
            
            &:hover {
              color: #ff6b6b;
            }
          }
        }
        
        .divider {
          margin: 0 8px;
          color: rgba(255, 255, 255, 0.2);
          font-size: 12px;
          line-height: 1;
        }
      }
      
      .glass-status-tag {
        background: rgba(82, 196, 26, 0.2) !important;
        backdrop-filter: blur(10px);
        -webkit-backdrop-filter: blur(10px);
        border: 1px solid rgba(82, 196, 26, 0.3) !important;
        color: #fff !important;
      }
    }
    
    .empty-state {
      text-align: center;
      padding: 60px 0;
      
      i {
        font-size: 64px;
        color: rgba(255, 255, 255, 0.3);
        margin-bottom: 16px;
      }
      
      p {
        font-size: 14px;
        color: rgba(255, 255, 255, 0.6);
        margin-bottom: 20px;
      }
      
      .glass-empty-btn {
        background: linear-gradient(135deg, rgba(102, 126, 234, 0.9), rgba(118, 75, 162, 0.9)) !important;
        backdrop-filter: blur(10px);
        -webkit-backdrop-filter: blur(10px);
        border: 1px solid rgba(255, 255, 255, 0.2) !important;
        color: #fff !important;
      }
    }
    
    .pagination-wrapper {
      margin-top: 24px;
      display: flex;
      justify-content: flex-end;
      
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
            background: rgba(102, 126, 234, 0.8) !important;
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
        
        .el-pagination__total,
        .el-pagination__sizes {
          color: rgba(255, 255, 255, 0.8) !important;
        }
        
        .el-select .el-input__inner {
          background: rgba(255, 255, 255, 0.1);
          border: 1px solid rgba(255, 255, 255, 0.2);
          color: #fff;
        }
      }
    }
  }
}

// 自定义弹窗样式
.dialog-fade-enter-active,
.dialog-fade-leave-active {
  transition: opacity 0.3s ease;
}

.dialog-fade-enter,
.dialog-fade-leave-to {
  opacity: 0;
}

.custom-dialog-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 2000;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(4px);
  -webkit-backdrop-filter: blur(4px);
}

.glass-dialog-box {
  width: 500px;
  max-width: 90vw;
  max-height: 80vh;
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(20px) saturate(180%);
  -webkit-backdrop-filter: blur(20px) saturate(180%);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 20px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.3);
  overflow: hidden;
  display: flex;
  flex-direction: column;
  
  .dialog-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 20px 24px;
    border-bottom: 1px solid rgba(255, 255, 255, 0.15);
    flex-shrink: 0;
    
    .dialog-title {
      font-size: 18px;
      font-weight: 600;
      color: #fff;
      text-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
    }
    
    .dialog-close {
      font-size: 20px;
      color: rgba(255, 255, 255, 0.7);
      cursor: pointer;
      transition: color 0.3s;
      
      &:hover {
        color: #fff;
      }
    }
  }
  
  .dialog-body {
    padding: 24px;
    overflow-y: auto;
    flex: 1;
    
    &::-webkit-scrollbar {
      width: 4px;
    }
    
    &::-webkit-scrollbar-thumb {
      background: rgba(255, 255, 255, 0.3);
      border-radius: 2px;
    }
    
    .detail-content {
      .detail-header {
        margin-bottom: 24px;
        
        .order-status-badge {
          display: inline-block;
          padding: 6px 16px;
          border-radius: 20px;
          font-size: 14px;
          font-weight: 600;
          
          &.success {
            background: rgba(82, 196, 26, 0.2);
            color: #52c41a;
            border: 1px solid rgba(82, 196, 26, 0.3);
          }
          
          &.pending {
            background: rgba(255, 152, 0, 0.2);
            color: #ffa502;
            border: 1px solid rgba(255, 152, 0, 0.3);
          }
          
          &.cancelled {
            background: rgba(140, 140, 140, 0.2);
            color: rgba(255, 255, 255, 0.6);
            border: 1px solid rgba(140, 140, 140, 0.3);
          }
          
          &.refunded {
            background: rgba(255, 77, 79, 0.2);
            color: #ff6b6b;
            border: 1px solid rgba(255, 77, 79, 0.3);
          }
        }
      }
      
      .detail-section {
        margin-bottom: 24px;
        
        &:last-child {
          margin-bottom: 0;
        }
        
        .section-title {
          font-size: 15px;
          font-weight: 600;
          color: #fff;
          margin-bottom: 16px;
          padding-bottom: 8px;
          border-bottom: 1px solid rgba(255, 255, 255, 0.15);
        }
        
        .detail-row {
          display: flex;
          justify-content: space-between;
          margin-bottom: 12px;
          font-size: 14px;
          
          .label {
            color: rgba(255, 255, 255, 0.6);
          }
          
          .value {
            color: #fff;
            font-weight: 500;
            
            &.amount {
              font-size: 18px;
              color: #a29bfe;
              font-weight: 700;
            }
            
            &.bonus {
              color: #ffa502;
              font-weight: 600;
            }
            
            &.total {
              font-size: 18px;
              color: #52c41a;
              font-weight: 700;
            }
            
            &.transaction-id {
              font-family: monospace;
              font-size: 12px;
            }
          }
          
          &.highlight {
            background: rgba(255, 255, 255, 0.08);
            padding: 8px 12px;
            border-radius: 8px;
            margin: 8px -12px;
          }
          
          &.total-row {
            margin-top: 12px;
            padding-top: 12px;
            border-top: 1px dashed rgba(255, 255, 255, 0.2);
          }
        }
      }
    }
  }
  
  .dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
    padding: 16px 24px 24px;
    border-top: 1px solid rgba(255, 255, 255, 0.15);
    flex-shrink: 0;
    
    .cancel-btn {
      background: rgba(255, 255, 255, 0.1) !important;
      backdrop-filter: blur(10px);
      -webkit-backdrop-filter: blur(10px);
      border: 1px solid rgba(255, 255, 255, 0.2) !important;
      color: rgba(255, 255, 255, 0.8) !important;
      
      &:hover {
        background: rgba(255, 255, 255, 0.2) !important;
        color: #fff !important;
      }
    }
    
    .confirm-btn {
      background: linear-gradient(135deg, rgba(102, 126, 234, 0.9), rgba(118, 75, 162, 0.9)) !important;
      backdrop-filter: blur(10px);
      -webkit-backdrop-filter: blur(10px);
      border: 1px solid rgba(255, 255, 255, 0.2) !important;
      color: #fff !important;
      
      &:hover {
        opacity: 0.9;
      }
    }
  }
}

// 响应式适配
@media (max-width: 1200px) {
  .orders-container {
    .stats-cards {
      grid-template-columns: repeat(2, 1fr);
    }
    
    .filter-section {
      .filter-row {
        .filter-item.date-range {
          .el-date-editor {
            width: 220px;
          }
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .orders-container {
    padding: 16px;
    
    .stats-cards {
      grid-template-columns: 1fr;
    }
    
    .filter-section {
      .filter-row {
        flex-direction: column;
        align-items: flex-start;
        
        .filter-item {
          width: 100%;
          
          label {
            min-width: 80px;
          }
          
          .el-select,
          .el-date-editor {
            flex: 1;
          }
        }
        
        .filter-actions {
          margin-left: 0;
          width: 100%;
          justify-content: flex-end;
        }
      }
    }
  }
}
</style>