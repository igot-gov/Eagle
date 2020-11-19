import { Component, OnInit, OnDestroy, Input } from '@angular/core'
import { NSNetworkDataV2 } from '../../models/network-v2.model'

@Component({
  selector: 'ws-app-left-menu',
  templateUrl: './left-menu.component.html',
  styleUrls: ['./left-menu.component.scss'],
})
export class LeftMenuComponent implements OnInit, OnDestroy {

  @Input()
  tabsData!: NSNetworkDataV2.IProfileTab
  constructor() { }

  ngOnInit(): void {

  }
  ngOnDestroy() {

  }

}
