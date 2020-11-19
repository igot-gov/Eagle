import axios from 'axios'
import { Router } from 'express'
import { UploadedFile } from 'express-fileupload'
import FormData from 'form-data'
import { axiosRequestConfig } from '../../configs/request.config'
import { CONSTANTS } from '../../utils/env'
import { logError } from '../../utils/logger'
import { ERROR } from '../../utils/message'
import { extractUserIdFromRequest } from '../../utils/requestExtract'

export const bannerApi = Router()
const END_POINTS = {
    createBanner: `${CONSTANTS.SB_EXT_API_BASE_2}/v1/banners/`,
    getCurrentBanners: `${CONSTANTS.SB_EXT_API_BASE_2}/v1/current-banners/`,
    publishToS3: `${CONSTANTS.CONTENT_API_BASE}/contentv3/publish`,
    updateBanner: (bannerId: string) => `${CONSTANTS.SB_EXT_API_BASE_2}/v1/banners/${bannerId}`,
    updateCurrentBanners: `${CONSTANTS.SB_EXT_API_BASE_2}/v1/current-banners`,
    uploadToS3: `${CONSTANTS.CONTENT_API_BASE}/contentv3/upload`,
}

bannerApi.post('/publish', async (req, res) => {
    try {
        const rootOrg = req.header('rootOrg')
        if (!rootOrg) {
            res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
            return
        }
        const encodedLocation = encodeURIComponent(`${rootOrg}/app-assets/banners`)
        const response = await axios.post(
            `${END_POINTS.publishToS3}/${encodedLocation}`,
            {},
            axiosRequestConfig
        )
        res.send(response.data)
    } catch (err) {
        logError('ERR BANNER PUBLISH -> ', err)
        res.status((err && err.response && err.response.status) || 500)
            .send((err && err.response && err.response.data) || {
                error: ERROR.GENERAL_ERR_MSG,
            })
    }
})

bannerApi.post('/upload', async (req, res) => {
    try {
        if (req.files && req.files.content) {
            const rootOrg = req.header('rootOrg')
            if (!rootOrg) {
                res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
                return
            }
            const file: UploadedFile = req.files.content as UploadedFile
            const formData = new FormData()
            formData.append('content', Buffer.from(file.data), {
                // tslint:disable-next-line: max-line-length
                filename: `${rootOrg.toLowerCase()}_${extractUserIdFromRequest(req).substr(0, 5)}_${new Date().getTime()}.${file.name.split('.').reverse()[0]}`,
            })
            const encodedLocation = encodeURIComponent(`${rootOrg}/app-assets/banners/artifacts`)
            const response = await axios.post(
                `${END_POINTS.uploadToS3}/${encodedLocation}`,
                formData,
                {
                    ...axiosRequestConfig,
                    headers: formData.getHeaders(),
                }
            )
            res.send(response.data)
        } else {
            throw new Error('File not found')
        }
    } catch (err) {
        logError('ERR BANNER UPLOAD TO S3 -> ', err)
        res.status((err && err.response && err.response.status) || 500)
            .send((err && err.response && err.response.data) || {
                error: ERROR.GENERAL_ERR_MSG,
            })
    }
})

bannerApi.get('/currentBanners', async (req, res) => {
    try {
        const rootOrg = req.header('rootOrg')
        const org = req.header('org')
        const langCode = req.query.langCode
        if (!rootOrg || !org) {
            res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
            return
        }
        const response = await axios.get(
            END_POINTS.getCurrentBanners,
            {
                ...axiosRequestConfig,
                headers: {
                    langCode,
                    org,
                    rootOrg,
                },
            }
        )
        res.send(response.data)
    } catch (err) {
        logError('ERR FETCHING CURRENT BANNERS -> ', err)
        res.status((err && err.response && err.response.status) || 500)
            .send((err && err.response && err.response.data) || {
                error: ERROR.GENERAL_ERR_MSG,
            })
    }
})

bannerApi.post('/createBanner', async (req, res) => {
    try {
        const rootOrg = req.header('rootOrg')
        const org = req.header('org')
        const langCode = req.query.langCode
        const updatedBy = extractUserIdFromRequest(req)
        if (!rootOrg || !org) {
            res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
            return
        }
        const response = await axios.post(
            END_POINTS.createBanner,
            {
                ...req.body,
                updatedBy,
            },
            {
                ...axiosRequestConfig,
                headers: {
                    langCode,
                    org,
                    rootOrg,
                },
            }
        )
        res.send(response.data)
    } catch (err) {
        logError('ERR CREATING NEW BANNER -> ', err)
        res.status((err && err.response && err.response.status) || 500)
            .send((err && err.response && err.response.data) || {
                error: ERROR.GENERAL_ERR_MSG,
            })
    }
})

bannerApi.post('/updateCurrentBanner', async (req, res) => {
    try {
        const rootOrg = req.header('rootOrg')
        const org = req.header('org')
        const langCode = req.query.langCode
        const updatedBy = extractUserIdFromRequest(req)
        if (!rootOrg || !org) {
            res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
            return
        }
        const response = await axios.post(
            END_POINTS.updateCurrentBanners,
            {
                ...req.body,
                updatedBy,
            },
            {
                ...axiosRequestConfig,
                headers: {
                    langCode,
                    org,
                    rootOrg,
                },
            }
        )
        res.send(response.data)
    } catch (err) {
        logError('ERR UPDATING CURRENT BANNERS -> ', err)
        res.status((err && err.response && err.response.status) || 500)
            .send((err && err.response && err.response.data) || {
                error: ERROR.GENERAL_ERR_MSG,
            })
    }
})

bannerApi.post('/updateBanner/:bannerId', async (req, res) => {
    try {
        const rootOrg = req.header('rootOrg')
        const org = req.header('org')
        const langCode = req.query.langCode
        const updatedBy = extractUserIdFromRequest(req)
        const bannerId = req.params.bannerId
        if (!rootOrg || !org) {
            res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
            return
        }
        const response = await axios.put(
            END_POINTS.updateBanner(bannerId),
            {
                ...req.body,
                updatedBy,
            },
            {
                ...axiosRequestConfig,
                headers: {
                    langCode,
                    org,
                    rootOrg,
                },
            }
        )
        res.send(response.data)
    } catch (err) {
        logError('ERR UPDATING BANNER -> ', err)
        res.status((err && err.response && err.response.status) || 500)
            .send((err && err.response && err.response.data) || {
                error: ERROR.GENERAL_ERR_MSG,
            })
    }
})
