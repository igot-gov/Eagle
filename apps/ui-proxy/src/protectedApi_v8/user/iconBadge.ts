import axios from 'axios'
import { Router } from 'express'
import { axiosRequestConfig } from '../../configs/request.config'
import { CONSTANTS } from '../../utils/env'
import { extractUserIdFromRequest } from '../../utils/requestExtract'

const API_END_POINTS = {
  unreadNotificationCount: CONSTANTS.NOTIFICATIONS_API_BASE + '/v1/users',
}

export const iconBadgeApi = Router()

iconBadgeApi.get('/unseenNotificationCount', async (req, res) => {
  try {
    const uuid = extractUserIdFromRequest(req)
    const rootOrg = req.header('rootOrg')
    const response = await axios.get(
      `${API_END_POINTS.unreadNotificationCount}/${uuid}/notification-summary`,
      {
        ...axiosRequestConfig,
        headers: { rootOrg },
      }
    )
    res.json(response.data.totalCount)
  } catch (err) {
    res.status((err && err.response && err.response.status) || 500).send(
      (err && err.response && err.response.data) || {
        error: 'Failed due to unknown reason',
      }
    )
  }
})
