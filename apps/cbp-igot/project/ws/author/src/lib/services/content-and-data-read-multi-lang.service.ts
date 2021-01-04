import { Injectable } from '@angular/core'
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router'
import { Observable, of } from 'rxjs'
import { ApiService } from '../modules/shared/services/api.service'
import { NSContent } from '../interface/content'
import { CONTENT_READ_HIERARCHY_AND_DATA } from '../constants/apiEndpoints'
import { catchError } from 'rxjs/operators'

@Injectable()
export class ContentAndDataReadMultiLangTOCResolver implements Resolve<{ content: NSContent.IContentMeta, data: any }[] | null> {

  constructor(
    private apiService: ApiService,
    private router: Router,
  ) {
  }

  resolve(
    route: ActivatedRouteSnapshot,
  ): Observable<{ content: NSContent.IContentMeta, data: any }[]> | null {
    const id = route.params['id']
    if (id !== 'new') {
      return this.apiService.get<{ content: NSContent.IContentMeta, data: any }[]>(
        `${CONTENT_READ_HIERARCHY_AND_DATA}${id}?mode=edit`,
      ).pipe(
        catchError((v: any) => {
          this.router.navigateByUrl('/error-somethings-wrong')
          return of(v)
        }),
      )
    }
    return null
  }
}
