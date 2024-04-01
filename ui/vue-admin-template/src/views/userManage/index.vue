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
                    <el-button type="primary" size="small" style="width: 60px" @click="handleQuery">查询</el-button>
                  </template>
                  <template>
                    <el-button size="small" style="width: 60px" @click="handleReset">重置</el-button>
                  </template>
                </el-col>
              </el-row>
            </template>
            <template />
          </el-card>
        </el-collapse-item>
      </el-collapse>
    </template>
    <template>
      <el-table
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
          prop="avatar"
          label="头像"
        />
        <el-table-column
          prop="role"
          label="角色"
        />
        <el-table-column
          prop="state"
          label="状态"
        />
        <el-table-column
          prop="createTime"
          label="创建时间"
        />
      </el-table>
    </template>
    <template>
      <el-divider />
    </template>
    <template>
      <el-pagination
        :current-page="Number.parseInt(queryResult.current)"
        :page-sizes="[10, 30, 50, 100, 150, 300, 500]"
        :page-size="10"
        layout="total, sizes, prev, pager, next, jumper"
        :total="Number.parseInt(queryResult.total)"
        style="text-align: right"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </template>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { pageUser } from '@/api/admin'

export default {
  name: 'UserManage',
  data() {
    return {
      queryResult: [],
      queryForm: {
        account: undefined,
        name: undefined,
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
      ]
    }
  },
  computed: {
    ...mapGetters([
      'role'
    ])
  },
  created() {
    pageUser(this.queryForm).then(response => {
      this.queryResult = response.data
    })
  },
  methods: {
    handleQuery() {
      pageUser(this.queryForm).then(response => {
        this.queryResult = response.data
      })
    },
    handleReset() {
      this.resetQueryForm()
      pageUser(this.queryForm).then(response => {
        this.queryResult = response.data
      })
    },
    handleSizeChange(val) {
      this.queryForm.size = val
      pageUser(this.queryForm).then(response => {
        this.queryResult = response.data
      })
    },
    handleCurrentChange(val) {
      this.queryForm.page = val
      pageUser(this.queryForm).then(response => {
        this.queryResult = response.data
      })
    },
    async resetQueryForm() {
      this.queryForm.account = undefined
      this.queryForm.name = undefined
      this.queryForm.role = undefined
      this.queryForm.state = undefined
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
</style>
