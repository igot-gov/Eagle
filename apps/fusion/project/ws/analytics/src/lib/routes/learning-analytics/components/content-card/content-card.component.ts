import { Component, Input, OnInit } from '@angular/core'
import { ROOT_WIDGET_CONFIG } from '@ws-widget/collection'
import { NsAnalytics } from '../../models/learning-analytics.model'
import { LearningAnalyticsService } from '../../services/learning-analytics.service'
@Component({
  selector: 'ws-analytics-content-card',
  templateUrl: './content-card.component.html',
  styleUrls: ['./content-card.component.scss'],
})
export class ContentCardComponent implements OnInit {
  @Input() pieData: any
  @Input() completed = 0
  @Input() source = ''
  @Input() progress = ''
  @Input() title = ''
  @Input() lexId = ''
  @Input() contentUrl = ''
  @Input() isExternal = false
  @Input() contentData: any
  loader = false
  widgetPieGraph: NsAnalytics.IGraphWidget = {} as NsAnalytics.IGraphWidget
  margin = {
    top: 25,
    right: 20,
    bottom: 25,
    left: 20,
  }
  graphData1: number[] = []
  labels: string[] = []
  filterArray: NsAnalytics.IFilterObj[] = []
  constructor(private analyticsSrv: LearningAnalyticsService) { }

  ngOnInit() {
    this.graphData(this.contentData.data)
  }
  downloadReports(lexId: string) {
    this.loader = true
    const filter = {
      key: 'content_id',
      value: lexId,
    }
    this.filterArray.push(filter)
    this.analyticsSrv.fetchOutput('R6', this.contentData.startDate, this.contentData.endDate, this.filterArray).subscribe(
      (data: Blob) => {
        this.downloadFile(data, lexId)
      },
      () => {
        this.loader = true
      }
    )
  }

  downloadFile(data: Blob, lexId: any) {
    const ieEDGE = navigator.userAgent.match(/Edge/g)
    const blob = new Blob([data], { type: 'application/octet-stream' })
    const fileName = `${lexId}.csv`
    if (ieEDGE) {
      window.navigator.msSaveBlob(blob, fileName)
    } else {
      const dataURL = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = dataURL
      link.download = fileName
      link.click()
      setTimeout(() => {
        window.URL.revokeObjectURL(dataURL)
      },
        // tslint:disable-next-line:align
        10)
      this.loader = false
      }
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
        graphId: this.contentData.id,
        graphType: 'pie',
        graphHeight: '50px',
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
