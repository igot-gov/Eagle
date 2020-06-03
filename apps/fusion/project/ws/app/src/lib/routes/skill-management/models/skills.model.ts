export namespace NsSkills {
  export interface IRecommendedSkill {
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
    user_level?: string
    progress?: string
    recommended_by?: string
    status?: string
    skill_description?: string
    skill_manager?: string
    skill_relevance?: string
    skill_category?: string
    skill_level_id?: string
  }
  export interface ISkill {
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
    skill_group?: string
    user_level?: string
    category?: string
    progress?: string
    skill_level?: string
    recommended_by?: string
    status?: string
    skill_description?: string
    skill_manager?: string
    skill_relevance?: string
    skill_category?: string
    skill_level_id?: string
  }

  export interface IRoles {
    image_url: string
    role_id: string
    role_name: string
    skill_id: string[]
    type: string
  }

  export interface IExistingRoles {
    image_url: string
    role_id: string
    role_name: string
    role_type: string
    description?: string
    skills: IExistingSkills[]
    skill_id?: string[]
    type: string
    created_on?: string
  }

  export interface IExistingSkills {
    skill_id: string
    skill_name: string
  }
}
