import { Component, Input, OnInit } from '@angular/core'
import { WidgetBaseComponent, NsWidgetResolver } from '@ws-widget/resolver'

@Component({
  selector: 'ws-widget-card-activity',
  templateUrl: './card-activity.component.html',
  styleUrls: ['./card-activity.component.scss'],

})

export class CardActivityComponent extends WidgetBaseComponent implements OnInit, NsWidgetResolver.IWidgetData<any>  {

  @Input()
  widgetData!: any
  item = { count: 7, icon: 'shop_two', name: 'Courses' }
  ngOnInit(): void {
  }
}
