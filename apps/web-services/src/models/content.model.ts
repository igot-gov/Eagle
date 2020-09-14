export interface IHistory {
  appIcon: string
  artifactUrl: string
  clients?: IClient[]
  complexityLevel: string
  contentType: TContentType
  continueLearningData: IContinueLearningResponse
  creatorLogo?: string
  description: string
  displayContentType?: string // For UI
  downloadUrl?: string
  duration: number
  identifier: string
  lastUpdatedOn: string
  me_totalSessionsCount: number
  mediaType: string
  mimeType: EMimeTypes
  name: string
  posterImage?: string
  resourceCategory?: string[]
  resourceType: string
  size?: number
  sourceShortName: string
}
export interface IContent {
  appIcon: string
  artifactUrl: string
  averageRating?: number | string | null
  bannerColor?: string
  body?: string
  certificationList?: IContent[]
  certificationStatus?: TCertificationStatus
  certificationSubmissionDate?: string
  certificationUrl?: string
  children: IContent[]
  collections?: IContent[]
  complexityLevel: string
  contentType: TContentType
  contentUrlAtSource: string
  creatorContacts: ICreator[]
  creatorLogo?: string
  creatorPosterImage?: string
  creatorThumbnail?: string
  creatorDetails?: ICreator[]
  categoryType: string
  description: string
  displayContentType?: string // For UI
  downloadUrl?: string
  duration: number
  exclusiveContent?: boolean
  hasAssessment?: string
  hasAccess: boolean
  idealScreenSize?: string
  identifier: string
  introductoryVideo?: string
  introductoryVideoIcon?: string
  isExternal: string | boolean // made as optional boolean for UI
  isIframeSupported: 'Yes' | 'No' | 'Maybe'
  lastUpdatedOn: string
  learningMode?: TLearningMode
  learningObjective: string
  learningTrack?: string
  license: string
  locale: string
  me_totalSessionsCount: number
  mimeType: EMimeTypes
  minLexVersion?: string
  msArtifactDetails?: IMSArtifactDetails
  name: string
  nextCertificationAttemptDate?: string
  playgroundInstructions?: string
  playgroundResources?: IResourcePlayground[]
  posterImage?: string
  postContents?: IPrePostContent[]
  preContents?: IPrePostContent[]
  preRequisites: string
  price: IPrice
  proctorUrl?: string
  progress?: number
  recentCertificationAttemptScore?: number
  recommendationReasons?: string[]
  registrationInstructions?: string
  resourceCategory?: string[]
  resourceType: string
  skills: ISkill[]
  softwareRequirements?: IResourceDetail[]
  sourceName: string
  ssoEnabled?: boolean
  subtitle?: string
  subTitles?: ISubtitle[]
  status?: string
  studyMaterials?: string[]
  systemRequirements?: string[]
  tags: ITag[]
  topics: IContentTopic[]
  totalRating?: number
  track: ITrack[]
}

export enum EContentTypes {
  PROGRAM = 'Learning Path',
  CHANNEL = 'Channel',
  COURSE = 'Course',
  KNOWLEDGE_ARTIFACT = 'Knowledge Artifact',
  KNOWLEDGE_BOARD = 'Knowledge Board',
  LEARNING_JOURNEY = 'Learning Journeys',
  MODULE = 'Collection',
  RESOURCE = 'Resource',
}

export type TLearningMode = 'Self-Paced' | 'Instructor-Led'
export type TContentType =
  | 'Learning Path'
  | 'Course'
  | 'Collection'
  | 'Channel'
  | 'Resource'
  | EContentTypes.KNOWLEDGE_BOARD
  | EContentTypes.LEARNING_JOURNEY
  | EContentTypes.KNOWLEDGE_ARTIFACT
  | 'Playlist'
  | 'Goal'

export enum ECollectionTypes {
  PLAYLIST = 'playlist',
  GOAL = 'goal',
}

type TCertificationStatus = 'ongoing' | 'passed' | 'canAttempt' | 'cannotAttempt'

export enum EMimeTypes {
  COLLECTION = 'application/vnd.ekstep.content-collection',
  HTML = 'application/html',
  ILP_FP = 'application/ilpfp',
  IAP = 'application/iap-assessment',
  MP3 = 'audio/mpeg',
  MP4 = 'video/mp4',
  M3U8 = 'application/x-mpegURL',
  INTERACTIVE_VIDEO = 'video/interactive',
  PDF = 'application/pdf',
  QUIZ = 'application/quiz',
  DRAG_DROP = 'application/drag-drop',
  HTML_PICKER = 'application/htmlpicker',
  WEB_MODULE = 'application/web-module',
  YOUTUBE = 'video/x-youtube',
  HANDS_ON = 'application/integrated-hands-on',
  CLASS_DIAGRAM = 'application/class-diagram',
  COLLECTION_RESOURCE = 'resource/collection',
  // Added on UI Only
  CERTIFICATION = 'application/certification',
  PLAYLIST = 'application/playlist',

  UNKNOWN = 'application/unknown',
}

interface ITag {
  id: string
  type: string
  value: string
}
interface IMSArtifactDetails {
  channelId: string
  videoId: string
}
interface IClient {
  displayName: string
  id: string
  name: string
}
interface ISubtitle {
  srclang: string
  label: string
  url: string
}
interface IPrePostContent {
  identifier: string
  name: string
}
interface IResourceDetail {
  title?: string
  url?: string
}
interface IResourcePlayground {
  appIcon: string
  artifactUrl: string
  identifier: string
  name: string
}
interface ITrack {
  id: string
  name: string
  status: string
  visibility: string
}
interface ISkill {
  id: string
  category: string
  skill: string
  name: string
}
export interface ICreator {
  id: string
  name: string
  email: string
}
export interface IContentTopic {
  identifier: string
  name: string
}
// API Based

export interface IContact {
  id: string
  name: string
  email: string
}

export interface IPrice {
  currency: string
  value: number
}

export interface IContinueLearningResponse {
  contextualPathId: string
  resourceId: string
  data: string
}

export interface IContinueLearningData extends IContent {
  continueData: string
}

export interface IRecommendationResponse {
  course: IContent
  reasonsForRecommendation: string[]
  score: number
}

export enum EDisplayContentType {
  ASSESSMENT = 'ASSESSMENT',
  AUDIO = 'AUDIO',
  CERTIFICATION = 'CERTIFICATION',
  CHANNEL = 'Channel',
  CLASS_DIAGRAM = 'CLASS_DIAGRAM',
  COURSE = 'COURSE',
  DEFAULT = 'DEFAULT',
  DRAG_DROP = 'DRAG_DROP',
  EXTERNAL_CERTIFICATION = 'EXTERNAL_CERTIFICATION',
  EXTERNAL_COURSE = 'EXTERNAL_COURSE',
  HANDS_ON = 'HANDS_ON',
  IAP = 'IAP',
  INSTRUCTOR_LED = 'INSTRUCTOR_LED',
  INTERACTIVE_VIDEO = 'INTERACTIVE_VIDEO',
  KNOWLEDGE_ARTIFACT = 'KNOWLEDGE_ARTIFACT',
  KNOWLEDGE_BOARD = 'Knowledge Board',
  LEARNING_JOURNEY = 'Learning Journeys',
  MODULE = 'MODULE',
  PDF = 'PDF',
  PROGRAM = 'PROGRAM',
  QUIZ = 'QUIZ',
  RESOURCE = 'RESOURCE',
  VIDEO = 'VIDEO',
  WEB_MODULE = 'WEB_MODULE',
  WEB_PAGE = 'WEB_PAGE',
  YOUTUBE = 'YOUTUBE',
}

export interface IContentMinimal {
  appIcon: string
  artifactUrl: string
  complexityLevel: string
  contentType: TContentType
  description: string
  creatorDetails: ICreator[]
  displayContentType: string
  duration: number
  identifier: string
  learningMode?: TLearningMode
  mimeType: EMimeTypes
  name: string
  posterImage?: string
  resourceType?: string
  status?: string
}

export interface IPageData {
  title: string
  URL: string
  audio?: Array<{
    title: string
    URL: string
    label: string
    srclang: string
  }>
}
