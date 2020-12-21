import axios from 'axios'
import { axiosRequestConfig, axiosRequestConfigLong } from '../configs/request.config'
import {
    IUserRegistry,
    IUserRegistryReadRequest,
    IUserRegistryRequest,
    IUserRegistrySearchRequest,
    IUserRegistryUpdateRequest,
} from '../models/profile-details.model'
import { CONSTANTS } from '../utils/env'
import { logError, logInfo, logObject } from '../utils/logger'

const apiEndpoints = {
    create: `${CONSTANTS.OPEN_SABER_USER_REGISTRY_BASE}/add`,
    read: `${CONSTANTS.OPEN_SABER_USER_REGISTRY_BASE}/read`,
    search: `${CONSTANTS.OPEN_SABER_USER_REGISTRY_BASE}/search`,
    update: `${CONSTANTS.OPEN_SABER_USER_REGISTRY_BASE}/update`,
}

export async function createUserRegistry(
    // tslint:disable-next-line: no-any
    data: IUserRegistry
    // tslint:disable-next-line: no-any
): Promise<any> {
    try {
        const requestToUserRegistry: IUserRegistryRequest = {
            ets: '11234',
            id: 'open-saber.registry.create',
            // tslint:disable-next-line: object-literal-sort-keys
            params: {
                did: '',
                key: '',
                msgid: '',
            },
            request: {
                PersonalDetails: {
                    ...data,
                },
            },
            ver: '1.0',
        }
        const myJSON = JSON.stringify(requestToUserRegistry)
        logInfo('-------Data for Registry-----')
        const res = await axios({
            ...axiosRequestConfig,
            data: myJSON,
            headers: {
                // tslint:disable-next-line: no-duplicate-string
                'Content-Type': 'application/json',
            },
            method: 'POST',
            url: apiEndpoints.create,
        })
        process.stdout.write('hello: ' + myJSON)
        return res.data || {}
    } catch (err) {
        logError('Request to open saber /add failed')
        logInfo(err)
        throw err
    }
}

export async function getUserRegistry(
    userId: string
    // tslint:disable-next-line: no-any
): Promise<any> {
    try {
        const requestToUserRegistry: IUserRegistrySearchRequest = {
            id: 'open-saber.registry.search',
            // tslint:disable-next-line: object-literal-sort-keys
            ets: '11234',
            ver: '1.0',
            params: {
                did: '',
                key: '',
                msgid: '',
            },
            request: {

                entityType: ['UserProfile'],
                filters: {
                    // tslint:disable-next-line: object-literal-key-quotes
                    id: { 'eq': userId },
                },
            },
        }
        const res = await axios({
            ...axiosRequestConfig,
            data: JSON.stringify(requestToUserRegistry),
            headers: {
                'Content-Type': 'application/json',
            },
            method: 'POST',
            url: apiEndpoints.search,
        })
        return res.data || {}
    } catch (err) {
        logError('Request to open saber /search failed')
        throw err
    }
}

export async function readUserRegistry(
    osid: string
    // tslint:disable-next-line: no-any
): Promise<any> {
    try {
        const requestToUserRegistry: IUserRegistryReadRequest = {
            id: 'open-saber.registry.read',
            ver: '1.0',
            // tslint:disable-next-line: object-literal-sort-keys
            ets: '11234',
            params: {
                did: '',
                key: '',
                msgid: '',
            },
            request: {
                UserProfile: {
                    osid,
                },
                includeSignatures: true,

            },
        }
        const res = await axios({
            ...axiosRequestConfig,
            data: JSON.stringify(requestToUserRegistry),
            headers: {
                'Content-Type': 'application/json',
            },
            method: 'POST',
            url: apiEndpoints.read,
        })
        return res.data || {}
    } catch (err) {
        logError('Request to open saber /read failed')
        throw err
    }
}

export async function updateUserRegistry(
    // tslint:disable-next-line: no-any
    data: any, userId: any, userProfileObj: any
    // tslint:disable-next-line: no-any
): Promise<any> {
    try {
        const dataWithOsid = {
            photo: data.photo,
            // tslint:disable-next-line: object-literal-sort-keys
            personalDetails: {
                // tslint:disable-next-line: object-literal-sort-keys
                osid: userProfileObj.personalDetails.osid,
                ...data.personalDetails,
            },
            // tslint:disable-next-line: object-literal-sort-keys
            employmentDetails: {
                osid: userProfileObj.employmentDetails.osid,
                ...data.employmentDetails,
            },
            skills: {
                osid: userProfileObj.skills.osid,
                ...data.skills,
            },
            // tslint:disable-next-line: object-literal-sort-keys
            interests: {
                osid: userProfileObj.interests.osid,
                ...data.interests,
            },
            academics: data.academics,
            competencies: data.competencies,
            // academics: {
            //     osid: userProfileObj.academics.osid,
            //     ...data.academics,
            // },
            professionalDetails: data.professionalDetails,
            // professionalDetails: {
            //     osid: userProfileObj.professionalDetails.osid,
            //     ...data.professionalDetails,
            // },
        }

        const requestToUserRegistry: IUserRegistryUpdateRequest = {
            id: 'open-saber.registry.update',
            ver: '1.0',
            // tslint:disable-next-line: object-literal-sort-keys
            ets: '11234',
            params: {
                did: '',
                key: '',
                msgid: '',
            },
            request: {
                // tslint:disable-next-line: max-line-length

                UserProfile: {
                    id: userId,
                    // tslint:disable-next-line: object-literal-sort-keys
                    '@id': userId,
                    '@type': 'UserProfile',
                    ...dataWithOsid,
                    osid: userProfileObj.osid,
                },
            },
        }

        const res = await axios({
            ...axiosRequestConfigLong,
            data: JSON.stringify(requestToUserRegistry),
            headers: {
                'Content-Type': 'application/json',
            },
            method: 'POST',
            url: apiEndpoints.update,
        })
        return res.data || {}
    } catch (err) {
        logError('Request to open saber /update failed')
        throw err
    }
}

export async function searchRegistry(
    // tslint:disable-next-line: no-any
    filters: any, limit?: number, offset?: number
    // tslint:disable-next-line: no-any
): Promise<any> {
    try {
        const requestToUserRegistry: IUserRegistrySearchRequest = {
            id: 'open-saber.registry.search',
            // tslint:disable-next-line: object-literal-sort-keys
            ets: '11234',
            ver: '1.0',
            params: {
                did: '',
                key: '',
                msgid: '',
            },
            request: {
                entityType: ['UserProfile'],
                filters,
                limit,
                offset,
            },
        }

        // tslint:disable-next-line: no-console
        logObject('Filters', filters)
        const res = await axios({
            ...axiosRequestConfig,
            data: JSON.stringify(requestToUserRegistry),
            headers: {
                'Content-Type': 'application/json',
            },
            method: 'POST',
            url: apiEndpoints.search,
        })
        return res.data || {}
    } catch (err) {
        logError('Request to open saber /search failed')
        throw err
    }
}
