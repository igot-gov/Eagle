import axios from 'axios'
import { Router } from 'express'
import { axiosRequestConfig } from '../../configs/request.config'
import {
    IUpdateUserRegistry, IUserRegistry,
} from '../../models/profile-details.model'
import {
    ISbUserResponse,
} from '../../models/user.model'
import {
    IUserDetailsResponse,
    IUserGraphProfile,
    IUserGraphProfileResponse,
} from '../../models/user.model'
import { createUserRegistry, getUserRegistry, updateUserRegistry } from '../../service/registry'
import { CONSTANTS } from '../../utils/env'
import { logError } from '../../utils/logger'

import {
    extractAuthorizationFromRequest,
    extractRootOrgNameFromRequest,
    extractUserEmailFromRequest,
    extractUserIdFromRequest,
    extractUserNameFromRequest,
    extractUserTokenFromRequest,
    IAuthorizedRequest,
} from '../../utils/requestExtract'

import { logInfo } from '../../utils/logger'
import {
    extractSbUserTokenContent,
} from '../../utils/sbRequestExtract'
const GENERAL_ERROR_MSG = 'Failed due to unknown reason'

// Update the v1 to v2
const apiEndpoints = {
    create: `${CONSTANTS.SB_EXT_API_BASE}/v1/user/createUser`,
    createSb: `${CONSTANTS.USER_SUNBIRD_DETAILS_API_BASE}/api/user/v1/create`,
    details: `${CONSTANTS.USER_LOCAL_SUNBIRD_DETAILS_API_BASE}/user`,
    detailsSb: `${CONSTANTS.USER_SUNBIRD_DETAILS_API_BASE}/api/user/v1/read`,
    graph: `${CONSTANTS.SB_EXT_API_BASE}/v1/Users`,
    graphV2: `${CONSTANTS.SB_EXT_API_BASE}/v2/Users`,
    searchSb: `${CONSTANTS.USER_SUNBIRD_DETAILS_API_BASE}/api/user/v1/search`,
    updatSb: `${CONSTANTS.USER_SUNBIRD_DETAILS_API_BASE}/api/user/v1/update`,
}
const xAuthUser = `${CONSTANTS.X_AUTH_USER}`
const contentType = `${CONSTANTS.CONTENT_TYPE}`
const contentTypeValue = `${CONSTANTS.CONTENT_TYPE_VALUE}`
const authorizationHeader = `${CONSTANTS.AUTHORIZATION}`
const xAppId = `${CONSTANTS.X_APP_ID}`
const xAppIdVAlue = `${CONSTANTS.X_APP_ID_VALUE}`

export async function getUserDetailsFromApi(userId: string): Promise<IUserDetailsResponse | null> {
    try {
        const res = await axios.get<IUserDetailsResponse>(
            `${apiEndpoints.details}/${userId}`,
            axiosRequestConfig
        )
        return res.data || {}
    } catch (err) {
        return null
    }
}

export async function getSbUserDetailsFromApi(userId: string, auth: string, xT: string, org: string): Promise<ISbUserResponse | null> {
    const xAuth = auth.split(' ')
    try {
        logInfo('-------getSbUserDetailsFromApi-----')
        axios.defaults.headers.common[xAuthUser] = xAuth[1]
        const res = await axios.get<ISbUserResponse>(
            `${apiEndpoints.detailsSb}/${userId}`,
            { headers: { Authorization: xAuth[0] + ' ' + xT, rootOrgName: org } }
        )
        return res.data || {}
    } catch (err) {
        return null
    }
}

export async function getUserDetailsFromGraph(userId: string): Promise<IUserGraphProfile | null> {
    try {
        const res = await axios.get<IUserGraphProfileResponse>(
            `${apiEndpoints.graphV2}/${userId}/Data`,
            axiosRequestConfig
        )
        return res.data.result.response
    } catch (err) {
        return null
    }
}

function manipulateResult(
    detailsResponse: IUserDetailsResponse | null,
    profileResponse: IUserGraphProfile | null,
    defaultName: string,
    defaultEmail: string
) {
    let empNumber = (detailsResponse && detailsResponse.empNumber) || 0
    try {
        // tslint:disable-next-line:ban
        empNumber = parseInt((profileResponse && profileResponse.companyName) || '0', 10)
    } catch (err) {
        logError(err)
    }

    return {
        email:
            (detailsResponse && detailsResponse.email) ||
            (profileResponse && profileResponse.onPremisesUserPrincipalName) ||
            defaultEmail,
        miscellaneous: {
            ...detailsResponse,
            ...profileResponse,
            empNumber,
        },
        name:
            (detailsResponse && detailsResponse.name) ||
            defaultName ||
            (profileResponse && `${profileResponse.givenName} ${profileResponse.surname}`),
    }
}

function manipulateResultV1(
    detailsResponse: ISbUserResponse | null,
    defaultEmail: string
) {
    let empNumber = (detailsResponse && detailsResponse.result.response.id) || 0
    try {
        if (detailsResponse != null) {
            empNumber = detailsResponse.result.response.id
        }
        // tslint:disable-next-line:ban

    } catch (err) {
        logError(err)
    }

    return {
        email:
            (detailsResponse && detailsResponse.result.response.email) ||
            defaultEmail,
        miscellaneous: {
            ...detailsResponse,
            empNumber,
        },
        name:
            (detailsResponse && detailsResponse.result.response.firstName + ' ' + detailsResponse.result.response.lastName),
    }
}

export async function getUserProfile(userId: string, req: IAuthorizedRequest) {
    try {
        const [detailsResponse, profileResponse] = await Promise.all([
            getUserDetailsFromApi(userId),
            getUserDetailsFromGraph(userId),
        ])

        return manipulateResult(
            detailsResponse,
            profileResponse,
            extractUserNameFromRequest(req),
            extractUserEmailFromRequest(req)
        )
    } catch (err) {
        return {}
    }
}

export async function getSbUserProfile(userId: string, req: IAuthorizedRequest) {
    const authorization = extractAuthorizationFromRequest(req)
    const xAuthenticatedUserToken = extractUserTokenFromRequest(req)
    const rootOrgName = extractRootOrgNameFromRequest(req)
    try {
        const [detailsResponse] = await Promise.all([
            getSbUserDetailsFromApi(userId, authorization, xAuthenticatedUserToken, rootOrgName),
        ])

        return manipulateResultV1(
            detailsResponse,
            extractUserNameFromRequest(req)
        )
    } catch (err) {
        return {}
    }
}

export const sbProfileApi = Router()

sbProfileApi.get('/empDB', async (req, res) => {
    try {
        const userId = extractUserIdFromRequest(req)
        const response = await getUserDetailsFromApi(userId)
        res.json(response)
    } catch (err) {
        res.status((err && err.response && err.response.status) || 500).send(
            (err && err.response && err.response.data) || {
                error: GENERAL_ERROR_MSG,
            }
        )
    }
})
sbProfileApi.get('/graph', async (req, res) => {
    try {
        const userId = extractUserIdFromRequest(req)
        const response = await getUserDetailsFromGraph(userId)
        res.json(response)
    } catch (err) {
        res.status((err && err.response && err.response.status) || 500).send(
            (err && err.response && err.response.data) || {
                error: GENERAL_ERROR_MSG,
            }
        )
    }
})

sbProfileApi.get('/graph/photo/:userEmail', async (req, res) => {
    try {
        const userEmail = req.params.userEmail
        const url = `${apiEndpoints.graph}/${userEmail}/Photo`
        const response = await axios.get<IUserGraphProfileResponse>(url, axiosRequestConfig)
        res.json(response.data)
    } catch (err) {
        logError('ERROR FETCHING USER IMAGE:', err)
        res.status((err && err.response && err.response.status) || 500).send(
            (err && err.response && err.response.data) || {
                error: GENERAL_ERROR_MSG,
            }
        )
    }
})

sbProfileApi.get('/', async (req, res) => {
    try {
        const userId = extractUserIdFromRequest(req)
        const response = await getSbUserProfile(userId, req)
        res.json(response)
    } catch (err) {
        res.status((err && err.response && err.response.status) || 500).send(
            (err && err.response && err.response.data) || {
                error: GENERAL_ERROR_MSG,
            }
        )
    }
})

sbProfileApi.post('/search', async (req, res) => {

    try {
        const tokenContent = extractSbUserTokenContent(req)
        const authorization = extractAuthorizationFromRequest(req)
        const xAuthenticatedUserToken = extractUserTokenFromRequest(req)
        const xAuth = authorization.split(' ')
        if (tokenContent) {
            axios.defaults.headers.common[xAuthUser] = xAuth[1]
            axios.defaults.headers.common[authorizationHeader] = xAuth[0] + ' ' + xAuthenticatedUserToken
            axios.defaults.headers.common[contentType] = contentTypeValue
            axios.defaults.headers.common[xAppId] = xAppIdVAlue
            const getuserName: string = req.body.userName

            const searchresponse = await axios({
                ...axiosRequestConfig,
                data: { request: { query: '', filters: { userName: getuserName.toLowerCase() } } },
                method: 'POST',
                url: apiEndpoints.searchSb,
            })

            res.send(searchresponse.data)
            return
        }
        res.status(404).send('')
    } catch (err) {
        logError('err in new user acceptance search >', err)
        res.status((err && err.response && err.response.status) || 500).send(
            (err && err.response && err.response.data) || {
                error: GENERAL_ERROR_MSG,
            }
        )
    }
})

sbProfileApi.post('/', async (req, res) => {

    try {
        const tokenContent = extractSbUserTokenContent(req)
        const authorization = extractAuthorizationFromRequest(req)
        const xAuthenticatedUserToken = extractUserTokenFromRequest(req)
        const xAuth = authorization.split(' ')

        axios.defaults.headers.common[xAuthUser] = xAuth[1]
        axios.defaults.headers.common[authorizationHeader] = xAuth[0] + ' ' + xAuthenticatedUserToken
        axios.defaults.headers.common[contentType] = contentTypeValue
        axios.defaults.headers.common[xAppId] = xAppIdVAlue
        if (tokenContent) {
            const response = await axios({
                ...axiosRequestConfig,
                data: { request: req.body },
                method: 'POST',
                url: apiEndpoints.createSb,
            })
            const responseData = response.data
            const createSbResponseData = JSON.parse(JSON.stringify(responseData))
            let reqresponse
            if (createSbResponseData.result.response === 'SUCCESS') {
                axios.defaults.headers.common[xAuthUser] = xAuth[1]
                axios.defaults.headers.common[authorizationHeader] = xAuth[0] + ' ' + xAuthenticatedUserToken
                axios.defaults.headers.common[contentType] = contentTypeValue
                axios.defaults.headers.common[xAppId] = xAppIdVAlue
                const getuserName: string = req.body.userName

                const searchresponse = await axios({
                    ...axiosRequestConfig,
                    data: { request: { query: '', filters: { userName: getuserName.toLowerCase() } } },
                    method: 'POST',
                    url: apiEndpoints.searchSb,
                })
                logInfo('-------Registry-----' + searchresponse.data.result.count)
                let id = ''
                let firstName = ''
                let lastName = ''
                let email = ''
                let phone = 0
                if (searchresponse.data.result.response.count > 0) {
                    id = searchresponse.data.result.response.content[0].id
                    firstName = searchresponse.data.result.response.content[0].firstName
                    lastName = searchresponse.data.result.response.content[0].lastName
                    email = searchresponse.data.result.response.content[0].email
                    const sphone = req.body.phone
                    phone = Number(sphone)
                    logInfo('-------If AfterData for Registry-----' + id + '   ' + phone)
                    const reqToRegistry: IUserRegistry = {
                        firstname: firstName,
                        mobile: phone, primaryEmail: email, surname: lastName,
                        userId: id,
                    }
                    reqresponse = await Promise.all([
                        createUserRegistry(reqToRegistry),
                    ])
                } else {
                    const userId: string = null || ''
                    logInfo('-------AfterData for Registry-----' + id)
                    const reqToRegistry: IUserRegistry = {
                        firstname: firstName,
                        mobile: phone, primaryEmail: email, surname: lastName,
                        userId,
                    }
                    reqresponse = await Promise.all([
                        createUserRegistry(reqToRegistry),
                    ])
                }

            }

            res.send(reqresponse)
            return
        }
        res.status(404).send('')
    } catch (err) {
        logError('err in new user acceptance >', err)
        res.status((err && err.response && err.response.status) || 500).send(
            (err && err.response && err.response.data) || {
                error: GENERAL_ERROR_MSG,
            }
        )
    }
})

sbProfileApi.patch('/', async (req, res) => {
    try {
        const tokenContent = extractSbUserTokenContent(req)
        const authorization = extractAuthorizationFromRequest(req)
        const xAuthenticatedUserToken = extractUserTokenFromRequest(req)
        const xAuth = authorization.split(' ')
        let response
        let updatSbResponse
        let updateresponse
        if (tokenContent) {

            response = await axios({
                ...axiosRequestConfig,
                data: { request: req.body },
                headers: {
                    Authorization: xAuth[0] + ' ' + xAuthenticatedUserToken,
                    'Content-Type': 'application/json',
                    'X-App-Id': 'sunbird.portal',
                    'X-Authenticated-User-Token': xAuth[1],
                },
                method: 'PATCH',
                url: apiEndpoints.updatSb,
            })
            updatSbResponse = JSON.parse(JSON.stringify(response.data))
            const userId = req.body.userId

            if (updatSbResponse.result.response === 'SUCCESS') {
                const responseRegistryData: IUpdateUserRegistry[] = await CheckUserRegistryExists(userId)
                let userProfileObj
                if (responseRegistryData && responseRegistryData.length > 0) {
                    userProfileObj = responseRegistryData[0]

                    const updatereqToRegistry = JSON.parse(JSON.stringify(req.body))
                    logInfo('-------Data for Registry-----' + updatereqToRegistry.firstName)

                    userProfileObj.firstname = updatereqToRegistry.firstName
                    userProfileObj.surname = updatereqToRegistry.lastName
                    userProfileObj.gender = updatereqToRegistry.gender
                    userProfileObj.dob = updatereqToRegistry.dob
                    userProfileObj.language = updatereqToRegistry.language

                    updateresponse = await updateUserRegistry(userProfileObj)
                }
            }
        }

        res.status(200).send(updateresponse)
    } catch (err) {
        logError('err in new user acceptance >', err)
        res.status((err && err.response && err.response.status) || 500).send(
            (err && err.response && err.response.data) || {
                error: GENERAL_ERROR_MSG,
            }
        )
    }
})

// tslint:disable-next-line: no-any
export async function CheckUserRegistryExists(userId: string): Promise<IUpdateUserRegistry[]> {
    return new Promise(async (resolve, reject) => {
        logInfo(`CheckUserRegistryExists: ${userId}`)
        const response = await getUserRegistry(userId)
        const jsonResponse = JSON.parse(JSON.stringify(response))
        if (response && response.params.status === 'UNSUCCESSFUL') {
            logError(' CheckUserRegistryExists: Response from open saber is UNSUCCESSFUL')
            reject(response)
        } else {
            logInfo('Reponse from open saber /serch : ', jsonResponse.result.PersonalDetails[0].firstname)
            if (response && response.result.PersonalDetails && response.result.PersonalDetails.length) {
                resolve(response.result.PersonalDetails)
            } else {
                logInfo(' CheckUserRegistryExists: user registry not yet present')
                resolve(response.result.PersonalDetails)
            }
        }
    })
}
