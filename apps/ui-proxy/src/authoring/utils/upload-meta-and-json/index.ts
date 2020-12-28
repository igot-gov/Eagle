import { IClassDiagram } from '../../models/class-diagram'
import { IUploadPartialS3Response, IUploadS3Request } from '../../models/response/custom-s3-upload'
import { IQuiz } from './../../models/quiz'
import { IWebModuleRequest } from './../../models/response/custom-s3-upload'
import { uploadAssessmentData } from './assessment'
import { uploadChannelData } from './channel'
import { uploadClassdiagramData } from './class-diagram'
import { uploadQuizData } from './quiz'
import { uploadUnKownData } from './unkown'
import { uploadWebModuleData } from './web-module'

export function uploadJSONData(
  content: IUploadS3Request<{} | IQuiz | IWebModuleRequest[]>
): Promise<IUploadPartialS3Response> {
  if (content.mimeType === 'application/channel') {
    return uploadChannelData(content)
  } else if (content.mimeType === 'application/quiz' && content.categoryType === 'Quiz') {
    return uploadQuizData(content as IUploadS3Request<IQuiz>)
  } else if (content.mimeType === 'application/quiz' && content.categoryType === 'Assessment') {
    return uploadAssessmentData(content as IUploadS3Request<IQuiz>)
  } else if (content.mimeType === 'application/web-module') {
    return uploadWebModuleData(content as IUploadS3Request<IWebModuleRequest[]>)
  } else if (content.mimeType === 'application/class-diagram') {
    return uploadClassdiagramData(content as IUploadS3Request<IClassDiagram>)
  }
  return uploadUnKownData(content)
}
