import { Component, OnInit, Input } from '@angular/core'
import { NsWidgetResolver, WidgetBaseComponent } from '@ws-widget/resolver'
import { IAtGlanceComponentData } from './at-glance.model'
@Component({
  selector: 'ws-widget-at-glance',
  templateUrl: './at-glance.component.html',
  styleUrls: ['./at-glance.component.scss'],
})
export class AtGlanceComponent extends WidgetBaseComponent
  implements OnInit, NsWidgetResolver.IWidgetData<IAtGlanceComponentData> {
  @Input() widgetData!: IAtGlanceComponentData
  containerClass = ''

  ngOnInit() {

  }
}
