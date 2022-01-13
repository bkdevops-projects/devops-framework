<template>
  <basic-container>
    <avue-crud
      v-model="form"
      v-bind="bindVal"
      :page.sync="page"
      :search.sync="params"
      v-on="onEvent"
    >
      <template slot="jobIdSearch">
        <el-select
          v-model="params.jobId"
          clearable
          filterable
          remote
          placeholder="请输入任务名称搜索"
          :remote-method="searchJobs"
          :loading="loading"
        >
          <el-option
            v-for="item in jobs"
            :key="item.name"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
      </template>
    </avue-crud>
  </basic-container>
</template>

<script>
import { list as listJob } from '@/api/job'
export default window.$crudCommon('log', {
  data() {
    return {
      searchLoading: false,
      jobs: []
    }
  },
  created() {
    if (this.$route.query.id) {
      this.params.jobId = this.$route.query.id
    }
  },
  methods: {
    searchJobs(query) {
      this.searchLoading = false
      listJob({
        pageNumber: 1,
        pageSize: 10,
        name: query
      }).then((res) => {
        this.searchLoading = false
        this.jobs = res.data.records
      }).finally(() => {
        this.searchLoading = false
      })
    }
  }

})
</script>
<style lang="scss" scoped>
</style>
