import { Component, Input, OnInit } from '@angular/core'
import { NSCarrierData } from './carrier.model'

@Component({
  selector: 'ws-widget-card-carrier',
  templateUrl: './card-carrier.component.html',
  styleUrls: ['./card-carrier.component.scss'],

})

export class CardCarrierComponent implements OnInit {
  @Input()
  carrier!: NSCarrierData.ICarrierData

  ngOnInit() {
  }
}
