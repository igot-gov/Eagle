import axios from 'axios'
import { Router } from 'express'

import { axiosRequestConfig } from '../configs/request.config'
import { CONSTANTS } from '../utils/env'
import { ERROR } from '../utils/message'

const API_END_POINTS = {
    addCompetency: `${CONSTANTS.FRAC_API_BASE}/api/frac/addDataNode`,
    getCompetency: `${CONSTANTS.FRAC_API_BASE}/api/frac/getAllNodes?type=COMPETENCY&status=VERIFIED`,
    searchCompetency: `${CONSTANTS.FRAC_API_BASE}/api/frac/searchNodes`,
}

export const competencyApi = Router()
const unknownError = 'Failed due to unknown reason'

competencyApi.get('/getCompetency', async (req, res) => {
    try {
        const rootOrg = req.header('rootOrg')
        if (!rootOrg) {
            res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
            return
        }
        const response = await axios.get(API_END_POINTS.getCompetency, axiosRequestConfig)
        res.status(response.status).send(response.data)
    } catch (err) {
        res.status((err && err.response && err.response.status) || 500).send(
            (err && err.response && err.response.data) || {
                error: unknownError,
            }
        )
    }
})

competencyApi.post('/addCompetency', async (req, res) => {
    try {
        const response = await axios.post(API_END_POINTS.addCompetency, req.body, axiosRequestConfig)
        res.status(response.status).send(response.data)
    } catch (err) {
        res.status((err && err.response && err.response.status) || 500).send(
            (err && err.response && err.response.data) || {
                error: unknownError,
            }
        )
    }
})

competencyApi.post('/searchCompetency', async (req, res) => {
    try {
        const response = await axios.post(API_END_POINTS.searchCompetency, req.body, axiosRequestConfig)
        res.status(response.status).send(response.data)
    } catch (err) {
        res.status((err && err.response && err.response.status) || 500).send(
            (err && err.response && err.response.data) || {
                error: unknownError,
            }
        )
    }
})
