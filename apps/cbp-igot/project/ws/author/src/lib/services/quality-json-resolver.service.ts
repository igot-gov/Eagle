import { Injectable } from '@angular/core'
import { Resolve, Router } from '@angular/router'
import { Observable, of } from 'rxjs'
import { ApiService } from '../modules/shared/services/api.service'
import { catchError, map } from 'rxjs/operators'
import { NSIQuality } from '../interface/content-quality'
// import { ConfigurationsService } from '@ws-widget/utils'

@Injectable()
export class QualityJSONResolver implements Resolve<NSIQuality.IContentQualityConfig> {

  constructor(
    private apiService: ApiService,
    private router: Router,
    // private configSvc: ConfigurationsService,

  ) {
  }

  resolve(
  ): Observable<NSIQuality.IContentQualityConfig> {

    return this.apiService.get<NSIQuality.IContentQualityConfig>(
      `http://localhost:3003/protected/v8/scoring/getTemplate/content_scoring_template`, {}
    ).pipe(map((data: any) => {
      return data.result.result
    })).pipe(
      catchError((v: any) => {
        this.router.navigateByUrl('/error-somethings-wrong')
        return of(v)
      }),
    )
    // return this.apiService.get<NSIQuality.IContentQualityConfig>(
    //   `${this.configSvc.baseUrl}/feature/auth-content-quality.json`,
    // ).pipe(
    //   catchError((v: any) => {
    //     this.router.navigateByUrl('/error-somethings-wrong')
    //     return of(v)
    //   }))
  }
}
