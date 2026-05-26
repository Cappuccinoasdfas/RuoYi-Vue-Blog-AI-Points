<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="文章标题" prop="title">
        <el-input
          v-model="queryParams.title"
          placeholder="请输入文章标题"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="分类" prop="category">
        <el-input
          v-model="queryParams.category"
          placeholder="请输入分类"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="作者ID" prop="userId">
        <el-input
          v-model="queryParams.userId"
          placeholder="请输入作者ID"
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
          v-hasPermi="['blog_article:article:add']"
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
          v-hasPermi="['blog_article:article:edit']"
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
          v-hasPermi="['blog_article:article:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['blog_article:article:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="articleList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="文章ID" align="center" prop="id" width="70" />
      
      <!-- 文章标题：带省略号和悬停提示 -->
      <el-table-column label="文章标题" align="center" prop="title" width="180" show-overflow-tooltip />
      
      <!-- 文章摘要：带省略号和悬停提示 -->
      <el-table-column label="文章摘要" align="center" prop="summary" width="200" show-overflow-tooltip />
      
      <!-- 封面图片：显示缩略图，悬停看大图 -->
      <el-table-column label="封面图片" align="center" prop="cover" width="100">
        <template slot-scope="scope">
          <el-popover
            placement="right"
            title="封面大图"
            trigger="hover"
            v-if="scope.row.cover">
            <img :src="scope.row.cover" style="max-width: 300px; max-height: 200px;" />
            <img slot="reference" :src="scope.row.cover" style="width: 40px; height: 40px; object-fit: cover; border-radius: 4px; cursor: pointer;" />
          </el-popover>
          <span v-else>-</span>
        </template>
      </el-table-column>
      
      <el-table-column label="分类" align="center" prop="categoryName" width="100" show-overflow-tooltip />
      <el-table-column label="浏览量" align="center" prop="viewCount" width="80" />
      <el-table-column label="点赞数" align="center" prop="likeCount" width="80" />
      <el-table-column label="评论数" align="center" prop="commentCount" width="80" />
      
      <!-- 作者信息：显示昵称，悬停显示ID -->
      <el-table-column label="作者" align="center" prop="nickName" width="120" show-overflow-tooltip>
        <template slot-scope="scope">
          <el-tooltip :content="`作者ID: ${scope.row.userId}`" placement="top">
            <span>{{ scope.row.nickName || scope.row.userName || scope.row.userId }}</span>
          </el-tooltip>
        </template>
      </el-table-column>
      
      <!-- 状态：用标签显示 -->
      <el-table-column label="状态" align="center" prop="status" width="80">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status === '0'" type="success" size="small">发布</el-tag>
          <el-tag v-else-if="scope.row.status === '1'" type="info" size="small">草稿</el-tag>
          <el-tag v-else type="danger" size="small">未知</el-tag>
        </template>
      </el-table-column>
      
      <el-table-column label="创建时间" align="center" prop="createTime" width="160" />
      
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="150" fixed="right">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['blog_article:article:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['blog_article:article:remove']"
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

    <!-- 添加或修改博客文章对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="700px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="文章标题" prop="title">
              <el-input v-model="form.title" placeholder="请输入文章标题" maxlength="200" show-word-limit />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="文章摘要" prop="summary">
              <el-input v-model="form.summary" type="textarea" :rows="3" placeholder="请输入摘要" maxlength="500" show-word-limit />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="文章内容">
              <editor v-model="form.content" :min-height="300"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="封面图片" prop="cover">
              <el-input v-model="form.cover" placeholder="请输入图片URL" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="分类" prop="category">
              <el-select v-model="form.category" placeholder="请选择分类" clearable style="width: 100%">
                <el-option label="前端技术" value="frontend" />
                <el-option label="后端架构" value="backend" />
                <el-option label="数据库" value="database" />
                <el-option label="运维部署" value="devops" />
                <el-option label="成长感悟" value="life" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="分类名称" prop="categoryName">
              <el-input v-model="form.categoryName" placeholder="自动填充或手动输入" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio label="0">发布</el-radio>
                <el-radio label="1">草稿</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="作者ID" prop="userId">
              <el-input v-model="form.userId" placeholder="请输入作者ID" :disabled="form.id" />
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
import { listArticle, getArticle, delArticle, addArticle, updateArticle } from "@/api/blog_article/article"

export default {
  name: "Article",
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      articleList: [],
      title: "",
      open: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        title: null,
        category: null,
        userId: null,
      },
      form: {},
      rules: {
        title: [
          { required: true, message: "文章标题不能为空", trigger: "blur" }
        ],
        userId: [
          { required: true, message: "作者ID不能为空", trigger: "blur" }
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
      listArticle(this.queryParams).then(response => {
        this.articleList = response.rows
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
        title: null,
        summary: null,
        content: null,
        cover: null,
        category: null,
        categoryName: null,
        viewCount: 0,
        likeCount: 0,
        commentCount: 0,
        userId: null,
        status: '0',
        delFlag: '0'
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
      // 自动填充当前登录用户ID
      this.form.userId = this.$store.state.user.id || this.$store.state.user.userId
      this.open = true
      this.title = "添加博客文章"
    },
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids[0]
      getArticle(id).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改博客文章"
      })
    },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateArticle(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addArticle(this.form).then(response => {
              this.$modal.msgSuccess("新增成功")
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    handleDelete(row) {
      const ids = row.id || this.ids.join(',')
      this.$modal.confirm('是否确认删除博客文章编号为"' + ids + '"的数据项？').then(() => {
        return delArticle(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    handleExport() {
      this.download('blog_article/article/export', {
        ...this.queryParams
      }, `article_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>

<style scoped>
/* 表格内容省略样式 */
.el-table .cell {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>