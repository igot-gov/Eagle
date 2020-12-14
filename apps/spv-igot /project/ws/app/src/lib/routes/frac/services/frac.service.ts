import { HttpClient } from '@angular/common/http'
import { Injectable } from '@angular/core'
import { ConfigurationsService } from '@ws-widget/utils'
import { IFrac } from '../interfaces/frac.model'

@Injectable({
  providedIn: 'root',
})
export class FracService {

  constructor(
    private configSvc: ConfigurationsService,
    private http: HttpClient) { }

  fetchFrac() {
    const frac: Promise<IFrac> = this.http
      .get<IFrac>(`${this.configSvc.baseUrl}/feature/frac.json`)
      .toPromise()
    return frac
  }
}
