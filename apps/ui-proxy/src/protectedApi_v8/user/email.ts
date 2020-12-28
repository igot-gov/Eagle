import axios from 'axios'
import { Router } from 'express'
import { axiosRequestConfig } from '../../configs/request.config'
import { CONSTANTS } from '../../utils/env'

const API_END_POINTS = {
  email: CONSTANTS.SB_EXT_API_BASE + '/v1/Notification/Send',
}

export const emailApi = Router()

emailApi.post('/emailText', async (req, res) => {
  try {
    const response = await axios.post(`${API_END_POINTS.email}/Text`, req.body, axiosRequestConfig)
    res.status(response.status).send(response.data)
  } catch (err) {
    res.status((err && err.response && err.response.status) || 500).send(
      (err && err.response && err.response.data) || {
        error: 'Failed due to unknown reason',
      }
    )
  }
})
