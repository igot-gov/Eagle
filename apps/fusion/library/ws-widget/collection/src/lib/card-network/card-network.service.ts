import { HttpClient } from '@angular/common/http'
import { map } from 'rxjs/operators'
import { Injectable } from '@angular/core'

const API_END_POINTS = {
  GET_ALL_ACTIVE_USER: `/apis/protected/v8/networkHub/users`,
}

@Injectable({
  providedIn: 'root',
})
export class CardNetWorkService {
  constructor(
    private http: HttpClient) { }

  fetchLatestUserInfo(data: any) {
    return this.http.post<any>(API_END_POINTS.GET_ALL_ACTIVE_USER, data).pipe(
      map(response => {
        return response
      }),
    )
  }

}
