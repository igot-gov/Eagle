import axios from 'axios'
import { Router } from 'express'
import { axiosRequestConfig } from '../../configs/request.config'
import { CONSTANTS } from '../../utils/env'
import { logError } from '../../utils/logger'
import { ERROR } from '../../utils/message'

const API_END_POINTS = {
    mandatoryContentStatus: (userId: string) => `${CONSTANTS.SB_EXT_API_BASE_2}/v1/check/mandatoryContentStatus/${userId}`,
}

export const mandatoryContent = Router()

mandatoryContent.get('/checkStatus/:userId', async (req, res) => {
    try {
        const rootOrg = req.headers.rootorg
        const org = req.headers.org
        const wid = req.headers.wid
        const userId = req.params.userId
        if (!rootOrg || !org || !wid || !userId) {
            res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
            return
        }
        const response = await axios.get(API_END_POINTS.mandatoryContentStatus(userId), {
            ...axiosRequestConfig,
            headers: {
              'rootOrg': rootOrg,
              'org' : org
            },
        })
        res.status(response.status).send(response.data)
    } catch (err) {
        logError('failed to process the request' + err)
        res.status((err && err.response && err.response.status) || 500).send(
            (err && err.response && err.response.data) || {
                error: 'Failed due to unknown reason',
            }
        )
    }
})
