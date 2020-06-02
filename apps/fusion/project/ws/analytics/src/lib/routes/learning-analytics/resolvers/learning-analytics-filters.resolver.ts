import { Injectable } from '@angular/core'
import { ReplaySubject } from 'rxjs'
import { NsAnalytics } from '../models/learning-analytics.model'
@Injectable({
  providedIn: 'root',
})
export class AnalyticsResolver {
  dateEventChangeSubject = new ReplaySubject<{ startDate: string, endDate: string }>(1)
  searchEventChangeSubject = new ReplaySubject<{ searchQuery: string }>(1)
  removeFilterEventChangeSubject = new ReplaySubject<NsAnalytics.IFilter[]>(1)
  dataFilterEventChangeSubject = new ReplaySubject<{ filterName: string, filterType: string | undefined }>(1)
  dataEvent = undefined
  dateEvent = undefined
  searchEvent = undefined
  removeEvent = undefined
  constructor() { }
  setDateFilterEvent(dateEvent: { startDate: string, endDate: string }) {
    this.dateEventChangeSubject.next(dateEvent)
  }
  setSearchFilterEvent(searchEvent: { searchQuery: string }) {
    this.searchEventChangeSubject.next(searchEvent)
  }
  removeFilterEvent(removeEvent: NsAnalytics.IFilter[]) {
    this.removeFilterEventChangeSubject.next(removeEvent)
  }
  dataFilterEvent(dataEvent: { filterName: string, filterType: string | undefined }) {
    this.dataFilterEventChangeSubject.next(dataEvent)
  }
}
