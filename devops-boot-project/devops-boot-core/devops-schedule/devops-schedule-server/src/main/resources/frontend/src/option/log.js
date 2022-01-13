export default (safe) => {
  return {
    menu: false,
    index: false,
    card: true,
    align: 'center',
    headerAlign: 'center',
    labelPosition: 'right',
    labelWidth: 120,
    border: true,
    stripe: true,
    column: [
      {
        label: '任务id',
        prop: 'jobId',
        search: true
      },
      {
        label: '调度时间',
        prop: 'triggerTime',
        type: 'datetime',
        format: 'yyyy-MM-dd HH:mm:ss',
        valueFormat: 'yyyy-MM-dd HH:mm:ss',
        searchSpan: 12,
        searchRange: true,
        search: true
      },
      {
        label: '调度结果',
        prop: 'triggerCode',
        dicUrl: '/dict/TriggerCode'
      },
      {
        label: '调度备注',
        prop: 'triggerMsg'
      },
      {
        label: '执行时间',
        prop: 'executionTime'
      },
      {
        label: '执行结果',
        prop: 'executionCode',
        dicUrl: '/dict/ExecutionCode'
      },
      {
        label: '执行备注',
        prop: 'executionMsg'
      }
    ]
  }
}
