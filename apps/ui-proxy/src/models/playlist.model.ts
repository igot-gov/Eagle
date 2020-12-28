import { EMimeTypes, IContent, TContentType } from './content.model'

export enum EPlaylistTypes {
  ME = 'user',
  SHARED = 'share',
  PENDING = 'pending',
}

export enum EPlaylistUpsertTypes {
  add = 'add',
  delete = 'delete',
}

export interface IPlaylistParams {
  'details-required': boolean
  sourceFields: string
  'visibility': string
}

export interface IPlaylistSbExtBase {
  user: {
    email: string
    name: string
    userId: string
  }
  isShared: number
  visibility: string
  created_on: string
  playlist_title: string
  source_playlist_id?: string
  playlist_id: string
  last_updated_on: string
  shared_by?: {
    user_email: string
    user_id: string
    name: string
  }
  resource_ids: string[]
  root_org: string
  shared_on?: string
  content_meta: IContent[]
  status?: string
}

export interface IPlaylistSbExt extends IPlaylistSbExtBase {
  resource: IResourceSbExt[]
}

export interface IResourceSbExt {
  appIcon: string
  resource_id: string
  time_duration: number
  mimeType: EMimeTypes
  resource_name: string
  contentType: TContentType
  resourceType: string
}

export interface IPlaylistSbExtResponse {
  result: {
    response: IPlaylistSbExtBase[]
  }
}

export interface IPlaylistSbExtRequest {
  content_ids: string[]
  playlist_id?: string
  playlist_title?: string
  shared_with?: string[]
  share_msg?: string
  visibility?: string
}

export interface IPlaylistSbDeleteRequest {
  content: string[]
}

export interface IPlaylistSbUpdateRequest {
  playlist_title: string
  content_ids?: string[]
  visibility?: string
}

export interface IPlaylistUpsertRequest {
  contentIds: string[]
}

export interface IPlaylistCreateRequest {
  createdBy: string
  playlist_title: string
  content_ids: string[]
  shareWith?: string[]
  shareMsg?: string
  visibility: string
}

export interface IPlaylistUpdateTitleRequest {
  playlist_title: string
  content_ids: IPlaylistResource[]
  visibility?: string
}

export interface IPlayListContentResource {
  identifier: string
}

export interface IPlayListUpdateRequest {
  playlist_title: string
  contentIds: IPlayListContentResource[]
}

export interface IPlaylist {
  contents: IContent[]
  createdOn: string
  duration?: number
  icon: string | null
  id: string
  name: string
  type: string
  resourceIds?: string[]
  sharedBy?: string
  sharedByDisplayName?: string
  sharedOn?: string
  visibility: string
  status?: string
}

export interface IPlaylistResource {
  appIcon: string
  identifier: string
  duration: number
  mimeType: EMimeTypes
  displayContentType?: string
  name: string
  contentType: TContentType
  resourceType?: string
  hasAccess?: boolean
  description?: string
}

export interface IPlaylistShareRequestSbExt {
  request: {
    playlist_id: string
    playlist_title: string
    resource_ids: string[]
    shared_with: string[]
  }
}

export interface IPlaylistShareRequest {
  users: string[]
  message?: string
}

export interface IPlaylistSbExtSyncRequest {
  content?: IContent[]
}

export interface IPlaylistGetRequest {
  createdOn: string
  isShared: number
  lastUpdatedOn: number
  playlistTitle: string
  resourceIds: string[]
  sharedBy: string
  sourcePlaylistId?: string
  visibility: string
  user: {
    email: string
    name: string
    userId: string
  }
  resource_ids: IContent[]
  status?: string
}

export interface IPlaylistSyncRequest {
  only_user_playlist_content: IContent[] | []
  only_sharedby_playlist_content: IContent[]
  common_content: IContent[] | []
}
