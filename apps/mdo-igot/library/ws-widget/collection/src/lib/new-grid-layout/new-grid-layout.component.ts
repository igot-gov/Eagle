import { Component, OnInit, Input } from '@angular/core'
import { NsWidgetResolver, WidgetBaseComponent } from '@ws-widget/resolver'
import {
  INewGridLayoutData,
  INewGridLayoutProcessedData,
  responsiveSuffix,
  sizeSuffix,
  INewGridLayoutDataMain,
} from './new-grid-layout.model'
@Component({
  selector: 'ws-widget-new-grid-layout',
  templateUrl: './new-grid-layout.component.html',
  styleUrls: ['./new-grid-layout.component.scss'],
})
export class NewGridLayoutComponent extends WidgetBaseComponent
  implements OnInit, NsWidgetResolver.IWidgetData<INewGridLayoutDataMain> {
  @Input() widgetData!: INewGridLayoutDataMain
  containerClass = ''
  processed: INewGridLayoutProcessedData[][] = []

  ngOnInit() {
    if (this.widgetData.gutter != null) {
      this.containerClass = `-mx-${this.widgetData.gutter}`
    }
    const gutterAdjustment = this.widgetData.gutter !== null ? `p-${this.widgetData.gutter}` : ''
    this.processed = this.widgetData.widgets.map(row =>
      row.map(
        (col: INewGridLayoutData): INewGridLayoutProcessedData => ({
          className: Object.entries(col.dimensions).reduce(
            (agg, [k, v]) =>
              `${agg} ${(responsiveSuffix as { [id: string]: string })[k]}:${sizeSuffix[v]}`,
            `${col.className} w-full ${gutterAdjustment}`,
          ),
          styles: col.styles,
          widget: col.widget,
        }),
      ),
    )
  }
}
