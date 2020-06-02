import axios from 'axios'
import { IUploadPartialS3Response } from '../../models/response/custom-s3-upload'
import { axiosRequestConfig } from './../../../configs/request.config'
import { CONSTANTS } from './../../../utils/env'
const formData = require('form-data')

export async function uploadToS3(
  data: {},
  path: string,
  fileName: string
): Promise<IUploadPartialS3Response> {
  const returnValue: IUploadPartialS3Response = {
    artifactUrl: null,
    downloadUrl: null,
    error: null,
  }
  path = path.split('/').join('%2F')
  const form = new formData()
  const file = Buffer.from(JSON.stringify(data), 'utf-8')
  form.append('content', file, fileName)
  try {
    const result = await axios.post(
      `${CONSTANTS.CONTENT_API_BASE}/contentv3/upload/${path}`,
      form,
      {
        ...axiosRequestConfig,
        // You need to use `getHeaders()` in Node.js because Axios doesn't
        // automatically set the multipart form boundary in Node.
        headers: form.getHeaders(),
      }
    )
    returnValue.artifactUrl = result.data.artifactURL
    returnValue.downloadUrl = result.data.downloadURL
  } catch (err) {
    returnValue.error = (err && err.response && err.response.data) || 'Failed due to unkown reason'
  }
  return returnValue
}
