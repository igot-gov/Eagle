export interface IQuizQuestionType {
  fillInTheBlanks: 'fitb',
  matchTheFollowing: 'mtf',
  multipleChoiceQuestionSingleCorrectAnswer: 'mcq-sca',
  multipleChoiceQuestionMultipleCorrectAnswer: 'mcq-mca'
}

export interface IQuizResult {
  timeLimit: number
  isAssessment: boolean
  questions: any[]
}
