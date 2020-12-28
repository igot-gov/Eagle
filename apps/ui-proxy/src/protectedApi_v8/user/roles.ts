import axios from 'axios'
import { Router } from 'express'
import { axiosRequestConfig } from '../../configs/request.config'
import { CONSTANTS } from '../../utils/env'
import { logError, logObject } from '../../utils/logger'
import { ERROR } from '../../utils/message'
import { extractUserIdFromRequest } from '../../utils/requestExtract'

const GENERAL_ERR_MSG = 'Failed due to unknown reason'
const apiEndpoints = {
  role: `${CONSTANTS.ROLES_API_BASE}/v1/user/roles`,
  rolesV2: `${CONSTANTS.ROLES_API_BASE}/v2/roles`,
  updateRoles: `${CONSTANTS.ROLES_API_BASE}/v1/update/roles`,
}

export async function getUserRoles(userId: string, rootOrg: string) {
  try {
    const response = await axios.get(
      `${CONSTANTS.ROLES_API_BASE}/v2/users/${userId}/roles`,
      {
        ...axiosRequestConfig,
        headers: {
          rootOrg,
        },
      }
    )
    return response.data
  } catch (error) {
    return ['author']
  }
}

export const rolesApi = Router()

rolesApi.get('/', async (req, res) => {
  try {
    const rootOrg = req.header('rootOrg')
    if (!rootOrg) {
      res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
      return
    }
    const userId = extractUserIdFromRequest(req)
    const response = await getUserRoles(userId, rootOrg)
    res.json(response)
  } catch (err) {
    logError('ERROR FETCHING ROLES OF USER ->', err)
    res.status((err && err.response && err.response.status) || 500)
      .send((err && err.response && err.response.data) || {
        error: GENERAL_ERR_MSG,
      })
  }
})

rolesApi.get('/allRoles', async (req, res) => {
  try {
    const rootOrg = req.header('rootOrg')
    if (!rootOrg) {
      res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
      return
    }
    const userId = 'masteruser'
    const response = await getUserRoles(userId, rootOrg)
    res.json(response)
  } catch (err) {
    logError('ERROR FETCHING ALL ROLES ->', err)
    res.status((err && err.response && err.response.status) || 500)
      .send((err && err.response && err.data) || {
        error: GENERAL_ERR_MSG,
      })
  }
})

rolesApi.get('/:userId', async (req, res) => {
  try {
    const rootOrg = req.header('rootOrg')
    if (!rootOrg) {
      res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
      return
    }
    const userId = req.params.userId
    const response = await getUserRoles(userId, rootOrg)
    res.json(response)
  } catch (err) {
    logError('ERROR FETCHING ROLES OF SPECIFIC USER ->', err)
    res.status((err && err.response && err.response.status) || 500)
      .send((err && err.response && err.data) || {
        error: GENERAL_ERR_MSG,
      })
  }
})

rolesApi.patch('/', async (req, res) => {
  try {
    const rootOrg = req.header('rootOrg')
    if (!rootOrg) {
      res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
      return
    }
    const response = await axios({
      ...axiosRequestConfig,
      data: req.body,
      headers: {
        rootOrg,
      },
      method: 'PATCH',
      url: `${apiEndpoints.updateRoles}`,
    })
    res.json(response.data || {})
  } catch (err) {
    logError('ERROR ON UPDATE USER ROLES >', err)
    res.status((err && err.response && err.response.status) || 500)
      .send((err && err.response && err.response.data) || {
        error: GENERAL_ERR_MSG,
      })
  }
})

rolesApi.get('/getRolesV2/:userId', async (req, res) => {
  try {
    const uuid = req.params.userId
    const rootOrg = req.header('rootOrg')
    if (!rootOrg) {
      res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
      return
    }
    const response = await getUserRoles(uuid, rootOrg)
    res.send(response)
  } catch (err) {
    logError('GET ROLES V2 ERR -> ', err)
    res.status((err && err.response && err.response.status) || 500)
      .send((err && err.response && err.response.data) || {
        error: GENERAL_ERR_MSG,
      })
  }
})

rolesApi.get('/getUsersV2/:role', async (req, res) => {
  try {
    const role = req.params.role
    const rootOrg = req.header('rootOrg')
    if (!rootOrg) {
      res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
      return
    }
    const response = await axios.get(
      `${apiEndpoints.rolesV2}/${role}/users`,
      {
        ...axiosRequestConfig,
        headers: {
          rootOrg,
        },
      }
    )
    res.send(response.data)
  } catch (err) {
    logError('GET ROLES V2 ERR -> ', err)
    res.status((err && err.response && err.response.status) || 500)
      .send((err && err.response && err.response.data) || {
        error: GENERAL_ERR_MSG,
      })
  }
})

rolesApi.post('/updateRolesV2', async (req, res) => {
  try {
    const actionBy = req.header('wid')
    const body = {
      ...req.body,
      action_by: actionBy,
    }
    const rootOrg = req.header('rootOrg')
    if (!rootOrg) {
      res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
      return
    }
    const response = await axios.post(
      apiEndpoints.rolesV2,
      body,
      {
        ...axiosRequestConfig,
        headers: {
          rootOrg,
        },
        params: req.query,
      }
    )
    res.send(response.data)
  } catch (err) {
    logError('UPDATE ROLES V2 ERR -> ', err)
    res.status((err && err.response && err.response.status) || 500)
      .send((err && err.response && err.response.data) || {
        error: GENERAL_ERR_MSG,
      })
  }
})

// tslint:disable-next-line: no-any
export async function updateRolesV2Mock(actionByWid: any, updateRolesReq: any, rootOrg: string) {
  return new Promise(async (resolve, reject) => {
    const body = {
      ...updateRolesReq,
      action_by: actionByWid,
    }
    if (!rootOrg) {
      reject(ERROR.ERROR_NO_ORG_DATA)
    }
    logObject('Updating roles with', body)
    const response = await axios.post(
      apiEndpoints.rolesV2,
      body,
      {
        ...axiosRequestConfig,
        headers: {
          rootOrg,
        },
      }
    ).catch((err) => {
      logError('UPDATE ROLES V2 Mock ERR -> ', err)
      reject(err)
    })
    resolve(response)
  })
}
