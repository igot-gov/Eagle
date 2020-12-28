import { IUploadS3Request, IWebModuleRequest, IWebModuleUploadResponse } from '../../models/response/custom-s3-upload'
import { uploadToS3 } from '../S3/upload'
import { IWebModulePartialUploadResponse } from './../../models/response/custom-s3-upload'

export async function uploadWebModuleData(
  data: IUploadS3Request<IWebModuleRequest[]>
): Promise<IWebModuleUploadResponse> {
  const results: Array<Promise<IWebModulePartialUploadResponse>> = data.data.map(async (v) => {
    return {
      name: v.name,
      ...(await uploadToS3(
        v.content,
        `${data.path}${v.name.endsWith('.html') ? '/assets' : ''}`,
        v.name
      )),
    }
  })
  const finalResult: IWebModuleUploadResponse = {
    artifactUrl: null,
    downloadUrl: null,
    error: null,
    subResult: [],
  }

  for (const result of results) {
    const report = await result
    if (report.artifactUrl && report.artifactUrl.endsWith('json')) {
      finalResult.artifactUrl = report.artifactUrl
      finalResult.downloadUrl = report.downloadUrl
    }
    if (finalResult.subResult) {
      finalResult.subResult.push(report)
    } else {
      finalResult.subResult = [report]
    }
  }
  return finalResult
}
