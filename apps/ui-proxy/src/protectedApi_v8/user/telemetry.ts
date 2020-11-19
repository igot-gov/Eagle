import axios from 'axios'
import { Request, Response, Router } from 'express'
import { axiosRequestConfig } from '../../configs/request.config'
import { CONSTANTS } from '../../utils/env'

const API_END_POINTS = {
    telemetry: `${CONSTANTS.TELEMETRY_SB_BASE}/v1/telemetry`,
}

export const telemetryApi = Router()

telemetryApi.post('/', async (req: Request, res: Response) => {
    try {
        const response = await axios.post(
            API_END_POINTS.telemetry,
            req.body,
            axiosRequestConfig
        )

        res.status(response.status).send(response.data)
    } catch (err) {
        res.status((err && err.response && err.response.status) || 500)
            .send((err && err.response && err.response.data) || {
                error: 'Failed due to unknown reason',
            })
    }
})
