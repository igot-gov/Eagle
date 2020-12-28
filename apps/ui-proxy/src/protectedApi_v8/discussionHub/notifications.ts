import axios from 'axios'
import { Router } from 'express'
import { getRootOrg } from '../../authoring/utils/header'
import { axiosRequestConfig } from '../../configs/request.config'
import { getUserUID, getWriteApiToken } from '../../utils/discussionHub-helper'
import { CONSTANTS } from '../../utils/env'
import { logError, logInfo } from '../../utils/logger'
import { extractUserIdFromRequest } from '../../utils/requestExtract'

const API_ENDPOINTS = {
    getNotifications: `${CONSTANTS.DISCUSSION_HUB_API_BASE}/api/notifications`,
}

export const notificationsApi = Router()

notificationsApi.get('/', async (req, res) => {
    try {
        const rootOrg = getRootOrg(req)
        const userId = extractUserIdFromRequest(req)
        logInfo(`UserId: ${userId}, rootOrg: ${rootOrg}`)
        const userUid = await getUserUID(userId)
        const url = API_ENDPOINTS.getNotifications + `?_uid=${userUid}`
        const response = await axios.get(
            url,
            { ...axiosRequestConfig, headers: { authorization: getWriteApiToken() } }
        )
        res.send(response.data)
    } catch (err) {
        logError('ERROR ON GET topicsApi /recent >', err)
        res.status((err && err.response && err.response.status) || 500)
            .send(err && err.response && err.response.data || {})
    }
})
