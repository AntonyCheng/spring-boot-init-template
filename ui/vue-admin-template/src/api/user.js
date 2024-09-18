import request from '@/utils/request'

export function updateAccount(data) {
  return request({
    url: '/user/update/account',
    method: 'put',
    data
  })
}

export function updateName(data) {
  return request({
    url: '/user/update/name',
    method: 'put',
    data
  })
}

export function updateEmail(data) {
  return request({
    url: '/user/update/email',
    method: 'put',
    data
  })
}

export function updatePassword(data) {
  return request({
    url: '/user/update/password',
    method: 'put',
    data
  })
}

export function updateAvatar(data) {
  return request({
    url: '/user/update/avatar',
    method: 'put',
    data
  })
}

export function adminPageUser(data) {
  return request({
    url: '/admin/user/page',
    method: 'get',
    params: data
  })
}

export function adminAddUser(data) {
  return request({
    url: '/admin/user/add',
    method: 'post',
    data
  })
}

export function adminDeleteUser(data) {
  return request({
    url: '/admin/user/delete/' + data,
    method: 'delete'
  })
}

export function adminUpdateInfo(data) {
  return request({
    url: '/admin/user/update/info',
    method: 'put',
    data
  })
}

export function adminUpdateState(data) {
  return request({
    url: '/admin/user/update/state',
    method: 'put',
    data
  })
}

export function adminResetPassword(data) {
  return request({
    url: '/admin/user/reset/password',
    method: 'put',
    data
  })
}

export function adminExportExcel() {
  return request({
    url: '/admin/user/export',
    method: 'get',
    // 后端传来二进制流是需要修改为blob类型
    responseType: 'blob'
  })
}

export function adminExportUserTemplate() {
  return request({
    url: '/admin/user/template',
    method: 'get',
    // 后端传来二进制流是需要修改为blob类型
    responseType: 'blob'
  })
}

export function adminImportUser(data) {
  return request({
    url: '/admin/user/import',
    method: 'post',
    data
  })
}
