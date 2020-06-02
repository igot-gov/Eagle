export interface IGroupMember {
  lp_linked_profile_id: number
  lp_groupmember_id: number
  lp_linked_id: number
}

export interface IGroup {
  lp_groupid: string
  lp_groupdesc: string
  lp_groupname: string
  group_member: IGroupMember[]
  lp_groupimage: string
  certification_mandatory: boolean
}

export interface IVariant {
  variant_id: string
  variant_image: string
  variant_name: string
  variant_description: string
  group: IGroup[]
}

export interface IRole {
  role_name: string
  role_id: string
  role_image: string
  variants: IVariant[]
  roles_defined: boolean
  courses_available: boolean
  role_description: string
}

export interface INsoData {
  arm_id: number
  roles: IRole[]
  arm_name: string
}

export interface ICourse {
  course_id: number
  course_name: string
  course_classroom_link: string
  course_lex_link: string
  course_time: number
  course_image: string
  course_description: string
}

export interface IProfile {
  profile_time: number
  profile_name: string
  courses: ICourse[]
  profile_image: string
  profile_id: number
  attached: boolean
  courses_list: string[]
  profile_display_name: string
  technology: string[]
  profile_description: string
}

export interface ILpPlayground {
  playground_name: string
  playground_id: number
  playground_description: string
  playground_link: string
}

export interface ILpInternalCertification {
  virtual_proctoring: boolean
  virtual_proctoring_id: string
  lp_internal_certification_link: string
  alternate_parent_id?: string[]
  lp_internal_certification_name: string
  lp_internal_certification_description: string
  is_alternate: boolean
  lp_internal_certification_id: string
}

export interface IExternalCertification {
  virtual_proctoring: boolean
  virtual_proctoring_id?: string
  alternate_parent_id: string[]
  lp_external_certification_name: string
  lp_external_certification_id: string
  lp_external_certification_link: string
  is_alternate: boolean
  lp_external_certification_description: string
}

export interface ILpData {
  lp_recommendation: string
  lp_description: string
  lp_external_certification: IExternalCertification[]
  profiles: IProfile[]
  lp_playground: ILpPlayground[]
  lp_name: string
  lp_alternate: number[]
  lp_image: string
  linked_program: string
  lp_id: number
  capstone_description: string
  lp_capstone: string
  lp_internal_certification: ILpInternalCertification[]
}

export interface IOfferings {
  Accelerate: INsoData
  Innovate: INsoData
  Insight: INsoData
  Experience: INsoData
  Assure: INsoData
}

export interface IFsData {
  fs_id: number
  fs_name: string
  fs_desc: string
  fs_image: string
  fs_linked_program: string
  fs_internal_certification: IFsInternalCertification[]
  fs_playground: IFsPlayground[]
  fs_external_certification: IFsExternalCertification[]
  fs_course: IFsCourse[]
}

export interface IFsPlayground {
  playground_name: string
  playground_id: number
  playground_desc: string
  playground_link: string
  playground_image: string
}

export interface IFsInternalCertification {
  internal_certification_id: string
  internal_certification_name: string
  internal_certification_desc: string
  internal_certification_link: string
  internal_certification_image: string
}

export interface IFsExternalCertification {
  external_certification_id: number
  external_certification_name: string
  external_certification_desc: string
  external_certification_image: string
  external_certification_link: string
}

export interface IFsCourse {
  course_id: number
  course_name: string
  course_desc: string
  course_lex_link: string
  course_image: string
  course_time: number
}
