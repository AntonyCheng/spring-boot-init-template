<template>
  <div class="user-manage-container">
    {{ id }}::{{ account }}::{{ name }}::{{ avatar }}::{{ role }}
  </div>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  name: 'UserManage',
  data() {
    return {
      dialogVisible: false,
      dialogType: ''
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
    async getUserInfo() {
      await this.$store.dispatch('auth/getInfo')
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
