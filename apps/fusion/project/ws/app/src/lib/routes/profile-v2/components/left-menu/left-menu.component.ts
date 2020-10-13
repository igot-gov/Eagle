import { Component, OnInit, OnDestroy, Input } from '@angular/core'
import { NSProfileDataV2 } from '../../models/profile-v2.model'

@Component({
  selector: 'app-profile-v2-left-menu',
  templateUrl: './left-menu.component.html',
  styleUrls: ['./left-menu.component.scss'],
})
export class LeftMenuComponent implements OnInit, OnDestroy {

  @Input()
  tabsData!: NSProfileDataV2.IProfileTab
  constructor() { }

  ngOnInit(): void {

  }
  ngOnDestroy() {

  }
}
