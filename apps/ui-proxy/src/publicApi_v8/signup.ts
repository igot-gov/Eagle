import { Router } from 'express'
import { createUser, performNewUserSteps } from '../protectedApi_v8/admin/userRegistration'
import {
    checkUniqueKey,
    checkUUIDMaster,
    createKeycloakUser,
    UpdateKeycloakUserPassword,
    updateUniqueKey,
    updateUUIDMaster,
} from '../utils/keycloak-user-creation'
import { logError, logInfo } from '../utils/logger'

export const signup = Router()

signup.post('/', async (req, res) => {
    try {
        let createKeycloak: void | { id: string }
        const signupReq = {
            email: req.body.email,
            firstName: req.body.fname || '',
            lastName: req.body.lname || '',
            uniqueId: req.body.code,
            username: req.body.email,
            // phone: req.body.phone,
        }
        // check into DB, uniqueId if found active, proceded and make the key inactive
        checkUniqueKey(signupReq.uniqueId, async (err, resp) => {
            if (err) {
                res.status(400).send(`1001: Wrong Code ${signupReq.uniqueId} !!` || {})
            }
            if (resp) {
                if (!resp.active) {
                    res.status(400).send(`1002: Code ${signupReq.uniqueId} is already is used !!` || {})
                }

                createKeycloak = await createKeycloakUser(req)
                    .catch((error) => {
                        if (error.response.status === 409) {
                            res.status(400).send(`1005: User with email ${signupReq.email} is already registered !!`)
                        }
                        res.status(400).send('1003: User could not be create in Keycloack !!' || {})
                    })
                if (createKeycloak && createKeycloak.id) {
                    const id = createKeycloak.id
                    updateUniqueKey(signupReq.uniqueId, async (error, response) => {
                        if (response) {
                            await UpdateKeycloakUserPassword(id, false)
                                .catch((_err) => {
                                    logError('ERROR ON UpdateKeycloakUserPassword', _err)
                                    res.status(400).send('1003: User default password could not be set !!' || {})
                                })
                            res.json(createKeycloak || {})
                        }
                        if (error) {
                            res.status(400).send(`1004: active satus of code ${signupReq.uniqueId} failed !!` || {})
                        }
                    })
                }
            }
        })
    } catch (err) {
        logError('ERROR ON signup USERS >', err)
        res.status((err && err.response && err.response.status) || 500)
            .send(err && err.response && err.response.data || {})
    }
})

signup.post('/create/:uniqueId', async (req, res) => {
    try {
        const result = await checkUUIDMaster(req.params.uniqueId)
            .catch((err) => {
                logInfo(`1001: Invalid Code ${req.params.uniqueId}`, err)
                res.json({ msg: `1001: Invalid Code ${req.params.uniqueId}` })
            })
        if (result) {
            const maskedEmail = getMaskedEmail(result.email)
            if (result.active) {
                logInfo('unique id found, creating new user in keycloak')
                const reqToNewUser = {
                    body: {
                        email: result.email,
                        fname: result.firstname,
                        lname: result.lastname,
                    },
                }
                const userId = await createUser(reqToNewUser)
                    .catch(async (err) => {
                        if (err.response.status === 409) {
                            res.json({ msg: `1004: User with email already exists` })
                        } else {
                            res.json({ msg: `User with email could not be created in Keycloak` })
                        }
                    })
                if (userId) {
                    logInfo('user created successfully. Now performing new user')
                    let msg = ''
                    await performNewUserSteps(userId, req, reqToNewUser.body.email)
                        .catch((err) => {
                            msg = `${err}`
                        })
                    await updateUUIDMaster(req.params.uniqueId, result.email)
                    if (msg) {
                        res.json({ msg: `1002: User with this email successfully registered. ${msg}` })
                    } else {
                        res.json({ msg: `1005: User with this email successfully registered`, email: maskedEmail })
                    }
                }
            } else {
                // if !resul.active
                res.json({ msg: `1003: Code is already used !!`, email: maskedEmail })
            }
        } else {
            res.status(400).send({ msg: `Could not process the request, please try again after some time!!` })
        }
    } catch (err) {
        logError('ERROR ON signup with unique ID >', err)
        res.status((err && err.response && err.response.status) || 500)
            .send(err && err.response && err.response.data || {})
    }
})

export function getMaskedEmail(email: string): string {
    if (email) {
        const [name, domain] = email.split('@')
        const [provider, domainName] = domain.split('.')
        return getMaskedString(name) + '@' + getMaskedString(provider) + '.' + getMaskedString(domainName)
    } else {
        return ''
    }
}

export function getMaskedString(str: string): string {
    if (!str) {
        return ''
    }
    return str[0] + str.slice(1).replace(/.(?!$)/g, '*')
}
