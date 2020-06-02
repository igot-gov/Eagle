import { AxiosRequestConfig as oldConfig } from 'axios'

// tslint:disable-next-line: interface-name
export interface AxiosRequestConfig extends oldConfig {
  retry?: number
  __retryCount?: number
  retryDelay?: number
  timeout?: number
}
