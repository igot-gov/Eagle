import { Injectable } from '@angular/core'
import { HttpClient } from '@angular/common/http'
import { NSDiscussData } from '../models/discuss.model'
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
  fetchNetworkProfile: '/apis/protected/v8/user/profileDetails/getUserRegistry',
  // Above line is to fetch own details only for loged in user.
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

  get getUserProfile(): NsUser.IUserProfile {
    return this.usr
  }
  appendPage(page: any, url: string) {
    if (page) {
      return `${url}?page=${page}`
    }
    return `${url}?page=1`
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

  fetchRecentD(page?: any) {
    const url = this.appendPage(page, API_ENDPOINTS.recentPost)
    return this.http.get<NSDiscussData.IDiscussionData>(url)
  }
  fetchPopularD(page?: any) {
    const url = this.appendPage(page, API_ENDPOINTS.popularPost)
    return this.http.get<NSDiscussData.IDiscussionData>(url)
  }

  fetchTopicById(topicId: number, page?: any) {
    let url = API_ENDPOINTS.getTopic + topicId.toString()
    url = this.appendPage(page, url)
    return this.http.get<NSDiscussData.IDiscussionData>(url)
  }

  fetchTopicByIdSort(topicId: number, sort: any, page?: any) {
    let url = API_ENDPOINTS.getTopic + topicId.toString()
    url = this.appendPage(page, url)
    return this.http.get<NSDiscussData.IDiscussionData>(`${url}&sort=${sort}`)
  }

  fetchUnreadCOunt() {
    return this.http.get<any>(API_ENDPOINTS.unread)
  }
  fetchProfile() {
    return this.http.get<NSDiscussData.IProfile>(API_ENDPOINTS.profile)
  }
  fetchProfileInfo(slug: string) {
    return this.http.get<NSDiscussData.IProfile>(API_ENDPOINTS.fetchProfile(slug))
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
  fetchSingleCategoryDetails(cid: number, page?: any) {
    const url = this.appendPage(page, API_ENDPOINTS.getSingleCategoryDetails(cid))
    return this.http.get<NSDiscussData.ICategoryData>(url)
  }
  fetchSingleCategoryDetailsSort(cid: number, sort: any, page?: any) {
    const url = this.appendPage(page, API_ENDPOINTS.getSingleCategoryDetails(cid))
    return this.http.get<NSDiscussData.ICategoryData>(`${url}&sort=${sort}`)
  }
  fetchNetworkProfile() {
    return this.http.get<any>(API_ENDPOINTS.fetchNetworkProfile)
  }

}
