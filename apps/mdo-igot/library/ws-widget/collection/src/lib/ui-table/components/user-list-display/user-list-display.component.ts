import { Component, OnInit, Input } from '@angular/core'
import { WidgetBaseComponent } from '@ws-widget/resolver'

@Component({
  selector: 'ws-widget-display-table',
  templateUrl: './user-list-display.component.html',
  styleUrls: ['./user-list-display.component.scss'],
})
export class UserListDisplayComponent extends WidgetBaseComponent
  implements OnInit {
  @Input() widgetData: any
  ngOnInit(): void {
  }

}
