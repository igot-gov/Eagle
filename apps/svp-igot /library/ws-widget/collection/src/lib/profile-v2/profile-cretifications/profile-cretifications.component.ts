import { Component, HostBinding, Input, OnInit } from '@angular/core'
import { WidgetBaseComponent, NsWidgetResolver } from '@ws-widget/resolver'
import { IProCert } from './profile-cretifications.model'

@Component({
  selector: 'ws-widget-profile-v2-cretifications',
  templateUrl: './profile-cretifications.component.html',
  styleUrls: ['./profile-cretifications.component.scss'],
  /* tslint:disable */
  host: { class: 'flex flex-1' },
  /* tslint:enable */
})

// developing for old skill+certifications
export class ProfileCretificationsComponent extends WidgetBaseComponent implements OnInit, NsWidgetResolver.IWidgetData<any> {
  @Input() widgetData!: IProCert
  @HostBinding('id')
  public id = 'profile-cert'
  ngOnInit(): void {
  }

}
