import { Injectable } from '@angular/core'
import { ApiService } from '@ws/author/src/lib/modules/shared/services/api.service'
// tslint:disable-next-line:max-line-length
const VALIDATE_PDF_CONTENT = '/apis/protected/v8/profanity/validatePdfContent'
const START_VALIDATE_PDF_CONTENT = '/apis/protected/v8/profanity/startPdfProfanity'
const GET_VALIDATE_PDF_CONTENT = '/apis/protected/v8/profanity/getPdfProfanity'
// const backwordSlash = '/'

@Injectable()
export class ProfanityService {

  accessPath: string[] = []
  constructor(
    private apiService: ApiService,
  ) { }

  featchProfanity(url: string) {
    const finalUrl = url.replace('?type=main', '')

    const requestData = {
      pdfDownloadUrl: finalUrl,
    }
    // const userId = this.configSvc.userProfile && this.configSvc.userProfile.userId
    return this.apiService.post<any>(
      `${VALIDATE_PDF_CONTENT}`, requestData
    )
  }
  startProfanity(content: string, url: string, filename: string) {
    let requestData = null
    if (url && url != null && url !== undefined) {
      const finalUrl = url.replace('?type=main', '')
      requestData = {
        pdfDownloadUrl: finalUrl,
        contentId: content,
        fileName: filename,
      }
      // tslint:disable-next-line:no-console
      console.log(requestData)
    }
    // tslint:disable-next-line:no-console
    console.log(requestData)
    // const userId = this.configSvc.userProfile && this.configSvc.userProfile.userId
    return this.apiService.post<any>(
      `${START_VALIDATE_PDF_CONTENT}`, requestData
    )
  }
  getProfanity(finalData: any) {
    let requestData = null

    if (finalData && finalData != null && finalData !== undefined) {
      const finalUrl = finalData.pdfDownloadUrl.replace('?type=main', '')
      requestData = {
        pdfDownloadUrl: finalUrl,
        contentId: finalData.contentId,
        fileName: finalData.fileName,
      }
      // tslint:disable-next-line:no-console
      console.log(requestData)
    }
    // tslint:disable-next-line:no-console
    console.log(requestData)
    return this.apiService.post<any>(
      `${GET_VALIDATE_PDF_CONTENT}`, requestData
    )

  }
}
