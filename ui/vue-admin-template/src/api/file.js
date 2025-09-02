import request from '@/utils/request'

export function adminPageFile(data) {
  return request({
    url: '/file/page',
    method: 'get',
    params: data
  })
}

export function adminAddFile(data) {
  return request({
    url: '/file/add',
    method: 'post',
    data
  })
}

export function adminDeleteFile(data) {
  return request({
    url: '/file/delete/' + data,
    method: 'delete'
  })
}

export function adminExportExcel() {
  return request({
    url: '/file/export',
    method: 'get',
    // 后端传来二进制流是需要修改为blob类型
    responseType: 'blob'
  })
}
