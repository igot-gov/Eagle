import { Injectable } from '@angular/core'
import { HttpClient } from '@angular/common/http'

const API_ENDPOINTS = {
  getAllCategories: '/apis/protected/v8/discussionHub/categories',
  getAllTags: '/apis/protected/v8/discussionHub/tags',
  createPost: '/apis/protected/v8/discussionHub/writeApi/v2/topics',
}

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
}
