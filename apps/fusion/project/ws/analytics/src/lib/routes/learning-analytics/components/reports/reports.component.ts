import { Component, OnInit, OnDestroy, ViewChild, ElementRef } from '@angular/core'
import { NsError, ROOT_WIDGET_CONFIG, GraphGeneralService } from '@ws-widget/collection'
import { NsWidgetResolver } from '@ws-widget/resolver'
import { TFetchStatus } from '@ws-widget/utils'
import { Subscription } from 'rxjs'
import { NsAnalytics } from '../../models/learning-analytics.model'
import { AnalyticsResolver } from '../../resolvers/learning-analytics-filters.resolver'
import { LearningAnalyticsService } from '../../services/learning-analytics.service'
import { LearningAnalyticsGuard } from '../../guards/learning-analytics.guard'
import { MatSnackBar } from '@angular/material'

@Component({
  selector: 'ws-analytics-reports',
  templateUrl: './reports.component.html',
  styleUrls: ['./reports.component.scss'],
})
export class ReportsComponent implements OnInit, OnDestroy {
  fetchStatus: TFetchStatus = 'none'
  startDate = ''
  showFilter = false
  endDate = ''
  searchQuery = ''
  userId = ''
  selectable = true
  removable = true
  addOnBlur = true
  currentTab = 'User'
  selected = ''
  defaultMsg = true
  yesterday: Date = new Date()
  reportsData: NsAnalytics.IReportData[] = []
  tabNames: string[] = []
  desktopView: any
  last15Days = false
  dateFilterApplied = false
  filterApplied = false
  filterData: NsAnalytics.IFilter[] = []
  tabFetchStatus: TFetchStatus = 'none'
  filterArray: NsAnalytics.IFilterObj[] = []
  @ViewChild('errorContent', { static: true })
  errorContentMessage!: ElementRef<any>
  @ViewChild('dateContent', { static: true })
  dateContentMessage!: ElementRef<any>
  @ViewChild('filterContent', { static: true })
  filterContentMessage!: ElementRef<any>

  errorWidget: NsWidgetResolver.IRenderConfigWithTypedData<NsError.IWidgetErrorResolver> = {
    widgetType: ROOT_WIDGET_CONFIG.errorResolver._type,
    widgetSubType: ROOT_WIDGET_CONFIG.errorResolver.errorResolver,
    widgetData: {
      errorType: 'internalServer',
    },
  }
  private filterEventSubscription: Subscription | null = null
  private filterDataEventSubscription: Subscription | null = null
  private removeEventSubscription: Subscription | null = null
  private dateEventSubscription: Subscription | null = null
  private searchEventSubscription: Subscription | null = null
  constructor(
    private guardFile: LearningAnalyticsGuard,
    private analyticsSrv: LearningAnalyticsService,
    private resolver: AnalyticsResolver,
    private snackBar: MatSnackBar,
    private graphGeneralSvc: GraphGeneralService,
    // private configSvc: ConfigurationsService,
  ) { }

  ngOnInit() {
    this.desktopView = this.guardFile.canActivate()
    this.filterEventSubscription = this.graphGeneralSvc.filterEventChangeSubject.subscribe(
      (filterEvent: any) => {
        const filter = {
          key: filterEvent.filterType,
          value: filterEvent.filterName,
        }
        this.filterArray.push(filter)
        this.filterApplied = true
        this.getReportData()
      },
    )
    this.filterDataEventSubscription = this.resolver.dataFilterEventChangeSubject.subscribe(
      (filterEvent: any) => {
        const filter = {
          key: filterEvent.filterType,
          value: filterEvent.filterName,
        }
        this.filterArray.push(filter)
        this.filterApplied = true
        this.getReportData()
      },
    )
    this.removeEventSubscription = this.resolver.removeFilterEventChangeSubject.subscribe(
      (removeEvent: any) => {
        this.filterArray = []
        if (removeEvent.length === 0) {
          this.filterArray = []
          this.searchQuery = ''
        } else {
          removeEvent.forEach((event: NsAnalytics.IFilter) => {
            if (event.filterType === 'searchQuery') {
              this.searchQuery = event.filterName
            } else {
              const filter = {
                key: event.filterType,
                value: event.filterName,
              }
              this.filterArray.push(filter)
            }
          })
        }
        this.getReportData()
      },
    )
    this.dateEventSubscription = this.resolver.dateEventChangeSubject.subscribe(
      (dateEvent: any) => {
        this.startDate = dateEvent.startDate
        this.endDate = dateEvent.endDate
        this.dateFilterApplied = true
        this.getReportData()
      },
    )

    this.searchEventSubscription = this.resolver.searchEventChangeSubject.subscribe(
      (searchEvent: any) => {
        this.searchQuery = searchEvent.searchQuery
        this.getReportData()
      },
    )
    this.getReportData()
  }

  getReportData() {
    this.fetchStatus = 'fetching'
    this.analyticsSrv.reportsData().subscribe(
      (resData: NsAnalytics.IReportResponse) => {
        // console.log(resData)
        resData.reports.map((obj: any) => {
          obj['loader'] = false
          obj['downloadStatus'] = 'none'
        })
        resData.reports.sort((x, y) => {
          const nameA = x.report_name.toLowerCase()
          const nameB = y.report_name.toLowerCase()
          if (nameA < nameB) {
            return -1
          }
          if (nameA > nameB) {
            return 1
          }
          return 0
        })
        this.reportsData = resData.reports
        this.fetchStatus = 'done'
        this.reportsData.map(report => {
          if (this.tabNames.indexOf(report.tab_name) === -1) {
            this.tabNames.push(report.tab_name)
          }
        })
      },
      () => {
        this.fetchStatus = 'error'
      },
    )
  }
  downloadReports(reports: any) {
    if (reports.filter_mandatory) {
      if (this.dateFilterApplied || this.filterApplied) {
        if (this.dateFilterApplied) {
          if ((new Date(this.endDate).getTime() - new Date(this.startDate).getTime()) < 1382400000) {
            reports.downloadStatus = 'fetching'
            reports.loader = true
            this.analyticsSrv.fetchOutput(reports.report_id, this.startDate, this.endDate, this.filterArray).subscribe(
              (data: Blob) => {
                this.downloadFile(data, reports)
                reports.downloadStatus = 'done'
              },
              () => {
                reports.downloadStatus = 'error'
              }
            )
          } else {
            this.snackBar.open(this.errorContentMessage.nativeElement.value)
          }
        } else {
          reports.downloadStatus = 'fetching'
          reports.loader = true
          this.analyticsSrv.fetchOutput(reports.report_id, this.startDate, this.endDate, this.filterArray).subscribe(
            (data: Blob) => {
              this.downloadFile(data, reports)
              reports.downloadStatus = 'done'
            },
            () => {
              reports.downloadStatus = 'error'
            }
          )
        }
      } else {
        this.snackBar.open(this.filterContentMessage.nativeElement.value)
      }
    } else {
      reports.downloadStatus = 'fetching'
      reports.loader = true
      this.analyticsSrv.fetchOutput(reports.report_id, this.startDate, this.endDate, this.filterArray).subscribe(
        (data: Blob) => {
          this.downloadFile(data, reports)
          reports.downloadStatus = 'done'
        },
        () => {
          reports.downloadStatus = 'error'
        }
      )
    }
  }
  // downloadReports(reports: any) {
  //   if (reports.filter_mandatory) {
  //     if (this.dateFilterApplied || this.filterApplied) {
  //       if (this.dateFilterApplied) {
  //         if (new Date(this.startDate).getTime() < new Date(this.endDate).getTime()) {
  //           if ((new Date(this.endDate).getTime() - new Date(this.startDate).getTime()) < 1382400000) {
  //             reports.downloadStatus = 'fetching'
  //             reports.loader = true
  //             this.analyticsSrv.fetchOutput(reports.report_id, this.startDate, this.endDate, this.filterArray).subscribe(
  //               (data: Blob) => {
  //                 this.downloadFile(data, reports)
  //                 reports.downloadStatus = 'done'
  //               },
  //               () => {
  //                 reports.downloadStatus = 'error'
  //               }
  //             )
  //           } else {
  //             this.snackBar.open(this.errorContentMessage.nativeElement.value)
  //           }
  //         } else {
  //           this.snackBar.open(this.dateContentMessage.nativeElement.value)
  //         }
  //       } else {
  //         reports.downloadStatus = 'fetching'
  //         reports.loader = true
  //         this.analyticsSrv.fetchOutput(reports.report_id, this.startDate, this.endDate, this.filterArray).subscribe(
  //           (data: Blob) => {
  //             this.downloadFile(data, reports)
  //             reports.downloadStatus = 'done'
  //           },
  //           () => {
  //             reports.downloadStatus = 'error'
  //           }
  //         )
  //       }
  //     } else {
  //       this.snackBar.open(this.filterContentMessage.nativeElement.value)
  //     }
  //   } else {
  //     reports.downloadStatus = 'fetching'
  //     reports.loader = true
  //     this.analyticsSrv.fetchOutput(reports.report_id, this.startDate, this.endDate, this.filterArray).subscribe(
  //       (data: Blob) => {
  //         this.downloadFile(data, reports)
  //         reports.downloadStatus = 'done'
  //       },
  //       () => {
  //         reports.downloadStatus = 'error'
  //       }
  //     )
  //   }
  // }
  downloadFile(data: Blob, file: any) {
    const ieEDGE = navigator.userAgent.match(/Edge/g)
    const blob = new Blob([data], { type: 'application/octet-stream' })
    const fileName = `${file.report_name}.csv`
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
      file.loader = false
    }
  }
  refresh(report: any) {
    report.downloadStatus = 'done'
    report.loader = false
  }
  ngOnDestroy() {
    if (this.filterEventSubscription) {
      this.filterEventSubscription.unsubscribe()
    }
    if (this.filterDataEventSubscription) {
      this.filterDataEventSubscription.unsubscribe()
    }
    if (this.dateEventSubscription) {
      this.dateEventSubscription.unsubscribe()
    }
    if (this.searchEventSubscription) {
      this.searchEventSubscription.unsubscribe()
    }
    if (this.removeEventSubscription) {
      this.removeEventSubscription.unsubscribe()
    }
  }
}
