import { Component, Input, OnInit } from '@angular/core'
import { WidgetBaseComponent, NsWidgetResolver } from '@ws-widget/resolver'

@Component({
  selector: 'ws-widget-profile-v2-academics',
  templateUrl: './profile-academics.component.html',
  styleUrls: ['./profile-academics.component.scss'],
  /* tslint:disable */
  host: { class: 'flex flex-1' },
  /* tslint:enable */
})
export class ProfileAcademicsComponent extends WidgetBaseComponent implements OnInit, NsWidgetResolver.IWidgetData<any> {
  @Input() widgetData: any
  ngOnInit(): void {
  }

}
