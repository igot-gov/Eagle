import { Component, Input } from '@angular/core'
import { WidgetBaseComponent } from '@ws-widget/resolver'

@Component({
  selector: 'ws-widget-card-activity',
  templateUrl: './card-activity.component.html',
  styleUrls: ['./card-activity.component.scss'],

})

export class CardActivityComponent extends WidgetBaseComponent {

  @Input()
  widgetData!: any
  items = [
    { count: 7, icon: 'shop_two', name: 'Courses' },
    { count: 3, icon: 'card_membership', name: 'Certificates' },
    { count: 42, icon: 'query_builder', name: 'Training Hours' },
    { count: 20, icon: 'history', name: 'Daily Minutes' },
    { count: 56, icon: 'hourglass_empty', name: 'Karma' },
    { count: 123, icon: 'group_work', name: 'IGOT Coins' }]

}
