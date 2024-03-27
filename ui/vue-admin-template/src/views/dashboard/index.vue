<template>
  <div class="dashboard-container">
    <el-card style="max-width: 100%">
      <template #header>
        <div class="card-header">
          <span style="font-size: 16px"><b>我的头像</b></span>
        </div>
      </template>
      <template #default>
        <el-row>
          <el-col align="center">
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
              <el-avatar v-if="avatar" :size="100" :src="avatar" />
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
                    <el-col :span="12">
                      {{ id }}
                    </el-col>
                  </el-row>
                </template>
              </el-descriptions-item>
              <el-descriptions-item :span="2">
                <template slot="label">
                  <i class="el-icon-user-solid" />
                  账号
                </template>
                <template>
                  <el-row>
                    <el-col :span="12">
                      {{ id }}
                    </el-col>
                    <el-col :span="12" style="text-align: end">
                      <el-button type="primary" size="mini">修改账号</el-button>
                      <el-button type="primary" size="mini">修改密码</el-button>
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
                    <el-col :span="12">
                      {{ name }}
                    </el-col>
                    <el-col :span="12" style="text-align: end">
                      <el-button type="primary" size="mini">修改名称</el-button>
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
                    <el-col :span="12">
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
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { updateAvatar } from '@/api/user'

export default {
  name: 'Dashboard',
  data() {
    return {
      fileList: []
    }
  },
  computed: {
    ...mapGetters([
      'id',
      'account',
      'name',
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
      return updateAvatar(formData).then(result => {
        this.getUserInfo()
        this.fileList = []
        return result
      }).catch(error => {
        this.fileList = []
        return error
      })
    },
    handleSuccess(response) {
      console.log('response:', response)
    },
    async getUserInfo() {
      await this.$store.dispatch('auth/getInfo')
    }
  }
}
</script>

<style lang="scss" scoped>
.dashboard {
  &-container {
    margin: 30px;
  }

  &-text {
    font-size: 30px;
    line-height: 46px;
  }
}
</style>
