import axios from 'axios'
import { Router } from 'express'
import { axiosRequestConfig } from '../configs/request.config'
import { CONSTANTS } from '../utils/env'

const API_END_POINTS = {
  infyMe: CONSTANTS.SB_EXT_API_BASE + '/v1/infyme/metrics',
  platformPostfixUrl: '/stats/data/now',
}

export const counterApi = Router()
// Api call happens in Lex  experience wow page in features for lex wowstats
counterApi.get('/', async (_req, res) => {
  try {
    let urlPrefix = CONSTANTS.COUNTER
    if (CONSTANTS.USE_SERVING_HOST_COUNTER) {
      urlPrefix = 'http://10.177.63.164:5903'
    }

    const response = await axios.get(
      `${urlPrefix}${API_END_POINTS.platformPostfixUrl}`,
      axiosRequestConfig
    )
    res.status(response.status).send(response.data)
  } catch (err) {
    res.status((err && err.response && err.response.status) || 500).send(
      (err && err.response && err.response.data) || {
        error: 'Failed due to unknown reason',
      }
    )
  }
})
// Api call happens in Lex  experience wow page in features for infyme wow stats
counterApi.get('/infyMe', async (req, res) => {
  try {
    let startdate = null
    let enddate = null
    if (req.query) {
      const query = req.query
      startdate = query.startdate
      enddate = query.enddate
    }
    if (startdate && enddate) {
      const response = await axios.get(
        `${API_END_POINTS.infyMe}?start-date=${startdate}&end-date=${enddate}`
      )
      res.status(response.status).send(response.data)
    } else {
      const response = await axios.get(`${API_END_POINTS.infyMe}`)
      res.status(response.status).send(response.data)
    }
  } catch (err) {
    res.status((err && err.response && err.response.status) || 500).send(
      (err && err.response && err.response.data) || {
        error: 'Failed due to unknown reason',
      }
    )
  }
})
