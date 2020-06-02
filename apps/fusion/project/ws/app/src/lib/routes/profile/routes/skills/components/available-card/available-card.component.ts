import { Component, OnInit, Input } from '@angular/core'
import { ROOT_WIDGET_CONFIG } from '@ws-widget/collection'
import { IGraphWidget, IGraphDataCourse, IBarGraphData } from '../../models/competency-model'
@Component({
  selector: 'ws-app-available-card',
  templateUrl: './available-card.component.html',
  styleUrls: ['./available-card.component.scss'],
})
export class AvailableCardComponent implements OnInit {
  widgetHorizontalBar: IGraphWidget = {} as IGraphWidget
  @Input() orgGraphData: IGraphDataCourse = {} as IGraphDataCourse
  @Input() type = ''
  graphData1: number[] = []
  lables: string[] = []

  constructor() {}

  ngOnInit() {
    this.graphData(this.orgGraphData)
  }
  graphData(orgGraphData: IGraphDataCourse) {
    if (this.type === 'course') {
      this.lables = ['0-25%', '25-50%', '50-75%', '75-100%']
    } else {
      this.lables = ['Not Qualified', 'Qualified']
    }
    this.orgGraphData.data.map((cur: IBarGraphData) => {
      this.graphData1.push(cur.y)
    })
    this.widgetHorizontalBar = {
      widgetType: ROOT_WIDGET_CONFIG.graph._type,
      widgetSubType: ROOT_WIDGET_CONFIG.graph.graphGeneral,
      widgetData: {
        graphId: orgGraphData.id,
        graphType: 'pie',
        graphHeight: '100px',
        graphWidth: '90%',
        graphLegend: false,
        graphLegendFontSize: 11,
        graphTicksFontSize: 11,
        graphGridLinesDisplay: false,
        graphDefaultPalette: 'default',
        graphData: {
          labels: this.lables,
          datasets: [
            {
              data: this.graphData1,
              backgroundColor: [
                'rgb(111, 30, 81)',
                'rgb(234, 32, 39)',
                'rgb(247, 159, 31)',
                'rgb(238, 90, 36)',
              ],
              borderWidth: 1,
            },
          ],
        },
      },
    }
  }
}
