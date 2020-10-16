import { Injectable } from '@angular/core'
import { ActivatedRouteSnapshot, Resolve } from '@angular/router'

import { Observable, forkJoin, of } from 'rxjs'

@Injectable({
  providedIn: 'root',
})
export class InitResolveService implements Resolve<any> {
  constructor(
  ) { }

  resolve(route: ActivatedRouteSnapshot): Observable<any> {
    const forkProcess: Observable<any>[] = [of(undefined)]
    // const pushedJobs: string[] = ['']
    const data: string[] = route.data ? route.data.load || [] : []

    if (data.includes('ckeditor')) {
      // forkProcess.push(this.zipJSInject.inject())
    }
    return forkJoin(forkProcess).pipe()
    // tap(v => {
    // if (pushedJobs.includes('config')) {
    //   this.authInitService.ownerDetails = v[pushedJobs.indexOf('config')].ownerDetails
    // }
    // })
    // ),
    // catchError((v: any) => {
    //   this.router.navigateByUrl('/error-somethings-wrong')
    //   return of(v)
    // }),

  }
}
