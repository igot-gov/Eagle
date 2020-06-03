export interface ICatalogItem {
    identifier: string,
    value: string,
    children: string[],
    parent: string[]
}

export interface ICatalogTreeNode {
    identifier: string,
    value: string,
    children: ICatalogTreeNode[],
    path?: string[],
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
