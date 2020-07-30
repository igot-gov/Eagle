import axios from 'axios'
import { Router } from 'express'
import { axiosRequestConfig } from '../configs/request.config'
import { EContentTypes, IContent } from '../models/content.model'
import { IPaginatedApiResponse } from '../models/paginatedApi.model'
import { processContent, shuffleContent } from '../utils/contentHelpers'
import { CONSTANTS } from '../utils/env'
import { getStringifiedQueryParams } from '../utils/helpers'
import { logError } from '../utils/logger'
import { ERROR } from '../utils/message'
import { extractUserEmailFromRequest, extractUserIdFromRequest } from '../utils/requestExtract'

const API_END_POINTS = {
  interest: (userId: string) => `${CONSTANTS.RECOMMENDATION_API_BASE}/${userId}/recommendations/interest`,
  interestV2: `${CONSTANTS.SB_EXT_API_BASE_2}/v1/users`,
  recommendations: CONSTANTS.RECOMMENDATION_API_BASE,
  searchV6: `${CONSTANTS.SB_EXT_API_BASE}/v6/search`,
  usage: CONSTANTS.RECOMMENDATION_API_BASE + '/v1/recommendation',
  // interest: (userId: string) => `${CONSTANTS.SB_EXT_API_BASE}/${userId}/recommendations/interest`,
  // recommendations: CONSTANTS.SB_EXT_API_BASE,
  // usage: CONSTANTS.SB_EXT_API_BASE + '/v1/recommendation',
}

export const recommendationApi = Router()

recommendationApi.get('/', async (req, res) => {
  try {
    const org = req.header('org')
    const rootOrg = req.header('rootOrg')
    const langCode = req.header('locale')
    if (!org || !rootOrg) {
      res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
      return
    }
    const filters = req.query.filters
    let decodedFilters = {
      recommendationCategory: 'org',
    }
    if (filters) {
      decodedFilters = JSON.parse(decodeURIComponent(filters))
    }
    const recommendationCategory = decodedFilters.recommendationCategory
    // tslint:disable-next-line: max-line-length
    const url = `${API_END_POINTS.recommendations}/${extractUserIdFromRequest(
      req
    )}/recommendations?type=${recommendationCategory}`
    const response = await axios({
      ...axiosRequestConfig,
      headers: {
        langCode,
        org,
        rootOrg,
      },
      method: 'GET',
      url,
    })
    let contents: IContent[] = []
    if (
      Array.isArray(
        response.data.result &&
        response.data.result.response &&
        response.data.result.response.result
      )
    ) {
      contents = response.data.result.response.result.map((content: IContent) =>
        processContent(content)
      )
    }
    contents = shuffleContent(contents)
    const result: IPaginatedApiResponse = {
      contents,
      hasMore: false,
    }
    res.json(result)
  } catch (err) {
    logError('RECOMMENDATIONS FETCH ERROR >', err)
    res.status((err && err.response && err.response.status) || 500)
      .send((err && err.response && err.response.data) || {
        error: ERROR.GENERAL_ERR_MSG,
      })
  }
})

recommendationApi.get('/interestBased', async (req, res) => {
  try {
    const org = req.header('org')
    const rootOrg = req.header('rootOrg')
    const langCode = req.header('langCode') || 'en'
    if (!org || !rootOrg) {
      res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
      return
    }
    const pageNo = req.query.pageNo || 0
    const pageSize = req.query.pageSize || 20
    const queryParams = getStringifiedQueryParams({
      pageNumber: pageNo,
      pageSize,
    })
    const url = `${API_END_POINTS.interest(extractUserIdFromRequest(req))}?${queryParams}`
    const response = await axios.get(url, {
      ...axiosRequestConfig,
      headers: {
        langCode,
        locale: langCode,
        org,
        rootOrg,
      },
    })
    let contents: IContent[] = []
    if (
      Array.isArray(
        response.data.result &&
        response.data.result.response &&
        response.data.result.response.result
      )
    ) {
      contents = response.data.result.response.result.map((content: IContent) =>
        processContent(content)
      )
    }
    contents = shuffleContent(contents)

    const result: IPaginatedApiResponse = {
      contents,
      hasMore: false,
    }
    res.json(result)
  } catch (err) {
    logError('INTEREST BASED RECOMMENDATIONS FETCH ERROR >', err)
    res.status((err && err.response && err.response.status) || 500)
      .send((err && err.response && err.response.data) || {
        error: ERROR.GENERAL_ERR_MSG,
      })
  }
})

recommendationApi.get('/keyword', async (req, res) => {
  try {
    const userId = extractUserIdFromRequest(req)
    const rootOrg = req.header('rootOrg')
    const response = await axios.get(`${API_END_POINTS.interestV2}/${userId}/interests`, {
      ...axiosRequestConfig,
      headers: { rootOrg },
    })
    if (response.data && Array.isArray(response.data.user_interest)) {
      const keyword: [] = response.data.user_interest
      const searchBody = {
        filters: [
          {
            andFilters: [
              {
                keywords: keyword,
              },
            ],
          },
        ],
        pageNo: 0,
        pageSize: 10,
        query: keyword.join(' '),
        rootOrg,
        uuid: userId,

      }

      const responses = await axios.post(API_END_POINTS.searchV6, searchBody, axiosRequestConfig)
      let contents: IContent[] = []
      if (Array.isArray(responses.data.result)) {
        contents = responses.data.result.map((content: IContent) => processContent(content))
      }
      const result: IPaginatedApiResponse = {
        contents,
        hasMore: false,
      }
      res.json(result)
    } else {
      const result: IPaginatedApiResponse = {
        contents: [],
        hasMore: false,
      }
      res.json(result)
    }
  } catch (err) {
    logError('RECOMMENDATIONS TYPE FETCH ERROR >', err)
    res.status((err && err.response && err.response.status) || 500)
      .send((err && err.response && err.response.data) || {
        error: ERROR.GENERAL_ERR_MSG,
      })
  }
})

recommendationApi.get('/usageBased', async (req, res) => {
  try {
    const org = req.header('org')
    const rootOrg = req.header('rootOrg')
    const langCode = req.header('locale')
    if (!org || !rootOrg) {
      res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
      return
    }
    const pageNo = req.query.pageNo || 0
    const pageSize = req.query.pageSize || 20
    const queryParams = getStringifiedQueryParams({
      pageNumber: pageNo,
      resourceCount: pageSize,
      userId: extractUserIdFromRequest(req),
    })
    const url = `${API_END_POINTS.usage}/${extractUserEmailFromRequest(req)}/usage?${queryParams}`
    const response = await axios({
      ...axiosRequestConfig,
      headers: {
        langCode,
        org,
        rootOrg,
      },
      method: 'GET',
      url,
    })
    let contents: IContent[] = []
    if (Array.isArray(response.data.result && response.data.result.response)) {
      contents = response.data.result.response.map((content: IContent) => processContent(content))
    }
    contents = shuffleContent(contents)
    const result: IPaginatedApiResponse = {
      contents,
      hasMore: false,
    }
    res.json(result)
  } catch (err) {
    logError('USAGE BASED RECOMMENDATIONS FETCH ERROR >', err)
    res.status((err && err.response && err.response.status) || 500)
      .send((err && err.response && err.response.data) || {
        error: ERROR.GENERAL_ERR_MSG,
      })
  }
})

recommendationApi.get('/:recommendationType', async (req, res) => {
  try {
    const org = req.header('org')
    const rootOrg = req.header('rootOrg')
    const langCode = req.header('locale')
    if (!org || !rootOrg) {
      res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
      return
    }
    const filters = req.query.filters
    let decodedFilters = {
      recommendationCategory: 'org',
    }
    if (filters) {
      decodedFilters = JSON.parse(decodeURIComponent(filters))
    }
    const recommendationCategory = decodedFilters.recommendationCategory
    const pageNo = req.query.pageNo || 0
    const pageSize = req.query.pageSize || 20
    const sourceFields = req.query.sourceFields
    const recommendationType = req.params.recommendationType
    // tslint:disable-next-line: no-any
    const params: any = {
      pageNumber: pageNo,
      pageSize,
      sourceFields,
      type: recommendationCategory,
    }
    const url = `${API_END_POINTS.recommendations}/${extractUserIdFromRequest(req)}/recommendations/${recommendationType}`
    if (recommendationType === 'latest') {
      params.learningMode = 'Self-Paced'
      if (rootOrg === 'Infosys' && org === 'Infosys Ltd') {
        // tslint:disable-next-line: max-line-length
        params.excludeContentType = `${EContentTypes.KNOWLEDGE_ARTIFACT},${EContentTypes.RESOURCE},${EContentTypes.MODULE},${EContentTypes.CHANNEL}`
      } else {
        if (req.query.excludeContentType) {
          params.excludeContentType = req.query.excludeContentType
        }
      }
      if (rootOrg !== 'PNG' && org !== 'PNG') {
        params.url += `isExternal=false`
      }
    }
    const response = await axios.get(url, {
      ...axiosRequestConfig,
      headers: {
        langCode,
        org,
        rootOrg,
      },
      params,
    })
    let contents: IContent[] = []
    if (Array.isArray(response.data.result && response.data.result.response)) {
      contents = response.data.result.response.map((content: IContent) => processContent(content))
    }
    const result: IPaginatedApiResponse = {
      contents,
      hasMore: false,
    }
    res.json(result)
  } catch (err) {
    logError('RECOMMENDATIONS TYPE FETCH ERROR >', err)
    res.status((err && err.response && err.response.status) || 500)
      .send((err && err.response && err.response.data) || {
        error: ERROR.GENERAL_ERR_MSG,
      })
  }
})
