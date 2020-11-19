import { IPageData } from '../../../models/content.model'
import { IDownloadS3Request } from '../../models/response/custom-s3-download'
import { readFromS3 } from '../S3/read'
import { extractChannelData } from './channel'

export function readJSONData(content: IDownloadS3Request): Promise<{} | null> {
  if (!content.artifactUrl) {
    return Promise.resolve(null)
  } else {
    if (content.mimeType === 'application/channel') {
      return extractChannelData(content.artifactUrl)
    } else if (
      content.mimeType === 'application/quiz' ||
      content.mimeType === 'application/class-diagram'
    ) {
      if (content.categoryType === 'Quiz') {
        return extractChannelData(content.artifactUrl)
      }
      return extractChannelData(content.artifactUrl.replace('.json', '-key.json'))
    } else if (content.mimeType === 'application/web-module') {
      return extractWebModuleData(content.artifactUrl)
    }
    return Promise.resolve(null)
  }
}

export async function extractWebModuleData(artifactUrl: string) {
  // tslint:disable-next-line: no-any
  const result: any = { pageJson: [], pages: [] }
  const res = await readFromS3(artifactUrl)
  result.pageJson = res
  const pageUrlBase = artifactUrl.replace('/manifest.json', '')
  const pagesApi = result.pageJson.map(async (e: IPageData) => {
    if (e.URL) {
      return readFromS3(pageUrlBase + e.URL)
    }
    return null
  })
  result.pages = []
  for (const page of pagesApi) {
    if (page) {
      const data = await page
      result.pages.push(data)
    }
  }
  return result
}
