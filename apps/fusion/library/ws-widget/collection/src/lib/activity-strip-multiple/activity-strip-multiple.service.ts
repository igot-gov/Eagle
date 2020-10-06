import { Injectable } from '@angular/core'
import { HttpClient } from '@angular/common/http'
import { Observable } from 'rxjs'
// import { getStringifiedQueryParams } from '@ws-widget/utils'
// import { NsNetworkStripNewMultiple } from './network-strip-multiple.model'

@Injectable({
  providedIn: 'root',
})
export class ActivityStripNewMultipleService {

  constructor(
    private http: HttpClient,
  ) { }
  fetchActivityCard(req: any, url: string): Observable<any> {
    return this.http.get<any>(url, req)
  }
}
