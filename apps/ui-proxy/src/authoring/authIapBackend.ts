import express from 'express'
import { createProxyServer } from 'http-proxy'
import { CONSTANTS } from '../utils/env'

export const authIapBackend = express.Router()
const proxyCreator = createProxyServer()

authIapBackend.all('*', (req, res) => {
  req.url = req.url.replace('/authIapApi', '')
  proxyCreator.web(req, res, {
    changeOrigin: true,
    target: CONSTANTS.IAP_BACKEND_AUTH,
  })
})
