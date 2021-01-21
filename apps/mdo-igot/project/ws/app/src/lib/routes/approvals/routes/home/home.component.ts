import { AfterViewInit, Component, ElementRef, HostListener, OnDestroy, OnInit, ViewChild } from '@angular/core'
import { ActivatedRoute, Router, Event, NavigationEnd } from '@angular/router'
import moment from 'moment'
import { ConfigurationsService } from '../../../../../../../../../library/ws-widget/utils/src/public-api'
import { NeedApprovalsService } from '../../services/need-approvals.service'

@Component({
  selector: 'ws-app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent implements OnInit, AfterViewInit, OnDestroy {
  @ViewChild('stickyMenu', { static: true }) menuElement!: ElementRef
  tabsData!: any[]
  currentTab = 'needsapproval'
  sticky = false
  elementPosition: any
  userDetails: any
  fullname!: string
  wfHistory: any[] = []
  profileData: any[] = []
  profileDataKeys: any[] = []

  @HostListener('window:scroll', ['$event'])
  handleScroll() {
    const windowScroll = window.pageYOffset
    if (windowScroll >= this.elementPosition) {
      this.sticky = true
    } else {
      this.sticky = false
    }
  }
  constructor(
    private configSvc: ConfigurationsService,
    private activeRoute: ActivatedRoute,
    private router: Router,
    private needApprService: NeedApprovalsService
  ) {
    this.getDepartment()
    this.router.events.subscribe((event: Event) => {
      if (event instanceof NavigationEnd) {
        const workflowData = this.activeRoute.snapshot.data.workflowData.data.result.data[0] || {}
        let wfHistoryDatas = this.activeRoute.snapshot.data.workflowHistoryData.data.result.data || {}
        this.fullname = workflowData ? `${workflowData.userInfo.first_name} ${workflowData.userInfo.last_name}` : ''
        const datas: any[] = Object.values(wfHistoryDatas)
        wfHistoryDatas = [].concat.apply([], datas)
        const wfHistoryData = wfHistoryDatas.filter((wfh: { inWorkflow: any }) => !wfh.inWorkflow)
        let currentdate: Date

        this.activeRoute.data.subscribe(data => {
          this.profileData = data.pageData.data.profileData
          this.profileDataKeys = data.pageData.data.profileDataKey
        })

        wfHistoryData.forEach((wfh: any) => {
          currentdate = new Date(wfh.createdOn)
          if (typeof wfh.updateFieldValues === 'string') {
            const fields = JSON.parse(wfh.updateFieldValues)
            let pendingwfh: any
            let feildNameObj: any
            let feildKeyObj: any
            if (fields.length > 0) {
              fields.forEach((field: any) => {
                pendingwfh = field
                const labelKey = Object.keys(field.toValue)[0]
                const fieldKey = field.fieldKey
                feildNameObj = this.profileData ? this.profileData.filter(userData => userData.key === labelKey)[0] : {}
                feildKeyObj = this.profileDataKeys ? this.profileDataKeys.filter(userData => userData.key === fieldKey)[0] : {}
              })
              this.wfHistory.push({
                fieldKey: feildKeyObj ? feildKeyObj.name : null,
                requestedon: `${currentdate.getDate()}
                  ${moment(currentdate.getMonth() + 1, 'MM').format('MMM')}
                  ${currentdate.getFullYear()}
                  ${currentdate.getHours()} :
                  ${currentdate.getMinutes()} :
                  ${currentdate.getSeconds()}`,
                toValue: pendingwfh.toValue ? pendingwfh.toValue[Object.keys(pendingwfh.toValue)[0]] : null,
                fromValue: pendingwfh.fromValue ? pendingwfh.fromValue[Object.keys(pendingwfh.fromValue)[0]] : null,
                fieldName: feildNameObj ? feildNameObj.name : null,
                comment: wfh.comment ? wfh.comment : null,
                action: wfh.action ? wfh.action : null,
              })
            }
          }
        })
      }
    })
  }
  getDepartment() {
    this.needApprService.getMyDepartment().subscribe(res => {
      if (res && res.deptName) {
        this.configSvc.departName = res.deptName
      }
    })
  }
  ngOnInit() {
    this.tabsData = [{
      name: 'Needs approval',
      key: 'needsapproval',
      render: true,
      enabled: true,
    },
    {
      name: 'Personal details',
      key: 'personalInfo',
      render: true,
      enabled: true,
    },
    {
      name: 'Academics',
      key: 'academics',
      render: true,
      enabled: true,
    },
    {
      name: 'Professional details',
      key: 'profdetails',
      render: true,
      enabled: true,
    },
    {
      name: 'Certification and skills',
      key: 'skills',
      render: true,
      enabled: true,
    }]
  }

  ngAfterViewInit() {
    this.elementPosition = this.menuElement.nativeElement.parentElement.offsetTop
  }

  onSideNavTabClick(id: string) {
    this.currentTab = id
    const el = document.getElementById(id)
    if (el != null) {
      // el.style.backgroundColor = '#FDECDE'
      el.scrollIntoView({ behavior: 'smooth', block: 'start', inline: 'start' })
    }
  }

  ngOnDestroy(): void { }
}
