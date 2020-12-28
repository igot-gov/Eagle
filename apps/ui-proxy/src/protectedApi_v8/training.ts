import axios from 'axios'
import { format as formatDate } from 'date-fns'
import { Request, Response, Router } from 'express'
import { IFeedbackTraining, IInfyJLStatus, IJITForm, IJITRequest, ITraining, ITrainingApiResponse, ITrainingCounts, ITrainingFeedbackQuestion, ITrainingRequest, ITrainingSession, ITrainingShareBody, ITrainingUserPrivileges } from '../models/training.model'
import { IUserAutocomplete } from '../models/user.model'
import { CONSTANTS } from '../utils/env'
import { getDateRangeString, getEmailLocalPart, getStringifiedQueryParams } from '../utils/helpers'
import { extractUserEmailFromRequest } from '../utils/requestExtract'
const apiEndpoints = {
  training: `${CONSTANTS.LEARNING_HUB_API_BASE}/lHub/v1`,
}

const GENERAL_ERROR_MSG = 'Failed due to unknown reason'

const TRAINING_ENDPOINT = '/:trainingId'

export const trainingApi = Router()

// Get trainings
trainingApi.get('/content/:contentId/trainings', async (req: Request, res: Response) => {
  try {
    const contentId = req.params.contentId
    const emailId = getEmailLocalPart(extractUserEmailFromRequest(req))
    const { toDate, fromDate, location } = req.query

    const queryParamObj = {
      email: emailId,
      end_dt: formatDate(new Date(toDate), 'dd MMM yyyy'),
      start_dt: formatDate(new Date(fromDate), 'dd MMM yyyy'),
    }

    const queryParams: string = getStringifiedQueryParams(queryParamObj)

    let url = `${apiEndpoints.training}/content/${contentId}/trainings?${queryParams}`

    if (location) {
      url += `&location=${location}`
    }

    const trainings: ITraining[] = await axios
      .get<ITraining[]>(url)
      .then((response) => response.data)

    return res.send(trainings)
  } catch (err) {
    return res.status((err && err.response && err.response.status) || 400).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})

// Get training sessions (currently defunct)
trainingApi.get('/trainingsId/sessions', async (req: Request, res: Response) => {
  try {
    const trainingId = req.params.trainingId

    const trainingSessions: ITrainingSession[] = await axios
      .get<ITrainingSession[]>(`${apiEndpoints.training}/offerings/${trainingId}/sessions`)
      .then((response) => response.data)

    return res.send(trainingSessions)
  } catch (err) {
    return res.status((err && err.response && err.response.status) || 400).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})

// Get training count for single content.
trainingApi.get('/content/:contentId/trainings/count', async (req: Request, res: Response) => {
  try {
    const { contentId } = req.params

    const countsObj: ITrainingCounts = await axios
      .post<ITrainingCounts>(`${apiEndpoints.training}/offerings/count`, {
        identifiers: [contentId],
      })
      .then((response) => response.data)

    return res.send(countsObj)
  } catch (err) {
    return res.status((err && err.response && err.response.status) || 400).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})

// Get no. of trainings for a content.
trainingApi.post('/count', async (req: Request, res: Response) => {
  try {
    const apiResult: ITrainingCounts = await axios
      .post<ITrainingCounts>(`${apiEndpoints.training}/offerings/count`, req.body)
      .then((response) => response.data)

    return res.send(apiResult)
  } catch (err) {
    return res.status((err && err.response && err.response.status) || 400).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})

// Register for a training
trainingApi.post(TRAINING_ENDPOINT, async (req: Request, res: Response) => {
  try {
    const trainingId = req.params.trainingId
    const emailId = getEmailLocalPart(extractUserEmailFromRequest(req))

    const apiResult: ITrainingApiResponse = await axios
      .post<ITrainingApiResponse>(
        `${apiEndpoints.training}/offerings/${trainingId}/users/${emailId}`
      )
      .then((response) => response.data)
      .then(() => {
        return { res_code: 1, res_message: 'Registered' }
      })

    return res.send(apiResult)
  } catch (err) {
    return res.status((err && err.response && err.response.status) || 400).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})

// Deregister from a training
trainingApi.delete(TRAINING_ENDPOINT, async (req: Request, res: Response) => {
  try {
    const trainingId = req.params.trainingId
    const emailId = getEmailLocalPart(extractUserEmailFromRequest(req))

    const apiResult: ITrainingApiResponse = await axios
      .delete<ITrainingApiResponse>(
        `${apiEndpoints.training}/offerings/${trainingId}/users/${emailId}`
      )
      .then((response) => response.data)

    return res.send(apiResult)
  } catch (err) {
    return res.status((err && err.response && err.response.status) || 400).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})

// Nominate users for training
trainingApi.post('/:trainingId/nominees', async (req: Request, res: Response) => {
  try {
    const trainingId = req.params.trainingId
    const emailId = getEmailLocalPart(extractUserEmailFromRequest(req))
    const nominees = req.body.nominees as IUserAutocomplete[]

    if (!nominees) {
      throw {
        res_code: -100,
        res_message: 'No nominees',
      }
    }

    const nomineeEmails = nominees.map((nominee) => getEmailLocalPart(nominee.email))

    const body = {
      manager: emailId,
      nominees: nomineeEmails,
    }

    const resp: ITrainingApiResponse[] = await axios
      .post<ITrainingApiResponse[]>(`${apiEndpoints.training}/offerings/${trainingId}/users`, body)
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

// Share a training
trainingApi.post('/:trainingId/share', async (req: Request, res: Response) => {
  try {
    const { trainingId } = req.params
    const users: IUserAutocomplete[] = req.body.users as IUserAutocomplete[]
    const emailId = getEmailLocalPart(extractUserEmailFromRequest(req))
    let shareEmails: string[] = []

    if (users) {
      shareEmails = users.map((user) => getEmailLocalPart(user.email))
    }

    const shareBody: ITrainingShareBody = {
      sharedBy: emailId,
      shared_with: shareEmails,
    }

    const apiResult: ITrainingApiResponse = await axios
      .post(`${apiEndpoints.training}/offerings/${trainingId}/share`, shareBody)
      .then((response) => response.data)

    return res.send(apiResult)
  } catch (err) {
    return res.status((err && err.response && err.response.status) || 400).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})

// Get watchlist
trainingApi.get('/watchlist', async (req: Request, res: Response) => {
  try {
    const emailId = getEmailLocalPart(extractUserEmailFromRequest(req))

    const watchlist: string[] = await axios
      .get<string[]>(`${apiEndpoints.training}/users/${emailId}/watchlist`)
      .then((response) => response.data)

    return res.send(watchlist)
  } catch (err) {
    return res.status((err && err.response && err.response.status) || 400).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})

// Check if a content is in watchlist
trainingApi.get('/watchlist/content/:contentId/status', async (req: Request, res: Response) => {
  try {
    const emailId = getEmailLocalPart(extractUserEmailFromRequest(req))
    const { contentId } = req.params

    const inWatchlist: boolean = await axios
      .get<string[]>(`${apiEndpoints.training}/users/${emailId}/watchlist`)
      .then((response) => response.data)
      .then((watchlist) => {
        const index = watchlist.findIndex((id) => id === contentId)
        if (index !== -1) {
          return true
        }
        return false
      })

    return res.send({ inWatchlist })
  } catch (err) {
    return res.status((err && err.response && err.response.status) || 400).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})

// Add content to watchlist
trainingApi.post('/watchlist/content/:contentId', async (req: Request, res: Response) => {
  try {
    const { contentId } = req.params
    const emailId = getEmailLocalPart(extractUserEmailFromRequest(req))

    const apiResult: ITrainingApiResponse = await axios
      .post(`${apiEndpoints.training}/content/${contentId}/users/${emailId}`)
      .then((response) => response.data)

    return res.send(apiResult)
  } catch (err) {
    return res.status((err && err.response && err.response.status) || 400).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})

// Remove content from watchlist
trainingApi.delete('/watchlist/content/:contentId', async (req: Request, res: Response) => {
  try {
    const { contentId } = req.params
    const emailId = getEmailLocalPart(extractUserEmailFromRequest(req))

    const apiResult: ITrainingApiResponse = await axios
      .delete<ITrainingApiResponse>(
        `${apiEndpoints.training}/content/${contentId}/users/${emailId}`
      )
      .then((response) => response.data)

    return res.send(apiResult)
  } catch (err) {
    return res.status((err && err.response && err.response.status) || 400).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})

// Get past JIT requests made by a user.
trainingApi.get('/trainings/jit', async (req: Request, res: Response) => {
  try {
    const userId = getEmailLocalPart(extractUserEmailFromRequest(req))

    const jitRequests: IJITRequest[] = await axios
      .get<IJITRequest[]>(`${apiEndpoints.training}/users/${userId}/jit`)
      .then((response) => response.data)

    return res.send(jitRequests)
  } catch (err) {
    return res.status((err && err.response && err.response.status) || 400).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})

// Make a JIT request
trainingApi.post('/trainings/jit', async (req: Request, res: Response) => {
  try {
    const jitForm: IJITForm = req.body
    const jitRequest: IJITRequest = {
      additional_info: jitForm.additionalInfo,
      content_id: jitForm.contentId,
      content_name: jitForm.contentName || jitForm.searchedContent,
      location_code: jitForm.location,
      no_of_participants: jitForm.participantCount,
      participant_profile: jitForm.participantProfile,
      raised_by: getEmailLocalPart(extractUserEmailFromRequest(req)),
      start_date: formatDate(new Date(jitForm.startDate), 'yyyy-MM-dd'),
      track_code: jitForm.track,
      training_by_vendor: jitForm.trainingByVendor,
      training_level: jitForm.trainingLevel,
    }

    const apiResult: ITrainingApiResponse = await axios
      .post<ITrainingApiResponse>(`${apiEndpoints.training}/jit`, jitRequest)
      .then((response) => response.data)

    return res.send(apiResult)
  } catch (err) {
    return res.status((err && err.response && err.response.status) || 500).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})

// Get trainings planned by a manager's team members
trainingApi.get('/trainingsForApproval', async (req: Request, res: Response) => {
  try {
    const userId = getEmailLocalPart(extractUserEmailFromRequest(req))

    const trainingRequests: ITrainingRequest[] = await axios
      .get<ITrainingRequest[]>(`${apiEndpoints.training}/manager/${userId}/offerings`)
      .then((response) => response.data)

    return res.send(trainingRequests)
  } catch (err) {
    return res.status((err && err.response && err.response.status) || 400).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})

// Reject a training request as a manager
trainingApi.patch(TRAINING_ENDPOINT, async (req: Request, res: Response) => {
  try {
    const { trainingId } = req.params
    const emailId = getEmailLocalPart(extractUserEmailFromRequest(req))

    const apiResult: ITrainingApiResponse = await axios
      .patch<ITrainingApiResponse>(
        `${apiEndpoints.training}/offerings/${trainingId}/users/${emailId}`,
        req.body
      )
      .then((response) => response.data)

    return res.send(apiResult)
  } catch (err) {
    return res.status((err && err.response && err.response.status) || 400).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})

// Get trainings pending feedback
trainingApi.get('/trainings/feedback', async (req: Request, res: Response) => {
  try {
    const emailId = getEmailLocalPart(extractUserEmailFromRequest(req))

    const trainingsForFeedback: IFeedbackTraining[] = await axios
      .get<IFeedbackTraining[]>(`${apiEndpoints.training}/users/${emailId}/offerings/feedback`)
      .then((response) => response.data)
      .then((trainings) => {
        trainings.forEach((training) => {
          training.date_range = getDateRangeString(training.start_dt, training.end_dt)
        })
        return trainings
      })

    return res.send(trainingsForFeedback)
  } catch (err) {
    return res.status((err && err.response && err.response.status) || 400).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})

// Get feedback form
trainingApi.get('/feedback/:formId', async (req: Request, res: Response) => {
  try {
    const { formId } = req.params

    const feedbackQuestions: ITrainingFeedbackQuestion[] = await axios
      .get<ITrainingFeedbackQuestion[]>(`${apiEndpoints.training}/feedback/${formId}`)
      .then((response) => response.data)

    return res.send(feedbackQuestions)
  } catch (err) {
    return res.status((err && err.response && err.response.status) || 400).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})

// Submit training feedback
trainingApi.post('/trainings/:trainingId/feedback', async (req: Request, res: Response) => {
  try {
    const { formId } = req.query
    if (!formId) {
      throw new Error('Provide a form template ID.')
    }
    const { trainingId } = req.params
    const emailId = getEmailLocalPart(extractUserEmailFromRequest(req))

    const queryParams: string = getStringifiedQueryParams({
      template: formId,
    })

    const apiResult: ITrainingApiResponse = await axios
      .post(
        `${apiEndpoints.training}/offerings/${trainingId}/users/${emailId}/feedback?${queryParams}`,
        req.body
      )
      .then((response) => response.data)

    return res.send(apiResult)
  } catch (err) {
    return res.status((err && err.response && err.response.status) || 400).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})

// Get eligibility for training extra features (user must be JL6+)
trainingApi.get('/userInfo', async (req: Request, res: Response) => {
  try {
    const emailId = getEmailLocalPart(extractUserEmailFromRequest(req))

    const isJL6OrAbove: ITrainingUserPrivileges = await axios
      .get<IInfyJLStatus>(`${apiEndpoints.training}/users/${emailId}`)
      .then((response) => ({
        canNominate: response.data.isJL6AndAbove || false,
        canRequestJIT: response.data.isJL6AndAbove || false,
      }))

    return res.send(isJL6OrAbove)
  } catch (err) {
    return res.status((err && err.response && err.response.status) || 400).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})
