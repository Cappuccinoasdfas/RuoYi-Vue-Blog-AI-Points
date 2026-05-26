<template>
  <el-dialog
    title="修改密码"
    :visible.sync="visible"
    width="400px"
    :close-on-click-modal="false"
  >
    <el-form 
      ref="form" 
      :model="form" 
      :rules="rules"
      label-width="100px"
    >
      <el-form-item label="当前密码" prop="oldPassword">
        <el-input 
          v-model="form.oldPassword" 
          type="password"
          placeholder="请输入当前密码"
          show-password
        />
      </el-form-item>
      
      <el-form-item label="新密码" prop="newPassword">
        <el-input 
          v-model="form.newPassword" 
          type="password"
          placeholder="请输入新密码"
          show-password
        />
      </el-form-item>
      
      <el-form-item label="确认密码" prop="confirmPassword">
        <el-input 
          v-model="form.confirmPassword" 
          type="password"
          placeholder="请再次输入新密码"
          show-password
        />
      </el-form-item>
    </el-form>
    
    <div slot="footer" class="dialog-footer">
      <el-button @click="visible = false">取 消</el-button>
      <el-button type="primary" :loading="submitting" @click="handleSubmit">
        确 定
      </el-button>
    </div>
  </el-dialog>
</template>

<script>
import { updatePassword } from '../api/user'

export default {
  name: 'ChangePassword',
  data() {
    // 确认密码校验
    const validateConfirmPassword = (rule, value, callback) => {
      if (value !== this.form.newPassword) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }
    
    return {
      visible: false,
      submitting: false,
      form: {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      },
      rules: {
        oldPassword: [
          { required: true, message: '请输入当前密码', trigger: 'blur' }
        ],
        newPassword: [
          { required: true, message: '请输入新密码', trigger: 'blur' },
          { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请再次输入新密码', trigger: 'blur' },
          { validator: validateConfirmPassword, trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    /**
     * 打开弹窗
     */
    open() {
      this.visible = true
      this.form = {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      }
      this.$nextTick(() => {
        this.$refs.form?.clearValidate()
      })
    },

    /**
     * 提交表单
     */
    handleSubmit() {
      this.$refs.form.validate(async (valid) => {
        if (!valid) return
        
        this.submitting = true
        
        try {
          const res = await updatePassword({
            oldPassword: this.form.oldPassword,
            newPassword: this.form.newPassword
          })
          
          if (res.code === 0 || res.code === 200) {
            this.$message.success('密码修改成功，请重新登录')
            this.visible = false
            
            // 退出登录
            setTimeout(() => {
              this.$store.dispatch('LogOut').then(() => {
                location.href = '/index'
              })
            }, 1500)
          } else {
            this.$message.error(res.msg || '修改失败')
          }
        } catch (error) {
          console.error('修改密码失败:', error)
          this.$message.error('修改失败，请稍后重试')
        } finally {
          this.submitting = false
        }
      })
    }
  }
}
</script>