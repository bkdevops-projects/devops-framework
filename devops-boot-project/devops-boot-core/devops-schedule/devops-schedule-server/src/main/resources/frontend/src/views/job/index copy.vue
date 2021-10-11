<template>
  <basic-container>
    <avue-crud
      ref="crud"
      v-model="form"
      :data="data"
      :option="option"
      :page.sync="page"
      :table-loading="loading"
      @on-load="getList"
      @row-update="rowUpdate"
      @row-save="rowSave"
      @row-del="rowDelete"
      @refresh-change="refreshChange"
      @search-reset="searchChange"
      @search-change="searchChange"
    />
  </basic-container>
</template>

<script>
import { list, create, update, del } from '@/api/job'
import option from '@/option/job'
export default {
  data() {
    return {
      page: {},
      form: {},
      params: {},
      loading: false,
      data: [],
      option: option(this)
    }
  },
  created() {},
  methods: {
    getList() {
      this.loading = true
      const data = Object.assign(
        {
          pageNumber: this.page.currentPage,
          pageSize: this.page.pageSize
        },
        this.params
      )
      this.data = []
      list(data).then((res) => {
        this.loading = false
        this.page.total = res.data.totalPages
        this.data = res.data.records
      })
    },
    rowSave(row, done, loading) {
      create(row).then(() => {
        this.$message.success('新增成功')
        done()
        this.getList()
      }).catch(() => {
        loading()
      })
    },
    rowUpdate(row, index, done, loading) {
      update(row).then(() => {
        this.$message.success('修改成功')
        done()
        this.getList()
      }).catch(() => {
        loading()
      })
    },
    rowDelete(row) {
      this.$confirm('此操作将永久删除, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return del(row.id)
      }).then(() => {
        this.$message.success('删除成功')
        this.getList()
      })
    },
    searchChange(params, done) {
      if (done) done()
      this.params = params
      this.page.currentPage = 1
      this.getList()
      this.$message.success('搜索成功')
    },
    refreshChange() {
      this.getList()
      this.$message.success('刷新成功')
    }
  }
}
</script>
