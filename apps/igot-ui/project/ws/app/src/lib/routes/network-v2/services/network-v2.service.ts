import { Injectable } from '@angular/core'
import { HttpClient } from '@angular/common/http'
import { NSNetworkDataV2 } from '../models/network-v2.model'

const API_ENDPOINTS = {
  getRecommendedUsers: '/apis/protected/v8/connections/connections/recommended',
  createConnection: `/apis/protected/v8/connections/add/connection`,
  updateConnection: `/apis/protected/v8/connections/update/connection`,
  connectionRequests : `/apis/protected/v8/connections/connections/requested`,
  connectionRequestsReceived : `/apis/protected/v8/connections/connections/requests/received`,
  connectionEstablished: `/apis/protected/v8/connections/connections/established`,
  getSuggestedUsers: `/apis/protected/v8/connections/connections/suggests`,
}

@Injectable({
  providedIn: 'root',
})
export class NetworkV2Service {

  constructor(
    private http: HttpClient) {
  }

  fetchAllConnectionRequests() {
    return this.http.get<NSNetworkDataV2.IConnectionRequestResponse>(API_ENDPOINTS.connectionRequests)
  }

  fetchAllReceivedConnectionRequests() {
    return this.http.get<NSNetworkDataV2.IConnectionRequest>(API_ENDPOINTS.connectionRequestsReceived)
  }

  fetchAllRecommendedUsers(data: NSNetworkDataV2.IRecommendedUserReq) {
    return this.http.post(API_ENDPOINTS.getRecommendedUsers, data)
  }

  fetchAllSuggestedUsers() {
    return this.http.get(API_ENDPOINTS.getSuggestedUsers)
  }

  createConnection(data: any) {
    return this.http.post(API_ENDPOINTS.createConnection, data)
  }

  updateConnection(data: any) {
    return this.http.post(API_ENDPOINTS.updateConnection, data)
  }

  fetchAllConnectionEstablished() {
    return this.http.get<NSNetworkDataV2.IEstablishedConnectResopnse>(API_ENDPOINTS.connectionEstablished)
  }

  fetchAllConnectionEstablishedById(wid: any) {
    const url = `${API_ENDPOINTS.connectionEstablished}/${wid}`
    return this.http.get<NSNetworkDataV2.IEstablishedConnectResopnse>(url)
  }
}
