import { Injectable } from '@angular/core'
import { ConfigurationsService } from '@ws-widget/utils'
import { ApiService } from '@ws/author/src/lib/modules/shared/services/api.service'
// tslint:disable-next-line:max-line-length
const CONTENT_READ_MULTIPLE_HIERARCHY = '/apis/protected/v8/profanity/checkProfanity/'
const backwordSlash = '/'

@Injectable()
export class ProfanityService {

  accessPath: string[] = []
  constructor(
    private apiService: ApiService,
    private configSvc: ConfigurationsService,
  ) { }

  featchProfanity(content: string) {
    const userId = this.configSvc.userProfile && this.configSvc.userProfile.userId
    return this.apiService.get<any>(
      `${CONTENT_READ_MULTIPLE_HIERARCHY}` + `${content}` + `${backwordSlash}` + `${userId}`,
    )
  }
}
