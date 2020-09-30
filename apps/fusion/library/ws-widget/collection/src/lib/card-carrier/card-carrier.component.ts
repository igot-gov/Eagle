import { Component, Input, OnInit } from '@angular/core'
import { NSCarrierData } from './carrier.model'

@Component({
  selector: 'ws-widget-card-carrier',
  templateUrl: './card-carrier.component.html',
  styleUrls: ['./card-carrier.component.scss'],

})

export class CardCarrierComponent implements OnInit {
  @Input()
  carrierList!: NSCarrierData.ICarrierData[]

  ngOnInit() {
    this.filldummyData()
  }

  filldummyData() {
    this.carrierList = [
      {
        timeinfo: '2 Hours ago',
        // tslint:disable-next-line:max-line-length
        description: ' The undersigned is directed to convey the following regarding conveyance deed:-  2. In continuation to letter No. T -106/3 dated 05/06.01.20 11, it is to inform you that the conveyance deed has to be executed based on total cost of the dwelling unit as notified vide memo No.118-STR -1 20 11/969 dated 25.01.20 11 issued by Superintendent -Stamp and Registration, Financial Commissioner & Principal Secretary to Government, Haryana Revenue & Disaster Management Department( copy enclosed).',
        category: 'Category Name',
        count: 12,
        title: 'Filling up of the post of Deputy Director in the Directorate of Enforcement on deputation basis-regarding.',

      },
    ]

  }
}
