export interface IFeedback {
  category?: string
  contentId?: string
  role: string
  rootFeedbackId?: string
  sentiment?: string
  text: string
  type: string
}

export interface IFeedbackSubmit {
  category?: string
  content_id?: string
  rootFeedbackId?: string
  sentiment?: string
  text: string
  type: string
  user_id: string
}

export interface IFeedbackSearchQuery {
  query: string
  filters: { [key: string]: string[] }
  viewedBy: string
  all: boolean
  from: number
  size: number
}

export interface IFeedbackSearch {
  query: string
  filters: { [key: string]: string[] }
  viewed_by: string
  user_id: string
  all: boolean
  from: number
  size: number
}
