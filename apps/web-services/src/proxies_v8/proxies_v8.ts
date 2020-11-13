import express from 'express'
import { CONSTANTS } from '../utils/env'
import {
  ilpProxyCreatorRoute,
  proxyCreatorRoute,
  scormProxyCreatorRoute,
} from '../utils/proxyCreator'

export const proxiesV8 = express.Router()

proxiesV8.get('/', (_req, res) => {
  res.json({
    type: 'PROXIES Route',
  })
})
proxiesV8.use(
  '/content',
  proxyCreatorRoute(express.Router(), CONSTANTS.CONTENT_API_BASE + '/content')
)
proxiesV8.use(
  '/contentv3',
  proxyCreatorRoute(express.Router(), CONSTANTS.CONTENT_API_BASE + '/contentv3')
)
proxiesV8.use(
  '/fastrack',
  proxyCreatorRoute(express.Router(), CONSTANTS.ILP_FP_PROXY + '/fastrack')
)
proxiesV8.use(
  '/hosted',
  proxyCreatorRoute(express.Router(), CONSTANTS.CONTENT_API_BASE + '/hosted')
)
proxiesV8.use('/ilp-api', ilpProxyCreatorRoute(express.Router(), CONSTANTS.ILP_FP_PROXY))
proxiesV8.use(
  '/scorm-player',
  scormProxyCreatorRoute(express.Router(), CONSTANTS.SCORM_PLAYER_BASE)
)
proxiesV8.use(
  '/LA',
  proxyCreatorRoute(express.Router(), CONSTANTS.APP_ANALYTICS, Number(CONSTANTS.ANALYTICS_TIMEOUT))
)
proxiesV8.use(
  '/FordGamification',
  proxyCreatorRoute(express.Router(), CONSTANTS.GAMIFICATION_API_BASE + '/FordGamification')
)
proxiesV8.use(
  '/static-ilp',
  proxyCreatorRoute(express.Router(), CONSTANTS.STATIC_ILP_PROXY + '/static-ilp')
)
proxiesV8.use(
  '/web-hosted',
  proxyCreatorRoute(express.Router(), CONSTANTS.WEB_HOST_PROXY + '/web-hosted')
)
