import { Component, OnInit } from '@angular/core'
import { AnalyticsService } from '../../services/analytics.service'
import { NSAnalyticsData } from '../../models/analytics.model'
import { TFetchStatus } from '@ws-widget/utils'
@Component({
  selector: 'ws-app-refactoring',
  templateUrl: './refactoring.component.html',
  styleUrls: ['./refactoring.component.scss'],
})
export class RefactoringComponent implements OnInit {
  startDate = '2018-04-01'
  endDate = '2020-03-31'
  contentType = 'Course'
  isCompleted = 0
  nsoFetchStatus: TFetchStatus = 'fetching'
  nsoData: NSAnalyticsData.INsoResponse | null = null

  constructor(private analyticsSrv: AnalyticsService) { }
  ngOnInit() {
    this.nsoFetchStatus = 'fetching'
    this.analyticsSrv.nsoArtifacts(this.startDate, this.endDate, this.contentType, this.isCompleted).subscribe(
      (nsoResponse: NSAnalyticsData.INsoResponse) => {
        this.nsoData = nsoResponse
        this.nsoFetchStatus = 'done'
      })
  }

}
