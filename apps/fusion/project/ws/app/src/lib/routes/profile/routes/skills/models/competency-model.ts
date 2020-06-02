import { IWidgetGraphData } from '@ws-widget/collection'
export interface IAdmin {
  is_admin: boolean
}
export interface IApprover {
  skill_group: string
  role: string
  role_name: string
}

export interface ICourseObj {
  sequence_id: number
  course_type: string
  content_id: string
  content_name: string
}
export interface IAuthResponse {
  roles: IApprover[]
}
export interface IWidgetData {
  widgetData: {
    strips: {
      key: string
      title: string
      request: {
        ids: string[]
      }
    }[]
    loader: boolean
  }
  widgetSubType: string
  widgetType: string
  widgetHostClass: string
}
export interface IEndorsementObj {
  skill_name: string
  skill_id: string
  skill_level: string
  skill_level_id: string
  approver_id: string
  message: string
}

export interface IEndorsementCreateObj {
  skill_name: string
  skill_id: string
  project_code: string
  approver_id: string
  message: string
}
export interface IRecommendedSkills {
  category?: string
  certification_count?: number | null
  course_count?: number
  criticality?: string
  horizon?: string
  skill_group?: string
  skill_level?: string
  image_url: string
  is_deleted: null
  is_grouped: null
  is_searchable: number
  rank_number: number
  skill_id: number
  skill_name: string
  status?: string
  skill_category?: string
}
export interface IAddSkillObj {
  skill_name: string
  skill_description: string
  skill_id?: string
  skill_level_description_Starter: string
  skill_level_description_Basic: string
  skill_level_description_Advanced: string
  skill_level_description_Expert: string
  skill_group: string
  skill_category: string
  skill_manager: string
  skill_relevance: string
}
export interface IMySkills {
  cert_comp_per: null
  certification_comp: null
  certification_count?: number | null
  course_comp: number
  course_comp_per: number
  course_count?: number
  date_certification_comp: null
  date_certification_start: null
  date_course_comp: string
  date_course_start: string
  email_id: string
  image_url: string
  learning_path_id: string
  learning_path_name: string
  seq: number
  skill_certification_comp: null
  skill_course_comp: number
  skill_group_id: string
  skill_id: string
  skill_name: string
  horizon?: string
  category?: string
  level?: string
  recommended_by?: string
  status?: string
}
export interface IRequiredSkills {
  cert_comp_per: null
  certification_comp: null
  certification_count?: number | null
  course_comp: number
  course_comp_per: number
  course_count?: number
  date_certification_comp: null
  date_certification_start: null
  date_course_comp: string
  date_course_start: string
  email_id: string
  image_url: string
  learning_path_id: string
  learning_path_name: string
  seq: number
  skill_certification_comp: null
  skill_course_comp: number
  skill_group_id: string
  skill_id: string
  skill_name: string
  horizon?: string
  category?: string
  skill_group?: string
  skill_level?: string
  status?: string
}
export interface IAllSkills {
  category_wise_skill_count: ICount[]
  horizon_wise_skill_count: ICount[]
  is_pagination: boolean
  skill_list: ISkillList[]
  total_skills: number
  status?: string
}

export interface ISelectedEmailId {
  skill_id: number
  skill_name: string
}
export interface ISkillQuotient {
  assessment: IAssessment[]
  available_certification: IAvailableCourseCertification[]
  available_certification_org_data: { [key: string]: ICount[] }
  available_course: IAvailableCourseCertification[]
  prerequisites?: IRequiredSkills[]
  available_course_org_data: { [key: string]: IOrgData[] }
  default_quotient: number
  org_wide_stats: IOrgData[]
  skill_details: ISkillList[]
  skill_quotient: ISkillQuotientDetails
  total_user_count: number
  type: string
}
export interface IAssessment {
  assessment_date: string
  assessment_score: number
  certification_result: null
  content_name: string
  content_type: null
  email_id: string
  lex_id: string
  max_score: number
  min_score: number
  percentile: number
  quotient_type: string
  skill_id: number
  type: string
  scoreRange?: IRangeObj
  orgWideGraph?: IGraphWidget
}

export interface IGraphWidget {
  widgetType: string
  widgetSubType: string
  widgetData: IWidgetGraphData
}

export interface IRangeObj {
  name: string
  data: IDataObj[]
}

export interface ICount {
  doc_count: number
  key: string
}
export interface ISkillList {
  category: string
  certification_count: number
  course_count: number
  criticality: string
  horizon: string
  skill_level?: string
  skill_level_id?: string
  image_url: string
  is_deleted: null
  is_grouped: null
  is_searchable: number
  rank_number: number
  skill_id: number
  skill_name: string
  skill_type: null
  skill_group?: string
  status?: string
}

export interface IDataObj {
  y: number
}
export interface IAvailableCourseCertification {
  content_name: string
  learning_path_id: string
  learning_path_name: string
  learning_type: string
  lex_id: string
  res_id_list: string
  res_type: string
  role_id: string
  role_name: string
  skill_id: string
  skill_name: string
  material_url: string
}
export interface IOrgData {
  doc_count: number
  from: number
  key: string
  to: number
}
export interface ISkillQuotientDetails {
  cert_comp_per: null
  certification_comp: number
  certification_count: null
  course_comp: number
  course_comp_per: number
  course_count: number
  date_certification_comp: null
  date_certification_start: null
  date_course_comp: string
  date_course_start: string
  email_id: string
  image_url: string
  learning_path_id: string
  learning_path_name: string
  seq: number
  skill_certification_comp: number
  skill_course_comp: number
  skill_group_id: string
  skill_id: string
  skill_name: string
  total_skill_quotient: number
}

export interface IExistingSkills {
  skill_id: string
  skill_name: string
}

export interface ISearchAutoComplete {
  emailId: string
  employeeId: string
  jl: string
  puCode: string
}
export interface IRoleQuotientResponse {
  assessment_quotient: number
  certification_quotient: number
  certification_comp: number
  course_comp: number
  default_quotient: number
  image_url: string
  role_quotient_details: IRoleDetails
  total_quotient: number
  type: string
}
export interface IRoleDetails {
  image_url: string
  role_id: string
  skills: ISkillsData[]
  role_name: string
}
export interface ISkillsData {
  assessment: any
  available_assessment: any
  available_certification: ICourseData[]
  available_certification_org_data: ICount[]
  available_course: ICourseData[]
  available_course_org_data: ICount[]
  certification: any
  skill_id: number
  skill_name: string
}
export interface ICourseData {
  content_name: string
  course_group_id: string
  lex_id: string
  material_url: string
  skill_id: number
  skill_name: string
  type: string
}
export interface ICreateObj {
  role_id: string
  type: string
}
export interface ICreateNewObj {
  role_name: string
  skill_id: any
  skill_level_id?: any
  type: string
  role_headline?: string
  job_family?: string
  sub_job_family?: string
  position_type?: string
  skill_group?: string
  role_responsibilities?: string
}

export interface IUpdateRole {
  role_name: string
  role_headline?: string
  job_family?: string
  sub_job_family?: string
  position_type?: string
  role_responsibilities?: string
  skill_id: any
  skill_level_id?: any
  role_id?: any
  skill_group?: any
}
export interface IShareObj {
  role_name: string
  role_id: string
  wid: string[]
  message: string
}
export interface ICertificationList {
  assessment_date: Date
  content_name: string
  lex_id: string
  max_score: number
  min_score: number
  percentile: number
  type: string
}
export interface IGraphDataCourse {
  name: string
  id: string
  materialUrl: string
  totalUsers: number
  legend: boolean
  data: IBarGraphData[]
}
export interface IBarGraphData {
  y: number
}
