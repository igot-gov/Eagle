import axios from 'axios'
import { Router } from 'express'
import { axiosRequestConfig } from '../../configs/request.config'
import { CONSTANTS } from '../../utils/env'

import { ERROR } from '../../utils/message'

const API_END_POINTS = {
    usersByCommonDept: (rootOrg: string, searchItem: string) =>
        `${CONSTANTS.USER_PROFILE_API_BASE}/user/commonDept/autocomplete/${rootOrg}/all/${searchItem}`,
}

export const userSearchApi = Router()

userSearchApi.get('/commonDept/:query', async (req, res) => {
    const org = req.header('org')
    const rootOrg = req.header('rootOrg')
    try {
        if (!org || !rootOrg) {
            res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
            return
        }
        const url = API_END_POINTS.usersByCommonDept(rootOrg, req.params.query)
        const response = await axios({
            ...axiosRequestConfig,
            headers: { rootOrg },
            method: 'GET',
            url,
        })
        res.send(response.data)
    } catch (err) {
        res.status((err && err.response && err.response.status) || 500)
            .send((err && err.reponse && err.response.data) || {
                error: 'Failed due to unknown reason',
            })
    }
})
