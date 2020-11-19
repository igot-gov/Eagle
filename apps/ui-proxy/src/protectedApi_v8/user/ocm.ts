import axios from 'axios'
import { Router } from 'express'
import { axiosRequestConfig } from '../../configs/request.config'
import { CONSTANTS } from '../../utils/env'
import { extractUserIdFromRequest } from '../../utils/requestExtract'

const API_END_POINTS = {
  user: CONSTANTS.SB_EXT_API_BASE + '/v1/users/',
}

export const ocmApi = Router()

ocmApi.get('/getToDos/:id', async (req, res) => {
  try {
    const userId = extractUserIdFromRequest(req)
    const id = req.params.id
    const response = await axios.get(
      `${API_END_POINTS.user}${userId}/task_groups/${id}/tasks`,
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
