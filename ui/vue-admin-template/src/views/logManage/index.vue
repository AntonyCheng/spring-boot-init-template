<template>
  <div class="user-manage-container">
    <template>
      <el-collapse>
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
                    <el-form-item label="接口URI">
                      <el-input v-model="queryForm.uri" placeholder="请输入接口URI" style="width: 200px" />
                    </el-form-item>
                    <el-form-item label="操作描述">
                      <el-input v-model="queryForm.description" placeholder="请输入操作描述" style="width: 200px" />
                    </el-form-item>
                    <el-form-item label="操作类型">
                      <el-select v-model="queryForm.operator" placeholder="请选择操作类型" clearable>
                        <el-option
                          v-for="item in operatorMapping"
                          :key="item.value"
                          :label="item.label"
                          :value="item.value"
                        />
                      </el-select>
                    </el-form-item>
                    <el-form-item label="请求方法">
                      <el-input v-model="queryForm.requestMethod" placeholder="请输入请求方法" style="width: 200px" />
                    </el-form-item>
                    <el-form-item label="方法名称">
                      <el-input v-model="queryForm.method" placeholder="请输入方法名称" style="width: 200px" />
                    </el-form-item>
                    <el-form-item label="用户账号">
                      <el-input v-model="queryForm.userAccount" placeholder="请输入用户账号" style="width: 200px" />
                    </el-form-item>
                    <el-form-item label="用户地点">
                      <el-input v-model="queryForm.location" placeholder="请输入用户地点" style="width: 200px" />
                    </el-form-item>
                    <el-form-item label="操作结果">
                      <el-select v-model="queryForm.result" placeholder="请选择操作结果" clearable>
                        <el-option
                          v-for="item in resultMapping"
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
              <el-button type="success" size="small" style="width: 100px" @click="handleExport">导出日志信息</el-button>
            </template>
            <template>
              <el-button type="danger" size="small" style="width: 100px" @click="handleClear">清空日志</el-button>
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
          prop="uri"
          label="接口URI"
        />
        <el-table-column
          prop="description"
          label="描述"
        />
        <el-table-column
          prop="operator"
          label="类型"
          align="center"
        />
        <el-table-column
          prop="requestMethod"
          label="请求方法"
          align="center"
        />
        <el-table-column
          prop="method"
          label="方法名称"
        />
        <el-table-column
          prop="userAccount"
          label="用户账号"
        />
        <el-table-column
          prop="ip"
          label="用户IP"
        />
        <el-table-column
          prop="location"
          label="用户地点"
        />
        <el-table-column
          prop="param"
          label="参数"
          width="180"
        >
          <template v-slot="scope">
            <el-input type="textarea" :rows="6" resize="none" disabled style="font-size: small" :value="JSON.stringify(JSON.parse(scope.row.param), null, '  ')" />
          </template>
        </el-table-column>
        <el-table-column
          prop="result"
          label="结果"
          align="center"
        >
          <template v-slot="scope">
            <el-button v-if="scope.row.result === 0" type="primary" size="mini">
              正常
            </el-button>
            <el-button v-else type="danger" size="mini">
              异常
            </el-button>
          </template>
        </el-table-column>
        <el-table-column
          prop="json"
          label="响应内容"
          align="center"
        >
          <template v-slot="scope">
            <el-popover
              placement="bottom"
              width="500"
              trigger="click"
            >
              <el-button v-if="scope.row.result === 0" slot="reference" type="success" size="mini">查看内容</el-button>
              <el-button v-else slot="reference" type="danger" size="mini">查看内容</el-button>
              <!--              <el-input type="textarea" :rows="10" :value="JSON.stringify(JSON.parse(scope.row.json), null, '\t')" />-->
              <el-input type="textarea" :rows="10" :value="scope.row.json" />
            </el-popover>
          </template>
        </el-table-column>
        <el-table-column
          prop="time"
          label="耗时（ms）"
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
import { adminClearLog, adminDeleteLog, adminExportExcel, adminPageLog } from '@/api/log'
import { Loading, Message } from 'element-ui'

export default {
  name: 'LogManage',
  data() {
    return {
      queryLoading: false,
      pageLoading: false,
      queryResult: [],
      queryForm: {
        uri: undefined,
        description: undefined,
        operator: undefined,
        requestMethod: undefined,
        method: undefined,
        userAccount: undefined,
        location: undefined,
        result: undefined,
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
      operatorMapping: [
        {
          label: '其他',
          value: 0
        },
        {
          label: '增加',
          value: 1
        },
        {
          label: '删除',
          value: 2
        },
        {
          label: '查询',
          value: 3
        },
        {
          label: '修改',
          value: 4
        },
        {
          label: '导入',
          value: 5
        },
        {
          label: '导出',
          value: 6
        }
      ]
    }
  },
  created() {
    this.pageLoading = true
    adminPageLog(this.queryForm).then(response => {
      this.queryResult = response.data
      this.pageLoading = false
    })
  },
  methods: {
    handleQuery() {
      if (
        this.queryForm.uri === undefined &&
        this.queryForm.description === undefined &&
        this.queryForm.operator === undefined &&
        this.queryForm.requestMethod === undefined &&
        this.queryForm.method === undefined &&
        this.queryForm.userAccount === undefined &&
        this.queryForm.location === undefined &&
        this.queryForm.result === undefined
      ) {
        return
      }
      this.queryForm.page = 1
      this.pageLoading = true
      this.queryLoading = true
      adminPageLog(this.queryForm).then(response => {
        this.queryResult = response.data
        this.pageLoading = false
        this.queryLoading = false
      })
    },
    handleReset() {
      if (
        this.queryForm.uri === undefined &&
        this.queryForm.description === undefined &&
        this.queryForm.operator === undefined &&
        this.queryForm.requestMethod === undefined &&
        this.queryForm.method === undefined &&
        this.queryForm.userAccount === undefined &&
        this.queryForm.location === undefined &&
        this.queryForm.result === undefined
      ) {
        return
      }
      this.resetQueryForm()
      this.pageLoading = true
      adminPageLog(this.queryForm).then(response => {
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
        adminDeleteLog(data.id).then(response => {
          if (this.queryResult.total % this.queryResult.size === 1) {
            this.queryForm.page--
          }
          this.pageLoading = true
          adminPageLog(this.queryForm).then(response => {
            this.queryResult = response.data
            this.pageLoading = false
          })
          Message.success(response.msg)
        })
      })
    },
    handleClear() {
      this.$confirm('此操作将清空所有日志, 是否继续?', '确认清空', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        adminClearLog().then(response => {
          this.pageLoading = true
          adminPageLog(this.queryForm).then(response => {
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
      adminPageLog(this.queryForm).then(response => {
        this.queryResult = response.data
        this.pageLoading = false
      })
    },
    handleCurrentChange(val) {
      this.queryForm.page = val
      this.pageLoading = true
      adminPageLog(this.queryForm).then(response => {
        this.queryResult = response.data
        this.pageLoading = false
      })
    },
    async resetQueryForm() {
      this.queryForm.uri = undefined
      this.queryForm.description = undefined
      this.queryForm.operator = undefined
      this.queryForm.requestMethod = undefined
      this.queryForm.method = undefined
      this.queryForm.userAccount = undefined
      this.queryForm.location = undefined
      this.queryForm.result = undefined
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
