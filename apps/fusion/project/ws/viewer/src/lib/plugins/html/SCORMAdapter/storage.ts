import { Injectable } from '@angular/core'
// import _ from 'lodash'
const VARIABLES = {
  KEY: 'scormData',
}
@Injectable({
  providedIn: 'root',
})

export class Storage {
  constructor() {

  }
  setItem(element: any, value: any) {
    const item = window.localStorage.getItem(VARIABLES.KEY)
    if (!item) {
      window.localStorage.setItem(VARIABLES.KEY, '{}')
    }
    const getItem = window.localStorage.getItem(VARIABLES.KEY)
    if (getItem) {
      const itemParsed = JSON.parse(getItem)
      if (itemParsed) {
        itemParsed[element] = value
        // _.set(itemParsed,'element',value);
        window.localStorage.setItem(VARIABLES.KEY, JSON.stringify(itemParsed))
      }
    }

  }

  getItem(element: any) {
    let item = window.localStorage.getItem(VARIABLES.KEY)
    if (!item) {
      return null
    }
    item = JSON.parse(item)
    if (item) {
      const _return = item[element] || null
      return _return
    }
    return null
  }

  getAll() {
    let item = window.localStorage.getItem(VARIABLES.KEY)
    if (!item) {
      return ''
    }
    item = JSON.parse(item)
    return item || ''
  }

  clearAll() {
    window.localStorage.removeItem(VARIABLES.KEY)
  }
}
