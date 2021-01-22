import { Injectable } from '@angular/core'
import { HttpClient } from '@angular/common/http'
import { Observable } from 'rxjs'
import {
  IUserProfileDetails,
  ILanguagesApiData,
  INationalityApiData,
  IUserProfileDetailsFromRegistry,
  IProfileMetaApiData,
  INameField,
} from '../models/user-profile.model'

const API_ENDPOINTS = {
  updateProfileDetails: '/apis/protected/v8/user/profileDetails/createUserRegistry',
  getUserdetailsFromRegistry: '/apis/protected/v8/user/profileDetails/getUserRegistry',
  getUserdetails: '/apis/protected/v8/user/details/detailV1',
  getMasterNationlity: '/apis/protected/v8/user/profileDetails/getMasterNationalities',
  getMasterLanguages: '/apis/protected/v8/user/profileDetails/getMasterLanguages',
  getProfilePageMeta: '/apis/protected/v8/user/profileDetails/getProfilePageMeta',
  approveRequest: '/apis/protected/v8/workflowhandler/transition',
  getNewDepartments: '/apis/protected/v8/portal/listDeptNames',
  getPendingFields: '/apis/protected/v8/workflowhandler/userWFApplicationFieldsSearch',
}

@Injectable()
export class UserProfileService {
  constructor(
    private http: HttpClient,
  ) {
  }
  updateProfileDetails(data: any) {
    return this.http.post(API_ENDPOINTS.updateProfileDetails, data)
  }
  getUserdetails(email: string | undefined): Observable<[IUserProfileDetails]> {
    return this.http.post<[IUserProfileDetails]>(API_ENDPOINTS.getUserdetails, { email })
  }
  getMasterLanguages(): Observable<ILanguagesApiData> {
    return this.http.get<ILanguagesApiData>(API_ENDPOINTS.getMasterLanguages)
  }
  getMasterNationlity(): Observable<INationalityApiData> {
    return this.http.get<INationalityApiData>(API_ENDPOINTS.getMasterNationlity)
  }
  getProfilePageMeta(): Observable<IProfileMetaApiData> {
    return this.http.get<IProfileMetaApiData>(API_ENDPOINTS.getProfilePageMeta)
  }
  getNewDepartments(): Observable<INameField[]> {
    return this.http.get<INameField[]>(API_ENDPOINTS.getNewDepartments)
  }
  getUserdetailsFromRegistry(): Observable<[IUserProfileDetailsFromRegistry]> {
    return this.http.get<[IUserProfileDetailsFromRegistry]>(API_ENDPOINTS.getUserdetailsFromRegistry)
  }
  approveRequest(data: any) {
    return this.http.post(API_ENDPOINTS.approveRequest, data)
  }
  listApprovalPendingFields() {
    return this.http.post<any>(API_ENDPOINTS.getPendingFields, {
      serviceName: 'profile',
      applicationStatus: 'SEND_FOR_APPROVAL'
    })
  }
}
