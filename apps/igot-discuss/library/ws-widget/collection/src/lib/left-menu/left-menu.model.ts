
export interface ILeftMenu {
  name: string
  key: string
  render: boolean
  badges: {
    enabled: boolean
    uri?: string
  }
  enabled: boolean
  routerLink: string
}
