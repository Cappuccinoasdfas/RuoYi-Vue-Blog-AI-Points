<template>
  <div class="publish-page">
    <!-- 页面标题 -->
    <div class="page-header glass-header">
      <h2>{{ isEdit ? '编辑文章' : '写文章' }}</h2>
      <div class="header-actions">
        <el-button @click="handleSaveDraft" :loading="saving" class="glass-btn-secondary">
          <i class="el-icon-document"></i>
          保存草稿
        </el-button>
        <el-button type="primary" @click="handlePublish" :loading="publishing" class="glass-btn-primary">
          <i class="el-icon-s-promotion"></i>
          {{ isEdit ? '更新' : '发布' }}
        </el-button>
      </div>
    </div>
    
    <!-- 文章编辑表单 -->
    <div class="publish-form glass-form">
      <!-- 标题输入 -->
      <div class="form-item">
        <el-input
          v-model="form.title"
          placeholder="请输入文章标题..."
          maxlength="200"
          show-word-limit
          class="title-input glass-input"
        />
      </div>
      
      <!-- 封面图上传 -->
      <div class="form-item">
        <div class="cover-upload">
          <span class="label">封面图片：</span>
          <el-upload
            class="cover-uploader"
            :action="uploadUrl"
            :headers="uploadHeaders"
            :before-upload="beforeCoverUpload"
            :on-success="handleCoverSuccess"
            :on-error="handleUploadError"
            :show-file-list="false"
            accept="image/*"
          >
            <div v-if="form.cover" class="cover-preview glass-cover">
              <img :src="form.cover" alt="封面" />
              <div class="cover-mask">
                <i class="el-icon-edit"></i>
                <span>更换封面</span>
              </div>
            </div>
            <div v-else class="upload-placeholder glass-upload">
              <i class="el-icon-plus"></i>
              <span>添加封面</span>
            </div>
          </el-upload>
          <el-button 
            v-if="form.cover" 
            type="text" 
            @click="form.cover = ''"
            class="remove-cover"
          >
            移除封面
          </el-button>
        </div>
      </div>
      
      <!-- 文章摘要 -->
      <div class="form-item">
        <el-input
          v-model="form.summary"
          type="textarea"
          :rows="3"
          placeholder="文章摘要（选填，不填则自动截取正文前50字）"
          maxlength="50"
          show-word-limit
          class="glass-textarea"
        />
      </div>
      
      <!-- 分类选择 -->
      <div class="form-item">
        <span class="label">文章分类：</span>
        <el-select 
          v-model="form.category" 
          placeholder="请选择分类"
          @change="handleCategoryChange"
          class="glass-select"
        >
          <el-option
            v-for="item in categoryOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </div>
      
      <!-- 富文本编辑器 - 支持文字和图片 -->
      <div class="form-item editor-container">
        <span class="label">文章内容：</span>
        
        <!-- 工具栏 -->
        <div class="editor-toolbar glass-toolbar">
          <el-button-group>
            <el-button 
              size="small" 
              @click="insertMarkdown('**', '**')"
              title="加粗"
              class="toolbar-btn"
            >
              <i class="el-icon-format-bold"></i>
            </el-button>
            <el-button 
              size="small" 
              @click="insertMarkdown('*', '*')"
              title="斜体"
              class="toolbar-btn"
            >
              <i class="el-icon-format-italic"></i>
            </el-button>
            <el-button 
              size="small" 
              @click="insertMarkdown('~~', '~~')"
              title="删除线"
              class="toolbar-btn"
            >
              <i class="el-icon-format-strikethrough"></i>
            </el-button>
          </el-button-group>
          
          <el-button-group>
            <el-button 
              size="small" 
              @click="insertMarkdown('# ', '')"
              title="一级标题"
              class="toolbar-btn"
            >
              H1
            </el-button>
            <el-button 
              size="small" 
              @click="insertMarkdown('## ', '')"
              title="二级标题"
              class="toolbar-btn"
            >
              H2
            </el-button>
            <el-button 
              size="small" 
              @click="insertMarkdown('### ', '')"
              title="三级标题"
              class="toolbar-btn"
            >
              H3
            </el-button>
          </el-button-group>
          
          <el-button-group>
            <el-button 
              size="small" 
              @click="insertMarkdown('```\n', '\n```')"
              title="代码块"
              class="toolbar-btn"
            >
              <i class="el-icon-format-code"></i>
            </el-button>
            <el-button 
              size="small" 
              @click="insertMarkdown('> ', '')"
              title="引用"
              class="toolbar-btn"
            >
              <i class="el-icon-format-quote"></i>
            </el-button>
            <el-button 
              size="small" 
              @click="insertMarkdown('- ', '')"
              title="无序列表"
              class="toolbar-btn"
            >
              <i class="el-icon-format-list"></i>
            </el-button>
            <el-button 
              size="small" 
              @click="insertMarkdown('1. ', '')"
              title="有序列表"
              class="toolbar-btn"
            >
              <i class="el-icon-format-orderedlist"></i>
            </el-button>
          </el-button-group>
          
          <el-button-group>
            <el-button 
              size="small" 
              @click="insertLink"
              title="插入链接"
              class="toolbar-btn"
            >
              <i class="el-icon-link"></i>
            </el-button>
          </el-button-group>
        </div>
        
        <!-- 编辑器主体 -->
        <div class="editor-wrapper">
          <el-input
            ref="editor"
            v-model="form.content"
            type="textarea"
            :rows="15"
            placeholder="写下你的故事..."
            class="content-editor glass-editor"
            @keydown.tab.prevent="handleTabKey"
          />
          
          <!-- 预览区域 -->
          <div class="preview-panel glass-preview" v-if="showPreview">
            <div class="preview-header">
              <span>预览</span>
              <el-button 
                type="text" 
                size="small"
                @click="showPreview = false"
                class="preview-close"
              >
                关闭
              </el-button>
            </div>
            <div 
              class="preview-content markdown-body"
              v-html="renderedPreview"
            ></div>
          </div>
        </div>
        
        <!-- 预览切换按钮 -->
        <div class="editor-footer">
          <el-button 
            type="text" 
            @click="showPreview = !showPreview"
            class="preview-toggle"
          >
            <i :class="showPreview ? 'el-icon-edit' : 'el-icon-view'"></i>
            {{ showPreview ? '返回编辑' : '预览' }}
          </el-button>
        </div>
      </div>
      
      <!-- 视频上传预留区域 -->
      <div class="form-item video-section">
        <el-collapse v-model="activeCollapse" class="glass-collapse">
          <el-collapse-item title="视频附件（开发中...）" name="video">
            <div class="video-placeholder">
              <i class="el-icon-video-camera"></i>
              <p>视频上传功能开发中，敬请期待...</p>
              <el-upload
                action="#"
                :disabled="true"
                :show-file-list="false"
                accept="video/*"
              >
                <el-button type="primary" plain disabled class="glass-btn-disabled">
                  上传视频
                </el-button>
              </el-upload>
              <p class="video-tip">
                提示：支持MP4、AVI等常见格式，单个视频不超过100MB
              </p>
            </div>
          </el-collapse-item>
        </el-collapse>
      </div>
    </div>
  </div>
</template>

<script>
import { saveArticle, updateArticle, getArticleDetail } from '../api/article'
import MarkdownIt from 'markdown-it'

const md = new MarkdownIt({
  html: true,
  linkify: true,
  breaks: true
})

export default {
  name: 'PublishPage',
  data() {
    return {
      isEdit: false,
      articleId: null,
      saving: false,
      publishing: false,
      showPreview: false,
      activeCollapse: [],
      
      uploadUrl: process.env.VUE_APP_BASE_API + '/blog/article/upload',
      
      form: {
        title: '',
        summary: '',
        content: '',
        cover: '',
        category: '',
        categoryName: '',
        status: '0'
      },
      
      categoryOptions: [
        { value: 'frontend', label: '前端开发' },
        { value: 'backend', label: '后端开发' },
        { value: 'life', label: '生活随笔' },
        { value: 'ai', label: '人工智能' }
      ]
    }
  },
  
  computed: {
    uploadHeaders() {
      const token = this.$store.state.user.token
      return {
        Authorization: 'Bearer ' + token
      }
    },
    
    renderedPreview() {
      if (!this.form.content) {
        return '<p style="color: rgba(255,255,255,0.5); text-align: center;">暂无内容</p>'
      }
      try {
        return md.render(this.form.content)
      } catch (error) {
        console.error('Markdown渲染失败:', error)
        return '<p style="color: #ff6b6b;">渲染失败，请检查Markdown语法</p>'
      }
    }
  },
  
  mounted() {
    console.log('uploadUrl ？值:', this.uploadUrl)
    const articleId = this.$route.query.id
    if (articleId) {
      this.isEdit = true
      this.articleId = articleId
      this.loadArticle(articleId)
    }
  },
  
  methods: {
    async loadArticle(id) {
      try {
        const res = await getArticleDetail(id)
        
        if (res.code === 0 || res.code === 200) {
          const article = res.data
          this.form = {
            title: article.title || '',
            summary: article.summary || '',
            content: article.content || '',
            cover: article.cover || '',
            category: article.category || '',
            categoryName: article.categoryName || '',
            status: article.status || '0'
          }
        } else {
          this.$message.error(res.msg || '加载文章失败')
        }
      } catch (error) {
        console.error('加载文章失败:', error)
        this.$message.error('加载文章失败')
      }
    },
    
    handleCategoryChange(value) {
      const option = this.categoryOptions.find(item => item.value === value)
      this.form.categoryName = option ? option.label : ''
    },
    
    beforeCoverUpload(file) {
      console.log('当前文件大小：', file.size / 1024 / 1024, 'MB');
      console.log('限制阈值：5MB');
      const isImage = file.type.startsWith('image/')
      const isLt5M = file.size / 1024 / 1024 < 5
      
      if (!isImage) {
        this.$message.error('只能上传图片文件！')
        return false
      }
      if (!isLt5M) {
        this.$message.error('图片大小不能超过5MB！')
        return false
      }
      return true
    },
    
    handleCoverSuccess(response) {
      if (response.code === 0 || response.code === 200) {
        this.form.cover = response.data.url || response.data
        this.$message.success('封面上传成功')
      } else {
        this.$message.error(response.msg || '上传失败')
      }
    },
    
    beforeContentImageUpload(file) {
      console.log('当前文件大小：', file.size / 1024 / 1024, 'MB');
      console.log('限制阈值：5MB');
      const isImage = file.type.startsWith('image/')
      const isLt5M = file.size / 1024 / 1024 < 5
      
      if (!isImage) {
        this.$message.error('只能上传图片文件！')
        return false
      }
      if (!isLt5M) {
        this.$message.error('图片大小不能超过5MB！')
        return false
      }
      return true
    },
    
    handleContentImageSuccess(response) {
      if (response.code === 0 || response.code === 200) {
        const imageUrl = response.data.url || response.data
        this.insertMarkdown(`![图片](${imageUrl})`, '')
        this.$message.success('图片上传成功')
      } else {
        this.$message.error(response.msg || '上传失败')
      }
    },
    
    handleUploadError(error) {
      console.error('上传失败:', error)
      this.$message.error('上传失败，请稍后重试')
    },
    
    insertMarkdown(prefix, suffix) {
      const textarea = this.$refs.editor.$refs.textarea
      if (!textarea) return
      
      const start = textarea.selectionStart
      const end = textarea.selectionEnd
      const selectedText = this.form.content.substring(start, end)
      
      const newText = this.form.content.substring(0, start) + 
                     prefix + selectedText + suffix + 
                     this.form.content.substring(end)
      
      this.form.content = newText
      
      this.$nextTick(() => {
        textarea.focus()
        const newPosition = start + prefix.length + selectedText.length
        textarea.setSelectionRange(newPosition, newPosition)
      })
    },
    
    insertLink() {
      const url = prompt('请输入链接地址：', 'https://')
      if (url) {
        const text = prompt('请输入链接文字（可选）：', '')
        this.insertMarkdown(`[${text || url}](${url})`, '')
      }
    },
    
    handleTabKey(e) {
      this.insertMarkdown('    ', '')
    },
    
    async handleSaveDraft() {
      if (!this.validateForm()) return
      
      this.form.status = '1'
      this.saving = true
      
      try {
        let res
        if (this.isEdit) {
          res = await updateArticle({ ...this.form, id: this.articleId })
        } else {
          res = await saveArticle(this.form)
        }
        
        if (res.code === 0 || res.code === 200) {
          this.$message.success('草稿保存成功')
          
          if (!this.isEdit) {
            setTimeout(() => {
              this.$router.push('/blog/my-articles')
            }, 1000)
          }
        } else {
          this.$message.error(res.msg || '保存失败')
        }
      } catch (error) {
        console.error('保存草稿失败:', error)
        this.$message.error('保存失败，请稍后重试')
      } finally {
        this.saving = false
      }
    },
    
    async handlePublish() {
      if (!this.validateForm()) return
      
      this.form.status = '0'
      this.publishing = true
      
      try {
        let res
        if (this.isEdit) {
          res = await updateArticle({ ...this.form, id: this.articleId })
        } else {
          res = await saveArticle(this.form)
        }
        
        if (res.code === 0 || res.code === 200) {
          this.$message.success(this.isEdit ? '更新成功' : '发布成功')
          
          setTimeout(() => {
            this.$router.push('/blog/home')
          }, 1000)
        } else {
          this.$message.error(res.msg || '发布失败')
        }
      } catch (error) {
        console.error('发布失败:', error)
        this.$message.error('发布失败，请稍后重试')
      } finally {
        this.publishing = false
      }
    },
    
    validateForm() {
      if (!this.form.title.trim()) {
        this.$message.warning('请输入文章标题')
        return false
      }
      
      if (!this.form.content.trim()) {
        this.$message.warning('请输入文章内容')
        return false
      }
      
      if (!this.form.category) {
        this.$message.warning('请选择文章分类')
        return false
      }
      
      if (!this.form.summary) {
        const text = this.form.content.replace(/[#*`\[\]()\n\r]/g, '').trim()
        this.form.summary = text.substring(0, 200)
      }
      
      return true
    }
  }
}
</script>

<style lang="scss" scoped>
.publish-page {
  max-width: 900px;
  margin: 0 auto;
  background: transparent;
  
  // 玻璃态页面标题
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
    align-items: center;
    justify-content: space-between;
    margin-bottom: 24px;
    
    h2 {
      font-size: 24px;
      font-weight: 600;
      color: #fff;
      margin: 0;
      text-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    }
    
    .header-actions {
      display: flex;
      gap: 12px;
    }
  }
  
  // 玻璃态按钮
  .glass-btn-primary {
    background: rgba(79, 70, 229, 0.8) !important;
    backdrop-filter: blur(10px);
    -webkit-backdrop-filter: blur(10px);
    border: 1px solid rgba(255, 255, 255, 0.2) !important;
    color: #fff !important;
    box-shadow: 0 4px 12px rgba(79, 70, 229, 0.3);
    
    &:hover {
      background: rgba(67, 56, 202, 0.9) !important;
    }
  }
  
  .glass-btn-secondary {
    background: rgba(255, 255, 255, 0.15) !important;
    backdrop-filter: blur(10px);
    -webkit-backdrop-filter: blur(10px);
    border: 1px solid rgba(255, 255, 255, 0.2) !important;
    color: #fff !important;
    
    &:hover {
      background: rgba(255, 255, 255, 0.25) !important;
    }
  }
  
  // 玻璃态表单
  .glass-form {
    background: rgba(255, 255, 255, 0.12);
    backdrop-filter: blur(20px) saturate(180%);
    -webkit-backdrop-filter: blur(20px) saturate(180%);
    border: 1px solid rgba(255, 255, 255, 0.15);
    border-radius: 16px;
    padding: 24px;
  }
  
  .publish-form {
    .form-item {
      margin-bottom: 24px;
      
      .label {
        display: block;
        margin-bottom: 8px;
        font-size: 14px;
        font-weight: 500;
        color: rgba(255, 255, 255, 0.9);
        text-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
      }
      
      // 标题输入框
      ::v-deep .glass-input {
        .el-input__inner {
          background: rgba(255, 255, 255, 0.1);
          backdrop-filter: blur(10px);
          -webkit-backdrop-filter: blur(10px);
          border: 1px solid rgba(255, 255, 255, 0.2);
          color: #fff;
          font-size: 20px;
          font-weight: 600;
          height: 48px;
          
          &::placeholder {
            color: rgba(255, 255, 255, 0.5);
          }
          
          &:focus {
            border-color: rgba(255, 255, 255, 0.4);
            background: rgba(255, 255, 255, 0.15);
          }
        }
      }
      
      // 文本域
      ::v-deep .glass-textarea {
        textarea {
          background: rgba(255, 255, 255, 0.1) !important;
          backdrop-filter: blur(10px);
          -webkit-backdrop-filter: blur(10px);
          border: 1px solid rgba(255, 255, 255, 0.2) !important;
          color: #fff !important;
          
          &::placeholder {
            color: rgba(255, 255, 255, 0.5);
          }
          
          &:focus {
            border-color: rgba(255, 255, 255, 0.4) !important;
            background: rgba(255, 255, 255, 0.15) !important;
          }
        }
        
        .el-input__count {
          background: transparent;
          color: rgba(255, 255, 255, 0.7);
        }
      }
      
      // 下拉选择框
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
      
      .cover-upload {
        display: flex;
        align-items: center;
        gap: 16px;
        flex-wrap: wrap;
        
        .cover-uploader {
          .glass-cover {
            position: relative;
            width: 200px;
            height: 120px;
            border-radius: 8px;
            overflow: hidden;
            cursor: pointer;
            border: 1px solid rgba(255, 255, 255, 0.2);
            
            img {
              width: 100%;
              height: 100%;
              object-fit: cover;
            }
            
            .cover-mask {
              position: absolute;
              top: 0;
              left: 0;
              width: 100%;
              height: 100%;
              background: rgba(0, 0, 0, 0.5);
              backdrop-filter: blur(4px);
              -webkit-backdrop-filter: blur(4px);
              display: flex;
              flex-direction: column;
              align-items: center;
              justify-content: center;
              color: #fff;
              opacity: 0;
              transition: opacity 0.3s;
              
              i {
                font-size: 24px;
                margin-bottom: 4px;
              }
              
              span {
                font-size: 12px;
              }
            }
            
            &:hover .cover-mask {
              opacity: 1;
            }
          }
          
          .glass-upload {
            width: 200px;
            height: 120px;
            border: 2px dashed rgba(255, 255, 255, 0.3);
            border-radius: 8px;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            cursor: pointer;
            transition: all 0.3s;
            background: rgba(255, 255, 255, 0.05);
            backdrop-filter: blur(10px);
            -webkit-backdrop-filter: blur(10px);
            
            i {
              font-size: 28px;
              color: rgba(255, 255, 255, 0.7);
              margin-bottom: 8px;
            }
            
            span {
              font-size: 14px;
              color: rgba(255, 255, 255, 0.7);
            }
            
            &:hover {
              border-color: rgba(255, 255, 255, 0.6);
              
              i, span {
                color: #fff;
              }
            }
          }
        }
        
        .remove-cover {
          color: #ff6b6b !important;
        }
      }
      
      .editor-container {
        // 工具栏
        .glass-toolbar {
          display: flex;
          align-items: center;
          gap: 8px;
          padding: 12px;
          background: rgba(255, 255, 255, 0.08);
          backdrop-filter: blur(10px);
          -webkit-backdrop-filter: blur(10px);
          border: 1px solid rgba(255, 255, 255, 0.15);
          border-bottom: none;
          border-radius: 8px 8px 0 0;
          flex-wrap: wrap;
          
          .toolbar-btn {
            background: rgba(255, 255, 255, 0.1);
            border: 1px solid rgba(255, 255, 255, 0.15);
            color: rgba(255, 255, 255, 0.8);
            
            &:hover {
              background: rgba(255, 255, 255, 0.2);
              color: #fff;
            }
          }
        }
        
        .editor-wrapper {
          position: relative;
          
          ::v-deep .glass-editor {
            textarea {
              background: rgba(255, 255, 255, 0.1) !important;
              backdrop-filter: blur(10px);
              -webkit-backdrop-filter: blur(10px);
              border: 1px solid rgba(255, 255, 255, 0.15) !important;
              color: #fff !important;
              font-family: 'Monaco', 'Menlo', monospace;
              font-size: 15px;
              line-height: 1.8;
              resize: vertical;
              border-radius: 0 0 8px 8px;
              
              &::placeholder {
                color: rgba(255, 255, 255, 0.5);
              }
              
              &:focus {
                border-color: rgba(255, 255, 255, 0.3) !important;
                background: rgba(255, 255, 255, 0.15) !important;
              }
            }
          }
          
          .glass-preview {
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: rgba(255, 255, 255, 0.12);
            backdrop-filter: blur(20px) saturate(180%);
            -webkit-backdrop-filter: blur(20px) saturate(180%);
            border: 1px solid rgba(255, 255, 255, 0.15);
            border-radius: 0 0 8px 8px;
            display: flex;
            flex-direction: column;
            z-index: 10;
            
            .preview-header {
              display: flex;
              align-items: center;
              justify-content: space-between;
              padding: 12px 16px;
              border-bottom: 1px solid rgba(255, 255, 255, 0.15);
              
              span {
                font-weight: 500;
                color: #fff;
              }
              
              .preview-close {
                color: rgba(255, 255, 255, 0.8);
                
                &:hover {
                  color: #fff;
                }
              }
            }
            
            .preview-content {
              flex: 1;
              padding: 16px;
              overflow-y: auto;
              font-size: 15px;
              line-height: 1.8;
              color: rgba(255, 255, 255, 0.9);
              
              ::v-deep {
                h1, h2, h3, h4 {
                  margin-top: 24px;
                  margin-bottom: 16px;
                  font-weight: 600;
                  color: #fff;
                }
                
                p {
                  margin-bottom: 16px;
                }
                
                img {
                  max-width: 100%;
                  border-radius: 8px;
                }
                
                pre {
                  background: rgba(0, 0, 0, 0.3);
                  padding: 16px;
                  border-radius: 8px;
                  overflow-x: auto;
                  
                  code {
                    color: #abb2bf;
                  }
                }
                
                code {
                  background: rgba(0, 0, 0, 0.2);
                  padding: 2px 6px;
                  border-radius: 4px;
                }
                
                blockquote {
                  border-left: 4px solid rgba(255, 255, 255, 0.5);
                  padding-left: 16px;
                  margin: 16px 0;
                  color: rgba(255, 255, 255, 0.7);
                }
              }
            }
          }
        }
        
        .editor-footer {
          margin-top: 12px;
          text-align: right;
          
          .preview-toggle {
            color: rgba(255, 255, 255, 0.8);
            
            &:hover {
              color: #fff;
            }
          }
        }
      }
      
      .video-section {
        ::v-deep .glass-collapse {
          background: transparent;
          border: 1px solid rgba(255, 255, 255, 0.15);
          border-radius: 8px;
          
          .el-collapse-item__header {
            background: rgba(255, 255, 255, 0.08);
            backdrop-filter: blur(10px);
            -webkit-backdrop-filter: blur(10px);
            border: none;
            color: rgba(255, 255, 255, 0.9);
            font-weight: 500;
            padding: 0 16px;
            height: 48px;
            border-radius: 8px;
          }
          
          .el-collapse-item__wrap {
            background: rgba(255, 255, 255, 0.05);
            backdrop-filter: blur(10px);
            -webkit-backdrop-filter: blur(10px);
            border: none;
            border-radius: 0 0 8px 8px;
          }
          
          .el-collapse-item__content {
            color: rgba(255, 255, 255, 0.8);
          }
        }
        
        .video-placeholder {
          padding: 32px;
          text-align: center;
          
          i {
            font-size: 48px;
            color: rgba(255, 255, 255, 0.5);
            margin-bottom: 16px;
          }
          
          p {
            color: rgba(255, 255, 255, 0.7);
            margin-bottom: 16px;
          }
          
          .glass-btn-disabled {
            background: rgba(255, 255, 255, 0.1) !important;
            backdrop-filter: blur(10px);
            -webkit-backdrop-filter: blur(10px);
            border: 1px solid rgba(255, 255, 255, 0.15) !important;
            color: rgba(255, 255, 255, 0.5) !important;
          }
          
          .video-tip {
            margin-top: 16px;
            font-size: 12px;
            color: rgba(255, 255, 255, 0.4);
          }
        }
      }
    }
  }
}

/* 移动端适配 */
@media (max-width: 768px) {
  .publish-page {
    .publish-form {
      padding: 16px;
      
      .editor-container {
        .glass-toolbar {
          overflow-x: auto;
          flex-wrap: nowrap;
          
          &::-webkit-scrollbar {
            height: 4px;
          }
          
          &::-webkit-scrollbar-thumb {
            background: rgba(255, 255, 255, 0.3);
            border-radius: 2px;
          }
        }
      }
      
      .cover-upload {
        .cover-uploader {
          .glass-cover,
          .glass-upload {
            width: 160px;
            height: 100px;
          }
        }
      }
    }
  }
}
</style>