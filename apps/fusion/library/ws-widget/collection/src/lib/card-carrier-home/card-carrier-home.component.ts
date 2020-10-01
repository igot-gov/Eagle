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

  filldummyData() {
    // this.carrierList = [
    //   {
    //     timeinfo: '2 Hours ago',
    //     // tslint:disable-next-line:max-line-length
    //     description: ' The undersigned is directed to convey the following regarding conveyance deed:-  2. In continuation to letter No. T -106/3 dated 05/06.01.20 11, it is to inform you that the conveyance deed has to be executed based on total cost of the dwelling unit as notified vide memo No.118-STR -1 20 11/969 dated 25.01.20 11 issued by Superintendent -Stamp and Registration, Financial Commissioner & Principal Secretary to Government, Haryana Revenue & Disaster Management Department( copy enclosed).',
    //     category: 'Category Name',
    //     count: 12,
    //     title: 'Filling up of the post of Deputy Director in the Directorate of Enforcement on deputation basis-regarding.',

    //   },
    //   {
    //     timeinfo: '2 Hours ago',
    //     // tslint:disable-next-line:max-line-length
    //     description: ' The undersigned is directed to convey the following regarding conveyance deed:-  2. In continuation to letter No. T -106/3 dated 05/06.01.20 11, it is to inform you that the conveyance deed has to be executed based on total cost of the dwelling unit as notified vide memo No.118-STR -1 20 11/969 dated 25.01.20 11 issued by Superintendent -Stamp and Registration, Financial Commissioner & Principal Secretary to Government, Haryana Revenue & Disaster Management Department( copy enclosed).',
    //     category: 'Category Name',
    //     count: 12,
    //     title: 'Filling up of the post of Deputy Director in the Directorate of Enforcement on deputation basis-regarding.',

    //   },
    // ]

  }
}
