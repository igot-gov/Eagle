import { Injectable } from '@angular/core'
import { HttpClient } from '@angular/common/http'
import { Observable } from 'rxjs'
import { getStringifiedQueryParams } from '@ws-widget/utils'
import { NsContentStripNewMultiple } from './content-strip-new-multiple.model'

@Injectable({
  providedIn: 'root',
})
export class ContentStripNewMultipleService {

  constructor(
    private http: HttpClient,
  ) { }

  getContentStripResponseApi(request: NsContentStripNewMultiple.IStripRequestApi, filters?: { [key: string]: string | undefined }):
    Observable<NsContentStripNewMultiple.IContentStripResponseApi> {
    let stringifiedQueryParams = ''
    stringifiedQueryParams = getStringifiedQueryParams({
      pageNo: request.queryParams ? request.queryParams.pageNo : undefined,
      pageSize: request.queryParams ? request.queryParams.pageSize : undefined,
      pageState: request.queryParams ? request.queryParams.pageState : undefined,
      sourceFields: request.queryParams ? request.queryParams.sourceFields : undefined,
      filters: filters ? encodeURIComponent(JSON.stringify(filters)) : undefined,
    })
    let url = request.path
    url += stringifiedQueryParams ? `?${stringifiedQueryParams}` : ''
    return this.http.get<NsContentStripNewMultiple.IContentStripResponseApi>(url)
  }
}
