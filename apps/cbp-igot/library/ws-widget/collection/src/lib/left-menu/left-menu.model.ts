
export interface ILeftMenu {
  logo?: boolean
  logoPath?: string
  name: string
  menus: IMenu[]
  userRoles?: Set<string>
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
  requiredRoles?: any[]
  isAllowed?: boolean
}
