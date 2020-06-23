import axios from 'axios'
import { Router } from 'express'
import { axiosRequestConfig } from '../../configs/request.config'
import { CONSTANTS } from '../../utils/env'
import { logError } from '../../utils/logger'
import { ERROR } from '../../utils/message'
import { extractUserIdFromRequest } from '../../utils/requestExtract'

const API_END_POINTS = {
    createUserRegistry: `${CONSTANTS.USER_PROFILE_API_BASE}/public/v8/profileDetails/createUserRegistry`,
    getMasterLanguages: `${CONSTANTS.USER_PROFILE_API_BASE}/public/v8/profileDetails/getMasterLanguages`,
    getMasterNationalities: `${CONSTANTS.USER_PROFILE_API_BASE}/public/v8/profileDetails/getMasterNationalities`,
    getUserRegistry: `${CONSTANTS.USER_PROFILE_API_BASE}/public/v8/profileDetails/getUserRegistry`,
    setUserProfileStatus: `${CONSTANTS.USER_PROFILE_API_BASE}/public/v8/profileDetails/setUserProfileStatus`,
    userProfileStatus: `${CONSTANTS.USER_PROFILE_API_BASE}/public/v8/profileDetails/userProfileStatus`,
}

export async function getUserProfileStatus(wid: string) {
    try {
        const response = await axios.post(API_END_POINTS.userProfileStatus, { wid }, {
            ...axiosRequestConfig,
        })
        if (response.data.status) {
            return true
        } else {
            return false
        }
    } catch (err) {
        logError('ERROR GETTING USER PROFILE STATUS FROM  ${API_END_POINTS.userProfileStatus} >', err)
        return false
    }
}

export const profileDeatailsApi = Router()

profileDeatailsApi.post('/createUserRegistry', async (req: any, res: any) => {
    try {
        const userId = extractUserIdFromRequest(req)
        const response = await axios.post(API_END_POINTS.createUserRegistry, { ...req.body, userId }, {
            ...axiosRequestConfig,
        })
        res.status(response.status).json(response.data)
    } catch (err) {
        logError('ERROR CREATING USER REGISTRY >', err)
        res.status((err && err.response && err.response.status) || 500).send(err)
    }
})

// tslint:disable-next-line: no-identical-functions
profileDeatailsApi.get('/getUserRegistry', async (req: any, res: any) => {
    try {
        const userId = extractUserIdFromRequest(req)

        const response = await axios.post(API_END_POINTS.getUserRegistry, { wid: userId }, {
            ...axiosRequestConfig,
        })
        res.status(response.status).send(response.data)
    } catch (err) {
        logError('ERROR FETCHING USER REGISTRY >', err)
        res.status((err && err.response && err.response.status) || 500).send(err)
    }
})

profileDeatailsApi.get('/userProfileStatus', async (req: any, res: any) => {
    try {
        const org = req.header('org')
        const rootOrg = req.header('rootOrg')
        if (!org || !rootOrg) {
            res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
            return
        }
        req.body.wid = extractUserIdFromRequest(req)
        const response = await axios.post(API_END_POINTS.userProfileStatus, req, {
            ...axiosRequestConfig,
            headers: { rootOrg },
        })
        res.status(response.status).send(response.data)
    } catch (err) {
        logError('ERROR FETCHING USER PROFILE STATUS >', err)
        res.status((err && err.response && err.response.status) || 500).send(err)
    }
})

profileDeatailsApi.post('/setUserProfileStatus', async (req: any, res: any) => {
    try {
        req.body.wid = extractUserIdFromRequest(req)
        const response = await axios.post(API_END_POINTS.setUserProfileStatus, req, {
            ...axiosRequestConfig,
            headers: req.headers,
        })
        res.status(response.status).send(response.data)
    } catch (err) {
        logError('ERROR SETTING USER PROFILE STATUS >', err)
        res.status((err && err.response && err.response.status) || 500).send(err)
    }
})

profileDeatailsApi.get('/getMasterLanguages', async (_req: any, res: any) => {
    try {
        const response = await axios.get(API_END_POINTS.getMasterLanguages, {
            ...axiosRequestConfig,
        })
        res.status(response.status).send(response.data)
    } catch (err) {
        logError('ERROR FETCHING MASTER LANGUAGES >', err)
        res.status((err && err.response && err.response.status) || 500).send(err)
    }
})

// tslint:disable-next-line: no-identical-functions
profileDeatailsApi.get('/getMasterNationalities', async (_req: any, res: any) => {
    try {
        const response = await axios.get(API_END_POINTS.getMasterNationalities, {
            ...axiosRequestConfig,
        })
        res.status(response.status).send(response.data)
    } catch (err) {
        logError('ERROR FETCHING MASTER NATIONALITIES >', err)
        res.status((err && err.response && err.response.status) || 500).send(err)
    }
})
