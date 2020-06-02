import { HttpClient } from '@angular/common/http'
import { ConfigurationsService } from '@ws-widget/utils'
import { Injectable } from '@angular/core'
import { Observable } from 'rxjs'
import { NSApiResponse } from '@ws/author/src/lib/interface/apiResponse'

@Injectable()
export class UploadPdfService {
  constructor(private http: HttpClient, private configSvc: ConfigurationsService) {}

  updateEmailId(data: any): Observable<any> {
    return this.http.put<any>(`email`, data)
  }

  upload(data: FormData): Observable<NSApiResponse.IFileApiResponse> {
    return this.http.post<NSApiResponse.IFileApiResponse>(
      `/apis/authContent/upload/Siemens/meta-assets/lex_profile_${
        this.configSvc.userProfile ? this.configSvc.userProfile.userId : ''
      }/assets`,
      data,
    )
  }
  publish(): Observable<any> {
    return this.http.post<any>(
      `/apis/authContent/publish/Siemens/meta-assets/lex_profile_${
        this.configSvc.userProfile ? this.configSvc.userProfile.userId : ''
      }/assets`,
      undefined,
    )
  }
}
