import axios from 'axios'
import { axiosRequestConfig } from '../../configs/request.config'
import { CONSTANTS } from './../../utils/env'

interface IContentLocal {
  identifier: string
  locale: string
}

export async function searchForOtherLanguage(
  query: string,
  uuid: string,
  rootOrg: string
): Promise<string[]> {
  const searchBody = {
    filters: [
      {
        andFilters: [
          {
            isContentEditingDisabled: [false],
            isMetaEditingDisabled: [false],
            status: [
              'Draft',
              'InReview',
              'Reviewed',
              'QualityReview',
              'Live',
              'Deleted',
              'MarkedForDeletion',
              'Processing',
              'Unpublished',
            ],
          },
        ],
      },
    ],
    query,
    rootOrg,
    uuid,
  }

  try {
    const v = await axios.post(
      `${CONSTANTS.SEARCH_API_BASE}/v6/search/auth`,
      searchBody,
      axiosRequestConfig
    )
    const result: string[] = [query]
    if (v.data && v.data.result) {
      const resultContent = v.data.result.find(
        (content: { identifier: string }) => content.identifier === query
      )
      if (resultContent) {
        const searchList = ['hasTranslations', 'isTranslationOf']
        searchList.forEach((meta) => {
          if (resultContent[meta] && resultContent[meta].length) {
            resultContent[meta].forEach((data: IContentLocal) => {
              result.push(data.identifier)
            })
          }
        })
      }
    }
    return result
  } catch (ex) {
    return [query]
  }
}
