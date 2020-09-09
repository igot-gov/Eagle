export interface IUserDetailCard {
  wid: string,
  root_org: string
  org: string,
  first_name: string,
  last_name: string,
  email: string,
  department_name: string,
  time_inserted: string,
  full_count: number,
}

export interface IUserInfo {
  limit: number,
  offset: number,
  totalCount: number,
  users: IUserDetailCard[],
}
