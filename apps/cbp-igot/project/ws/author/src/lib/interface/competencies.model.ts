export namespace NSCompetencie {

  export interface ISearch {
    type: string
    field: string
    keyword: string
  }
  export interface ICompetencie {
    additionalProperties: { competencyType: string }
    competencyType?: string
    description: string
    id: string
    name: string
    source: null
    status: string
    type: string
  }
  export interface IWebResponse {
    errorMessage: string
    statusCode: number
    statusMessage: string
  }
  export interface ICompetencieResponse {
    responseData: ICompetencie[]
    statusInfo: IWebResponse
  }

}
