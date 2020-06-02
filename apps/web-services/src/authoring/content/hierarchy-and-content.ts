import { Request } from 'express'
import { readJSONData } from '../utils/read-meta-and-json'
import { IContent } from './../../models/content.model'
import { getHierarchyV2, getMultipleHierarchyV2 } from './hierarchy'

export async function getHierarchyV2WithContent(
  id: string,
  org: string,
  rootOrg: string,
  req: Request
  // tslint:disable-next-line: no-any
): Promise<{ content: IContent; data: any }> {
  const content = await getHierarchyV2(id, org, rootOrg, req)
  const data = await readJSONData(content)
  return { content, data }
}

export async function getMultipleHierarchyV2WithContent(
  ids: string[],
  org: string,
  rootOrg: string,
  req: Request
  // tslint:disable-next-line: no-any
): Promise<Array<{ content: IContent; data: any }>> {
  // tslint:disable-next-line: no-any
  const returnData: Array<{ content: IContent; data: any }> = []
  const contents = await getMultipleHierarchyV2(ids, org, rootOrg, req)
  const jsonPromises = contents.map(async (content) => {
    return { content, data: await readJSONData(content) }
  })
  for (const result of jsonPromises) {
    const data = await result
    returnData.push(data)
  }

  return returnData
}
