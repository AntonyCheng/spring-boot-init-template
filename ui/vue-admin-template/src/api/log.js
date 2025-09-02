import request from '@/utils/request'

export function adminPageLog(data) {
  return request({
    url: '/log/page',
    method: 'get',
    params: data
  })
}

export function adminClearLog() {
  return request({
    url: '/log/clear',
    method: 'delete'
  })
}

export function adminDeleteLog(data) {
  return request({
    url: '/log/delete/' + data,
    method: 'delete'
  })
}

export function adminExportExcel() {
  return request({
    url: '/log/export',
    method: 'get',
    // 后端传来二进制流是需要修改为blob类型
    responseType: 'blob'
  })
}
