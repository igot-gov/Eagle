import { Injectable } from '@angular/core'

@Injectable({
  providedIn: 'root',
})
export class RouteDataService {
  constructor() { }
  obj: any = {}

  setStoredData(key: any, value: any) {
    this.obj[key] = value
  }

  getStoredData(key: any) {
    return this.obj[key]
  }
}
