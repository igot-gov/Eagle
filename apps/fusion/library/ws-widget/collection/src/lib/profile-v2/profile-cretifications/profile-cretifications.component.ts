import { Component, Input, OnInit } from '@angular/core'
import { WidgetBaseComponent, NsWidgetResolver } from '@ws-widget/resolver'

@Component({
  selector: 'ws-widget-profile-v2-cretifications',
  templateUrl: './profile-cretifications.component.html',
  styleUrls: ['./profile-cretifications.component.scss'],
  /* tslint:disable */
  host: { class: 'flex flex-1' },
  /* tslint:enable */
})
export class ProfileCretificationsComponent extends WidgetBaseComponent implements OnInit, NsWidgetResolver.IWidgetData<any> {
  @Input() widgetData: any
  ngOnInit(): void {
  }

}
