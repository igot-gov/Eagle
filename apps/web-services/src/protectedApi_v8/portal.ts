import axios from 'axios'
import express from 'express'
import { NextFunction, Request, Response } from 'express'
import { CONSTANTS } from '../utils/env'
import { logError, logInfo } from '../utils/logger'
import { extractUserIdFromRequest } from '../utils/requestExtract'
import { getUserRoles } from './user/roles'

export const portalApi = express.Router()

const API_END_POINTS = {
    accessValidator: (keyWord: string) => `${CONSTANTS.SB_EXT_API_BASE_2}/portal/${keyWord}/isAdmin`,
    deptApi: `${CONSTANTS.SB_EXT_API_BASE_2}/portal/department`,
}

const portalValidator = async function validatePortalAccess(req: Request, res: Response, next: NextFunction) {
    logInfo('validatePortalAccess...URL -> ', req.originalUrl)
    try {
        const userId = extractUserIdFromRequest(req)
        let accessUrlKey = ''
        if (req.originalUrl.includes('/mdo/')) {
            accessUrlKey = 'mdo'
        } else if (req.originalUrl.includes('/spv/')) {
            accessUrlKey = 'spv'
        } else if (req.originalUrl.includes('/cbp/')) {
            accessUrlKey = 'cbp'
        }

        const resp = await axios.get(API_END_POINTS.accessValidator(accessUrlKey), {
            headers: {
                wid: userId,
            },
        })
        const hasAccess = JSON.parse(resp.data)
        if (hasAccess) {
            next()
        } else {
            logInfo('Need to throw 403 error')
            res.status(400).send('Access Denied')
        }
    } catch (err) {
        logError('Failed to process request: ' + err)
        res.status((err && err.response && err.response.status) || 500).send(
            (err && err.response && err.response.data) || {
                error: 'Unknown Error',
            }
        )
    }
}

export const mdoPortalApi = express.Router()
export const spvPortalApi = express.Router()

const unknownError = 'Failed due to unknown reason'
const failedToProcess = 'Failed to process the request.'
const badRequest = 'Bad request. UserId is a mandatory header'
const department = '/department'

portalApi.get('/isAdmin/:deptType', async (req, res) => {
    const userId = req.headers.wid as string
    const deptType = req.params.deptType
    if (!userId || !deptType) {
        res.status(400).send('Bad Request.')
        return
    }
    const resp = await axios.get(API_END_POINTS.accessValidator(deptType), {
        headers: {
            wid: userId,
        },
    })
    res.status(resp.status).send(resp.data)
})

portalApi.use('/mdo', portalValidator, mdoPortalApi)
portalApi.use('/spv', portalValidator, spvPortalApi)

portalApi.all('/', async (req, res) => {
    try {
        const userId = req.headers.wid as string
        const rootOrg = req.headers.rootOrg as string
        const roles = await getUserRoles(userId, rootOrg)
        logInfo(roles)
    } catch (err) {
        logError(failedToProcess + err)
        res.status((err && err.response && err.response.status) || 500).send(
            (err && err.response && err.response.data) || {
                error: unknownError,
            }
        )
    }
})

spvPortalApi.get(department, async (req, res) => {
    try {
        const userId = req.headers.wid as string
        if (!userId) {
            res.status(400).send(badRequest)
            return
        }
        const response = await axios.get(API_END_POINTS.deptApi)
        res.status(response.status).send(response.data)
    } catch (err) {
        logError(failedToProcess + err)
        res.status((err && err.response && err.response.status) || 500).send(
            (err && err.response && err.response.data) || {
                error: unknownError,
            }
        )
    }
})

spvPortalApi.post(department, async (req, res) => {
    try {
        const userId = req.headers.wid as string
        if (!userId) {
            res.status(400).send(badRequest)
            return
        }
        const response = await axios.post(API_END_POINTS.deptApi, req.body)
        res.status(response.status).send(response.data)
    } catch (err) {
        logError(failedToProcess + err)
        res.status((err && err.response && err.response.status) || 500).send(
            (err && err.response && err.response.data) || {
                error: unknownError,
            }
        )
    }
})

spvPortalApi.patch(department, async (req, res) => {
    try {
        const userId = req.headers.wid as string
        if (!userId) {
            res.status(400).send(badRequest)
            return
        }
        const response = await axios.patch(API_END_POINTS.deptApi, req.body)
        res.status(response.status).send(response.data)
    } catch (err) {
        logError(failedToProcess + err)
        res.status((err && err.response && err.response.status) || 500).send(
            (err && err.response && err.response.data) || {
                error: unknownError,
            }
        )
    }
})
