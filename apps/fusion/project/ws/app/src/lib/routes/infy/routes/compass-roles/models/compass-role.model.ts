export interface ICompassRolesResponse {
  role_name: string
  skills: ISkillsCompassResponse[]
  role_desc: string
}

export interface ISkillsCompassResponse {
  role_desc: string
  isCertificationMandatory: number
  skill_desc: string
  is_skill_mandatory: number
  is_course_mandatory: number
  image_url: string
  available_program_list: IProgramList[]
  skill_group_id: string
  skill_group_name: string,
}

export interface IProgramList {
  lex_id: string
  content_name: string
  learning_path_desc: string
  available_courses: IAvailableCourses[]
  available_certifications: IAvailableCertification[],
}

export interface IAvailableCourses {
  linked_program: string
  content_name: string
  res_type: string
  learning_type: string
  skill_desc: string
  image_url: string
  skill_name: string
  skill_id: string
  linked_program_name: string
  lex_course_id: string
  learning_path_desc: string,
}

export interface IAvailableCertification {
  linked_program: string
  content_name: string
  res_type: string
  learning_type: string
  skill_desc: string
  image_url: string
  skill_name: string
  skill_id: string
  linked_program_name: string
  lex_course_id: string
  learning_path_desc: string,
}
