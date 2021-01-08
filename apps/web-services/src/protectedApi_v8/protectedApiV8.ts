import express from 'express'
import { CONSTANTS } from '../utils/env'
import { admin } from './admin/admin'
import { attendedContentApi } from './attendent-content'
import { catalogApi } from './catalog'
import { certificationApi } from './certifications'
import { cohortsApi } from './cohorts'
import { competencyApi } from './competency'
import { conceptGraphApi } from './concept'
import { connectionsApi } from './connections'
import { contentApi } from './content'
import { contentValidationApi } from './contentValidation'
import { counterApi } from './counter'
import { deptApi } from './departments'
import { discussionHubApi } from './discussionHub/discussionHub'
import { externalEventsApi } from './event-external'
import { eventsApi } from './events'
import { infyRadioApi } from './infyradio'
import { knowledgeHubApi } from './khub'
import { leaderBoardApi } from './leaderboard'
import { navigatorApi } from './navigator'
import { networkConnectionApi } from './network'

import { networkHubApi } from './network-hub'
import { portalApi } from './portal-v3'
import { recommendationApi } from './recommendation'
import { scoringApi } from './scoring'
import { scromApi } from './scrom'
import { socialApi } from './social'
import { trainingApi } from './training'
import { translateApi } from './translate'
import { user } from './user/user'
import { workflowHandlerApi } from './workflow-handler'

export const protectedApiV8 = express.Router()

protectedApiV8.get('/', (_req, res) => {
  res.json({
    config: CONSTANTS.HTTPS_HOST,
    type: 'PROTECTED API HOST 👌',
  })
})

protectedApiV8.use('/admin', admin)
protectedApiV8.use('/catalog', catalogApi)
protectedApiV8.use('/certifications', certificationApi)
protectedApiV8.use('/cohorts', cohortsApi)
protectedApiV8.use('/concept', conceptGraphApi)
protectedApiV8.use('/content', contentApi)
protectedApiV8.use('/profanity', contentValidationApi)
protectedApiV8.use('/counter', counterApi)
protectedApiV8.use('/discussionHub', discussionHubApi)
protectedApiV8.use('/infyradio', infyRadioApi)
protectedApiV8.use('/khub', knowledgeHubApi)
protectedApiV8.use('/leaderboard', leaderBoardApi)
protectedApiV8.use('/navigator', navigatorApi)
protectedApiV8.use('/networkHub', networkHubApi)
protectedApiV8.use('/recommendation', recommendationApi)
protectedApiV8.use('/scrom', scromApi)
protectedApiV8.use('/social', socialApi)
protectedApiV8.use('/training', trainingApi)
protectedApiV8.use('/user', user)
protectedApiV8.use('/events', eventsApi)
protectedApiV8.use('/translate', translateApi)
protectedApiV8.use('/attended-content', attendedContentApi)
protectedApiV8.use('/event-external', externalEventsApi)
protectedApiV8.use('/network', networkConnectionApi)
protectedApiV8.use('/connections', connectionsApi)
protectedApiV8.use('/competency', competencyApi)
protectedApiV8.use('/dept', deptApi)
protectedApiV8.use('/workflowhandler', workflowHandlerApi)
protectedApiV8.use('/scroing', scoringApi)
protectedApiV8.use('/portal', portalApi)
