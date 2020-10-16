export namespace NSCompetenciesData {
  export interface ICompetenciesJsonData {
    tabs: ICompetenciesTab[]
  }

  export interface ICompetenciesTab {
    name: string
    key: string
    badges: {
      enabled: boolean
      uri?: string
    }
    enabled: boolean
    routerLink: string
  }
}
