import request from '@/utils/request'

export function pageUser(data) {
  return request({
    url: '/admin/user/page',
    method: 'get',
    params: data
  })
}

export function addUser(data) {
  return request({
    url: '/admin/user/add',
    method: 'post',
    data
  })
}

export function deleteUser(data) {
  return request({
    url: '/admin/user/delete/' + data,
    method: 'delete'
  })
}

export function updateInfo(data) {
  return request({
    url: '/admin/user/update/info',
    method: 'put',
    data
  })
}

export function updateState(data) {
  return request({
    url: '/admin/user/update/state/' + data,
    method: 'put'
  })
}

export function resetPassword(data) {
  return request({
    url: '/admin/user/reset/password',
    method: 'put',
    data
  })
}

export function exportExcel() {
  return request({
    url: '/admin/user/export',
    method: 'get',
    // 后端传来二进制流是需要修改为blob类型
    responseType: 'blob'
  })
}
