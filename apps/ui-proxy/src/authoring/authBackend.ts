import express from 'express'
import { createProxyServer } from 'http-proxy'
import { CONSTANTS } from '../utils/env'

export const authBackend = express.Router()
const proxyCreator = createProxyServer()

authBackend.all('*', (req, res) => {
  req.url = req.url.replace('/authApi', '')
  proxyCreator.web(req, res, {
    target: CONSTANTS.AUTHORING_BACKEND,
  })
})
