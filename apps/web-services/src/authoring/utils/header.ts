import { Request } from 'express'
import { extractUserIdFromRequest } from '../../utils/requestExtract'
import { axiosRequestConfig } from './../../configs/request.config'

export const getHeaders = (req: Request) => {
  return {
    ...axiosRequestConfig,
    org: getOrg(req),
    rootOrg: getRootOrg(req),
    wid: extractUserIdFromRequest(req),
  }
}

export const getOrg = (req: Request): string => {
  return req.header('org') || 'Infosys Ltd'
}

export const getRootOrg = (req: Request): string => {
  return req.header('rootOrg') || 'Infosys'
}
