import axios from 'axios'
import { Router } from 'express'
import { axiosRequestConfig } from '../configs/request.config'
import { CONSTANTS } from '../utils/env'
import { logError, logInfo } from '../utils/logger'
import { ERROR } from '../utils/message'

const unknown = 'Failed due to unknown reason'
const apiEndpoints = {
  getConnectionRequestsData: `${CONSTANTS.NETWORK_SERVICE_BACKEND}/connections/profile/fetch/requested`,
  getConnectionEstablishedData: `${CONSTANTS.NETWORK_SERVICE_BACKEND}/connections/profile/fetch/established`,
  getSuggestedConnectionData: `${CONSTANTS.NETWORK_SERVICE_BACKEND}/connections/profile/find/suggests`,
  postConnectionRecommendationData: `${CONSTANTS.NETWORK_SERVICE_BACKEND}/connections/profile/find/recommended`,
  postAddConnectionData: `${CONSTANTS.NETWORK_SERVICE_BACKEND}/connections/add`,
  postUpdateConnectionData: `${CONSTANTS.NETWORK_SERVICE_BACKEND}/connections/update`,
}

export const networkConnectionApi = Router()

networkConnectionApi.get('/get/connections/requested', async (req, res) => {
  console.log('Connection requests GET API called=====>', req.header('userId') || 'uuid missing')
  try {
    const rootOrg = req.headers.rootorg
    const userId = req.header('userId')

    if (!rootOrg) {
      res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
      return
    }
    if (!userId) {
      res.status(400).send(ERROR.GENERAL_ERR_MSG)
      return
    }
    const response = await axios.get(apiEndpoints.getConnectionRequestsData, {
      ...axiosRequestConfig,
      headers: {
        rootOrg,
        userId
      }
    })
    res.send((response.data))

  } catch (err) {
    logError(err)
    res.status((err && err.response && err.response.status) || 500).send(
      (err && err.response && err.response.data) || {
        error: unknown,
      }
    )
  }
})

networkConnectionApi.get('/get/connections/established', async (req, res) => {
  console.log('Connection requests GET API called=====>', req.header('userId') || 'uuid missing')
  try {
    const rootOrg = req.headers.rootorg
    const userId = req.header('userId')

    if (!rootOrg) {
      res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
      return
    }
    if (!userId) {
      res.status(400).send(ERROR.GENERAL_ERR_MSG)
      return
    }
    const response = await axios.get(apiEndpoints.getConnectionEstablishedData, {
      ...axiosRequestConfig,
      headers: {
        rootOrg,
        userId
      }
    })
    res.send((response.data))

  } catch (err) {
    logError(err)
    res.status((err && err.response && err.response.status) || 500).send(
      (err && err.response && err.response.data) || {
        error: unknown,
      }
    )
  }
})

networkConnectionApi.get('/connections/suggests', async (req, res) => {
  console.log('Connection requests GET API called=====>', req.header('userId') || 'uuid missing')
console.log("exec - /connections/suggests");
  try {
    const rootOrg = req.headers.rootorg
    const userId = req.header('userId')

    if (!rootOrg) {
      res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
      return
    }
    if (!userId) {
      res.status(400).send(ERROR.GENERAL_ERR_MSG)
      return
    }
    const response = await axios.get(apiEndpoints.getSuggestedConnectionData, {
      ...axiosRequestConfig,
      headers: {
        rootOrg,
        userId
      }
    })
    res.send((response.data))

  } catch (err) {
    logError('SUGGEST ERROR >', err)
    res.status((err && err.response && err.response.status) || 500).send(
      (err && err.response && err.response.data) || {
        error: unknown,
      }
    )
  }
})


networkConnectionApi.post('/add/connection', async (req, res) => {
  try {
    const rootOrg = req.header('rootorg')
    const connectionId = req.body.connectionId
    const userId = req.body.userId


    // logInfo('org, rootOrg, contentId', org, rootOrg, contentId)
    if (!rootOrg) {
      res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
      return
    }
    if (!userId || !connectionId) {
      res.status(400).send(ERROR.GENERAL_ERR_MSG)
      return
    }

    const body = req.body
    console.log('body========>', JSON.stringify(body))

    const response = await axios.post(
      apiEndpoints.postAddConnectionData,
      body,
      {
        ...axiosRequestConfig,
        headers: {
          rootOrg,
        }
      }
    )
    res.send(response.data)

  } catch (err) {
    logError(err)
    res.status((err && err.response && err.response.status) || 500).send(
      (err && err.response && err.response.data) || {
        error: unknown,
      }
    )
  }
})

networkConnectionApi.post('/update/connection', async (req, res) => {
  try {
    const body = req.body
    console.log('body========>', JSON.stringify(body))

    const rootOrg = req.header('rootorg')
    const connectionId = req.body.connectionId
    const userId = req.body.userId
    const status = req.body.status

    if (!rootOrg) {
      res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
      return
    }
    if (!userId || !connectionId || !status) {
      res.status(400).send(ERROR.GENERAL_ERR_MSG)
      return
    }

    const response = await axios.post(
      apiEndpoints.postUpdateConnectionData,
      body,
      {
        ...axiosRequestConfig,
        headers: {
          rootOrg,
        }
      }
    )
    res.send(response.data)

  } catch (err) {
    logError(err)
    res.status((err && err.response && err.response.status) || 500).send(
      (err && err.response && err.response.data) || {
        error: unknown,
      }
    )
  }
})

networkConnectionApi.post('/recommended', async (req, res) => {
  try {
    const body = req.body
    console.log('body========>', JSON.stringify(body))

    const rootOrg = req.header('rootorg') || 'igot'

    if (!rootOrg) {
      res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
      return
    }

    const response = await axios.post(
      apiEndpoints.postAddConnectionData,
      body,
      {
        ...axiosRequestConfig,
        headers: {
          rootOrg,
        }
      }
    )
    res.send(response.data)

  } catch (err) {
    logError(err)
    res.status((err && err.response && err.response.status) || 500).send(
      (err && err.response && err.response.data) || {
        error: unknown,
      }
    )
  }
})


