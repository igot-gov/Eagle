import express from 'express'
import { CONSTANTS } from '../utils/env'
import {
  ilpProxyCreatorRoute,
  proxyCreatorKnowledge,
  proxyCreatorLearner,
  proxyCreatorRoute,
  proxyCreatorSunbird,
  proxyCreatorSunbirdSearch,
  proxyCreatorToAppentUserId,
  scormProxyCreatorRoute
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

proxiesV8.use('/sunbirdigot/*',
  // tslint:disable-next-line: max-line-length
  proxyCreatorSunbirdSearch(express.Router(), `https://igot-sunbird.idc.tarento.com/api/composite/v1/search`)
)

proxiesV8.use('/action/*',
  proxyCreatorKnowledge(express.Router(), `http://knowledge-mw-service:5000`)
)

proxiesV8.use('/api/user/v2/read',
  proxyCreatorToAppentUserId(express.Router(), `https://igot-sunbird.idc.tarento.com/api/user/v2/read/`)
)

proxiesV8.use('/content-progres/*',
  // tslint:disable-next-line: max-line-length
  proxyCreatorSunbirdSearch(express.Router(), `https://igot-sunbird.idc.tarento.com/api/course/v1/content/state/update`)
)
proxiesV8.use('/read/content-progres/*',
  // tslint:disable-next-line: max-line-length
  proxyCreatorSunbirdSearch(express.Router(), `https://igot-sunbird.idc.tarento.com/api/course/v1/content/state/read`)
)

proxiesV8.use('/learner/*',
  // tslint:disable-next-line: max-line-length
  proxyCreatorLearner(express.Router(), `http://kong:8000`)
)

proxiesV8.use('/api/*',
  // tslint:disable-next-line: max-line-length
  proxyCreatorSunbird(express.Router(), `http://kong:8000`)
)
