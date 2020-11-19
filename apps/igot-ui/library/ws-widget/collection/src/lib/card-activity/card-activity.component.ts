import { Component, HostBinding, Input, OnInit } from '@angular/core'
import { WidgetBaseComponent, NsWidgetResolver } from '@ws-widget/resolver'

@Component({
  selector: 'ws-widget-card-activity',
  templateUrl: './card-activity.component.html',
  styleUrls: ['./card-activity.component.scss'],

})

export class CardActivityComponent extends WidgetBaseComponent implements OnInit, NsWidgetResolver.IWidgetData<any>  {

  @Input()
  widgetData!: any
  item: any
  @HostBinding('id')
  public id = `activity_${Math.random()}`
  ngOnInit(): void {
    if (this.widgetData && this.widgetData.content) {
      if (this.widgetData.content.hasOwnProperty('totalDuration')) {
        this.item = { name: 'Training Hours', icon: 'timer', count: this.widgetData.content.totalDuration.value }
      } else if (this.widgetData.content.hasOwnProperty('contentCount')) {
        this.item = { name: 'Contents', icon: 'shop_two', count: this.widgetData.content.contentCount.value }
      } else if (this.widgetData.content.hasOwnProperty('coins')) {
        this.item = { name: 'IGOT coins', icon: 'monetization_on', count: this.widgetData.content.coins.value }
      } else if (this.widgetData.content.hasOwnProperty('dailyTimeSpent')) {
        this.item = { name: 'Daily Minutes', icon: 'timer', count: this.widgetData.content.dailyTimeSpent.value }
      } else if (this.widgetData.content.hasOwnProperty('karmaPoints')) {
        this.item = { name: 'Karma', icon: 'loop', count: this.widgetData.content.karmaPoints.value }
      } else if (this.widgetData.content.hasOwnProperty('certificateCount')) {
        this.item = { name: 'Badges', icon: 'stars', count: this.widgetData.content.certificateCount.value }
      } else {
        this.item = this.widgetData.content
      }

    }
  }
}
