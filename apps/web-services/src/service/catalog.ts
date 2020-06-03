import { IFilterUnitContent, IFilterUnitResponse } from '../models/catalog.model'
import { searchV5 } from '../protectedApi_v8/content'

export async function getFilters(userId: string, rootOrg: string, type: string) {
  const requestBody = {
    request: {
      isStandAlone: true,
      pageNo: 0,
      pageSize: 0,
      query: '*',
      rootOrg,
      uuid: userId,
    },
  }

  const response = await searchV5(requestBody)
  const catalogPaths = response.filters
    .find((filter: IFilterUnitResponse) => filter.type === type)

  if (catalogPaths) {
    return catalogPaths.content
  }
  return []
}

export function getFilterUnitByType(filter: IFilterUnitContent | undefined, type: string): IFilterUnitContent | null {
  if (filter && filter.type === type) {
    return filter
  } else if (filter && filter.children != null) {
    let result = null
    for (let i = 0; result == null && i < filter.children.length; i++) {
      result = getFilterUnitByType(filter.children[i], type)
    }
    return result
  }
  return null
}
