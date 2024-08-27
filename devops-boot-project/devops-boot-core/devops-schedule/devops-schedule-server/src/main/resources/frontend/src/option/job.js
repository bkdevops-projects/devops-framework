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
        display: false
      },
      {
        label: '工作组',
        prop: 'groupId',
        search: true,
        type: 'select',
        props: {
          label: 'name',
          value: 'id'
        },
        dicUrl: '/worker/group/names',
        display: false
      },
      {
        label: '调度类型',
        prop: 'scheduleType1', // 这里字段名不能跟下面的scheduleType重复，不然下面框的控制会失效
        dicUrl: '/dict/ScheduleType',
        props: {
          value: 'scheduleType'
        },
        display: false
      },
      {
        label: '调度配置',
        prop: 'scheduleConf',
        display: false
      },
      {
        label: '状态',
        prop: 'triggerStatus',
        search: true,
        type: 'select',
        dicUrl: '/dict/TriggerStatus',
        display: false
      }
    ],
    group: [
      {
        icon: 'el-icon-info',
        label: '基础配置',
        column: [
          {
            label: '任务名称',
            prop: 'name',
            rules: [{
              required: true,
              message: '请输入任务名称',
              trigger: 'blur'
            }]
          },
          {
            label: '工作组',
            prop: 'groupId',
            type: 'select',
            props: {
              label: 'name',
              value: 'id'
            },
            dicUrl: '/worker/group/names',
            rules: [{
              required: true,
              message: '请选择工作组',
              trigger: 'blur'
            }]
          },
          {
            label: '任务描述',
            prop: 'description',
            type: 'textarea',
            maxlength: 256,
            showWordLimit: true,
            span: 24
          }
        ]
      },
      {
        icon: 'el-icon-s-operation',
        label: '调度配置',
        column: [
          {
            label: '调度类型',
            prop: 'scheduleType',
            type: 'select',
            value: 1,
            dicUrl: '/dict/ScheduleType',
            control: (val, form) => {
              if (val === 1) {
                return {
                  scheduleConf: {
                    label: 'Cron',
                    display: false,
                    rules: [{
                      required: false
                    }]
                  }
                }
              } else if (val === 2) {
                return {
                  scheduleConf: {
                    label: '执行时间',
                    type: 'datetime',
                    format: 'yyyy年MM月dd日 HH时mm分ss秒',
                    valueFormat: 'yyyy-MM-dd HH:mm:ss',
                    placeholder: '请选择执行时间',
                    display: true,
                    value: '',
                    rules: [{
                      required: true,
                      message: '请选择执行时间',
                      trigger: 'blur'
                    }]
                  }
                }
              } else if (val === 3) {
                return {
                  scheduleConf: {
                    label: '执行间隔',
                    type: 'number',
                    controlsPosition: '',
                    placeholder: '请输入执行间隔, 单位秒',
                    minRows: 1,
                    display: true,
                    rules: [{
                      required: true,
                      message: '请输入执行间隔',
                      trigger: 'blur'
                    }]
                  }
                }
              } else if (val === 4) {
                return {
                  scheduleConf: {
                    label: 'Cron表达式',
                    display: true,
                    type: 'input',
                    placeholder: '请输入Cron表达式',
                    value: '',
                    rules: [{
                      required: true,
                      message: '请输入Cron表达式',
                      trigger: 'blur'
                    }]
                  }
                }
              }
            },
            rules: [{
              required: true,
              message: '请选择调度类型',
              trigger: 'blur'
            }]
          },
          {
            label: '调度配置',
            prop: 'scheduleConf',
            display: false
          }
        ]
      },
      {
        icon: 'el-icon-s-order',
        label: '任务配置',
        column: [
          {
            label: '运行模式',
            prop: 'jobMode',
            type: 'select',
            value: 1,
            dicUrl: '/dict/JobMode',
            control: (val, form) => {
              if (val === 1) {
                return {
                  image: {
                    display: false
                  },
                  jobHandler: {
                    display: true
                  },
                  source: {
                    display: false
                  },
                }
              } else if (val === 2) {
                return {
                  image: {
                    display: false
                  },
                  jobHandler: {
                    display: false
                  },
                  source: {
                    display: true
                  },
                }
              } else if (val === 3) {
                return {
                  image: {
                    display: true
                  },
                  jobHandler: {
                    display: false
                  },
                  source: {
                    display: true
                  },
                }
              }
            },
            rules: [{
              required: true,
              message: '请选择运行模式',
              trigger: 'blur'
            }]
          },
          {
            label: 'jobHandler',
            prop: 'jobHandler',
            type: 'input',
            display: false,
            rules: [{
              message: '请输入job handler',
              trigger: 'blur'
            }]
          },
          {
            label: '镜像',
            prop: 'image',
            type: 'input',
            display: false,
            rules: [{
              message: '请输入镜像',
              trigger: 'blur'
            }]
          },
          {
            label: '资源',
            prop: 'source',
            type: 'textarea',
            display: false,
            placeholder: '请输入资源',
            maxlength: 10240,
            showWordLimit: true,
            span: 24
          },
          {
            label: '任务参数',
            prop: 'jobParam',
            type: 'textarea',
            value: '{}',
            placeholder: '请输入任务参数(可选，json格式)',
            maxlength: 1024,
            showWordLimit: true,
            span: 24,
            rules: [{
              validator: validateJson,
              message: '请检查json格式',
              trigger: 'blur'
            }]
          }
        ]
      },
      {
        icon: 'el-icon-setting',
        label: '高级配置',
        collapse: false,
        column: [
          {
            label: '过期策略',
            prop: 'misfireStrategy',
            type: 'select',
            value: 1,
            dicUrl: '/dict/MisfireStrategy',
            rules: [{
              required: true,
              message: '请选择过期策略',
              trigger: 'blur'
            }]
          },
          {
            label: '路由策略',
            prop: 'routeStrategy',
            type: 'select',
            value: 1,
            dicUrl: '/dict/RouteStrategy',
            rules: [{
              required: true,
              message: '请选择路由策略',
              trigger: 'blur'
            }]
          },
          {
            label: '阻塞策略',
            prop: 'blockStrategy',
            type: 'select',
            value: 1,
            dicUrl: '/dict/BlockStrategy',
            rules: [{
              required: true,
              message: '请选择阻塞策略',
              trigger: 'blur'
            }]
          },
          {
            label: '超时时间(秒)',
            prop: 'jobTimeout',
            type: 'number',
            controlsPosition: '',
            value: -1,
            rules: [{
              required: true,
              message: '请输入超时时间',
              trigger: 'blur'
            }]
          },
          {
            label: '最大重试次数',
            prop: 'maxRetryCount',
            type: 'number',
            controlsPosition: '',
            minRows: 0,
            maxRows: 10,
            value: 1,
            rules: [{
              required: true,
              message: '请输入最大重试次数',
              trigger: 'blur'
            }]
          }
        ]
      }
    ]
  }
}

const validateJson = (rule, value, callback) => {
  try {
    JSON.parse(value)
  } catch (e) {
    callback(new Error('json格式非法'))
  }
  callback()
}
