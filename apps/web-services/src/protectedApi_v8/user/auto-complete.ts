import axios from 'axios'
import { Router } from 'express'
import { axiosRequestConfig } from '../../configs/request.config'
import { CONSTANTS } from '../../utils/env'
import { getStringifiedQueryParams } from '../../utils/helpers'
import { ERROR } from '../../utils/message'

const API_END_POINTS = {
  users: (rootOrg: string, searchItem: string, queryParams?: string) =>
    `${CONSTANTS.USER_PROFILE_API_BASE}/user/autocomplete/${rootOrg}/all/${searchItem}?${queryParams}`,
}

export const autocompleteApi = Router()

autocompleteApi.get('/:query', async (req, res) => {
  const org = req.header('org')
  const rootOrg = req.header('rootOrg')
  try {
    const queryParams = getStringifiedQueryParams({
      filters: JSON.stringify({
        dealer_code: req.query.dealerCode,
      }),
      source_fields: req.query.sourceFields ? JSON.stringify(req.query.sourceFields.split(',')) : undefined,
    })
    if (!org || !rootOrg) {
      res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
      return
    }
    const url = API_END_POINTS.users(rootOrg, req.params.query, queryParams)
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
