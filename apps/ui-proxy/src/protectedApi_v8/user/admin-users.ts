import axios from 'axios'
import { Router } from 'express'
import { axiosRequestConfig } from '../../configs/request.config'
import { CONSTANTS } from '../../utils/env'
import { logError } from '../../utils/logger'
import { ERROR } from '../../utils/message'

const API_END_POINTS = {
  createuser: `${CONSTANTS.USER_CREATE_API_BASE}/users`,
}
export const usersApi = Router()

usersApi.post('/createuser', async (req, res) => {
  try {
    const keycloak: boolean = JSON.parse(req.query.keycloak)
    const rootOrg = req.header('rootOrg')
    if (!rootOrg) {
      res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
      return
    }
    const response = await axios.request({
      auth: {
        password: CONSTANTS.USER_CREATE_PASSWORD,
        username: CONSTANTS.USER_CREATE_USERNAME,
      },
      data: req.body,
      method: 'POST',
      ...axiosRequestConfig,
      params: {
        keycloakOnly: keycloak,
        pidOnly: true,
        rootOrg,
      },
      url: API_END_POINTS.createuser,
    })
    res.send(response.data)
  } catch (err) {
    logError('CREATE USER ERR -> ', err)
    res.status((err && err.response && err.response.status) || 500)
      .send((err && err.response && err.response.data) || {
        error: 'Failed due to unknown reason',
      })
  }
})
