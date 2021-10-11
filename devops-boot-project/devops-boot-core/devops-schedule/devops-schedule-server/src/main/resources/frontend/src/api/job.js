import request from '@/utils/request'

export const list = (data) => request({
  url: '/job/list',
  method: 'get',
  params: data
})

export const create = (data) => request({
  url: '/job/create',
  method: 'post',
  data: data
})

export const update = (id, data) => request({
  url: '/job/update',
  method: 'post',
  data: data
})

export const del = (id) => request({
  url: '/job/delete',
  method: 'delete',
  params: { id }
})

export const stop = (id) => request({
  url: '/job/stop',
  method: 'post',
  params: { id }
})

export const start = (id) => request({
  url: '/job/start',
  method: 'post',
  params: { id }
})
