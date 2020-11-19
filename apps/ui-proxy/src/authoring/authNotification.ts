import express from 'express'
import { createProxyServer } from 'http-proxy'
import { CONSTANTS } from '../utils/env'

export const authNotification = express.Router()
const proxyCreator = createProxyServer({ timeout: 10000 })

authNotification.all('*', (req, res) => {
  req.url = req.url.replace('/authNotificationApi', '')
  proxyCreator.web(req, res, {
    target: CONSTANTS.NOTIFICATIONS_API_BASE,
  })
})
