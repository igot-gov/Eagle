import { Injectable } from '@angular/core'
import { Observable } from 'rxjs'
import { HttpClient } from '@angular/common/http'
import {
  IRequiredSkills,
  IRecommendedSkills,
  IAllSkills,
  IAdmin,
  ISkillQuotient,
  ICreateObj,
  IMySkills,
} from '../models/competency-model'
import { NsSkills } from '../models/skills.model'

const PROTECTED_SLAG_V8 = `/LA1/api`

const LA_API_END_POINTS = {
  REQUIRED_SKILLS: `${PROTECTED_SLAG_V8}/skills`,
  RECOMMENDED_SKILLS: `${PROTECTED_SLAG_V8}/recommendedSkills`,
  ACQUIRED_SKILLS: `${PROTECTED_SLAG_V8}/myskills`,
  IS_ADMIN: `${PROTECTED_SLAG_V8}/isAdmin`,
  IS_APPROVER: `${PROTECTED_SLAG_V8}/isApprover`,
  ALL_SKILLS: `${PROTECTED_SLAG_V8}/allSkills`,
  ROLES: `${PROTECTED_SLAG_V8}/role/get`,
  SKILL_QUOTIENT: `${PROTECTED_SLAG_V8}/skillquotient`,
  ROLE_QUOTIENT: `${PROTECTED_SLAG_V8}/rolequotient`,
  EXISTING_ROLES: `${PROTECTED_SLAG_V8}/role/getExisting`,
  CREATE_ROLE: `${PROTECTED_SLAG_V8}/role/add`,
  AUTOCOMPLETE_SKILLS: `${PROTECTED_SLAG_V8}/search`,
  SEARCH_SKILLS: `${PROTECTED_SLAG_V8}/skill/search`,
  SHARE_ROLE: `${PROTECTED_SLAG_V8}/role/shareRole`,
  EDIT_ROLE: `${PROTECTED_SLAG_V8}/role/update`,
  DELETE_ROLE: `${PROTECTED_SLAG_V8}/role/delete`,
  PROJECT_ENDORSEMENT_LIST: `${PROTECTED_SLAG_V8}/projectEndorsement/getList`,
  PROJECT_ENDORSEMENT_REQUEST: `${PROTECTED_SLAG_V8}/projectEndorsement/endorseRequest`,
  PROJECT_ENDORSEMENT_GET: `${PROTECTED_SLAG_V8}/projectEndorsement/get`,
  PROJECT_ENDORSEMENT_ADD: `${PROTECTED_SLAG_V8}/projectEndorsement/add`,

}

@Injectable({
  providedIn: 'root',
})
export class CompetencyService {
  constructor(private http: HttpClient) {}
  mySkills(): Observable<IMySkills[]> {
    return this.http.get<IMySkills[]>(`${LA_API_END_POINTS.ACQUIRED_SKILLS}`)
  }
  requiredSKills(): Observable<IRequiredSkills[]> {
    return this.http.get<IRequiredSkills[]>(`${LA_API_END_POINTS.REQUIRED_SKILLS}`)
  }
  recommendedSkills(): Observable<IRecommendedSkills[]> {
    return this.http.get<IRecommendedSkills[]>(`${LA_API_END_POINTS.RECOMMENDED_SKILLS}`)
  }
  isAdmin(): Observable<IAdmin> {
    return this.http.get<IAdmin>(`${LA_API_END_POINTS.IS_ADMIN}`)
  }
  roles(): Observable<NsSkills.IRoles[]> {
    return this.http.get<NsSkills.IRoles[]>(`${LA_API_END_POINTS.ROLES}`)
  }
  existingRoles(): Observable<NsSkills.IExistingRoles[]> {
    return this.http.get<NsSkills.IExistingRoles[]>(`${LA_API_END_POINTS.EXISTING_ROLES}`)
  }
  createRole(createObj: ICreateObj): Observable<null> {
    return this.http.post<null>(`${LA_API_END_POINTS.CREATE_ROLE}`, createObj)
  }
  skillQuotient(skillId: number): Observable<ISkillQuotient> {
    return this.http.get<ISkillQuotient>(`${LA_API_END_POINTS.SKILL_QUOTIENT}?skill_id=${skillId}`)
  }
  allSkills(
    searchText: string,
    horizon: string,
    category: string,
    pageNo: number,
  ): Observable<IAllSkills> {
    return this.http.get<IAllSkills>(
      `${LA_API_END_POINTS.ALL_SKILLS}?search_text=${searchText}&horizon=${horizon}&category=${category}&page_number=${pageNo}`,
    )
  }
}
