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
