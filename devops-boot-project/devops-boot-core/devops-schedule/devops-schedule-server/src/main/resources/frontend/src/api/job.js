import request from '@/utils/request'

function encodeBase64(data) {
  data.source = btoa(data.source)
  return data
}

function decodeBase64(res) {
  for (const item of res.data.records) {
    item.source = atob(item.source)
  }
  return res
}

export const list = (data) => request({
  url: '/job/list',
  method: 'get',
  params: data
}).then(res => decodeBase64(res))

export const create = (data) => request({
  url: '/job/create',
  method: 'post',
  data: encodeBase64(data)
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
