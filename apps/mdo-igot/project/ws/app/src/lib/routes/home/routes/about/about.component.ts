
import { AfterViewInit, Component, OnDestroy, OnInit } from '@angular/core'
@Component({
  selector: 'ws-app-about',
  templateUrl: './about.component.html',
  styleUrls: ['./about.component.scss'],
  /* tslint:disable */
  // host: { class: 'flex flex-1 margin-top-l' },
  /* tslint:enable */
})
export class AboutComponent implements OnInit, AfterViewInit, OnDestroy {
  constructor() {
  }

  ngOnDestroy() {
    // if (this.tabs) {
    //   this.tabs.unsubscribe()
    // }
  }
  ngOnInit() {
    // int left blank
  }
  ngAfterViewInit() {
    // this.elementPosition = this.menuElement.nativeElement.parentElement.offsetTop
  }

}
