import { IWidgetGraphData } from '@ws-widget/collection'

export namespace NsAnalytics {
  export interface IConfig {
    currentYear: number
    threshold: number
    todaysDate: string
    today: boolean
    years: any[]
    fromDate: Date | string | null
    toDate: Date | string | null
    type: string
    quarters: IQuarters[]
    selectedYear: number | null
    selectedQuarters: any[]
  }

  export interface IQuarters {
    name: string
    id: number
    color: string
  }
  export interface IQuarterObj {
    key: string
    value: number
  }

  export interface IFilterObj {
    [key: string]: string
  }

  export interface IFilter {
    filterName: string
    filterType: string
  }
  export interface IGraphWidget {
    widgetType: string
    widgetSubType: string
    widgetData: IWidgetGraphData
  }
  export interface IReportResponse {
    reports: IReportData[]
  }
  export interface IReportData {
    report_id: string
    description: string
    query_id: number
    connection_id: number
    report_name: string
    tab_name: string
    loader: boolean
    downloadStatus: string
  }
  export interface IRoleCompletionResponse {
    pathway_completion_role: IRoleCompletionData[]
  }
  export interface IRoleCompletionData {
    job_title: string
    learning_pathname: string
    completed: string
    total: string
  }
  export interface IRoleNameData {
    roles: [string]
  }
  export interface ICourseCompletionResponse {
    user_progress: ICourseCompletionData[]
  }
  export interface ICourseCompletionData {
    levels: ILevels[]
    job_title: string
    wid: string
    stars_id: string
  }
  export interface ILevels {
    certified: IDetails[]
    professional: IDetails[]
    Master: IDetails[]
  }
  export interface IDetails {
    course_code: string
    course_name: string
    progress: string
  }
}
