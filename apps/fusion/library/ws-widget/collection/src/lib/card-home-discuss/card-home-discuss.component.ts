import { Component, OnInit, Input } from '@angular/core'
import { WidgetBaseComponent, NsWidgetResolver } from '@ws-widget/resolver'

@Component({
  selector: 'ws-widget-home-component',
  templateUrl: './card-home-discuss.component.html',
  styleUrls: ['./card-home-discuss.component.scss'],
})
export class CardHomeDiscussComponent extends WidgetBaseComponent implements OnInit, NsWidgetResolver.IWidgetData<any> {
  @Input() widgetData: any
  ngOnInit(): void {
  }

}
