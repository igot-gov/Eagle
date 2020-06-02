import {
  Component, OnInit,
  Output,
  EventEmitter,
} from '@angular/core'
import { MatSelectChange } from '@angular/material'
import { TFetchStatus, ConfigurationsService } from '@ws-widget/utils'
import { GamificationService } from '../../../../services/gamification.service'
import { FormControl } from '@angular/forms'
import { DatePipe } from '@angular/common'
@Component({
  selector: 'ws-app-leaderboard',
  templateUrl: './leaderboard-home.component.html',
  styleUrls: ['./leaderboard-home.component.scss'],
})
export class LeaderboardHomeComponent implements OnInit {
  public sideNavBarOpened = false
  maxDate: Date
  leaderboard!: any
  query!: any
  leaderBoardGuild!: any
  rolesList!: any
  originalLeaderboard!: any
  sprint!: any
  sprintSelected!: any
  dealers!: any
  selectedRole!: string
  fetchStatus: TFetchStatus
  leaderBoardFirstRowGuild!: any
  selectedRegion!: string
  userName: string | undefined
  roleName!: string
  dealerCode!: any
  dateStr!: any
  dateEnd!: any
  regionName!: string
  startDate = new FormControl(new Date())
  endDate = new FormControl(new Date())
  kpiList = [{}]
  kpiSelected!: any

  @Output() langChangedEvent = new EventEmitter<string>()

  constructor(
    private gamificationSvc: GamificationService,
    private configSvc: ConfigurationsService,
    private datePipe: DatePipe,
  ) {
    if (this.configSvc.userProfile) {
      this.userName = this.configSvc.userProfile.userName
    }
    this.fetchStatus = 'none'
    this.maxDate = new Date()
    this.leaderBoardGuild = []
  }

  ngOnInit() {
    this.kpiList = [{
      name: 'Guild',
      EntityCode: '',
      value: 0,

    },
    {
      name: 'Aggregate',
      EntityCode: '',
      value: 1,
    },
    {
      name: 'Certifications',
      EntityCode: '3',
      value: 73936,
    },
    {
      name: 'Comments',
      EntityCode: '3',
      value: 28,
    },
    {
      name: 'Content',
      EntityCode: '3',
      value: 4,
    },
    {
      name: 'Forum Posts',
      EntityCode: '3',
      value: 23,
    },
    {
      name: 'Peer Sharing',
      EntityCode: '3',
      value: 21,
    },
    {
      name: 'Quizzes',
      EntityCode: '3',
      value: 1,
    },
    ]
    this.rolesList = ['All', 'Sales Manager', 'Service Technician',
      'Parts Manager', 'Sales Consultant', 'Service Manager', 'Service Advisor', 'Dealer/Partner',
      'Parts Sales Person']
    this.fetchGuildLeaderboardData(null, null, '')
    this.fetchDealersData()
    this.sprintSelected = 'fy'
    this.selectedRole = 'All'
  }

  onkpiChange(kpi: any) {
    this.kpiList.forEach((each: any) => {
      if (each.name === kpi.value) {
        this.kpiSelected = each
      }
    })
    if (kpi.value === 'Guild') {
      this.fetchGuildLeaderboardData(null, null, this.kpiSelected.EntityCode)
    }
    if (kpi.value === 'Aggregate') {
      this.fetchActivityLeaderboardData(null, null, this.kpiSelected.EntityCode, this.kpiSelected.value)
    } else if (kpi.value !== 'Guild') {
      this.fetchActivityLeaderboardData(null, null, this.kpiSelected.EntityCode, this.kpiSelected.value)
    }
  }

  fetchGuildLeaderboardData(dateSt: any, dateEn: any, entityCode: string) {
    if (dateSt && dateEn) {
      this.dateStr = this.datePipe.transform(dateSt, 'yyyy-MM-dd')
      this.dateEnd = this.datePipe.transform(dateEn, 'yyyy-MM-dd')
    }
    this.fetchStatus = 'fetching'
    this.gamificationSvc.fetchGuildLeaderboard(this.sprint, this.dateStr, this.dateEnd, entityCode).subscribe(data => {
      this.leaderBoardGuild = data
      this.originalLeaderboard = data
      this.leaderBoardFirstRowGuild = this.leaderBoardGuild.filter(
        (each: any) => each.FirstName === this.userName
      )
      this.fetchStatus = 'done'
    })
  }

  fetchActivityLeaderboardData(dateSt: any, dateEn: any, entityCode: string, activityId: number) {
    if (dateSt && dateEn) {
      this.dateStr = this.datePipe.transform(dateSt, 'yyyy-MM-dd')
      this.dateEnd = this.datePipe.transform(dateEn, 'yyyy-MM-dd')
    }
    this.fetchStatus = 'fetching'
    this.gamificationSvc.fetchLeaderBoard(this.sprint, this.dateStr, this.dateEnd, entityCode, activityId).subscribe(data => {
      this.leaderBoardGuild = data
      this.originalLeaderboard = data
      this.leaderBoardFirstRowGuild = this.leaderBoardGuild.filter(
        (each: any) => each.FirstName === this.userName
      )
      this.fetchStatus = 'done'
    })
  }

  private fetchDealersData() {
    this.gamificationSvc.fetchDealers().subscribe(data => {
      this.dealers = data.Dealers
    })
  }

  // Function to capture sprint value
  sprintChanged(path: MatSelectChange) {
    if (path.value === 'h1') {
      this.sprint = 36097
    }
    if (path.value === 'h2') {
      this.sprint = 36098
    }
    if (path.value === 'fy') {
      this.sprint = ''
    }
    this.fetchGuildLeaderboardData(null, null, '')
  }

  roleChanged(path: MatSelectChange) {
    this.roleName = path.value
    if (this.dealerCode === undefined) {
      this.dealerCode = 'All Dealers'
    }
    if (this.regionName === undefined) {
      this.regionName = 'All'
    }
    this.applyFilter(this.roleName, this.regionName, this.dealerCode)
  }

  regionChanged(path: MatSelectChange) {
    this.regionName = path.value
    if (this.dealerCode === undefined) {
      this.dealerCode = 'All Dealers'
    }
    if (this.roleName === undefined) {
      this.roleName = 'All'
    }
    this.applyFilter(this.roleName, this.regionName, this.dealerCode)
  }

  dealerChanged(path: MatSelectChange) {
    this.dealerCode = path.value
    if (this.roleName === undefined) {
      this.roleName = 'All'
    }
    if (this.regionName === undefined) {
      this.regionName = 'All'
    }
    this.applyFilter(this.roleName, this.regionName, this.dealerCode)

  }

  applyFilter(role: string, region: string, dealer: any) {
    this.leaderBoardGuild = this.originalLeaderboard
    if (role !== undefined && role !== 'All') {
      this.leaderBoardGuild = this.leaderBoardGuild.filter(
        (each: any) => each.Column2 === role
      )
    }
    if (region !== undefined && region !== 'All') {
      this.leaderBoardGuild = this.leaderBoardGuild.filter(
        (each: any) => each.City === region
      )
    }
    if (dealer !== undefined && dealer !== 'All Dealers') {
      this.leaderBoardGuild = this.leaderBoardGuild.filter(
        (each: any) => each.dealerCode === dealer
      )
    }
  }

  handleSearchQuery(value: any) {
    this.leaderBoardGuild = this.originalLeaderboard
    this.leaderBoardGuild = Object.assign([], this.leaderBoardGuild).filter(
      (item: any) => item.FirstName.toLowerCase().indexOf(value.toLowerCase()) > -1
    )
  }

}
