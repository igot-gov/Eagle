import axios from 'axios'
import { Router } from 'express'

import { axiosRequestConfig } from '../configs/request.config'
import { CONSTANTS } from '../utils/env'
import { logError } from '../utils/logger'
import { ERROR } from '../utils/message'

const API_END_POINTS = {
    checkProfanity: (contentId: string, userId: string) =>
        `${CONSTANTS.CONTENT_VALIDATION_API_BASE}/contentValidation/v1/checkProfanity/${contentId}/${userId}`,
}

export const contentValidationApi = Router()
const unknownError = 'Failed due to unknown reason'

contentValidationApi.get('/checkProfanity/:contentId/:userId', async (req, res) => {
    try {
        const rootOrgValue = req.headers.rootorg
        const orgValue = req.headers.org
        const widValue = req.headers.wid
        const userId = req.params.userId
        const contentId = req.params.contentId
        if (!rootOrgValue || !orgValue || !widValue || !contentId || !userId) {
            res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
            return
        }

        const response = await axios.get(API_END_POINTS.checkProfanity(contentId, userId), {
            ...axiosRequestConfig,
            headers: {
                org: orgValue,
                rootOrg: rootOrgValue,
                wid: widValue,
            },
        })
        res.status(response.status).send(response.data)
    } catch (err) {
        logError('failed to process the request' + err)
        res.status((err && err.response && err.response.status) || 500).send(
            (err && err.response && err.response.data) || {
                error: unknownError,
            }
        )
    }
})
