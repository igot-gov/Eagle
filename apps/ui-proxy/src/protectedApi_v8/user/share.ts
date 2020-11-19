import axios from 'axios'
import { Router } from 'express'
import { axiosRequestConfig } from '../../configs/request.config'
import { IContent } from '../../models/content.model'
import { IPaginatedApiResponse } from '../../models/paginatedApi.model'
import { processContent } from '../../utils/contentHelpers'
import { CONSTANTS } from '../../utils/env'
import { getStringifiedQueryParams } from '../../utils/helpers'
import { logError } from '../../utils/logger'
import { ERROR } from '../../utils/message'
import { extractUserIdFromRequest } from '../../utils/requestExtract'

const API_END_POINTS = {
  GET_SHARED: (userId: string) => `${CONSTANTS.SB_EXT_API_BASE_2}/v1/users/${userId}/share`,
  SHARE: CONSTANTS.SB_EXT_API_BASE + '/v1/Notification/Send',
  SHARE_CONTENT: CONSTANTS.NOTIFICATIONS_API_BASE + '/v1/notification/event',
  SHARE_V1: `${CONSTANTS.SB_EXT_API_BASE_2}/v1/content-share`,
}

export const shareApi = Router()

shareApi.post('/', async (req, res) => {
  try {
    const response = await axios.post(
      API_END_POINTS.SHARE,
      req.body,
      axiosRequestConfig
    )
    res.status(response.status).json(response.data.result)
  } catch (err) {
    res.status((err && err.response && err.response.status) || 500)
      .send((err && err.response && err.response.data) || err)
  }
})

shareApi.post('/content', async (req, res) => {
  try {
    const org = req.header('org')
    const rootOrg = req.header('rootOrg')
    const langCode = req.header('locale')
    if (!org || !rootOrg) {
      res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
      return
    }
    let data = req.body
    let url = API_END_POINTS.SHARE_CONTENT
    if (rootOrg === 'Ford') {
      url = API_END_POINTS.SHARE_V1
      data = {
        content_id: data['target-data'].identifier,
        share_message: data['tag-value-pair']['#message'],
        shared_by: extractUserIdFromRequest(req),
        shared_with: data.recipients.sharedWith,
        targetUrl: data['tag-value-pair']['#targetUrl'],
      }
    }
    const response = await axios({
      ...axiosRequestConfig,
      data,
      headers: {
        langCode,
        org,
        rootOrg,
      },
      method: 'POST',
      url,
    })
    res.status(response.status).json(response.data)
  } catch (err) {
    res.status((err && err.response && err.response.status) || 500)
      .send((err && err.response && err.response.data) || err)
  }
})

shareApi.get('/shared', async (req, res) => {
  try {
    const { size, page, isInIntranet } = req.query
    const queryParams = getStringifiedQueryParams({
      isInIntranet,
      page,
      size,
    })
    const userId = extractUserIdFromRequest(req)
    const rootOrg = req.header('rootOrg')
    if (!rootOrg) {
      res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
      return
    }
    const response = await axios({
      ...axiosRequestConfig,
      headers: {
        rootOrg,
      },
      method: 'GET',
      url: `${API_END_POINTS.GET_SHARED(userId)}?${queryParams}`,
    })
    let contents: IContent[] = []
    if (Array.isArray(response.data.shareDetails)) {
      contents = response.data.shareDetails.map((content: IContent) => processContent(content))
    }
    const result: IPaginatedApiResponse = {
      contents,
      hasMore: false,
    }
    res.json(result)
  } catch (error) {
    logError('SHARED CONTENT FETCH ERROR >', error)
    res.status(500).json(error)
  }
})
