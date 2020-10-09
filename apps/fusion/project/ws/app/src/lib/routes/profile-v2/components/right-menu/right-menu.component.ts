import { Component, Input, OnInit } from '@angular/core'
import { NSProfileData } from '../../models/profile-v2.model'

@Component({
  selector: 'app-profile-v2-right-menu',
  templateUrl: './right-menu.component.html',
  styleUrls: ['./right-menu.component.scss'],
  /* tslint:disable */
  host: { class: 'flex flex-1' },
  /* tslint:enable */
})
export class RightMenuComponent implements OnInit {
  @Input() tags!: NSProfileData.IProfile[]

  items = [
    'All new methods of control of powers of the administrative authorities and more such policies.',
    'Powers and functions of the administrative authorities Methods of control of powers of the administrative authorities',
  ]
  ngOnInit(): void {

  }

}
