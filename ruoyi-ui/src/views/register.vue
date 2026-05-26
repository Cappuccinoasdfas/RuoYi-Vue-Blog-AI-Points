<template>
  <div class="register">
    <el-form ref="registerForm" :model="registerForm" :rules="registerRules" class="register-form">
      <h3 class="title">{{ title }}</h3>
      
      <el-form-item prop="username">
        <el-input v-model="registerForm.username" type="text" auto-complete="off" placeholder="用户名">
          <svg-icon slot="prefix" icon-class="user" class="el-input__icon input-icon" />
        </el-input>
      </el-form-item>
      
      <el-form-item prop="email">
        <el-input v-model="registerForm.email" type="email" auto-complete="off" placeholder="邮箱">
          <svg-icon slot="prefix" icon-class="email" class="el-input__icon input-icon" />
        </el-input>
      </el-form-item>
      
      <el-form-item prop="password">
        <el-input
          v-model="registerForm.password"
          type="password"
          auto-complete="off"
          placeholder="密码"
          @keyup.enter.native="handleRegister"
        >
          <svg-icon slot="prefix" icon-class="password" class="el-input__icon input-icon" />
        </el-input>
      </el-form-item>
      
      <el-form-item prop="confirmPassword">
        <el-input
          v-model="registerForm.confirmPassword"
          type="password"
          auto-complete="off"
          placeholder="确认密码"
          @keyup.enter.native="handleRegister"
        >
          <svg-icon slot="prefix" icon-class="password" class="el-input__icon input-icon" />
        </el-input>
      </el-form-item>
      
      <el-form-item prop="code">
        <el-input
          v-model="registerForm.code"
          auto-complete="off"
          placeholder="验证码"
          style="width: 60%"
          @keyup.enter.native="handleRegister"
        >
          <svg-icon slot="prefix" icon-class="validCode" class="el-input__icon input-icon" />
        </el-input>
        <div class="register-code">
          <el-button
            :disabled="codeButtonDisabled"
            :loading="sendingCode"
            style="width: 100%; margin-left: 10px;"
            @click="handleSendCode"
          >
            {{ codeButtonText }}
          </el-button>
        </div>
      </el-form-item>
      
      <el-form-item style="width:100%;">
        <el-button
          :loading="loading"
          size="medium"
          type="primary"
          style="width:100%;"
          @click.native.prevent="handleRegister"
        >
          <span v-if="!loading">注 册</span>
          <span v-else>注 册 中...</span>
        </el-button>
        <div style="float: right;">
          <router-link class="link-type" :to="'/login'">使用已有账户登录</router-link>
        </div>
      </el-form-item>
    </el-form>
    
    <!-- 底部 -->
    <div class="el-register-footer">
      <span>{{ footerContent }}</span>
    </div>
  </div>
</template>

<script>
import { register, sendRegisterCode } from "@/api/login"
import defaultSettings from '@/settings'

export default {
  name: "Register",
  data() {
    const equalToPassword = (rule, value, callback) => {
      if (this.registerForm.password !== value) {
        callback(new Error("两次输入的密码不一致"))
      } else {
        callback()
      }
    }
    
    return {
      title: process.env.VUE_APP_TITLE,
      footerContent: defaultSettings.footerContent,
      registerForm: {
        username: "",
        email: "",
        password: "",
        confirmPassword: "",
        code: ""
      },
      registerRules: {
        username: [
          { required: true, message: "请输入用户名", trigger: "blur" },
          { min: 2, max: 20, message: "用户名长度必须介于 2 和 20 之间", trigger: "blur" },
          { pattern: /^[a-zA-Z0-9_\u4e00-\u9fa5]+$/, message: "用户名只能包含中英文、数字和下划线", trigger: "blur" }
        ],
        email: [
          { required: true, message: "请输入邮箱地址", trigger: "blur" },
          { type: 'email', message: "请输入正确的邮箱格式", trigger: "blur" }
        ],
        password: [
          { required: true, message: "请输入密码", trigger: "blur" },
          { min: 6, max: 20, message: "密码长度必须介于 6 和 20 之间", trigger: "blur" },
          { pattern: /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d@$!%*#?&]+$/, message: "密码必须包含字母和数字", trigger: "blur" }
        ],
        confirmPassword: [
          { required: true, message: "请再次输入密码", trigger: "blur" },
          { validator: equalToPassword, trigger: "blur" }
        ],
        code: [
          { required: true, message: "请输入验证码", trigger: "blur" },
          { pattern: /^\d{6}$/, message: "验证码必须为6位数字", trigger: "blur" }
        ]
      },
      loading: false,
      sendingCode: false,
      codeButtonDisabled: false,
      countdown: 60,
      countdownTimer: null
    }
  },
  computed: {
    codeButtonText() {
      if (this.sendingCode) {
        return '发送中...'
      }
      return this.codeButtonDisabled ? `${this.countdown}秒后重试` : '获取验证码'
    }
  },
  beforeDestroy() {
    if (this.countdownTimer) {
      clearInterval(this.countdownTimer)
    }
  },
  methods: {
    // 发送验证码
    handleSendCode() {
      // 验证用户名和邮箱
      this.$refs.registerForm.validateField('username', (usernameError) => {
        if (usernameError) {
          this.$message.error('请先填写正确的用户名')
          return
        }
        
        this.$refs.registerForm.validateField('email', (emailError) => {
          if (emailError) {
            this.$message.error('请先填写正确的邮箱地址')
            return
          }
          
          this.sendingCode = true
          
          const data = {
            username: this.registerForm.username,
            email: this.registerForm.email
          }
          
          sendRegisterCode(data).then(response => {
            this.$message.success('验证码已发送到您的邮箱，请查收')
            this.startCountdown()
          }).catch(error => {
            // this.$message.error(error.message || '发送验证码失败，请重试')
          }).finally(() => {
            this.sendingCode = false
          })
        })
      })
    },
    
    // 开始倒计时
    startCountdown() {
      this.codeButtonDisabled = true
      this.countdown = 60
      
      if (this.countdownTimer) {
        clearInterval(this.countdownTimer)
      }
      
      this.countdownTimer = setInterval(() => {
        if (this.countdown > 0) {
          this.countdown--
        } else {
          clearInterval(this.countdownTimer)
          this.countdownTimer = null
          this.codeButtonDisabled = false
          this.countdown = 60
        }
      }, 1000)
    },
    
    // 注册处理
    handleRegister() {
      this.$refs.registerForm.validate(valid => {
        if (valid) {
          this.loading = true
          
          const registerData = {
            username: this.registerForm.username,
            email: this.registerForm.email,
            password: this.registerForm.password,
            confirmPassword: this.registerForm.confirmPassword,
            code: this.registerForm.code
          }
          
          register(registerData).then(() => {
            const username = this.registerForm.username
            this.$alert(`<font color='red'>恭喜你，您的账号 ${username} 注册成功！</font>`, '系统提示', {
              dangerouslyUseHTMLString: true,
              type: 'success'
            }).then(() => {
              if (this.countdownTimer) {
                clearInterval(this.countdownTimer)
                this.countdownTimer = null
              }
              this.$router.push("/login")
            }).catch(() => {
              this.$router.push("/login")
            })
          }).catch(() => {
            this.loading = false
          })
        } else {
          this.$message.warning('请完善注册信息')
          return false
        }
      })
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
.register {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  background-image: url("../assets/images/login-background.jpg");
  background-size: cover;
}

.title {
  margin: 0px auto 30px auto;
  text-align: center;
  color: #707070;
}

.register-form {
  border-radius: 6px;
  background: #ffffff;
  width: 400px;
  padding: 25px 25px 5px 25px;
  
  .el-input {
    height: 38px;
    
    input {
      height: 38px;
    }
  }
  
  .input-icon {
    height: 39px;
    width: 14px;
    margin-left: 2px;
  }
}

.register-code {
  width: 40%;
  float: right;
}

.el-register-footer {
  height: 40px;
  line-height: 40px;
  position: fixed;
  bottom: 0;
  width: 100%;
  text-align: center;
  color: #fff;
  font-family: Arial;
  font-size: 12px;
  letter-spacing: 1px;
}
</style>