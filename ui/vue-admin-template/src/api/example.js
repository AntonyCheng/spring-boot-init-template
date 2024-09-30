import request from '@/utils/request'

export function checkCaptcha(data) {
  return request({
    url: '/example/check/captcha',
    method: 'post',
    data
  })
}

export function decryptionRequestParameters(data, param) {
  return request({
    url: '/example/encrypt',
    method: 'post',
    params: {
      exampleEncryptParam: param
    },
    data
  })
}

export function exportWordByTemplate(title, name, date) {
  return request({
    url: '/example/word/template',
    method: 'get',
    params: {
      title: title,
      name: name,
      date: date
    }
  })
}

export function getParagraphsTxtInWord(file) {
  return request({
    url: '/example/word/txt/paragraphs',
    method: 'post',
    file,
    // 后端传来二进制流是需要修改为blob类型
    responseType: 'blob'
  })
}

export function getTablesZipInWord(file) {
  return request({
    url: '/example/word/zip/tables',
    method: 'post',
    file,
    // 后端传来二进制流是需要修改为blob类型
    responseType: 'blob'
  })
}

export function getImagesZipInWord(file) {
  return request({
    url: '/example/word/zip/images',
    method: 'post',
    file,
    // 后端传来二进制流是需要修改为blob类型
    responseType: 'blob'
  })
}
