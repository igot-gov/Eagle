import axios from 'axios'
import { Router } from 'express'

import { CONSTANTS } from '../../utils/env'

const API_END_POINTS = {
    mandatoryContentStatus: (userId: string) => `${CONSTANTS.SB_EXT_API_BASE_2}/v1/check/mandatoryContentStatus/${userId}`,
}

export const mandatoryContent = Router()

mandatoryContent.get('/checkStatus/:userId', async (req, res) => {
    try {
        const userId = req.params.userId
        const response = await axios.get(API_END_POINTS.mandatoryContentStatus(userId), req.body)
        res.status(response.status).send(response.data)
    } catch (err) {
        res.status((err && err.response && err.response.status) || 500).send(
            (err && err.response && err.response.data) || {
                error: 'Failed due to unknown reason',
            }
        )
    }
})
