import express from 'express'
import { CONSTANTS } from '../utils/env'
import { discussionHubApi } from './discussionHub/discussionHub'

export const protectedApiV8 = express.Router()

protectedApiV8.get('/', (_req, res) => {
  res.json({
    config: CONSTANTS.HTTPS_HOST,
    type: 'PROTECTED API HOST 👌',
  })
})


protectedApiV8.use('/discussionHub', discussionHubApi)
