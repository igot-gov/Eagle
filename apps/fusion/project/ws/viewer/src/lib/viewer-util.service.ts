import { ConfigurationsService } from '@ws-widget/utils'
import { Injectable } from '@angular/core'
import { HttpClient } from '@angular/common/http'
import { Observable } from 'rxjs'
import { ContentProgressService } from '../../../../../library/ws-widget/collection/src/lib/_common/content-progress/content-progress.service'
import { NsContent } from '@ws-widget/collection'

@Injectable({
  providedIn: 'root',
})
export class ViewerUtilService {
  API_ENDPOINTS = {
    setS3Cookie: `/apis/v8/protected/content/setCookie`,
    PROGRESS_UPDATE: `/apis/protected/v8/user/realTimeProgress/update`,
  }
  downloadRegex = new RegExp(`(/content-store/.*?)(\\\)?\\\\?['"])`, 'gm')
  authoringBase = '/apis/authContent/'
  constructor(
    private http: HttpClient,
    private configservice: ConfigurationsService,
    private progressSvc: ContentProgressService,
    ) { }

  async fetchManifestFile(url: string) {
    this.setS3Cookie(url)
    const manifestFile = await this.http
      .get<any>(url)
      .toPromise()
      .catch((_err: any) => { })
    return manifestFile
  }

  private async setS3Cookie(contentId: string) {
    await this.http
      .post(this.API_ENDPOINTS.setS3Cookie, { contentId })
      .toPromise()
      .catch((_err: any) => { })
    return
  }

  realTimeProgressUpdate(contentId: string, request: any) {
    this.http
      .post(`${this.API_ENDPOINTS.PROGRESS_UPDATE}/${contentId}`, request)
      .subscribe((data: any) => {
        this.progressSvc.updateProgressHash(data)
      })
  }

  getContent(contentId: string): Observable<NsContent.IContent> {
    return this.http.get<NsContent.IContent>(
      // tslint:disable-next-line:max-line-length
      `/apis/authApi/action/content/hierarchy/${contentId}?rootOrg=${
      this.configservice.rootOrg || 'Infosys'
      }&org=${this.configservice.activeOrg || 'Infosys Ltd'}`,
    )
  }

  getAuthoringUrl(url: string): string {
    return url
      ? `/apis/authContent/${
      url.includes('/content-store/') ? new URL(url).pathname.slice(1) : encodeURIComponent(url)
      }`
      : ''
  }

  regexDownloadReplace = (_str = '', group1: string, group2: string): string => {
    return `${this.authoringBase}${encodeURIComponent(group1)}${group2}`
  }

  replaceToAuthUrl(data: any): any {
    return JSON.parse(
      JSON.stringify(data).replace(
        this.downloadRegex,
        this.regexDownloadReplace,
      ),
    )
  }

}
