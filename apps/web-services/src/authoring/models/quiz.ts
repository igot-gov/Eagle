export interface IMCQ {
  questionId: string
  options: Array<{
    optionId: string
    text: string
    isCorrect: boolean
  }>
  question: string
  multiSelection: boolean
  questionType: 'mcq-sca' | 'mcq-mca'
}

export interface IFillUps {
  questionId: string
  options: Array<{
    optionId: string
    text: string
    isCorrect: boolean
  }>
  question: string
  multiSelection: boolean
  questionType: 'fitb'
}

export interface IMatch {
  questionId: string
  options: Array<{
    optionId: string
    text: string
    isCorrect: boolean
    match: string
  }>
  question: string
  multiSelection: boolean
  questionType: 'mtf'
}

export interface IQuiz {
  timeLimit: number
  isAssessment: boolean
  questions: Array<IFillUps | IMCQ | IMatch>
}
