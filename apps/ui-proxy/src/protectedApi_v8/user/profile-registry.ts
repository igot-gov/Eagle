import axios from 'axios'
import { Router } from 'express'
import { axiosRequestConfig, axiosRequestConfigLong } from '../../configs/request.config'
import { CONSTANTS } from '../../utils/env'
import { logError, logInfo } from '../../utils/logger'
import { extractUserIdFromRequest } from '../../utils/requestExtract'

const API_END_POINTS = {
  createUserRegistry: (userId: string) => `${CONSTANTS.NETWORK_HUB_SERVICE_BACKEND}/v1/user/create/profile?userId=${userId}`,
  getUserRegistry: `${CONSTANTS.NETWORK_HUB_SERVICE_BACKEND}/v1/user/get/profile`,
  getUserRegistryById: (userId: string) => `${CONSTANTS.NETWORK_HUB_SERVICE_BACKEND}/v1/user/search/profile?userId=${userId}`,
  updateUserRegistry: (userId: string) => `${CONSTANTS.NETWORK_HUB_SERVICE_BACKEND}/v1/user/update/profile?userId=${userId}`,
  updateUserWorkflowRegistry: (userId: string) => `${CONSTANTS.NETWORK_HUB_SERVICE_BACKEND}/v1/user/update/workflow/profile?userId=${userId}`,
}

export const profileRegistryApi = Router()

profileRegistryApi.post('/createUserRegistry', async (req, res) => {
  try {
    const userId = extractUserIdFromRequest(req)
    logInfo('Create user registry for', userId)
    const getUserIdExistresponse = await axios.get(API_END_POINTS.getUserRegistryById(userId), {
      ...axiosRequestConfig,
    })
    if (getUserIdExistresponse.data) {
      const response = await axios.post(API_END_POINTS.updateUserRegistry(userId), { ...req.body, userId }, {
        ...axiosRequestConfigLong,
      })
      res.status(response.status).json(response.data)
    } else {
      const response = await axios.post(API_END_POINTS.createUserRegistry(userId), { ...req.body, userId }, {
        ...axiosRequestConfigLong,
      })
      res.status(response.status).json(response.data)
    }
  } catch (err) {
    logError('ERROR CREATING USER REGISTRY >', err)
    res.status((err && err.response && err.response.status) || 500).send(err)
  }
})

profileRegistryApi.post('/updateUserRegistry', async (req, res) => {
  try {
    const userId = extractUserIdFromRequest(req)
    logInfo('Update user registry for', userId)
    const response = await axios.post(API_END_POINTS.updateUserRegistry(userId), { ...req.body, userId }, {
      ...axiosRequestConfigLong,
    })
    res.status(response.status).json(response.data)
  } catch (err) {
    logError('ERROR CREATING USER REGISTRY >', err)
    res.status((err && err.response && err.response.status) || 500).send(err)
  }
})

profileRegistryApi.post('/updateUserWorkflowRegistry', async (req, res) => {
  try {
    const userId = extractUserIdFromRequest(req)
    logInfo('Update user workflow registry for', userId)
    const response = await axios.post(API_END_POINTS.updateUserWorkflowRegistry(userId), { ...req.body, userId }, {
      ...axiosRequestConfigLong,
    })
    res.status(response.status).json(response.data)
  } catch (err) {
    logError('ERROR UPDATING USER REGISTRY WORKFLOW>', err)
    res.status((err && err.response && err.response.status) || 500).send(err)
  }
})

// tslint:disable-next-line: no-identical-functions
profileRegistryApi.get('/getUserRegistry/:osid', async (req, res) => {
  try {
    const osid = req.params.osid
    logInfo('Get user registry for', osid)
    const response = await axios.post(API_END_POINTS.getUserRegistry, { osid }, {
      ...axiosRequestConfig,
    })
    res.status(response.status).send(response.data)
  } catch (err) {
    logError('ERROR FETCHING USER REGISTRY >', err)
    res.status((err && err.response && err.response.status) || 500).send(err)
  }
})

// tslint:disable-next-line: no-identical-functions
profileRegistryApi.get('/getUserRegistryById', async (req, res) => {
  try {
    const userId = extractUserIdFromRequest(req)
    logInfo('Get user registry for', userId)

    const response = await axios.get(API_END_POINTS.getUserRegistryById(userId), {
      ...axiosRequestConfig,
    })
    res.status(response.status).send(response.data)
  } catch (err) {
    logError('ERROR FETCHING USER REGISTRY >', err)
    res.status((err && err.response && err.response.status) || 500).send(err)
  }
})
