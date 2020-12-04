import axios from 'axios'
import { Router } from 'express'

import { axiosRequestConfig } from '../configs/request.config'
import { CONSTANTS } from '../utils/env'
import { logError } from '../utils/logger'
import { ERROR } from '../utils/message'

const API_END_POINTS = {
    applicationTransition: `${CONSTANTS.WORKFLOW_HANDLER_SERVICE_API_BASE}/v1/workflow/transition`,
    applicationsSearch: `${CONSTANTS.WORKFLOW_HANDLER_SERVICE_API_BASE}/v1/workflow/applications/search`,
    historyBasedOnWfId: (workflowId: string, applicationId: string) =>
        `${CONSTANTS.WORKFLOW_HANDLER_SERVICE_API_BASE}/v1/workflow/${workflowId}/${applicationId}/history`,
    nextActionSearch: (serviceName: string, state: string) =>
        `${CONSTANTS.WORKFLOW_HANDLER_SERVICE_API_BASE}/v1/workflow/nextAction/${serviceName}/${state}`,

}

export const workflowHandlerApi = Router()
const unknownError = 'Failed due to unknown reason'
const failedToProcess = 'Failed to process the request. '

workflowHandlerApi.post('/transition', async (req, res) => {
    try {
        const rootOrgValue = req.headers.rootorg
        const orgValue = req.headers.org
        if (!rootOrgValue || !orgValue) {
            res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
            return
        }
        const response = await axios.post(
            API_END_POINTS.applicationTransition,
            req.body,
            {
                ...axiosRequestConfig,
                headers: {
                    org: orgValue,
                    rootOrg: rootOrgValue,
                },
            }
        )
        res.status(response.status).send(response.data)
    } catch (err) {
        logError(failedToProcess + err)
        res.status((err && err.response && err.response.status) || 500).send(
            (err && err.response && err.response.data) || {
                error: unknownError,
            }
        )
    }
})

workflowHandlerApi.post('/applicationsSearch', async (req, res) => {
    try {
        const rootOrgValue = req.headers.rootorg
        const orgValue = req.headers.org
        if (!rootOrgValue || !orgValue) {
            res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
            return
        }
        const response = await axios.post(
            API_END_POINTS.applicationsSearch,
            req.body,
            {
                ...axiosRequestConfig,
                headers: {
                    org: orgValue,
                    rootOrg: rootOrgValue,

                },
            }
        )
        res.status(response.status).send(response.data)
    } catch (err) {
        logError(failedToProcess + err)
        res.status((err && err.response && err.response.status) || 500).send(
            (err && err.response && err.response.data) || {
                error: unknownError,
            }
        )
    }
})

workflowHandlerApi.get('/nextActionSearch/:serviceName/:state', async (req, res) => {
    try {
        const serviceName = req.params.serviceName
        const state = req.params.state
        const rootOrgValue = req.headers.rootorg
        const orgValue = req.headers.org
        const response = await axios.get(API_END_POINTS.nextActionSearch(serviceName, state), {
            ...axiosRequestConfig,
            headers: {
                org: orgValue,
                rootOrg: rootOrgValue,

            },
        })
        res.status(response.status).send(response.data)
    } catch (err) {
        logError(failedToProcess + err)
        res.status((err && err.response && err.response.status) || 500).send(
            (err && err.response && err.response.data) || {
                error: unknownError,
            }
        )
    }
})

workflowHandlerApi.get('/applicationHistory/:wfId/:applicationId', async (req, res) => {
    try {
        const wfId = req.params.wfId
        const applicationId = req.params.applicationId
        const rootOrgValue = req.headers.rootorg
        const orgValue = req.headers.org
        const response = await axios.get(API_END_POINTS.historyBasedOnWfId(wfId, applicationId), {
            ...axiosRequestConfig,
            headers: {
                org: orgValue,
                rootOrg: rootOrgValue,
            },
        })
        res.status(response.status).send(response.data)
    } catch (err) {
        logError(failedToProcess + err)
        res.status((err && err.response && err.response.status) || 500).send(
            (err && err.response && err.response.data) || {
                error: unknownError,
            }
        )
    }
})
