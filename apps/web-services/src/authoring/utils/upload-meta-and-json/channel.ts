import { IUploadPartialS3Response, IUploadS3Request } from '../../models/response/custom-s3-upload'
import { uploadToS3 } from '../S3/upload'

export function uploadChannelData(data: IUploadS3Request<{}>): Promise<IUploadPartialS3Response> {
  return uploadToS3(data.data, data.path, 'channel.json')
}
