import axios from 'axios'
import buffer from 'buffer'
import { Router } from 'express'
import { UploadedFile } from 'express-fileupload'
import { axiosRequestConfig } from '../configs/request.config'
import { ICertificationUserPrivileges } from '../models/certification.model'
import { IInfyJLStatus } from '../models/training.model'
import { CONSTANTS } from '../utils/env'
import { getEmailLocalPart } from '../utils/helpers'
import { extractUserEmailFromRequest } from '../utils/requestExtract'

const GENERAL_ERROR_MSG = 'Failed due to unknown reason'

const apiEndpoints = {
  certifications: `${CONSTANTS.LEARNING_HUB_API_BASE}/lHub`,
  trainings: `${CONSTANTS.LEARNING_HUB_API_BASE}/lHub/v1`,
}

export const certificationApi = Router()

// Get certification data
certificationApi.get('/:certificationId/bookingInfo', async (req, res) => {
  try {
    const emailId = getEmailLocalPart(extractUserEmailFromRequest(req))
    const { certificationId } = req.params

    const certification = await axios
      .get(
        `${apiEndpoints.certifications}/users/${emailId}/certifications/${certificationId}/booking-information`,
        { ...axiosRequestConfig }
      )
      .then((response) => response.data)

    return res.send(certification)
  } catch (err) {
    return res.status((err && err.response && err.response.status) || 400).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})

// Get ACC test centers
certificationApi.get('/:certificationId/testCenters', async (req, res) => {
  try {
    const { certificationId } = req.params

    const testCenters = await axios
      .get(`${apiEndpoints.certifications}/certifications/${certificationId}/test-centers`, {
        ...axiosRequestConfig,
      })
      .then((response) => response.data)

    return res.send(testCenters)
  } catch (err) {
    return res.status((err && err.response && err.response.status) || 400).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})

// Get ACC slots
certificationApi.get(
  '/:certificationId/locations/:location/testCenters/:testCenter/slots',
  async (req, res) => {
    try {
      const { certificationId, location, testCenter } = req.params
      const url =
        `${apiEndpoints.certifications}` +
        `/certifications/${certificationId}/locations/${location}/test-centers/${testCenter}/slots`

      const accSlots = await axios
        .get(url, { ...axiosRequestConfig })
        .then((response) => response.data)

      return res.send(accSlots)
    } catch (err) {
      return res.status((err && err.response && err.response.status) || 400).send(
        (err && err.response && err.response.data) || {
          error: GENERAL_ERROR_MSG,
        }
      )
    }
  }
)

// Book/Update ACC slot
certificationApi.post('/:certificationId/booking/:slotNo', async (req, res) => {
  try {
    const emailId = getEmailLocalPart(extractUserEmailFromRequest(req))
    const { certificationId, slotNo } = req.params
    const url = `${apiEndpoints.certifications}/users/${emailId}/certifications/${certificationId}/booking/${slotNo}`

    const accBookingResponse = await axios({
      ...axiosRequestConfig,
      method: 'POST',
      url,
    }).then((response) => response.data)

    return res.send(accBookingResponse)
  } catch (err) {
    return res.status((err && err.response && err.response.status) || 400).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})

// Get At-Desk countries
certificationApi.get('/countries', async (_req, res) => {
  try {
    const atDeskCountries = await axios
      .get(`${apiEndpoints.certifications}/countries`, {
        ...axiosRequestConfig,
      })
      .then((response) => response.data)

    return res.send(atDeskCountries)
  } catch (err) {
    return res.status((err && err.response && err.response.status) || 400).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})

// Get At-Desk locations
certificationApi.get('/countries/:countryCode/locations', async (req, res) => {
  try {
    const { countryCode } = req.params

    const atDeskLocations = await axios
      .get(`${apiEndpoints.certifications}/countries/${countryCode}/locations`, {
        ...axiosRequestConfig,
      })
      .then((response) => response.data)

    return res.send(atDeskLocations)
  } catch (err) {
    return res.status((err && err.response && err.response.status) || 400).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})

// Get At-Desk slots
certificationApi.get('/slots', async (_req, res) => {
  try {
    const atDeskSlots = await axios
      .get(`${apiEndpoints.certifications}/slots`, { ...axiosRequestConfig })
      .then((response) => response.data)

    return res.send(atDeskSlots)
  } catch (err) {
    return res.status((err && err.response && err.response.status) || 400).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})

// Book At-Desk slot
certificationApi.post('/:certificationId/atDeskBooking', async (req, res) => {
  try {
    const emailId = getEmailLocalPart(extractUserEmailFromRequest(req))
    const { certificationId } = req.params

    const atDeskBookingResponse = await axios
      .post(
        `${apiEndpoints.certifications}/users/${emailId}/certifications/${certificationId}/atdesk-booking`,
        req.body,
        { ...axiosRequestConfig }
      )
      .then((response) => response.data)

    return res.send(atDeskBookingResponse)
  } catch (err) {
    return res.status((err && err.response && err.response.status) || 400).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})

// Cancel an ACC or At-Desk booking
certificationApi.delete('/:certificationId/slots/:slotNo', async (req, res) => {
  try {
    const emailId = getEmailLocalPart(extractUserEmailFromRequest(req))
    const { certificationId, slotNo } = req.params
    const { icfdId } = req.query

    const slotDeleteResponse = await axios
      .delete(
        `${apiEndpoints.certifications}/users/${emailId}/certifications/${certificationId}/slots/${slotNo}`,
        {
          ...axiosRequestConfig,
          params: { icfd_id: icfdId },
        }
      )
      .then((response) => response.data)

    return res.send(slotDeleteResponse)
  } catch (err) {
    return res.status((err && err.response && err.response.status) || 400).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})

// Get currencies - Budget Approval
certificationApi.get('/currencies', async (_req, res) => {
  try {
    const currencies = await axios
      .get(`${apiEndpoints.certifications}/currencies`, {
        ...axiosRequestConfig,
      })
      .then((response) => response.data)

    return res.send(currencies)
  } catch (err) {
    return res.status((err && err.response && err.response.status) || 400).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})

// Submit Budget Approval request
certificationApi.post('/:certificationId/budgetRequest', async (req, res) => {
  try {
    const emailId = getEmailLocalPart(extractUserEmailFromRequest(req))
    const { certificationId } = req.params

    const budgetRequestSubmitResponse = await axios
      .post(
        `${apiEndpoints.certifications}/users/${emailId}/certifications/${certificationId}/budget-request`,
        req.body,
        { ...axiosRequestConfig }
      )
      .then((response) => response.data)

    return res.send(budgetRequestSubmitResponse)
  } catch (err) {
    return res.status((err && err.response && err.response.status) || 400).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})

// Cancel Budget Approval request
certificationApi.delete('/:certificationId/budgetRequest', async (req, res) => {
  try {
    const emailId = getEmailLocalPart(extractUserEmailFromRequest(req))
    const { certificationId } = req.params

    const budgetRequestCancelResponse = await axios
      .delete(
        `${apiEndpoints.certifications}/users/${emailId}/certifications/${certificationId}/budget-request`,
        { ...axiosRequestConfig }
      )
      .then((response) => response.data)

    return res.send(budgetRequestCancelResponse)
  } catch (err) {
    return res.status((err && err.response && err.response.status) || 400).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})

// Upload proof for External Certification
certificationApi.post('/:certificationId/result', async (req, res) => {
  try {
    const emailId = getEmailLocalPart(extractUserEmailFromRequest(req))
    const { certificationId } = req.params

    let fileBuffer: Buffer
    let fileBase64: string
    let fileName: string

    if (req.files) {
      const file: UploadedFile = req.files.file as UploadedFile
      fileBuffer = buffer.Buffer.from(file.data)
      fileBase64 = fileBuffer.toString('base64')
      fileName = file.name
    } else {
      throw new Error('File not found.')
    }

    const resultUploadResponse = await axios
      .post(
        `${apiEndpoints.certifications}/users/${emailId}/certifications/${certificationId}/result`,
        {
          exam_date: req.body.examDate,
          file: fileBase64,
          fileName,
          result: req.body.result,
          result_type: req.body.resultType,
          verifierEmail: req.body.verifierEmail,
        },
        { ...axiosRequestConfig }
      )
      .then((response) => response.data)

    return res.send(resultUploadResponse)
  } catch (err) {
    return res.status((err && err.response && err.response.status) || 400).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})

// Submit or withdraw result verification
certificationApi.patch('/:certificationId/result', async (req, res) => {
  try {
    const emailId = getEmailLocalPart(extractUserEmailFromRequest(req))
    const { certificationId } = req.params
    const { action } = req.query

    const resultSubmitResponse = await axios
      .patch(
        `${apiEndpoints.certifications}/users/${emailId}/certifications/${certificationId}/result`,
        req.body,
        { ...axiosRequestConfig, params: { action } }
      )
      .then((response) => response.data)

    return res.send(resultSubmitResponse)
  } catch (err) {
    return res.status((err && err.response && err.response.status) || 400).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})

// Get the document submitted for result verification
certificationApi.get('/submittedDocument', async (req, res) => {
  try {
    const { documentUrl } = req.query

    const document = await axios
      .get(`${apiEndpoints.certifications}/submitted-document`, {
        ...axiosRequestConfig,
        params: { document: documentUrl },
      })
      .then((response) => response.data)

    return res.send(document)
  } catch (err) {
    return res.status((err && err.response && err.response.status) || 400).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})

// Delete the document submitted for submission
certificationApi.delete('/:certificationId/document', async (req, res) => {
  try {
    const emailId = getEmailLocalPart(extractUserEmailFromRequest(req))
    const { certificationId } = req.params
    const { documentUrl } = req.query

    const docDeleteResponse = await axios
      .delete(
        `${apiEndpoints.certifications}/users/${emailId}/certifications/${certificationId}/document`,
        { ...axiosRequestConfig, params: { filename: documentUrl } }
      )
      .then((response) => response.data)

    return res.send(docDeleteResponse)
  } catch (err) {
    return res.status((err && err.response && err.response.status) || 400).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})

// Get items for approval
certificationApi.get('/certificationApprovals', async (req, res) => {
  try {
    const emailId = getEmailLocalPart(extractUserEmailFromRequest(req))
    const { type } = req.query

    const approvalItems = await axios
      .get(`${apiEndpoints.certifications}/users/${emailId}/certification-approvals`, {
        ...axiosRequestConfig,
        params: { type },
      })
      .then((response) => response.data)

    return res.send(approvalItems)
  } catch (err) {
    return res.status((err && err.response && err.response.status) || 400).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})

// Approve or reject an at-desk request
certificationApi.post('/atDeskRequests/:icfdId', async (req, res) => {
  try {
    const { icfdId } = req.params

    const resp = await axios
      .post(`${apiEndpoints.certifications}/certification-requests/${icfdId}`, req.body, {
        ...axiosRequestConfig,
      })
      .then((response) => response.data)

    return res.send(resp)
  } catch (err) {
    return res.status((err && err.response && err.response.status) || 400).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})

// Approve or reject a budget-approval request
certificationApi.post('/:certificationId/budgetRequestApproval', async (req, res) => {
  try {
    const emailId = getEmailLocalPart(extractUserEmailFromRequest(req))
    const { certificationId } = req.params
    const { ecdpId, sino } = req.query

    const resp = await axios
      .post(
        `${apiEndpoints.certifications}/users/${emailId}/certifications/${certificationId}/budget-request-approval`,
        req.body,
        { ...axiosRequestConfig, params: { sino, ecdp_id: ecdpId } }
      )
      .then((response) => response.data)

    return res.send(resp)
  } catch (err) {
    return res.status((err && err.response && err.response.status) || 400).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})

// Approve or reject a result-verification request
certificationApi.post('/:certificationId/resultVerificationRequests', async (req, res) => {
  try {
    const emailId = getEmailLocalPart(extractUserEmailFromRequest(req))
    const { certificationId } = req.params

    const url =
      `${apiEndpoints.certifications}` +
      `/users/${emailId}/certifications/${certificationId}/result-verification-requests`

    const resp = await axios
      .post(url, req.body, { ...axiosRequestConfig })
      .then((response) => response.data)

    return res.send(resp)
  } catch (err) {
    return res.status((err && err.response && err.response.status) || 400).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})

// Get completed/attempted certifications
certificationApi.get('/', async (req, res) => {
  try {
    const emailId = getEmailLocalPart(extractUserEmailFromRequest(req))
    const { status } = req.query

    const certifications = await axios
      .get(`${apiEndpoints.certifications}/users/${emailId}/certifications`, {
        ...axiosRequestConfig,
        params: { status },
      })
      .then((response) => response.data)

    return res.send(certifications)
  } catch (err) {
    return res.status((err && err.response && err.response.status) || 400).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})

// Get status of the requests that a user has raised
certificationApi.get('/certificationRequests', async (req, res) => {
  try {
    const emailId = getEmailLocalPart(extractUserEmailFromRequest(req))
    const { type, startDate, endDate } = req.query

    const certificationRequests = await axios
      .get(
        `${apiEndpoints.certifications}/users/${emailId}/certifications/certification-requests`,
        {
          ...axiosRequestConfig,
          params: { start_date: startDate, end_date: endDate, type },
        }
      )
      .then((response) => response.data)

    return res.send(certificationRequests)
  } catch (err) {
    return res.status((err && err.response && err.response.status) || 400).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})

// Get the details of all certification submissions by a user (all past attempts)
certificationApi.get('/:certificationId/submissions', async (req, res) => {
  try {
    const emailId = getEmailLocalPart(extractUserEmailFromRequest(req))
    const { certificationId } = req.params

    const submissions = await axios
      .get(
        `${apiEndpoints.certifications}/users/${emailId}/certifications/${certificationId}/submissions`,
        { ...axiosRequestConfig }
      )
      .then((response) => response.data)

    return res.send(submissions)
  } catch (err) {
    return res.status((err && err.response && err.response.status) || 400).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})

// Get certification privileges
certificationApi.get('/:emailId/privileges', async (req, res) => {
  try {
    const { emailId } = req.params

    const privileges: ICertificationUserPrivileges = await getCertificationUserPrivileges(emailId)

    return res.send(privileges)
  } catch (err) {
    return res.status((err && err.response && err.response.status) || 400).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})

// Get default at-desk proctor
certificationApi.get('/defaultProctor', async (req, res) => {
  try {
    const emailId = getEmailLocalPart(extractUserEmailFromRequest(req))

    const defaultProctor: ICertificationUserPrivileges = await getCertificationUserPrivileges(
      emailId
    )

    return res.send(defaultProctor)
  } catch (err) {
    return res.status((err && err.response && err.response.status) || 400).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})

// FUNCTIONS
const getCertificationUserPrivileges = async (emailId: string) => {
  return axios
    .get<IInfyJLStatus>(`${apiEndpoints.trainings}/users/${emailId}`)
    .then((response) => response.data)
    .then(
      (userData) =>
        ({
          canApproveBudgetRequest: userData.isJL7AndAbove || false,
          canProctorAtDesk: userData.isJL6AndAbove || false,
          canVerifyResult: userData.isJL7AndAbove || false,
          manager: userData.manager,
        } as ICertificationUserPrivileges)
    )
}
