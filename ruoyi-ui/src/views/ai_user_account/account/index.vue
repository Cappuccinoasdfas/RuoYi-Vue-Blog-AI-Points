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
      <el-form-item label="当前积分余额，默认0" prop="balance">
        <el-input
          v-model="queryParams.balance"
          placeholder="请输入当前积分余额，默认0"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="累计获得积分总额" prop="totalEarned">
        <el-input
          v-model="queryParams.totalEarned"
          placeholder="请输入累计获得积分总额"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="累计消费积分总额" prop="totalSpent">
        <el-input
          v-model="queryParams.totalSpent"
          placeholder="请输入累计消费积分总额"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="乐观锁版本号，每次更新+1" prop="version">
        <el-input
          v-model="queryParams.version"
          placeholder="请输入乐观锁版本号，每次更新+1"
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
          v-hasPermi="['ai_user_account:account:add']"
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
          v-hasPermi="['ai_user_account:account:edit']"
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
          v-hasPermi="['ai_user_account:account:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['ai_user_account:account:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="accountList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="账户ID，主键自增" align="center" prop="accountId" />
      <el-table-column label="用户ID，关联sys_user.user_id" align="center" prop="userId" />
      <el-table-column label="当前积分余额，默认0" align="center" prop="balance" />
      <el-table-column label="累计获得积分总额" align="center" prop="totalEarned" />
      <el-table-column label="累计消费积分总额" align="center" prop="totalSpent" />
      <el-table-column label="乐观锁版本号，每次更新+1" align="center" prop="version" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['ai_user_account:account:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['ai_user_account:account:remove']"
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

    <!-- 添加或修改用户积分账户对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="用户ID，关联sys_user.user_id" prop="userId">
              <el-input v-model="form.userId" placeholder="请输入用户ID，关联sys_user.user_id" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="当前积分余额，默认0" prop="balance">
              <el-input v-model="form.balance" placeholder="请输入当前积分余额，默认0" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="累计获得积分总额" prop="totalEarned">
              <el-input v-model="form.totalEarned" placeholder="请输入累计获得积分总额" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="累计消费积分总额" prop="totalSpent">
              <el-input v-model="form.totalSpent" placeholder="请输入累计消费积分总额" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="乐观锁版本号，每次更新+1" prop="version">
              <el-input v-model="form.version" placeholder="请输入乐观锁版本号，每次更新+1" />
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
import { listAccount, getAccount, delAccount, addAccount, updateAccount } from "@/api/ai_user_account/account"

export default {
  name: "Account",
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
      // 用户积分账户表格数据
      accountList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        userId: null,
        balance: null,
        totalEarned: null,
        totalSpent: null,
        version: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        userId: [
          { required: true, message: "用户ID，关联sys_user.user_id不能为空", trigger: "blur" }
        ],
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询用户积分账户列表 */
    getList() {
      this.loading = true
      listAccount(this.queryParams).then(response => {
        this.accountList = response.rows
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
        accountId: null,
        userId: null,
        balance: null,
        totalEarned: null,
        totalSpent: null,
        version: null,
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
      this.ids = selection.map(item => item.accountId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加用户积分账户"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const accountId = row.accountId || this.ids
      getAccount(accountId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改用户积分账户"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.accountId != null) {
            updateAccount(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addAccount(this.form).then(response => {
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
      const accountIds = row.accountId || this.ids
      this.$modal.confirm('是否确认删除用户积分账户编号为"' + accountIds + '"的数据项？').then(function() {
        return delAccount(accountIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('ai_user_account/account/export', {
        ...this.queryParams
      }, `account_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
