import { Injectable } from '@angular/core'
import { HttpClient } from '@angular/common/http'
import { NSDiscussData } from '../models/discuss.model'

const API_ENDPOINTS = {
  getAllCategories: '/apis/protected/v8/discussionHub/categories',
  getAllTags: '/apis/protected/v8/discussionHub/tags',
  createPost: '/apis/protected/v8/discussionHub/writeApi/v2/topics',
  recentPost: '/apis/protected/v8/discussionHub/topics/recent',
  popularPost: '/apis/protected/v8/discussionHub/topics/popular',
  unread: '/apis/protected/v8/discussionHub/topics/unread',
  getTopic: '/apis/protected/v8/discussionHub/topics/',

}
/* this page needs refactor*/
@Injectable({
  providedIn: 'root',
})
export class DiscussService {

  constructor(
    private http: HttpClient) { }

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
  fetchRecentD() {
    return this.http.get<NSDiscussData.IDiscussionData[]>(API_ENDPOINTS.recentPost)
  }
  fetchPopularD() {
    return this.http.get<NSDiscussData.IDiscussionData[]>(API_ENDPOINTS.popularPost)
  }
  fetchTopicById(topicId: number) {
    return this.http.get<NSDiscussData.IDiscussionData[]>(API_ENDPOINTS.getTopic + topicId.toString())
  }
  fetchNotifications() {
    return this.http.get<any>(API_ENDPOINTS.unread)
  }
}
