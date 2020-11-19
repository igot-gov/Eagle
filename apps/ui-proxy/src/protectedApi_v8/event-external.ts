import axios from 'axios'
import { Router } from 'express'
import { axiosRequestConfig } from '../configs/request.config'

const externalApiEndpoint = 'https://lab42.idemo-ppc.com/awsapi/s3/json/event'

export const externalEventsApi = Router()

externalEventsApi.get('/', async (_req, res) => {
    try {
        const apikey = '41ccd6ed78971a9051b1b17a9f81dbdff44ac020eff79b3d703ad0afa39490d3'
        const response = await axios.get(externalApiEndpoint, {
            ...axiosRequestConfig,
            headers: { api_key: apikey },
        })
        const data = response.data
        res.json(data || {})
    } catch (err) {
        res.status((err && err.response && err.response.status) || 500)
            .send(err && err.response && err.response.data || {})
    }
})
