<template>
  <el-dialog
    title="编辑资料"
    :visible.sync="visible"
    width="450px"
    :close-on-click-modal="false"
  >
    <el-form 
      ref="form" 
      :model="form" 
      :rules="rules"
      label-width="80px"
    >
      <el-form-item label="用户昵称" prop="nickName">
        <el-input 
          v-model="form.nickName" 
          placeholder="请输入昵称"
          maxlength="30"
          show-word-limit
        />
      </el-form-item>
      
      <el-form-item label="手机号码" prop="phonenumber">
        <el-input 
          v-model="form.phonenumber" 
          placeholder="请输入手机号码"
          maxlength="11"
        />
      </el-form-item>
      
      <el-form-item label="邮箱地址" prop="email">
        <el-input 
          v-model="form.email" 
          placeholder="请输入邮箱"
        />
      </el-form-item>
      
      <el-form-item label="性别" prop="sex">
        <el-radio-group v-model="form.sex">
          <el-radio label="0">男</el-radio>
          <el-radio label="1">女</el-radio>
          <el-radio label="2">保密</el-radio>
        </el-radio-group>
      </el-form-item>
    </el-form>
    
    <div slot="footer" class="dialog-footer">
      <el-button @click="visible = false">取 消</el-button>
      <el-button type="primary" :loading="submitting" @click="handleSubmit">
        保 存
      </el-button>
    </div>
  </el-dialog>
</template>

<script>
import { updateProfile } from '../api/user'

export default {
  name: 'EditProfile',
  props: {
    userInfo: {
      type: Object,
      default: () => ({})
    }
  },
  data() {
    // 手机号校验
    const validatePhone = (rule, value, callback) => {
      if (value && !/^1[3-9]\d{9}$/.test(value)) {
        callback(new Error('请输入正确的手机号码'))
      } else {
        callback()
      }
    }
    
    // 邮箱校验
    const validateEmail = (rule, value, callback) => {
      if (value && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value)) {
        callback(new Error('请输入正确的邮箱地址'))
      } else {
        callback()
      }
    }
    
    return {
      visible: false,
      submitting: false,
      form: {
        nickName: '',
        phonenumber: '',
        email: '',
        sex: '0'
      },
      rules: {
        nickName: [
          { required: true, message: '请输入昵称', trigger: 'blur' },
          { min: 2, max: 30, message: '长度在 2 到 30 个字符', trigger: 'blur' }
        ],
        phonenumber: [
          { validator: validatePhone, trigger: 'blur' }
        ],
        email: [
          { validator: validateEmail, trigger: 'blur' }
        ]
      }
    }
  },
  watch: {
    userInfo: {
      handler(val) {
        if (val) {
          this.form = {
            nickName: val.nickName || '',
            phonenumber: val.phonenumber || '',
            email: val.email || '',
            sex: val.sex || '0'
          }
        }
      },
      immediate: true
    }
  },
  methods: {
    /**
     * 打开弹窗
     */
    open() {
      this.visible = true
    },

    /**
     * 提交表单
     */
    handleSubmit() {
      this.$refs.form.validate(async (valid) => {
        if (!valid) return
        
        this.submitting = true
        
        try {
          const res = await updateProfile(this.form)
          
          if (res.code === 0 || res.code === 200) {
            this.$message.success('保存成功')
            
            // 更新 store 中的用户信息
            this.$store.dispatch('user/updateInfo', this.form)
            
            this.visible = false
            this.$emit('success', this.form)
          } else {
            this.$message.error(res.msg || '保存失败')
          }
        } catch (error) {
          console.error('保存失败:', error)
          this.$message.error('保存失败，请稍后重试')
        } finally {
          this.submitting = false
        }
      })
    }
  }
}
</script>