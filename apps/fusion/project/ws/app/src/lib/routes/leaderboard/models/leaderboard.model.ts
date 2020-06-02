export interface ILeaderboard {
  start_date: string
  end_date: string
  lastUpdatedDate: string
  leaderboard_type: ELeaderboardType
  leaderboard_year: number
  duration_type: ELeaderboardDurationType
  duration_value: number
  prev?: ILeaderboardPrevNext
  next?: ILeaderboardPrevNext
  items: ILeaderboardItem[]
}

export interface ILeaderboardPrevNext {
  leaderboard_year: number
  duration_value: number
}

export interface ILeaderboardItem {
  first_name: string
  last_name: string
  email_id: string
  designation?: string
  rank: number
  points: number
  leaderboard_type?: ELeaderboardType
  leaderboard_year?: number
  duration_type?: ELeaderboardDurationType
  duration_value?: number
}

export interface IHallOfFameItem {
  first_name: string
  last_name: string
  email_id: string
  designation?: string
  rank: number
  points: number
  leaderboard_type: ELeaderboardType
  leaderboard_year: number
  duration_type: ELeaderboardDurationType
  duration_value: number
}

export enum ELeaderboardType {
  Learner = 'L',
  Collaborator = 'C',
}

export enum ELeaderboardDurationType {
  Weekly = 'W',
  Monthly = 'M',
  HallOfFame = 'M',
}

export enum EDurationTypeRouteParam {
  Weekly = 'weekly',
  Monthly = 'monthly',
  HallOfFame = 'hall-of-fame',
}
