import { IContent, IContentMinimal, TContentType } from '../models/content.model'
const CONTENT_URL_PREFIX_SLICE_REGEX = /http:\/\/private-[^/]+/

export function processContent(content: IContent): IContent {
  if (!content) {
    return content
  }
  return {
    ...content,
    appIcon: processUrl(content.appIcon),
    artifactUrl: processUrl(content.artifactUrl),
    children: Array.isArray(content.children) ? content.children.map((u) => processContent(u)) : [],

    displayContentType: processDisplayContentType(content.contentType, content.resourceType),
    downloadUrl: processDownloadUrl(content.downloadUrl || ''),
    introductoryVideo: processUrl(content.introductoryVideo),
    introductoryVideoIcon: processUrl(content.introductoryVideoIcon),
    isExternal: processIsExternal(content.isExternal),
    playgroundResources: (content.playgroundResources || []).map((u) => ({
      ...u,
      artifactUrl: processUrl(u.artifactUrl),
    })),
    subTitles: (content.subTitles || []).map((u) => ({
      ...u,
      url: processUrl(u.url),
    })),
  }
}

export function shuffleContent(array: IContent[]) {
  let currentIndex = array.length
  let randomIndex = 0
  let temporaryValue: IContent | null = null
  while (0 !== currentIndex) {
    // Pick a remaining element...
    randomIndex = Math.floor(Math.random() * currentIndex)
    currentIndex -= 1

    // And swap it with the current element.
    temporaryValue = array[currentIndex]
    array[currentIndex] = array[randomIndex]
    array[randomIndex] = temporaryValue
  }

  return array
}

export function getMinimalContent(content: IContent): IContentMinimal {
  return {
    appIcon: processUrl(content.appIcon),
    artifactUrl: content.artifactUrl,
    complexityLevel: content.complexityLevel,
    contentType: content.contentType,
    creatorDetails: content.creatorDetails || content.creatorContacts,
    description: content.description,
    displayContentType: processDisplayContentType(content.contentType, content.resourceType),
    duration: content.duration,
    identifier: content.identifier,
    learningMode: content.learningMode,
    mimeType: content.mimeType,
    name: content.name,
    status: content.status,
  }
}

function processIsExternal(isExternal: string | boolean): boolean {
  if (typeof isExternal === 'boolean') {
    return isExternal
  }
  return typeof isExternal === 'string' ? isExternal.toLowerCase() === 'yes' : false
}

export function processUrl(url: string | null | undefined) {
  return (url || '').replace(CONTENT_URL_PREFIX_SLICE_REGEX, '/apis/proxies/v8')
}
export function appendUrl(url: string) {
  return '/apis/proxies/v8' + url
}

export function appendProxiesUrl(url: string) {
  return '/apis/proxies/v8/web-hosted/navigator/images/' + url
}

export function processDisplayContentType(contentType: TContentType, resourceType?: string) {
  return resourceType || contentType
}

export function processDownloadUrl(url: string) {
  return processUrl(url)
}
