import { HttpClient } from '@angular/common/http'
import { map, catchError } from 'rxjs/operators'
import { Injectable } from '@angular/core'
import { ConfigurationsService } from '@ws-widget/utils/src/public-api'
import { NSNetworkDataV2 } from '@ws/app/src/lib/routes/network-v2/models/network-v2.model'
import { NetworkV2Service } from '@ws/app/src/lib/routes/network-v2/services/network-v2.service'
import { of } from 'rxjs'

const API_END_POINTS = {
  GET_ALL_ACTIVE_USER: `/apis/protected/v8/networkHub/users`,
  GET_ALL_SEARCH_USER: `/apis/protected/v8/user/profileRegistry/searchUserRegistry`,
}

@Injectable({
  providedIn: 'root',
})
export class CardNetWorkService {
  constructor(
    private http: HttpClient,
    private configSvc: ConfigurationsService,
    private networkV2Service: NetworkV2Service,
  ) { }

  fetchLatestUserInfo(data: any) {
    return this.http.post<any>(API_END_POINTS.GET_ALL_ACTIVE_USER, data).pipe(
      map(response => {
        return response
      }),
    )
  }
  // fetchSearchUserInfo(searchKey: string) {
  //   return this.http.post<any>(API_END_POINTS.GET_ALL_SEARCH_USER).pipe(
  //     map(response => {
  //       return response
  //     }),
  //   )
  // }
  fetchSearchUserInfo(searchKey: string) {

    let req: NSNetworkDataV2.ISearchUserReq
    req = {
      limit: 50,
      offset: 0,
      filters: {
        personalDetails: {
          firstname: {
            startsWith: searchKey,
          },
        },
      },
    }
    return this.http.post<any>(API_END_POINTS.GET_ALL_SEARCH_USER, req).pipe(
      map(response => {
        return response
      }),
    )
  }

  fetchMyMdoUsers() {
    let usrDept = 'iGOT'
    if (this.configSvc.userProfile) {
      usrDept = this.configSvc.userProfile.departmentName || 'iGOT'
    }
    let req: NSNetworkDataV2.IRecommendedUserReq
    req = {
      size: 50,
      offset: 0,
      search: [
        {
          field: 'employmentDetails.departmentName',
          values: [usrDept],
        },
      ],
    }
    return this.networkV2Service.fetchAllRecommendedUsers(req).pipe(
      map((data: any) => ({ data, error: null })),
      catchError(error => of({ error, data: null })),
    )
  }

}
