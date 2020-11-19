import { EMimeTypes, IContentMinimal, TContentType } from './content.model'

export interface IGoalUpsertRequest {
  id?: string
  type: string
  contentIds?: string[]
  name?: string
  description?: string
  duration?: number
}

export interface IGoalUserData {
  email?: string
  name?: string
  userId?: string
}

export interface IGoalUpsertSbExt {
  goal_id?: string
  goal_type: string
  goal_content_id?: string[]
  goal_title?: string
  goal_desc?: string
  goal_duration?: number
}

export interface IGoalSbExtV1 {
  created_on: number
  goal_content_id: string[]
  goal_desc: string
  goal_duration: number
  goal_end_date?: number
  goal_id: string
  goal_start_date?: number
  goal_title: string
  goal_type: string
  goalProgess: number
  last_updated_on: number
  // user_email: string;
  user: IGoalUserData
  shared_on?: number
  shared_by?: IGoalUserData
  content_data: IContentMinimal[]
  resource_progress: IContentProgress[]
  goal_content_details?: IContentMinimal[]
  recipient_list: string[]
}

export interface IGoal {
  contentIds: string[]
  contentProgress?: IContentProgress[]
  contents: IContentMinimal[]
  description: string
  duration: number
  endDate?: number
  goalFor: 'me' | 'others'
  id: string
  isShared: boolean
  name: string
  progress?: number
  sharedWith?: string[]
  sharedBy?: IGoalUserData
  sharedOn?: number
  startDate?: number
  type: string
  userEmail?: string
  user?: IGoalUserData
  createdForSelf?: boolean
  createdForOthers?: boolean
}

export interface IContentProgress {
  contentType: string
  duration: number
  hasAccess: boolean
  identifier: string
  mimeType: string
  name: string
  resource_progress: number
  status: string
  timeLeft: number
  isInIntranet?: boolean
}

export interface IUserGoalSbExt {
  goals_in_progress: IGoalSbExtV1[]
  completed_goals: IGoalSbExtV1[]
}

export interface IUserGoals {
  goalsInProgress: IGoal[]
  completedGoals: IGoal[]
}

export interface IGoalSbExtV2 {
  createdOn: number
  goalContentId: string[]
  resources: IContentMinimal[]
  goalDescription: string
  goalTitle: string
  id: string
  createdForSelf?: boolean
  createdForOthers?: boolean
}

export interface IGoalGroupSbExt {
  group_id: string
  group_name: string
  goals?: IGoalSbExtV2[]
}

export interface IGoalsGroup {
  id: string
  name: string
  goals?: IGoal[]
}

export interface IGoalUpsertResponseSbExt {
  errors?: Array<{
    code: string
    message: string
  }>
}

export interface IGoalUpsertResponse {
  error?: string
}

export const ERROR_CODE_HASH: { [code: string]: string } = {
  'Goal already exists': 'ERROR_GOAL_EXISTS',
}

export interface ITrackStatusSbExt {
  accepted: ITrackAcceptedSbExt[]
  accepted_count: number
  rejected: ITrackRejectedSbExt[]
  rejected_count: number
  pending: ITrackPendingSbExt[]
  pending_count: number
}

export interface ITrackAcceptedSbExt {
  shared_with: IGoalUserData
  goal_progress: number
  resource_progress_tracker: IResourceProgressSbExt[]
  goal_end_date: number
  last_updated_on: number
  goal_start_date: number
  status: number
}

export interface IResourceProgressSbExt {
  resource_progress: number
  time_left: number
  content_type: TContentType
  mime_type: EMimeTypes
  resource_id: string
  resource_duration: number
  resource_name: string
  resourceType: string
}

export interface ITrackRejectedSbExt {
  status_message: string
  shared_with: string
  last_updated_on: number
  status: number
}

export interface ITrackPendingSbExt {
  shared_with: IGoalUserData
  last_updated_on: number
  status: number
}

export interface ITrackStatus {
  accepted: ITrackAccepted[]
  rejected: ITrackRejected[]
  pending: ITrackPending[]
}

export interface ITrackAccepted {
  sharedWith: IGoalUserData
  progress: number
  resourceProgressTracker: IResourceProgressSbExt[]
  endDate: number
  lastUpdatedOn: number
  startDate: number
  status: number
}

export interface IProgressResource {
  contentType: TContentType
  displayContentType: string
  duration: number
  id: string
  mimeType: EMimeTypes
  name: string
  progress: number
  timeLeft: number
}

export interface ITrackRejected {
  message: string
  sharedWith: string
  lastUpdatedOn: number
  status: number
}

export interface ITrackPending {
  sharedWith: IGoalUserData
  lastUpdatedOn: number
  status: number
}
