import { Component, OnInit, Input } from '@angular/core'
import { CounterService } from '../../services/counter.service'
import { TFetchStatus } from '@ws-widget/utils'
import { IWsStatsConfig } from '../../models/ocm.model'

import { IWsCounterPlatformResponse, IWsCounterInfyMeResponse } from '../../models/counter.model'

type TStatTitleKey = 'lex' | 'infyme'
@Component({
  selector: 'ws-app-adoption-dashboard',
  templateUrl: './adoption-dashboard.component.html',
  styleUrls: ['./adoption-dashboard.component.scss'],
})
export class AdoptionDashboardComponent implements OnInit {
  @Input() config: IWsStatsConfig | null = null
  lexCounterData: IWsCounterPlatformResponse | null = null
  infyMeCounterData: IWsCounterInfyMeResponse | null = null
  lexStatFetchStatus: TFetchStatus = 'none'
  infyMeStatFetchStatus: TFetchStatus = 'none'

  stats: { title: string; titleKey: string; iconName: string; count: number }[] = []
  currentStat: TStatTitleKey = 'lex'
  counterEnabled = false
  constructor(private counterSvc: CounterService) {}

  ngOnInit() {
    // const instanceConfig =  this.configSvc.getInstanceConfig()
    // const counterStatus = instanceConfig.featureStatus.get(EnumWsInstanceFeature.COUNTER)
    // if (counterStatus) {
    // this.counterEnabled = counterStatus.isAvailable
    this.counterEnabled = true
    // }
    this.currentStat = 'lex'
    this.statsClicked()
  }

  statsClicked() {
    switch (this.currentStat) {
      case 'lex': {
        this.lexStatFetchStatus = 'fetching'
        this.fetchLexCounterData()
        break
      }
      case 'infyme': {
        this.infyMeStatFetchStatus = 'fetching'
        this.fetchInfyMeCounterData()
        break
      }
    }
  }

  fetchLexCounterData() {
    this.counterSvc.fetchPlatformCounterData().subscribe(
      data => {
        this.lexCounterData = data
        this.lexStatFetchStatus = 'done'
        if (this.lexCounterData) {
          this.processLexCounterData()
        }
      },
      () => {
        this.lexStatFetchStatus = 'error'
      },
    )
  }

  fetchInfyMeCounterData() {
    this.counterSvc.fetchInfyMeCounterData().subscribe(
      data => {
        this.infyMeCounterData = data
        this.infyMeStatFetchStatus = 'done'
        if (this.infyMeCounterData) {
          this.processInfyMeCounterData()
        }
      },
      () => {
        this.infyMeStatFetchStatus = 'error'
      },
    )
  }

  processLexCounterData() {
    const lexCounterData = this.lexCounterData
    if (lexCounterData) {
      this.stats = this.stats.filter(stat => stat.titleKey !== 'lex')
      this.stats.push(
        {
          title: 'Total unique learners on LEX',
          titleKey: 'lex',
          iconName: 'people',
          count: lexCounterData.users[lexCounterData.users.length - 1].count,
        },
        {
          title: 'Average request per second',
          titleKey: 'lex',
          iconName: 'access_time',
          count: lexCounterData.load[lexCounterData.load.length - 1].count,
        },
        {
          title: 'Active Learners in last 5 mins',
          titleKey: 'lex',
          iconName: 'notifications',
          count: lexCounterData.learners[lexCounterData.learners.length - 1].count,
        },
      )
    }
  }

  processInfyMeCounterData() {
    const infyMeCounterData = this.infyMeCounterData
    if (infyMeCounterData) {
      this.stats = this.stats.filter(stat => stat.titleKey !== 'infyme')
      this.stats.push(
        {
          title: 'Total downloads',
          titleKey: 'infyme',
          iconName: '',
          count: infyMeCounterData.totalDownloads,
        },
        {
          title: 'Yesterday\'s Download ',
          titleKey: 'infyme',
          iconName: '',
          count: infyMeCounterData.yesterdaysDownloads,
        },
      )
    }
  }
}
