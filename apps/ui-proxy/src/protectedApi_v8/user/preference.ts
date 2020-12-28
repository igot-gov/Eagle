import axios from 'axios'
import { Router } from 'express'
import { axiosRequestConfig } from '../../configs/request.config'
import { CONSTANTS } from '../../utils/env'
import { ERROR } from '../../utils/message'
import { extractUserIdFromRequest } from '../../utils/requestExtract'

const apiEndpoints = {
  preferences: `${CONSTANTS.PREFERENCE_API_BASE}/v1/user`,
}

export async function getUserPreference(userId: string, rootOrg: string) {
  try {
    const response = await axios.get<JSON>(`${apiEndpoints.preferences}/${userId}/preferences`, {
      ...axiosRequestConfig,
      headers: { rootOrg },
    })
    return response.data
  } catch (error) {
    return {}
  }
}

export const protectedPreference = Router()

protectedPreference.get('/', async (req, res) => {
  try {
    const userId = req.query.wid || extractUserIdFromRequest(req)
    const rootOrg = req.header('rootOrg')
    if (!rootOrg) {
      res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
      return
    }
    const response = await getUserPreference(userId, rootOrg)
    res.json(response)
  } catch (err) {
    res
      .status((err && err.response && err.response.status) || 500)
      .send((err && err.response && err.response.data) || err)
  }
})

protectedPreference.put('/', async (req, res) => {
  try {
    const userId = extractUserIdFromRequest(req)
    const rootOrg = req.header('rootOrg')
    if (!rootOrg) {
      res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
      return
    }
    const response = await axios.put(
      `${apiEndpoints.preferences}/${userId}/preferences`,
      req.body,
      { ...axiosRequestConfig, headers: { rootOrg } }
    )

    res.status(response.status).send(response.data)
  } catch (err) {
    res
      .status((err && err.response && err.response.status) || 500)
      .send((err && err.response && err.response.data) || err)
  }
})
