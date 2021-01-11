import axios from 'axios'
import { Router } from 'express'
import { axiosRequestConfig } from '../../configs/request.config'
import {
    IPersonalDetails,
    IRootObject,
    ISBAcademic,
    ISBPersonalDetails,
    ISBProfessionalDetail,
    ISBUser,
    ISBUserProfile,
    IUserProfile,
} from '../../models/profile-details.model'
import {
    ISbUserResponse, ISunbirdbUserResponse,
} from '../../models/user.model'
import {
    IUserDetailsResponse,
    IUserGraphProfile,
    IUserGraphProfileResponse,
} from '../../models/user.model'
import { createUserRegistry, getUserRegistry, readUserRegistry, updateUserRegistry } from '../../service/registry'
import { CONSTANTS } from '../../utils/env'
import { logError } from '../../utils/logger'

import {
    extractAuthorizationFromRequest,
    extractRootOrgFromRequest,
    extractUserEmailFromRequest,
    extractUserIdFromRequest,
    extractUserNameFromRequest,
    extractUserTokenFromRequest,
    IAuthorizedRequest,
} from '../../utils/requestExtract'

import { logInfo } from '../../utils/logger'
import {
    extractSbUserIdFromRequest, extractSbUserTokenContent
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

export async function getSbUserDetailsFromApi(userId: string, auth: string,
                                              xT: string, req: IAuthorizedRequest): Promise<Partial<ISBUserProfile> | null> {
    const xAuth = auth.split(' ')
    try {
        logInfo('-------Enter-----')
        axios.defaults.headers.common[xAuthUser] = xAuth[1]
        const res = await axios.get<ISbUserResponse>(
            `${apiEndpoints.detailsSb}/${userId}`,
            { headers: { Authorization: xAuth[0] + ' ' + xT } }
        )
        logInfo('-------res-----' + JSON.stringify(res))
        const getUserResponse = JSON.parse(JSON.stringify(res))
        logInfo('-------getSbUserDetailsFromApi-----' + getUserResponse.params.status)
        let userProfileSb: Partial<ISBUserProfile> = {} as Partial<ISBUserProfile>
        if (res.data.params.status === 'success') {
            const responseRegistryData: IUserProfile[] = await getUserRegistry(userId)
            let userProfileObj
            if (responseRegistryData && responseRegistryData.length > 0) {
                userProfileObj = responseRegistryData[0]
                userProfileObj.id = req.body.userId
                await updateUserRegistry(userProfileObj, req.body)
                await delay(200)
                const responseFromRead = await readUserRegistry(userProfileObj.osid)
                const getupdateresponse = JSON.parse(JSON.stringify(responseFromRead))
                const personaldetails = getupdateresponse.result.UserProfile.personalDetails
                const countryCodeSb: string = personaldetails.countryCode
                const domicileMediumSb: string = personaldetails.domicileMedium
                const languageSb: string[] = personaldetails.language
                const middlenameSb: string = personaldetails.middlename
                const mobileSb: number = personaldetails.mobile
                const nationalitySb: string = personaldetails.nationality
                const officialEmailSb: string = personaldetails.officialEmail
                const personalEmailSb: string = personaldetails.personalEmail
                const pincodeSb: string = personaldetails.pincode
                const postalAddressSb: string = personaldetails.postalAddress
                const primaryEmailSb: string = personaldetails.primaryEmail
                const telephoneSb: string = personaldetails.telephone
                const lastNameSb: string = personaldetails.surname
                const genderSb: string = personaldetails.gender
                const firstNameSb: string = personaldetails.firstname
                const dobSb: string = personaldetails.dob
                const personalDetailsRegistry: Partial<ISBPersonalDetails> = {
                    countryCode: countryCodeSb, dob: dobSb, domicileMedium: domicileMediumSb,
                    firstName: firstNameSb, gender: genderSb, language: languageSb,
                    lastName: lastNameSb, middlename: middlenameSb, mobile: mobileSb,
                    nationality: nationalitySb, officialEmail: officialEmailSb,
                    personalEmail: personalEmailSb, pincode: pincodeSb,
                    postalAddress: postalAddressSb, primaryEmail: primaryEmailSb,
                    telephone: telephoneSb,
                }
                const acadamicsSb: Partial<ISBAcademic[]> = getupdateresponse.result.UserProfile.academics
                const professionalDetailSb: Partial<ISBProfessionalDetail[]> = getupdateresponse.result.UserProfile.professionalDetails
                userProfileSb = {
                    academics: acadamicsSb,
                    personalDetails: personalDetailsRegistry,
                    professionalDetails: professionalDetailSb,
                }

            }
        }
        return userProfileSb || {}
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
    try {
        const [detailsResponse] = await Promise.all([
            getSbUserDetailsFromApi(userId, authorization, xAuthenticatedUserToken, req),
        ])

        return detailsResponse
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
        logInfo('-------Enter-----')
        const tokenContent = extractSbUserTokenContent(req)
        const authorization = extractAuthorizationFromRequest(req)
        const xAuthenticatedUserToken = extractUserTokenFromRequest(req)
        const xAuth = authorization.split(' ')
        if (tokenContent) {
            const userId: string = extractSbUserIdFromRequest(req)
            axios.defaults.headers.common[xAuthUser] = xAuth[1]
            axios.defaults.headers.common[authorizationHeader] = xAuth[0] + ' ' + xAuthenticatedUserToken
            const userResponse = await axios.get<ISbUserResponse>(
                `${apiEndpoints.detailsSb}/${userId}`
            )
            if (userResponse.data.params.status === 'success') {
                const responseRegistryData: IUserProfile[] = await CheckUserRegistryExists(userId)
                let userProfileObj
                if (responseRegistryData && responseRegistryData.length > 0) {
                    userProfileObj = responseRegistryData[0]
                    userProfileObj.id = req.body.userId
                    const personaldetails = userProfileObj.personalDetails
                    const acadamicsSb = userProfileObj.academics
                    const interestsSb = userProfileObj.interests
                    const skillsSb = userProfileObj.skills
                    const employmentDetailsSb = userProfileObj.employmentDetails
                    const professionalDetailSb = userProfileObj.professionalDetails
                    const userProfileSb: Partial<IUserProfile> = {
                        academics: acadamicsSb,
                        employmentDetails: employmentDetailsSb,
                        interests: interestsSb,
                        personalDetails: personaldetails,
                        professionalDetails: professionalDetailSb,
                        skills: skillsSb,
                    }
                    res.status(200).send(userProfileSb)
                }
            }
        }
        return
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
            const reuestJson = JSON.parse(JSON.stringify(req.body))
            let searchresponse

            if (reuestJson.hasOwnProperty('firstName')) {
                searchresponse = await axios({
                    ...axiosRequestConfig,
                    data: { request: { query: '', filters: req.body } },
                    method: 'POST',
                    url: apiEndpoints.searchSb,
                })
                res.send(searchresponse.data)
            } else if (reuestJson.hasOwnProperty('userName')) {
                const getuserName: string = req.body.userName
                searchresponse = await axios({
                    ...axiosRequestConfig,
                    data: { request: { query: '', filters: { userName: getuserName.toLowerCase() } } },
                    method: 'POST',
                    url: apiEndpoints.searchSb,
                })
                res.send(searchresponse.data)
            }

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
        const sbemail_ = req.body.personalDetails.email
        const sbemailVerified_: boolean = req.body.personalDetails.emailVerified
        const sbfirstName_ = req.body.personalDetails.firstName
        const sblastName_ = req.body.personalDetails.lastName
        const sbchannel_ = extractRootOrgFromRequest(req)
        const sbuserName_ = req.body.personalDetails.email
        const sbUserProfile: Partial<ISBUser> = {
            channel: sbchannel_, email: sbemail_, emailVerified: sbemailVerified_, firstName: sbfirstName_,
            lastName: sblastName_, userName: sbuserName_,
        }

        if (tokenContent) {
            logInfo('------------' + JSON.stringify(sbUserProfile))
            const requestJson = JSON.parse(JSON.stringify(sbUserProfile))
            const response = await axios({
                ...axiosRequestConfig,
                data: { request: requestJson },
                method: 'POST',
                url: apiEndpoints.createSb,
            })
            await delay(200)
            const responseData = response.data
            const createSbResponseData = JSON.parse(JSON.stringify(responseData))
            let reqresponse
            let sbUserProfileResponse: Partial<ISunbirdbUserResponse> = {} as Partial<ISunbirdbUserResponse>
            if (createSbResponseData.result.response === 'SUCCESS') {
                axios.defaults.headers.common[xAuthUser] = xAuth[1]
                axios.defaults.headers.common[authorizationHeader] = xAuth[0] + ' ' + xAuthenticatedUserToken
                axios.defaults.headers.common[contentType] = contentTypeValue
                axios.defaults.headers.common[xAppId] = xAppIdVAlue
                logInfo('-------USer-----' + sbuserName_)
                const searchresponse = await axios({
                    ...axiosRequestConfig,
                    data: { request: { query: '', filters: { firstName: sbfirstName_, lastName: sblastName_ } } },
                    method: 'POST',
                    url: apiEndpoints.searchSb,
                })
                await delay(300)
                let id_ = ''
                let firstname_ = ''
                let surname_ = ''
                let primaryEmail_ = ''
                const middlename_ = ''
                const nationality_ = ''
                const domicileMedium_ = ''
                const knownLanguages_: string[] = []
                const countryCode_ = ''
                const mobile_: number = null || 0
                const telephone_ = ''
                const officialEmail_ = ''
                const personalEmail_ = ''
                const postalAddress_ = ''
                const pincode_ = ''
                const osCreatedAt_ = ''
                const osUpdatedAt_ = ''
                const osCreatedBy_ = ''
                const osUpdatedBy_ = ''
                const _osroot_ = ''
                const type_ = 'personalDetails'
                logInfo('-------Serch result-----' + JSON.stringify(searchresponse.data))
                if (searchresponse.data.result.response.count > 0) {
                    id_ = searchresponse.data.result.response.content[0].id
                    firstname_ = searchresponse.data.result.response.content[0].firstName
                    surname_ = searchresponse.data.result.response.content[0].lastName
                    primaryEmail_ = sbemail_
                    const photo_ = ''
                    const userId_ = id_
                    const userosCreatedAt_ = ''
                    const userosUpdatedAt_ = ''
                    const userosCreatedBy_ = ''
                    const userosUpdatedBy_ = ''
                    const userType_ = 'UserProfile'
                    logInfo('-------If AfterData for Registry-----' + id_)
                    const personalDetailsRegistry: Partial<IPersonalDetails> = {
                        '@type': type_, _osroot: _osroot_,
                        countryCode: countryCode_, domicileMedium: domicileMedium_,
                        firstname: firstname_, knownLanguages: knownLanguages_
                        , middlename: middlename_, mobile: mobile_,
                        nationality: nationality_, officialEmail: officialEmail_,
                        osCreatedAt: osCreatedAt_, osCreatedBy: osCreatedBy_, osUpdatedAt: osUpdatedAt_,
                        osUpdatedBy: osUpdatedBy_, personalEmail: personalEmail_, pincode: pincode_,
                        postalAddress: postalAddress_,
                        primaryEmail: primaryEmail_,
                        surname: surname_,
                        telephone: telephone_,
                    }
                    const userProfileRegistry: Partial<IUserProfile> = {
                        '@id': userId_, '@type': userType_,
                        id: id_,
                        osCreatedAt: userosCreatedAt_, osCreatedBy: userosCreatedBy_, osUpdatedAt: userosUpdatedAt_,
                        osUpdatedBy: userosUpdatedBy_,
                        personalDetails: personalDetailsRegistry, photo: photo_,
                        userId: id_,
                    }
                    logInfo('-------Before AfterData for Registry-----' + id_)
                    reqresponse = await createUserRegistry(userProfileRegistry)

                    await delay(300)
                    if (reqresponse.params.status === 'SUCCESSFUL') {
                        logInfo('-------After AfterData for Registry-----' + id_)
                        sbUserProfileResponse = {
                            email: sbemail_, firstName: firstname_, lastName: firstname_,
                            userId: id_,
                        }
                    }

                } else {

                    const researchresponse = await axios({
                        ...axiosRequestConfig,
                        data: { request: { query: '', filters: { firstName: sbfirstName_, lastName: sblastName_ } } },
                        method: 'POST',
                        url: apiEndpoints.searchSb,
                    })
                    await delay(200)
                    id_ = researchresponse.data.result.response.content[0].id

                    firstname_ = researchresponse.data.result.response.content[0].firstName
                    surname_ = researchresponse.data.result.response.content[0].lastName

                    primaryEmail_ = researchresponse.data.result.response.content[0].email
                    const photo_ = ''
                    const userId_ = id_
                    const userosCreatedAt_ = ''
                    const userosUpdatedAt_ = ''
                    const userosCreatedBy_ = ''
                    const userosUpdatedBy_ = ''
                    const userType_ = 'UserProfile'
                    logInfo('-------If AfterData for Registry-----' + id_)
                    const personalDetailsRegistry: Partial<IPersonalDetails> = {
                        '@type': type_, _osroot: _osroot_,
                        countryCode: countryCode_, domicileMedium: domicileMedium_,
                        firstname: firstname_, knownLanguages: knownLanguages_
                        , middlename: middlename_, mobile: mobile_,
                        nationality: nationality_, officialEmail: officialEmail_,
                        osCreatedAt: osCreatedAt_, osCreatedBy: osCreatedBy_, osUpdatedAt: osUpdatedAt_,
                        osUpdatedBy: osUpdatedBy_, personalEmail: personalEmail_, pincode: pincode_,
                        postalAddress: postalAddress_,
                        primaryEmail: primaryEmail_,
                        surname: surname_,
                        telephone: telephone_,
                    }
                    const userProfileRegistry: Partial<IUserProfile> = {
                        '@id': userId_, '@type': userType_,
                        id: id_,
                        osCreatedAt: userosCreatedAt_, osCreatedBy: userosCreatedBy_, osUpdatedAt: userosUpdatedAt_,
                        osUpdatedBy: userosUpdatedBy_,
                        personalDetails: personalDetailsRegistry, photo: photo_,
                        userId: id_,
                    }
                    logInfo('-------Before AfterData for Registry-----' + id_)
                    reqresponse = await createUserRegistry(userProfileRegistry)

                    await delay(300)
                    if (reqresponse.params.status === 'SUCCESSFUL') {
                        logInfo('-------After AfterData for Registry-----' + id_)
                        sbUserProfileResponse = {
                            email: sbemail_, firstName: firstname_, lastName: firstname_,
                            userId: id_,
                        }
                    }
                }

            }
            res.send(sbUserProfileResponse)
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

sbProfileApi.post('/createUser', async (req, res) => {

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
            logInfo('-------Registry-----' + JSON.stringify(req.body.personalDetails))

            res.send(req.body.personalDetails)
            return
        }
        res.status(404).send('')
    } catch (err) {
        logError('err in new user acceptance =', err)
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
            const userId: string = extractSbUserIdFromRequest(req)
            axios.defaults.headers.common[xAuthUser] = xAuth[1]
            axios.defaults.headers.common[authorizationHeader] = xAuth[0] + ' ' + xAuthenticatedUserToken
            const userResponse = await axios.get<ISbUserResponse>(
                `${apiEndpoints.detailsSb}/${userId}`
            )
            const channel: string = extractRootOrgFromRequest(req)
            const getUserchannel: string = userResponse.data.result.response.channel
            if (channel.toLowerCase() === getUserchannel.toLowerCase()) {
                const responseRegistryData: IUserProfile[] = await CheckUserRegistryExists(userId)
                let userProfileObj
                if (responseRegistryData && responseRegistryData.length > 0) {
                    userProfileObj = responseRegistryData[0]
                    userProfileObj.id = req.body.userId
                    const updateResponseData: IRootObject = await updateUserRepo(req, userProfileObj)

                    if (updateResponseData.params.status === 'SUCCESSFUL') {
                        const updatedresponseRegistryData: IUserProfile[] = await CheckUserRegistryExists(userId)
                        await delay(900)
                        let updateduserProfileObj
                        if (updatedresponseRegistryData && updatedresponseRegistryData.length > 0) {
                            updateduserProfileObj = updatedresponseRegistryData[0]
                            const personaldetailsSb = updateduserProfileObj.personalDetails
                            const acadamicsSb = updateduserProfileObj.academics
                            const interestsSb = updateduserProfileObj.interests
                            const skillsSb = updateduserProfileObj.skills
                            const employmentDetailsSb = updateduserProfileObj.employmentDetails
                            const professionalDetailSb = updateduserProfileObj.professionalDetails
                            const userProfileSb: Partial<IUserProfile> = {
                                academics: acadamicsSb,
                                employmentDetails: employmentDetailsSb,
                                interests: interestsSb,
                                personalDetails: personaldetailsSb,
                                professionalDetails: professionalDetailSb,
                                skills: skillsSb,
                            }
                            res.status(200).send(userProfileSb)
                        }
                    }
                }
            } else {
                const updateUserresponse = await axios({
                    ...axiosRequestConfig,
                    data: { request: req.body.personalDetails },
                    headers: {
                        Authorization: xAuth[0] + ' ' + xAuthenticatedUserToken,
                        'Content-Type': 'application/json',
                        'X-App-Id': 'sunbird.portal',
                        'X-Authenticated-User-Token': xAuth[1],
                    },
                    method: 'PATCH',
                    url: apiEndpoints.updatSb,
                })
                const updatSbResponse = JSON.parse(JSON.stringify(updateUserresponse.data))
                if (updatSbResponse.result.response === 'SUCCESS') {
                    const responseRegistryData: IUserProfile[] = await CheckUserRegistryExists(userId)
                    let userProfileObj
                    if (responseRegistryData && responseRegistryData.length > 0) {
                        userProfileObj = responseRegistryData[0]
                        userProfileObj.id = req.body.userId
                        const updateUserResponse = await updateUserRegistry(userProfileObj, req.body)
                        await delay(300)
                        if (updateUserResponse.params.status === 'SUCCESSFUL') {
                            const updatedresponseRegistryData: IUserProfile[] = await CheckUserRegistryExists(userId)
                            let updateduserProfileObj
                            if (updatedresponseRegistryData && updatedresponseRegistryData.length > 0) {
                                updateduserProfileObj = updatedresponseRegistryData[0]
                                const personaldetailsSb = updateduserProfileObj.personalDetails
                                const acadamicsSb = updateduserProfileObj.academics
                                const interestsSb = updateduserProfileObj.interests
                                const skillsSb = updateduserProfileObj.skills
                                const employmentDetailsSb = updateduserProfileObj.employmentDetails
                                const professionalDetailSb = updateduserProfileObj.professionalDetails
                                const userProfileSb: Partial<IUserProfile> = {
                                    academics: acadamicsSb,
                                    employmentDetails: employmentDetailsSb,
                                    interests: interestsSb,
                                    personalDetails: personaldetailsSb,
                                    professionalDetails: professionalDetailSb,
                                    skills: skillsSb,
                                }
                                res.status(200).send(userProfileSb)
                            }
                        }

                    }
                }
            }
        }

        return
    } catch (err) {
        logError('err in new user acceptance--- >', err)
        res.status((err && err.response && err.response.status) || 500).send(
            (err && err.response && err.response.data) || {
                error: GENERAL_ERROR_MSG,
            }
        )
    }
})

// tslint:disable-next-line: no-any
export function CheckUserRegistryExists(userId: string): Promise<IUserProfile[]> {
    return new Promise(async (resolve, reject) => {
        logInfo(`CheckUserRegistryExists: ${userId}`)
        const response = await getUserRegistry(userId)
        await delay(1300)
        logInfo('-------Response Registry-----')
        if (response && response.params.status === 'UNSUCCESSFUL') {
            logError(' CheckUserRegistryExists: Response from open saber is UNSUCCESSFUL')
            reject(response)
        } else {
            if (response && response.result.UserProfile) {
                resolve(response.result.UserProfile)
            } else {
                logInfo(' CheckUserRegistryExists: user registry not yet present')
                resolve(response.result.UserProfile)
            }
        }
    })
}

export async function updateUserRepo(userReq: IAuthorizedRequest, userProfileObj: IUserProfile): Promise<IRootObject> {
    return new Promise(async (resolve, reject) => {
        const updateResponseData = await updateUserRegistry(userProfileObj, userReq.body)
        logInfo('-------Response Registry-----')
        if (updateResponseData && updateResponseData.params.status === 'UNSUCCESSFUL') {
            logError(' CheckUserRegistryExists: Response from open saber is UNSUCCESSFUL')
            reject(updateResponseData)
        } else {
            if (updateResponseData && updateResponseData.params) {
                resolve(updateResponseData)
            } else {
                logInfo(' CheckUserRegistryExists: user registry not yet present')
                resolve(updateResponseData)
            }
        }
    })
}

function delay(ms: number) {
    return new Promise((resolve) => setTimeout(resolve, ms))
}
