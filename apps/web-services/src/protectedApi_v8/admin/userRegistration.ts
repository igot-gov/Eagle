import axios from 'axios'
import cassandraDriver from 'cassandra-driver'
import { Router } from 'express'
import { cassandraClientOptions } from '../../configs/cassandra.config'
import { axiosRequestConfig } from '../../configs/request.config'
import { CONSTANTS } from '../../utils/env'
import {
    createKeycloakUser,
    getAuthToken,
    sendActionsEmail,
    UpdateKeycloakUserPassword,
} from '../../utils/keycloak-user-creation'
import { logError, logInfo } from '../../utils/logger'
import { extractUserIdFromRequest } from '../../utils/requestExtract'
import { wTokenApiMock } from '../user/details'

const REGISTRATION_BASE = `${CONSTANTS.SB_EXT_API_BASE_2}/v1/content-sources`
const API_ENDPOINTS = {
    deregisterUsers: (source: string) => `${REGISTRATION_BASE}/${source}/deregistered-users`,
    listUsers: (source: string) => `${REGISTRATION_BASE}/${source}/users`,
    registrationStatus: REGISTRATION_BASE,
    wtokenPath: `http://localhost:3003/protected/v8/user/details/wtokenprotected/v8/user/details/wtoken`,
}

export const userRegistrationApi = Router()

userRegistrationApi.get('/listUsers/:source', async (req, res) => {
    try {
        const rootOrg = req.header('rootOrg')
        const source = req.params.source
        const response = await axios.get(
            API_ENDPOINTS.listUsers(source),
            { headers: { rootOrg } }
        )
        res.json(response.data)
    } catch (err) {
        logError('ERROR ON GET ALL REGISTERED USERS >', err)
        res.status((err && err.response && err.response.status) || 500)
            .send(err && err.response && err.response.data || {})
    }
})

userRegistrationApi.post('/deregisterUsers/:source', async (req, res) => {
    try {
        const rootOrg = req.header('rootOrg')
        const source = req.params.source
        const response = await axios.post(
            API_ENDPOINTS.deregisterUsers(source),
            req.body,
            { headers: { rootOrg } }
        )
        res.json(response.data)
    } catch (err) {
        logError('ERROR ON DEREGISTER USERS >', err)
        res.status((err && err.response && err.response.status) || 500)
            .send(err && err.response && err.response.data || {})
    }
})

userRegistrationApi.get('/getAllSources', async (req, res) => {
    try {
        const rootOrg = req.header('rootOrg')
        const response = await axios.get(`${API_ENDPOINTS.registrationStatus}?registrationProvided=false`, {
            ...axiosRequestConfig,
            headers: { rootOrg },
        })
        const data = response.data.filter((o: { registrationUrl: string | null }) => o.registrationUrl !== null)
        res.json(data || {})
    } catch (err) {
        logError('ERROR ON GET ALL SOURCES >', err)
        res.status((err && err.response && err.response.status) || 500)
            .send(err && err.response && err.response.data || {})
    }
})

userRegistrationApi.get('/getSourceDetail/:id', async (req, res) => {
    try {
        const rootOrg = req.header('rootOrg')
        const source = req.params.id
        const response = await axios.get(`${API_ENDPOINTS.registrationStatus}/${source}`, {
            ...axiosRequestConfig,
            headers: { rootOrg },
        })
        res.json(response.data || {})
    } catch (err) {
        logError('ERROR ON GET SOURCE DETAILS >', err)
        res.status((err && err.response && err.response.status) || 500)
            .send(err && err.response && err.response.data || {})
    }
})

userRegistrationApi.get('/checkUserRegistrationContent/:source', async (req, res) => {
    try {
        const source = req.params.source
        const uuid = extractUserIdFromRequest(req)
        const rootOrg = req.header('rootOrg')
        const response = await axios.get(
            `${API_ENDPOINTS.registrationStatus}/${source}/users/${uuid}`, {
            ...axiosRequestConfig,
            headers: { rootOrg },
        })
        res.json(response.data || {})
    } catch (err) {
        logError('ERROR ON CHECK SOURCE REGISTRATION STATUS >', err)
        res.status((err && err.response && err.response.status) || 500)
            .send(err && err.response && err.response.data || {})
    }
})

userRegistrationApi.post('/register', async (req, res) => {
    try {
        const source = req.body.source
        const rootOrg = req.header('rootOrg')
        const response = await axios.post(
            `${API_ENDPOINTS.registrationStatus}/${source}/users`,
            req.body.items,
            {
                ...axiosRequestConfig,
                headers: {
                    rootOrg,
                },
            }
        )
        res.json(response.data || {})
    } catch (err) {
        logError('ERROR ON REGISTRATIO USERS >', err)
        res.status((err && err.response && err.response.status) || 500)
            .send(err && err.response && err.response.data || {})
    }
})

userRegistrationApi.post('/create-user', async (req, res) => {
    try {
        let createKeycloak: void | { id: string }
        createKeycloak = await createKeycloakUser(req)
            .catch((error) => {
                if (error.response.status === 409) {
                    res.status(400).send(`1005: User with email ${req.body.email} is already exists !!`)
                } else {
                    res.status(400).send('1003: User could not be created in Keycloack !!' || {})
                }
            })
        if (createKeycloak && createKeycloak.id) {
            await UpdateKeycloakUserPassword(createKeycloak.id, false)
                .catch((error) => {
                    logError('ERROR ON UpdateKeycloakUserPassword', error)
                    res.status(400).send('1003: User default password could not be set !!' || {})
                })
            getAuthToken(req.body.email).then(async (kcaAuthToken) => {
                if (kcaAuthToken && kcaAuthToken.access_token) {
                    const wTokenResponse = await wTokenApiMock(req, kcaAuthToken.access_token)
                    // tslint:disable-next-line: max-line-length
                    if (wTokenResponse && wTokenResponse.user && wTokenResponse.user.length) {
                        logInfo('New User keycloak auth successfull')
                        logInfo(`User: ${req.body.email} -- wid: ${wTokenResponse.user[0].wid}`)
                    }
                }
            }).catch((error) => {
                logError('ERROR ON getAuthToken', error)
                res.status(400).send('1004: User getAuthToken failed !!' || {})
            })
            await UpdateKeycloakUserPassword(createKeycloak.id, true)
                .catch((error) => {
                    logError('ERROR ON UpdateKeycloakUserPassword', error)
                    // res.status(400).send('1003: User default password could not be set !!' || {})
                })
            await sendActionsEmail(createKeycloak.id)
            .catch((error) => {
                logError('ERROR ON sendActionsEmail', error)
                // res.status(400).send('1003: Email could not be set !!' || {})
            })
            // console.log('kcaAuthToken', kcaAuthToken)
            res.json({ data: 'User Created successfully!' })
        }
    } catch (err) {
        logError('ERROR ON CREATE USERS >', err)
        res.status((err && err.response && err.response.status) || 500)
            .send(err && err.response && err.response.data || {})
    }
})

userRegistrationApi.post('/user/access-path', async (req, res) => {
    try {
        const clientConnect = new cassandraDriver.Client(cassandraClientOptions)
        // return new Promise((resolve, _reject) => {
        const query = `SELECT * FROM ${CONSTANTS.CASSANDRA_KEYSPACE}.user_access_paths
            WHERE user_id=${req.body.wid}`
        clientConnect.execute(query, (err, result) => {
            if (!err && result && result.rows) {
                const key = result.rows
                clientConnect.shutdown()
                res.json(key || {})
            } else if (err) {
                logError(`ERROR executing the query >> ${query}`)
                res.status(400).send('Something went wrong!')
            }
        })
        // })
    } catch (err) {
        logError('ERROR ON access-path >', err)
        res.status((err && err.response && err.response.status) || 500)
            .send(err && err.response && err.response.data || {})
    }
})

userRegistrationApi.post('/user/update-access-path', async (req, res) => {
    try {
        const clientConnect = new cassandraDriver.Client(cassandraClientOptions)
        const query = `INSERT INTO ${CONSTANTS.CASSANDRA_KEYSPACE}.user_access_paths
            (root_org, org, user_id, cas_id, access_paths, temporary , ttl) VALUES (?, ?, ?, ?, ?, ?, ?)`
        const params = [
            req.body.root_org,
            req.body.org,
            req.body.user_id,
            req.body.cas_id,
            req.body.access_paths,
            req.body.temporary,
            req.body.ttl,
        ]
        clientConnect.execute(query, params, (err, _result) => {
            if (!err) {
                clientConnect.shutdown()
                logInfo('Update Query to user_access_paths successful')
                res.json('User access paths updated successfully !!')
            } else if (err) {
                clientConnect.shutdown()
                logError(`ERROR executing the query >> ${query}`)
                res.status(400).send('Something went wrong!')
            }
        })
        // })
    } catch (err) {
        logError('ERROR ON access-path >', err)
        res.status((err && err.response && err.response.status) || 500)
            .send(err && err.response && err.response.data || {})
    }
})

userRegistrationApi.post('bulkUpload', async (req, res) => {
    try {
        logInfo('inside bbulkupload:', req.body)
    } catch (err) {
        logError('ERROR ON BULK UPLOAD >', err)
        res.status((err && err.response && err.response.status) || 500)
            .send(err && err.response && err.response.data || {})
    }
})
