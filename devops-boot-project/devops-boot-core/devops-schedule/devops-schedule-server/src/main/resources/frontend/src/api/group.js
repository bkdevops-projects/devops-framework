import request from '@/utils/request'

export const listNames = () => request({
  url: '/worker/group/names',
  method: 'get'
})

export const list = (data) => request({
  url: '/worker/group/list',
  method: 'get',
  params: data
})

export const create = (data) => request({
  url: '/worker/group/create',
  method: 'post',
  data: data
})

export const update = (id, data) => request({
  url: '/worker/group/update',
  method: 'post',
  data: data
})

export const del = (id) => request({
  url: '/worker/group//delete',
  method: 'delete',
  params: { id }
})
