<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="套餐名称" prop="packageName">
        <el-input
          v-model="queryParams.packageName"
          placeholder="请输入套餐名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="充值金额" prop="price">
        <el-input
          v-model="queryParams.price"
          placeholder="请输入充值金额"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="基础积分" prop="basePoints">
        <el-input
          v-model="queryParams.basePoints"
          placeholder="请输入基础积分"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="额外赠送积分" prop="bonusPoints">
        <el-input
          v-model="queryParams.bonusPoints"
          placeholder="请输入额外赠送积分"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="总获得积分" prop="totalPoints">
        <el-input
          v-model="queryParams.totalPoints"
          placeholder="请输入总获得积分"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="优惠标签" prop="discountLabel">
        <el-input
          v-model="queryParams.discountLabel"
          placeholder="请输入优惠标签"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="是否推荐 0否 1是" prop="isRecommend">
        <el-input
          v-model="queryParams.isRecommend"
          placeholder="请输入是否推荐 0否 1是"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="排序" prop="sortOrder">
        <el-input
          v-model="queryParams.sortOrder"
          placeholder="请输入排序"
          clearable
          @keyup.enter.native="handleQuery"
        />
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
          v-hasPermi="['ai_recharge_package:package:add']"
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
          v-hasPermi="['ai_recharge_package:package:edit']"
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
          v-hasPermi="['ai_recharge_package:package:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['ai_recharge_package:package:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="packageList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="套餐ID" align="center" prop="packageId" />
      <el-table-column label="套餐名称" align="center" prop="packageName" />
      <el-table-column label="充值金额" align="center" prop="price" />
      <el-table-column label="基础积分" align="center" prop="basePoints" />
      <el-table-column label="额外赠送积分" align="center" prop="bonusPoints" />
      <el-table-column label="总获得积分" align="center" prop="totalPoints" />
      <el-table-column label="优惠标签" align="center" prop="discountLabel" />
      <el-table-column label="是否推荐 0否 1是" align="center" prop="isRecommend" />
      <el-table-column label="排序" align="center" prop="sortOrder" />
      <el-table-column label="状态 0上架 1下架" align="center" prop="status" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['ai_recharge_package:package:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['ai_recharge_package:package:remove']"
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

    <!-- 添加或修改充值套餐对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="套餐名称" prop="packageName">
              <el-input v-model="form.packageName" placeholder="请输入套餐名称" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="充值金额" prop="price">
              <el-input v-model="form.price" placeholder="请输入充值金额" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="基础积分" prop="basePoints">
              <el-input v-model="form.basePoints" placeholder="请输入基础积分" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="额外赠送积分" prop="bonusPoints">
              <el-input v-model="form.bonusPoints" placeholder="请输入额外赠送积分" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="总获得积分" prop="totalPoints">
              <el-input v-model="form.totalPoints" placeholder="请输入总获得积分" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="优惠标签" prop="discountLabel">
              <el-input v-model="form.discountLabel" placeholder="请输入优惠标签" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="是否推荐 0否 1是" prop="isRecommend">
              <el-input v-model="form.isRecommend" placeholder="请输入是否推荐 0否 1是" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="排序" prop="sortOrder">
              <el-input v-model="form.sortOrder" placeholder="请输入排序" />
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
import { listPackage, getPackage, delPackage, addPackage, updatePackage } from "@/api/ai_recharge_package/package"

export default {
  name: "Package",
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
      // 充值套餐表格数据
      packageList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        packageName: null,
        price: null,
        basePoints: null,
        bonusPoints: null,
        totalPoints: null,
        discountLabel: null,
        isRecommend: null,
        sortOrder: null,
        status: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        packageName: [
          { required: true, message: "套餐名称不能为空", trigger: "blur" }
        ],
        price: [
          { required: true, message: "充值金额不能为空", trigger: "blur" }
        ],
        basePoints: [
          { required: true, message: "基础积分不能为空", trigger: "blur" }
        ],
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询充值套餐列表 */
    getList() {
      this.loading = true
      listPackage(this.queryParams).then(response => {
        this.packageList = response.rows
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
        packageId: null,
        packageName: null,
        price: null,
        basePoints: null,
        bonusPoints: null,
        totalPoints: null,
        discountLabel: null,
        isRecommend: null,
        sortOrder: null,
        status: null,
        createTime: null,
        updateTime: null
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
      this.ids = selection.map(item => item.packageId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加充值套餐"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const packageId = row.packageId || this.ids
      getPackage(packageId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改充值套餐"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.packageId != null) {
            updatePackage(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addPackage(this.form).then(response => {
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
      const packageIds = row.packageId || this.ids
      this.$modal.confirm('是否确认删除充值套餐编号为"' + packageIds + '"的数据项？').then(function() {
        return delPackage(packageIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('ai_recharge_package/package/export', {
        ...this.queryParams
      }, `package_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
