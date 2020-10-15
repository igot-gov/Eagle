import { Injectable } from '@angular/core'
import { HttpClient } from '@angular/common/http'
// import { NSCompetenciesData } from '../models/competencies.model'
import { ConfigurationsService, NsUser } from 'library/ws-widget/utils/src/public-api'

const API_ENDPOINTS = {
  getAllCategories: '/apis/protected/v8/discussionHub/categories',
  getSingleCategoryDetails: (cid: number) => `/apis/protected/v8/discussionHub/categories/${cid}`,
  getAllTags: '/apis/protected/v8/discussionHub/tags',
  createPost: '/apis/protected/v8/discussionHub/writeApi/v2/topics',
  votePost: (pid: number) => `apis/protected/v8/discussionHub/writeApi/v2/posts/${pid}/vote`,
  replyPost: (tid: number) => `apis/protected/v8/discussionHub/writeApi/v2/topics/${tid}`,
  bookmarkPost: (pid: number) => `apis/protected/v8/discussionHub/writeApi/v2/posts/${pid}/bookmark`,
  recentPost: '/apis/protected/v8/discussionHub/topics/recent',
  popularPost: '/apis/protected/v8/discussionHub/topics/popular',
  unread: '/apis/protected/v8/discussionHub/topics/unread/total',
  getTopic: '/apis/protected/v8/discussionHub/topics/',
  profile: '/apis/protected/v8/discussionHub/users/me',
  fetchProfile: (slug: string) => `/apis/protected/v8/discussionHub/users/${slug}/about`,
  listUpVote: (slug: string) => `/apis//protected/v8/discussionHub/users/${slug}/upvoted`,
  listDownVoted: (slug: string) => `/apis/protected/v8/discussionHub/users/${slug}/downvoted`,
  listSaved: (slug: string) => `/apis/protected/v8/discussionHub/users/${slug}/bookmarks`,
}
/* this page needs refactor*/
@Injectable({
  providedIn: 'root',
})
export class CompetenceService {
  usr: any
  constructor(
    private http: HttpClient, private configSvc: ConfigurationsService) {
    this.usr = this.configSvc.userProfile
  }

  get getUserProfile(): NsUser.IUserProfile {
    return this.usr
  }
  appendPage(page: any, url: string) {
    if (page) {
      return `${url}?page=${page}`
    }
    return url
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

  votePost(pid: number, data: any) {
    const url = API_ENDPOINTS.votePost(pid)
    return this.http.post(url, data)
  }

  deleteVotePost(pid: number) {
    const url = API_ENDPOINTS.votePost(pid)
    return this.http.delete(url)
  }

  bookmarkPost(pid: number) {
    const url = API_ENDPOINTS.bookmarkPost(pid)
    return this.http.post(url, {})
  }

  deleteBookmarkPost(pid: number) {
    const url = API_ENDPOINTS.bookmarkPost(pid)
    return this.http.delete(url)
  }

  replyPost(tid: number, data: any) {
    const url = API_ENDPOINTS.replyPost(tid)
    return this.http.post(url, data)
  }



}
