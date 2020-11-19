import { IUploadPartialS3Response, IUploadS3Request } from '../../models/response/custom-s3-upload'
import { uploadToS3 } from '../S3/upload'
import { IQuiz } from './../../models/quiz'

export async function uploadAssessmentData(
  data: IUploadS3Request<IQuiz>
): Promise<IUploadPartialS3Response> {
  const key = uploadToS3(data.data, data.path, 'assessment-key.json')
  const question = uploadToS3(processAssessmentData(data.data), data.path, 'assessment.json')
  const keyReport = await key
  const questionReport = await question
  if (keyReport.artifactUrl && questionReport.artifactUrl) {
    return questionReport
  } else {
    return {
      artifactUrl: null,
      downloadUrl: null,
      error: keyReport.error || questionReport.error,
    }
  }
}

function processAssessmentData(data: IQuiz): IQuiz {
  const returnValue: IQuiz = JSON.parse(JSON.stringify(data))
  returnValue.questions.forEach((questionSet) => {
    if (questionSet.questionType === 'mcq-sca' || questionSet.questionType === 'mcq-mca') {
      questionSet.options.forEach((optionSet) => {
        optionSet.isCorrect = false
      })
    }
    if (questionSet.questionType === 'fitb') {
      questionSet.options.forEach((optionSet) => {
        optionSet.isCorrect = false
        optionSet.text = ''
      })
    }
    if (questionSet.questionType === 'mtf') {
      const optionsBackUp = JSON.parse(JSON.stringify(questionSet.options))
      const shuffledArray = shuffle(questionSet.options.length)
      questionSet.options.forEach((optionSet, index) => {
        optionSet.isCorrect = false
        optionSet.match = optionsBackUp[shuffledArray[index]].match
      })
    }
  })
  return returnValue
}

function shuffle(length: number) {
  let currentIndex = length
  let temp = 0
  let randomIndex = 0
  const array = []
  let i = 0
  while (i < length) {
    array.push(i)
    i += 1
  }
  // While there remain elements to shuffle...
  while (0 !== currentIndex) {
    // Pick a remaining element...
    randomIndex = Math.floor(Math.random() * currentIndex)
    currentIndex -= 1

    // And swap it with the current element.
    temp = array[currentIndex]
    array[currentIndex] = array[randomIndex]
    array[randomIndex] = temp
  }
  return array
}
