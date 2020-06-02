import { Injectable } from '@angular/core'
import { BehaviorSubject } from 'rxjs'

@Injectable({
  providedIn: 'root',
})
export class RootService {
  showNavbarDisplay$ = new BehaviorSubject<boolean>(true)
  showFooterDisplay$ = new BehaviorSubject<boolean>(true)
  showHeaderDisplay$ = new BehaviorSubject<boolean>(true)
  showChatBotDisplay$ = new BehaviorSubject<boolean>(true)
  constructor() {}
}
