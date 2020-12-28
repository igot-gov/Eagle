
export interface ILeftMenu {
  logo?: boolean
  logoPath?: string
  name: string
  menus: IMenu[]
}
export interface IMenu {
  name: string
  key: string
  render: boolean
  fragment?: boolean
  badges?: {
    enabled: boolean
    uri?: string
  }
  enabled: boolean
  routerLink: string,
  customRouting?: boolean
  paramaterName?: string
  queryParams?: string
}
