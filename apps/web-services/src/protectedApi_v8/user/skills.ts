import axios from 'axios'
import { Request, Response, Router } from 'express'
import { axiosRequestConfig } from '../../configs/request.config'
import { CONSTANTS } from '../../utils/env'

const apiEndPoints = {
  read: `${CONSTANTS.AUTHORING_BACKEND}/action/meta/v1/skills`,
}

export const skillsApi = Router()

skillsApi.post('/autocomplete', async (req: Request, res: Response) => {
  try {
    const rootOrg = req.header('rootOrg')
    const org = req.header('org')
    const langCode = req.header('locale')

    // tslint:disable-next-line: no-console
    console.log(
      // tslint:disable-next-line: max-line-length
      `AUTOCOMPLETE::${apiEndPoints.read}    :: rootOrg:${rootOrg}, org:${org}, langCode:${langCode}`
    )
    const response = await axios.post(`${apiEndPoints.read}`, req.body, {
      ...axiosRequestConfig,
      headers: { rootOrg, org, langCode },
    })
    res.send(response.data)
  } catch (err) {
    res.status((err && err.response && err.response.status) || 500).send(
      (err && err.response && err.response.data) || {
        error: 'Failed due to unknown reason',
      }
    )
  }
})
