import axios from 'axios'
import { Buffer } from 'buffer'
import { Router } from 'express'
import { UploadedFile } from 'express-fileupload'
import FormData from 'form-data'
import { axiosRequestConfig } from '../../configs/request.config'
import { ISubmission } from '../../models/exercise.model'
import { processUrl } from '../../utils/contentHelpers'
import { CONSTANTS } from '../../utils/env'
import { logError } from '../../utils/logger'
import { extractUserIdFromRequest } from '../../utils/requestExtract'

const GENERAL_ERR_MSG = 'Failed due to unknown reason'
const API_END_POINTS = {
  createContentDirectory: (contentId: string) =>
    `${CONSTANTS.CONTENT_API_BASE}/content/submissions/${contentId}`,
  postSubmission: (contentId: string, uuId: string) =>
    `${CONSTANTS.SUBMISSION_API_BASE}/v1/users/${uuId}/exercises/${contentId}/submissions`,
  submissionData: `${CONSTANTS.SB_EXT_API_BASE_3}/v1/users`,
  uploadFile: (contentId: string) =>
    `${CONSTANTS.CONTENT_API_BASE}/content/submissions/${contentId}/artifacts`,
}

export const exerciseApi = Router()
exerciseApi.get('/getSubmissions', async (req, res) => {
  try {
    const uuid = extractUserIdFromRequest(req)
    const { contentId, type } = req.query

    const response = await axios.get(
      `${API_END_POINTS.submissionData}/${uuid}/exercises/${contentId}/submissions?type=${type}`,
      axiosRequestConfig
    )

    response.data.response.forEach((element: ISubmission) => {
      element.submission_url = processUrl(element.submission_url)
    })
    res.json(response.data)
  } catch (err) {
    logError(err)
    res.status((err && err.response && err.response.status) || 500).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERR_MSG,
      }
    )
  }
})

exerciseApi.post('/postsubmission/:contentId', async (req, res) => {
  try {
    const contentId = req.params.contentId
    const uuId = extractUserIdFromRequest(req)
    const response = await axios.post(
      API_END_POINTS.postSubmission(contentId, uuId),
      req.body,
      axiosRequestConfig
    )
    res.send(response.data)
  } catch (err) {
    logError('ERROR CREATE CONTENT DIRECTORY ->', err)
    res
      .status((err && err.response && err.response.status) || 500)
      .send((err && err.response && err.response.data) || {
        error: GENERAL_ERR_MSG,
      })
  }
})

exerciseApi.post('/createContentDirectory/:contentId', async (req, res) => {
  try {
    const contentId = req.params.contentId
    const response = await axios.post(
      API_END_POINTS.createContentDirectory(contentId),
      req.body,
      axiosRequestConfig
    )
    res.status(response.status)
  } catch (err) {
    logError('ERROR CREATE CONTENT DIRECTORY ->', err)
    res
      .status((err && err.response && err.response.status) || 500)
      .send((err && err.response && err.response.data) || {
        error: GENERAL_ERR_MSG,
      })
  }
})

exerciseApi.post('/uploadFileToContentDirectory/:contentId', async (req, res) => {
  try {
    if (req.files && req.files.file) {
      const file: UploadedFile = req.files.file as UploadedFile
      const formData = new FormData()
      formData.append('content', Buffer.from(file.data), {
        contentType: file.mimetype,
        filename: file.name,
      })
      const contentId = req.params.contentId
      formData.submit(API_END_POINTS.uploadFile(contentId), (err, response) => {
        if (response.statusCode === 200 || response.statusCode === 201) {
          response.on('data', (data) => {
            res.send(JSON.parse(data.toString('utf8')))
          })
        } else {
          res.send(
            (err && err.message) || {
              error: GENERAL_ERR_MSG,
            }
          )
        }
      })
    } else {
      throw new Error('File not found')
    }
  } catch (err) {
    logError('ERROR UPLOAD FILE TO CONTENT DIRECTORY ->', err)
    res
      .status((err && err.response && err.response.status) || 500)
      .send((err && err.response && err.response.data) || {
        error: GENERAL_ERR_MSG,
      })
  }
})
