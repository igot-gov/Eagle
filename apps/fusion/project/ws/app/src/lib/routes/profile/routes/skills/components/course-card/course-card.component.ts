import { Component, Input, OnInit } from '@angular/core'
import { ROOT_WIDGET_CONFIG } from '@ws-widget/collection'
import { IGraphWidget } from '../../models/competency-model'
@Component({
  selector: 'ws-app-course-card',
  templateUrl: './course-card.component.html',
  styleUrls: ['./course-card.component.scss'],
})
export class CourseCardComponent implements OnInit {
  @Input() pieData: any
  @Input() completed = 0
  @Input() source = ''
  @Input() progress = ''
  @Input() title = ''
  @Input() lexId = ''
  @Input() contentUrl = ''
  @Input() isExternal = false
  @Input() historyData: any
  widgetPieGraph: IGraphWidget = {} as IGraphWidget
  margin = {
    top: 25,
    right: 20,
    bottom: 25,
    left: 20,
  }
  graphData1: number[] = []
  labels: string[] = []
  constructor() { }

  ngOnInit() {
    this.graphData(this.historyData.data)
  }
  graphData(pieData: any) {
    this.labels = ['0-25%', '25-50%', '50-75%', '75-100%']
    pieData.forEach((cur: any) => {
      this.graphData1.push(cur.y)
    })
    this.widgetPieGraph = {
      widgetType: ROOT_WIDGET_CONFIG.graph._type,
      widgetSubType: ROOT_WIDGET_CONFIG.graph.graphGeneral,
      widgetData: {
        graphId: this.historyData.id,
        graphType: 'pie',
        graphHeight: '60px',
        graphWidth: '90%',
        graphLegend: false,
        graphLegendFontSize: 11,
        graphTicksFontSize: 11,
        graphGridLinesDisplay: false,
        graphDefaultPalette: 'default',
        graphData: {
          labels: this.labels,
          datasets: [
            {
              data: this.graphData1,
              backgroundColor: [
                'rgb(255, 82, 61)', 'rgb(240, 179, 35)', 'rgb(32, 150, 205)', 'rgb(120, 157, 74)',
              ],
              borderWidth: 1,
            },
          ],
        },
      },
    }
  }
}
