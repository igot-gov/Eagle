import axios from 'axios'
import { Router } from 'express'
import { getRootOrg } from '../../authoring/utils/header'
import { axiosRequestConfig } from '../../configs/request.config'
import { CONSTANTS } from '../../utils/env'
import { logError, logInfo } from '../../utils/logger'
import { getWriteApiToken } from '../../utils/nodebb-helper'
import { extractUserIdFromRequest } from '../../utils/requestExtract'

const API_ENDPOINTS = {
    createTopic: `${CONSTANTS.NODE_BB_API_BASE}/api/v2/topics`,
}

export const writeApi = Router()

writeApi.post('/topics', async (req, res) => {
    try {
        const rootOrg = getRootOrg(req)
        const userId = extractUserIdFromRequest(req)
        logInfo(`UserId: ${userId}, rootOrg: ${rootOrg}`)
        const url = API_ENDPOINTS.createTopic
        const response = await axios.post(
            url,
            {
                ...req.body,
                // _uid: getWriteApiUID(),
            },
            { ...axiosRequestConfig, headers: { authorization: getWriteApiToken() } }
        )
        res.send(response.data)
    } catch (err) {
        logError('ERROR ON GET topicsApi /topics >', err)
        res.status((err && err.response && err.response.status) || 500)
            .send(err && err.response && err.response.data || {})
    }
})
