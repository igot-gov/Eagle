import { Component, OnInit, Input, HostBinding } from '@angular/core'
import { NsWidgetResolver, WidgetBaseComponent } from '@ws-widget/resolver'
import { IAuthorData } from './author-card.model'
@Component({
  selector: 'ws-widget-author-card',
  templateUrl: './author-card.component.html',
  styleUrls: ['./author-card.component.scss'],
  // /* tslint:disable */
  // // host: { class: '' }
  // /* tslint:enable */
})
export class AuthorCardComponent extends WidgetBaseComponent
  implements OnInit, NsWidgetResolver.IWidgetData<IAuthorData> {
  @Input() widgetData!: IAuthorData
  @HostBinding('id')
  public id = `auth-card-${Math.random()}`
  @HostBinding('class')
  public class = 'flex flex-1 mr-8 mb-4'

  ngOnInit() {

  }
  getProfileLink() {
    if (this.widgetData && this.widgetData.profileLink) {
      return `/app/profile/${this.widgetData.profileLink}`
    }
    return null
  }
}
