import axios from 'axios'
import { Router } from 'express'
import { getRootOrg } from '../../authoring/utils/header'
import { axiosRequestConfig } from '../../configs/request.config'
import { getUserUID, getWriteApiToken } from '../../utils/discussionHub-helper'
import { CONSTANTS } from '../../utils/env'
import { logError, logInfo } from '../../utils/logger'
import { extractUserIdFromRequest } from '../../utils/requestExtract'

const API_ENDPOINTS = {
    getPopularTopics: `${CONSTANTS.DISCUSSION_HUB_API_BASE}/api/popular`,
    getRecentTopics: `${CONSTANTS.DISCUSSION_HUB_API_BASE}/api/recent`,
    getTopTopics: `${CONSTANTS.DISCUSSION_HUB_API_BASE}/api/top`,
    getUnreadTopics: `${CONSTANTS.DISCUSSION_HUB_API_BASE}/api/unread`,
    getUnreadTopicsTotal: `${CONSTANTS.DISCUSSION_HUB_API_BASE}/api/unread/total`,
    // tslint:disable-next-line: object-literal-sort-keys
    getTopicDetails: (tid: number) => `${CONSTANTS.DISCUSSION_HUB_API_BASE}/api/topic/${tid}`,
}

export const topicsApi = Router()

topicsApi.get('/recent', async (req, res) => {
    try {
        const rootOrg = getRootOrg(req)
        const pageNo = req.query.page || 1
        const userId = extractUserIdFromRequest(req)
        let url = API_ENDPOINTS.getRecentTopics + `?page=${pageNo}`
        if (CONSTANTS.DISCUSSION_CATEGORY_LIST) {
            url = url + `&` + CONSTANTS.DISCUSSION_CATEGORY_LIST
        }
        logInfo(`UserId: ${userId}, rootOrg: ${rootOrg}, Url: ${url}`)
        const response = await axios.get(
            url,
            { ...axiosRequestConfig, headers: { rootOrg } }
        )
        res.send(response.data)
    } catch (err) {
        logError('ERROR ON GET topicsApi /recent >', err)
        res.status((err && err.response && err.response.status) || 500)
            .send(err && err.response && err.response.data || {})
    }
})

topicsApi.get('/top', async (req, res) => {
    try {
        const rootOrg = getRootOrg(req)
        const userId = extractUserIdFromRequest(req)
        logInfo(`UserId: ${userId}, rootOrg: ${rootOrg}`)
        const url = API_ENDPOINTS.getTopTopics
        const response = await axios.get(
            url,
            { ...axiosRequestConfig, headers: { rootOrg } }
        )
        res.send(response.data)
    } catch (err) {
        logError('ERROR ON GET topicsApi /top >', err)
        res.status((err && err.response && err.response.status) || 500)
            .send(err && err.response && err.response.data || {})
    }
})

topicsApi.get('/popular', async (req, res) => {
    try {
        const rootOrg = getRootOrg(req)
        const userId = extractUserIdFromRequest(req)
        const pageNo = req.query.page || 1
        logInfo(`UserId: ${userId}, rootOrg: ${rootOrg}`)
        const url = API_ENDPOINTS.getPopularTopics
        const response = await axios.get(
            `${url}?page=${pageNo}`,
            { ...axiosRequestConfig, headers: { rootOrg } }
        )
        res.send(response.data)
    } catch (err) {
        logError('ERROR ON GET topicsApi /popular >', err)
        res.status((err && err.response && err.response.status) || 500)
            .send(err && err.response && err.response.data || {})
    }
})

topicsApi.get('/unread', async (req, res) => {
    try {
        const rootOrg = getRootOrg(req)
        const userId = extractUserIdFromRequest(req)
        logInfo(`UserId: ${userId}, rootOrg: ${rootOrg}`)
        const userUid = await getUserUID(userId)
        const url = API_ENDPOINTS.getUnreadTopics + `?_uid=${userUid}`
        const response = await axios.get(
            url,
            { ...axiosRequestConfig, headers: { authorization: getWriteApiToken() } }
        )
        res.send(response.data)
    } catch (err) {
        logError('ERROR ON GET topicsApi /unread >', err)
        res.status((err && err.response && err.response.status) || 500)
            .send(err && err.response && err.response.data || {})
    }
})

topicsApi.get('/unread/total', async (req, res) => {
    try {
        const rootOrg = getRootOrg(req)
        const userId = extractUserIdFromRequest(req)
        logInfo(`UserId: ${userId}, rootOrg: ${rootOrg}`)
        const userUid = await getUserUID(userId)
        const url = API_ENDPOINTS.getUnreadTopicsTotal + `?_uid=${userUid}`
        const response = await axios.get(
            url,
            { ...axiosRequestConfig, headers: { authorization: getWriteApiToken() } }
        )
        res.send(response.data)
    } catch (err) {
        logError('ERROR ON GET topicsApi /unread >', err)
        res.status((err && err.response && err.response.status) || 500)
            .send(err && err.response && err.response.data || {})
    }
})

topicsApi.get('/:tid', async (req, res) => {
    try {
        const rootOrg = getRootOrg(req)
        const userId = extractUserIdFromRequest(req)
        const pageNo = req.query.page || 1
        const sort = req.query.sort || ''
        logInfo(`UserId: ${userId}, rootOrg: ${rootOrg}`)
        const tid = req.params.tid
        const userUid = await getUserUID(userId)
        const url = API_ENDPOINTS.getTopicDetails(tid) + `?page=${pageNo}&_uid=${userUid}&sort=${sort}`
        const response = await axios.get(
            url,
            { ...axiosRequestConfig, headers: { authorization: getWriteApiToken() } }
        )
        res.send(response.data)
    } catch (err) {
        logError('ERROR ON GET topicsApi /:tid >', err)
        res.status((err && err.response && err.response.status) || 500)
            .send(err && err.response && err.response.data || {})
    }
})
