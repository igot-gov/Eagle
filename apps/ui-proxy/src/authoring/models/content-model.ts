export interface IContent {
  identifier: string
  locale: string
}

export interface IContentUserDetails {
  name: string
  id: string
}

export type TMimeTypes = 'application/pdf' | 'application/html' | 'application/channel'
export type TLearningMode = 'Instructor-Led' | 'Self-Paced'
export type TStatus =
  | 'Draft'
  | 'InReview'
  | 'Reviewed'
  | 'Live'
  | 'QualityReview'
  | 'Processing'
  | 'Deleted'
