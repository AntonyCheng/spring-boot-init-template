<template>
  <div class="dashboard-container">
    <template>
      <el-collapse style="align-content: center">
        <el-collapse-item>
          <template slot="title">
            <span style="font-size: 16px"><b>示例一：注册/找回密码</b></span>
          </template>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-card style="max-width: 100%">
                <template #default>
                  <el-form ref="registerForm" :model="registerForm" label-width="80px">
                    <el-form-item
                      label="用户账号"
                      prop="account"
                      :rules="[
                        {required:true,message:'账号不能为空',trigger: 'blur'},
                        {min:2,max:16,message: '账号长度介于2-16位之间',trigger: 'blur'}
                      ]"
                    >
                      <el-input
                        ref="registerAccount"
                        v-model="registerForm.account"
                        placeholder="请输入用户账号"
                        autocomplete="off"
                      />
                    </el-form-item>
                    <el-form-item
                      label="用户密码"
                      prop="password"
                      :rules="[
                        {required:true,message:'密码不能为空',trigger: 'blur'},
                        {min:5,max:16,message: '密码长度介于5-16位之间',trigger: 'blur'}
                      ]"
                    >
                      <el-input
                        ref="password"
                        v-model="registerForm.password"
                        :type="passwordType"
                        placeholder="请输入用户密码"
                        autocomplete="off"
                      />
                      <span class="show-pwd" @click="showPwd('passwordType')">
                        <svg-icon :icon-class="passwordType === 'password' ? 'eye' : 'eye-open'" />
                      </span>
                    </el-form-item>
                    <el-form-item
                      label="确认密码"
                      prop="checkPassword"
                      :rules="[
                        {required:true,message:'密码不能为空',trigger: 'blur'}
                      ]"
                    >
                      <el-input
                        ref="checkPassword"
                        v-model="registerForm.checkPassword"
                        :type="checkPasswordType"
                        placeholder="请输入确认密码"
                        autocomplete="off"
                      />
                      <span class="show-pwd" @click="showPwd('checkPasswordType')">
                        <svg-icon :icon-class="checkPasswordType === 'password' ? 'eye' : 'eye-open'" />
                      </span>
                    </el-form-item>
                    <el-form-item
                      label="用户邮箱"
                      prop="email"
                      :rules="[
                        {required:true,message:'邮箱不能为空',trigger: 'blur'}
                      ]"
                    >
                      <el-input
                        ref="registerEmail"
                        v-model="registerForm.email"
                        placeholder="请输入用户邮箱"
                        autocomplete="off"
                      />
                    </el-form-item>
                  </el-form>
                  <el-button
                    :loading="registerLoading"
                    type="primary"
                    style="width: 120px"
                    @click="handleRegister"
                  >注册
                  </el-button>
                </template>
              </el-card>
            </el-col>
            <el-col :span="12">
              <el-card style="max-width: 100%">
                <template #default>
                  <el-form ref="retrievePasswordForm" :model="retrievePasswordForm" label-width="100px">
                    <el-form-item
                      label="用户账号"
                      prop="account"
                      :rules="[
                        {required:true,message:'账号不能为空',trigger: 'blur'}
                      ]"
                    >
                      <el-input
                        ref="retrieveAccount"
                        v-model="retrievePasswordForm.account"
                        placeholder="请输入用户账号"
                        autocomplete="off"
                      />
                    </el-form-item>
                    <el-form-item
                      label="用户邮箱"
                      prop="email"
                      :rules="[
                        {required:true,message:'邮箱不能为空',trigger: 'blur'}
                      ]"
                    >
                      <el-input
                        ref="retrieveEmail"
                        v-model="retrievePasswordForm.email"
                        placeholder="请输入用户邮箱"
                        autocomplete="off"
                      />
                    </el-form-item>
                    <el-form-item
                      label="用户新密码"
                      prop="newPassword"
                      :rules="[
                        {required:true,message:'密码不能为空',trigger: 'blur'},
                        {min:5,max:16,message: '密码长度介于5-16位之间',trigger: 'blur'}
                      ]"
                    >
                      <el-input
                        ref="newPassword"
                        v-model="retrievePasswordForm.newPassword"
                        :type="newPasswordType"
                        placeholder="请输入用户新密码"
                        autocomplete="off"
                      />
                      <span class="show-pwd" @click="showPwd('newPasswordType')">
                        <svg-icon :icon-class="newPasswordType === 'password' ? 'eye' : 'eye-open'" />
                      </span>
                    </el-form-item>
                    <el-form-item
                      label="确认新密码"
                      prop="checkNewPassword"
                      :rules="[
                        {required:true,message:'密码不能为空',trigger: 'blur'}
                      ]"
                    >
                      <el-input
                        ref="checkNewPassword"
                        v-model="retrievePasswordForm.checkNewPassword"
                        :type="checkNewPasswordType"
                        placeholder="请输入确认新密码"
                        autocomplete="off"
                      />
                      <span class="show-pwd" @click="showPwd('checkNewPasswordType')">
                        <svg-icon :icon-class="checkNewPasswordType === 'password' ? 'eye' : 'eye-open'" />
                      </span>
                    </el-form-item>
                    <el-form-item
                      label="找回验证码"
                      prop="passwordCode"
                      :rules="[
                        {required:true,message:'找回密码验证码不能为空',trigger: 'blur'}
                      ]"
                    >
                      <el-row>
                        <el-col :span="20">
                          <el-input
                            ref="passwordCode"
                            v-model="retrievePasswordForm.passwordCode"
                            placeholder="请输入找回验证码"
                            autocomplete="off"
                          />
                        </el-col>
                        <el-col :span="4" style="text-align: right">
                          <el-button
                            :loading="retrievePasswordLoading"
                            type="success"
                            @click="getPasswordCode"
                          >获取验证码
                          </el-button>
                        </el-col>
                      </el-row>
                    </el-form-item>
                  </el-form>
                  <el-button
                    :loading="retrievePasswordLoading"
                    type="primary"
                    style="width: 120px"
                    @click="checkPasswordCode"
                  >找回密码
                  </el-button>
                </template>
              </el-card>
            </el-col>
          </el-row>
        </el-collapse-item>
        <el-collapse-item>
          <template slot="title">
            <span style="font-size: 16px"><b>示例二：验证码校验</b></span>
          </template>
          <el-card style="max-width: 100%">
            <template #default />
          </el-card>
        </el-collapse-item>
        <el-collapse-item>
          <template slot="title">
            <span style="font-size: 16px"><b>示例三：Word工具类</b></span>
          </template>
          <el-card style="max-width: 100%">
            <template #default />
          </el-card>
        </el-collapse-item>
        <el-collapse-item>
          <template slot="title">
            <span style="font-size: 16px"><b>示例四：PDF工具类</b></span>
          </template>
          <el-card style="max-width: 100%">
            <template #default />
          </el-card>
        </el-collapse-item>
      </el-collapse>
    </template>
  </div>
</template>

<script>

import { checkEmailCode, getEmailCode, register } from '@/api/auth'
import { Message } from 'element-ui'

export default {
  name: 'Example',
  data() {
    return {
      registerLoading: false,
      registerForm: {
        account: undefined,
        password: undefined,
        checkPassword: undefined,
        email: undefined
      },
      passwordType: 'password',
      checkPasswordType: 'password',
      retrievePasswordLoading: false,
      retrievePasswordForm: {
        account: undefined,
        email: undefined,
        newPassword: undefined,
        checkNewPassword: undefined,
        passwordCode: undefined
      },
      newPasswordType: 'password',
      checkNewPasswordType: 'password'
    }
  },
  methods: {
    showPwd(type) {
      switch (type) {
        case 'passwordType':
          if (this.passwordType === 'password') {
            this.passwordType = ''
          } else {
            this.passwordType = 'password'
          }
          this.$nextTick(() => {
            this.$refs.password.focus()
          })
          break
        case 'checkPasswordType':
          if (this.checkPasswordType === 'password') {
            this.checkPasswordType = ''
          } else {
            this.checkPasswordType = 'password'
          }
          this.$nextTick(() => {
            this.$refs.checkPassword.focus()
          })
          break
        case 'newPasswordType':
          if (this.newPasswordType === 'password') {
            this.newPasswordType = ''
          } else {
            this.newPasswordType = 'password'
          }
          this.$nextTick(() => {
            this.$refs.newPassword.focus()
          })
          break
        case 'checkNewPasswordType':
          if (this.checkNewPasswordType === 'password') {
            this.checkNewPasswordType = ''
          } else {
            this.checkNewPasswordType = 'password'
          }
          this.$nextTick(() => {
            this.$refs.checkNewPassword.focus()
          })
          break
      }
    },
    handleRegister() {
      this.$refs['registerForm'].validate(valid => {
        if (valid) {
          this.registerLoading = true
          const data = {
            account: this.registerForm.account,
            password: this.registerForm.password,
            checkPassword: this.registerForm.checkPassword,
            email: this.registerForm.email
          }
          register(data).then(response => {
            this.resetUpdateForm()
            Message.success(response.msg)
          }).finally(() => {
            this.registerLoading = false
          })
        }
      })
    },
    resetUpdateForm() {
      this.registerForm.account = undefined
      this.registerForm.password = undefined
      this.registerForm.checkPassword = undefined
      this.registerForm.email = undefined
    },
    getPasswordCode() {
      this.retrievePasswordLoading = true
      const data = {
        account: this.retrievePasswordForm.account,
        email: this.retrievePasswordForm.email
      }
      getEmailCode(data).then(response => {
        Message.success(response.msg)
      }).finally(() => {
        this.retrievePasswordLoading = false
      })
    },
    checkPasswordCode() {
      this.$refs['retrievePasswordForm'].validate(valid => {
        if (valid) {
          this.retrievePasswordLoading = true
          const data = {
            account: this.retrievePasswordForm.account,
            email: this.retrievePasswordForm.email,
            newPassword: this.retrievePasswordForm.newPassword,
            checkNewPassword: this.retrievePasswordForm.checkNewPassword,
            passwordCode: this.retrievePasswordForm.passwordCode
          }
          checkEmailCode(data).then(response => {
            this.resetUpdateForm()
            Message.success(response.msg)
          }).finally(() => {
            this.retrievePasswordLoading = false
          })
        }
      })
    }
  }
}
</script>

<style lang="scss">
.dashboard {
  &-container {
    margin: 30px;
  }

  &-text {
    font-size: 30px;
    line-height: 46px;
  }
}

.show-pwd {
  position: absolute;
  right: 10px;
  top: 2px;
  font-size: 16px;
  color: #889aa4;
  cursor: pointer;
  user-select: none;
}
</style>
