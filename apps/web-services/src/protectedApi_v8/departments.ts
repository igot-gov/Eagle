import axios from 'axios'
import { Router } from 'express'

import { axiosRequestConfig } from '../configs/request.config'
import { CONSTANTS } from '../utils/env'

const API_END_POINTS = {
    addDepartment: `${CONSTANTS.USER_PROFILE_API_BASE}/dept/`,
    getAllDepartment: `${CONSTANTS.USER_PROFILE_API_BASE}/dept`,
    searchDepartment: `${CONSTANTS.USER_PROFILE_API_BASE}/dept/search`,
}

export const deptApi = Router()
const unknownError = 'Failed due to unknown reason'

deptApi.get('/getAllDept', async (_req, res) => {
    try {
        const response = await axios.get(API_END_POINTS.getAllDepartment, axiosRequestConfig)
        res.status(response.status).send(response.data)
    } catch (err) {
        res.status((err && err.response && err.response.status) || 500).send(
            (err && err.response && err.response.data) || {
                error: unknownError,
            }
        )
    }
})

deptApi.post('/addDept', async (req, res) => {
    try {
        const response = await axios.post(API_END_POINTS.addDepartment, req.body, axiosRequestConfig)
        res.status(response.status).send(response.data)
    } catch (err) {
        res.status((err && err.response && err.response.status) || 500).send(
            (err && err.response && err.response.data) || {
                error: unknownError,
            }
        )
    }
})

deptApi.get('/searchDept', async (req, res) => {
    try {
        const friendlyNameValue = req.header('friendlyName')
        const response = await axios.get(API_END_POINTS.searchDepartment, {
            ...axiosRequestConfig,
            headers: {
                friendlyName: friendlyNameValue,
            },
        })
        res.status(response.status).send(response.data)
    } catch (err) {
        res.status((err && err.response && err.response.status) || 500).send(
            (err && err.response && err.response.data) || {
                error: unknownError,
            }
        )
    }
})
