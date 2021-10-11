export default (safe) => {
  return {
    index: true,
    card: true,
    align: 'center',
    headerAlign: 'center',
    labelPosition: 'right',
    labelWidth: 120,
    border: true,
    stripe: true,
    column: [
      {
        label: '名称',
        prop: 'name',
        search: true,
        maxlength: 32,
        rules: [{
          required: true,
          message: '请输入工作组名称',
          trigger: 'blur'
        }]
      },
      {
        label: '注册类型',
        prop: 'discoveryType',
        type: 'select',
        value: 1,
        dicUrl: '/dict/DiscoveryType',
        rules: [{
          required: true,
          message: '请选择注册类型',
          trigger: 'blur'
        }],
        control: (val, form) => {
          if (val === 3) {
            return {
              registryList: {
                label: '地址列表',
                type: 'array',
                display: true
              }
            }
          } else {
            return {
              registryList: {
                label: '地址列表',
                type: 'array',
                display: false
              }
            }
          }
        }
      },
      {
        label: '地址列表',
        prop: 'registryList',
        type: 'array'
      }
    ]
  }
}
