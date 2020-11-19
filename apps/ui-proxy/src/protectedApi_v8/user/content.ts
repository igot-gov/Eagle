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
import { extractUserIdFromRequest, IAuthorizedRequest } from '../../utils/requestExtract'
import { getMultipleContent } from '../content'

const API_END_POINTS = {
  assignedContent: (userId: string) =>
    `${CONSTANTS.SB_EXT_API_BASE_2}/v1/users/${userId}/assigned-content`,
  contentLikeNumber: `${CONSTANTS.SB_EXT_API_BASE_2}/v1/likes-count`,
  like: (userId: string) => `${CONSTANTS.LIKE_API_BASE}/v1/user/${userId}/likes`,
}

const GENERAL_ERROR_MSG = 'Failed due to unknown reason'

export const userContentApi = Router()

userContentApi.post('/contentLikes', async (req, res) => {
  try {
    const org = req.header('org')
    const rootOrg = req.header('rootOrg')
    if (!org || !rootOrg) {
      res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
      return
    }

    const response = await axios.post(API_END_POINTS.contentLikeNumber, req.body, {
      ...axiosRequestConfig,
      headers: { rootOrg },
    })
    res.status(response.status).send(response.data)
  } catch (err) {
    logError('ERROR FETCHING CONTENT LIKES >', err)
    res.status((err && err.response && err.response.status) || 500).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})

userContentApi.get('/like', async (req, res) => {
  try {
    const org = req.header('org')
    const rootOrg = req.header('rootOrg')
    if (!org || !rootOrg) {
      res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
      return
    }
    const response = await fetchLikedIdsResponse(req, rootOrg, org)
    res.json(response)
  } catch (err) {
    logError('ERROR FETCHING LIKES >', err)
    res.status((err && err.response && err.response.status) || 500).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})
export async function fetchLikedIdsResponse(req: IAuthorizedRequest, rootOrg: string, org: string) {
  try {
    const response = await axios({
      ...axiosRequestConfig,
      headers: {
        org,
        rootOrg,
      },
      method: 'GET',
      url: `${API_END_POINTS.like(extractUserIdFromRequest(req))}`,
    })
    return response.data
  } catch (e) {
    throw new Error(e)
  }
}
userContentApi.get('/like/contents', async (req, res) => {
  try {
    const org = req.header('org')
    const rootOrg = req.header('rootOrg')
    if (!org || !rootOrg) {
      res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
      return
    }
    const likedIdsResponse = await fetchLikedIdsResponse(req, rootOrg, org)
    const likedIds = likedIdsResponse || []
    if (!Array.isArray(likedIds) || !likedIds.length) {
      res.send([])
    }
    const response = await getMultipleContent(likedIds, rootOrg, org, extractUserIdFromRequest(req))
    const result: IPaginatedApiResponse = {
      contents: response || [],
      hasMore: false,
    }
    res.json(result)
  } catch (err) {
    logError('ERROR in LIKE GET CONTENTS >', err)
    res.status((err && err.response && err.response.status) || 500).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})

userContentApi.post('/like/:contentId', async (req, res) => {
  try {
    const org = req.header('org')
    const rootOrg = req.header('rootOrg')
    if (!org || !rootOrg) {
      res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
      return
    }
    const response = await axios({
      ...axiosRequestConfig,
      data: req.body,
      headers: {
        rootOrg,
      },
      method: 'POST',
      url: `${API_END_POINTS.like(extractUserIdFromRequest(req))}?content_id=${
        req.params.contentId
      }`,
    })
    res.json(response.data)
  } catch (err) {
    logError('ERROR LIKING >', err)
    res.status((err && err.response && err.response.status) || 500).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})
userContentApi.delete('/unlike/:contentId', async (req, res) => {
  try {
    const org = req.header('org')
    const rootOrg = req.header('rootOrg')
    if (!rootOrg || !org) {
      res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
      return
    }
    const response = await axios({
      ...axiosRequestConfig,
      data: req.body,
      headers: {
        rootOrg,
      },
      method: 'DELETE',
      url: `${API_END_POINTS.like(extractUserIdFromRequest(req))}?content_id=${
        req.params.contentId
      }`,
    })
    res.json(response.data)
  } catch (err) {
    logError('ERROR UN-LIKING >', err)
    res.status((err && err.response && err.response.status) || 500).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})

userContentApi.get('/assigned-content', async (req, res) => {
  try {
    const { isInIntranet, isExternal, isStandAlone, pageSize, sourceFields } = req.query
    const queryParams = getStringifiedQueryParams({
      isExternal,
      isInIntranet,
      isStandAlone,
      pageSize,
      sourceFields,
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
      url: `${API_END_POINTS.assignedContent(userId)}?${queryParams}`,
    })
    let contents: IContent[] = []
    if (Array.isArray(response.data.assignedContents)) {
      contents = response.data.assignedContents.map((content: IContent) => processContent(content))
    }
    const result: IPaginatedApiResponse = {
      contents,
      hasMore: false,
    }
    res.json(result)
  } catch (error) {
    logError('ASSIGNED CONTENT FETCH ERROR >', error)
    res.status(500).json(error)
  }
})
