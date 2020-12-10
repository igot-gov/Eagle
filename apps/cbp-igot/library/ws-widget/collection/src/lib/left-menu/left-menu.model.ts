
export interface ILeftMenu {
  logo?: boolean
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
