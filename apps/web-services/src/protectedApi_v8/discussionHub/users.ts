import axios from 'axios'
import { Router } from 'express'
import { getRootOrg } from '../../authoring/utils/header'
import { axiosRequestConfig } from '../../configs/request.config'
import { CONSTANTS } from '../../utils/env'
import { logError, logInfo } from '../../utils/logger'
import { extractUserIdFromRequest } from '../../utils/requestExtract'

const API_ENDPOINTS = {
    getUserBookmarks: (slug: string) => `${CONSTANTS.DISCUSSION_HUB_API_BASE}/api/user/${slug}/bookmarks`,
    getUserDownvotedPosts: (slug: string) => `${CONSTANTS.DISCUSSION_HUB_API_BASE}/api/user/${slug}/downvoted`,
    getUserGroups: (slug: string) => `${CONSTANTS.DISCUSSION_HUB_API_BASE}/api/user/${slug}/groups`,
    getUserInfo: (slug: string) => `${CONSTANTS.DISCUSSION_HUB_API_BASE}/api/user/${slug}/info`,
    getUserPosts: (slug: string) => `${CONSTANTS.DISCUSSION_HUB_API_BASE}/api/user/${slug}/posts`,
    getUserUpvotedPosts: (slug: string) => `${CONSTANTS.DISCUSSION_HUB_API_BASE}/api/user/${slug}/upvoted`,
    getUsersWatchedTopics: (slug: string) => `${CONSTANTS.DISCUSSION_HUB_API_BASE}/api/user/${slug}/watched`,
    // tslint:disable-next-line: object-literal-sort-keys
    getUserByEmail: (email: string) => `${CONSTANTS.DISCUSSION_HUB_API_BASE}/api/user/email/${email}`,
}

export const usersApi = Router()

usersApi.get('/:slug/bookmarks', async (req, res) => {
    try {
        const rootOrg = getRootOrg(req)
        const userId = extractUserIdFromRequest(req)
        logInfo(`UserId: ${userId}, rootOrg: ${rootOrg}`)
        const slug = req.params.slug
        const url = API_ENDPOINTS.getUserBookmarks(slug)
        const response = await axios.get(
            url,
            { ...axiosRequestConfig, headers: { rootOrg } }
        )
        res.send(response.data)
    } catch (err) {
        logError('ERROR ON GET topicsApi /:slug/bookmarks >', err)
        res.status((err && err.response && err.response.status) || 500)
            .send(err && err.response && err.response.data || {})
    }
})

usersApi.get('/:slug/downvoted', async (req, res) => {
    try {
        const rootOrg = getRootOrg(req)
        const userId = extractUserIdFromRequest(req)
        logInfo(`UserId: ${userId}, rootOrg: ${rootOrg}`)
        const slug = req.params.slug
        const url = API_ENDPOINTS.getUserDownvotedPosts(slug)
        const response = await axios.get(
            url,
            { ...axiosRequestConfig, headers: { rootOrg } }
        )
        res.send(response.data)
    } catch (err) {
        logError('ERROR ON GET topicsApi /:slug/downvoted >', err)
        res.status((err && err.response && err.response.status) || 500)
            .send(err && err.response && err.response.data || {})
    }
})

usersApi.get('/:slug/groups', async (req, res) => {
    try {
        const rootOrg = getRootOrg(req)
        const userId = extractUserIdFromRequest(req)
        logInfo(`UserId: ${userId}, rootOrg: ${rootOrg}`)
        const slug = req.params.slug
        const url = API_ENDPOINTS.getUserGroups(slug)
        const response = await axios.get(
            url,
            { ...axiosRequestConfig, headers: { rootOrg } }
        )
        res.send(response.data)
    } catch (err) {
        logError('ERROR ON GET topicsApi /:slug/groups >', err)
        res.status((err && err.response && err.response.status) || 500)
            .send(err && err.response && err.response.data || {})
    }
})

usersApi.get('/:slug/info', async (req, res) => {
    try {
        const rootOrg = getRootOrg(req)
        const userId = extractUserIdFromRequest(req)
        logInfo(`UserId: ${userId}, rootOrg: ${rootOrg}`)
        const slug = req.params.slug
        const url = API_ENDPOINTS.getUserInfo(slug)
        const response = await axios.get(
            url,
            { ...axiosRequestConfig, headers: { rootOrg } }
        )
        res.send(response.data)
    } catch (err) {
        logError('ERROR ON GET topicsApi /:slug/info >', err)
        res.status((err && err.response && err.response.status) || 500)
            .send(err && err.response && err.response.data || {})
    }
})

usersApi.get('/:slug/posts', async (req, res) => {
    try {
        const rootOrg = getRootOrg(req)
        const userId = extractUserIdFromRequest(req)
        logInfo(`UserId: ${userId}, rootOrg: ${rootOrg}`)
        const slug = req.params.slug
        const url = API_ENDPOINTS.getUserPosts(slug)
        const response = await axios.get(
            url,
            { ...axiosRequestConfig, headers: { rootOrg } }
        )
        res.send(response.data)
    } catch (err) {
        logError('ERROR ON GET topicsApi /:slug/posts >', err)
        res.status((err && err.response && err.response.status) || 500)
            .send(err && err.response && err.response.data || {})
    }
})

usersApi.get('/:slug/upvoted', async (req, res) => {
    try {
        const rootOrg = getRootOrg(req)
        const userId = extractUserIdFromRequest(req)
        logInfo(`UserId: ${userId}, rootOrg: ${rootOrg}`)
        const slug = req.params.slug
        const url = API_ENDPOINTS.getUserUpvotedPosts(slug)
        const response = await axios.get(
            url,
            { ...axiosRequestConfig, headers: {} }
        )
        res.send(response.data)
    } catch (err) {
        logError('ERROR ON GET topicsApi /:slug/upvoted >', err)
        res.status((err && err.response && err.response.status) || 500)
            .send(err && err.response && err.response.data || {})
    }
})

usersApi.get('/:slug/watched', async (req, res) => {
    try {
        const rootOrg = getRootOrg(req)
        const userId = extractUserIdFromRequest(req)
        logInfo(`UserId: ${userId}, rootOrg: ${rootOrg}`)
        const slug = req.params.slug
        const url = API_ENDPOINTS.getUsersWatchedTopics(slug)
        const response = await axios.post(
            url,
            { _uid: 1 },
            { ...axiosRequestConfig, headers: {} }
        )
        res.send(response.data)
    } catch (err) {
        logError('ERROR ON GET topicsApi /:slug/watched >', err)
        res.status((err && err.response && err.response.status) || 500)
            .send(err && err.response && err.response.data || {})
    }
})

usersApi.get('/email/:email', async (req, res) => {
    try {
        const rootOrg = getRootOrg(req)
        const userId = extractUserIdFromRequest(req)
        logInfo(`UserId: ${userId}, rootOrg: ${rootOrg}`)
        const email = req.params.email
        const response = await getUserByEmail(email)
        res.send(response.data)
    } catch (err) {
        logError('ERROR ON GET topicsApi /:slug/watched >', err)
        res.status((err && err.response && err.response.status) || 500)
            .send(err && err.response && err.response.data || {})
    }
})

// tslint:disable-next-line: no-any
export async function getUserByEmail(email: any): Promise<any> {
    logInfo('Finding user in NodeBB DiscussionHub...')
    // tslint:disable-next-line: no-try-promise
    try {
        const url = API_ENDPOINTS.getUserByEmail(email)
        return new Promise(async (resolve, reject) => {
            const response = await axios.get(
                url,
                { ...axiosRequestConfig }
            ).catch((err) => {
                logError('ERROR ON method getUserByEmail api call to nodebb DiscussionHub >', err)
                reject(err)
            })
            resolve(response)
        })

    } catch (err) {
        logError('ERROR ON method getUserByEmail >', err)
        return err
    }
}
