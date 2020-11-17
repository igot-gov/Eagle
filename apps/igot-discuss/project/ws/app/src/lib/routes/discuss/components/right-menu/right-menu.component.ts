import { Component, Input, OnInit } from '@angular/core'
import { NSDiscussData } from '../../models/discuss.model'

@Component({
  selector: 'app-discuss-right-menu',
  templateUrl: './right-menu.component.html',
  styleUrls: ['./right-menu.component.scss'],
  /* tslint:disable */
  host: { class: 'flex flex-1' },
  /* tslint:enable */
})
export class RightMenuComponent implements OnInit {
  @Input() tags!: NSDiscussData.ITag[]

  items = [
    'All new methods of control of powers of the administrative authorities and more such policies.',
    'Powers and functions of the administrative authorities Methods of control of powers of the administrative authorities',
  ]
  ngOnInit(): void {

  }

}
