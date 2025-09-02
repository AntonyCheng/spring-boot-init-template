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
    url: '/user/page',
    method: 'get',
    params: data
  })
}

export function adminAddUser(data) {
  return request({
    url: '/user/add',
    method: 'post',
    data
  })
}

export function adminDeleteUser(data) {
  return request({
    url: '/user/delete/' + data,
    method: 'delete'
  })
}

export function adminUpdateInfo(data) {
  return request({
    url: '/user/update/info',
    method: 'put',
    data
  })
}

export function adminUpdateState(data) {
  return request({
    url: '/user/update/state',
    method: 'put',
    data
  })
}

export function adminResetPassword(data) {
  return request({
    url: '/user/reset/password',
    method: 'put',
    data
  })
}

export function adminExportExcel() {
  return request({
    url: '/user/export',
    method: 'get',
    // 后端传来二进制流是需要修改为blob类型
    responseType: 'blob'
  })
}

export function adminExportUserTemplate() {
  return request({
    url: '/user/template',
    method: 'get',
    // 后端传来二进制流是需要修改为blob类型
    responseType: 'blob'
  })
}

export function adminImportUser(data) {
  return request({
    url: '/user/import',
    method: 'post',
    data
  })
}
