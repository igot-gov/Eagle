
export namespace NSScrollspyMenuData {
  export interface IScrollspyJsonData {
    tabs: IScrollspyTab[]
  }

  export interface IScrollspyTab {
    name: string
    key: string
    render: boolean
    enabled: boolean
  }
}
