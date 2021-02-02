import axios from 'axios'
import { Router } from 'express'
import { axiosRequestConfig, axiosRequestConfigLong } from '../../configs/request.config'
import { CONSTANTS } from '../../utils/env'
import { logError, logInfo } from '../../utils/logger'
import { extractUserIdFromRequest } from '../../utils/requestExtract'
const fs = require('fs')

const API_END_POINTS = {
  createUserRegistry: (userId: string) => `${CONSTANTS.NETWORK_HUB_SERVICE_BACKEND}/v1/user/create/profile?userId=${userId}`,
  getUserRegistry: `${CONSTANTS.NETWORK_HUB_SERVICE_BACKEND}/v1/user/get/profile`,
  getUserRegistryById: (userId: string) => `${CONSTANTS.NETWORK_HUB_SERVICE_BACKEND}/v1/user/search/profile?userId=${userId}`,
  searchUserRegistry: `${CONSTANTS.NETWORK_HUB_SERVICE_BACKEND}/v1/user/search/profile`,
  updateUserRegistry: (userId: string) => `${CONSTANTS.NETWORK_HUB_SERVICE_BACKEND}/v1/user/update/profile?userId=${userId}`,
  updateUserWorkflowRegistry: (userId: string) =>
    `${CONSTANTS.NETWORK_HUB_SERVICE_BACKEND}/v1/user/update/workflow/profile?userId=${userId}`,
}

const ERROR_MESSAGE_CREATE_REGISTRY = 'ERROR CREATING USER REGISTRY >'

export const profileRegistryApi = Router()

profileRegistryApi.post('/createUserRegistry', async (req, res) => {
  try {
    const userId = extractUserIdFromRequest(req)
    logInfo('Create user registry for', userId)
    const getUserIdExistresponse = await axios.get(API_END_POINTS.getUserRegistryById(userId), {
      ...axiosRequestConfig,
    })
    if (getUserIdExistresponse.data && getUserIdExistresponse.data.result &&
      getUserIdExistresponse.data.result.UserProfile
      && getUserIdExistresponse.data.result.UserProfile.length) {

      const response = await axios.post(API_END_POINTS.updateUserRegistry(userId), { ...req.body, userId }, {
        ...axiosRequestConfigLong,
      })
      res.status(response.status).json(response.data)
    } else {
      // const data = req.body;
      // const deptName = req.body.
      const response = await axios.post(API_END_POINTS.createUserRegistry(userId), { ...req.body, userId }, {
        ...axiosRequestConfigLong,
      })
      res.status(response.status).json(response.data)
    }
  } catch (err) {
    logError(ERROR_MESSAGE_CREATE_REGISTRY, err)
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
    logError(ERROR_MESSAGE_CREATE_REGISTRY, err)
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
    logInfo('Get user registry by id', userId)

    const response = await axios.get(API_END_POINTS.getUserRegistryById(userId), {
      ...axiosRequestConfig,
    })
    res.status(response.status).send(response.data)
  } catch (err) {
    logError('ERROR FETCHING USER REGISTRY by id >', err)
    res.status((err && err.response && err.response.status) || 500).send(err)
  }
})

profileRegistryApi.post('/searchUserRegistry', async (req, res) => {
  try {
    const userId = extractUserIdFromRequest(req)
    logInfo('Search user registry', userId)

    const response = await axios.post(API_END_POINTS.searchUserRegistry, { ...req.body }, {
      ...axiosRequestConfig,
    })
    res.status(response.status).json(response.data)
  } catch (err) {
    logError('ERROR FETCHING USER REGISTRY by id >', err)
    res.status((err && err.response && err.response.status) || 500).send(err)
  }
})

profileRegistryApi.get('/getUserRegistryByUser/:id', async (req, res) => {
  try {
    let userId = req.params.id
    if (!userId) {
      userId = extractUserIdFromRequest(req)
    }
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

profileRegistryApi.get('/getMasterNationalities', async (_req, res) => {
  try {
    // tslint:disable-next-line: no-identical-functions
    fs.readFile(__dirname + '/../../static-data/nationality.json', (err: Error, json: string) => {
      if (!err) {
        const obj = JSON.parse(json)
        res.json(obj)
      }
    })
  } catch (err) {
    res.status((err && err.response && err.response.status) || 500).send(err)
  }
})

profileRegistryApi.get('/getMasterLanguages', async (_req, res) => {
  try {
    fs.readFile(__dirname + '/../../static-data/languages.json', (err: Error, json: string) => {
      if (!err) {
        const obj = JSON.parse(json)
        res.json({
          languages: obj.languages.map((item: string) => {
            return { name: item }
          }),
        })
      }
    })
  } catch (err) {
    res.status((err && err.response && err.response.status) || 500).send(err)
  }
})

profileRegistryApi.get('/getProfilePageMeta', async (_req, res) => {
  try {
    const govtOrg = await govtOrgMeta()
      .catch((err) => {
        logError(`error fetching govtOrgMeta`, err)
      })
    const industries = await industreisMeta()
      .catch((err) => {
        logError(`error fetching industreisMeta`, err)
      })
    const degrees = await degreesMeta()
      .catch((err) => {
        logError(`error fetching degreesMeta`, err)
      })
    const designations = await designationMeta()
      .catch((err) => {
        logError(`error fetching designationMeta`, err)
      })
    res.json({
      degrees,
      designations,
      govtOrg,
      industries,
    })
  } catch (err) {
    res.status((err && err.response && err.response.status) || 500).send(err)
  }
})

export async function govtOrgMeta() {
  return new Promise(async (resolve, reject) => {
    try {
      await fs.readFile(__dirname + '/../../static-data/govtOrg.json', (err: Error, json: string) => {
        if (!err) {
          const obj = JSON.parse(json)
          const result = {
            cadre: obj.cadre.map((item: string) => {
              return { name: item }
            }),
            ministries: obj.ministries.map((item: string) => {
              return { name: item }
            }),
            service: obj.services.map((item: string) => {
              return { name: item }
            }),
          }
          resolve(result)
        } else {
          reject(err)

        }
      })
    } catch (err) {
      logError('ERROR on govtOrgMeta')
      throw err
    }
  })
}

export async function industreisMeta() {
  return new Promise(async (resolve, reject) => {
    try {
      await fs.readFile(__dirname + '/../../static-data/industries.json', (err: Error, json: string) => {
        if (!err) {
          const obj = JSON.parse(json)
          resolve(
            obj.industries.map((item: string) => {
              return { name: item }
            })
          )
        } else {
          reject(err)

        }
      })
    } catch (err) {
      logError('ERROR on industreisMeta')
      throw err
    }
  })
}

export async function degreesMeta() {
  return new Promise(async (resolve, reject) => {
    try {
      await fs.readFile(__dirname + '/../../static-data/degrees.json', (err: Error, json: string) => {
        if (!err) {
          const obj = JSON.parse(json)
          const result = {
            graduations: obj.graduations.map((item: string) => {
              return { name: item }
            }),
            postGraduations: obj.postGraduations.map((item: string) => {
              return { name: item }
            }),
          }
          resolve(result)
        } else {
          reject(err)

        }
      })
    } catch (err) {
      logError('ERROR on degreesMeta')
      throw err
    }
  })
}

export async function designationMeta() {
  return new Promise(async (resolve, reject) => {
    try {
      await fs.readFile(__dirname + '/../../static-data/designation.json', (err: Error, json: string) => {
        if (!err) {
          const obj = JSON.parse(json)
          const result = {
            designations: obj.designations.map((item: string) => {
              return { name: item }
            }),
            gradePay: obj.gradePay.map((item: string) => {
              return { name: item }
            }),
          }
          resolve(result)
        } else {
          reject(err)

        }
      })
    } catch (err) {
      logError('ERROR on designationMeta')
      throw err
    }
  })
}

profileRegistryApi.post('/createUserRegistryV2/:userId', async (req, res) => {
  try {
    const userId = req.params.userId
    logInfo('Create user registry for', userId)
    const getUserIdExistresponse = await axios.get(API_END_POINTS.getUserRegistryById(userId), {
      ...axiosRequestConfig,
    })
    if (getUserIdExistresponse.data && getUserIdExistresponse.data.result &&
      getUserIdExistresponse.data.result.UserProfile
      && getUserIdExistresponse.data.result.UserProfile.length) {

      const response = await axios.post(API_END_POINTS.updateUserRegistry(userId), { ...req.body, userId }, {
        ...axiosRequestConfigLong,
      })
      res.status(response.status).json(response.data)
    } else {
      // const data = req.body;
      // const deptName = req.body.
      const response = await axios.post(API_END_POINTS.createUserRegistry(userId), { ...req.body, userId }, {
        ...axiosRequestConfigLong,
      })
      res.status(response.status).json(response.data)
    }
  } catch (err) {
    logError(ERROR_MESSAGE_CREATE_REGISTRY, err)
    res.status((err && err.response && err.response.status) || 500).send(err)
  }
})
