export interface IContentQualityConfig {
  enabledRole?: string[]
  questionsData: IQuestionConfig[]
}

export interface IQuestionConfig {
  type: string
  name: string
  desc?: string
  questions: IQualityQuestion[]
}
export interface IQualityQuestion {
  position: Number
  question: String,
  options: IQualityQuestionOption[]
}

export interface IQualityQuestionOption {
  name: string
  weight: Number
  selected?: boolean
}
