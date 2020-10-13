import axios from 'axios'
import { Router } from 'express'
import { axiosRequestConfig } from '../configs/request.config'
import { CONSTANTS } from '../utils/env'
import { logError } from '../utils/logger'
import { ERROR } from '../utils/message'
import { extractUserIdFromRequest } from '../utils/requestExtract'

const unknown = 'Network Apis:- Failed due to unknown reason'
const apiEndpoints = {
  getConnectionEstablishedData: `${CONSTANTS.NETWORK_SERVICE_BACKEND}/connections/profile/fetch/established`,
  getConnectionRequestsData: `${CONSTANTS.NETWORK_SERVICE_BACKEND}/connections/profile/fetch/requested`,
<<<<<<< HEAD
  getConnectionSuggestsData: `${CONSTANTS.NETWORK_SERVICE_BACKEND}/connections/profile/find/suggests`,
  postConnectionAddData: `${CONSTANTS.NETWORK_SERVICE_BACKEND}/connections/add`,
  postConnectionRecommendationData: `${CONSTANTS.NETWORK_SERVICE_BACKEND}/connections/profile/find/recommended`,
  postConnectionUpdateData: `${CONSTANTS.NETWORK_SERVICE_BACKEND}/connections/update`,
=======
  getSuggestedConnectionData: `${CONSTANTS.NETWORK_SERVICE_BACKEND}/connections/profile/find/suggests`,
  postAddConnectionData: `${CONSTANTS.NETWORK_SERVICE_BACKEND}/connections/add`,
  postConnectionRecommendationData: `${CONSTANTS.NETWORK_SERVICE_BACKEND}/connections/profile/find/recommended`,
  postUpdateConnectionData: `${CONSTANTS.NETWORK_SERVICE_BACKEND}/connections/update`,
>>>>>>> 5bc29914bb8452851190595e5a491582546f174a
}

export const networkConnectionApi = Router()

<<<<<<< HEAD
networkConnectionApi.get('/connections/requested', async (req, res) => {
=======
networkConnectionApi.get('/get/connections/requested', async (req, res) => {
>>>>>>> 5bc29914bb8452851190595e5a491582546f174a
  try {
    const rootOrg = req.headers.rootorg
    const userId = extractUserIdFromRequest(req)

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
        userId,
      },
    })
    res.send((response.data))

  } catch (err) {
    logError('CONNECTIONS REQUESTS ERROR> ', err)
    res.status((err && err.response && err.response.status) || 500).send(
      (err && err.response && err.response.data) || {
        error: unknown,
      }
    )
  }
})

networkConnectionApi.get('/connections/established', async (req, res) => {
  try {
    const rootOrg = req.headers.rootorg
    const userId = extractUserIdFromRequest(req)

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
        userId,
      },
    })
    res.send((response.data))

  } catch (err) {
    logError('CONNECTIONS ERROR', err)
    res.status((err && err.response && err.response.status) || 500).send(
      (err && err.response && err.response.data) || {
        error: unknown,
      }
    )
  }
})

networkConnectionApi.get('/connections/suggests', async (req, res) => {
  try {
    const rootOrg = req.headers.rootorg
    const userId = extractUserIdFromRequest(req)

    if (!rootOrg) {
      res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
      return
    }
    if (!userId) {
      res.status(400).send(ERROR.GENERAL_ERR_MSG)
      return
    }
    const response = await axios.get(apiEndpoints.getConnectionSuggestsData, {
      ...axiosRequestConfig,
      headers: {
        rootOrg,
        userId,
      },
    })
    res.send((response.data))

  } catch (err) {
    logError('SUGGESTS ERROR >', err)
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

<<<<<<< HEAD
=======
    // logInfo('org, rootOrg, contentId', org, rootOrg, contentId)
>>>>>>> 5bc29914bb8452851190595e5a491582546f174a
    if (!rootOrg) {
      res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
      return
    }
    if (!userId || !connectionId) {
      res.status(400).send(ERROR.GENERAL_ERR_MSG)
      return
    }

    const body = req.body
<<<<<<< HEAD
=======

>>>>>>> 5bc29914bb8452851190595e5a491582546f174a
    const response = await axios.post(
      apiEndpoints.postConnectionAddData,
      body,
      {
        ...axiosRequestConfig,
        headers: {
          rootOrg,
        },
      }
    )
    res.send(response.data)

  } catch (err) {
    logError('ADD CONNECTION ERROR > ', err)
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
<<<<<<< HEAD
=======

>>>>>>> 5bc29914bb8452851190595e5a491582546f174a
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
      apiEndpoints.postConnectionUpdateData,
      body,
      {
        ...axiosRequestConfig,
        headers: {
          rootOrg,
        },
      }
    )
    res.send(response.data)

  } catch (err) {
    logError('UPDATE CONNECTION ERROR > ', err)
    res.status((err && err.response && err.response.status) || 500).send(
      (err && err.response && err.response.data) || {
        error: unknown,
      }
    )
  }
})

networkConnectionApi.post('/connections/recommended', async (req, res) => {
  try {
    const body = req.body
<<<<<<< HEAD
=======

>>>>>>> 5bc29914bb8452851190595e5a491582546f174a
    const rootOrg = req.header('rootorg') || 'igot'

    if (!rootOrg) {
      res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
      return
    }

    const response = await axios.post(
      apiEndpoints.postConnectionRecommendationData,
      body,
      {
        ...axiosRequestConfig,
        headers: {
          rootOrg,
        },
      }
    )
    res.send(response.data)

  } catch (err) {
    logError('RECOMMENDED ERROR > ', err)
    res.status((err && err.response && err.response.status) || 500).send(
      (err && err.response && err.response.data) || {
        error: unknown,
      }
    )
  }
})
