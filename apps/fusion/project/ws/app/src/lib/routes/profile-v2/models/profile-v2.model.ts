
export namespace NSProfileData {
  export interface IProfileJsonData {
    tabs: IProfileTab[]
  }

  export interface IProfileTab {
    name: string
    key: string
    badges: {
      enabled: boolean
      uri?: string
    }
    enabled: boolean
    routerLink: string
  }
  export interface IProfile {
    name?: string
  }
  export interface IPortalProfile {
    department_name: string
    email: string
    first_name: string
    kid: string
    last_name: string
    rank: number
    wid: string
  }
}
