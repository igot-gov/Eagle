import axios from 'axios'
import { Request, Response, Router } from 'express'
import { IGenericApiResponse } from '../../models/generic.model'
import { ITopic, ITopicResponse, ITopicsApiResponse } from '../../models/topic.model'
import { CONSTANTS } from '../../utils/env'

const apiEndPoints = {
  autocomplete: `${CONSTANTS.ES_BASE}/lex_topic/_search`,
  recommend: `${CONSTANTS.SB_EXT_API_BASE}/v1/topics/recommended?q=new`,
}
export const topicApi = Router()

topicApi.get('/recommend', async (_req: Request, res: Response) => {
  try {
    const topicResponse: ITopicsApiResponse = await axios
      .get<IGenericApiResponse<ITopicsApiResponse>>(apiEndPoints.recommend)
      .then((response) => response.data.result.response)

    const topics: ITopic[] = topicResponse.topics.map((topicsResponse: ITopicResponse) => ({
      count: topicsResponse.count,
      id: topicsResponse.id,
      name: topicsResponse['concepts.name'],
    }))
    res.send(topics)
  } catch (err) {
    res.status((err && err.response && err.response.status) || 500).send(
      (err && err.response && err.response.data) || {
        error: 'Failed due to unknown reason',
      }
    )
  }
})

topicApi.get('/autocomplete', async (req: Request, res: Response) => {
  try {
    const body = {
      _source: { includes: 'name' },
      suggest: {
        'name-suggest': {
          completion: { field: 'name' },
          prefix: req.query.q,
        },
      },
    }

    const response = await axios.request({
      auth: {
        password: CONSTANTS.ES_PASSWORD,
        username: CONSTANTS.ES_USERNAME,
      },
      data: body,
      method: 'POST',
      url: apiEndPoints.autocomplete,
    })
    res.json(response.data)
  } catch (err) {
    res.status((err && err.response && err.response.status) || 500).send(
      (err && err.response && err.response.data) || {
        error: 'Failed due to unknown reason',
      }
    )
  }
})
