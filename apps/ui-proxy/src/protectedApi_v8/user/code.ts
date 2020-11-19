import axios from 'axios'
import { Router } from 'express'
import { axiosRequestConfig } from '../../configs/request.config'
import { ISubmission } from '../../models/exercise.model'
import { processUrl } from '../../utils/contentHelpers'
import { CONSTANTS, RESTRICTED_PYTHON_STMT } from '../../utils/env'
import { logError } from '../../utils/logger'
import { ERROR } from '../../utils/message'
import { extractUserIdFromRequest } from '../../utils/requestExtract'

const API_ENDPOINTS = {
  execute: `${CONSTANTS.IAP_CODE_API_BASE}/backend/Code/Compile`,
  verify_submit_base: `${CONSTANTS.SUBMISSION_API_BASE}/v1/users`,
  view_last_submission_base: `${CONSTANTS.SUBMISSION_API_BASE}/v1/users`,
}

const API_ENDPOINT_TAILS = {
  ceSubmit: `multilanguage-submission?type=submit`,
  ceVerify: `multilanguage-submission?type=verify`,
  fpJavaSubmit: 'java-submission?type=submit',
  fpJavaVerify: `java-submission?type=verify`,
  fpSubmit: `python-submission?type=submit`,
  fpVerify: `python-submission?type=verify`,
  pfSubmit: `code-submissions`,
}

const GENERAL_ERROR_MSG = 'Failed due to unknown reason'

export type actionType = 'Verify' | 'Submit'
export type groupType = 'fp' | 'ce' | 'pf'
export type verifySubmitType =
  | 'fpVerify'
  | 'fpSubmit'
  | 'ceVerify'
  | 'ceSubmit'
  | 'pfSubmit'
  | 'fpJavaSubmit'
  | 'fpJavaVerify'

export async function execute(requestBody: Response['body']): Promise<{}> {
  try {
    const response = await axios.post(
      `${API_ENDPOINTS.execute}`,
      {
        ...requestBody,
        clientId: 'LEX',
        clientSecret: CONSTANTS.IAP_CLIENT_SECRET,
      },
      axiosRequestConfig
    )
    return response.data || {}
  } catch (e) {
    logError(e)
    return {}
  }
}

export async function verifySubmit(
  type: verifySubmitType,
  lexId: string,
  uuid: string,
  body: Response['body'],
  rootOrg: string
): Promise<{}> {
  try {
    const url = `${API_ENDPOINTS.verify_submit_base}/${uuid}/exercises/${lexId}/${API_ENDPOINT_TAILS[type]}`
    const requestBody = {
      ...body,
      clientId: 'LEX',
      clientSecret: CONSTANTS.IAP_CLIENT_SECRET,
    }
    const response = await axios({
      // ...axiosRequestConfig,
      data: requestBody,
      headers: {
        rootOrg,
      },
      method: 'POST',
      url,
    })
    return response.data || {}
  } catch (e) {
    logError(e)
    return {}
  }
}

export async function viewLastSubmission(
  lexId: string,
  uuid: string,
  rootOrg: string
): Promise<{}> {
  try {
    // tslint:disable-next-line: max-line-length
    const url = `${API_ENDPOINTS.view_last_submission_base}/${uuid}/exercises/${lexId}/submissions?user_id_type=uuid&type=latest`
    const response = await axios({
      ...axiosRequestConfig,
      headers: {
        rootOrg,
      },
      method: 'GET',
      url,
    })
    response.data.response.forEach((element: ISubmission) => {
      element.submission_url = processUrl(element.submission_url)
    })
    return response.data || {}
  } catch (e) {
    logError(e)
    return {}
  }
}

// tslint:disable-next-line: no-any
export function checkForBlockedStatement(req: any): null | any {
  if (req.body && req.body.language === 16) {
    let canReject = false
    let i = 0
    while (i < RESTRICTED_PYTHON_STMT.length && !canReject) {
      const regex = new RegExp(RESTRICTED_PYTHON_STMT[i], 'gm')
      if (regex.test(req.body.code)) {
        canReject = true
      }
      i = i + 1
    }
    if (canReject) {
      return {
        code: req.body.code,
        errors: 'Forbidden statements found in the code',
        langid: 16,
        output: '',
        time: 0,
      }
    }
  }
  return null
}

export const codeApi = Router()

codeApi.post('/execute', async (req, res) => {
  const isForbiddenStmtPresent = checkForBlockedStatement(req)
  if (isForbiddenStmtPresent) {
    res.status(200).send(isForbiddenStmtPresent)
    return
  }
  try {
    const response = await execute(req.body)
    res.json(response)
  } catch (err) {
    res.status((err && err.response && err.response.status) || 500).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})

codeApi.get('/viewLastSubmission/:contentId', async (req, res) => {
  try {
    const org = req.header('org')
    const rootOrg = req.header('rootOrg')
    if (!org || !rootOrg) {
      res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
      return
    }
    const lexId = req.params.contentId
    const uuid = extractUserIdFromRequest(req)
    const response = await viewLastSubmission(lexId, uuid, rootOrg)
    res.json(response)
  } catch (err) {
    res.status((err && err.response && err.response.status) || 500).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})

codeApi.post('/:group/:action/:contentId', async (req, res) => {
  const isForbiddenStmtPresent = checkForBlockedStatement(req)
  if (isForbiddenStmtPresent) {
    res.status(200).send(isForbiddenStmtPresent)
    return
  }
  try {
    const org = req.header('org')
    const rootOrg = req.header('rootOrg')
    if (!org || !rootOrg) {
      res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
      return
    }
    const group: string = req.params.group
    const action: string = req.params.action.charAt(0).toUpperCase() + req.params.action.slice(1)
    const groupAction: verifySubmitType = (group + action) as verifySubmitType
    const lexId = req.params.contentId
    const uuid = extractUserIdFromRequest(req)
    const response = await verifySubmit(groupAction, lexId, uuid, req.body, rootOrg)
    res.json(response)
  } catch (err) {
    res.status((err && err.response && err.response.status) || 500).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})
