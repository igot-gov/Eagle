export type Primitive = string | boolean | number | undefined | null | Symbol

export interface IJSON {
  [index: string]: IJSON | Primitive
}

export interface IGenericApiResponse<T> {
  id: string
  ver: string
  ts: string
  params?: {}
  responseCode: string
  result: IGenericApiResult<T>
}

export interface IGenericApiResult<T> {
  response: T
}
