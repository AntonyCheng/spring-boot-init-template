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
                        ref="registerPassword"
                        v-model="registerForm.password"
                        :type="passwordType"
                        placeholder="请输入用户密码"
                        autocomplete="off"
                      />
                      <span class="show-pwd" @click="showPwd('passwordType')">
                        <svg-icon :icon-class="passwordType === 'password' ? 'eye' : 'eye-open'"/>
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
                        <svg-icon :icon-class="checkPasswordType === 'password' ? 'eye' : 'eye-open'"/>
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
                        <svg-icon :icon-class="newPasswordType === 'password' ? 'eye' : 'eye-open'"/>
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
                        <svg-icon :icon-class="checkNewPasswordType === 'password' ? 'eye' : 'eye-open'"/>
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
            <template #default>
              <el-form ref="captchaForm" :model="captchaForm" label-width="100px">
                <el-form-item
                  label="图片验证码"
                  prop="captcha.code"
                  class="captcha-code-form"
                  :rules="[
                    {required:true,message:'验证码不能为空',trigger: 'blur'}
                  ]"
                >
                  <el-input
                    ref="captcha.code"
                    v-model="captchaForm.captcha.code"
                    placeholder="请输入验证码结果"
                    name="captcha.code"
                    type="text"
                    tabindex="3"
                    auto-complete="on"
                  >
                    <template slot="append">
                      <span class="code-container">
                        <el-button v-if="imgFlag === 0" @click="getCaptchaBase64">获取验证码</el-button>
                        <img v-else alt="服务未开启" :src="captchaImgBase64" @click="getCaptchaBase64">
                      </span>
                    </template>
                  </el-input>
                </el-form-item>
              </el-form>
              <el-button
                :loading="captchaLoading"
                type="primary"
                style="width: 120px"
                @click="checkCaptchaCode"
              >验证码校验
              </el-button>
            </template>
          </el-card>
        </el-collapse-item>
        <el-collapse-item>
          <template slot="title">
            <span style="font-size: 16px"><b>示例三：请求加密</b></span>
          </template>
          <el-card style="max-width: 100%">
            <template #default>
              <el-form ref="encryptForm" :model="encryptForm" label-width="80px">
                <el-form-item
                  label="用户账号"
                  prop="account"
                  :rules="[
                    {required:true,message:'用户账号不能为空',trigger: 'blur'},
                    {min:5,max:10,message: '账号长度介于5-10位之间',trigger: 'blur'}
                  ]"
                >
                  <el-input
                    ref="encryptAccount"
                    v-model="encryptForm.account"
                    placeholder="请输入用户账号"
                    autocomplete="off"
                  />
                </el-form-item>
                <el-form-item
                  label="用户密码"
                  prop="password"
                  :rules="[
                    {required:true,message:'用户密码不能为空',trigger: 'blur'},
                    {min:5,max:10,message: '密码长度介于5-10位之间',trigger: 'blur'}
                  ]"
                >
                  <el-input
                    ref="encryptPassword"
                    v-model="encryptForm.password"
                    :type="encryptPasswordType"
                    placeholder="请输入用户密码"
                    autocomplete="off"
                  />
                  <span class="show-pwd" @click="showPwd('encryptPasswordType')">
                    <svg-icon :icon-class="encryptPasswordType === 'password' ? 'eye' : 'eye-open'"/>
                  </span>
                </el-form-item>
                <el-form-item
                  label="加密参数"
                  prop="param"
                  :rules="[
                    {required:true,message:'加密参数不能为空',trigger: 'blur'}
                  ]"
                >
                  <el-input
                    ref="encryptParam"
                    v-model="encryptForm.param"
                    placeholder="请输入加密请求参数"
                    autocomplete="off"
                  />
                </el-form-item>
              </el-form>
              <el-button
                :loading="encryptLoading"
                type="primary"
                style="width: 150px"
                @click="handleEncryptRequest"
              >发送加密请求
              </el-button>
            </template>
          </el-card>
        </el-collapse-item>
        <el-collapse-item>
          <template slot="title">
            <span style="font-size: 16px"><b>示例四：Word工具类</b></span>
          </template>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-card style="max-width: 100%">
                <template #default>
                  <el-form ref="wordForm" :model="wordForm" label-width="80px">
                    <el-form-item
                      label="模板标题"
                      prop="title"
                      :rules="[
                        {required:true,message:'模板标题不能为空',trigger: 'blur'}
                      ]"
                    >
                      <el-input
                        ref="wordTitle"
                        v-model="wordForm.title"
                        placeholder="请输入模板标题"
                        autocomplete="off"
                      />
                    </el-form-item>
                    <el-form-item
                      label="用户名称"
                      prop="name"
                      :rules="[
                        {required:true,message:'用户名称不能为空',trigger: 'blur'}
                      ]"
                    >
                      <el-input
                        ref="wordName"
                        v-model="wordForm.name"
                        placeholder="请输入用户名称"
                        autocomplete="off"
                      />
                    </el-form-item>
                    <el-form-item
                      label="导出日期"
                      prop="date"
                      :rules="[
                        {required:true,message:'导出日期不能为空',trigger: 'blur'}
                      ]"
                    >
                      <el-date-picker
                        v-model="wordForm.date"
                        type="date"
                        placeholder="请选择日期"
                        value-format="yyyy-MM-dd"
                        style="width: 100%"
                        clearable
                      />
                    </el-form-item>
                  </el-form>
                  <el-button
                    :loading="wordLoading"
                    type="primary"
                    style="width: 150px"
                    @click="getWordTemplate"
                  >导出Word模板
                  </el-button>
                </template>
              </el-card>
            </el-col>
            <el-col :span="12">
              <el-card style="max-width: 100%">
                <template #default>
                  <el-upload
                    action=""
                    :limit="1"
                    :show-file-list="false"
                    :file-list="wordFiles"
                    :before-upload="beforeUploadWord"
                    :http-request="getWordParagraphs"
                  >
                    <el-button
                      :loading="wordLoading"
                      type="primary"
                      size="small"
                      style="width: 240px"
                    >获取Word段落
                    </el-button>
                  </el-upload>
                  <el-divider/>
                  <el-upload
                    action=""
                    :limit="1"
                    :show-file-list="false"
                    :file-list="wordFiles"
                    :before-upload="beforeUploadWord"
                    :http-request="getWordTables"
                  >
                    <el-button
                      :loading="wordLoading"
                      type="primary"
                      size="small"
                      style="width: 240px"
                    >获取Word表格
                    </el-button>
                  </el-upload>
                  <el-divider/>
                  <el-upload
                    action=""
                    :limit="1"
                    :show-file-list="false"
                    :file-list="wordFiles"
                    :before-upload="beforeUploadWord"
                    :http-request="getWordImages"
                  >
                    <el-button
                      :loading="wordLoading"
                      type="primary"
                      size="small"
                      style="width: 240px"
                    >获取Word图像
                    </el-button>
                  </el-upload>
                  <div slot="tip" class="el-upload__tip">只能上传docx或doc文件，且不超过500kb</div>
                </template>
              </el-card>
            </el-col>
          </el-row>
        </el-collapse-item>
        <el-collapse-item>
          <template slot="title">
            <span style="font-size: 16px"><b>示例五：PDF工具类</b></span>
          </template>
          <el-card style="max-width: 100%">
            <template #default>
              <el-row>
                <el-col align="center">
                  <el-button
                    :loading="pdfLoading"
                    type="primary"
                    size="small"
                    style="width: 240px"
                    @click="getPdfByFreemarker"
                  >通过Freemarker引擎导出PDF
                  </el-button>
                  <el-button
                    :loading="pdfLoading"
                    type="primary"
                    size="small"
                    style="width: 240px"
                    @click="getPdfByThymeleaf"
                  >通过Thymeleaf引擎导出PDF
                  </el-button>
                  <el-button
                    :loading="pdfLoading"
                    type="primary"
                    size="small"
                    style="width: 240px"
                    @click="getPdfByJte"
                  >通过Jte引擎导出PDF
                  </el-button>
                </el-col>
              </el-row>
              <el-divider/>
              <el-row :gutter="20">
                <el-col :span="12" align="right">
                  <el-upload
                    action=""
                    :limit="1"
                    :show-file-list="false"
                    :file-list="pdfFiles"
                    :before-upload="beforeUploadPdf"
                    :http-request="getPdfParagraphs"
                  >
                    <el-button
                      :loading="pdfLoading"
                      type="primary"
                      size="small"
                      style="width: 240px"
                    >获取PDF段落
                    </el-button>
                  </el-upload>
                </el-col>
                <el-col :span="12" align="left">
                  <el-upload
                    action=""
                    :limit="1"
                    :show-file-list="false"
                    :file-list="pdfFiles"
                    :before-upload="beforeUploadPdf"
                    :http-request="getPdfImages"
                  >
                    <el-button
                      :loading="pdfLoading"
                      type="primary"
                      size="small"
                      style="width: 240px"
                    >获取PDF图像
                    </el-button>
                  </el-upload>
                </el-col>
              </el-row>
              <div slot="tip" class="el-upload__tip" style="text-align: center">只能上传docx文件，且不超过500kb</div>
            </template>
          </el-card>
        </el-collapse-item>
        <el-collapse-item>
          <template slot="title">
            <span style="font-size: 16px"><b>示例六：IP工具类</b></span>
          </template>
          <el-card style="max-width: 100%">
            <template #default>
              <el-form ref="ipForm" :model="ipForm" label-width="120px">
                <el-form-item
                  label="手动输入IP地址"
                  prop="ip"
                  class="ip-form"
                  :rules="[
                    {required:true,message:'IP地址不能为空',trigger: 'blur'}
                  ]"
                >
                  <el-input
                    ref="ip"
                    v-model="ipForm.ip"
                    placeholder="请输入IP地址"
                    name="ip"
                    type="text"
                    tabindex="3"
                    auto-complete="on"
                  >
                    <template slot="append">
                      <span class="ip-container">
                        <el-button @click="getRegion">获取IP所在地区</el-button>
                      </span>
                    </template>
                  </el-input>
                </el-form-item>
              </el-form>
              <el-divider/>
              <el-button
                :loading="ipLoading"
                type="primary"
                size="small"
                style="width: 260px"
                @click="getIpAndRegion"
              >获取当前请求所带IP和对应地区
              </el-button>
            </template>
          </el-card>
        </el-collapse-item>
      </el-collapse>
    </template>
  </div>
</template>

<script>

import {checkEmailCode, getEmailCode, register} from '@/api/auth'
import {Message} from 'element-ui'
import {
  captcha,
  checkCaptcha,
  decryptionRequestParameters,
  exportPdfByFreemarkerTemplate,
  exportPdfByJteTemplate,
  exportPdfByThymeleafTemplate,
  exportWordByTemplate,
  getImagesZipInPdf,
  getImagesZipInWord,
  getIpAndRegionByRequest,
  getParagraphsTxtInPdf,
  getParagraphsTxtInWord,
  getRegionByIp,
  getRsaPublicKey,
  getTablesZipInWord
} from '@/api/example'
import {JSEncrypt} from 'jsencrypt'

export default {
  name: 'Example',
  data() {
    return {
      // 注册/找回密码
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
      checkNewPasswordType: 'password',
      // 验证码校验
      captchaLoading: false,
      captchaForm: {
        captcha: {
          code: undefined,
          uuid: undefined
        }
      },
      imgFlag: 0,
      captchaImgBase64: undefined,
      // 请求加密
      encryptLoading: false,
      encryptForm: {
        account: undefined,
        password: undefined,
        param: undefined
      },
      encryptPasswordType: 'password',
      // Word工具类
      wordLoading: false,
      wordForm: {
        title: undefined,
        name: undefined,
        date: undefined
      },
      wordFiles: [],
      // PDF工具类
      pdfLoading: false,
      pdfFiles: [],
      // IP工具类
      ipLoading: false,
      ipForm: {
        ip: undefined
      }
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
        case 'encryptPasswordType':
          if (this.encryptPasswordType === 'password') {
            this.encryptPasswordType = ''
          } else {
            this.encryptPasswordType = 'password'
          }
          this.$nextTick(() => {
            this.$refs.encryptPassword.focus()
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
            this.resetRegisterForm()
            Message.success(response.msg)
          }).finally(() => {
            this.registerLoading = false
          })
        }
      })
    },
    resetRegisterForm() {
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
            this.resetRetrievePasswordForm()
            Message.success(response.msg)
          }).finally(() => {
            this.retrievePasswordLoading = false
          })
        }
      })
    },
    resetRetrievePasswordForm() {
      this.retrievePasswordForm.account = undefined
      this.retrievePasswordForm.email = undefined
      this.retrievePasswordForm.newPassword = undefined
      this.retrievePasswordForm.checkNewPassword = undefined
      this.retrievePasswordForm.passwordCode = undefined
    },
    getCaptchaBase64() {
      captcha().then(response => {
        this.captchaForm.captcha.uuid = response.data.uuid
        this.captchaImgBase64 = 'data:image/gif;base64,' + response.data.imgBase64
      }).finally(() => {
        this.imgFlag = 1
      })
    },
    checkCaptchaCode() {
      this.$refs['captchaForm'].validate(valid => {
        if (valid) {
          this.captchaLoading = true
          const data = {
            captcha: {
              uuid: this.captchaForm.captcha.uuid,
              code: this.captchaForm.captcha.code
            }
          }
          checkCaptcha(data).then(response => {
            this.resetCaptchaForm()
            Message.success(response.msg)
          }).finally(() => {
            this.captchaLoading = false
            this.getCaptchaBase64()
          })
        }
      })
    },
    resetCaptchaForm() {
      this.captchaForm.captcha.code = undefined
      this.captchaForm.captcha.uuid = undefined
    },
    handleEncryptRequest() {
      this.$refs['encryptForm'].validate(async valid => {
        if (valid) {
          this.encryptLoading = true
          const encryptor = new JSEncrypt()
          const encryptKey = await this.getEncryptKey()
          encryptor.setPublicKey(encryptKey)
          this.encryptForm.password = encryptor.encrypt(this.encryptForm.password)
          this.encryptForm.param = encryptor.encrypt(this.encryptForm.param)
          const data = {
            account: this.encryptForm.account,
            password: this.encryptForm.password
          }
          const param = this.encryptForm.param
          decryptionRequestParameters(data, param).then(response => {
            this.resetEncryptForm()
            this.$message({
              type: 'success',
              dangerouslyUseHTMLString: true,
              message: '请求体：' + JSON.stringify(response.data.exampleEncryptBody) + '<br>请求参数：' + response.data.exampleEncryptParam
            })
          }).finally(() => {
            this.encryptLoading = false
          })
        }
      })
    },
    async getEncryptKey() {
      const response = await getRsaPublicKey()
      return response.msg
    },
    resetEncryptForm() {
      this.encryptForm.account = undefined
      this.encryptForm.password = undefined
      this.encryptForm.param = undefined
    },
    getWordTemplate() {
      this.$refs['wordForm'].validate(valid => {
        if (valid) {
          this.wordLoading = true
          exportWordByTemplate(this.wordForm.title, this.wordForm.name, this.wordForm.date).then(async data => {
            if (data) {
              const blob = new Blob([data.data])
              const url = URL.createObjectURL(blob)
              const link = document.createElement('a')
              link.href = url
              link.download = decodeURIComponent(data.headers['download-filename'])
              document.body.appendChild(link)
              link.click()
              document.body.removeChild(link)
              URL.revokeObjectURL(url)
            } else {
              Message.error('下载失败')
            }
            this.resetWordForm()
          }).catch(() => {
            Message.error('下载失败')
          }).finally(() => {
            this.wordLoading = false
          })
        }
      })
    },
    resetWordForm() {
      this.wordForm.title = undefined
      this.wordForm.name = undefined
      this.wordForm.date = undefined
    },
    beforeUploadWord(file) {
      const isDOCX = file.name.endsWith('.docx') || file.name.endsWith('.doc')
      const isLt500K = file.size / 1024 < 500
      if (!isDOCX) {
        this.$message.error('上传文件只能是 DOCX 或 DOC 格式!')
      }
      if (!isLt500K) {
        this.$message.error('上传文件大小不能超过 500KB!')
      }
      return isDOCX && isLt500K
    },
    getWordParagraphs(wordFile) {
      this.wordLoading = true
      const {file} = wordFile
      const formData = new FormData()
      formData.append('file', file)
      getParagraphsTxtInWord(formData).then(async data => {
        if (data) {
          const blob = new Blob([data.data])
          const url = URL.createObjectURL(blob)
          const link = document.createElement('a')
          link.href = url
          link.download = decodeURIComponent(data.headers['download-filename'])
          document.body.appendChild(link)
          link.click()
          document.body.removeChild(link)
          URL.revokeObjectURL(url)
        } else {
          Message.error('下载失败')
        }
      }).catch(() => {
        Message.error('下载失败')
      }).finally(() => {
        this.wordFiles = []
        this.wordLoading = false
      })
    },
    getWordTables(wordFile) {
      this.wordLoading = true
      console.log(wordFile)
      const {file} = wordFile
      const formData = new FormData()
      formData.append('file', file)
      getTablesZipInWord(formData).then(async data => {
        if (data) {
          const blob = new Blob([data.data])
          const url = URL.createObjectURL(blob)
          const link = document.createElement('a')
          link.href = url
          link.download = decodeURIComponent(data.headers['download-filename'])
          document.body.appendChild(link)
          link.click()
          document.body.removeChild(link)
          URL.revokeObjectURL(url)
        } else {
          Message.error('下载失败')
        }
      }).catch(() => {
        Message.error('下载失败')
      }).finally(() => {
        this.wordFiles = []
        this.wordLoading = false
      })
    },
    getWordImages(wordFile) {
      this.wordLoading = true
      const {file} = wordFile
      const formData = new FormData()
      formData.append('file', file)
      getImagesZipInWord(formData).then(async data => {
        if (data) {
          const blob = new Blob([data.data])
          const url = URL.createObjectURL(blob)
          const link = document.createElement('a')
          link.href = url
          link.download = decodeURIComponent(data.headers['download-filename'])
          document.body.appendChild(link)
          link.click()
          document.body.removeChild(link)
          URL.revokeObjectURL(url)
        } else {
          Message.error('下载失败')
        }
      }).catch(() => {
        Message.error('下载失败')
      }).finally(() => {
        this.wordFiles = []
        this.wordLoading = false
      })
    },
    beforeUploadPdf(file) {
      const isPDF = file.name.endsWith('.pdf')
      const isLt500K = file.size / 1024 < 500
      if (!isPDF) {
        this.$message.error('上传文件只能是 PDF 格式!')
      }
      if (!isLt500K) {
        this.$message.error('上传文件大小不能超过 500KB!')
      }
      return isPDF && isLt500K
    },
    getPdfByFreemarker() {
      this.pdfLoading = true
      exportPdfByFreemarkerTemplate().then(async data => {
        if (data) {
          const blob = new Blob([data.data])
          const url = URL.createObjectURL(blob)
          const link = document.createElement('a')
          link.href = url
          link.download = decodeURIComponent(data.headers['download-filename'])
          document.body.appendChild(link)
          link.click()
          document.body.removeChild(link)
          URL.revokeObjectURL(url)
        } else {
          Message.error('下载失败')
        }
        this.resetWordForm()
      }).catch(() => {
        Message.error('下载失败')
      }).finally(() => {
        this.pdfLoading = false
      })
    },
    getPdfByThymeleaf() {
      this.pdfLoading = true
      exportPdfByThymeleafTemplate().then(async data => {
        if (data) {
          const blob = new Blob([data.data])
          const url = URL.createObjectURL(blob)
          const link = document.createElement('a')
          link.href = url
          link.download = decodeURIComponent(data.headers['download-filename'])
          document.body.appendChild(link)
          link.click()
          document.body.removeChild(link)
          URL.revokeObjectURL(url)
        } else {
          Message.error('下载失败')
        }
        this.resetWordForm()
      }).catch(() => {
        Message.error('下载失败')
      }).finally(() => {
        this.pdfLoading = false
      })
    },
    getPdfByJte() {
      this.pdfLoading = true
      exportPdfByJteTemplate().then(async data => {
        if (data) {
          const blob = new Blob([data.data])
          const url = URL.createObjectURL(blob)
          const link = document.createElement('a')
          link.href = url
          link.download = decodeURIComponent(data.headers['download-filename'])
          document.body.appendChild(link)
          link.click()
          document.body.removeChild(link)
          URL.revokeObjectURL(url)
        } else {
          Message.error('下载失败')
        }
        this.resetWordForm()
      }).catch(() => {
        Message.error('下载失败')
      }).finally(() => {
        this.pdfLoading = false
      })
    },
    getPdfParagraphs(pdfFile) {
      this.pdfLoading = true
      const {file} = pdfFile
      const formData = new FormData()
      formData.append('file', file)
      getParagraphsTxtInPdf(formData).then(async data => {
        if (data) {
          const blob = new Blob([data.data])
          const url = URL.createObjectURL(blob)
          const link = document.createElement('a')
          link.href = url
          link.download = decodeURIComponent(data.headers['download-filename'])
          document.body.appendChild(link)
          link.click()
          document.body.removeChild(link)
          URL.revokeObjectURL(url)
        } else {
          Message.error('下载失败')
        }
      }).catch(() => {
        Message.error('下载失败')
      }).finally(() => {
        this.pdfFiles = []
        this.pdfLoading = false
      })
    },
    getPdfImages(pdfFile) {
      this.pdfLoading = true
      const {file} = pdfFile
      const formData = new FormData()
      formData.append('file', file)
      getImagesZipInPdf(formData).then(async data => {
        if (data) {
          const blob = new Blob([data.data])
          const url = URL.createObjectURL(blob)
          const link = document.createElement('a')
          link.href = url
          link.download = decodeURIComponent(data.headers['download-filename'])
          document.body.appendChild(link)
          link.click()
          document.body.removeChild(link)
          URL.revokeObjectURL(url)
        } else {
          Message.error('下载失败')
        }
      }).catch(() => {
        Message.error('下载失败')
      }).finally(() => {
        this.pdfFiles = []
        this.pdfLoading = false
      })
    },
    getRegion() {
      this.$refs['ipForm'].validate(valid => {
        if (valid) {
          this.ipLoading = true
          getRegionByIp(this.ipForm.ip).then(response => {
            Message.success(response.msg)
            this.resetIpForm()
          }).finally(() => {
            this.ipLoading = false
          })
        }
      })
    },
    resetIpForm() {
      this.ipForm.ip = undefined
    },
    getIpAndRegion() {
      this.ipLoading = true
      getIpAndRegionByRequest().then(response => {
        Message.success(response.msg)
      }).finally(() => {
        this.ipLoading = false
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

.captcha-code-form {
  .code-container {
    padding-top: 5px;
    display: inline-block;

    img {
      height: 30px;
      width: 100px;
      display: inline-block;
    }
  }
}

.ip-form {
  .ip-container {
    padding-top: 5px;
    display: inline-block;
  }
}

</style>
