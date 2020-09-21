import { Component, OnInit, Input } from '@angular/core'
import { WidgetBaseComponent, NsWidgetResolver } from '@ws-widget/resolver'

@Component({
  selector: 'ws-widget-home-component',
  templateUrl: './card-home-top.component.html',
  styleUrls: ['./card-home-top.component.scss'],
})
export class CardHomeTopComponent extends WidgetBaseComponent implements OnInit, NsWidgetResolver.IWidgetData<any> {
  @Input() widgetData: any
  ngOnInit(): void {
  }

}
