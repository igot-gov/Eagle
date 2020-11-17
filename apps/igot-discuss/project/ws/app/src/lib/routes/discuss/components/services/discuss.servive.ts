import { Injectable } from '@angular/core'
import { HttpClient } from '@angular/common/http'

import { Observable } from 'rxjs'
import { NsDiscussionForum } from '../../../../../../../../../library/ws-widget/collection/src/public-api'

const PROTECTED_SLAG_V8 = '/apis/protected/v8'

const API_END_POINTS = {
  // SOCIAL_TIMELINE: `${PROTECTED_SLAG_V8}/social/post/timelineV2`, // this has to be changed(Temporary)
  SOCIAL_TIMELINE: `${PROTECTED_SLAG_V8}/social/post/timeline`,
  SOCIAL_VIEW_CONVERSATION: `${PROTECTED_SLAG_V8}/social/post/viewConversation`,
  SOCIAL_VIEW_CONVERSATION_V2: `${PROTECTED_SLAG_V8}/social/post/viewConversationV2`,
  SOCIAL_POST_PUBLISH: `${PROTECTED_SLAG_V8}/social/post/publish`,
  SOCIAL_POST_DELETE: `${PROTECTED_SLAG_V8}/social/post/delete`,
  SOCIAL_POST_ACTIVITY_UPDATE: `${PROTECTED_SLAG_V8}/social/post/activity/create`,
  SOCIAL_POST_ACTIVITY_USERS: `${PROTECTED_SLAG_V8}/social/post/activity/users`,
  SOCIAL_POST_UPDATE: `${PROTECTED_SLAG_V8}/social/edit/meta`,
}

@Injectable({
  providedIn: 'root',
})
export class DiscussService {
  constructor(private http: HttpClient) { }

  deletePost(postId: string, userId: string) {
    const req: NsDiscussionForum.IPostDeleteRequest = {
      userId,
      id: postId,
    }
    return this.http.post(API_END_POINTS.SOCIAL_POST_DELETE, req)
  }
  updateActivity(
    request: NsDiscussionForum.IPostActivityUpdateRequest | NsDiscussionForum.IPostFlagActivityUpdateRequest,
  ) {
    return this.http.post<any>(API_END_POINTS.SOCIAL_POST_ACTIVITY_UPDATE, request)
  }
  fetchActivityUsers(request: NsDiscussionForum.IActivityUsers): Observable<NsDiscussionForum.IActivityUsersResult> {
    return this.http.post<NsDiscussionForum.IActivityUsersResult>(API_END_POINTS.SOCIAL_POST_ACTIVITY_USERS, request)
  }
  fetchTimelineData(request: NsDiscussionForum.ITimelineRequest): Observable<NsDiscussionForum.ITimeline> {
    return this.http.post<NsDiscussionForum.ITimeline>(API_END_POINTS.SOCIAL_TIMELINE, request)
  }
  publishPost(request: NsDiscussionForum.IPostPublishRequest | NsDiscussionForum.IPostCommentRequest) {
    return this.http.post<any>(API_END_POINTS.SOCIAL_POST_PUBLISH, request)
  }
  updatePost(request: NsDiscussionForum.IPostUpdateRequest) {
    return this.http.put<any>(API_END_POINTS.SOCIAL_POST_UPDATE, request)
  }
  fetchPost(request: NsDiscussionForum.IPostRequest): Observable<NsDiscussionForum.IPostResult> {
    return this.http.post<NsDiscussionForum.IPostResult>(API_END_POINTS.SOCIAL_VIEW_CONVERSATION, request)
  }
  fetchAllPosts(request: NsDiscussionForum.IPostRequestV2): Observable<NsDiscussionForum.IPostResultV2> {
    return this.http.post<NsDiscussionForum.IPostResultV2>(API_END_POINTS.SOCIAL_VIEW_CONVERSATION_V2, request)
  }
}
