import axios from 'axios'
import { Router } from 'express'
import { axiosRequestConfig } from '../../configs/request.config'
import { CONSTANTS } from '../../utils/env'
import { ERROR } from '../../utils/message'
import { extractUserIdFromRequest } from '../../utils/requestExtract'

const API_END_POINTS = {
  contentAverageRatingInfo: (contentId: string) => `${CONSTANTS.RATING_API_BASE}/v1/content/average-rating/${contentId}`,
  contentRating: (contentId: string, userId: string) =>
    `${CONSTANTS.RATING_API_BASE}/v1/contents/${contentId}/users/${userId}/ratings`,
  contentRatings: `${CONSTANTS.RATING_API_BASE}/v1/content/ratings`,
  contentsRating: `${CONSTANTS.RATING_API_BASE}/v1/contents/average-rating`,

}

const GENERAL_ERROR_MSG = 'Failed due to unknown reason'

export const ratingApi = Router()

ratingApi.get('/:contentId', async (req, res) => {
  try {
    const contentId = req.params.contentId
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
      url: `${API_END_POINTS.contentRating(contentId, extractUserIdFromRequest(req))}`,
    })
    res.status(response.status).send(response.data)
  } catch (err) {
    res.status((err && err.response && err.response.status) || 500).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})

ratingApi.post('/rating', async (req, res) => {
  try {
    const rootOrg = req.header('rootOrg')
    if (!rootOrg) {
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
      url: `${API_END_POINTS.contentsRating}`,
    })
    res.status(response.status).send(response.data)
  } catch (err) {
    res.status((err && err.response && err.response.status) || 500).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})

ratingApi.post('/:contentId', async (req, res) => {
  try {
    const contentId = req.params.contentId
    const rootOrg = req.header('rootOrg')
    if (!rootOrg) {
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
      url: `${API_END_POINTS.contentRating(contentId, extractUserIdFromRequest(req))}`,
    })
    res.status(response.status).send(response.data)
  } catch (err) {
    res.status((err && err.response && err.response.status) || 500).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})

ratingApi.delete('/:id', async (req, res) => {
  try {
    const contentId = req.params.id
    const rootOrg = req.header('rootOrg')
    if (!rootOrg) {
      res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
      return
    }
    const response = await axios.delete(
      `${API_END_POINTS.contentRating(contentId, extractUserIdFromRequest(req))}`,
      {
        ...axiosRequestConfig,
        headers: {
          rootOrg,
        },
      }
    )
    res.status(response.status).send(response.data)
  } catch (err) {
    res.status((err && err.response && err.response.status) || 500).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})

ratingApi.get('/content/average-ratingInfo/:contentId', async (req, res) => {
  try {
    const contentId = req.params.id
    const rootOrg = req.header('rootOrg')
    if (!rootOrg) {
      res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
      return
    }
    const response = await axios.get(
      API_END_POINTS.contentAverageRatingInfo(contentId),
      {
        ...axiosRequestConfig,
        headers: {
          rootOrg,
        },
      }
    )
    res.status(response.status).send(response.data)
  } catch (err) {
    res.status((err && err.response && err.response.status) || 500).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})

ratingApi.post('/content/ratings', async (req, res) => {
  try {
    const rootOrg = req.header('rootOrg')
    if (!rootOrg) {
      res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
      return
    }
    const response = await axios.post(
      API_END_POINTS.contentRatings,
      req.body,
      {
        ...axiosRequestConfig,
        headers: {
          rootOrg,
        },
      }
    )
    res.status(response.status).send(response.data)
  } catch (err) {
    res.status((err && err.response && err.response.status) || 500).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})
