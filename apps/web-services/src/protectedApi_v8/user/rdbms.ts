import axios from 'axios'
import { Router } from 'express'
import { axiosRequestConfig } from '../../configs/request.config'
import { CONSTANTS } from '../../utils/env'
import { logError } from '../../utils/logger'
import { extractUserIdFromRequest } from '../../utils/requestExtract'

const API_ENDPOINTS = {
  conceptData: `${CONSTANTS.VIEWER_PLUGIN_RDBMS_API_BASE}/v1/db/conceptdata/resources`,
  execute: `${CONSTANTS.VIEWER_PLUGIN_RDBMS_API_BASE}/v1/users`,
}

const GENERAL_ERR_MSG = 'Failed due to unknown reason'

export const rdbmsApi = Router()

rdbmsApi.get('/initializeDb/:contentId', async (req, res) => {
  try {
    const uuid = extractUserIdFromRequest(req)
    const contentId = req.params.contentId
    const response = await axios.get(
      `${API_ENDPOINTS.execute}/${uuid}/resources/${contentId}/initialize`,
      axiosRequestConfig
    )
    res.send(response.data)
  } catch (err) {
    logError('INITIALIZE DB ERROR -> ', err)
    res.status((err && err.response && err.response.status) || 500)
      .send((err && err.response && err.response.data) || {
        error: GENERAL_ERR_MSG,
      })
  }
})

rdbmsApi.get('/conceptData/:contentId', async (req, res) => {
  try {
    const contentId = req.params.contentId
    const response = await axios.get(
      `${API_ENDPOINTS.conceptData}/${contentId}`,
      axiosRequestConfig
    )
    res.json(response.data)
  } catch (err) {
    logError('GET RDBMS CONCEPT DATA ERR -> ', err)
    res.status((err && err.response && err.response.status) || 500)
      .send((err && err.response && err.response.data) || {
        error: GENERAL_ERR_MSG,
      })
  }
})

rdbmsApi.get('/expectedOutput/:contentId', async (req, res) => {
  try {
    const uuid = extractUserIdFromRequest(req)
    const contentId = req.params.contentId
    const response = await axios.get(
      `${API_ENDPOINTS.execute}/${uuid}/resources/${contentId}`,
      axiosRequestConfig
    )
    res.json(response.data)
  } catch (err) {
    logError('GET EXPECTED OUTPUT ERR -> ', err)
    res.status((err && err.response && err.response.status) || 500)
      .send((err && err.response && err.response.data) || {
        error: GENERAL_ERR_MSG,
      })
  }
})

rdbmsApi.get('/dbstructure/:contentId', async (req, res) => {
  try {
    const uuid = extractUserIdFromRequest(req)
    const contentId = req.params.contentId
    const response = await axios.get(
      `${API_ENDPOINTS.execute}/${uuid}/resources/${contentId}/tabledata`,
      axiosRequestConfig
    )
    res.json(response.data)
  } catch (err) {
    logError('GET DB STRUCTURE ERR -> ', err)
    res.status((err && err.response && err.response.status) || 500)
      .send((err && err.response && err.response.data) || {
        error: GENERAL_ERR_MSG,
      })
  }
})

rdbmsApi.get('/tableRefresh/:contentId', async (req, res) => {
  try {
    const uuid = extractUserIdFromRequest(req)
    const contentId = req.params.contentId
    const response = await axios.get(
      `${API_ENDPOINTS.execute}/${uuid}/resources/${contentId}/tableinfo`,
      axiosRequestConfig
    )
    res.json(response.data)
  } catch (err) {
    logError('TABLE REFRESH ERR -> ', err)
    res.status((err && err.response && err.response.status) || 500)
      .send((err && err.response && err.response.data) || {
        error: GENERAL_ERR_MSG,
      })
  }
})

rdbmsApi.post('/executeQuery', async (req, res) => {
  try {
    const uuid = extractUserIdFromRequest(req)
    const response = await axios.post(
      `${API_ENDPOINTS.execute}/${uuid}/query/execute`,
      {
        ...req.body,
      },
      axiosRequestConfig
    )
    res.json(response.data)
  } catch (err) {
    logError('EXECUTE QUERY ERR -> ', err)
    res.status((err && err.response && err.response.status) || 500)
      .send((err && err.response && err.response.data) || {
        error: GENERAL_ERR_MSG,
      })
  }
})

rdbmsApi.post('/compareQuery', async (req, res) => {
  try {
    const uuid = extractUserIdFromRequest(req)
    const response = await axios.post(
      `${API_ENDPOINTS.execute}/${uuid}/querycompareexecute`,
      {
        ...req.body,
      },
      axiosRequestConfig
    )
    res.json(response.data)
  } catch (err) {
    logError('COMPARE QUERY ERR -> ', err)
    res.status((err && err.response && err.response.status) || 500)
      .send((err && err.response && err.response.data) || {
        error: GENERAL_ERR_MSG,
      })
  }
})

rdbmsApi.post('/playground', async (req, res) => {
  try {
    const uuid = extractUserIdFromRequest(req)
    const response = await axios.post(
      `${API_ENDPOINTS.execute}/${uuid}/query/playground`,
      {
        ...req.body,
      },
      axiosRequestConfig
    )
    res.json(response.data)
  } catch (err) {
    logError('PLAYGROUND ERR -> ', err)
    res.status((err && err.response && err.response.status) || 500)
      .send((err && err.response && err.response.data) || {
        error: GENERAL_ERR_MSG,
      })
  }
})

rdbmsApi.post('/compositeQuery/:type', async (req, res) => {
  try {
    const uuid = extractUserIdFromRequest(req)
    const type = req.params.type
    const response = await axios.post(
      `${API_ENDPOINTS.execute}/${uuid}/query/composite?type=${type}`,
      {
        ...req.body,
      },
      axiosRequestConfig
    )
    res.json(response.data)
  } catch (err) {
    logError('COMPOSITE QUERY ERR -> ', err)
    res.status((err && err.response && err.response.status) || 500)
      .send((err && err.response && err.response.data) || {
        error: GENERAL_ERR_MSG,
      })
  }
})

rdbmsApi.post('/verifyExercise/:contentId', async (req, res) => {
  try {
    const uuid = extractUserIdFromRequest(req)
    const contentId = req.params.contentId
    const response = await axios.post(
      `${API_ENDPOINTS.execute}/${uuid}/resources/${contentId}?type=verify`,
      {
        ...req.body,
      },
      axiosRequestConfig
    )
    res.json(response.data)
  } catch (err) {
    logError('VERIFY EXERCISE ERR -> ', err)
    res.status((err && err.response && err.response.status) || 500)
      .send((err && err.response && err.response.data) || {
        error: GENERAL_ERR_MSG,
      })
  }
})

rdbmsApi.post('/submitExercise/:contentId', async (req, res) => {
  try {
    const uuid = extractUserIdFromRequest(req)
    const contentId = req.params.contentId
    const response = await axios.post(
      `${API_ENDPOINTS.execute}/${uuid}/resources/${contentId}?type=submit`,
      {
        ...req.body,
      },
      axiosRequestConfig
    )
    res.json(response.data)
  } catch (err) {
    logError('SUBMIT EXERCISE ERR -> ', err)
    res.status((err && err.response && err.response.status) || 500)
      .send((err && err.response && err.response.data) || {
        error: GENERAL_ERR_MSG,
      })
  }
})
