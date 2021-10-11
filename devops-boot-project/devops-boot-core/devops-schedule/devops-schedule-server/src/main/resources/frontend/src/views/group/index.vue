<template>
  <basic-container>
    <avue-crud
      v-model="form"
      v-bind="bindVal"
      :page.sync="page"
      v-on="onEvent"
    />
  </basic-container>
</template>

<script>

export default window.$crudCommon('group', {
  data() {
    return {}
  },
  methods: {
  }
})
</script>
<style lang="scss" scoped>
</style>
