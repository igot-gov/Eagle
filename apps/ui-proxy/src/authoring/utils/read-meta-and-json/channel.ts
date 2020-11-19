import { readFromS3 } from '../S3/read'

export function extractChannelData(url: string): Promise<{}> {
  return readFromS3(url)
}
