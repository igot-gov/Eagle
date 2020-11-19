import { IQuiz } from '../../models/quiz'
import { IUploadPartialS3Response, IUploadS3Request } from '../../models/response/custom-s3-upload'
import { uploadToS3 } from '../S3/upload'

export function uploadQuizData(data: IUploadS3Request<IQuiz>): Promise<IUploadPartialS3Response> {
  return uploadToS3(data.data, data.path, 'quiz.json')
}
