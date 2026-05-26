<template>
  <el-dialog
    title="更换头像"
    :visible.sync="visible"
    width="400px"
    :close-on-click-modal="false"
  >
    <div class="avatar-upload">
      <!-- 裁剪区域 -->
      <div class="cropper-wrapper">
        <vue-cropper
          ref="cropper"
          :img="options.img"
          :info="true"
          :autoCrop="options.autoCrop"
          :autoCropWidth="options.autoCropWidth"
          :autoCropHeight="options.autoCropHeight"
          :fixedBox="options.fixedBox"
          :outputType="options.outputType"
          @realTime="realTime"
        />
      </div>
      
      <!-- 预览区域 -->
      <div class="preview-wrapper">
        <div class="preview-box">
          <img :src="previewUrl" alt="预览" />
        </div>
        <p class="preview-tip">预览</p>
      </div>
      
      <!-- 上传按钮 -->
      <div class="upload-action">
        <el-upload
          ref="upload"
          action="#"
          :show-file-list="false"
          :before-upload="beforeUpload"
          accept="image/*"
        >
          <el-button size="small" icon="el-icon-plus">
            选择图片
          </el-button>
        </el-upload>
        <span class="upload-tip">支持 JPG/PNG，不超过 2MB</span>
      </div>
    </div>
    
    <div slot="footer" class="dialog-footer">
      <el-button @click="visible = false">取 消</el-button>
      <el-button type="primary" :loading="uploading" @click="handleUpload">
        确 定
      </el-button>
    </div>
  </el-dialog>
</template>

<script>
import { VueCropper } from 'vue-cropper'
import { updateAvatar } from '../api/user'

export default {
  name: 'AvatarUpload',
  components: {
    VueCropper
  },
  data() {
    return {
      visible: false,
      uploading: false,
      previewUrl: '',
      options: {
        img: '',
        autoCrop: true,
        autoCropWidth: 200,
        autoCropHeight: 200,
        fixedBox: true,
        outputType: 'png'
      }
    }
  },
  methods: {
    /**
     * 打开弹窗
     */
    open() {
      this.visible = true
      this.options.img = ''
      this.previewUrl = ''
    },

    /**
     * 上传前校验
     */
    beforeUpload(file) {
      const isImage = file.type.startsWith('image/')
      const isLt2M = file.size / 1024 / 1024 < 5
      
      if (!isImage) {
        this.$message.error('只能上传图片文件！')
        return false
      }
      
      if (!isLt2M) {
        this.$message.error('图片大小不能超过 5MB！')
        return false
      }
      
      const reader = new FileReader()
      reader.onload = (e) => {
        this.options.img = e.target.result
      }
      reader.readAsDataURL(file)
      
      return false // 阻止自动上传
    },

    /**
     * 实时预览
     */
    realTime(data) {
      this.previewUrl = data.url
    },

    /**
     * 上传头像
     */
    async handleUpload() {
      if (!this.options.img) {
        this.$message.warning('请先选择图片')
        return
      }
      
      this.uploading = true
      
      try {
        // 获取裁剪后的图片
        this.$refs.cropper.getCropBlob(async (blob) => {
          const formData = new FormData()
          formData.append('avatarfile', blob, 'avatar.png')
          
          const res = await updateAvatar(formData)
          
          if (res.code === 0 || res.code === 200) {
            this.$message.success('头像更新成功')
            
            // 更新 store 中的头像
            const avatar = res.data || res.imgUrl
            this.$store.dispatch('user/updateAvatar', avatar)
            
            this.visible = false
            this.$emit('success', avatar)
          } else {
            this.$message.error(res.msg || '上传失败')
          }
        })
      } catch (error) {
        console.error('上传头像失败:', error)
        this.$message.error('上传失败，请稍后重试')
      } finally {
        this.uploading = false
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.avatar-upload {
  .cropper-wrapper {
    width: 100%;
    height: 300px;
    margin-bottom: 16px;
  }
  
  .preview-wrapper {
    display: flex;
    align-items: center;
    margin-bottom: 16px;
    
    .preview-box {
      width: 60px;
      height: 60px;
      border-radius: 50%;
      overflow: hidden;
      border: 1px solid #e8eaed;
      
      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }
    }
    
    .preview-tip {
      margin-left: 12px;
      font-size: 13px;
      color: #8c8c8c;
    }
  }
  
  .upload-action {
    display: flex;
    align-items: center;
    
    .upload-tip {
      margin-left: 12px;
      font-size: 12px;
      color: #bfbfbf;
    }
  }
}
</style>