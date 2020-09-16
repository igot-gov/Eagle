import axios from 'axios'
import { Router } from 'express'
import request from 'request'
import { axiosRequestConfig } from '../../configs/request.config'
import { CONSTANTS } from '../../utils/env'
import { logError, logInfo, logInfoHeading } from '../../utils/logger'
import { ERROR } from '../../utils/message'
import { extractUserIdFromRequest, extractUserToken } from '../../utils/requestExtract'
import { getUserByEmail } from '../discussionHub/users'
import { createDiscussionHubUser } from '../discussionHub/writeApi'
import { getUserProfile } from './profile'
import { getUserProfileStatus } from './profile-details'
import { getUserRoles } from './roles'
import { getTncStatus } from './tnc'

export const detailsApi = Router()

const API_END_POINTS = {
  detail: `${CONSTANTS.USER_PROFILE_API_BASE}/user/multi-fetch/wid`,
  emailId: `${CONSTANTS.USER_PROFILE_API_BASE}/user/multi-fetch/email`,
  managerDetails: `${CONSTANTS.USER_PROFILE_API_BASE}/user`,
  pidProfile: `${CONSTANTS.USER_PROFILE_API_BASE}/user/get-update`,
}

detailsApi.get('/', async (req, res) => {
  try {
    const userId = extractUserIdFromRequest(req)
    const rootOrg = req.header('rootOrg') || ''
    const org = req.header('org') || ''
    const locale = req.header('langCode')
    if (!org || !rootOrg) {
      res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
      return
    }
    const tncStatus = await getTncStatus(userId, rootOrg, org, locale)
    const roles = await getUserRoles(userId, rootOrg)
    const profileDetailsStatus = await getUserProfileStatus(userId)
    const returnRoles = [...roles.default_roles, ...roles.user_roles]
    res.json({
      group: [],
      profileDetailsStatus,
      roles: returnRoles,
      tncStatus,
    })
  } catch (err) {
    res.status((err && err.response && err.response.status) || 500).send(err)
    return
  }
})

detailsApi.get('/wtoken', async (req, res) => {
  try {
    const rootOrg = req.header('rootOrg') || ''
    const org = req.header('org') || ''
    if (!org || !rootOrg) {
      res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
      return
    }
    const kcToken = extractUserToken(req)
    const url = API_END_POINTS.pidProfile
    // tslint:disable-next-line: no-commented-code
    // const body = {
    //   json: {
    //     token: kcToken,
    //   },
    // }
    const options: request.CoreOptions = {
      headers: {
        org,
        rootOrg,
      },
      ...axiosRequestConfig,
      json: {
        token: kcToken,
      },
    }
    // tslint:disable-next-line: no-commented-code
    // const bodyWithConfigRequestOptions = { ...body, options }
    logInfoHeading('==========WToken API Request===============')
    // tslint:disable-next-line: no-commented-code
    // request.post(url, options).pipe(res)
    request.post(url, options, async (error, _res, body) => {
      if (error) {
        logError(`Error on wtoken api call to user profile service: `, error)
      }
      if (body.user) {
        const user = body.user
        // Check if user is present in NodeBB NodeBB DiscussionHub
        // tslint:disable-next-line: no-any
        const userPresent = await getUserByEmail(user.email).catch(async (err: any) => {
          if (err.response && (err.response.status === 404)) {
            // If user is not already present in nodeBB NodeBB DiscussionHub
            // then create the user
            const reqToDiscussionHub = {
              email: user.email,
              fullname: `${user.first_name} ${user.last_name}`,
              password: CONSTANTS.DISCUSSION_HUB_DEFAULT_PASSWORD,
              username: user.wid,
            }
            // tslint:disable-next-line: no-any
            await createDiscussionHubUser(reqToDiscussionHub).catch((createDiscussionHubUserErr: any) => {
              logError(`Creatin of User failed..!:`, createDiscussionHubUserErr)
              res.send(body)
            })
          }
        })
        if (userPresent) {
          logInfo('User already present in NodeBB DiscussionHub. Skiping create')
        }
      }
      res.send(body)
    })
  } catch (err) {
    // tslint:disable-next-line: no-console
    console.log('------------------W TOKEN ERROR---------\n', err)
    res.status((err && err.response && err.response.status) || 500).send(err)
  }
})

// tslint:disable-next-line: no-any // tslint:disable-next-line: cognitive-complexity
export function wTokenApiMock(req: any, token: any): Promise<any> {
  return new Promise((resolve, reject) => {
    try {
      const rootOrg = req.header('rootOrg') || CONSTANTS.DEFAULT_ROOT_ORG
      const org = req.header('org') || CONSTANTS.DEFAULT_ORG
      // tslint:disable-next-line: no-any
      let kcToken: any
      kcToken = token
      const url = API_END_POINTS.pidProfile
      const options: request.CoreOptions = {
        headers: {
          org,
          rootOrg,
        },
        ...axiosRequestConfig,
        json: {
          department_name: req.body.department,
          token: kcToken,
        },
      }

      request.post(url, options, async (error, _res, body) => {
        if (error) {
          reject(error)
        }
        if (body.user) {
          const user = body.user
          // Check if user is present in NodeBB DiscussionHub
          // tslint:disable-next-line: no-identical-functions
          const userPresent = await getUserByEmail(user.email).catch(async (err) => {
            if (err.response && (err.response.status === 404)) {
              // If user is not already present in nodeBB DiscussionHub
              // then create the user
              const reqToDiscussionHub = {
                email: user.email,
                fullname: `${user.first_name} ${user.last_name}`,
                password: CONSTANTS.DISCUSSION_HUB_DEFAULT_PASSWORD,
                username: user.wid,
              }
              // tslint:disable-next-line: no-any
              await createDiscussionHubUser(reqToDiscussionHub).catch((createDiscussionHubUserErr: any) => {
                logError(`Creatin of User failed..!:`, createDiscussionHubUserErr)
                resolve(body)
              })
            }
          })
          if (userPresent) {
            logInfo('User already present in NodeBB DiscussionHub. Skiping create')
          }
        }
        resolve(body)
      })

    } catch (err) {
      // tslint:disable-next-line: no-console
      console.log('------------------W TOKEN ERROR---------\n', err)
      reject()
    }
  })
}

detailsApi.get('/infosys', async (req, res) => {
  try {
    const userId = extractUserIdFromRequest(req)
    const userProfile = await getUserProfile(userId, req)
    res.json({
      userProfile,
    })
  } catch (err) {
    res.status((err && err.response && err.response.status) || 500).send(err)
  }
})

detailsApi.post('/managerDetails', async (req, res) => {
  try {
    const rootOrg = req.header('rootOrg')
    if (!rootOrg) {
      res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
      return
    }
    const response = await axios.post(
      API_END_POINTS.managerDetails,
      req.body,
      { ...axiosRequestConfig, headers: { rootOrg } }
    )
    res.status(response.status).send(response.data)
  } catch (err) {
    res
      .status((err && err.response && err.response.status) || 500)
      .send((err && err.response && err.response.data) || err)
  }
})

detailsApi.post('/detailV1', async (req, res) => {
  const _rootOrg = req.header('rootOrg')
  const url = `${API_END_POINTS.emailId}`
  try {
    if (!_rootOrg) {
      res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
      return
    }
    const response = await axios.post(
      url,
      {
        conditions: {
          root_org: _rootOrg,
        },
        source_fields: ['wid', 'email', 'first_name', 'last_name', 'department_name'],
        values: [req.body.email],
      },
      {
        ...axiosRequestConfig,
        headers: { _rootOrg },
      }
    )

    res.json(response.data)
  } catch (err) {
    res.status(500).send(err)
  }
})

detailsApi.get('/detailV2', async (req, res) => {
  const _rootOrg = req.header('rootOrg')
  const wid = extractUserIdFromRequest(req)
  const url = `http://10.177.22.26:9200/user/multi-fetch/wid`
  try {
    if (!_rootOrg) {
      res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
      return
    }
    const response = await axios.post(
      url,
      {
        conditions: {
          root_org: _rootOrg,
        },
        source_fields: ['wid', 'email', 'first_name', 'last_name'],
        values: [wid],
      },
      {
        ...axiosRequestConfig,
        headers: { _rootOrg },
      }
    )
    res.json(response.data[0])
  } catch (err) {
    res.status(500).send(err.response.data)
  }
})
