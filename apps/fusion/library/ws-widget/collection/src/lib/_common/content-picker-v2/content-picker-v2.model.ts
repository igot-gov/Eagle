export interface IAppliedFilters {
  [key: string]: Set<string>
}

export interface IWidgetData {
  preselected?: Set<string>
  includedFilters?: IIncludedFilters
  preAppliedFilters?: IFilter
  mode?: 'ids' | 'query'
  sortableFields?: ISortableFields
  enablePreselected?: boolean
}

export interface IFilter {
  [key: string]: string[]
}

export interface IIncludedFilters {
  [key: string]: {
    displayName: string
    values: string[]
  }
}

export interface ISortableFields {
  [key: string]: {
    order: 'asc' | 'desc'
    displayName: string
  }
}
