import { Injectable } from '@angular/core'
import { ApiService } from '@ws/author/src/lib/modules/shared/services/api.service'
// tslint:disable-next-line:max-line-length
const VALIDATE_PDF_CONTENT = '/apis/protected/v8/profanity/startPdfProfanity'
// const backwordSlash = '/'
/* tslint:disable */
import _ from 'lodash'
/* tslint:enable */

@Injectable()
export class ProfanityService {

  accessPath: string[] = []
  constructor(
    private apiService: ApiService,
  ) { }

  startProfanity(content: string, url: string, fileName: string) {
    const finalUrl = url.replace('?type=main', '')
    const finalFileName = this.getFileName(url)
    if (fileName) {
      // will remove
    }
    // if (fileName && finalFileName) {
    const requestData = {
      fileName: finalFileName,
      pdfDownloadUrl: finalUrl,
      contentId: content,
    }
    return this.apiService.post<any>(
      `${VALIDATE_PDF_CONTENT}`, requestData
    )
    // }
    // return {}
  }

  getFileName(url: string) {
    const nameArr = (url.match(new RegExp('%2Fartifacts%2F' + '(.*)' + '?type=main')))
    if (nameArr && nameArr[0]) {
      let finalFileName = nameArr[0].replace('%2Fartifacts%2F', '')
      finalFileName = finalFileName.replace('?type=main', '')
      return finalFileName
    }
    return null
  }
}
