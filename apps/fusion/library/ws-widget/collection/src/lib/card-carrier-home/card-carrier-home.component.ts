import { Component, Input, OnInit } from '@angular/core'
import { NSCarrierData } from './carrier.model'
import { WidgetBaseComponent } from '@ws-widget/resolver'

@Component({
  selector: 'ws-widget-card-carrier-home',
  templateUrl: './card-carrier-home.component.html',
  styleUrls: ['./card-carrier-home.component.scss'],

})

export class CardCarrierHomeComponent extends WidgetBaseComponent implements OnInit {
  // @Input()
  // carrierList!: NSCarrierData.ICarrierData[]

  @Input() widgetData: any
  carrier!: NSCarrierData.ICarrierData
  ngOnInit() {
    // this.filldummyData()
    if (this.widgetData && this.widgetData.content) {
      this.carrier = ([this.widgetData.content] || []).map((d: any) => {
        return {
          title: d.title,
          description: d.title,
          category: 'Career', // d.category.name
          count: d.viewcount,
          timeinfo: d.timestamp,
        }
      })[0]
    }
  }
}
