import axios from 'axios'
import { axiosRequestConfig } from './../../../configs/request.config'
import { CONSTANTS } from './../../../utils/env'

export async function readFromS3(url: string): Promise<{}> {
  url = url.split('/').slice(4).join('%2F')
  const result = await axios.get(
    `${CONSTANTS.CONTENT_API_BASE}/contentv3/download/${url}`,
    axiosRequestConfig
  )
  try {
    return JSON.parse(result.data)
  } catch {
    return result.data
  }
}
