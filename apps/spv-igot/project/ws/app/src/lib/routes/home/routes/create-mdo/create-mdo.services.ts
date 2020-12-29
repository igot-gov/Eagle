import { Injectable } from '@angular/core'
import { HttpClient } from '@angular/common/http'
import { Observable } from 'rxjs'

const API_END_POINTS = {
  GET_ALL_DEPARTMENTS: '/apis/protected/v8/portal/departmentType/',
  CREATE_DEPARTMENT: '/apis/protected/v8/portal/spv/department',
}

const DEPARTMENT_NAME = 'igot'

@Injectable({
  providedIn: 'root',
})
export class CreateMDOService {
  constructor(private http: HttpClient) { }
  getAllSubDepartments(deptName: string): Observable<any> {
    return this.http.get<any>(`${API_END_POINTS.GET_ALL_DEPARTMENTS}${deptName}`)
  }
  createDepartment(deptData: any): Observable<any> {
    const departmentData = {
      rootOrg: DEPARTMENT_NAME,
      deptName: deptData.name,
      deptTypeId: deptData.deptSubTypeId,
      description: '',
      headquarters: '',
      logo: deptData.fileUpload,
    }
    return this.http.post<any>(`${API_END_POINTS.CREATE_DEPARTMENT}`, departmentData)
  }
}
