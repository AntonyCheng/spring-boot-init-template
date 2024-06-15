import request from '@/utils/request'

export function adminPageLog(data) {
  return request({
    url: '/admin/log/page',
    method: 'get',
    params: data
  })
}

export function adminClearLog() {
  return request({
    url: '/admin/log/clear',
    method: 'get'
  })
}

export function adminDeleteLog(data) {
  return request({
    url: '/admin/log/delete/' + data,
    method: 'delete'
  })
}

export function adminExportExcel() {
  return request({
    url: '/admin/log/export',
    method: 'get',
    // 后端传来二进制流是需要修改为blob类型
    responseType: 'blob'
  })
}
