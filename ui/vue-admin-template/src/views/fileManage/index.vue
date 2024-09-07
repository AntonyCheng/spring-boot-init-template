<template>
  <div class="user-manage-container">
    <template>
      <el-collapse accordion>
        <el-collapse-item>
          <template slot="title">
            <span style="font-size: 16px"><b>查询条件</b></span>
          </template>
          <el-card style="max-width: 100%;">
            <template #default>
              <el-row>
                <el-col :span="18">
                  <el-form
                    :inline="true"
                    :model="queryForm"
                    class="demo-form-inline"
                    size="small"
                    label-width="80px"
                  >
                    <el-form-item label="原名称">
                      <el-input v-model="queryForm.originalName" placeholder="请输入原名称" style="width: 200px" />
                    </el-form-item>
                    <el-form-item label="扩展名">
                      <el-input v-model="queryForm.suffix" placeholder="请输入扩展名" style="width: 200px" />
                    </el-form-item>
                    <el-form-item label="OSS类型">
                      <el-select v-model="queryForm.ossType" placeholder="请选择OSS类型" clearable>
                        <el-option
                          v-for="item in ossTypeMapping"
                          :key="item.value"
                          :label="item.label"
                          :value="item.value"
                        />
                      </el-select>
                    </el-form-item>
                  </el-form>
                </el-col>
                <el-col :span="6" style="text-align: center">
                  <template>
                    <el-button
                      :loading="queryLoading"
                      type="primary"
                      size="small"
                      style="width: 80px"
                      @click="handleQuery"
                    >查询
                    </el-button>
                  </template>
                  <template>
                    <el-button :loading="queryLoading" size="small" style="width: 80px" @click="handleReset">重置
                    </el-button>
                  </template>
                </el-col>
              </el-row>
            </template>
          </el-card>
        </el-collapse-item>
        <el-collapse-item>
          <template slot="title">
            <span style="font-size: 16px"><b>更多操作</b></span>
          </template>
          <template>
            <template>
              <el-button type="success" size="small" style="width: 100px" @click="handleExport">导出文件信息</el-button>
            </template>
          </template>
        </el-collapse-item>
      </el-collapse>
    </template>
    <template>
      <el-table
        v-loading="pageLoading"
        :data="queryResult.records"
        stripe
        style="width: 100%"
      >
        <el-table-column
          v-if="false"
          prop="id"
          label="ID"
        />
        <el-table-column
          prop="originalName"
          label="文件名称"
        />
        <el-table-column
          prop="suffix"
          label="扩展名"
        />
        <el-table-column
          prop="size"
          label="文件大小"
        />
        <el-table-column
          prop="url"
          label="文件地址"
        />
        <el-table-column
          prop="ossType"
          label="OSS类型"
          align="center"
        />
        <el-table-column
          prop="createTime"
          label="创建时间"
        />
        <el-table-column
          fixed="right"
          label="操作"
          width="150"
        >
          <template #default="scope">
            <el-button type="danger" size="mini" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </template>
    <template>
      <el-pagination
        :current-page="Number.parseInt(queryResult.current)"
        :page-sizes="[10, 30, 50, 100]"
        :page-size="10"
        layout="total, sizes, prev, pager, next, jumper"
        :total="Number.parseInt(queryResult.total)"
        style="text-align: right;margin-top: 10px"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </template>
  </div>
</template>

<script>
import { adminDeleteFile, adminExportExcel, adminPageFile } from '@/api/file'
import { Loading, Message } from 'element-ui'

export default {
  name: 'FileManage',
  data() {
    return {
      queryLoading: false,
      pageLoading: false,
      queryResult: [],
      queryForm: {
        originalName: undefined,
        suffix: undefined,
        ossType: undefined,
        page: 1,
        size: 10,
        allowDeep: false
      },
      resultMapping: [
        {
          label: '正常',
          value: 0
        },
        {
          label: '异常',
          value: 1
        }
      ],
      ossTypeMapping: [
        {
          label: 'MinIO',
          value: 'minio'
        },
        {
          label: '腾讯云COS',
          value: 'tencent'
        },
        {
          label: '阿里云OSS',
          value: 'ali'
        }
      ]
    }
  },
  created() {
    this.pageLoading = true
    adminPageFile(this.queryForm).then(response => {
      this.queryResult = response.data
      this.pageLoading = false
    })
  },
  methods: {
    handleQuery() {
      if (
        this.queryForm.originalName === undefined &&
        this.queryForm.suffix === undefined &&
        this.queryForm.ossType === undefined
      ) {
        return
      }
      this.queryForm.page = 1
      this.pageLoading = true
      this.queryLoading = true
      adminPageFile(this.queryForm).then(response => {
        this.queryResult = response.data
        this.pageLoading = false
        this.queryLoading = false
      })
    },
    handleReset() {
      if (
        this.queryForm.originalName === undefined &&
        this.queryForm.suffix === undefined &&
        this.queryForm.ossType === undefined
      ) {
        return
      }
      this.resetQueryForm()
      this.pageLoading = true
      adminPageFile(this.queryForm).then(response => {
        this.queryResult = response.data
        this.pageLoading = false
      })
    },
    handleExport() {
      const downloadLoadingInstance = Loading.service({
        text: '正在下载数据，请稍候',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)'
      })
      adminExportExcel().then(async data => {
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
        downloadLoadingInstance.close()
      }).catch(() => {
        Message.error('下载失败')
        downloadLoadingInstance.close()
      })
    },
    handleDelete(data) {
      this.$confirm('此操作将删除该日志, 是否继续?', '确认删除', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        adminDeleteFile(data.id).then(response => {
          if (this.queryResult.total % this.queryResult.size === 1) {
            this.queryForm.page--
          }
          this.pageLoading = true
          adminPageFile(this.queryForm).then(response => {
            this.queryResult = response.data
            this.pageLoading = false
          })
          Message.success(response.msg)
        })
      })
    },
    handleSizeChange(val) {
      this.queryForm.size = val
      this.pageLoading = true
      adminPageFile(this.queryForm).then(response => {
        this.queryResult = response.data
        this.pageLoading = false
      })
    },
    handleCurrentChange(val) {
      this.queryForm.page = val
      this.pageLoading = true
      adminPageFile(this.queryForm).then(response => {
        this.queryResult = response.data
        this.pageLoading = false
      })
    },
    async resetQueryForm() {
      this.queryForm.originalName = undefined
      this.queryForm.suffix = undefined
      this.queryForm.ossType = undefined
      this.queryForm.page = 1
    }
  }
}
</script>

<style lang="scss">
.user-manage {
  &-container {
    margin: 30px;
  }

  &-text {
    font-size: 30px;
    line-height: 46px;
  }
}

.avatar-class {
  img {
    width: 100%;
    background-size: cover;
  }
}
</style>
