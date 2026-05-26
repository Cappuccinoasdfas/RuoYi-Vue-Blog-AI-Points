<template>
  <div class="recharge-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">积分充值</h1>
      <p class="page-subtitle">选择套餐，快速获得积分</p>
    </div>

    <!-- 积分余额 -->
    <div class="balance-card">
      <div class="balance-label">我的积分</div>
      <div class="balance-value">{{ userPoints }}</div>
    </div>

    <!-- 充值套餐 -->
    <div class="package-section">
      <h2 class="section-title">充值套餐</h2>

      <div v-loading="packageLoading" class="package-list">
        <div
          v-for="pkg in packageList"
          :key="pkg.packageId"
          class="package-item"
          :class="{ recommend: pkg.isRecommend === 1 }"
        >
          <div class="package-name">{{ pkg.packageName }}</div>
          <div class="package-price">¥{{ pkg.price }}</div>

          <div class="package-points">
            <span class="base">{{ pkg.basePoints }} 积分</span>
            <span v-if="pkg.bonusPoints > 0" class="bonus">+{{ pkg.bonusPoints }}</span>
          </div>

          <div v-if="pkg.discountLabel" class="package-tag">{{ pkg.discountLabel }}</div>

          <div class="package-total">
            共得 <strong>{{ pkg.totalPoints }}</strong> 积分
          </div>
        </div>

        <div v-if="!packageLoading && packageList.length === 0" class="empty">
          暂无可选套餐
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getRechargePackages, getUserPoints, getRechargeOrders } from '../api/PointsProduct'

export default {
  name: 'Recharge',
  data() {
    return {
      userPoints: 0,
      packageList: [],
      packageLoading: false
    }
  },
  mounted() {
    this.fetchPackages()
    this.fetchUserPoints()
  },
  methods: {
    async fetchPackages() {
      this.packageLoading = true
      try {
        const res = await getRechargePackages()
        if (res.code === 200) {
          this.packageList = res.rows || []
        }
      } finally {
        this.packageLoading = false
      }
    },
  async fetchUserPoints() {
    try {
      const res = await getUserPoints()
      console.log('积分接口返回:', res)
      if (res.code === 200) {
        this.userPoints = res.data ?? 0  // ✅ 直接拿 res.data
      }
    } catch (e) {
      console.error('获取积分失败', e)
    }
  }
  }
}


</script>

<style lang="scss" scoped>
.recharge-container {
  padding: 20px;
  color: #fff;
}

// 标题
.page-header {
  margin-bottom: 24px;
  .page-title { font-size: 24px; font-weight: 600; margin: 0 0 6px; }
  .page-subtitle { font-size: 14px; color: rgba(255,255,255,0.6); margin: 0; }
}

// 积分卡片
.balance-card {
  background: linear-gradient(135deg, rgba(102,126,234,0.7), rgba(118,75,162,0.7));
  border-radius: 14px;
  padding: 20px 24px;
  margin-bottom: 28px;
  .balance-label { font-size: 13px; opacity: 0.85; margin-bottom: 6px; }
  .balance-value { font-size: 40px; font-weight: 700; }
}

// 套餐区域
.package-section {
  margin-bottom: 28px;
  .section-title { font-size: 17px; font-weight: 600; margin: 0 0 16px; }
}

.package-list {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;

  .package-item {
    background: rgba(255,255,255,0.08);
    border: 1px solid rgba(255,255,255,0.1);
    border-radius: 12px;
    padding: 20px;
    text-align: center;
    transition: all 0.25s;
    position: relative;

    &.recommend {
      border-color: rgba(255,165,2,0.5);
      box-shadow: 0 0 20px rgba(255,165,2,0.15);
    }

    .package-name { font-size: 14px; color: rgba(255,255,255,0.7); margin-bottom: 8px; }
    .package-price { font-size: 30px; font-weight: 700; margin-bottom: 10px; }

    .package-points {
      margin-bottom: 8px;
      .base { font-size: 16px; }
      .bonus { font-size: 14px; color: #ffa502; margin-left: 6px; font-weight: 600; }
    }

    .package-tag {
      display: inline-block;
      background: rgba(255,165,2,0.2);
      color: #ffa502;
      padding: 2px 10px;
      border-radius: 10px;
      font-size: 12px;
      margin-bottom: 8px;
    }

    .package-total {
      font-size: 13px;
      color: rgba(255,255,255,0.6);
      strong { color: #fff; font-size: 18px; }
    }
  }

  .empty {
    grid-column: 1 / -1;
    text-align: center;
    padding: 40px;
    color: rgba(255,255,255,0.4);
  }
}

// 表格
.order-section {
  .section-title { font-size: 17px; font-weight: 600; margin: 0 0 14px; }

  .order-table {
    background: transparent !important;
    ::v-deep .el-table__body tr {
      background: transparent !important;
      td { color: rgba(255,255,255,0.85); border-bottom: 1px solid rgba(255,255,255,0.06); }
      &:hover td { background: rgba(255,255,255,0.04) !important; }
    }
    ::v-deep .el-table__empty-text { color: rgba(255,255,255,0.4); }
    &::before { display: none; }
  }

  .bonus-tag { font-size: 12px; color: #ffa502; margin-left: 3px; }
}

// 响应式
@media (max-width: 768px) {
  .package-list {
    grid-template-columns: 1fr;
  }
}
</style>