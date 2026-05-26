// utils/index.js

/**
 * 格式化时间
 */
export function formatTime(time) {
  if (!time) return ''
  
  const now = new Date()
  const date = new Date(time)
  const diff = now - date
  
  // 一分钟内
  if (diff < 60000) {
    return '刚刚'
  }
  
  // 一小时内
  if (diff < 3600000) {
    return `${Math.floor(diff / 60000)}分钟前`
  }
  
  // 一天内
  if (diff < 86400000) {
    return `${Math.floor(diff / 3600000)}小时前`
  }
  
  // 一周内
  if (diff < 604800000) {
    return `${Math.floor(diff / 86400000)}天前`
  }
  
  // 格式化日期
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hour = String(date.getHours()).padStart(2, '0')
  const minute = String(date.getMinutes()).padStart(2, '0')
  
  // 今年内不显示年份
  if (year === now.getFullYear()) {
    return `${month}-${day} ${hour}:${minute}`
  }
  
  return `${year}-${month}-${day} ${hour}:${minute}`
}