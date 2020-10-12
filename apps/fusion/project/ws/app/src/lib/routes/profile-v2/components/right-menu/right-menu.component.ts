import { Component, Input, OnInit } from '@angular/core'
import { NSProfileDataV2 } from '../../models/profile-v2.model'

@Component({
  selector: 'app-profile-v2-right-menu',
  templateUrl: './right-menu.component.html',
  styleUrls: ['./right-menu.component.scss'],
  /* tslint:disable */
  host: { class: 'flex flex-1' },
  /* tslint:enable */
})
export class RightMenuComponent implements OnInit {
  // @Input() tags!: NSProfileDataV2.IProfile[]
  ngOnInit(): void {

  }

}
