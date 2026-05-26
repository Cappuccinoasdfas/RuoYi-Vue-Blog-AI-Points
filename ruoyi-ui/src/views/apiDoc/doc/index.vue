<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="模块名称" prop="moduleName">
        <el-input
          v-model="queryParams.moduleName"
          placeholder="请输入模块名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="接口名称" prop="apiName">
        <el-input
          v-model="queryParams.apiName"
          placeholder="请输入接口名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="请求URL" prop="requestUrl">
        <el-input
          v-model="queryParams.requestUrl"
          placeholder="请输入请求URL"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="请求方式" prop="requestMethod">
        <el-select v-model="queryParams.requestMethod" placeholder="请选择" clearable>
          <el-option label="GET" value="GET" />
          <el-option label="POST" value="POST" />
          <el-option label="PUT" value="PUT" />
          <el-option label="DELETE" value="DELETE" />
        </el-select>
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
          v-hasPermi="['apiDoc:doc:add']"
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
          v-hasPermi="['apiDoc:doc:edit']"
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
          v-hasPermi="['apiDoc:doc:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['apiDoc:doc:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="docList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" width="70" />
      <el-table-column label="模块名称" align="center" prop="moduleName" width="120" show-overflow-tooltip />
      <el-table-column label="接口名称" align="center" prop="apiName" width="150" show-overflow-tooltip />
      <el-table-column label="请求URL" align="center" prop="requestUrl" width="200" show-overflow-tooltip />
      <el-table-column label="请求方式" align="center" prop="requestMethod" width="90">
        <template slot-scope="scope">
          <el-tag :type="getMethodTagType(scope.row.requestMethod)" size="small">
            {{ scope.row.requestMethod }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="请求参数" align="center" prop="requestParams" width="180">
        <template slot-scope="scope">
          <el-tooltip 
            v-if="scope.row.requestParams" 
            placement="top" 
            :content="formatJsonForDisplay(scope.row.requestParams)"
            popper-class="json-tooltip"
          >
            <div class="text-ellipsis">
              <i class="el-icon-document"></i>
              {{ truncateText(scope.row.requestParams, 30) }}
            </div>
          </el-tooltip>
          <span v-else class="empty-text">-</span>
        </template>
      </el-table-column>
      <el-table-column label="返回示例" align="center" prop="responseExample" width="180">
        <template slot-scope="scope">
          <el-tooltip 
            v-if="scope.row.responseExample" 
            placement="top" 
            :content="formatJsonForDisplay(scope.row.responseExample)"
            popper-class="json-tooltip"
          >
            <div class="text-ellipsis">
              <i class="el-icon-document"></i>
              {{ truncateText(scope.row.responseExample, 30) }}
            </div>
          </el-tooltip>
          <span v-else class="empty-text">-</span>
        </template>
      </el-table-column>
      <el-table-column label="排序" align="center" prop="sortOrder" width="60" />
      <el-table-column label="状态" align="center" prop="status" width="80">
        <template slot-scope="scope">
          <el-switch
            v-model="scope.row.status"
            active-value="0"
            inactive-value="1"
            @change="handleStatusChange(scope.row)"
          />
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" width="120" show-overflow-tooltip />
      <el-table-column label="操作" align="center" width="150" fixed="right">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleView(scope.row)"
          >详情</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['apiDoc:doc:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['apiDoc:doc:remove']"
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

    <!-- 添加或修改对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="700px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="模块名称" prop="moduleName">
              <el-input v-model="form.moduleName" placeholder="请输入模块名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="排序号" prop="sortOrder">
              <el-input-number v-model="form.sortOrder" :min="0" placeholder="排序号" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="接口名称" prop="apiName">
              <el-input v-model="form.apiName" placeholder="请输入接口名称/简要描述" />
            </el-form-item>
          </el-col>
          <el-col :span="16">
            <el-form-item label="请求URL" prop="requestUrl">
              <el-input v-model="form.requestUrl" placeholder="请输入请求URL" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="请求方式" prop="requestMethod">
              <el-select v-model="form.requestMethod" placeholder="请选择" style="width: 100%;">
                <el-option label="GET" value="GET" />
                <el-option label="POST" value="POST" />
                <el-option label="PUT" value="PUT" />
                <el-option label="DELETE" value="DELETE" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="请求参数" prop="requestParams">
              <el-input 
                v-model="form.requestParams" 
                type="textarea" 
                :rows="5" 
                placeholder='请输入JSON格式参数，例如：[{"name":"id","type":"Long","required":true,"desc":"文章ID"}]'
              />
              <div class="form-tip">
                <el-button type="text" size="mini" @click="formatJsonField('requestParams')">格式化JSON</el-button>
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="返回示例" prop="responseExample">
              <el-input 
                v-model="form.responseExample" 
                type="textarea" 
                :rows="6" 
                placeholder='请输入JSON格式返回示例'
              />
              <div class="form-tip">
                <el-button type="text" size="mini" @click="formatJsonField('responseExample')">格式化JSON</el-button>
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-radio-group v-model="form.status">
                <el-radio label="0">正常</el-radio>
                <el-radio label="1">停用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入备注" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="cancel">取 消</el-button>
        <el-button type="primary" @click="submitForm">确 定</el-button>
      </div>
    </el-dialog>

    <!-- 查看详情对话框 -->
    <el-dialog title="接口详情" :visible.sync="viewOpen" width="700px" append-to-body>
      <el-descriptions :column="1" border size="medium">
        <el-descriptions-item label="模块名称">{{ viewForm.moduleName }}</el-descriptions-item>
        <el-descriptions-item label="接口名称">{{ viewForm.apiName }}</el-descriptions-item>
        <el-descriptions-item label="请求URL">
          <code>{{ viewForm.requestUrl }}</code>
        </el-descriptions-item>
        <el-descriptions-item label="请求方式">
          <el-tag :type="getMethodTagType(viewForm.requestMethod)">{{ viewForm.requestMethod }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="请求参数">
          <pre class="json-view" v-if="viewForm.requestParams">{{ formatJson(viewForm.requestParams) }}</pre>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="返回示例">
          <pre class="json-view" v-if="viewForm.responseExample">{{ formatJson(viewForm.responseExample) }}</pre>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="备注">{{ viewForm.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script>
import { listDoc, getDoc, delDoc, addDoc, updateDoc } from "@/api/apiDoc/doc"

export default {
  name: "Doc",
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      docList: [],
      title: "",
      open: false,
      viewOpen: false,
      viewForm: {},
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        moduleName: null,
        apiName: null,
        requestUrl: null,
        requestMethod: null,
      },
      form: {
        status: "0",
        sortOrder: 0
      },
      rules: {
        moduleName: [
          { required: true, message: "模块名称不能为空", trigger: "blur" }
        ],
        apiName: [
          { required: true, message: "接口名称不能为空", trigger: "blur" }
        ],
        requestUrl: [
          { required: true, message: "请求URL不能为空", trigger: "blur" }
        ],
        requestMethod: [
          { required: true, message: "请选择请求方式", trigger: "change" }
        ],
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listDoc(this.queryParams).then(response => {
        this.docList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    cancel() {
      this.open = false
      this.reset()
    },
    reset() {
      this.form = {
        id: null,
        moduleName: null,
        apiName: null,
        requestUrl: null,
        requestMethod: null,
        requestParams: null,
        responseExample: null,
        sortOrder: 0,
        status: "0",
        remark: null
      }
      this.resetForm("form")
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm("queryForm")
      this.handleQuery()
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加API接口文档"
    },
    handleView(row) {
      this.viewForm = { ...row }
      this.viewOpen = true
    },
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids
      getDoc(id).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改API接口文档"
      })
    },
    handleStatusChange(row) {
      const text = row.status === "0" ? "启用" : "停用"
      this.$modal.confirm('确认要"' + text + '"该接口吗？').then(() => {
        updateDoc(row).then(() => {
          this.$modal.msgSuccess(text + "成功")
        })
      }).catch(() => {
        row.status = row.status === "0" ? "1" : "0"
      })
    },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateDoc(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addDoc(this.form).then(response => {
              this.$modal.msgSuccess("新增成功")
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    handleDelete(row) {
      const ids = row.id || this.ids
      this.$modal.confirm('是否确认删除该接口文档？').then(function() {
        return delDoc(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    handleExport() {
      this.download('apiDoc/doc/export', {
        ...this.queryParams
      }, `api_doc_${new Date().getTime()}.xlsx`)
    },
    getMethodTagType(method) {
      const typeMap = {
        'GET': 'success',
        'POST': 'warning',
        'PUT': 'primary',
        'DELETE': 'danger'
      }
      return typeMap[method] || 'info'
    },
    truncateText(text, maxLength) {
      if (!text) return ''
      const str = String(text)
      return str.length > maxLength ? str.substring(0, maxLength) + '...' : str
    },
    formatJson(jsonStr) {
      try {
        return JSON.stringify(JSON.parse(jsonStr), null, 2)
      } catch {
        return jsonStr
      }
    },
    formatJsonForDisplay(jsonStr) {
      try {
        return JSON.stringify(JSON.parse(jsonStr), null, 2)
      } catch {
        return jsonStr
      }
    },
    formatJsonField(field) {
      try {
        const parsed = JSON.parse(this.form[field])
        this.form[field] = JSON.stringify(parsed, null, 2)
        this.$message.success('格式化成功')
      } catch (e) {
        this.$message.warning('JSON格式不正确，无法格式化')
      }
    }
  }
}
</script>

<style scoped>
.text-ellipsis {
  display: flex;
  align-items: center;
  cursor: pointer;
  color: #606266;
  max-width: 160px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.text-ellipsis i {
  margin-right: 4px;
  color: #409eff;
}

.empty-text {
  color: #c0c4cc;
}

.form-tip {
  text-align: right;
  margin-top: 4px;
}

.json-view {
  background-color: #282c34;
  color: #abb2bf;
  padding: 12px;
  border-radius: 6px;
  overflow-x: auto;
  font-family: 'Consolas', 'Monaco', monospace;
  font-size: 13px;
  line-height: 1.5;
  margin: 0;
  max-height: 400px;
  overflow-y: auto;
  white-space: pre-wrap;
  word-break: break-all;
}

code {
  background-color: #f5f7fa;
  padding: 4px 8px;
  border-radius: 4px;
  font-family: 'Consolas', monospace;
  color: #409eff;
}
</style>

<style>
/* 全局样式 - tooltip 宽度 */
.json-tooltip {
  max-width: 500px !important;
}

.json-tooltip .el-tooltip__content {
  max-height: 400px;
  overflow-y: auto;
  white-space: pre-wrap;
  word-break: break-all;
  font-family: 'Consolas', 'Monaco', monospace;
  font-size: 12px;
  line-height: 1.5;
  background-color: #1e1e1e;
  color: #d4d4d4;
}
</style>