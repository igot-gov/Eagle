export interface IDownloadS3Response {
  identifier: string
  content: {} | null
}

export interface IDownloadS3Request {
  categoryType: string
  mimeType: string
  artifactUrl: string
  identifier: string
}
