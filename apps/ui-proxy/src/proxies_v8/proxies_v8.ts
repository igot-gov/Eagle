import express from 'express'
import { UploadedFile } from 'express-fileupload'
import FormData from 'form-data'
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
import { extractUserIdFromRequest, extractUserToken } from '../utils/requestExtract'

export const proxiesV8 = express.Router()

proxiesV8.get('/', (_req, res) => {
  res.json({
    type: 'PROXIES Route',
  })
})

proxiesV8.post('/upload/*', (req, res) => {
  if (req.files && req.files.data) {
    const url = removePrefix('/proxies/v8/upload', req.originalUrl)
    const file: UploadedFile = req.files.data as UploadedFile
    const formData = new FormData()
    formData.append('file', Buffer.from(file.data), {
      contentType: file.mimetype,
      filename: file.name,
    })
    formData.submit(
      {
        headers: {
          // tslint:disable-next-line:max-line-length
          Authorization: 'bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJRekw4VVA1dUtqUFdaZVpMd1ZtTFJvNHdqWTg2a2FrcSJ9.TPjV0xLacSbp3FbJ7XeqHoKFN35Rl4YHx3DZNN9pm0o',
          org: 'dopt',
          rootorg: 'igot',
          'x-authenticated-user-token': extractUserToken(req),
          'x-authenticated-userid': extractUserIdFromRequest(req),
        },
        host: 'knowledge-mw-service',
        path: url,
        port: 5000,
      },
      (err, response) => {

        response.on('data', (data) => {
          if (!err && (response.statusCode === 200 || response.statusCode === 201)) {
            res.send(JSON.parse(data.toString('utf8')))
          } else {
            res.send(data.toString('utf8'))
          }
        })
        if (err) {
          res.send(err)
        }

      }
    )
  } else {
    res.send('File not found')
  }
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

function removePrefix(prefix: string, s: string) {
  return s.substr(prefix.length)
}