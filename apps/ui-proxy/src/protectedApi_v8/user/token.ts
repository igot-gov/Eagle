import axios from 'axios'
import { Router } from 'express'
import { axiosRequestConfig } from '../../configs/request.config'
import { CONSTANTS } from '../../utils/env'

const apiEndpoints = {
  tokenWithCode: `${CONSTANTS.CONTENT_API_BASE}/user-access-token?code=`,
  tokenWithEmail: `${CONSTANTS.CONTENT_API_BASE}/access-token?email=`,
}

export const userTokenApi = Router()

userTokenApi.get('/', async (req, res) => {
  try {
    const { email, code, redirectUrl } = req.query
    if (email) {
      const response = await axios.get(`${apiEndpoints.tokenWithEmail}${email}`, axiosRequestConfig)
      res.json(response)
    } else if (code && redirectUrl) {
      const response = await axios.get(
        `${apiEndpoints.tokenWithCode}${code}&redirecturi=${redirectUrl}`,
        axiosRequestConfig
      )
      res.json(response)
    } else {
      res
        .status(400)
        .send(
          'You must pass (email) || (code && redirectUrl) in query parameter, to retrieve the code.'
        )
    }
  } catch (err) {
    res.status((err && err.response && err.response.status) || 500).send(
      (err && err.response && err.response.data) || {
        error: 'Failed due to unknown reason',
      }
    )
  }
})
