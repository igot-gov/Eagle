import { AxiosRequestConfig } from '../models/axios-request-config.model'
import { CONSTANTS } from '../utils/env'

export const axiosRequestConfig: AxiosRequestConfig = {
  retry: 0,
  retryDelay: 1,
  timeout: Number(CONSTANTS.TIMEOUT) || 10000,
}
