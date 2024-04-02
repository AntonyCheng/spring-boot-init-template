import request from '@/utils/request'

export function pageUser(data) {
  return request({
    url: '/admin/page',
    method: 'get',
    params: data
  })
}

export function addUser(data) {
  return request({
    url: '/admin/add',
    method: 'post',
    data
  })
}

export function deleteUser(data) {
  return request({
    url: '/admin/delete/' + data,
    method: 'delete'
  })
}

export function updateInfo(data) {
  return request({
    url: '/admin/update/info',
    method: 'put',
    data
  })
}

export function updateState(data) {
  return request({
    url: '/admin/update/state/' + data,
    method: 'put'
  })
}

export function resetPassword(data) {
  return request({
    url: '/admin/reset/password',
    method: 'put',
    data
  })
}

export function exportExcel() {
  return request({
    url: '/admin/export',
    method: 'get',
    // 后端传来二进制流是需要修改为blob类型
    responseType: 'blob'
  })
}
