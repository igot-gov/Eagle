import axios from 'axios'
import { Router } from 'express'
import { axiosRequestConfig } from '../../configs/request.config'
import { CONSTANTS } from '../../utils/env'
import { extractUserIdFromRequest } from '../../utils/requestExtract'

const API_END_POINTS = {
  activities: (userId: string) =>
    `${CONSTANTS.SB_EXT_API_BASE_3}/v1/activities/user/${userId}`,
}

export const activity = Router()

activity.get('/', async (req, res) => {
  const wid = extractUserIdFromRequest(req)

  const rootOrg = req.header('rootOrg')
  const org = req.header('org')
  const url = `${API_END_POINTS.activities(wid)}`
  try {
    const response = await axios.get(url, {
      ...axiosRequestConfig,
      headers: { 'Content-Type': 'application/json', wid, rootOrg, org },
    })
    const data = response.data
    res.send(data)
  } catch (err) {
    // tslint:disable-next-line: no-console
    console.log('err::', err)
    res.status((err && err.response && err.response.status) || 500).send(
      (err && err.response && err.response.data) || {
        error: 'Failed due to unknown reason',
      }
    )
  }
})
