export interface IColums {
  displayName: String
  key: String
  image?: string
  isList?: boolean
  prop?: string
  link?: {
    path: string
    dParams?: string
    sParams?: string
  }
  defaultValue: any
  pipe?: any
}
export interface IAction {
  name: String
  icon: String
  type: string
  disabled?: boolean
  label: string
  condition?: string
  optional?: boolean
  optional_key?: string
  optional_Value?: string
}

export interface ITableMenu {
  action: string
  disabled: boolean
  icon: string
  name: string
}
export interface IActionsMenu {
  headIcon: String
  rowIcon: string
  menus: ITableMenu[]
}
export interface ITable {
  // data: any[]
  columns: IColums[]
  actions: IAction[]
  actionsMenu?: IActionsMenu
  needHash: boolean
  needCheckBox: boolean
  sortState?: string
  sortColumn?: string
}

// export interface ITable {
//   data: any[]
//   actions: IAction[]
//   columns: IColums[]
// }
