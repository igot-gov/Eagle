import { HttpClient, HttpHeaders } from '@angular/common/http'
import { Injectable } from '@angular/core'
import { ConfigurationsService } from '@ws-widget/utils'
import { Observable } from 'rxjs'

const API_END_POINTS = {
    SEARCH_EVENT: '/apis/protected/v8/content/searchV6',
}

@Injectable({
    providedIn: 'root',
})
export class EventsService {
    httpOptions = {
        headers: new HttpHeaders({
            validator_URL: `https://${this.configSvc.hostPath}/apis/protected/v8/user/validate`,
        }),
    }
    constructor(private http: HttpClient, private configSvc: ConfigurationsService) { }

    getEvents(req: any): Observable<any> {
        return this.http.post<any>(API_END_POINTS.SEARCH_EVENT, req)
    }
}
