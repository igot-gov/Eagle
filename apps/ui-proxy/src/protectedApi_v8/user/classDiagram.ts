import axios from 'axios'
import { Router } from 'express'
import { axiosRequestConfig } from '../../configs/request.config'
import { CONSTANTS } from '../../utils/env'
import { logError } from '../../utils/logger'
import { extractUserIdFromRequest } from '../../utils/requestExtract'

const API_ENDPOINTS = {
  submission: `${CONSTANTS.SUBMISSION_API_BASE}/v1/users`,
}

export const classDiagramApi = Router()

classDiagramApi.post('/classdiagram/submit/:contentId', async (req, res) => {
  try {
    const uuid = extractUserIdFromRequest(req)
    const contentId = req.params.contentId
    const config = axiosRequestConfig
    config.headers = {
      rootOrg: req.header('rootOrg'),
    }

    const response = await axios.post(
      `${API_ENDPOINTS.submission}/${uuid}/exercises/${contentId}/classdiagram-submission`,
      {
        ...req.body,
      },
      config
    )
    res.json(response.data)
  } catch (err) {
    logError(err)
    res.status((err && err.response && err.response.status) || 500).send(
      (err && err.response && err.response.data) || {
        error: 'Failed due to unknown reason',
      }
    )
  }
})
