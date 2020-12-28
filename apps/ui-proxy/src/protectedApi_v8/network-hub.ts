import axios from 'axios'
import { Router } from 'express'
import { axiosRequestConfig } from '../configs/request.config'
import { CONSTANTS } from '../utils/env'
import { ERROR } from '../utils/message'
import { extractUserIdFromRequest } from '../utils/requestExtract'

const API_END_POINTS = {
    getNetworkHubUsers: `${CONSTANTS.USER_PROFILE_API_BASE}/public/v8/networkHub/users`,
}

export const networkHubApi = Router()

networkHubApi.post('/users', async (req, res) => {
    const userId = extractUserIdFromRequest(req)
    try {
        const rootOrg = req.header('rootOrg')
        if (!rootOrg) {
            res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
            return
        }
        const reqStructure = {
            department: req.body.department || '',
            intervalInDays: req.body.intervalInDays || 7,
            // intervalInDays: new Date(Date.now() + req.body.intervalInDays * 24 * 60 * 60 * 1000),
            limit: req.body.limit || 20,
            offset: req.body.offset || 0,
            type: req.body.type || 'latestUsers',
            userId,
        }
        const response = await axios.post(API_END_POINTS.getNetworkHubUsers, reqStructure, {
            ...axiosRequestConfig,
            headers: { rootOrg },
        })
        res.json(response.data)
    } catch (err) {
        res
            .status((err && err.response && err.response.status) || 500)
            .send((err && err.response && err.response.data) || err)
    }
})
