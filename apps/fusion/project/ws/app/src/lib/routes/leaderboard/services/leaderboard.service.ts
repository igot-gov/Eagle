import { Injectable } from '@angular/core'
import {
  ELeaderboardDurationType,
  ILeaderboardPrevNext,
  EDurationTypeRouteParam,
} from '../models/leaderboard.model'
import { LeaderboardApiService } from '../apis/leaderboard-api.service'

@Injectable({
  providedIn: 'root',
})
export class LeaderboardService {
  private tabsMap: Map<EDurationTypeRouteParam, ELeaderboardDurationType>

  currentDateObj: {
    week: number;
    month: number;
    year: number;
  }

  constructor(private leaderboardApi: LeaderboardApiService) {
    const currentDate = new Date()

    this.currentDateObj = {
      week: this.getCurrentWeekNumber(currentDate),
      month: currentDate.getMonth() + 1,
      year: currentDate.getFullYear(),
    }

    this.tabsMap = new Map([
      [EDurationTypeRouteParam.Weekly, ELeaderboardDurationType.Weekly],
      [EDurationTypeRouteParam.Monthly, ELeaderboardDurationType.Monthly],
      [EDurationTypeRouteParam.HallOfFame, ELeaderboardDurationType.Monthly],
    ])
  }

  getApiParams(tab: EDurationTypeRouteParam, prevNext?: ILeaderboardPrevNext) {
    const lbParams = {
      durationType: this.tabsMap.get(tab) || ELeaderboardDurationType.Weekly,
      durationValue: 0,
      year: new Date().getFullYear(),
    }
    if (tab !== EDurationTypeRouteParam.HallOfFame) {
      if (!(prevNext && prevNext.duration_value)) {
        lbParams.durationValue =
          this.tabsMap.get(tab) === ELeaderboardDurationType.Weekly
            ? this.getCurrentWeekNumber(new Date())
            : new Date().getMonth() + 1
      } else {
        lbParams.durationValue = prevNext.duration_value
      }
      lbParams.year = !(prevNext && prevNext.duration_value)
        ? lbParams.year
        : prevNext.leaderboard_year
    }
    return lbParams
  }

  getLeaderboard(tab: EDurationTypeRouteParam, prevNext?: ILeaderboardPrevNext) {
    const lbParams = this.getApiParams(tab, prevNext)
    return this.leaderboardApi.getLeaderboardData(
      lbParams.durationType,
      lbParams.durationValue,
      lbParams.year,
    )
  }

  getCurrentWeekNumber(inputDate: Date) {
    const date: Date = new Date(
      Date.UTC(inputDate.getFullYear(), inputDate.getMonth(), inputDate.getDate()),
    )
    const dayNum = date.getUTCDay() || 7
    date.setUTCDate(date.getUTCDate() + 4 - dayNum)
    const yearStart = new Date(Date.UTC(date.getUTCFullYear(), 0, 1))
    return Math.ceil(((date.getTime() - yearStart.getTime()) / 86400000 + 1) / 7)
  }
}
