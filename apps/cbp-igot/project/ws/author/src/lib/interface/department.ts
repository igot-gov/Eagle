export interface IDepartment {
  id: number
  rootOrg: string
  deptName: string
  deptTypeId: number
  deptTypeInfo: IDeptTypeInfo
  rolesInfo: IDepartmentRole[]
  description: string
  noOfUsers: number
  headquarters: string
  logo: string
  adminUserList: any
  active_users: any
  inActive_users: any
  blocked_users: any
}

export interface IDepartmentRole {
  deptRoleId: number
  roleId: number
  roleName: string
  descritpion: string
}

export interface IDeptTypeInfo {
  id: number
  deptType: string
  deptSubType: string
  description: string
}
