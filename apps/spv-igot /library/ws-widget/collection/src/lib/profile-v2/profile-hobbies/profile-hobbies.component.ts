import { Component, HostBinding, Input, OnInit } from '@angular/core'
import { WidgetBaseComponent, NsWidgetResolver } from '@ws-widget/resolver'
import { IProHobbies } from './profile-hobbies.model'

@Component({
  selector: 'ws-widget-profile-v2-hobbies',
  templateUrl: './profile-hobbies.component.html',
  styleUrls: ['./profile-hobbies.component.scss'],
  /* tslint:disable */
  host: { class: 'flex flex-1' },
  /* tslint:enable */
})

// developing for old skill+certifications
export class ProfileHobbiesComponent extends WidgetBaseComponent implements OnInit, NsWidgetResolver.IWidgetData<any> {
  @Input() widgetData!: IProHobbies
  @HostBinding('id')
  public id = 'profile-hobbies'
  ngOnInit(): void {
  }

}
