<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="用户ID，关联sys_user.user_id" prop="userId">
        <el-input
          v-model="queryParams.userId"
          placeholder="请输入用户ID，关联sys_user.user_id"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="商品ID，关联ai_points_product.product_id" prop="productId">
        <el-input
          v-model="queryParams.productId"
          placeholder="请输入商品ID，关联ai_points_product.product_id"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="购买时消耗的积分数" prop="pointsCost">
        <el-input
          v-model="queryParams.pointsCost"
          placeholder="请输入购买时消耗的积分数"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="过期时间，NULL表示永久有效" prop="expireTime">
        <el-date-picker clearable
          v-model="queryParams.expireTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择过期时间，NULL表示永久有效">
        </el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['ai_user_purchase:purchase:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['ai_user_purchase:purchase:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['ai_user_purchase:purchase:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['ai_user_purchase:purchase:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="purchaseList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="购买记录ID，主键自增" align="center" prop="purchaseId" />
      <el-table-column label="用户ID，关联sys_user.user_id" align="center" prop="userId" />
      <el-table-column label="商品ID，关联ai_points_product.product_id" align="center" prop="productId" />
      <el-table-column label="购买时消耗的积分数" align="center" prop="pointsCost" />
      <el-table-column label="过期时间，NULL表示永久有效" align="center" prop="expireTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.expireTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态：0已失效 1有效" align="center" prop="status" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['ai_user_purchase:purchase:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['ai_user_purchase:purchase:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改用户购买记录对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="用户ID，关联sys_user.user_id" prop="userId">
              <el-input v-model="form.userId" placeholder="请输入用户ID，关联sys_user.user_id" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="商品ID，关联ai_points_product.product_id" prop="productId">
              <el-input v-model="form.productId" placeholder="请输入商品ID，关联ai_points_product.product_id" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="购买时消耗的积分数" prop="pointsCost">
              <el-input v-model="form.pointsCost" placeholder="请输入购买时消耗的积分数" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="过期时间，NULL表示永久有效" prop="expireTime">
              <el-date-picker clearable
                v-model="form.expireTime"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="请选择过期时间，NULL表示永久有效">
              </el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listPurchase, getPurchase, delPurchase, addPurchase, updatePurchase } from "@/api/ai_user_purchase/purchase"

export default {
  name: "Purchase",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 用户购买记录表格数据
      purchaseList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        userId: null,
        productId: null,
        pointsCost: null,
        expireTime: null,
        status: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        userId: [
          { required: true, message: "用户ID，关联sys_user.user_id不能为空", trigger: "blur" }
        ],
        productId: [
          { required: true, message: "商品ID，关联ai_points_product.product_id不能为空", trigger: "blur" }
        ],
        pointsCost: [
          { required: true, message: "购买时消耗的积分数不能为空", trigger: "blur" }
        ],
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询用户购买记录列表 */
    getList() {
      this.loading = true
      listPurchase(this.queryParams).then(response => {
        this.purchaseList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    // 取消按钮
    cancel() {
      this.open = false
      this.reset()
    },
    // 表单重置
    reset() {
      this.form = {
        purchaseId: null,
        userId: null,
        productId: null,
        pointsCost: null,
        expireTime: null,
        status: null,
        createTime: null
      }
      this.resetForm("form")
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm")
      this.handleQuery()
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.purchaseId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加用户购买记录"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const purchaseId = row.purchaseId || this.ids
      getPurchase(purchaseId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改用户购买记录"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.purchaseId != null) {
            updatePurchase(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addPurchase(this.form).then(response => {
              this.$modal.msgSuccess("新增成功")
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const purchaseIds = row.purchaseId || this.ids
      this.$modal.confirm('是否确认删除用户购买记录编号为"' + purchaseIds + '"的数据项？').then(function() {
        return delPurchase(purchaseIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('ai_user_purchase/purchase/export', {
        ...this.queryParams
      }, `purchase_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
