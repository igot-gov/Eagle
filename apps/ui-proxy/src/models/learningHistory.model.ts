import { TContentType } from './content.model'

export interface ILearningHistory {
  count: number
  error?: string
  results: ILearningHistoryItem[]
}

export interface ILearningHistoryItem {
  children: string[]
  contentType?: TContentType
  displayContentType?: string
  identifier?: string
  last_ts?: number
  name?: string
  pending?: number
  progress?: number
  thumbnail?: string
  timeLeft?: number
  totalDuration?: number
}
