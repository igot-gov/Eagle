import { Injectable } from '@angular/core'
import { BehaviorSubject } from 'rxjs'
import { WebModuleData } from '../components/web-module.class'

@Injectable()
export class WebStoreService {

  selectedWebIndex = new BehaviorSubject<number>(0)
  currentWebIndex = 0
  collectiveWeb: { [key: string]: WebModuleData[] } = {}
  resourceType = 'Web'
  currentId = ''

  constructor() { }

  changeWeb(index: number) {
    this.currentWebIndex = index
    this.selectedWebIndex.next(index)
  }

  updateWeb(index: number, webObj: any) {
    this.collectiveWeb[this.currentId][index] = webObj
  }

  getWeb() {
    return (this.currentId && this.collectiveWeb[this.currentId]) ? JSON.parse(JSON.stringify(this.collectiveWeb[this.currentId])) : null
  }

}
