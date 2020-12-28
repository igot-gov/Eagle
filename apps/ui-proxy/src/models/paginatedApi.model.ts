import { IContent } from './content.model'

export interface IPaginatedApiResponse {
  contents: IContent[]
  hasMore?: boolean
  pageState?: string
  totalHits?: number
}
