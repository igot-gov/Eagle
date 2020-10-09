import { Injectable } from '@angular/core'
import { HttpClient } from '@angular/common/http'

import { Observable } from 'rxjs'
import { UserAutocompleteService } from 'library/ws-widget/collection/src/public-api'
// import { NsDiscussionForum } from '../../../../../../../../../library/ws-widget/collection/src/public-api'

const PROTECTED_SLAG_V8 = '/apis/protected/v8'

const API_END_POINTS = {
  DISCUSS_PROFILE: '/apis/protected/v8/discussionHub/users',
  PROFILE_DETAIL: `${PROTECTED_SLAG_V8}/social/post/timeline`,
  SOCIAL_VIEW_CONVERSATION: `${PROTECTED_SLAG_V8}/social/post/viewConversation`,
}

@Injectable({
  providedIn: 'root',
})
export class ProfileV2Service {
  constructor(private http: HttpClient, private fetchUser: UserAutocompleteService
  ) { }
  fetchDiscussProfile(wid: string): Observable<any> {
    return this.http.get<any>(`${API_END_POINTS.DISCUSS_PROFILE}/${wid}`)
  }
  fetchProfile(email: string): Observable<any> {
    return this.fetchUser.fetchAutoComplete(email)
  }
  fetchPost(request: any): Observable<any> {
    return this.http.post<any>(API_END_POINTS.SOCIAL_VIEW_CONVERSATION, request)
  }
  // fetchAllPosts(request: anyV2): Observable<anyV2> {
  //   return this.http.post<NsDiscussionForum.IPostResultV2>(API_END_POINTS.SOCIAL_VIEW_CONVERSATION_V2, request)
  // }
}
