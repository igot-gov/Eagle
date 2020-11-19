import axios from 'axios'
import { Request, Response, Router } from 'express'
import { axiosRequestConfig } from '../configs/request.config'
import { IKhubItemSearch, IKHubResultHome } from '../models/khub.model'
import { CONSTANTS } from '../utils/env'
import { getStringifiedQueryParams } from '../utils/helpers'
import { ERROR } from '../utils/message'

const apiEndpoints = {
  morelikeApi: `${CONSTANTS.KHUB_SEARCH_BASE}/api/v1/moreLikeThis`,
  relatedResources: (contentId: string, contentType: string) =>
    `${CONSTANTS.KHUB_SEARCH_BASE}/api/v1/moreLikeThis/${contentId}?contentType=${contentType}`,
  searchApi: `${CONSTANTS.KHUB_SEARCH_BASE}/api/v1/search`,
  topicsApi: `${CONSTANTS.KHUB_SEARCH_BASE}/api/v1/topic`,
}
const GENERAL_ERROR_MSG = 'Failed due to unknown reason'
export const knowledgeHubApi = Router()

knowledgeHubApi.get('/fetchRelatedResources/:contentId/:contentType', async (req, res) => {
  const { contentId, contentType } = req.params
  try {
    const rootOrg = req.header('rootOrg')
    const org = req.header('org')
    if (!rootOrg || !org) {
      res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
      return
    }

    const response = await axios
      .get(apiEndpoints.relatedResources(contentId, contentType), {
        ...axiosRequestConfig,
        headers: { rootOrg, org },
      })
      .then((resp) => resp.data.result.response)
    return res.status(response.status).send(response)
  } catch (err) {
    return res.status((err && err.response && err.response.status) || 500).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})

// Get Search Home
knowledgeHubApi.get('/home/', async (req: Request, res: Response) => {
  try {
    const queryParams = getStringifiedQueryParams({
      from: 0,
      size: req.query.size,
    })
    let url: string
    url = `${apiEndpoints.searchApi}?${queryParams}`
    const searchData: IKHubResultHome = await axios
      .get<IKHubResultHome>(url, axiosRequestConfig)
      .then((response) => response.data)
    return res.send(searchData)
  } catch (err) {
    return res.status((err && err.response && err.response.status) || 500).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})
// GET search Results for query
knowledgeHubApi.get('/search/:query/:from/:size/:category', async (req: Request, res: Response) => {
  try {
    const query = req.params.query
    const filter = req.query.filter
    const from = req.params.from
    const size = req.params.size
    const category = req.params.category
    const queryParams = getStringifiedQueryParams({
      category,
      filter,
      from,
      searchQuery: query,
      size,
    })
    let url: string
    url = `${apiEndpoints.searchApi}?${queryParams}`
    const searchResultData: IKhubItemSearch = await axios
      .get<IKhubItemSearch>(url, axiosRequestConfig)
      .then((response) => {
        return response.data
      })
    return res.send(searchResultData)
  } catch (err) {
    return res.status((err && err.response && err.response.status) || 500).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})
// GET search single Item
knowledgeHubApi.get('/item/:id', async (req: Request, res: Response) => {
  try {
    const queryParams = getStringifiedQueryParams({
      filter: `"itemId":["${req.params.id}"]`,
    })
    let url: string
    url = `${apiEndpoints.searchApi}?${queryParams}`
    const searchResultData: {} = await axios
      .get<{}>(url, axiosRequestConfig)
      .then((response) => response.data)
    return res.send(searchResultData)
  } catch (err) {
    return res.status((err && err.response && err.response.status) || 500).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})

// GET search Morelike This
knowledgeHubApi.get('/moreLike/:category/:itemId/:source', async (req: Request, res: Response) => {
  try {
    const category = req.params.category
    const itemId = req.params.itemId
    const source = req.params.source
    const queryParams = getStringifiedQueryParams({
      category,
      itemId,
      source,
    })
    let url: string
    url = `${apiEndpoints.morelikeApi}?${queryParams}`
    const searchResultData: {} = await axios
      .get<{}>(url, axiosRequestConfig)
      .then((response) => response.data)
    return res.send(searchResultData)
  } catch (err) {
    return res.status((err && err.response && err.response.status) || 500).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})

// GET search Topic add and delete
knowledgeHubApi.post('/topic', async (req: Request, res: Response) => {
  try {
    let url: string
    url = `${apiEndpoints.topicsApi}`
    const searchResultData: {} = await axios
      .post<{}>(url, req.body, axiosRequestConfig)
      .then((response) => response.data)
    return res.send(searchResultData)
  } catch (err) {
    return res.status((err && err.response && err.response.status) || 500).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})
