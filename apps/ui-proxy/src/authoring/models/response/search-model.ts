import { IContentUserDetails, TLearningMode, TMimeTypes, TStatus } from './../content-model'
export interface ISearchResponse {
  id: string
  ver: string
  ts: string
  responseCode: string
  result?: {
    response?: {
      result?: ISearchContent[]
      totalHits: number
      filtersUsed: string[]
      notToBeShownFilters: string[]
      filters: ISearchResponseFilter[]
    }
  }
}

export interface ISearchContent {
  identifier: string
  creatorContacts: IContentUserDetails[]
  trackContacts: IContentUserDetails[]
  publisherDetails: IContentUserDetails[]
  creatorDetails: IContentUserDetails[]
  mimeType: TMimeTypes
  learningMode: TLearningMode
  duration: number
  expiryDate: string
  size: number
  collections: ISearchCollections[]
  children: ISearchCollections[]
  name: string
  lastUpdatedOn: string
  isStandAlone: boolean
  contentType: string
  status: TStatus
  hasTranslations?: ISearchResponseTranslations[]
  isTranslationOf?: ISearchResponseTranslations[]
}

export interface ISearchCollections {
  reason: string
  identifier: string
  childrenClassifiers: []
  visibility: string
  name: string
  index: 0
  description: string
  mimeType: TMimeTypes
  objectType: string
  relation: string
  status: TStatus
}

export interface ISearchResponseFilter {
  displayName: string
  type: string
  content: Array<{ displayName: string; type: string; count: number; id: string }>
}

export interface ISearchResponseTranslations {
  identifier: string
  locale: string
}
