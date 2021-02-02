import { HttpClient, HttpHeaders } from '@angular/common/http'
import { Injectable } from '@angular/core'
import { ConfigurationsService } from '@ws-widget/utils'
import { Observable } from 'rxjs'

const API_END_POINTS = {
    SEARCH_EVENT: '/apis/protected/v8/content/searchV6',
    JOIN_A_MEETING: '/apis/protected/v8/user/realTimeProgress/update',
    GET_PARTICIPANTS: 'apis/protected/v8/cohorts/activeusers',
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

    joinMeeting(req: any, identifier: any) {
        return this.http.post<any>(`${API_END_POINTS.JOIN_A_MEETING}/${identifier}`, req)
    }

    getParticipants(identifier: any) {
        return this.http.get<any>(`${API_END_POINTS.GET_PARTICIPANTS}/${identifier}`)
    }
}
