export interface IUploadS3Response {
  identifier: string
  artifactUrl: string | null
  error: {} | null
  downloadUrl: string | null
  subResult?: IWebModuleUploadResponse[]
}

export interface IWebModulePartialUploadResponse {
  name: string
  downloadUrl: string | null
  artifactUrl: string | null
  error: {} | null
}

export interface IWebModuleUploadResponse {
  artifactUrl: string | null
  error: {} | null
  downloadUrl: string | null
  subResult: IWebModulePartialUploadResponse[]
}

export interface IUploadPartialS3Response {
  artifactUrl: string | null
  error: {} | null
  downloadUrl: string | null
}

export interface IUploadS3Request<T> {
  identfier: string
  categoryType: string
  mimeType: string
  path: string
  data: T
  name?: string
}

export interface IWebModuleRequest {
  name: string
  content: string | {}
}
