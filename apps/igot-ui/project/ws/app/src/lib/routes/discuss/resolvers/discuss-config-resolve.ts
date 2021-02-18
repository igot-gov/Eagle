import { Injectable } from '@angular/core'
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router'
import { Observable, of } from 'rxjs'
// import { map, catchError } from 'rxjs/operators'
import { } from '@ws-widget/collection'
// import { IResolveResponse } from '@ws-widget/utils'
// import { DiscussService } from '../services/discuss.service'
// import { NSDiscussData } from '../models/discuss.model'
import { DiscussUtilsService } from '../services/discuss-utils.service'
// import { DiscussUtilsService } from '../services/discuss-util.service'

@Injectable()
export class DiscussConfigResolve
  implements
  Resolve<any> {
  constructor(private discussionSvc: DiscussUtilsService) { }

  resolve(
    _route: ActivatedRouteSnapshot,
    _state: RouterStateSnapshot,
  ): Observable<any> {

    const config = {
      menuOptions: [
        {
          route: 'categories',
          enable: true,
        },
        {
          route: 'tags',
          enable: true,
        },
        {
          route: 'All discussions',
          enable: true,
        },
        {
          route: 'my-discussion',
          enable: true,
        },
      ],
      userName: 'nptest',
      context: {
        id: 1,
      },
      categories: { result: [2] },
    }
    return (this.discussionSvc.getDiscussionConfig() ? this.discussionSvc.getDiscussionConfig() : config)
  }
}
