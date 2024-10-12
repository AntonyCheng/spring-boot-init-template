import axios from 'axios'
import { MessageBox, Message } from 'element-ui'
import store from '@/store'
import { getToken } from '@/utils/auth'

// create an axios instance
const service = axios.create({
  baseURL: '/api', // url = base url + request url
  withCredentials: true // send cookies when cross-domain requests
  // timeout: 10000 // request timeout
})

// request interceptor
service.interceptors.request.use(
  config => {
    // do something before request is sent

    if (store.getters.token) {
      // let each request carry token
      // ['X-Token'] is a custom headers key
      // please modify it according to the actual situation
      config.headers['Authorization'] = getToken()
    }
    return config
  },
  error => {
    // do something with request error
    console.log(error) // for debug
    return Promise.reject(error)
  }
)

const RESPONSE_URI_WHITE_LIST = [
  '/admin/user/export',
  '/admin/user/template',
  '/admin/log/export',
  '/admin/file/export',
  '/example/word/template',
  '/example/word/txt/paragraphs',
  '/example/word/zip/tables',
  '/example/word/zip/images',
  '/example/pdf/template/freemarker',
  '/example/pdf/template/thymeleaf',
  '/example/pdf/template/jte',
  '/example/pdf/txt/paragraphs',
  '/example/pdf/zip/images'
]

// response interceptor
service.interceptors.response.use(
  /**
   * If you want to get http information such as headers or status
   * Please return  response => response
   */

  /**
   * Determine the request status by custom code
   * Here is just an example
   * You can also judge the status by HTTP Status Code
   */
  response => {
    const res = response.data
    // if the uri in whitelist, it is judged as a success.
    if (RESPONSE_URI_WHITE_LIST.includes(response.request.responseURL.split(service.defaults.baseURL)[1].split('?')[0])) {
      return response
    }
    // if the custom code is not 200, it is judged as an error.
    if (res.code !== 200) {
      Message({
        message: res.msg || 'Error',
        type: 'error',
        duration: 5 * 1000
      })

      // 401: Illegal token;
      if (res.code === 401) {
        // to re-login
        MessageBox.confirm('您的状态为注销状态，可以选择点击“取消”留在这个页面，也可以重新登录', '确认注销', {
          confirmButtonText: '重新登陆',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          store.dispatch('auth/resetToken').then(() => {
            location.reload()
          })
        })
      }
      return Promise.reject(new Error(res.msg || 'Error'))
    } else {
      return res
    }
  },
  error => {
    console.log('err' + error) // for debug
    Message({
      message: error.message,
      type: 'error',
      duration: 5 * 1000
    })
    return Promise.reject(error)
  }
)

export default service
