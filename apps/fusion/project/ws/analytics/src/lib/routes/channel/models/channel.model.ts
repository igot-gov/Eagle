export namespace NsChannelAnalytics {
  export interface IChannelAnalyticsData {
    day_wise_users: IBarChartData[]
    avg_time_spent: number
    hits: number
    userCount: number
    city: IChartData[]
    department: IChartData[]
    device: IChartData[]
    [key: string]: any
  }

  export interface IChartData {
    key: string
    doc_count: number
    total_hits: number
  }

  export interface IBarChartData {
    key_as_string: string
    key: number
    doc_count: number
    hits_count: number
  }
}
