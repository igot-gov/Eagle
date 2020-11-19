import axios from 'axios'
import { Router } from 'express'
import { axiosRequestConfig } from '../../configs/request.config'
import { CONSTANTS } from '../../utils/env'

const API_END_POINTS = {
  emailToUserId: CONSTANTS.SB_EXT_API_BASE + '/v1/user/finduuid?userEmail=',
}

export async function getUserId(userEmail: string) {
  const url = `${API_END_POINTS.emailToUserId}${userEmail}`
  const response = await axios.get(url, axiosRequestConfig)
  const result = response.data.result
  let data: { email: string; userId: string | null }
  if (result.error) {
    data = {
      email: userEmail,
      userId: null,
    }
  } else {
    data = result.result
  }

  return data
}

export const emailToUserIdApi = Router()

emailToUserIdApi.get('/:emailId', async (req, res) => {
  try {
    const userEmail = req.params.emailId
    const data = await getUserId(userEmail)
    res.send(data)
  } catch (err) {
    res.status((err && err.response && err.response.status) || 500).send(
      (err && err.response && err.response.data) || {
        error: 'Failed due to unknown reason',
      }
    )
  }
})
