import { Injectable } from '@angular/core'
import { Data } from '@angular/router'
import { BehaviorSubject } from 'rxjs'

@Injectable({
  providedIn: 'root',
})
export class LocalDataService {
  contentTitle: BehaviorSubject<string> = new BehaviorSubject<string>('')

  constructor() {
  }
  initData(data: Data) {
    this.contentTitle.next(data ? data.name : '')
  }
}
