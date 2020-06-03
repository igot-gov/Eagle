import { Injectable } from '@angular/core'
import { Observable } from 'rxjs'
import { HttpClient, HttpHeaders } from '@angular/common/http'
import {
  IRequiredSkills,
  IRecommendedSkills,
  IAllSkills,
  IAdmin,
  ISkillQuotient,
  ICreateObj,
  IMySkills,
  ISkillList,
  ICreateNewObj,
  IRoleQuotientResponse,
  ISearchAutoComplete,
  IShareObj,
  IUpdateRole,
  IAuthResponse,
  IAddSkillObj,
} from '../models/competency-model'
import { NsSkills } from '../models/skills.model'
import { ConfigurationsService } from '@ws-widget/utils'

// const PROTECTED_SLAG_V8 = `https://wingspan-staging.infosysapps.com/LA1/api`
const PROTECTED_SLAG_V8 = `/LA1/api`

const LA_API_END_POINTS = {
  REQUIRED_SKILLS: `${PROTECTED_SLAG_V8}/skills`,
  RECOMMENDED_SKILLS: `${PROTECTED_SLAG_V8}/recommendedSkills`,
  CREATED_SKILLS: `${PROTECTED_SLAG_V8}/createdSkills`,
  ACQUIRED_SKILLS: `${PROTECTED_SLAG_V8}/myskills`,
  IS_ADMIN: `${PROTECTED_SLAG_V8}/isAdmin`,
  IS_APPROVER: `${PROTECTED_SLAG_V8}/isApprover`,
  ALL_SKILLS: `${PROTECTED_SLAG_V8}/allSkills`,
  ROLES: `${PROTECTED_SLAG_V8}/v1/role/get`,
  SKILL_QUOTIENT: `${PROTECTED_SLAG_V8}/SkillQuotient`,
  ROLE_QUOTIENT: `${PROTECTED_SLAG_V8}/rolequotient`,
  EXISTING_ROLES: `${PROTECTED_SLAG_V8}/role/getExisting`,
  CREATED_ROLES: `${PROTECTED_SLAG_V8}/role/createdRoles`,
  CREATE_ROLE: `${PROTECTED_SLAG_V8}/role/add`,
  AUTOCOMPLETE_SKILLS: `${PROTECTED_SLAG_V8}/search`,
  SEARCH_SKILLS: `${PROTECTED_SLAG_V8}/skill/search`,
  SHARE_ROLE: `${PROTECTED_SLAG_V8}/role/share`,
  EDIT_ROLE: `${PROTECTED_SLAG_V8}/role/update`,
  DELETE_ROLE: `${PROTECTED_SLAG_V8}/role/delete`,
  PROJECT_ENDORSEMENT_LIST: `${PROTECTED_SLAG_V8}/projectEndorsement/getList`,
  PROJECT_ENDORSEMENT_REQUEST: `${PROTECTED_SLAG_V8}/projectEndorsement/endorseRequest`,
  PROJECT_ENDORSEMENT_GET: `${PROTECTED_SLAG_V8}/projectEndorsement/get`,
  PROJECT_ENDORSEMENT_ADD: `${PROTECTED_SLAG_V8}/projectEndorsement/add`,
  ADD_SKILL: `${PROTECTED_SLAG_V8}/skills/add`,
  CATEGORY: `${PROTECTED_SLAG_V8}/skills/filterValues`,
  ASSIGNED_ROLES: `${PROTECTED_SLAG_V8}/role/assignedRoles`,
  ADD_LEARNING_PATH: `${PROTECTED_SLAG_V8}/skill/update`,
  SHORTLISTED_SKILLS: `${PROTECTED_SLAG_V8}/skills/shortlist`,
}
@Injectable({
  providedIn: 'root',
})
export class SkillsService {
  httpOptions = {
    headers: new HttpHeaders({
      validator_URL: `https://${this.configSvc.hostPath}/apis/protected/v8/user/validate`,
    }),
  }
  constructor(private http: HttpClient, private configSvc: ConfigurationsService) {}
  mySkills(): Observable<IMySkills[]> {
    return this.http.get<IMySkills[]>(`${LA_API_END_POINTS.ACQUIRED_SKILLS}`, this.httpOptions)
  }
  shortListedSkills(skillObj: any): Observable<null> {
    return this.http.post<null>(`${LA_API_END_POINTS.SHORTLISTED_SKILLS}`, skillObj, this.httpOptions)
  }
  createdSkills(): Observable<IRequiredSkills[]> {
    return this.http.get<IRequiredSkills[]>(`${LA_API_END_POINTS.CREATED_SKILLS}`, this.httpOptions)
  }
  requiredSKills(status: string): Observable<IRequiredSkills[]> {
    return this.http.get<IRequiredSkills[]>(
      `${LA_API_END_POINTS.REQUIRED_SKILLS}?status=${status}`,
      this.httpOptions,
    )
  }
  recommendedSkills(): Observable<IRecommendedSkills[]> {
    return this.http.get<IRecommendedSkills[]>(
      `${LA_API_END_POINTS.RECOMMENDED_SKILLS}`,
      this.httpOptions,
    )
  }
  isAdmin(): Observable<IAdmin> {
    return this.http.get<IAdmin>(`${LA_API_END_POINTS.IS_ADMIN}`, this.httpOptions)
  }
  categoryList(type: string): Observable<null> {
    return this.http.get<null>(`${LA_API_END_POINTS.CATEGORY}?type=${type}`, this.httpOptions)
  }
  isApprover(): Observable<IAuthResponse> {
    return this.http.get<IAuthResponse>(`${LA_API_END_POINTS.IS_APPROVER}`, this.httpOptions)
  }
  createdRoles(): Observable<NsSkills.IRoles[]> {
    return this.http.get<NsSkills.IRoles[]>(`${LA_API_END_POINTS.CREATED_ROLES}`, this.httpOptions)
  }
  assignedRoles(): Observable<NsSkills.IRoles[]> {
    return this.http.get<NsSkills.IRoles[]>(`${LA_API_END_POINTS.ASSIGNED_ROLES}`, this.httpOptions)
  }
  roles(): Observable<NsSkills.IRoles[]> {
    return this.http.get<NsSkills.IRoles[]>(`${LA_API_END_POINTS.ROLES}`, this.httpOptions)
  }
  existingRoles(skillGroup: string): Observable<NsSkills.IExistingRoles[]> {
    return this.http.get<NsSkills.IExistingRoles[]>(
      `${LA_API_END_POINTS.EXISTING_ROLES}?skill_group=${skillGroup}`,
      this.httpOptions,
    )
  }
  getProjectEndorsementRequest(): Observable<null> {
    return this.http.get<null>(`${LA_API_END_POINTS.PROJECT_ENDORSEMENT_GET}`, this.httpOptions)
  }
  deleteRole(roleId: string): Observable<null> {
    return this.http.get<null>(
      `${LA_API_END_POINTS.DELETE_ROLE}?role_id=${roleId}`,
      this.httpOptions,
    )
  }
  endorsementRequest(approverObj: any): Observable<null> {
    // tslint:disable-next-line:max-line-length
    return this.http.post<null>(
      `${LA_API_END_POINTS.PROJECT_ENDORSEMENT_REQUEST}?endorse_id=${approverObj.endorse_id}`,
      approverObj,
      this.httpOptions,
    )
  }
  endorsementList(type: string): Observable<null> {
    return this.http.get<null>(
      `${LA_API_END_POINTS.PROJECT_ENDORSEMENT_LIST}?request_type=${type}`,
      this.httpOptions,
    )
  }
  addEndorsement(endorseObj: any): Observable<null> {
    return this.http.post<null>(
      `${LA_API_END_POINTS.PROJECT_ENDORSEMENT_ADD}`,
      endorseObj,
      this.httpOptions,
    )
  }
  createRole(createObj: ICreateObj | ICreateNewObj): Observable<null> {
    return this.http.post<null>(`${LA_API_END_POINTS.CREATE_ROLE}`, createObj, this.httpOptions)
  }
  addSkill(addSkillObj: IAddSkillObj): Observable<null> {
    return this.http.post<null>(`${LA_API_END_POINTS.ADD_SKILL}`, addSkillObj, this.httpOptions)
  }
  updateRole(updateObj: IUpdateRole): Observable<null> {
    return this.http.post<null>(`${LA_API_END_POINTS.EDIT_ROLE}`, updateObj, this.httpOptions)
  }
  shareRole(shareObj: IShareObj): Observable<null> {
    return this.http.post<null>(`${LA_API_END_POINTS.SHARE_ROLE}`, shareObj, this.httpOptions)
  }
  addLearningPath(learningPathObj: any): Observable<null> {
    return this.http.post<null>(
      `${LA_API_END_POINTS.ADD_LEARNING_PATH}`,
      learningPathObj,
      this.httpOptions,
    )
  }
  skillQuotient(skillId: string): Observable<ISkillQuotient> {
    return this.http.get<ISkillQuotient>(
      `${LA_API_END_POINTS.SKILL_QUOTIENT}?skill_id=${skillId}`,
      this.httpOptions,
    )
  }
  roleQuotient(roleId: string): Observable<IRoleQuotientResponse> {
    return this.http.get<IRoleQuotientResponse>(
      `${LA_API_END_POINTS.ROLE_QUOTIENT}?role_id=${roleId}`,
      this.httpOptions,
    )
  }
  allSkills(
    searchText: string,
    key1: string,
    key2: string,
    value1: string,
    value2: string,
    pageNumber: number,
  ): Observable<IAllSkills> {
    return this.http.get<IAllSkills>(
      // tslint:disable-next-line:max-line-length
      `${LA_API_END_POINTS.ALL_SKILLS}?search_text=${encodeURIComponent(
        searchText,
      )}&${key1}=${encodeURIComponent(value1)}&${key2}=${encodeURIComponent(
        value2,
      )}&page_number=${pageNumber}`,
      this.httpOptions,
    )
  }
  autoCompleteSkills(
    searchText: string,
    type: string,
  ): Observable<ISkillList[] | ISearchAutoComplete[]> {
    // tslint:disable-next-line:max-line-length
    return this.http.get<ISkillList[] | ISearchAutoComplete[]>(
      `${LA_API_END_POINTS.AUTOCOMPLETE_SKILLS}?search_text=${searchText}&type=${type}`,
      this.httpOptions,
    )
  }
  autoCompleteSearchSkills(searchText: string): Observable<ISkillList[]> {
    return this.http.get<ISkillList[]>(
      `${LA_API_END_POINTS.SEARCH_SKILLS}?search_text=${searchText}`,
      this.httpOptions,
    )
  }
}
