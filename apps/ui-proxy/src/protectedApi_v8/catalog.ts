import { Router } from 'express'
import { IFilterUnitContent } from '../models/catalog.model'
import { getFilters, getFilterUnitByType } from '../service/catalog'
import { ERROR } from '../utils/message'
import { extractUserIdFromRequest } from '../utils/requestExtract'

export const catalogApi = Router()

catalogApi.get('/', async (req, res) => {
  try {
    const userId = extractUserIdFromRequest(req)
    const rootOrg = req.headers.rootorg
    if (!rootOrg) {
      res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
      return
    }
    if (typeof rootOrg === 'string') {
      const filters = await getFilters(userId, rootOrg, 'catalogPaths')
      res.send(filters)
      return
    }

    res.status(400).send({ error: ERROR.ERROR_NO_ORG_DATA })
  } catch (err) {
    res.status((err && err.response && err.response.status) || 500).send(
      (err && err.response && err.response.data) || {
        error: 'Failed due to unknown reason',
      }
    )
  }
})

catalogApi.post('/tags', async (req, res) => {
  try {
    const userId = extractUserIdFromRequest(req)
    const rootOrg = req.headers.rootorg
    if (!rootOrg) {
      res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
      return
    }
    const { tags, type } = req.body
    if (typeof rootOrg === 'string') {
      const filterContents: IFilterUnitContent[] = await getFilters(userId, rootOrg, 'catalogPaths')
      const filterContent = filterContents.find((content) => content.type === type)
      const catalog: IFilterUnitContent | null = getFilterUnitByType(filterContent, tags)
      if (catalog) {
        res.send(catalog.children)
        return
      }

      res.status(400).send({ error: ERROR.ERROR_NO_ORG_DATA })
    }
  } catch (err) {
    res.status((err && err.response && err.response.status) || 500).send(
      (err && err.response && err.response.data) || {
        error: 'Failed due to unknown reason',
      }
    )
  }
})
