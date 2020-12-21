import axios from 'axios'
import { Router } from 'express'
import { axiosRequestConfig } from '../../configs/request.config'
import {
    IUserRegistry,
} from '../../models/profile-details.model'
import {
    IUserDetailsResponse,
    IUserGraphProfile,
    IUserGraphProfileResponse,
} from '../../models/user.model'
import { createUserRegistry } from '../../service/registry'
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

import {
    extractSbUserTokenContent,
} from '../../utils/sbRequestExtract'

const GENERAL_ERROR_MSG = 'Failed due to unknown reason'

// Update the v1 to v2
const apiEndpoints = {
    create: `${CONSTANTS.SB_EXT_API_BASE}/v1/user/createUser`,
    createSb: `${CONSTANTS.CREATE_USER_SUNBIRD_DETAILS_API_BASE}/api/user/v1/create`,
    details: `${CONSTANTS.USER_SUNBIRD_DETAILS_API_BASE}/user`,
    graph: `${CONSTANTS.SB_EXT_API_BASE}/v1/Users`,
    graphV2: `${CONSTANTS.SB_EXT_API_BASE}/v2/Users`,
}

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

export async function getSbUserDetailsFromApi(userId: string, auth: string, xT: string, org: string): Promise<IUserDetailsResponse | null> {
    const xAuth = auth.split(' ')
    try {
        const res = await axios.get<IUserDetailsResponse>(
            `${apiEndpoints.details}/${userId}`,
            { headers: { 'X-Authenticated-User-Token': xAuth[1], Authorization: xAuth[0] + ' ' + xT, rootOrgName: org } }
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
    detailsResponse: IUserDetailsResponse | null,
    defaultEmail: string
) {
    let empNumber = (detailsResponse && detailsResponse.empNumber) || 0
    try {
        if (detailsResponse != null) {
            empNumber = detailsResponse.empNumber
        }
        // tslint:disable-next-line:ban

    } catch (err) {
        logError(err)
    }

    return {
        email:
            (detailsResponse && detailsResponse.email) ||
            defaultEmail,
        miscellaneous: {
            ...detailsResponse,
            empNumber,
        },
        name:
            (detailsResponse && detailsResponse.name),
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

sbProfileApi.patch('/', async (req, res) => {

    try {
        const tokenContent = extractSbUserTokenContent(req)
        const authorization = extractAuthorizationFromRequest(req)
        const xAuthenticatedUserToken = extractUserTokenFromRequest(req)
        const xAuth = authorization.split(' ')
        if (tokenContent) {

            const response = await axios({
                ...axiosRequestConfig,
                data: { request: req.body },
                headers: {
                    Authorization: xAuth[0] + ' ' + xAuthenticatedUserToken,
                    'Content-Type': 'application/json',
                    'X-App-Id': 'sunbird.portal',
                    'X-Authenticated-User-Token': xAuth[1],
                },
                method: 'POST',
                url: apiEndpoints.createSb,
            })
            const responseData = response.data
            const contact = JSON.parse(JSON.stringify(responseData))
            let reqresponse
            if (contact.result.response === 'SUCCESS') {
                const firstName: string = req.body.firstName
                const lastName: string = req.body.lastName
                const email: string = req.body.email
                const sphone: string = req.body.phone
                const phone: number = Number(sphone)
                const reqToRegistry: IUserRegistry = { firstname: firstName, surname: lastName, mobile: phone, primaryEmail: email }
                reqresponse = await Promise.all([
                    createUserRegistry(reqToRegistry),
                ])
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
