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
        itemParsed[element] = JSON.parse(value)
        // _.set(itemParsed,'element',value);
        console.log("return Item=> stringfy", JSON.stringify(itemParsed))
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
      console.log("return Item", item)
      return item[element] || null

    }
    return null
  }

  getAll() {
    let item = window.localStorage.getItem(VARIABLES.KEY)
    if (!item) {
      return ''
    }
    item = JSON.parse(item)
    console.log("GET ALL", item)
    return item || ''
  }

  clearAll() {
    window.localStorage.removeItem(VARIABLES.KEY)
  }
}
