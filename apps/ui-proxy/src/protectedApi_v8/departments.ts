import axios from 'axios'
import { Router } from 'express'

import { axiosRequestConfig } from '../configs/request.config'
import { CONSTANTS } from '../utils/env'

const API_END_POINTS = {
    getAllDepartment: `${CONSTANTS.SB_EXT_API_BASE_2}/portal/getAllDept`,
    searchDepartment: (friendlyName: string) => `${CONSTANTS.SB_EXT_API_BASE_2}/portal/deptSearch?friendlyName=${friendlyName}`,
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

deptApi.get('/searchDept', async (req, res) => {
    try {
        const friendlyNameValue = req.query.friendlyName
        const response = await axios.get(API_END_POINTS.searchDepartment(friendlyNameValue), axiosRequestConfig)
        res.status(response.status).send(response.data)
    } catch (err) {
        res.status((err && err.response && err.response.status) || 500).send(
            (err && err.response && err.response.data) || {
                error: unknownError,
            }
        )
    }
})
