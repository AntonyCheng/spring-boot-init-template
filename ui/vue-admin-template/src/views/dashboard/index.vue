<template>
  <div class="dashboard-container">
    <el-card style="max-width: 100%">
      <template #header>
        <div class="card-header">
          <span style="font-size: 24px">我的信息</span>
        </div>
      </template>
      <template>
        <el-row>
          <el-col align="center">
            <el-upload
              class="avatar-uploader"
              action=""
              :show-file-list="false"
              :before-upload="handleBefore"
              :http-request="handleSubmit"
              :on-success="handleSuccess"
            >
              <img v-if="avatar" :src="avatar" class="avatar" alt="userAvatar">
              <!-- todo: 新增默认头像 -->
              <i v-else class="el-icon-plus avatar-uploader-icon" />
            </el-upload>
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
      console.log(isJPG, isPNG, isLt1M)
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
      console.log('file', file)
      const formData = new FormData()
      formData.append('file', file)
      return updateAvatar(formData).then(result => {
        this.getUserInfo()
        return result
      }).catch(error => {
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

.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.avatar-uploader .el-upload:hover {
  border-color: #409EFF;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  line-height: 178px;
  text-align: center;
}

.avatar {
  width: 178px;
  height: 178px;
  display: block;
}
</style>
