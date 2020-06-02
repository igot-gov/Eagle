import { Component, OnInit, ViewChild } from '@angular/core'
import { LearningAnalyticsService } from '../../services/learning-analytics.service'
import { NsAnalytics } from '../../models/learning-analytics.model'
import { TFetchStatus, ConfigurationsService } from '@ws-widget/utils'
import { MatPaginator } from '@angular/material/paginator'
import { NsError, ROOT_WIDGET_CONFIG } from '@ws-widget/collection'
import { NsWidgetResolver } from '@ws-widget/resolver'

export interface IPeriodicElement {
  jobTitle: string
  Certified: number
  Professional: number
  Master: number
  NotCertified: number
}
export interface ITile {
  cols: number
  text: string
  id: number
  color: string
}

@Component({
  selector: 'ws-analytics-my-learning-pathway',
  templateUrl: './my-learning-pathway.component.html',
  styleUrls: ['./my-learning-pathway.component.scss'],
})
export class MyLearningPathwayComponent implements OnInit {
  fetchStatus: TFetchStatus = 'none'
  loadingStatus: TFetchStatus = 'none'
  ELEMENT_DATA: IPeriodicElement[] = []
  roleData: NsAnalytics.IRoleCompletionData[] = []
  displayedColumns: string[] = ['JobTitle', 'certified', 'professional', 'master', 'notCertified']
  dataSource: any = []
  obj: any
  roles: any = []
  userData: any = []
  displayedColumnsTwo: any[] = []
  courseColumn: any = []
  userTableData: any = []
  userTableObj: any
  userFetchStatus: TFetchStatus = 'none'
  page = 1
  tabIndex = 0
  disabled = true
  disabledNext = true
  // levelColumn: ITile[] = [{ cols: 3, text: 'User Details', id: 0, color: 'primary' }]
  levelColumn: any = []
  levelObj: any
  errorWidget: NsWidgetResolver.IRenderConfigWithTypedData<NsError.IWidgetErrorResolver> = {
    widgetType: ROOT_WIDGET_CONFIG.errorResolver._type,
    widgetSubType: ROOT_WIDGET_CONFIG.errorResolver.errorResolver,
    widgetData: {
      errorType: 'internalServer',
    },
  }
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator | null = null
  // dataSource = ELEMENT_DATA
  constructor(
    private analyticsSrv: LearningAnalyticsService,
    private configSvc: ConfigurationsService,
  ) {
    // console.log(this.dataSource)
  }

  ngOnInit() {
    this.getRoleData()
    this.getRoleName()
    this.getUserData('Sales Manager', this.page)
  }

  getRoleData() {
    this.fetchStatus = 'fetching'
    return this.analyticsSrv.getRoleCompletionData().subscribe(
      (response: NsAnalytics.IRoleCompletionResponse) => {
        this.roleData = response.pathway_completion_role
        this.roleData.forEach((item: any) => {
          this.obj = {
            jobTitle: item.job_title,
          }
          const key = Object.keys(item)
          key.forEach((level: any) => {
            if (level !== 'job_title') {
              this.obj[level] = {
                percentage: item[level].percentage,
                total: item[level].total,
                completed: item[level].completed,
              }
            }
          })
          this.ELEMENT_DATA.push(this.obj)
        })
        this.dataSource = this.ELEMENT_DATA
        this.fetchStatus = 'done'
      },
      () => {
        this.fetchStatus = 'error'
      },
    )
  }

  getRoleName() {
    return this.analyticsSrv.getRoleNameData().subscribe((response: NsAnalytics.IRoleNameData) => {
      this.roles = response.roles
    })
  }

  getUserData(role: string, page: number) {
    this.userFetchStatus = 'fetching'
    return this.analyticsSrv.getUserProgress(role, page).subscribe(
      (response: NsAnalytics.ICourseCompletionResponse) => {
        if (response.user_progress.length < 50) {
          this.disabledNext = true
        } else {
          this.disabledNext = false
        }
        this.userData = response.user_progress
        // this.displayedColumnsTwo = ['STARSID', 'USERNAME']
        this.displayedColumnsTwo = []
        this.userTableData = []
        this.levelColumn = [
          //   {
          //     cols: 3,
          //     text: 'User Details',
          //     id: 0,
          //     color: '#2096cd',
          //   },
        ]
        Object.keys(this.userData[0].levels).forEach(level => {
          let sequence = 1
          let colour = '#133A7C'
          if (level === 'Certified') {
            sequence = 1
            colour = this.configSvc.activeThemeObject
              ? this.configSvc.activeThemeObject.color.primary
              : ''
          } else if (level === 'Professional') {
            sequence = 2
            colour = '#2096cd'
          } else {
            sequence = 3
            colour = this.configSvc.activeThemeObject
              ? this.configSvc.activeThemeObject.color.primary
              : ''
          }
          this.levelObj = {
            text: level,
            cols: this.userData[0].levels[level].length,
            id: sequence,
            color: colour,
          }
          this.levelColumn.push(this.levelObj)
        })
        this.levelColumn.sort((a: { id: number }, b: { id: number }) => (a.id > b.id ? 1 : -1))
        // console.log(this.levelColumn)
        this.userData.forEach((userObj: any) => {
          this.userTableObj = {
            StarsId: userObj.stars_id,
            UserName: userObj.username,
          }
          userObj.levels['Certified'].map((courseObj: any) => {
            const courseCode = courseObj['Course Code']
            const courseTitle = courseObj['Course Title']
            const obj = { title: courseTitle, code: courseCode }
            const o = this.displayedColumnsTwo.find((present: any) => present.code === obj.code)
            // console.log(o)
            if (!o) {
              this.displayedColumnsTwo.push(obj)
            }

            this.userTableObj[courseTitle] = courseObj['Progress']
          })
          if (userObj.levels['Professional']) {
            userObj.levels['Professional'].map((courseObj: any) => {
              const courseCode = courseObj['Course Code']
              const courseTitle = courseObj['Course Title']
              const obj = { title: courseTitle, code: courseCode }
              const o = this.displayedColumnsTwo.find((present: any) => present.code === obj.code)
              // console.log(o)
              if (!o) {
                this.displayedColumnsTwo.push(obj)
              }
              this.userTableObj[courseTitle] = courseObj['Progress']
            })
          }
          if (userObj.levels['Master']) {
            userObj.levels['Master'].map((courseObj: any) => {
              const courseCode = courseObj['Course Code']
              const courseTitle = courseObj['Course Title']
              const obj = { title: courseTitle, code: courseCode }
              const o = this.displayedColumnsTwo.find((present: any) => present.code === obj.code)
              // console.log(o)
              if (!o) {
                this.displayedColumnsTwo.push(obj)
              }
              this.userTableObj[courseTitle] = courseObj['Progress']
            })
          }
          // console.log(this.userTableObj)
          // console.log(this.displayedColumnsTwo)
          this.userTableData.push(this.userTableObj)
        })
        // console.log(response.user_progress.length)
        this.userFetchStatus = 'done'
      },
      () => {
        this.userFetchStatus = 'error'
      },
    )
  }

  onTabChangeClient(event: any) {
    this.page = 1
    this.getUserData(event.tab.textLabel, 1)
  }

  changePageNext(role: string) {
    this.page += 1

    this.getUserData(role, this.page)
    if (this.page > 1) {
      this.disabled = false
    }
  }
  changePagePrev(role: string) {
    if (this.page === 2) {
      this.page -= 1
      this.disabled = true
    } else if (this.page <= 1) {
      this.disabled = true
    } else {
      this.page -= 1
      this.disabled = false
    }
    this.getUserData(role, this.page)
  }
}
