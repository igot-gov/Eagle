import axios from 'axios'
import { Router } from 'express'
import { axiosRequestConfig } from '../../configs/request.config'
import { CONSTANTS } from '../../utils/env'
import { logError } from '../../utils/logger'
import { ERROR } from '../../utils/message'

const API_END_POINTS = {
    mandatoryContentStatus: `${CONSTANTS.SB_EXT_API_BASE_2}/v1/check/mandatoryContentStatus`,
}

export const mandatoryContent = Router()

mandatoryContent.get('/checkStatus', async (req, res) => {
    try {
        const rootOrgValue = req.headers.rootorg
        const orgValue = req.headers.org
        const widValue = req.headers.wid
        if (!rootOrgValue || !orgValue || !widValue) {
            res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
            return
        }

        const response = await axios.get(API_END_POINTS.mandatoryContentStatus, {
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
                error: 'Failed due to unknown reason',
            }
        )
    }
})
