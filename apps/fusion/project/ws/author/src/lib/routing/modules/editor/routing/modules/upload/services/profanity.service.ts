import { Injectable } from '@angular/core'
import { ApiService } from '@ws/author/src/lib/modules/shared/services/api.service'
// tslint:disable-next-line:max-line-length
const VALIDATE_PDF_CONTENT = '/apis/protected/v8/profanity/validatePdfContent'
// const backwordSlash = '/'

@Injectable()
export class ProfanityService {

  accessPath: string[] = []
  constructor(
    private apiService: ApiService,
  ) { }

  featchProfanity(content: string, url: string) {
    // tslint:disable-next-line:no-console
    console.log(content)
    // tslint:disable-next-line:no-console
    const finalUrl = url.replace('type=main', '')
    // tslint:disable-next-line:no-console
    console.log(finalUrl)
    const requestData = {
      pdfDownloadUrl: finalUrl,
    }
    // const userId = this.configSvc.userProfile && this.configSvc.userProfile.userId
    return this.apiService.post<any>(
      `${VALIDATE_PDF_CONTENT}`, requestData
    )
  }
}
