// import {
//   CONTENT_BASE,
//   CONTENT_VIDEO_ENCODE,
//   CONTENT_BASE_ENCODE,
//   CONTENT_BASE_ZIP,
// } from '@ws/author/src/lib/constants/apiEndpoints'
// import { NSApiRequest } from '@ws/author/src/lib/interface/apiRequest'
import { Injectable } from '@angular/core'
import { Observable } from 'rxjs'
import { tap } from 'rxjs/operators'
// import { ApiService } from '@ws/author/src/lib/modules/shared/services/api.service'
// import { NSApiResponse } from '@ws/author/src/lib/interface//apiResponse'
// import { AccessControlService } from '@ws/author/src/lib/modules/shared/services/access-control.service'
// import { FIXED_FILE_NAME } from '../../../../../constants/upload'
import { HttpClient } from '@angular/common/http'
import { NSISelfCuration } from '../../../../../interface/self-curation'
// import { ConfigurationsService } from '@ws-widget/utils'

const PROTECTED_SLAG_V8 = '/apis/protected/v8'

const API_END_POINTS = {
  GET_PROFANITY: `${PROTECTED_SLAG_V8}/profanity/getPdfProfanityForContent`,
}

@Injectable()
export class SelfCurationService {
  curationData: { [key: string]: NSISelfCuration.ISelfCurationData } = {}
  currentContent!: string
  constructor(
    // private apiService: ApiService,
    // private accessService: AccessControlService,
    private http: HttpClient,
    // private configSvc: ConfigurationsService,
  ) { }

  getOriginalData(id: string): NSISelfCuration.ISelfCurationData {
    return this.curationData[id]
  }

  setcurationData(meta: NSISelfCuration.ISelfCurationData[]) {
    if (meta) {
      for (let i = 0; i < meta.length; i += 1) {
        this.curationData[meta[i].primaryKey.contentId] = JSON.parse(JSON.stringify(meta))
      }
    }
  }

  fetchresult(contentId: any): Observable<NSISelfCuration.ISelfCurationData[]> {
    return this.http.get<NSISelfCuration.ISelfCurationData[]>(`${API_END_POINTS.GET_PROFANITY}/${contentId}`)
      .pipe(tap(v => this.setcurationData(v)))
  }

  reset() {
    this.curationData = {}
    this.currentContent = ''
  }
}
