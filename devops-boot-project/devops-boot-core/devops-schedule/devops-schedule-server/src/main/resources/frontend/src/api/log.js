import request from '@/utils/request'

export const list = (data) => request({
  url: '/log/list',
  method: 'get',
  params: data
})

export const del = (id) => request({
  url: '/log/delete',
  method: 'delete',
  params: { id }
})
