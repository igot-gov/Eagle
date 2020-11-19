import axios from 'axios'
import { Router } from 'express'
import { axiosRequestConfig } from '../../configs/request.config'
import { CONSTANTS } from '../../utils/env'
import { logError } from '../../utils/logger'
import { ERROR } from '../../utils/message'

const API_ENDPOINTS = {
  getRoles: `${CONSTANTS.SB_EXT_API_BASE_4}/v1/user`,
  getRolesDescription: `${CONSTANTS.ROLES_API_BASE}/v2/all-roles`,
  updateRoles: `${CONSTANTS.ROLES_API_BASE}/v1/update/roles`,
}

const GENERAL_ERROR_MSG = 'Failed due to unknown reason'
export const userRolesApi = Router()

userRolesApi.get('/getRolesDescription/:lang', async (req, res) => {
  try {
    const rootOrg = req.header('rootOrg')
    const langCode = req.params.lang
    if (!rootOrg) {
      res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
      return
    }
    const response = await axios({
      ...axiosRequestConfig,
      headers: {
        langCode,
        rootOrg,
      },
      method: 'GET',
      url: `${API_ENDPOINTS.getRolesDescription}`,
    })
    res.send(response.data)
  } catch (err) {
    logError('GET ROLES DESCRIPTION V2 ERR -> ', err)
    res.status((err && err.response && err.response.status) || 500)
      .send((err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      })
  }
})

userRolesApi.get('/allRoles', async (req, res) => {
  try {
    const uuid = 'masteruser'
    const rootOrg = req.header('rootOrg')
    const response = await axios({
      ...axiosRequestConfig,
      headers: {
        rootOrg,
      },
      method: 'GET',
      url: `${API_ENDPOINTS.getRoles}/roles?userid=${uuid}`,
    })
    res.json(response.data || {})
  } catch (err) {
    logError('ERROR ON GET USER ROLES >', err)
    res.status((err && err.response && err.response.status) || 500).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})

userRolesApi.get('/:id', async (req, res) => {
  try {
    const id = req.params.id
    const rootOrg = req.header('rootOrg')
    const response = await axios({
      ...axiosRequestConfig,
      headers: {
        rootOrg,
      },
      method: 'GET',
      url: `${API_ENDPOINTS.getRoles}/roles?userid=${id}`,
    })
    res.json(response.data || {})
  } catch (err) {
    logError('ERROR ON GET USER ROLES >', err)
    res.status((err && err.response && err.response.status) || 500).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})

userRolesApi.patch('/', async (req, res) => {
  try {
    const rootOrg = req.header('rootOrg')
    const response = await axios({
      ...axiosRequestConfig,
      data: req.body,
      headers: {
        rootOrg,
      },
      method: 'PATCH',
      url: `${API_ENDPOINTS.updateRoles}`,
    })
    res.json(response.data || {})
  } catch (err) {
    logError('ERROR ON UPDATE USER ROLES >', err)
    res.status((err && err.response && err.response.status) || 500).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})
