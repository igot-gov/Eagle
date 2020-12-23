import { Injectable } from '@angular/core'
import { HttpClient, HttpBackend } from '@angular/common/http'
import { Observable } from 'rxjs'
import { map } from 'rxjs/operators'

const API_END_POINTS = {
  USER_SIGNUP: `/apis/public/v8/signup/create`,
  // USER_SIGNUP: `http://localhost:3003/public/v8/signup/create`,
}

@Injectable()
export class SignupAutoService {
  private httpClient: HttpClient

  constructor(handler: HttpBackend) {
    this.httpClient = new HttpClient(handler)
  }

  signup(id: any): Observable<any> {
    return this.httpClient.post<any>(`${API_END_POINTS.USER_SIGNUP}/${id}`, {}).pipe(
      map(response => {
        return response
      }),
    )
  }
}
