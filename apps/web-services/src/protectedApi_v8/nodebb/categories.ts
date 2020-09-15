import axios from 'axios'
import { Router } from 'express'
import { getRootOrg } from '../../authoring/utils/header'
import { axiosRequestConfig } from '../../configs/request.config'
import { CONSTANTS } from '../../utils/env'
import { logError, logInfo } from '../../utils/logger'
import { extractUserIdFromRequest } from '../../utils/requestExtract'

const API_ENDPOINTS = {
    getAllCategories: `${CONSTANTS.NODE_BB_API_BASE}/api/categories`,
    // tslint:disable-next-line: no-any
    getCategoryDetails: (cid: any, slug?: any, tid?: any) => {
        let url = `${CONSTANTS.NODE_BB_API_BASE}/api/category/${cid}`
        if (slug) {
            url = `${url}/${slug}`
        }
        if (tid) {
            url = `${url}/${tid}`
        }
        return url
    },
}

export const categoriesApi = Router()

categoriesApi.get('/', async (req, res) => {
    try {
        const rootOrg = getRootOrg(req)
        const userId = extractUserIdFromRequest(req)
        logInfo(`UserId: ${userId}, rootOrg: ${rootOrg}`)
        const url = API_ENDPOINTS.getAllCategories
        const response = await axios.get(
            url,
            { ...axiosRequestConfig, headers: { rootOrg } }
        )
        res.send(response.data)
    } catch (err) {
        logError('ERROR ON GET topicsApi /categories >', err)
        res.status((err && err.response && err.response.status) || 500)
            .send(err && err.response && err.response.data || {})
    }
})

categoriesApi.get('/:cid/:slug?/:tid?', async (req, res) => {
    try {
        const rootOrg = getRootOrg(req)
        const userId = extractUserIdFromRequest(req)
        logInfo(`UserId: ${userId}, rootOrg: ${rootOrg}`)
        const cid = req.params.cid
        const slug = req.params.slug || undefined
        const tid = req.params.tid || undefined
        const url = API_ENDPOINTS.getCategoryDetails(cid, slug, tid)
        const response = await axios.get(
            url,
            { ...axiosRequestConfig, headers: { rootOrg } }
        )
        res.send(response.data)
    } catch (err) {
        logError('ERROR ON GET topicsApi /:cid/:slug/:tid? >', err)
        res.status((err && err.response && err.response.status) || 500)
            .send(err && err.response && err.response.data || {})
    }
})
