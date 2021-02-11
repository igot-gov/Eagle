import axios from 'axios'
import { Router } from 'express'
import * as fs from 'fs'
import { axiosRequestConfig, axiosRequestConfigLong, axiosRequestConfigVeryLong } from '../../configs/request.config'
import { IPersonalDetails, ISBUser, ISunbirdbUserResponse, IUser } from '../../models/user.model'
import { CONSTANTS } from '../../utils/env'
import { logError, logInfo } from '../../utils/logger'
import { ERROR } from '../../utils/message'
import {
    extractAuthorizationFromRequest, extractRootOrgFromRequest,
    extractUserIdFromRequest, extractUserTokenFromRequest
} from '../../utils/requestExtract'

const API_END_POINTS = {
    createUserRegistry: `${CONSTANTS.USER_PROFILE_API_BASE}/public/v8/profileDetails/createUserRegistry`,
    getMasterLanguages: `${CONSTANTS.USER_PROFILE_API_BASE}/public/v8/profileDetails/getMasterLanguages`,
    getMasterNationalities: `${CONSTANTS.USER_PROFILE_API_BASE}/public/v8/profileDetails/getMasterNationalities`,
    getProfilePageMeta: `${CONSTANTS.USER_PROFILE_API_BASE}/public/v8/profileDetails/getProfilePageMeta`,
    getUserRegistry: `${CONSTANTS.USER_PROFILE_API_BASE}/public/v8/profileDetails/getUserRegistry`,
    getUserRegistryById: `${CONSTANTS.USER_PROFILE_API_BASE}/public/v8/profileDetails/getUserRegistryById`,
    setUserProfileStatus: `${CONSTANTS.USER_PROFILE_API_BASE}/public/v8/profileDetails/setUserProfileStatus`,
    userProfileStatus: `${CONSTANTS.USER_PROFILE_API_BASE}/public/v8/profileDetails/userProfileStatus`,
    // tslint:disable-next-line: object-literal-sort-keys
    migrateRegistry: `${CONSTANTS.USER_PROFILE_API_BASE}/public/v8/profileDetails/migrateRegistry`,
    createSb: `${CONSTANTS.USER_SUNBIRD_DETAILS_API_BASE}/api/user/v1/create`,
    searchSb: `${CONSTANTS.USER_SUNBIRD_DETAILS_API_BASE}/api/user/v1/search`,
}

const xAuthUser = `${CONSTANTS.X_AUTH_USER}`
const contentType = `${CONSTANTS.CONTENT_TYPE}`
const contentTypeValue = `${CONSTANTS.CONTENT_TYPE_VALUE}`
const authorizationHeader = `${CONSTANTS.AUTHORIZATION}`
const xAppId = `${CONSTANTS.X_APP_ID}`
const xAppIdVAlue = `${CONSTANTS.X_APP_ID_VALUE}`
const createUserRegistryApi = `${CONSTANTS.USER_SUNBIRD_DETAILS_API_BASE}/apis/protected/v8/user/profileRegistry/createUserRegistryV2`
const userPassword = 'Test.1234'

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

profileDeatailsApi.post('/createUserRegistry', async (req, res) => {
    try {
        const userId = extractUserIdFromRequest(req)
        logInfo('Create user registry for', userId)
        const response = await axios.post(API_END_POINTS.createUserRegistry, { ...req.body, userId }, {
            ...axiosRequestConfigLong,
        })
        res.status(response.status).json(response.data)
    } catch (err) {
        logError('ERROR CREATING USER REGISTRY >', err)
        res.status((err && err.response && err.response.status) || 500).send(err)
    }
})

// tslint:disable-next-line: no-identical-functions
profileDeatailsApi.get('/getUserRegistry', async (req, res) => {
    try {
        const userId = extractUserIdFromRequest(req)
        logInfo('Get user registry for', userId)
        const response = await axios.post(API_END_POINTS.getUserRegistry, { userId }, {
            ...axiosRequestConfig,
        })
        res.status(response.status).send(response.data)
    } catch (err) {
        logError('ERROR FETCHING USER REGISTRY >', err)
        res.status((err && err.response && err.response.status) || 500).send(err)
    }
})

// tslint:disable-next-line: no-identical-functions
profileDeatailsApi.get('/getUserRegistryById/:id', async (req, res) => {
    try {
        let userId = req.params.id
        if (!userId) {
            userId = extractUserIdFromRequest(req)
        }
        logInfo('Get user registry for', userId)

        const response = await axios.post(API_END_POINTS.getUserRegistry, { userId }, {
            ...axiosRequestConfig,
        })
        res.status(response.status).send(response.data)
    } catch (err) {
        logError('ERROR FETCHING USER REGISTRY >', err)
        res.status((err && err.response && err.response.status) || 500).send(err)
    }
})

profileDeatailsApi.get('/userProfileStatus', async (req, res) => {
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

profileDeatailsApi.post('/setUserProfileStatus', async (req, res) => {
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

profileDeatailsApi.get('/getMasterLanguages', async (_req, res) => {
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
profileDeatailsApi.get('/getMasterNationalities', async (_req, res) => {
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

profileDeatailsApi.get('/getProfilePageMeta', async (_req, res) => {
    try {
        const response = await axios.get(API_END_POINTS.getProfilePageMeta, {
            ...axiosRequestConfig,
        })
        res.status(response.status).send(response.data)
    } catch (err) {
        logError('ERROR FETCHING MASTER NATIONALITIES >', err)
        res.status((err && err.response && err.response.status) || 500).send(err)
    }
})

// Api to migrate data from eagleUser opensaber  to new userProfile opensaber
profileDeatailsApi.get('/migrateRegistry', async (req, res) => {
    const filePath = CONSTANTS.USER_BULK_UPLOAD_DIR || process.cwd() + '/user_upload/'
    try {
        // tslint:disable-next-line: no-any
        fs.readFile(filePath + 'migrateRegistry.json', async (err: any, json: any) => {
            if (!err) {
                const obj = JSON.parse(json)
                const widList = obj.widList
                const userId = extractUserIdFromRequest(req)

                logInfo('migrating the registry')
                const response = await axios.post(
                    API_END_POINTS.migrateRegistry,
                    { ...req.body, userId, widList },
                    {
                        ...axiosRequestConfigVeryLong,
                    }
                )
                res.status(response.status).json(response.data)
            } else {
                res.status(500).send(err)
            }
        })
    } catch (err) {
        logError('ERROR CREATING USER REGISTRY >', err)
        res.status((err && err.response && err.response.status) || 500).send(err)
    }
})

profileDeatailsApi.post('/createUser', async (req, res) => {
    try {
        const authorization = extractAuthorizationFromRequest(req)
        const xAuthenticatedUserToken = extractUserTokenFromRequest(req)
        const xAuth = authorization.split(' ')
        axios.defaults.headers.common[xAuthUser] = xAuth[1]
        axios.defaults.headers.common[authorizationHeader] = xAuth[0] + ' ' + xAuthenticatedUserToken
        axios.defaults.headers.common[contentType] = contentTypeValue
        axios.defaults.headers.common[xAppId] = xAppIdVAlue
        const sbemail_ = req.body.personalDetails.email
        const sbemailVerified_ = true
        const sbfirstName_ = req.body.personalDetails.firstName
        const sblastName_ = req.body.personalDetails.lastName
        const sbchannel_ = extractRootOrgFromRequest(req)
        const dataArr = sbemail_.split('@')
        const extractUserName = dataArr[dataArr.length - 2]
        const password_ = userPassword
        const searchresponse = await axios({
            ...axiosRequestConfig,
            data: { request: { query: '', filters: { userName: extractUserName.toLowerCase() } } },
            method: 'POST',
            url: API_END_POINTS.searchSb,
        })
        if (searchresponse.data.result.response.count > 0) {
            throw new Error('UserName Already Exist')
        } else {
            const sbUserProfile: Partial<ISBUser> = {
                channel: sbchannel_, email: sbemail_, emailVerified: sbemailVerified_, firstName: sbfirstName_,
                lastName: sblastName_, password: password_, userName: extractUserName,
            }
            const response = await axios({
                ...axiosRequestConfig,
                data: { request: sbUserProfile },
                method: 'POST',
                url: API_END_POINTS.createSb,
            })
            if (response.data.responseCode === 'CLIENT_ERROR') {
                throw new Error('Not able to create User in SunBird')
            } else {
                const personalDetailsRegistry: IPersonalDetails = {
                    firstname: sbfirstName_,
                    primaryEmail: sbemail_,
                    surname: sblastName_,
                    username: extractUserName,
                }
                const userRegistry: IUser = {
                    personalDetails: personalDetailsRegistry,
                }
                const sBuserId = response.data.result.userId
                const userRegistryResponse = await axios({
                    ...axiosRequestConfig,
                    data: userRegistry,
                    headers: {
                        Authorization: authorization,
                        wid: sBuserId,
                    },
                    method: 'POST',
                    url: `${createUserRegistryApi}/${sBuserId}`,
                })
                if (userRegistryResponse.data === null) {
                    throw new Error('Not able to create User Registry in Opensaber')
                } else {
                    const sbUserProfileResponse: Partial<ISunbirdbUserResponse> = {
                        email: sbemail_, firstName: sbfirstName_, lastName: sblastName_,
                        userId: sBuserId,
                    }
                    res.send(sbUserProfileResponse)
                }

            }
        }

    } catch (err) {
        logError('ERROR CREATING USER >', err)
        res.status((err && err.response && err.response.status) || 500).send(err)
    }
})
