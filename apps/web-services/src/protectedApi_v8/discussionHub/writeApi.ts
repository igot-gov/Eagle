import axios from 'axios'
import { Router } from 'express'
import { getRootOrg } from '../../authoring/utils/header'
import { axiosRequestConfig } from '../../configs/request.config'
import { getWriteApiAdminUID, getWriteApiToken } from '../../utils/discussionHub-helper'
import { CONSTANTS } from '../../utils/env'
import { logError, logInfo } from '../../utils/logger'
import { extractUserIdFromRequest } from '../../utils/requestExtract'

const API_ENDPOINTS = {
    createTopic: `${CONSTANTS.DISCUSSION_HUB_API_BASE}/api/v2/topics`,
    createUser: `${CONSTANTS.DISCUSSION_HUB_API_BASE}/api/v2/users`,
    replyToTopic: (topicId: string | number) => `${CONSTANTS.DISCUSSION_HUB_API_BASE}/api/v2/topics/${topicId}`,
    votePost: (postId: string | number) => `${CONSTANTS.DISCUSSION_HUB_API_BASE}/api/v2/posts/${postId}/vote`,
}

export const writeApi = Router()

// tslint:disable-next-line: no-any
export async function createDiscussionHubUser(user: any): Promise<any> {
    logInfo('Starting to create new user into NodeBB DiscussionHub...')
    // tslint:disable-next-line: no-try-promise
    try {
        const request1 = {
            ...user,
            _uid: getWriteApiAdminUID(),
        }
        const url = API_ENDPOINTS.createUser
        return new Promise(async (resolve, reject) => {
            const response = await axios.post(
                url,
                request1,
                { ...axiosRequestConfig, headers: { authorization: getWriteApiToken() } }
            ).catch((err) => {
                logError('ERROR ON method createDiscussionHubUser api call to nodebb DiscussionHub>', err)
                reject(err)
            })
            resolve(response)
        })

    } catch (err) {
        logError('ERROR ON method createDiscussionHubUser >', err)
        return err
    }
}

writeApi.post('/topics', async (req, res) => {
    try {
        const rootOrg = getRootOrg(req)
        const userId = extractUserIdFromRequest(req)
        logInfo(`UserId: ${userId}, rootOrg: ${rootOrg}`)
        const url = API_ENDPOINTS.createTopic
        const response = await axios.post(
            url,
            {
                ...req.body,
                // _uid: getWriteApiAdminUID(),
            },
            { ...axiosRequestConfig, headers: { authorization: getWriteApiToken() } }
        )
        res.send(response.data)
    } catch (err) {
        logError('ERROR ON POST writeApi /topics >', err)
        res.status((err && err.response && err.response.status) || 500)
            .send(err && err.response && err.response.data || {})
    }
})

writeApi.post('/topics/:topicId', async (req, res) => {
    try {
        const rootOrg = getRootOrg(req)
        const userId = extractUserIdFromRequest(req)
        logInfo(`UserId: ${userId}, rootOrg: ${rootOrg}`)
        const topicId = req.params.topicId
        const url = API_ENDPOINTS.replyToTopic(topicId)
        const response = await axios.post(
            url,
            {
                ...req.body,
                // _uid: getWriteApiAdminUID(),
            },
            { ...axiosRequestConfig, headers: { authorization: getWriteApiToken() } }
        )
        res.send(response.data)
    } catch (err) {
        logError('ERROR ON writeAPI  POST /topics/:topicId >', err)
        res.status((err && err.response && err.response.status) || 500)
            .send(err && err.response && err.response.data || {})
    }
})

writeApi.post('/users', async (req, res) => {
    try {
        const rootOrg = getRootOrg(req)
        const userId = extractUserIdFromRequest(req)
        logInfo(`UserId: ${userId}, rootOrg: ${rootOrg}`)
        const response = await createDiscussionHubUser(req.body)
        res.send(response.data)
    } catch (err) {
        logError('ERROR ON writeAPI POST /users >', err)
        res.status((err && err.response && err.response.status) || 500)
            .send(err && err.response && err.response.data || {})
    }
})

writeApi.post('/posts/:postId/vote', async (req, res) => {
    try {
        const rootOrg = getRootOrg(req)
        const userId = extractUserIdFromRequest(req)
        logInfo(`UserId: ${userId}, rootOrg: ${rootOrg}`)
        const postId = req.params.postId
        const url = API_ENDPOINTS.votePost(postId)
        const response = await axios.post(
            url,
            {
                ...req.body,
                // _uid: getWriteApiAdminUID(),
            },
            { ...axiosRequestConfig, headers: { authorization: getWriteApiToken() } }
        )
        res.send(response.data)
    } catch (err) {
        logError('ERROR ON writeAPI POST /posts/:postId/vote >', err)
        res.status((err && err.response && err.response.status) || 500)
            .send(err && err.response && err.response.data || {})
    }
})

