import axios from 'axios'
import { Router } from 'express'
import { axiosRequestConfig } from '../../configs/request.config'
import { CONSTANTS } from '../../utils/env'
import { logError, logErrorHeading, logInfo } from '../../utils/logger'
import { ERROR } from '../../utils/message'
import { extractUserIdFromRequest } from '../../utils/requestExtract'

const API_END_POINTS = {
  // tslint:disable-next-line: max-line-length
  hash: (userId: string) =>
    `${CONSTANTS.LEARNING_HISTORY_API_BASE}/v3/users/${userId}/contentlist/progress`,
  progressMeta: (userId: string, contentId: string) => {
    return `${CONSTANTS.PROGRESS_API_BASE}/v1/users/${userId}/content-ids/${contentId}/progress-meta`
  },
}

const GENERAL_ERROR_MSG = 'Failed due to unknown reason'

export const progressApi = Router()

progressApi.get('/:contentId', async (req, res) => {
  try {
    const contentId = req.params.contentId
    const uuid = extractUserIdFromRequest(req)
    const rootOrg = req.header('rootOrg')
    const response = await axios.get(API_END_POINTS.progressMeta(uuid, contentId), {
      headers: { rootOrg },
    })
    // tslint:disable-next-line: no-console
    console.log('get/:contentId progress api response : ', response)
    logInfo('get/:contentId progress api response.data : ', response.data)
    res.json(response.data)
  } catch (err) {
    logError('FETCH MARK AS COMPLETE META => ', err)
    res.status((err && err.response && err.response.status) || 500).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})

progressApi.get('/', async (req, res) => {
  try {
    const org = req.header('org')
    const rootOrg = req.header('rootOrg')
    if (!org || !rootOrg) {
      res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
      return
    }
    const response = await axios({
      ...axiosRequestConfig,
      headers: {
        rootOrg,
      },
      method: 'GET',
      url: API_END_POINTS.hash(extractUserIdFromRequest(req)),
    })
    // tslint:disable-next-line: no-console
    console.log('get progress api response : ', response)
    logInfo('get progress api response.data : ', response.data)
    res.json(response.data)
  } catch (err) {
    logErrorHeading('PROGRESS HASH ERROR')
    logError(err)
    res.status((err && err.response && err.response.status) || 500).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})

progressApi.post('/', async (req, res) => {
  try {
    const org = req.header('org')
    const rootOrg = req.header('rootOrg')
    if (!org || !rootOrg) {
      res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
      return
    }
    const response = await axios.post(
      API_END_POINTS.hash(extractUserIdFromRequest(req)),
      req.body,
      {
        ...axiosRequestConfig,
        headers: { rootOrg },
      }
    )
    // tslint:disable-next-line: no-console
    console.log('post progress api response : ', response)
    logInfo('post progress api response.data : ', response.data)

    res.status(response.status).send(response.data)
  } catch (err) {
    logErrorHeading('PROGRESS HASH ERROR POST')
    logError(err)
    res.status((err && err.response && err.response.status) || 500).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})
