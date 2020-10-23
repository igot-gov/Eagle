import { Component, Input, OnInit } from '@angular/core'
import { WidgetBaseComponent, NsWidgetResolver } from '@ws-widget/resolver'
import { IProfileCareer } from './profile-career.model'

@Component({
  selector: 'ws-widget-profile-v2-career',
  templateUrl: './profile-career.component.html',
  styleUrls: ['./profile-career.component.scss'],
  /* tslint:disable */
  host: { class: 'flex flex-1' },
  /* tslint:enable */
})
export class ProfileCareerComponent extends WidgetBaseComponent implements OnInit, NsWidgetResolver.IWidgetData<any> {
  @Input() widgetData!: IProfileCareer
  ngOnInit(): void {
    // console.log(this.widgetData)
  }

}
