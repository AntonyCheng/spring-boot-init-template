import request from '@/utils/request'

export function login(data) {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

export function getInfo() {
  return request({
    url: '/auth/info',
    method: 'get'
  })
}

export function logout() {
  return request({
    url: '/auth/logout',
    method: 'delete'
  })
}

// 以下均是示例模块接口
export function register(data) {
  return request({
    url: '/auth/register',
    method: 'post',
    data
  })
}

export function checkEmailCode(data) {
  return request({
    url: '/auth/check/email/code',
    method: 'post',
    data
  })
}

export function getEmailCode(data) {
  return request({
    url: '/auth/email/code',
    method: 'post',
    data
  })
}
