import axios from 'axios'
import { Router } from 'express'
import { axiosRequestConfig } from '../../configs/request.config'
import { CONSTANTS } from '../../utils/env'
import { extractUserIdFromRequest } from '../../utils/requestExtract'

const API_END_POINTS = {
  feedback: CONSTANTS.SB_EXT_API_BASE + '/v1/course/feedback/add/', // #POST/:userid
}

export const feedbackApi = Router()

feedbackApi.post('/', async (req, res) => {
  try {
    const userId = extractUserIdFromRequest(req)
    const response = await axios.post(
      `${API_END_POINTS.feedback}${userId}`,
      req.body,
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
