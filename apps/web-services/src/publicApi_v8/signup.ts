import { Router } from 'express'
import {
    checkUniqueKey,
    createKeycloakUser,
    UpdateKeycloakUserPassword,
    updateUniqueKey,
} from '../utils/keycloak-user-creation'
import { logError } from '../utils/logger'

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
