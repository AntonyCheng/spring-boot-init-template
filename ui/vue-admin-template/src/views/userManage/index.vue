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
                    <el-form-item label="账号">
                      <el-input v-model="queryForm.account" placeholder="请输入账号" style="width: 200px" />
                    </el-form-item>
                    <el-form-item label="名称">
                      <el-input v-model="queryForm.name" placeholder="请输入名称" style="width: 200px" />
                    </el-form-item>
                    <el-form-item label="邮箱">
                      <el-input v-model="queryForm.email" placeholder="请输入邮箱" style="width: 200px" />
                    </el-form-item>
                    <el-form-item label="角色">
                      <el-select v-model="queryForm.role" placeholder="请选择角色" clearable>
                        <el-option
                          v-for="item in roleMapping"
                          :key="item.value"
                          :label="item.label"
                          :value="item.value"
                        />
                      </el-select>
                    </el-form-item>
                    <el-form-item label="状态">
                      <el-select v-model="queryForm.state" placeholder="请选择状态" clearable>
                        <el-option
                          v-for="item in stateMapping"
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
                    <el-button :loading="queryLoading" type="primary" size="small" style="width: 80px" @click="handleQuery">查询</el-button>
                  </template>
                  <template>
                    <el-button :loading="queryLoading" size="small" style="width: 80px" @click="handleReset">重置</el-button>
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
              <el-button type="primary" size="small" style="width: 100px" @click="openAddDialog">添加用户</el-button>
            </template>
            <template>
              <el-button type="success" size="small" style="width: 100px" @click="handleExport">导出用户信息</el-button>
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
          prop="id"
          label="ID"
        />
        <el-table-column
          prop="account"
          label="账号"
        />
        <el-table-column
          prop="name"
          label="名称"
        />
        <el-table-column
          prop="email"
          label="邮箱"
        />
        <el-table-column
          prop="avatar"
          label="头像"
        >
          <template v-slot="scope">
            <el-avatar v-if="scope.row.avatar" :size="35" :src="scope.row.avatar" class="avatar-class" />
            <el-avatar v-else :size="35" style="font-size: large">{{ scope.row.name.at(0) }}</el-avatar>
          </template>
        </el-table-column>
        <el-table-column
          prop="role"
          label="角色"
        >
          <template v-slot="scope">
            {{ scope.row.role === 'admin' ? '管理员' : '用户' }}
          </template>
        </el-table-column>
        <el-table-column
          prop="state"
          label="状态"
        >
          <template v-slot="scope">
            <el-switch
              :value="scope.row.state === 0"
              active-color="#13ce66"
              inactive-color="#ff4949"
              @change="handleState(scope.row.id)"
            />
          </template>
        </el-table-column>
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
            <el-dropdown @command="(data)=>void openUpdateDialog(data,scope.row)">
              <el-button type="primary" size="mini">
                修改
              </el-button>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item command="info">信息</el-dropdown-item>
                <el-dropdown-item command="password">密码</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
            <el-divider direction="vertical" />
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
    <template>
      <el-dialog :show-close="false" title="添加用户" :visible.sync="addDialogVisible" @close="handleCancelAdd">
        <el-form ref="addForm" :model="addForm" label-width="80px">
          <el-form-item
            label="用户账号"
            prop="account"
            :rules="[
              {required:true,message:'账号不能为空',trigger: 'blur'},
              {min:2,max:16,message: '账号长度介于2-16位之间',trigger: 'blur'}
            ]"
          >
            <el-input v-model="addForm.account" autocomplete="off" />
          </el-form-item>
          <el-form-item
            label="用户密码"
            prop="password"
            :rules="[
              {required:true,message:'密码不能为空',trigger: 'blur'},
              {min:5,max:16,message: '密码长度介于5-16位之间',trigger: 'blur'}
            ]"
          >
            <el-input v-model="addForm.password" autocomplete="off" />
          </el-form-item>
          <el-form-item
            label="用户名称"
            prop="name"
            :rules="[
              {required:true,message:'名称不能为空',trigger: 'blur'},
              {min:1,max:16,message: '名称长度介于1-16位之间',trigger: 'blur'}
            ]"
          >
            <el-input v-model="addForm.name" autocomplete="off" />
          </el-form-item>
          <el-form-item
            label="用户邮箱"
            prop="email"
            :rules="[
              {required:true,message:'邮箱不能为空',trigger: 'blur'},
            ]"
          >
            <el-input v-model="addForm.email" autocomplete="off" />
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="handleCancelAdd">取 消</el-button>
          <el-button type="primary" :loading="addLoading" @click="handleAdd">添 加</el-button>
        </div>
      </el-dialog>
    </template>
    <template>
      <el-dialog :show-close="false" :title="updateType === 'info'?'修改信息':'修改密码'" :visible.sync="updateDialogVisible" @close="handleCancelUpdate">
        <el-form v-if="updateType==='info'" ref="updateForm" :model="updateForm" label-width="80px">
          <el-form-item
            label="用户ID"
            prop="id"
          >
            <el-input v-model="updateForm.id" disabled />
          </el-form-item>
          <el-form-item
            label="用户账号"
            prop="account"
            :rules="[
              {required:true,message:'新账号不能为空',trigger: 'blur'},
              {min:2,max:16,message: '新账号长度介于2-16位之间',trigger: 'blur'}
            ]"
          >
            <el-input v-model="updateForm.account" autocomplete="off" />
          </el-form-item>
          <el-form-item
            label="用户名称"
            prop="name"
            :rules="[
              {required:true,message:'新名称不能为空',trigger: 'blur'},
              {min:1,max:16,message: '新名称长度介于1-16位之间',trigger: 'blur'}
            ]"
          >
            <el-input v-model="updateForm.name" autocomplete="off" />
          </el-form-item>
          <el-form-item
            label="用户邮箱"
            prop="email"
            :rules="[
              {required:true,message:'新邮箱不能为空',trigger: 'blur'},
            ]"
          >
            <el-input v-model="updateForm.email" autocomplete="off" />
          </el-form-item>
        </el-form>
        <el-form v-else ref="updateForm" :model="updateForm" label-width="80px">
          <el-form-item
            label="用户账号"
            prop="account"
          >
            <el-input v-model="updateForm.account" disabled />
          </el-form-item>
          <el-form-item
            label="新密码"
            prop="newPassword"
            :rules="[
              {required:true,message:'新密码不能为空',trigger: 'blur'},
              {min:5,max:16,message: '新密码长度介于5-16位之间',trigger: 'blur'}
            ]"
          >
            <el-input v-model="updateForm.newPassword" autocomplete="off" />
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="handleCancelUpdate">取 消</el-button>
          <el-button type="primary" :loading="updateLoading" @click="handleUpdate">修 改</el-button>
        </div>
      </el-dialog>
    </template>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { adminAddUser, adminDeleteUser, adminExportExcel, adminPageUser, adminResetPassword, adminUpdateInfo, adminUpdateState } from '@/api/user'
import { Loading, Message } from 'element-ui'

export default {
  name: 'UserManage',
  data() {
    return {
      addLoading: false,
      queryLoading: false,
      updateLoading: false,
      pageLoading: false,
      queryResult: [],
      queryForm: {
        account: undefined,
        name: undefined,
        email: undefined,
        role: undefined,
        state: undefined,
        page: 1,
        size: 10,
        allowDeep: false
      },
      roleMapping: [
        {
          label: '用户',
          value: 'user'
        },
        {
          label: '管理员',
          value: 'admin'
        }
      ],
      stateMapping: [
        {
          label: '启用',
          value: 0
        },
        {
          label: '禁用',
          value: 1
        }
      ],
      addDialogVisible: false,
      addForm: {
        account: undefined,
        password: undefined,
        name: undefined,
        email: undefined
      },
      updateDialogVisible: false,
      updateType: undefined,
      updateForm: {
        id: undefined,
        account: undefined,
        name: undefined,
        email: undefined,
        newPassword: undefined
      }
    }
  },
  computed: {
    ...mapGetters([
      'role'
    ])
  },
  created() {
    this.pageLoading = true
    adminPageUser(this.queryForm).then(response => {
      this.queryResult = response.data
      this.pageLoading = false
    })
  },
  methods: {
    handleQuery() {
      if (
        this.queryForm.account === undefined &&
        this.queryForm.state === undefined &&
        this.queryForm.name === undefined &&
        this.queryForm.email === undefined &&
        this.queryForm.role === undefined
      ) {
        return
      }
      this.queryForm.page = 1
      this.pageLoading = true
      this.queryLoading = true
      adminPageUser(this.queryForm).then(response => {
        this.queryResult = response.data
        this.pageLoading = false
        this.queryLoading = false
      })
    },
    handleReset() {
      if (
        this.queryForm.account === undefined &&
        this.queryForm.state === undefined &&
        this.queryForm.name === undefined &&
        this.queryForm.email === undefined &&
        this.queryForm.role === undefined
      ) {
        return
      }
      this.resetQueryForm()
      this.pageLoading = true
      adminPageUser(this.queryForm).then(response => {
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
    openAddDialog() {
      this.addDialogVisible = true
    },
    handleAdd() {
      this.$refs['addForm'].validate(valid => {
        if (valid) {
          this.addLoading = true
          const data = {
            account: this.addForm.account,
            password: this.addForm.password,
            name: this.addForm.name,
            email: this.addForm.email
          }
          adminAddUser(data).then(response => {
            if (this.queryResult.total !== 0 && this.queryResult.total % this.queryResult.size === 0) {
              this.queryForm.page++
            }
            this.pageLoading = true
            adminPageUser(this.queryForm).then(response => {
              this.queryResult = response.data
              this.pageLoading = false
            })
            this.resetAddForm()
            this.addDialogVisible = false
            Message.success(response.msg)
          }).finally(() => {
            this.addLoading = false
          })
        }
      })
    },
    handleCancelAdd() {
      this.addDialogVisible = false
      this.resetAddForm()
      this.addLoading = false
    },
    openUpdateDialog(data1, data2) {
      this.updateType = data1
      this.updateForm.id = data2.id
      this.updateForm.account = data2.account
      this.updateForm.name = data2.name
      this.updateForm.email = data2.email
      this.updateDialogVisible = true
    },
    handleUpdate() {
      this.$refs['updateForm'].validate(valid => {
        if (valid) {
          this.updateLoading = true
          if (this.updateType === 'info') {
            const data = {
              id: this.updateForm.id,
              account: this.updateForm.account,
              name: this.updateForm.name,
              email: this.updateForm.email
            }
            adminUpdateInfo(data).then(response => {
              this.pageLoading = true
              adminPageUser(this.queryForm).then(response => {
                this.queryResult = response.data
                this.pageLoading = false
              })
              this.resetUpdateForm()
              this.updateDialogVisible = false
              Message.success(response.msg)
            }).finally(() => {
              this.updateLoading = false
            })
          } else {
            const data = {
              id: this.updateForm.id,
              newPassword: this.updateForm.newPassword
            }
            adminResetPassword(data).then(response => {
              this.pageLoading = true
              adminPageUser(this.queryForm).then(response => {
                this.queryResult = response.data
                this.pageLoading = false
              })
              this.resetUpdateForm()
              this.updateDialogVisible = false
              Message.success(response.msg)
            }).finally(() => {
              this.updateLoading = false
            })
          }
        }
      })
    },
    handleCancelUpdate() {
      this.$refs['updateForm'].resetFields()
      this.updateDialogVisible = false
      this.resetUpdateForm()
      this.updateLoading = false
    },
    handleState(id) {
      const data = {
        id: id
      }
      adminUpdateState(data).then(response => {
        this.pageLoading = true
        adminPageUser(this.queryForm).then(response => {
          this.queryResult = response.data
          this.pageLoading = false
        })
        Message.success(response.msg)
      })
    },
    handleDelete(data) {
      this.$confirm('此操作将删除该用户, 是否继续?', '确认删除', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        adminDeleteUser(data.id).then(response => {
          if (this.queryResult.total % this.queryResult.size === 1) {
            this.queryForm.page--
          }
          this.pageLoading = true
          adminPageUser(this.queryForm).then(response => {
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
      adminPageUser(this.queryForm).then(response => {
        this.queryResult = response.data
        this.pageLoading = false
      })
    },
    handleCurrentChange(val) {
      this.queryForm.page = val
      this.pageLoading = true
      adminPageUser(this.queryForm).then(response => {
        this.queryResult = response.data
        this.pageLoading = false
      })
    },
    async resetQueryForm() {
      this.queryForm.account = undefined
      this.queryForm.name = undefined
      this.queryForm.email = undefined
      this.queryForm.role = undefined
      this.queryForm.state = undefined
      this.queryForm.page = 1
    },
    async resetAddForm() {
      this.addForm.account = undefined
      this.addForm.password = undefined
      this.addForm.name = undefined
      this.addForm.email = undefined
    },
    async resetUpdateForm() {
      this.updateForm.id = undefined
      this.updateForm.account = undefined
      this.updateForm.name = undefined
      this.updateForm.email = undefined
      this.updateForm.newPassword = undefined
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
