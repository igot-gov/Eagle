import { Injectable } from '@angular/core'
import { HttpClient } from '@angular/common/http'
import { NSDiscussData } from '../models/discuss.model'
import { ConfigurationsService } from 'library/ws-widget/utils/src/public-api'

const API_ENDPOINTS = {
  getAllCategories: '/apis/protected/v8/discussionHub/categories',
  getAllTags: '/apis/protected/v8/discussionHub/tags',
  createPost: '/apis/protected/v8/discussionHub/writeApi/v2/topics',
  votePost: (pid: number) => `apis/protected/v8/discussionHub/writeApi/v2/posts/${pid}/vote`,
  replyPost: (tid: number) => `apis/protected/v8/discussionHub/writeApi/v2/topics/${tid}`,
  recentPost: '/apis/protected/v8/discussionHub/topics/recent',
  popularPost: '/apis/protected/v8/discussionHub/topics/popular',
  unread: '/apis/protected/v8/discussionHub/topics/unread',
  getTopic: '/apis/protected/v8/discussionHub/topics/',
  profile: '/apis/protected/v8/discussionHub/users/me',
  listUpVote: (slug: string) => `/apis//protected/v8/discussionHub/users/${slug}/upvoted`,
  listDownVoted: (slug: string) => `/apis/protected/v8/discussionHub/users/${slug}/downvoted`,
  listSaved: (slug: string) => `/apis/protected/v8/discussionHub/users/${slug}/bookmarks`,
}
/* this page needs refactor*/
@Injectable({
  providedIn: 'root',
})
export class DiscussService {
  usr: any
  constructor(
    private http: HttpClient, private configSvc: ConfigurationsService) {
    this.usr = this.configSvc.userProfile
  }

  fetchAllCategories() {
    const categories = this.http.get(API_ENDPOINTS.getAllCategories)
      .toPromise()
    return categories
  }

  fetchAllTags() {
    const tags = this.http.get(API_ENDPOINTS.getAllTags)
      .toPromise()
    return tags
  }

  createPost(data: any) {
    return this.http.post(API_ENDPOINTS.createPost, data)
  }

  fetchAllCategorie() {
    return this.http.get<NSDiscussData.ICategorie[]>(API_ENDPOINTS.getAllCategories)
  }
  fetchAllTag() {
    return this.http.get<NSDiscussData.ITag[]>(API_ENDPOINTS.getAllTags)
  }

  fetchPostDetails() {
    return this.http.get<NSDiscussData.ITag[]>(API_ENDPOINTS.getAllTags)
  }

  votePost(pid: number, data: any) {
    const url = API_ENDPOINTS.votePost(pid)
    return this.http.post(url, data)
  }

  replyPost(tid: number, data: any) {
    const url = API_ENDPOINTS.replyPost(tid)
    return this.http.post(url, data)
  }

  fetchRecentD() {
    return this.http.get<NSDiscussData.IDiscussionData[]>(API_ENDPOINTS.recentPost)
  }
  fetchPopularD() {
    return this.http.get<NSDiscussData.IDiscussionData>(API_ENDPOINTS.popularPost)
  }
  fetchTopicById(topicId: number) {
    return this.http.get<NSDiscussData.IDiscussionData>(API_ENDPOINTS.getTopic + topicId.toString())
  }
  fetchNotifications() {
    return this.http.get<any>(API_ENDPOINTS.unread)
  }
  fetchProfile() {
    return this.http.get<NSDiscussData.IProfile>(API_ENDPOINTS.profile)
  }
  fetchUpvoted() {
    return this.http.get<NSDiscussData.IProfile>(API_ENDPOINTS.listUpVote(this.usr.userId))
  }
  fetchDownvoted() {
    return this.http.get<NSDiscussData.IProfile>(API_ENDPOINTS.listDownVoted(this.usr.userId))
  }
  fetchSaved() {
    return this.http.get<NSDiscussData.IProfile>(API_ENDPOINTS.listSaved(this.usr.userId))
  }
}
