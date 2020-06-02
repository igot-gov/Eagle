import axios from 'axios'
import { CONSTANTS } from '../../utils/env'
import { logError } from '../../utils/logger'
import { axiosRequestConfig } from './../../configs/request.config'
import { ISearchResponse } from './../models/response/search-model'

export const fetchTranslatedContents = async (
  query: string,
  uuid: string,
  rootOrg = 'Infosys'
): Promise<string[]> => {
  // Generating search request body
  const searchBody = {
    request: {
      query,
      rootOrg,
      uuid,
    },
  }
  // Creating
  const ids: string[] = [query]
  try {
    const result: ISearchResponse = await axios.post(
      CONSTANTS.SB_EXT_API_BASE + '/authsearch5',
      searchBody,
      axiosRequestConfig
    )
    const data =
      result && result.result && result.result.response && result.result.response.result
        ? result.result.response.result
        : null
    if (data && data.length) {
      const requiredContent = data.find((v) => v.identifier === query)
      if (requiredContent) {
        if (requiredContent.isTranslationOf && requiredContent.isTranslationOf.length) {
          requiredContent.isTranslationOf.forEach((v) => ids.push(v.identifier))
        }
        if (requiredContent.hasTranslations && requiredContent.hasTranslations.length) {
          requiredContent.hasTranslations.forEach((v) => ids.push(v.identifier))
        }
      }
    }
  } catch (ex) {
    logError('Authoring tool Search for related content failed. Error : ' + JSON.stringify(ex))
  }
  // Returning the list of ids related to the content including the own content
  return ids
}
