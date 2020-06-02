export interface IMyAnalytics {
  top_content_jl: IMyAnalyticsTopContent[]
  top_content_unit: IMyAnalyticsTopContent[]
  learning_history: IMyAnalyticsLearningHistory[]
  learning_history_progress_range: IMyAnalyticsLearningHistoryProgressRange
  goal_progress: IMyAnalyticsGoalProgress[]
}

export interface IMyAnalyticsTopContent {
  lex_id: string
  count: number
  content_name: string
  progress_range: Array<{
    key: number;
    doc_count: number;
  }>
}

export interface IMyAnalyticsLearningHistory {
  lex_id: string
  content_name: string
  progress: number
  last_progress_date: Date
}

export interface IMyAnalyticsLearningHistoryProgressRange {
  [lexId: string]: Array<{ key: number; doc_count: number }>
}

export interface IMyAnalyticsGoalProgress {
  goal_id: string
  goal_title: string
  email_id: string
  last_updated_on: Date
  progress: number
  goal_type: string
  goal_duration: string
  goal_end_date: Date
  created_on: Date
  goal_content_id: string
  goal_start_date: Date
}

export interface IAdmin {
  is_admin: boolean
}
export interface IRecommendedSkills {
  category?: string
  certification_count?: number | null
  course_count?: number
  criticality?: string
  horizon?: string
  image_url: string
  is_deleted: null
  is_grouped: null
  is_searchable: number
  rank_number: number
  skill_id: number
  skill_name: string
}
export interface IAcquiredSkills {
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
}
export interface IAllSkills {
  category_wise_skill_count: ICount[]
  horizon_wise_skill_count: ICount[]
  is_pagination: boolean
  skill_list: ISkillList[]
  total_skills: number
}
export interface ISkillQuotient {
  assessment: IAssessment[]
  available_certification: IAvailableCourseCertification[]
  available_certification_org_data: { [key: string]: ICount[] }
  available_course: IAvailableCourseCertification[]
  available_course_org_data: { [key: string]: IOrgData[] }
  default_quotient: number
  org_wide_stats: IOrgData[]
  skill_details: ISkillList[]
  skill_quotient: ISkillQuotientDetails
  total_user_count: number
  type: string
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
  image_url: string
  is_deleted: null
  is_grouped: null
  is_searchable: number
  rank_number: number
  skill_id: number
  skill_name: string
  skill_type: null
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
}

export interface IRangeObj {
  name: string
  data: IDataObj[]
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
  certification_comp: null
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
  skill_certification_comp: number | null
  skill_course_comp: number
  skill_group_id: string
  skill_id: string
  skill_name: string
  total_skill_quotient: number
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
  skills: IExistingSkills[]
  type: string
}

export interface IExistingSkills {
  skill_id: string
  skill_name: string
}

export interface ICreateObj {
  roleId: string
  type: string
}

export interface ITimeSpentResponse {
  JL_wise: IGraphData[]
  badges_details: IBadgeDetails[]
  category_wise: IGraphData[]
  date_wise: IGraphData[]
  last_updated_on: Date
  org_wide_avg_time_spent: number
  org_wide_category_time_spent: IGraphData[]
  points_and_ranks: IPointsAndRanks
  time_spent_by_user: number
  timespent_user_vs_org_wide: {
    time_spent_by_user: number;
    usage_percent: number;
  }
  total_badges_earned: number
  track_wise_user_timespent: ITrackWiseData
  unit_wise: IGraphData
}
export interface IGraphData {
  key: number
  key_as_string: string
  value: number
}

interface IAchievementMeta {
  avgCountOrgWide: number
  total: number
  userCountVsOrgWide: number
}

export interface IAssessmentResponseV1 extends IAchievementMeta {
  assessments?: IAchievement[]
}
export interface IAchievementsResponse
  extends IAssessmentResponseV1,
    ICertificateResponse {
  achievements: IAchievement[]
}

export interface ICertificateResponse extends IAchievementMeta {
  certifications?: IAchievement[]
}

interface IAchievement {
  date: string
  score: number
  attempts: IAttemptsV1[]
  id: string
  isPassed: boolean
  maxScore: number
  minScore: number
  noOfAttempts: number
  percentile: number
  title: string
  scoreDistribution: IRangeV1
}

interface IRangeV1 {
  [key: string]: {
    '0.0-25.0': number;
    '25.0-50.0': number;
    '50.0-75.0': number;
    '75.0-100.0': number;
  }
}

interface IAttemptsV1 {
  Date: Date
  score: number
  isPassed: boolean
}

export interface IAssessmentResponse {
  assessment: IAssessment[]
  assessment_score_ranges: IRange
  certifications_count: number
  user_assessment_count_vs_org_wide: number
  certification_list: ICertificationList[]
}

export interface ITrackWiseData {
  [key: string]: IMonthWiseData[]
}
export interface IRange {
  [key: string]: {
    '25': number;
    '50': number;
    '75': number;
    '100': number;
    content_name: string;
    type: string;
  }
}
export interface IAssessment {
  assessment_date: string
  assessment_score: number
  content_name: string
  lex_id: string
  max_score: number
  min_score: number
  percentile: number
  type: string
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
export interface IBadgeDetails {
  badge_id: string
  badge_name: string
  badge_type: string
  content_name: string | null
  description: string
  email_id: string
  first_received_date: Date
  last_received_date: Date
  progress: string
}
export interface IPointsAndRanks {
  monthly_points_earned: number
  org_wide_avg_points_earned: number
  points_user_vs_org_wide: IPointsANdRanksOrgWide
  rank: number
  user_points_earned: number
}

export interface IPointsANdRanksOrgWide {
  points_earned_by_user: number
  points_percent: number
}
export interface IMonthWiseData {
  month_year: string
  number_of_content_accessed: number
  timespent_in_mins: number
  track: string
}
export interface IUserProgressResponse {
  goal_progress: IGoalProgress[]
  goal_shared_by_me: IGoalsShared[]
  goal_shared_to_me: IGoalsShared[]
  learning_history: ILearningHistory[]
  learning_history_progress_range: ILearningHistoryProgress[]
  playlist_progress: IPlaylistProgress[]
  playlist_shared_by_me: IPlaylistShared[]
  playlist_shared_to_me: IPlaylistShared[]
  top_content_jl: ITopContentByJl[]
  top_content_unit: ITopContentByJl[]
}
export interface IGoalProgress {
  created_on: string
  email_id: string
  goal_content_id: string
  goal_duration: string
  goal_end_date: string
  goal_id: string
  goal_start_date: string
  goal_title: string
  goal_type: string
  last_updated_on: string
  progress: number
}
export interface IGoalsShared {
  created_on: string
  email_id: string
  goal_end_date: null
  goal_id: string
  goal_start_date: null
  goal_title: string
  goal_type: string
  last_updated_on: string
  progress: number
  shared_by: string
  shared_on: string
  status: string
}
export interface ILearningHistory {
  content_name: string
  last_progress_date: string
  lex_id: string
  progress: number
}
export interface ILearningHistoryProgress {
  key: number
  doc_count: number
}
export interface IPlaylistShared {
  created_on: string
  email_id: string
  play_list_id: string
  play_list_title: string
  progress: number
  resource_id: string
  shared_by: string
  type?: string
}
export interface ITopContentByJl {
  content_name: string
  count: number
  lex_id: string
}
export interface IPlaylistProgress {
  created_on: string
  email_id: string
  last_updated_on: string
  play_list_id: string
  play_list_title: string
  progress: number
  shared_by: string
  visibility: string
}
export interface INsoResponse {
  artifacts_shared: IArtificatsShared[]
  content_created: IContentCreated[]
  experts_contacted: IExpertsContacted[]
  feature_usage_stats: IFeatureUsageStatistics
  likes_detail: IArtificatsShared[]
  nso_content_progress: INsoContentProgress
  nso_roles: INsoRoles[]
  playground_details: IPlayGroundDetails[]
  total_nso_program_taken: number
}
export interface IArtificatsShared {
  content_id: string
  content_name: string
  date_of_use: string
  email_id: string
  type: string
}
export interface IExpertsContacted {
  contact_medium: string
  contacted: string
  content_id: string
  content_name: string
  date_of_use: string
  email_id: string
  type: string
}
export interface IFeatureUsageStatistics {
  feedback_count: number
  from_leaders_count: number
  live_count: number
  marketing_count: number
  navigator_count: number
  onboarding_count: number
  radio_count: number
  search_count: number
  tour_count: number
  tv_count: number
}
export interface INsoContentProgress {
  role_name: string
}
export interface INsoRoles {
  lex_id: string
  role_id: string
  role_name: string
}
export interface IPlayGroundDetails {
  activity: string
  contest_Name: string
  date_of_use: string
  email_id: string
  marks_Obtained: string
  sub_activity: string
  type: string
  video_Proctoring: boolean
}
export interface IContentCreated {
  app_icon: string
  content_name: string
  content_type: string
  email_id: string
  last_updated_on: string
  lex_id: string
}

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
