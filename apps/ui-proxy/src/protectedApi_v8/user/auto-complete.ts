import axios from 'axios'
import { Router } from 'express'
import { axiosRequestConfig } from '../../configs/request.config'
import { CONSTANTS } from '../../utils/env'
import { ERROR } from '../../utils/message'

const API_END_POINTS = {
  users: (queryParams: string) =>
    `${CONSTANTS.NETWORK_HUB_SERVICE_BACKEND}/v1/user/autocomplete?${queryParams}`,
  usersByDepartment: (rootOrg: string, searchItem: string) =>
    `${CONSTANTS.USER_PROFILE_API_BASE}/user/autocomplete/${rootOrg}/department/${searchItem}`,
}

export const autocompleteApi = Router()

autocompleteApi.post('/department/:query', async (req, res) => {
  const org = req.header('org')
  const rootOrg = req.header('rootOrg')
  try {
    if (!org || !rootOrg) {
      res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
      return
    }
    const url = API_END_POINTS.usersByDepartment(rootOrg, req.params.query)

    const response = await axios.post(
      url,
      req.body,
      { ...axiosRequestConfig, headers: { rootOrg } }
    )
    res.send(response.data)
  } catch (err) {
    return err
  }
})

autocompleteApi.get('/:query', async (req, res) => {
  const org = req.header('org')
  const rootOrg = req.header('rootOrg')
  try {
    const queryParams = req.params.query
    if (!org || !rootOrg) {
      res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
      return
    }

    const url = API_END_POINTS.users('searchString=' + queryParams)
    const response = await axios({
      ...axiosRequestConfig,
      headers: { rootOrg },
      method: 'GET',
      url,
    })
    res.send(response.data)
  } catch (err) {
    res.status((err && err.response && err.response.status) || 500)
      .send((err && err.reponse && err.response.data) || {
        error: 'Failed due to unknown reason',
      })
  }
})
