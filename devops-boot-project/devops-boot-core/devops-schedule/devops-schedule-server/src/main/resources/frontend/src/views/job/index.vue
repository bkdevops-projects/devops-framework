<template>
  <basic-container>
    <avue-crud v-model="form" v-bind="bindVal" :page.sync="page" v-on="onEvent">
      <template slot="menu" slot-scope="{ type, size, row }">
        <el-button v-if="row.triggerStatus==0" icon="el-icon-check" :size="size" :type="type" @click.stop="startJob(row)">启动</el-button>
        <el-button v-if="row.triggerStatus==1" icon="el-icon-remove" :size="size" :type="type" @click.stop="stopJob(row)">停止</el-button>
        <el-button icon="el-icon-tickets" :size="size" :type="type" @click.stop="showLogs(row)">查看日志</el-button>
      </template>
      <template slot="triggerStatus" slot-scope="scope">
        <el-tag :type="scope.row.triggerStatus===0 ? 'danger' : 'success' ">{{ scope.label }}</el-tag>
      </template>
    </avue-crud>
  </basic-container>
</template>

<script>
import { stop, start } from '@/api/job'
export default window.$crudCommon('job', {
  data() {
    return {}
  },
  methods: {
    stopJob(row) {
      this.$confirm(`确定停止任务[${row.name}]?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return stop(row.id)
      }).then(() => {
        this.$message.success('停止成功')
        this.getList()
      })
    },
    startJob(row) {
      this.$confirm(`确定启动任务[${row.name}]?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return start(row.id)
      }).then(() => {
        this.$message.success('启动成功')
        this.getList()
      })
    },
    showLogs(row) {
      this.$router.push({
        path: '/log/index',
        query: {
          id: row.id
        }
      })
    }
  }
})
</script>
<style lang="scss" scoped>
</style>
