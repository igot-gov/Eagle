export interface ITopicsApiResponse {
  topics: ITopicResponse[]
}

export interface ITopicResponse {
  'concepts.name': string
  count: number
  id: string
}

export interface ITopic {
  name: string
  count: number
  id: string
}

export interface IInterestApiResponse {
  rootOrg: string
  org: string
  language: string
  interestId: string
  interest: string
}
