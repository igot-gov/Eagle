import axios from 'axios'
import { Router } from 'express'
import { axiosRequestConfig } from '../../configs/request.config'
import { CONSTANTS } from '../../utils/env'
import { ERROR } from '../../utils/message'
const API_END_POINTS = {
  accountSettings: `${CONSTANTS.NODE_API_BASE}/userprofiles/pathfinders/upsert`,
  resetPassword: `${CONSTANTS.RESET_PASSWORD}/pid/reset-password/generate-token`,
  viewProfile: `${CONSTANTS.NODE_API_BASE}/userprofiles/pathfinders/viewprofile`,
}
export const accountSettingsApi = Router()

accountSettingsApi.post('/resetPassword', async (_req, res) => {
  try {
    const resetPasswordUrl = API_END_POINTS.resetPassword
    const response = await axios.post(resetPasswordUrl, {})
    res.status(response.status).send(response.data)
  } catch (err) {
    res
      .status((err && err.response && err.response.status) || 500)
      .send((err && err.response && err.response.data) || err)
  }
})

accountSettingsApi.post('/', async (req, res) => {
  try {
    const org = req.header('org')
    const request = req.body
    const rootOrg = req.header('rootOrg')
    if (!rootOrg) {
      res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
      return
    }

    const url = API_END_POINTS.accountSettings
    const response = await axios.post(url, request, {
      ...axiosRequestConfig,
      headers: {
        'Content-Type': 'application/json',
        org,
        rootOrg,
      },
    })
    res.status(response.status).send(response.data)
  } catch (err) {
    res
      .status((err && err.response && err.response.status) || 500)
      .send((err && err.response && err.response.data) || err)
  }
})
