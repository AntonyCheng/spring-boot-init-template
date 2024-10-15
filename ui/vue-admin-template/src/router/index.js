import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

/* Layout */
import Layout from '@/layout'

/**
 * Note: sub-menu only appear when route children.length >= 1
 * Detail see: https://panjiachen.github.io/vue-element-admin-site/guide/essentials/router-and-nav.html
 *
 * hidden: true                   if set true, item will not show in the sidebar(default is false)
 * alwaysShow: true               if set true, will always show the root menu
 *                                if not set alwaysShow, when item has more than one children route,
 *                                it will becomes nested mode, otherwise not show the root menu
 * redirect: noRedirect           if set noRedirect will no redirect in the breadcrumb
 * name:'router-name'             the name is used by <keep-alive> (must set!!!)
 * meta : {
 roles: ['admin','editor']    control the page roles (you can set multiple roles)
 title: 'title'               the name show in sidebar and breadcrumb (recommend set)
 icon: 'svg-name'/'el-icon-x' the icon show in the sidebar
 breadcrumb: false            if set false, the item will hidden in breadcrumb(default is true)
 activeMenu: '/example/list'  if set path, the sidebar will highlight the path you set
 }
 */

/**
 * constantRoutes
 * a base page that does not have permission requirements
 * all roles can be accessed
 */
export const constantRoutes = [
  {
    path: '/login',
    component: () => import('@/views/login/index'),
    hidden: true,
    role: ['admin', 'user']
  },

  {
    path: '/404',
    component: () => import('@/views/404'),
    hidden: true,
    role: ['admin', 'user']
  },

  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index'),
        meta: { title: '首页', icon: 'dashboard' }
      }
    ],
    role: ['admin', 'user']
  },
  {
    path: '/user',
    component: Layout,
    children: [
      {
        path: 'manage',
        name: 'UserManage',
        component: () => import('@/views/userManage/index'),
        meta: { title: '用户管理', icon: 'table' }
      }
    ],
    role: ['admin']
  },
  {
    path: '/log',
    component: Layout,
    children: [
      {
        path: 'manage',
        name: 'LogManage',
        component: () => import('@/views/logManage/index'),
        meta: { title: '日志管理', icon: 'el-icon-s-help' }
      }
    ],
    role: ['admin']
  },
  {
    path: '/file',
    component: Layout,
    children: [
      {
        path: 'manage',
        name: 'FileManage',
        component: () => import('@/views/fileManage/index'),
        meta: { title: '文件管理', icon: 'el-icon-s-order' }
      }
    ],
    role: ['admin']
  },
  {
    path: '/example',
    component: Layout,
    children: [
      {
        path: 'example',
        name: 'Example',
        component: () => import('@/views/example/index'),
        meta: { title: '示例演示', icon: 'el-icon-s-grid' }
      }
    ],
    role: ['admin']
  },
  {
    path: 'external-link',
    component: Layout,
    children: [
      {
        path: 'https://github.com/AntonyCheng/spring-boot-init-template/',
        meta: { title: '项目地址', icon: 'link' }
      }
    ],
    role: ['admin', 'user']
  },

  // 404 page must be placed at the end !!!
  {
    path: '*',
    redirect: '/404',
    hidden: true,
    role: ['admin', 'user']
  }
]

const createRouter = () => new Router({
  // mode: 'history', // require service support
  scrollBehavior: () => ({ y: 0 }),
  routes: constantRoutes
})

const router = createRouter()

// Detail see: https://github.com/vuejs/vue-router/issues/1234#issuecomment-357941465
export function resetRouter() {
  const newRouter = createRouter()
  router.matcher = newRouter.matcher // reset router
}

export default router
