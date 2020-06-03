import axios from 'axios'
import { Request, Response, Router } from 'express'
import { axiosRequestConfig } from '../../configs/request.config'
import { CONSTANTS } from '../../utils/env'
import { ERROR } from '../../utils/message'
import { extractUserIdFromRequest } from '../../utils/requestExtract'

export const notificationsApi: Router = Router()

const GENERAL_ERROR_MSG = 'Failed due to unknown reason'

const apiEndpoints = {
  notifications: `${CONSTANTS.NOTIFICATIONS_API_BASE}/v1`,
  settings: (userId: string) => `${CONSTANTS.NOTIFICATIONS_API_BASE}/v1/users/${userId}/events`,
}
// Update notification settings
notificationsApi.patch('/settings', async (req: Request, res: Response) => {
  try {
    const rootOrg = req.header('rootOrg')
    const langCode = req.header('locale')
    if (!rootOrg) {
      res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
      return
    }
    const userId = extractUserIdFromRequest(req)
    const response = await axios.patch(apiEndpoints.settings(userId), req.body, {
      ...axiosRequestConfig,
      headers: { langCode, rootOrg },
    })
    return res.status(response.status).send(response.data)
  } catch (err) {
    return res.status((err && err.response && err.response.status) || 500).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})

// Get user notifications
notificationsApi.get('/', async (req: Request, res: Response) => {
  try {
    const rootOrg = req.header('rootOrg')
    if (!rootOrg) {
      res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
      return
    }

    const userId = extractUserIdFromRequest(req)
    const { classification, page, size } = req.query

    const notificationData = await axios
      .get(`${apiEndpoints.notifications}/users/${userId}/notifications`, {
        ...axiosRequestConfig,
        headers: { rootOrg },
        params: { classification, page, size },
      })
      .then((response) => response.data)

    return res.send(notificationData)
  } catch (err) {
    return res.status((err && err.response && err.response.status) || 500).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})

// Update notification seen status
notificationsApi.patch(
  '/:notificationId?/:classification?',
  async (req: Request, res: Response) => {
    try {
      const rootOrg = req.header('rootOrg')
      if (!rootOrg) {
        res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
        return
      }

      const userId = extractUserIdFromRequest(req)
      const { notificationId } = req.params
      let url = `${apiEndpoints.notifications}/users/${userId}/notifications`
      if (notificationId) {
        url += `/${notificationId}`
      }
      const response = await axios
        .patch(url, req.body, { ...axiosRequestConfig, headers: { rootOrg } })
        .then((resp) => resp.data)

      return res.send(response)
    } catch (err) {
      return res.status((err && err.response && err.response.status) || 500).send(
        (err && err.response && err.response.data) || {
          error: GENERAL_ERROR_MSG,
        }
      )
    }
  }
)

// notifications settings APIs
notificationsApi.get('/settings', async (req: Request, res: Response) => {
  try {
    const rootOrg = req.header('rootOrg')
    const langCode = req.header('locale')
    if (!rootOrg) {
      res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
      return
    }
    const userId = extractUserIdFromRequest(req)
    const response = await axios.get(apiEndpoints.settings(userId), {
      ...axiosRequestConfig,
      headers: { rootOrg, langCode },
    })
    return res.status(response.status).send(response.data)
  } catch (err) {
    return res.status((err && err.response && err.response.status) || 500).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})
