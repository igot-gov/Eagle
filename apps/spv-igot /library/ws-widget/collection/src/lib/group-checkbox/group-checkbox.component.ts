import { Component, OnInit, Input } from '@angular/core'
import { ICheckBoxList } from './group-checkbox.model'

@Component({
  selector: 'ws-widget-group-checkbox',
  templateUrl: './group-checkbox.component.html',
  styleUrls: ['./group-checkbox.component.scss'],
})
export class GroupCheckboxComponent implements OnInit {
  @Input() checkboxData!: ICheckBoxList[]
  @Input() title!: string
  noOfCol = 4

  constructor() { }

  ngOnInit() {
  }

}
