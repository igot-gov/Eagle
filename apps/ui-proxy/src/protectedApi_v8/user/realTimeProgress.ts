import axios from 'axios'
import { Router } from 'express'
import { axiosRequestConfig } from '../../configs/request.config'
import { CONSTANTS } from '../../utils/env'
import { logError, logErrorHeading } from '../../utils/logger'
import { ERROR } from '../../utils/message'
import { extractUserIdFromRequest } from '../../utils/requestExtract'

const API_END_POINTS = {
  progressUpdate: `${CONSTANTS.PROGRESS_API_BASE}` + '/v1/users',
}

export const realTimeProgressApi = Router()

realTimeProgressApi.post('/update/:contentId', async (req, res) => {
  try {
    const org = req.header('org')
    const rootOrg = req.header('rootOrg')
    if (!org || !rootOrg) {
      res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
      return
    }
    const userId = extractUserIdFromRequest(req)
    const params = req.params
    const contentId = params.contentId
    const url = `${API_END_POINTS.progressUpdate}/${userId}/content/${contentId}/progress/update`
    if (!contentId) {
      res.send(400)
    }
    const requestBody = req.body
    const response = await axios({
      ...axiosRequestConfig,
      data: requestBody,
      headers: {
        rootOrg,
      },
      method: 'POST',
      url,
    })
    res.json(response.data)
  } catch (err) {
    logErrorHeading('REAL TIME PROGRESS ERROR')
    logError(err)
    res.status((err && err.response && err.response.status) || 500).send(
      (err && err.response && err.response.data) || {
        error: 'Failed due to unknown reason',
      }
    )
  }
})

realTimeProgressApi.post('/markAsComplete/:contentId', async (req, res) => {
  try {
    const uuid = extractUserIdFromRequest(req)
    const contentId = req.params.contentId
    const rootOrg = req.header('rootOrg')
    const response = await axios.post(
      `${API_END_POINTS.progressUpdate}/${uuid}/content/${contentId}/progress/update?markread=true`,
      req.body,
      {
        ...axiosRequestConfig,
        headers: {
          rootOrg,
        },
      }
    )
    res.json(response.data)
  } catch (err) {
    logError('MARK AS COMPLETE ERROR -> ', err)
    res.status((err && err.response && err.response.status) || 500).send(
      (err && err.response && err.response.data) || {
        error: 'Failed due to unknown reason',
      }
    )
  }
})
