import axios from 'axios'
import { Request, Response, Router } from 'express'
import { axiosRequestConfig } from '../../configs/request.config'
import { CONSTANTS } from '../../utils/env'
import { ERROR } from '../../utils/message'
import { extractUserIdFromRequest } from '../../utils/requestExtract'
const apiEndPoints = {
  add: `${CONSTANTS.SB_EXT_API_BASE}/v1/user/topic/add`,
  autocomplete: `${CONSTANTS.INTEREST_API_BASE}/v1/interests/auto`,
  interestV2: `${CONSTANTS.INTEREST_API_BASE}/v1/users`,
  modify: `${CONSTANTS.INTEREST_API_BASE}/v2/users`,
  multiple: `${CONSTANTS.INTEREST_API_BASE}/v3/users`,
  read: `${CONSTANTS.SB_EXT_API_BASE}/v1/user/topic/read`,
}

const GENERAL_ERROR_MSG = 'Failed due to unknown reason'

export const topicsApi = Router()
topicsApi.get('/', async (req: Request, res: Response) => {
  try {
    const userId = extractUserIdFromRequest(req)
    const response = await axios.get(`${apiEndPoints.read}/${userId}`, axiosRequestConfig)

    if (
      response.data &&
      response.data.result &&
      response.data.result.response &&
      response.data.result.response.topics
    ) {
      res.send(response.data.result.response.topics)
      return
    }

    res.status(500).send(response.data)
  } catch (err) {
    res.status((err && err.response && err.response.status) || 500).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})

topicsApi.post('/', async (req: Request, res: Response) => {
  try {
    const userId = extractUserIdFromRequest(req)
    const body = {
      request: {
        ...req.body,
        userId,
      },
    }
    const response = await axios.post(apiEndPoints.add, body, axiosRequestConfig)
    res.json(response.data)
  } catch (err) {
    res.status((err && err.response && err.response.status) || 500).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})

topicsApi.get('/v2', async (req: Request, res: Response) => {
  try {
    const userId = req.query.wid || extractUserIdFromRequest(req)
    const rootOrg = req.header('rootOrg')
    const response = await axios.get(`${apiEndPoints.interestV2}/${userId}/interests`, {
      ...axiosRequestConfig,
      headers: { rootOrg },
    })
    if (response.data && Array.isArray(response.data.user_interest)) {
      res.send(response.data.user_interest)
      return
    }
    res.status(200).send([])
  } catch (err) {
    res.status((err && err.response && err.response.status) || 500).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})

topicsApi.delete('/', async (req: Request, res: Response) => {
  try {
    const rootOrg = req.header('rootOrg')
    const userId = extractUserIdFromRequest(req)

    if (!rootOrg) {
      res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
      return
    }
    const response = await axios.delete(`${apiEndPoints.modify}/${userId}/interests`, {
      ...axiosRequestConfig,
      data: req.body,
      headers: { rootOrg },
    })

    res.status(response.status).send(response.data)
  } catch (err) {
    res
      .status((err && err.response && err.response.status) || 500)
      .send((err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      })
  }
})

topicsApi.patch('/addMultiple', async (req: Request, res: Response) => {
  try {
    const rootOrg = req.header('rootOrg')
    const userId = extractUserIdFromRequest(req)
    const response = await axios.patch(`${apiEndPoints.multiple}/${userId}/interests`, req.body, {
      ...axiosRequestConfig,
      headers: { rootOrg },
    })
    res.json(response.data)
  } catch (err) {
    res.status((err && err.response && err.response.status) || 500).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})

topicsApi.patch('/', async (req: Request, res: Response) => {
  try {
    const rootOrg = req.header('rootOrg')
    const userId = extractUserIdFromRequest(req)
    const response = await axios.patch(`${apiEndPoints.modify}/${userId}/interests`, req.body, {
      ...axiosRequestConfig,
      headers: { rootOrg },
    })
    res.json(response.data)
  } catch (err) {
    res.status((err && err.response && err.response.status) || 500).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})

topicsApi.get('/suggested', async (req: Request, res: Response) => {
  try {
    const userId = extractUserIdFromRequest(req)
    const rootOrg = req.header('rootOrg')
    const org = req.header('org')
    const langCode = req.header('locale')
    const response = await axios.get(`${apiEndPoints.interestV2}/${userId}/interests/suggested`, {
      ...axiosRequestConfig,
      headers: { rootOrg, org, langCode },
    })

    res.send(response.data)
  } catch (err) {
    res.status((err && err.response && err.response.status) || 500).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})

topicsApi.get('/autocomplete', async (req: Request, res: Response) => {
  try {
    const { query } = req.query
    const rootOrg = req.header('rootOrg')
    const org = req.header('org')
    const langCode = req.header('locale')
    // tslint:disable-next-line: no-console
    console.log(
      // tslint:disable-next-line: max-line-length
      `AUTOCOMPLETE::${apiEndPoints.autocomplete}?query=${query}    :: rootOrg:${rootOrg}, org:${org}, langCode:${langCode}`
    )
    const response = await axios.get(`${apiEndPoints.autocomplete}?query=${query}`, {
      ...axiosRequestConfig,
      headers: { rootOrg, org, langCode },
    })
    res.send(response.data)
  } catch (err) {
    res.status((err && err.response && err.response.status) || 500).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})
