import { HttpClient } from '@angular/common/http'
import { Injectable } from '@angular/core'
import { Observable } from 'rxjs'
import { NsAnalytics } from '../models/learning-analytics.model'

// const PROTECTED_SLAG_V8 = `https://ford-staging.onwingspan.com/LA/api`
const PROTECTED_SLAG_V8 = `/apis/proxies/v8/LA/api`
// const PROTECTED_SLAG_V8 = 'http://kmserver11:6004/api'

const LA_API_END_POINTS = {
  TIME_SPENT: `${PROTECTED_SLAG_V8}/la/timespent`,
  CONTENT: `${PROTECTED_SLAG_V8}/la/content`,
  // TABS: `${PROTECTED_SLAG_V8}/la/getConfig`,
  CHANNELS: `${PROTECTED_SLAG_V8}/la/channelAnalytics`,
  DOWNLOAD: `${PROTECTED_SLAG_V8}/la/downloadReport`,
  REPORTS: `${PROTECTED_SLAG_V8}/la/getReports`,
  HOURLY: `${PROTECTED_SLAG_V8}/la/hourlyUsage`,
  PATHWAY: `${PROTECTED_SLAG_V8}/pathway`,
  ROLE: `${PROTECTED_SLAG_V8}/la/pathwayRoles`,
  USER_PROGRESS: `${PROTECTED_SLAG_V8}/la/pathwayUserProgress`,
  TABS: `${PROTECTED_SLAG_V8}/la/showPathway`,
}
@Injectable({
  providedIn: 'root',
})
export class LearningAnalyticsService {
  constructor(private http: HttpClient) {}
  timeSpent(
    selectedEndDate: string,
    selectedStartDate: string,
    contentType: string,
    filterArray: NsAnalytics.IFilterObj[],
    searchQuery: string,
  ): Observable<null> {
    const filters = JSON.stringify(filterArray)
    return this.http.get<null>(
      // tslint:disable-next-line: max-line-length
      `${LA_API_END_POINTS.TIME_SPENT}?aggsSize=200&endDate=${selectedEndDate}&startDate=${selectedStartDate}&from=0&contentType=${contentType}&search_query=${searchQuery}&filters=${filters}`,
    )
  }
  content(
    selectedEndDate: string,
    selectedStartDate: string,
    contentType: string,
    filterArray: NsAnalytics.IFilterObj[],
    searchQuery: string,
  ): Observable<null> {
    const filters = JSON.stringify(filterArray)
    return this.http.get<null>(
      // tslint:disable-next-line: max-line-length
      `${LA_API_END_POINTS.CONTENT}?aggsSize=200&endDate=${selectedEndDate}&startDate=${selectedStartDate}&from=0&contentType=${contentType}&search_query=${searchQuery}&filters=${filters}`,
    )
  }
  hourlyFilterData(
    filterKey: string,
    selectedEndDate: string,
    selectedStartDate: string,
    contentType: string,
    filterArray: NsAnalytics.IFilterObj[],
    searchQuery: string,
  ): Observable<null> {
    const filters = JSON.stringify(filterArray)
    return this.http.get<null>(
      // tslint:disable-next-line:max-line-length
      `${LA_API_END_POINTS.HOURLY}?aggsSize=200&endDate=${selectedEndDate}&startDate=${selectedStartDate}&from=0&contentType=${contentType}&search_query=${searchQuery}&filters=${filters}&week=${filterKey}`,
    )
  }
  // tslint:disable-next-line:max-line-length
  channels(
    selectedEndDate: string,
    selectedStartDate: string,
    contentType: string,
    filterArray: NsAnalytics.IFilterObj[],
    searchQuery: string,
  ): Observable<null> {
    const filters = JSON.stringify(filterArray)
    return this.http.get<null>(
      // tslint:disable-next-line: max-line-length
      `${LA_API_END_POINTS.CHANNELS}?endDate=${selectedEndDate}&startDate=${selectedStartDate}&from=0&contentType=${contentType}&search_query=${searchQuery}&filters=${filters}`,
    )
  }

  fetchOutput(
    reportId: string,
    selectedStartDate: string,
    selectedEndDate: string,
    filterArray: NsAnalytics.IFilterObj[],
  ): Observable<Blob> {
    const filters = JSON.stringify(filterArray)
    // tslint:disable-next-line:max-line-length
    const downloadLink = `${LA_API_END_POINTS.DOWNLOAD}?report_id=${reportId}&endDate=${selectedEndDate}&startDate=${selectedStartDate}&filters=${filters}`
    return this.http.get(downloadLink, { responseType: 'blob' })
  }
  reportsData(): Observable<NsAnalytics.IReportResponse> {
    return this.http.get<NsAnalytics.IReportResponse>(`${LA_API_END_POINTS.REPORTS}`)
  }
  getTabs(): Observable<null> {
    return this.http.get<null>(`${LA_API_END_POINTS.TABS}`)
  }
  getRoleCompletionData(): Observable<NsAnalytics.IRoleCompletionResponse> {
    return this.http.get<NsAnalytics.IRoleCompletionResponse>(`${LA_API_END_POINTS.PATHWAY}`)
  }
  getRoleNameData(): Observable<NsAnalytics.IRoleNameData> {
    return this.http.get<NsAnalytics.IRoleNameData>(`${LA_API_END_POINTS.ROLE}`)
  }
  getUserProgress(role: string, page: number): Observable<NsAnalytics.ICourseCompletionResponse> {
    return this.http.get<NsAnalytics.ICourseCompletionResponse>(
      `${LA_API_END_POINTS.USER_PROGRESS}?role=${role}&page=${page}`,
    )
  }
}
