import {
  ERROR_CODE_HASH,
  IGoal,
  IGoalGroupSbExt,
  IGoalSbExtV1,
  IGoalSbExtV2,
  IGoalsGroup,
  IGoalUpsertRequest,
  IGoalUpsertResponse,
  IGoalUpsertResponseSbExt,
  IGoalUpsertSbExt,
  IProgressResource as IResourceProgress,
  IResourceProgressSbExt,
  ITrackStatusSbExt,
  IUserGoals,
  IUserGoalSbExt
} from '../models/goal.model'
import { processDisplayContentType } from '../utils/contentHelpers'

export function transformToGoalForOthers(goalForOthersSbExt: IGoalSbExtV1): IGoal {
  return {
    contentIds: goalForOthersSbExt.goal_content_id,
    contentProgress: goalForOthersSbExt.resource_progress,
    contents:
      (goalForOthersSbExt.content_data || goalForOthersSbExt.goal_content_details || []).map(
        (content) => ({
          ...content,
          displayContentType: processDisplayContentType(content.contentType, content.resourceType),
        })
      ) || [],
    description: goalForOthersSbExt.goal_desc,
    duration: goalForOthersSbExt.goal_duration,
    endDate: goalForOthersSbExt.goal_end_date,
    goalFor: goalFor(goalForOthersSbExt.goal_type),
    id: goalForOthersSbExt.goal_id,
    isShared: isShared(goalForOthersSbExt.goal_type),
    name: goalForOthersSbExt.goal_title,
    progress: goalForOthersSbExt.goalProgess,
    sharedBy: goalForOthersSbExt.shared_by,
    sharedOn: goalForOthersSbExt.shared_on,
    sharedWith: goalForOthersSbExt.recipient_list,
    startDate: goalForOthersSbExt.goal_start_date,
    type: goalForOthersSbExt.goal_type,
    user: goalForOthersSbExt.user,
  }
}

export function transformToCommonGoal(goalSbExt: IGoalSbExtV2): IGoal {
  return {
    contentIds: goalSbExt.goalContentId,
    contents: goalSbExt.resources,
    createdForOthers: goalSbExt.createdForOthers,
    createdForSelf: goalSbExt.createdForSelf,
    description: goalSbExt.goalDescription,
    duration: 0,
    goalFor: 'me',
    id: goalSbExt.id,
    isShared: false,
    name: goalSbExt.goalTitle,
    type: 'common',
  }
}

export function transformToCommonGoalGroup(goalGroupSbExt: IGoalGroupSbExt): IGoalsGroup {
  return {
    goals: goalGroupSbExt.goals && goalGroupSbExt.goals.map(transformToCommonGoal),
    id: goalGroupSbExt.group_id,
    name: goalGroupSbExt.group_name,
  }
}

export function transformToUserGoals(userGoalSbExt: IUserGoalSbExt): IUserGoals {
  return {
    completedGoals: userGoalSbExt.completed_goals.map(transformToGoalForOthers),
    goalsInProgress: userGoalSbExt.goals_in_progress.map(transformToGoalForOthers),
  }
}

export function transformGoalUpsertRequest(goal: IGoalUpsertRequest): IGoalUpsertSbExt {
  return {
    goal_content_id: goal.contentIds,
    goal_desc: goal.description,
    goal_duration: goal.duration,
    goal_id: goal.id,
    goal_title: goal.name,
    goal_type: goal.type,
  }
}

export function transformGoalUpsertResponse(
  response: IGoalUpsertResponseSbExt
): IGoalUpsertResponse {
  const error = response.errors
  if (error && error.length) {
    return { error: ERROR_CODE_HASH[error[0].code] || error[0].code }
  }
  return {}
}

export function transformResourceProgress(
  resourceProgressSbExt: IResourceProgressSbExt
): IResourceProgress {
  return {
    contentType: resourceProgressSbExt.content_type,
    displayContentType: processDisplayContentType(
      resourceProgressSbExt.content_type,
      resourceProgressSbExt.resourceType
    ),
    duration: resourceProgressSbExt.resource_duration,
    id: resourceProgressSbExt.resource_id,
    mimeType: resourceProgressSbExt.mime_type,
    name: resourceProgressSbExt.resource_name,
    progress: resourceProgressSbExt.resource_progress,
    timeLeft: resourceProgressSbExt.time_left,
  }
}

export function transformToTrackStatus(trackStatusSbExt: ITrackStatusSbExt) {
  return {
    accepted: (trackStatusSbExt.accepted || []).map((goal) => ({
      endDate: goal.goal_end_date,
      lastUpdatedOn: goal.last_updated_on,
      progress: goal.goal_progress,
      resourceProgressTracker: goal.resource_progress_tracker,
      sharedWith: goal.shared_with,
      startDate: goal.goal_start_date,
      status: goal.status,
    })),
    pending: (trackStatusSbExt.pending || []).map((pending) => ({
      lastUpdatedOn: pending.last_updated_on,
      sharedWith: pending.shared_with,
      status: pending.status,
    })),
    rejected: (trackStatusSbExt.rejected || []).map((goal) => ({
      lastUpdatedOn: goal.last_updated_on,
      message: goal.status_message,
      sharedWith: goal.shared_with,
      status: goal.status,
    })),
  }
}

export function formPlaylistupdateObj(req: { name: string, versionKey: string }) {
  return {
    request: {
      content: {
        name: req.name,
        versionKey: req.versionKey,

      },
    },
  }
}

export function transformToSbExtPatchRequest(req: { contentIds: string[] }, goalId: string) {
  /* for Patch request to change playlist title */
  const id = goalId
  const hierarchy = {}

  hierarchy[id] = {
    children: req.contentIds,
    contentType: 'Collection',
    root: true,
  }

  return {
    request: {
      data: {
        hierarchy,
        nodesModified: {},
      },
    },
  }
}

export function formGoalRequestObj(request: { createdBy: string; name: string; description: string }, userId: string) {
  /* for Patch request to change playlist title */
  return {
    request: {
      content: {
        code: 'org.ekstep0.29884945860157064123',
        contentType: 'Collection',
        createdBy: userId,
        creator: request.createdBy,
        description: request.description,
        license: 'CC BY 4.0',
        mimeType: 'application/vnd.ekstep.content-collection',
        name: request.name,
        primaryCategory: 'Playlist',
      },
    },
  }
}

export function formContentRequestObj(req: { contentIds: string[] }, res: { result: { identifier: string } }, _userId: string) {
  /* for Patch request to change playlist title */
  const id = res.result.identifier
  const hierarchy = {}

  hierarchy[id] = {
    children: req.contentIds,
    contentType: 'Collection',
    root: true,
  }

  return {
    request: {
      data: {
        hierarchy,
        nodesModified: {},
      },
    },
  }

}

function goalFor(goalType: string) {
  return goalType === 'user' || goalType === 'common' ? 'me' : 'others'
}

function isShared(goalType: string) {
  return goalType === 'common_shared' || goalType === 'custom_shared'
}
