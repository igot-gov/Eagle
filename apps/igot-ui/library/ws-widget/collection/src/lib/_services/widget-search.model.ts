import { NsContent } from './widget-content.model'

export namespace NSSearch {
  export interface IFeatureSearchConfig {
    tabs: IConfigContentStrip[]
  }
  export interface IFilterSearchRequest {
    contentType?: NsContent.EContentTypes[]
    creatorContacts?: string[]
    labels?: string[]
    resourceCategory?: string[]
    catalogPaths?: string[]
  }
  export interface ISearchRequest {
    filters?: IFilterSearchRequest
    query?: string
    isStandAlone?: boolean
    instanceCatalog?: boolean
    locale?: string[]
    pageNo?: number
    pageSize?: number
    uuid?: string
    rootOrg?: string
    // sort?: { lastUpdatedOn: 'desc' | 'asc' }[]
    sort?: { [key: string]: string }[]
  }

  export interface ISearchOrgRegionRecommendationRequest extends ISearchRequest {
    defaultLabel?: string
    preLabelValue?: string
  }

  export interface ISearchV6Request {
    visibleFilters?: ISearchV6VisibleFilters
    excludeSourceFields?: string[]
    includeSourceFields?: string[]
    sort?: ISearchSort[]
    query: string
    sourceFields?: string[]
    locale?: string[]
    pageNo?: number
    pageSize?: number
    filters?: ISearchV6Filters[]
    isStandAlone?: boolean
    didYouMean?: boolean
  }

  export interface ISearchV6RequestV2 {
    request: {
      filters: {
        primaryCategory: any
      },
      query: string,
      sort_by: { lastUpdatedOn: string },
      fields: string[],
      facets: string[]
    }
  }

  export interface ISearchSort {
    [key: string]: 'asc' | 'desc'
  }

  export interface ISearchV6VisibleFilters {
    [key: string]: {
      displayName: string,
      order?: { [key: string]: 'asc' | 'desc' }[]
    }
  }

  export interface ISearchV6Filters {
    andFilters?: { [key: string]: string[] }[]
    notFilters?: { [key: string]: string[] }[]
  }
  export interface ISearchRedirection {
    f?: {
      [index: string]: string[]
    }
    q?: string
    tab?: string
  }
  export interface IConfigContentStrip {
    titleKey?: string
    title?: string
    reqRoles?: string[]
    reqFeatures?: string[]
    searchRedirection?: ISearchRedirection
    searchQuery?: ISearchRequest
    contentIds?: string[]
  }
  export interface ISearchApiResult {
    totalHits: number
    result: NsContent.IContent[]
    filters: IFilterUnitResponse[]
    notToBeShownFilters?: IFilterUnitResponse[]
    filtersUsed: string[]
  }
  export interface ISearchV6ApiResult {
    totalHits: number
    result: NsContent.IContent[]
    filtersUsed: string[]
    notVisibleFilters: string[]
    filters: IFilterUnitResponse[]
    queryUsed?: string
    doYouMean?: string
  }
  export interface IFilterUnitResponse {
    id?: string
    type: string
    displayName: string
    content: IFilterUnitContent[]
  }
  export interface IFilterUnitContent {
    type?: string
    id?: string
    displayName: string
    count: number
    children?: IFilterUnitContent[]
  }
  export interface ITypeUnitResponse {
    displayName: string
    type: string
    count: string
  }
  export interface ISearchV6ApiResultV2 {
    id: string
    ver: string
    ts: string
    params: IParamsContent
    responseCode: string
    result: ISearchData
    filters: IFilterUnitResponse[]
  }

  export interface IFacet {
    displayName: string,
    type: string,
    content: IContentFilter[],
  }
  export interface IContentFilter {
    displayName: string,
    type: string,
    count: number,
    id: string
  }
  export interface ISearchData {
    count: number
    content: NsContent.IContent[]
    facets: IFacetsData[]
  }
  export interface IFacetsData {
    values: IFacetsValues[]
    name: string
  }
  export interface IFacetsValues {
    name: string
    count: number
  }
  export interface ISearchContentData {
    ownershipType: string[]
    parent: string
    code: string
    credentials: {
      enabled: string
    }
    channel: string
    downloadUrl: string
    language: string[]
    mimeType: string
    variants: IVariantsData
    idealScreenSize: string
    leafNodes: string[]
    createdOn: string
    objectType: string
    primaryCategory: string
    children: string[]
    contentDisposition: string
    lastUpdatedOn: string
    contentEncoding: string
    contentType: string
    dialcodeRequired: string
    trackable: {
      enabled: string
      autoBatch: string
    }
    identifier: string
    lastStatusChangedOn: string
    audience: string[]
    os: string[]
    visibility: string
    index: number
    mediaType: string
    osId: string
    languageCode: string[]
    graph_id: string
    nodeType: string
    version: number
    pkgVersion: number
    versionKey: string
    license: string
    idealScreenDensity: string
    depth: number
    lastPublishedOn: string
    compatibilityLevel: number
    leafNodesCount: number
    name: string
    status: string
    node_id: number

  }
  export interface IVariantsData {
    online: {
      ecarUrl: string
      size: number
    },
    spine: {
      ecarUrl: string
      size: number
    }
  }
  export interface IParamsContent {
    resmsgid: string
    msgid: string
    status: string
    err: string
    errmsg: string
  }

}
