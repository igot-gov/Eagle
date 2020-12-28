import axios from 'axios'
import { Router } from 'express'
import { axiosRequestConfig } from '../../configs/request.config'
import { CONSTANTS } from '../../utils/env'
const API_END_POINTS = {
  viewprofile: `${CONSTANTS.NODE_API_BASE}/userprofiles/pathfinders/viewprofile`,
}
export const userMiniProfile = Router()

userMiniProfile.get('/:userId', async (req, res) => {
  const userId = req.params.userId
  const wid = userId
  const rootOrg = req.header('rootOrg')
  const org = req.header('org')
  const url = API_END_POINTS.viewprofile
  try {
    const response = await axios.get(url, {
      ...axiosRequestConfig,
      headers: { 'Content-Type': 'application/json', user_id: userId, wid, rootOrg, org },
    })
    const data = response.data
    res.send(data)
  } catch (err) {
    res.status((err && err.response && err.response.status) || 500).send(
      (err && err.response && err.response.data) || {
        error: 'Failed due to unknown reason',
      }
    )
  }
})
