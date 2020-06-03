import axios from 'axios'
import { Request, Response, Router } from 'express'
import { axiosRequestConfig } from '../../configs/request.config'
import {
  IFeedback,
  IFeedbackSearch,
  IFeedbackSearchQuery,
  IFeedbackSubmit,
} from '../../models/feedback.model'
import { CONSTANTS } from '../../utils/env'
import { ERROR } from '../../utils/message'
import { extractUserIdFromRequest } from '../../utils/requestExtract'

export const feedbackV2Api = Router()

const GENERAL_ERROR_MSG = 'Failed due to unknown reason'

const apiEndpoints = {
  feedback: `${CONSTANTS.FEEDBACK_API_BASE}/v1`,
}

// Middleware function for content request and service request submission
const sendSentimentNeutralFeedback = async (req: Request, res: Response) => {
  try {
    const rootOrg = req.header('rootOrg')
    if (!rootOrg) {
      res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
      return
    }

    const uuid = extractUserIdFromRequest(req)
    const feedback = req.body as IFeedback

    const body: IFeedbackSubmit = {
      text: feedback.text,
      type: feedback.type,
      user_id: uuid,
    }

    if (feedback.rootFeedbackId) {
      body.rootFeedbackId = feedback.rootFeedbackId
    }

    if (feedback.category) {
      body.category = feedback.category
    }

    const response = await axios.post(`${apiEndpoints.feedback}/feedback/submit`, body, {
      ...axiosRequestConfig,
      headers: { rootOrg },
      params: { role: feedback.role },
    })

    return res.send(response.data)
  } catch (err) {
    return res.status((err && err.response && err.response.status) || 500).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
}

// Submit platform feedback
feedbackV2Api.post('/platform', async (req: Request, res: Response) => {
  try {
    const uuid = extractUserIdFromRequest(req)
    const feedback = req.body as IFeedback
    const rootOrg = req.headers.rootorg
    if (!rootOrg) {
      res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
      return
    }

    const body: IFeedbackSubmit = {
      sentiment: feedback.sentiment,
      text: feedback.text,
      type: feedback.type,
      user_id: uuid,
    }

    if (feedback.rootFeedbackId) {
      body.rootFeedbackId = feedback.rootFeedbackId
    }

    if (feedback.category) {
      body.category = feedback.category
    }

    const response = await axios.post(`${apiEndpoints.feedback}/feedback/submit`, body, {
      ...axiosRequestConfig,
      headers: { rootOrg },
      params: { role: feedback.role },
    })
    return res.send(response.data)
  } catch (err) {
    return res.status((err && err.response && err.response.status) || 500).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})

// Submit content feedback
feedbackV2Api.post('/content/:contentId', async (req: Request, res: Response) => {
  try {
    const rootOrg = req.header('rootOrg')
    if (!rootOrg) {
      return res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
    }

    const { contentId } = req.params
    const uuid = extractUserIdFromRequest(req)
    const feedback = req.body as IFeedback

    const body: IFeedbackSubmit = {
      content_id: contentId,
      text: feedback.text,
      type: feedback.type,
      user_id: uuid,
    }

    if (feedback.rootFeedbackId) {
      body.rootFeedbackId = feedback.rootFeedbackId
    }

    if (feedback.sentiment) {
      body.sentiment = feedback.sentiment
    }

    const response = await axios
      .post(`${apiEndpoints.feedback}/feedback/submit`, body, {
        ...axiosRequestConfig,
        headers: { rootOrg },
        params: { role: feedback.role },
      })
      .then((resp) => resp.data)

    return res.send(response)
  } catch (err) {
    return res.status((err && err.response && err.response.status) || 500).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})

// Submit content request
feedbackV2Api.post('/content-request', sendSentimentNeutralFeedback)

// Submit service request
feedbackV2Api.post('/service-request', sendSentimentNeutralFeedback)

// Get feedback summary
feedbackV2Api.get('/feedback-summary', async (req: Request, res: Response) => {
  try {
    const rootOrg = req.header('rootOrg')
    if (!rootOrg) {
      return res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
    }

    const uuid = extractUserIdFromRequest(req)

    const feedbackSummary = await axios
      .get(`${apiEndpoints.feedback}/users/${uuid}/feedback-summary`, {
        headers: { rootOrg },
      })
      .then((response) => response.data)

    return res.send(feedbackSummary)
  } catch (err) {
    return res.status((err && err.response && err.response.status) || 500).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})

// Search
feedbackV2Api.post('/search', async (req: Request, res: Response) => {
  try {
    const rootOrg = req.header('rootOrg')
    if (!rootOrg) {
      return res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
    }

    const uuid = extractUserIdFromRequest(req)
    const searchObj: IFeedbackSearchQuery = req.body

    const body: IFeedbackSearch = {
      all: searchObj.all,
      filters: searchObj.filters,
      from: searchObj.from,
      query: searchObj.query,
      size: searchObj.size,
      user_id: uuid,
      viewed_by: searchObj.viewedBy,
    }

    const searchResults = await axios
      .post(`${apiEndpoints.feedback}/feedback/search`, body, {
        headers: { rootOrg },
      })
      .then((response) => response.data)

    return res.send(searchResults)
  } catch (err) {
    return res.status((err && err.response && err.response.status) || 500).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})

// Fetch a feedback thread
feedbackV2Api.get('/:feedbackId', async (req: Request, res: Response) => {
  try {
    const rootOrg = req.header('rootOrg')
    if (!rootOrg) {
      return res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
    }

    const { feedbackId } = req.params
    const uuid = extractUserIdFromRequest(req)

    const feedbackThread = await axios
      .get(`${apiEndpoints.feedback}/feedback/${feedbackId}?user_Id=${uuid}`, {
        headers: { rootOrg },
      })
      .then((response) => response.data)

    return res.send(feedbackThread)
  } catch (err) {
    return res.status((err && err.response && err.response.status) || 500).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})

// Update feedback status
feedbackV2Api.patch('/:feedbackId', async (req: Request, res: Response) => {
  try {
    const rootOrg = req.header('rootOrg')
    if (!rootOrg) {
      return res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
    }

    const { feedbackId } = req.params
    const { category } = req.query

    const uuid = extractUserIdFromRequest(req)

    const response = await axios
      .patch(`${apiEndpoints.feedback}/users/${uuid}/feedback/${feedbackId}`, req.body, {
        headers: { rootOrg },
        params: { category },
      })
      .then((resp) => resp.data)

    return res.send(response)
  } catch (err) {
    return res.status((err && err.response && err.response.status) || 500).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})

// Get feedback categories/config
feedbackV2Api.get('/categories', async (req: Request, res: Response) => {
  try {
    const rootOrg = req.header('rootOrg')
    if (!rootOrg) {
      return res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
    }

    const feedbackConfig = await axios
      .get(`${apiEndpoints.feedback}/config`, {
        headers: { rootOrg },
      })
      .then((response) => response.data)

    return res.send(feedbackConfig)
  } catch (err) {
    return res.status((err && err.response && err.response.status) || 500).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})
