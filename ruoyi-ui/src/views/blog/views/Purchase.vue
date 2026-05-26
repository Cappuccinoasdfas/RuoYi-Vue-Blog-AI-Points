<template>
  <div class="points-product-container">
    <!-- 页面标题 -->
    <div class="page-header glass-header">
      <h1 class="page-title">积分商城</h1>
      <p class="page-subtitle">使用积分兑换精品商品</p>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-wrap">
      <i class="el-icon-loading" style="font-size: 32px; color: rgba(255,255,255,0.8);"></i>
      <p style="color: rgba(255,255,255,0.7); margin-top: 12px;">加载中...</p>
    </div>

    <!-- 错误状态 -->
    <div v-else-if="errorMsg" class="error-wrap glass-section">
      <p class="error-text">{{ errorMsg }}</p>
      <el-button 
        type="primary" 
        class="retry-btn glass-recharge-btn"
        @click="initData"
      >
        重新加载
      </el-button>
    </div>

    <!-- 商品列表 -->
    <template v-else>
      <!-- 已购买商品区域 -->
      <div v-if="purchasedProducts.length > 0" class="product-section">
        <div class="section-header glass-header">
          <h2 class="section-title">
            <i class="el-icon-s-goods"></i>
            已购买的商品
          </h2>
          <span class="section-badge">{{ purchasedProducts.length }}个</span>
        </div>

        <div class="product-list">
          <div
            v-for="product in purchasedProducts"
            :key="'purchased-' + product.productId"
            class="product-item glass-product purchased"
            @click="handlePurchasedClick(product)"
          >
            <div class="product-info">
              <div class="product-name">
                {{ product.productName }}
                <span class="purchased-badge">已拥有</span>
              </div>
              <div class="product-desc">{{ product.description }}</div>
              <div class="product-meta">
                <span class="duration-tag">
                  {{ product.durationDays > 0 ? product.durationDays + '天有效' : '永久有效' }}
                </span>
              </div>
            </div>
            <div class="product-action">
              <span class="action-text">
                使用 <i class="el-icon-arrow-right"></i>
              </span>
            </div>
          </div>
        </div>
      </div>

      <!-- 分割线 -->
      <div 
        v-if="purchasedProducts.length > 0 && unpurchasedProducts.length > 0" 
        class="divider-wrapper"
      >
        <div class="divider-line"></div>
        <span class="divider-text">更多商品</span>
        <div class="divider-line"></div>
      </div>

      <!-- 未购买商品区域 -->
      <div v-if="unpurchasedProducts.length > 0" class="product-section">
        <div class="section-header glass-header" v-if="purchasedProducts.length === 0">
          <h2 class="section-title">
            <i class="el-icon-present"></i>
            全部商品
          </h2>
        </div>

        <div class="product-list">
          <div
            v-for="product in unpurchasedProducts"
            :key="'unpurchased-' + product.productId"
            class="product-item glass-product"
            @click="showPreview(product)"
          >
            <div class="product-info">
              <div class="product-name">{{ product.productName }}</div>
              <div class="product-desc">{{ product.description }}</div>
              <div class="product-meta">
                <span class="duration-tag">
                  {{ product.durationDays > 0 ? product.durationDays + '天' : '永久' }}
                </span>
              </div>
            </div>
            <div class="product-action">
              <span class="price-tag">
                <i class="el-icon-coin"></i>
                {{ product.price }} 积分
              </span>
            </div>
          </div>
        </div>
      </div>

      <!-- 无商品 -->
      <div v-if="allProducts.length === 0" class="empty-wrap glass-section">
        <i class="el-icon-shopping-cart-1" style="font-size: 48px; color: rgba(255,255,255,0.4);"></i>
        <p style="color: rgba(255,255,255,0.6); margin-top: 12px;">暂无商品</p>
      </div>
    </template>

    <!-- 购买预览弹窗（自定义毛玻璃弹窗） -->
    <transition name="dialog-fade">
      <div
        v-if="previewVisible"
        class="custom-dialog-mask"
        @click.self="previewVisible = false"
      >
        <div class="custom-dialog glass-dialog-box">
          <div class="dialog-header">
            <span class="dialog-title">购买预览</span>
            <i 
              class="el-icon-close dialog-close" 
              @click="previewVisible = false"
            ></i>
          </div>

          <div class="dialog-body" v-if="currentProduct">
            <div class="confirm-info glass-confirm-box">
              <div class="info-item">
                <span class="label">商品名称：</span>
                <span class="value">{{ currentProduct.productName }}</span>
              </div>
              <div class="info-item">
                <span class="label">商品描述：</span>
                <span class="value desc">{{ currentProduct.description }}</span>
              </div>
              <div class="info-item">
                <span class="label">有效期：</span>
                <span class="value">
                  {{ currentProduct.durationDays > 0 ? currentProduct.durationDays + '天' : '永久有效' }}
                </span>
              </div>
              <div class="info-item total-item">
                <span class="label">所需积分：</span>
                <span class="value total">
                  <i class="el-icon-coin"></i>
                  {{ currentProduct.price }} 积分
                </span>
              </div>
            </div>

            <div class="purchase-tip">
              <i class="el-icon-warning-outline"></i>
              点击确认后将消耗对应积分，请确认后操作
            </div>
          </div>

          <div class="dialog-footer">
            <el-button class="cancel-btn" @click="previewVisible = false">取消</el-button>
            <el-button 
              type="primary" 
              class="confirm-btn" 
              :loading="purchasing"
              @click="handlePurchase"
            >
              确认兑换
            </el-button>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>

<script>
import { getProductList, getPurchasedProducts, purchaseProduct  } from '../api/PointsProduct'

export default {
  name: 'PointsProduct',
  data() {
    return {
      loading: false,
      errorMsg: '',
      allProducts: [],
      purchasedIds: [],
      previewVisible: false,
      currentProduct: null,
      purchasing: false
    }
  },
  computed: {
    // 已购买商品
    purchasedProducts() {
      return this.allProducts.filter(item => this.purchasedIds.includes(item.productId))
    },
    // 未购买商品
    unpurchasedProducts() {
      return this.allProducts.filter(item => !this.purchasedIds.includes(item.productId))
    }
  },
  created() {
    this.initData()
  },
  methods: {
    // 初始化数据
    async initData() {
      this.loading = true
      this.errorMsg = ''

      try {
        const [productRes, purchasedRes] = await Promise.all([
          getProductList(),
          getPurchasedProducts()
        ])

        // 处理商品列表
        if (productRes.code === 200 && Array.isArray(productRes.rows)) {
          this.allProducts = productRes.rows
        } else {
          throw new Error(productRes.msg || '获取商品列表失败')
        }

        // 处理已购买列表
        if (purchasedRes.code === 200 && Array.isArray(purchasedRes.data)) {
         this.purchasedIds = purchasedRes.data.map(item => item.productId)
        } else {
          console.warn('获取已购列表格式异常:', purchasedRes)
          this.purchasedIds = []
        }
      } catch (error) {
        console.error('加载数据失败:', error)
        this.errorMsg = error.message || '加载失败，请稍后重试'
        this.$message.error('加载失败')
      } finally {
        this.loading = false
      }
    },

    // 已购买商品点击：跳转操作页面
    handlePurchasedClick(product) {
      switch (product.productType) {
        case 'personality_custom':
          this.$router.push({
            path: '/ai/personality/custom',
            query: { productId: product.productId }
          })
          break
        default:
          this.$router.push({
            path: '/points/product/use',
            query: { productId: product.productId }
          })
      }
      this.$message.success(`前往「${product.productName}」`)
    },

    // 显示购买预览弹窗
    showPreview(product) {
      this.currentProduct = { ...product }
      this.previewVisible = true
    },

async handlePurchase() {
  if (!this.currentProduct) return

  this.purchasing = true

  try {
    const productId = this.currentProduct.productId
    const res = await purchaseProduct(productId)
    
    if (res.code === 200) {
      this.$message.success(`成功兑换「${this.currentProduct.productName}」`)
      await this.refreshPurchasedList()
      this.previewVisible = false
      this.currentProduct = null
    }
    // ✅ 注意：这里不写 else 了！
    // 因为若依拦截器已经把非200的弹窗了，并且 reject 了
    // 根本走不到这里！
  } catch (error) {
    // ✅ 拦截器已经弹过"积分不足"了
    // 这里不再弹窗！只打日志
    console.error('购买失败:', error.message || error.msg)
    // 如果你怕拦截器没弹，可以加个兜底
    // 但不要重复弹！
  } finally {
    this.purchasing = false
  }
},

  // 刷新已购列表
  async refreshPurchasedList() {
    try {
      const res = await getPurchasedProducts()
      if (res.code === 200 && Array.isArray(res.data)) {
        this.purchasedIds = res.data
      }
    } catch (error) {
      console.error('刷新已购列表失败:', error)
    }
  },
}
  }
</script>

<style lang="scss" scoped>
.points-product-container {
  padding: 24px;
  background: transparent;
  min-height: 100vh;

  // 页面标题 - 毛玻璃
  .glass-header {
    background: rgba(255, 255, 255, 0.12);
    backdrop-filter: blur(20px) saturate(180%);
    -webkit-backdrop-filter: blur(20px) saturate(180%);
    border: 1px solid rgba(255, 255, 255, 0.15);
    border-radius: 16px;
    padding: 20px 24px;
  }

  .page-header {
    margin-bottom: 24px;

    .page-title {
      font-size: 28px;
      font-weight: 600;
      color: #fff;
      margin-bottom: 8px;
      text-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    }

    .page-subtitle {
      font-size: 14px;
      color: rgba(255, 255, 255, 0.8);
    }
  }

  // 加载状态
  .loading-wrap {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 80px 0;
  }

  // 错误状态
  .glass-section {
    background: rgba(255, 255, 255, 0.12);
    backdrop-filter: blur(20px) saturate(180%);
    -webkit-backdrop-filter: blur(20px) saturate(180%);
    border: 1px solid rgba(255, 255, 255, 0.15);
    border-radius: 16px;
    padding: 24px;
    margin-bottom: 24px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  }

  .error-wrap {
    text-align: center;
    padding: 40px 24px;

    .error-text {
      color: rgba(255, 255, 255, 0.8);
      font-size: 15px;
      margin-bottom: 16px;
    }
  }

  // 商品区域
  .product-section {
    margin-bottom: 8px;

    .section-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 16px;
      padding: 14px 20px;

      .section-title {
        font-size: 18px;
        font-weight: 600;
        color: #fff;
        text-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
        margin: 0;

        i {
          margin-right: 8px;
          color: rgba(255, 255, 255, 0.9);
        }
      }

      .section-badge {
        background: rgba(82, 196, 26, 0.3);
        backdrop-filter: blur(10px);
        -webkit-backdrop-filter: blur(10px);
        color: #fff;
        font-size: 12px;
        padding: 4px 14px;
        border-radius: 20px;
        border: 1px solid rgba(82, 196, 26, 0.4);
        font-weight: 600;
      }
    }
  }

  // 商品列表
  .product-list {
    display: flex;
    flex-direction: column;
    gap: 12px;
  }

  // 商品卡片 - 毛玻璃
  .glass-product {
    background: rgba(255, 255, 255, 0.1);
    backdrop-filter: blur(10px);
    -webkit-backdrop-filter: blur(10px);
    border: 1px solid rgba(255, 255, 255, 0.15);
    border-radius: 14px;
    padding: 18px 20px;
    cursor: pointer;
    transition: all 0.3s;

    &:hover {
      background: rgba(255, 255, 255, 0.18);
      border-color: rgba(255, 255, 255, 0.4);
      box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
      transform: translateY(-2px);
    }

    &.purchased {
      background: rgba(82, 196, 26, 0.15);
      border-color: rgba(82, 196, 26, 0.3);

      &:hover {
        background: rgba(82, 196, 26, 0.22);
        border-color: rgba(82, 196, 26, 0.5);
      }
    }
  }

  .product-item {
    display: flex;
    align-items: center;
    justify-content: space-between;

    .product-info {
      flex: 1;
      min-width: 0;

      .product-name {
        font-size: 16px;
        font-weight: 600;
        color: #fff;
        margin-bottom: 6px;
        display: flex;
        align-items: center;

        .purchased-badge {
          display: inline-block;
          font-size: 11px;
          font-weight: 500;
          color: #52c41a;
          background: rgba(82, 196, 26, 0.2);
          backdrop-filter: blur(10px);
          -webkit-backdrop-filter: blur(10px);
          padding: 2px 10px;
          border-radius: 12px;
          margin-left: 10px;
          border: 1px solid rgba(82, 196, 26, 0.3);
        }
      }

      .product-desc {
        font-size: 13px;
        color: rgba(255, 255, 255, 0.7);
        margin-bottom: 8px;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        overflow: hidden;
        line-height: 1.5;
      }

      .product-meta {
        .duration-tag {
          display: inline-block;
          font-size: 11px;
          color: rgba(255, 255, 255, 0.6);
          background: rgba(255, 255, 255, 0.1);
          backdrop-filter: blur(10px);
          -webkit-backdrop-filter: blur(10px);
          padding: 2px 10px;
          border-radius: 10px;
          border: 1px solid rgba(255, 255, 255, 0.1);
        }
      }
    }

    .product-action {
      margin-left: 16px;
      flex-shrink: 0;

      .action-text {
        font-size: 14px;
        color: rgba(255, 255, 255, 0.8);
        display: flex;
        align-items: center;
        gap: 4px;
        transition: color 0.3s;

        &:hover {
          color: #a29bfe;
        }
      }

      .price-tag {
        font-size: 16px;
        font-weight: 700;
        color: #ffa502;
        text-shadow: 0 1px 4px rgba(0, 0, 0, 0.2);

        i {
          margin-right: 4px;
        }
      }
    }
  }

  // 分割线
  .divider-wrapper {
    display: flex;
    align-items: center;
    margin: 28px 0 20px;
    padding: 0 8px;

    .divider-line {
      flex: 1;
      height: 1px;
      background: linear-gradient(
        to right,
        transparent,
        rgba(255, 255, 255, 0.25),
        transparent
      );
    }

    .divider-text {
      padding: 0 20px;
      font-size: 14px;
      color: rgba(255, 255, 255, 0.6);
      font-weight: 500;
      white-space: nowrap;
      text-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
    }
  }

  // 空状态
  .empty-wrap {
    text-align: center;
    padding: 60px 24px;
  }

  // 按钮样式
  .glass-recharge-btn {
    height: 40px;
    font-size: 14px;
    font-weight: 600;
    border-radius: 20px;
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.9), rgba(118, 75, 162, 0.9));
    backdrop-filter: blur(10px);
    -webkit-backdrop-filter: blur(10px);
    border: 1px solid rgba(255, 255, 255, 0.2);
    color: #fff;
    box-shadow: 0 4px 16px rgba(102, 126, 234, 0.4);

    &:hover {
      opacity: 0.95;
      transform: translateY(-1px);
    }
  }
}

// ==================== 自定义弹窗样式 ====================
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
  width: 420px;
  max-width: 90vw;
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(20px) saturate(180%);
  -webkit-backdrop-filter: blur(20px) saturate(180%);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 20px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.3);
  overflow: hidden;

  .dialog-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 20px 24px;
    border-bottom: 1px solid rgba(255, 255, 255, 0.15);

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

    .glass-confirm-box {
      background: rgba(255, 255, 255, 0.1);
      backdrop-filter: blur(10px);
      -webkit-backdrop-filter: blur(10px);
      border: 1px solid rgba(255, 255, 255, 0.15);
      border-radius: 12px;
      padding: 20px;
      margin-bottom: 20px;
    }

    .confirm-info {
      .info-item {
        display: flex;
        justify-content: space-between;
        margin-bottom: 12px;
        font-size: 15px;

        &:last-child {
          margin-bottom: 0;
        }

        .label {
          color: rgba(255, 255, 255, 0.7);
          flex-shrink: 0;
        }

        .value {
          font-weight: 600;
          color: #fff;
          text-align: right;
          word-break: break-all;

          &.desc {
            font-weight: 400;
            font-size: 13px;
            color: rgba(255, 255, 255, 0.8);
            max-width: 220px;
          }

          &.total {
            font-size: 22px;
            color: #ffa502;
            text-shadow: 0 1px 4px rgba(0, 0, 0, 0.2);

            i {
              margin-right: 6px;
            }
          }
        }

        &.total-item {
          margin-top: 12px;
          padding-top: 14px;
          border-top: 1px solid rgba(255, 255, 255, 0.2);
          margin-bottom: 0;
        }
      }
    }

    .purchase-tip {
      text-align: center;
      color: rgba(255, 255, 255, 0.7);
      font-size: 13px;

      i {
        margin-right: 4px;
        color: #ffa502;
      }
    }
  }

  .dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
    padding: 16px 24px 24px;

    .cancel-btn {
      background: rgba(255, 255, 255, 0.1) !important;
      backdrop-filter: blur(10px);
      -webkit-backdrop-filter: blur(10px);
      border: 1px solid rgba(255, 255, 255, 0.2) !important;
      color: rgba(255, 255, 255, 0.8) !important;
      border-radius: 20px;
      padding: 10px 24px;

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
      border-radius: 20px;
      padding: 10px 24px;

      &:hover {
        opacity: 0.9;
      }
    }
  }
}

// 响应式适配
@media (max-width: 768px) {
  .points-product-container {
    padding: 16px;

    .product-item {
      flex-direction: row;
    }
  }

  .glass-dialog-box {
    width: 90vw;
  }
}
</style>