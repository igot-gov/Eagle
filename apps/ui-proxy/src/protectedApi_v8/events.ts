import axios from 'axios'
import { Router } from 'express'
import { axiosRequestConfig } from '../configs/request.config'
import { CONSTANTS } from '../utils/env'

const EVENTS_BASE_API = `${CONSTANTS.CONTENT_API_BASE}/live-events`

export const eventsApi = Router()

eventsApi.get('/', async (_req, res) => {
  try {
    const response = await axios.get(EVENTS_BASE_API, {
      ...axiosRequestConfig,
    })
    res.send((response.data))
  } catch (err) {
    res
      .status((err && err.response && err.response.status) || 500)
      .send((err && err.response && err.response.data) || {
        error: 'Failed due to unknown reason',
      })
  }
})
