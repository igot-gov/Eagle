import axios, { AxiosError } from 'axios'
import { AxiosRequestConfig } from '../models/axios-request-config.model'

axios.interceptors.response.use(undefined, (err: AxiosError) => {
  const config = err.config as AxiosRequestConfig

  // If status code is less than 500 reject
  if (err.response && err.response.status < 500) {
    return Promise.reject(err)
  }
  // If config does not exist or the retry option is not set, reject
  if (!config || !config.retry) return Promise.reject(err)

  // Set the variable for keeping track of the retry count
  config.__retryCount = config.__retryCount || 0

  // Check if we've maxed out the total number of retries
  if (config.__retryCount >= config.retry) {
    // Changing the error code so front end will not call
    err.code = '404'
    // Reject with the error
    return Promise.reject(err)
  }

  // Increase the retry count
  config.__retryCount += 1

  // Create new promise to handle exponential delay
  const delay = new Promise((resolve) => {
    setTimeout(() => {
      resolve()
    }, config.retryDelay || 0)
  })

  // Return the promise in which recalls axios to retry the request
  return delay.then(() => {
    return axios(config)
  })
})
