<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="所属会话ID，关联ai_chat_session.session_id" prop="sessionId">
        <el-input
          v-model="queryParams.sessionId"
          placeholder="请输入所属会话ID，关联ai_chat_session.session_id"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="用户ID，关联sys_user.user_id" prop="userId">
        <el-input
          v-model="queryParams.userId"
          placeholder="请输入用户ID，关联sys_user.user_id"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="角色：user用户消息 assistantAI回复消息" prop="role">
        <el-input
          v-model="queryParams.role"
          placeholder="请输入角色：user用户消息 assistantAI回复消息"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="消耗的token数，用于统计和计费" prop="tokens">
        <el-input
          v-model="queryParams.tokens"
          placeholder="请输入消耗的token数，用于统计和计费"
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
          v-hasPermi="['ai_chat_message:message:add']"
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
          v-hasPermi="['ai_chat_message:message:edit']"
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
          v-hasPermi="['ai_chat_message:message:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['ai_chat_message:message:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="messageList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="消息ID，主键自增" align="center" prop="messageId" />
      <el-table-column label="所属会话ID，关联ai_chat_session.session_id" align="center" prop="sessionId" />
      <el-table-column label="用户ID，关联sys_user.user_id" align="center" prop="userId" />
      <el-table-column label="角色：user用户消息 assistantAI回复消息" align="center" prop="role" />
      <el-table-column label="消息内容，用户问题或AI回答" align="center" prop="content" />
      <el-table-column label="消耗的token数，用于统计和计费" align="center" prop="tokens" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['ai_chat_message:message:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['ai_chat_message:message:remove']"
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

    <!-- 添加或修改AI聊天消息对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="所属会话ID，关联ai_chat_session.session_id" prop="sessionId">
              <el-input v-model="form.sessionId" placeholder="请输入所属会话ID，关联ai_chat_session.session_id" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="用户ID，关联sys_user.user_id" prop="userId">
              <el-input v-model="form.userId" placeholder="请输入用户ID，关联sys_user.user_id" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="角色：user用户消息 assistantAI回复消息" prop="role">
              <el-input v-model="form.role" placeholder="请输入角色：user用户消息 assistantAI回复消息" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="消息内容，用户问题或AI回答">
              <editor v-model="form.content" :min-height="192"/>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="消耗的token数，用于统计和计费" prop="tokens">
              <el-input v-model="form.tokens" placeholder="请输入消耗的token数，用于统计和计费" />
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
import { listMessage, getMessage, delMessage, addMessage, updateMessage } from "@/api/ai_chat_message/message"

export default {
  name: "Message",
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
      // AI聊天消息表格数据
      messageList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        sessionId: null,
        userId: null,
        role: null,
        content: null,
        tokens: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        sessionId: [
          { required: true, message: "所属会话ID，关联ai_chat_session.session_id不能为空", trigger: "blur" }
        ],
        userId: [
          { required: true, message: "用户ID，关联sys_user.user_id不能为空", trigger: "blur" }
        ],
        role: [
          { required: true, message: "角色：user用户消息 assistantAI回复消息不能为空", trigger: "blur" }
        ],
        content: [
          { required: true, message: "消息内容，用户问题或AI回答不能为空", trigger: "blur" }
        ],
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询AI聊天消息列表 */
    getList() {
      this.loading = true
      listMessage(this.queryParams).then(response => {
        this.messageList = response.rows
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
        messageId: null,
        sessionId: null,
        userId: null,
        role: null,
        content: null,
        tokens: null,
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
      this.ids = selection.map(item => item.messageId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加AI聊天消息"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const messageId = row.messageId || this.ids
      getMessage(messageId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改AI聊天消息"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.messageId != null) {
            updateMessage(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addMessage(this.form).then(response => {
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
      const messageIds = row.messageId || this.ids
      this.$modal.confirm('是否确认删除AI聊天消息编号为"' + messageIds + '"的数据项？').then(function() {
        return delMessage(messageIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('ai_chat_message/message/export', {
        ...this.queryParams
      }, `message_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
