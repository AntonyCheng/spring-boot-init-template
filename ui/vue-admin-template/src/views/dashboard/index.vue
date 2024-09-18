<template>
  <div class="dashboard-container">
    <template>
      <el-card style="max-width: 100%">
        <template #header>
          <div class="card-header">
            <span style="font-size: 16px"><b>我的头像</b></span>
          </div>
        </template>
        <template #default>
          <el-row>
            <el-col style="text-align: center">
              <el-upload
                class="avatar-uploader"
                action=""
                :limit="1"
                :show-file-list="false"
                :file-list="fileList"
                :before-upload="handleBefore"
                :http-request="handleSubmit"
                :on-success="handleSuccess"
              >
                <el-avatar v-if="avatar" :size="100" :src="avatar" class="avatar-class" />
                <el-avatar v-else :size="100" style="font-size: xxx-large">{{ name.at(0) }}</el-avatar>
                <div slot="tip" class="el-upload__tip">点击上传（仅支持jpg/png文件，且不超过1MB）</div>
              </el-upload>
            </el-col>
            <el-col>
              <el-divider />
            </el-col>
            <el-col>
              <el-descriptions :column="1" border>
                <template slot="title">
                  <span style="font-size: 16px"><b>详细信息</b></span>
                </template>
                <el-descriptions-item>
                  <template slot="label">
                    <i class="el-icon-info" />
                    ID
                  </template>
                  <template>
                    <el-row>
                      <el-col>
                        {{ id }}
                      </el-col>
                    </el-row>
                  </template>
                </el-descriptions-item>
                <el-descriptions-item>
                  <template slot="label">
                    <i class="el-icon-user-solid" />
                    账号
                  </template>
                  <template>
                    <el-row>
                      <el-col :span="8" style="line-height: 200%">
                        {{ account }}
                      </el-col>
                      <el-col :span="16" style="text-align: end">
                        <el-button type="primary" size="mini" @click="openDialog('account')">修改账号</el-button>
                        <el-button type="primary" size="mini" @click="openDialog('password')">修改密码</el-button>
                      </el-col>
                    </el-row>
                  </template>
                </el-descriptions-item>
                <el-descriptions-item>
                  <template slot="label">
                    <i class="el-icon-star-on" />
                    名称
                  </template>
                  <template>
                    <el-row>
                      <el-col :span="8" style="line-height: 200%">
                        {{ name }}
                      </el-col>
                      <el-col :span="16" style="text-align: end">
                        <el-button type="primary" size="mini" @click="openDialog('name')">修改名称</el-button>
                      </el-col>
                    </el-row>
                  </template>
                </el-descriptions-item>
                <el-descriptions-item>
                  <template slot="label">
                    <i class="el-icon-s-comment" />
                    邮箱
                  </template>
                  <template>
                    <el-row>
                      <el-col :span="8" style="line-height: 200%">
                        {{ email }}
                      </el-col>
                      <el-col :span="16" style="text-align: end">
                        <el-button type="primary" size="mini" @click="openDialog('email')">修改邮箱</el-button>
                      </el-col>
                    </el-row>
                  </template>
                </el-descriptions-item>
                <el-descriptions-item>
                  <template slot="label">
                    <i class="el-icon-s-opportunity" />
                    角色
                  </template>
                  <template>
                    <el-row>
                      <el-col>
                        <el-tag v-if="role === 'admin'" size="small">管理员</el-tag>
                        <el-tag v-else size="small">用户</el-tag>
                      </el-col>
                    </el-row>
                  </template>
                </el-descriptions-item>
              </el-descriptions>
            </el-col>
          </el-row>
        </template>
      </el-card>
    </template>
    <template>
      <el-dialog
        :title="dialogType==='account'?'修改账号':dialogType==='password'?'修改密码':dialogType==='name'?'修改名称':'修改邮箱'"
        :visible.sync="dialogVisible"
        :show-close="false"
        @close="handleCancel"
      >
        <el-form v-if="dialogType==='account'" ref="updateAccountForm" :model="updateAccountForm" label-width="80px">
          <el-form-item label="旧帐号">
            <el-input v-model="account" disabled="disabled" style="font-weight: bold" />
          </el-form-item>
          <el-form-item
            label="新账号"
            prop="newAccount"
            :rules="[
              {required:true,message:'新账号不能为空',trigger: 'blur'},
              {min:2,max:16,message: '新账号长度介于2-16位之间',trigger: 'blur'}
            ]"
          >
            <el-input v-model="updateAccountForm.newAccount" autocomplete="off" placeholder="请输入新账号" />
          </el-form-item>
        </el-form>
        <el-form v-if="dialogType==='password'" ref="updatePasswordForm" :model="updatePasswordForm" label-width="80px">
          <el-form-item
            label="旧密码"
            prop="oldPassword"
            :rules="[
              {required:true,message:'旧密码不能为空',trigger: 'blur'},
            ]"
          >
            <el-input
              ref="oldPassword"
              v-model="updatePasswordForm.oldPassword"
              :type="oldPasswordType"
              placeholder="请输入旧密码"
              autocomplete="off"
            />
            <span class="show-pwd" @click="showPwd('old')">
              <svg-icon :icon-class="oldPasswordType === 'password' ? 'eye' : 'eye-open'" />
            </span>
          </el-form-item>
          <el-form-item
            label="新密码"
            prop="newPassword"
            :rules="[
              {required:true,message:'新密码不能为空',trigger: 'blur'},
              {min:5,max:16,message: '新密码长度介于5-16位之间',trigger: 'blur'}
            ]"
          >
            <el-input
              ref="newPassword"
              v-model="updatePasswordForm.newPassword"
              :type="newPasswordType"
              placeholder="请输入新密码"
              autocomplete="off"
            />
            <span class="show-pwd" @click="showPwd('new')">
              <svg-icon :icon-class="newPasswordType === 'password' ? 'eye' : 'eye-open'" />
            </span>
          </el-form-item>
          <el-form-item
            label="确认密码"
            prop="checkNewPassword"
            :rules="[
              {required:true,message:'确认密码不能为空',trigger: 'blur'}
            ]"
          >
            <el-input
              ref="checkNewPassword"
              v-model="updatePasswordForm.checkNewPassword"
              :type="checkNewPasswordType"
              placeholder="请再次输入新密码"
              autocomplete="off"
            />
            <span class="show-pwd" @click="showPwd('check')">
              <svg-icon :icon-class="checkNewPasswordType === 'password' ? 'eye' : 'eye-open'" />
            </span>
          </el-form-item>
        </el-form>
        <el-form v-if="dialogType==='name'" ref="updateNameForm" :model="updateNameForm" label-width="80px">
          <el-form-item label="旧名称">
            <el-input v-model="name" disabled="disabled" style="font-weight: bold" />
          </el-form-item>
          <el-form-item
            label="新名称"
            prop="newName"
            :rules="[
              {required:true,message:'新名称不能为空',trigger: 'blur'},
              {min:1,max:16,message: '新名称长度介于1-16位之间',trigger: 'blur'}
            ]"
          >
            <el-input v-model="updateNameForm.newName" placeholder="请输入新名称" autocomplete="off" />
          </el-form-item>
        </el-form>
        <el-form v-if="dialogType==='email'" ref="updateEmailForm" :model="updateEmailForm" label-width="80px">
          <el-form-item label="旧邮箱">
            <el-input v-model="email" disabled="disabled" placeholder="请输入旧邮箱" style="font-weight: bold" />
          </el-form-item>
          <el-form-item
            label="新邮箱"
            prop="newEmail"
            :rules="[
              {required:true,message:'新邮箱不能为空',trigger: 'blur'}
            ]"
          >
            <el-input v-model="updateEmailForm.newEmail" placeholder="请输入新邮箱" autocomplete="off" />
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="handleCancel">取 消</el-button>
          <el-button type="primary" @click="handleUpdate">修 改</el-button>
        </div>
      </el-dialog>
    </template>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { updateAccount, updateAvatar, updateEmail, updateName, updatePassword } from '@/api/user'
import { Message } from 'element-ui'

export default {
  name: 'Dashboard',
  data() {
    return {
      fileList: [],
      dialogVisible: false,
      dialogType: undefined,
      updateAccountForm: {
        newAccount: undefined
      },
      updatePasswordForm: {
        oldPassword: undefined,
        newPassword: undefined,
        checkNewPassword: undefined
      },
      updateNameForm: {
        newName: undefined
      },
      updateEmailForm: {
        newEmail: undefined
      },
      oldPasswordType: 'password',
      newPasswordType: 'password',
      checkNewPasswordType: 'password'
    }
  },
  computed: {
    ...mapGetters([
      'id',
      'account',
      'name',
      'email',
      'avatar',
      'role'
    ])
  },
  methods: {
    handleBefore(file) {
      const isJPG = file.type === 'image/jpeg'
      const isPNG = file.type === 'image/png'
      const isLt1M = file.size / 1024 / 1024 < 1
      if (!isJPG && !isPNG) {
        this.$message.error('上传头像图片只能是 JPG 或 PNG 格式!')
      }
      if (!isLt1M) {
        this.$message.error('上传头像图片大小不能超过 1MB!')
      }
      return (isJPG || isPNG) && isLt1M
    },
    handleSubmit(options) {
      const { file } = options
      const formData = new FormData()
      formData.append('file', file)
      return updateAvatar(formData).then(response => {
        this.fileList = []
        Message.success(response.msg)
        this.getUserInfo()
      }).catch(error => {
        this.fileList = []
        return error
      })
    },
    handleSuccess(response) {
      console.log('response:', response)
    },
    openDialog(dialogType) {
      this.dialogVisible = true
      this.dialogType = dialogType
    },
    handleUpdate() {
      switch (this.dialogType) {
        case 'account': {
          this.$refs['updateAccountForm'].validate(valid => {
            if (valid) {
              const data = { newAccount: this.updateAccountForm.newAccount }
              updateAccount(data).then(response => {
                this.getUserInfo()
                this.resetUpdateForm()
                this.dialogVisible = false
                Message.success(response.msg)
              }).catch(error => {
                return error
              })
            }
          })
          break
        }
        case 'password': {
          this.$refs['updatePasswordForm'].validate(valid => {
            if (valid) {
              const data = {
                oldPassword: this.updatePasswordForm.oldPassword,
                newPassword: this.updatePasswordForm.newPassword,
                checkNewPassword: this.updatePasswordForm.checkNewPassword
              }
              updatePassword(data).then(response => {
                this.resetUpdateForm()
                this.dialogVisible = false
                Message.success(response.msg)
                this.$store.dispatch('auth/logout')
              }).catch(error => {
                return error
              })
            }
          })
          break
        }
        case 'name': {
          this.$refs['updateNameForm'].validate(valid => {
            if (valid) {
              const data = { newName: this.updateNameForm.newName }
              updateName(data).then(response => {
                this.getUserInfo()
                this.resetUpdateForm()
                this.dialogVisible = false
                Message.success(response.msg)
              }).catch(error => {
                return error
              })
            }
          })
          break
        }
        case 'email': {
          this.$refs['updateEmailForm'].validate(valid => {
            if (valid) {
              const data = { newEmail: this.updateEmailForm.newEmail }
              updateEmail(data).then(response => {
                this.getUserInfo()
                this.resetUpdateForm()
                this.dialogVisible = false
                Message.success(response.msg)
              }).catch(error => {
                return error
              })
            }
          })
          break
        }
      }
    },
    handleCancel() {
      if (this.dialogType === 'account') {
        this.$refs['updateAccountForm'].resetFields()
      } else if (this.dialogType === 'password') {
        this.$refs['updatePasswordForm'].resetFields()
      } else if (this.dialogType === 'email') {
        this.$refs['updateEmailForm'].resetFields()
      } else {
        this.$refs['updateNameForm'].resetFields()
      }
      this.resetUpdateForm()
      this.dialogVisible = false
    },
    showPwd(passwordType) {
      switch (passwordType) {
        case 'old': {
          if (this.oldPasswordType === 'password') {
            this.oldPasswordType = ''
          } else {
            this.oldPasswordType = 'password'
          }
          this.$nextTick(() => {
            this.$refs.oldPassword.focus()
          })
          break
        }
        case 'new': {
          if (this.newPasswordType === 'password') {
            this.newPasswordType = ''
          } else {
            this.newPasswordType = 'password'
          }
          this.$nextTick(() => {
            this.$refs.newPassword.focus()
          })
          break
        }
        case 'check': {
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
      }
    },
    async getUserInfo() {
      await this.$store.dispatch('auth/getInfo')
    },
    async resetUpdateForm() {
      this.updateAccountForm.newAccount = ''
      this.updateNameForm.newName = ''
      this.updateEmailForm.newEmail = ''
      this.updatePasswordForm.newPassword = ''
      this.updatePasswordForm.oldPassword = ''
      this.updatePasswordForm.checkNewPassword = ''
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

.avatar-class{
  img{
    width: 100%;
    background-size: cover;
  }
}

.show-pwd{
  position: absolute;
  right: 10px;
  top: 2px;
  font-size: 16px;
  color: #889aa4;
  cursor: pointer;
  user-select: none;
}
</style>
