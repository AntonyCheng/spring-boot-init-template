import defaultSettings from '@/settings'

const title = defaultSettings.title || 'SpringBoot初始化模板'

export default function getPageTitle(pageTitle) {
  if (pageTitle) {
    return `${pageTitle} - ${title}`
  }
  return `${title}`
}
