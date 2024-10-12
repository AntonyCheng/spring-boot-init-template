import request from '@/utils/request'

// 验证码
export function captcha() {
  return request({
    url: '/captcha',
    method: 'post'
  })
}

export function checkCaptcha(data) {
  return request({
    url: '/example/check/captcha',
    method: 'post',
    data
  })
}

// 请求加密
export function getRsaPublicKey() {
  return request({
    url: '/encrypt/rsa/public/key',
    method: 'get'
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

// Word工具类
export function exportWordByTemplate(title, name, date) {
  return request({
    url: '/example/word/template',
    method: 'get',
    params: {
      title: title,
      name: name,
      date: date
    },
    // 后端传来二进制流是需要修改为blob类型
    responseType: 'blob'
  })
}

export function getParagraphsTxtInWord(data) {
  return request({
    url: '/example/word/txt/paragraphs',
    method: 'post',
    data,
    // 后端传来二进制流是需要修改为blob类型
    responseType: 'blob'
  })
}

export function getTablesZipInWord(data) {
  return request({
    url: '/example/word/zip/tables',
    method: 'post',
    data,
    // 后端传来二进制流是需要修改为blob类型
    responseType: 'blob'
  })
}

export function getImagesZipInWord(data) {
  return request({
    url: '/example/word/zip/images',
    method: 'post',
    data,
    // 后端传来二进制流是需要修改为blob类型
    responseType: 'blob'
  })
}

// PDF工具类
export function exportPdfByFreemarkerTemplate() {
  return request({
    url: '/example/pdf/template/freemarker',
    method: 'get',
    // 后端传来二进制流是需要修改为blob类型
    responseType: 'blob'
  })
}

export function exportPdfByThymeleafTemplate() {
  return request({
    url: '/example/pdf/template/thymeleaf',
    method: 'get',
    // 后端传来二进制流是需要修改为blob类型
    responseType: 'blob'
  })
}

export function exportPdfByJteTemplate() {
  return request({
    url: '/example/pdf/template/jte',
    method: 'get',
    // 后端传来二进制流是需要修改为blob类型
    responseType: 'blob'
  })
}

export function getParagraphsTxtInPdf(data) {
  return request({
    url: '/example/pdf/txt/paragraphs',
    method: 'post',
    data,
    // 后端传来二进制流是需要修改为blob类型
    responseType: 'blob'
  })
}

export function getImagesZipInPdf(data) {
  return request({
    url: '/example/pdf/zip/images',
    method: 'post',
    data,
    // 后端传来二进制流是需要修改为blob类型
    responseType: 'blob'
  })
}

// IP工具类
export function getRegionByIp(ip) {
  return request({
    url: '/example/ip2region/region/by/ip',
    method: 'get',
    params: {
      ip: ip
    }
  })
}

export function getIpAndRegionByRequest() {
  return request({
    url: '/example/ip2region/ip/region/by/request',
    method: 'get'
  })
}
