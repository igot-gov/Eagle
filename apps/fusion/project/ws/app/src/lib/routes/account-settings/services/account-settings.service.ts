import { Injectable } from '@angular/core'
import { HttpClient } from '@angular/common/http'
import { NsAccountSettings } from '../models/account-settings.model'
import { Observable } from 'rxjs'
import { ApiService } from '@ws/author/src/lib/modules/shared/services/api.service'
import { NSApiResponse } from '@ws/author/src/lib/interface//apiResponse'
import { NsMiniProfile } from '../../../../../../../../library/ws-widget/collection/src/public-api'
import { ConfigurationsService } from '../../../../../../../../library/ws-widget/utils/src/lib/services/configurations.service'

const API_END_POINTS = {
  accountsettings: `/apis/protected/v8/user/account-settings`,
  updateEmailId: `/apis/protected/v8/user/change-email`,
  userMiniProfile: (wid: string) => `/apis/protected/v8/user/mini-profile/${wid}`,
  resetPassword: `/apis/protected/v8/user/account-settings/resetPassword`,
}

@Injectable({
  providedIn: 'root',
})
export class AccountSettingsService {

  constructor(private http: HttpClient, private apiService: ApiService, private configSvc: ConfigurationsService) { }

  accountSettings(accountsettingsObj: any): Observable<NsMiniProfile.IMiniProfileData> {
    return this.http.post<NsMiniProfile.IMiniProfileData>(API_END_POINTS.accountsettings, accountsettingsObj)
  }
  updateEmailId(data: NsAccountSettings.IUserMetaTypeData): Observable<any> {
    return this.http.put<any>(`${API_END_POINTS.updateEmailId}/email`, data)
  }

  upload(
    data: FormData,
    userId: string,
  ): Observable<NSApiResponse.IFileApiResponse> {
    const file = data.get('content') as File
    let fileName = file.name
    fileName = this.appendToFilename(fileName)
    const newFormData = new FormData()
    newFormData.append('content', file, fileName)
    return this.apiService.post<NSApiResponse.IFileApiResponse>(
      `/apis/authContent/upload/Siemens/meta-assets/lex_profile_${userId}/assets`,
      newFormData,
      false,
    )
  }
  publish(

    userId: string,
  ): Observable<NSApiResponse.IFileApiResponse> {
    return this.apiService.post<NSApiResponse.IFileApiResponse>(
      `/apis/authContent/publish/Siemens/meta-assets/lex_profile_${userId}/assets`,
      undefined, false,

    )
  }

  appendToFilename(filename: string) {
    const timeStamp = new Date().getTime()
    const dotIndex = filename.lastIndexOf('.')
    if (dotIndex === -1) {
      return filename + timeStamp
    }
    return filename.substring(0, dotIndex) + timeStamp + filename.substring(dotIndex)
  }
  getToken(): Observable<any> {
    let changePasswordUrl = ''
    if (this.configSvc.instanceConfig && this.configSvc.instanceConfig.keycloak.changePasswordUrl) {
      // changePasswordUrl = 'https://siemens-staging.onwingspan.com/pid/reset-password/generate-token'
      changePasswordUrl = `${this.configSvc.instanceConfig.keycloak.changePasswordUrl}/pid/reset-password/generate-token`
    }

    return this.http.post<any>(changePasswordUrl, {})
  }

}
