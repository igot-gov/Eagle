import {
  IPlaylist,
  IPlaylistCreateRequest,
  IPlaylistGetRequest,
  IPlaylistSbDeleteRequest,
  IPlaylistSbExtBase,
  IPlaylistSbExtRequest,
  IPlaylistSbExtSyncRequest,
  IPlaylistSbUpdateRequest,
  IPlaylistSyncRequest,
  IPlaylistUpdateTitleRequest,
  IPlaylistUpsertRequest
} from '../models/playlist.model'

function transformToPlaylistBase(playlistSbExt: IPlaylistSbExtBase): IPlaylist {
  return {
    contents: playlistSbExt.content_meta,
    createdOn: playlistSbExt.created_on,
    icon: playlistSbExt.content_meta.length ? playlistSbExt.content_meta[0].appIcon : null,
    id: playlistSbExt.playlist_id,
    name: playlistSbExt.playlist_title,
    resourceIds: playlistSbExt.resource_ids,
    sharedBy: playlistSbExt.shared_by && playlistSbExt.shared_by.user_id,
    sharedByDisplayName: playlistSbExt.shared_by && playlistSbExt.shared_by.name,
    sharedOn: playlistSbExt.shared_on,
    status: playlistSbExt.status,
    type: Boolean(playlistSbExt.shared_by) ? 'share' : 'user',
    visibility: playlistSbExt.visibility,
  }
}
export function transformToPlaylistV2(playlistSbExt: IPlaylistSbExtBase): IPlaylist {
  return transformToPlaylistBase(playlistSbExt)
}

function transformToPlaylistBaseV3(playlistSbExt: IPlaylistGetRequest, playlistId: string): IPlaylist {

  return {
    contents: playlistSbExt.resource_ids,
    createdOn: playlistSbExt.createdOn,
    duration: playlistSbExt.resource_ids.reduce((duration, content) => duration + content.duration, 0),
    icon: playlistSbExt.resource_ids.length ? playlistSbExt.resource_ids[0].appIcon : null,
    id: playlistId,
    name: playlistSbExt.playlistTitle,
    resourceIds: playlistSbExt.resourceIds,
    sharedBy: playlistSbExt.sharedBy,
    status: playlistSbExt.status,
    type: Boolean(playlistSbExt.sharedBy) ? 'share' : 'user',
    visibility: playlistSbExt.visibility,
  }
}

export function transformToPlaylistV3(playlistSbExt: IPlaylistGetRequest, playlistId: string): IPlaylist {
  return transformToPlaylistBaseV3(playlistSbExt, playlistId)
}

export function transformToSbExtCreateRequest(upsertRequest: IPlaylistCreateRequest): IPlaylistSbExtRequest {
  return {
    content_ids: upsertRequest.content_ids,
    playlist_title: upsertRequest.playlist_title,
    visibility: upsertRequest.visibility,
  }
}

export function transformToSbExtSyncRequest(syncRequest: IPlaylistSyncRequest): IPlaylistSbExtSyncRequest {
  return {
    content: syncRequest.only_sharedby_playlist_content,
  }
}

export function transformToSbExtUpsertRequest(upsertRequest: IPlaylistUpsertRequest): IPlaylistSbExtRequest {
  /*add content to a playlist*/
  return {
    content_ids: upsertRequest.contentIds,
  }
}

export function transformToSbExtDeleteRequest(upsertRequest: IPlaylistUpsertRequest): IPlaylistSbDeleteRequest {
  /* delete contents from playlist*/
  return {
    content: upsertRequest.contentIds,
  }
}

export function transformToSbExtUpdateRequest(updateRequest: IPlaylistUpdateTitleRequest): IPlaylistSbUpdateRequest {
  /* for Patch request to change playlist title */
  return {
    content_ids: updateRequest.content_ids.map((content) => content.identifier),
    playlist_title: updateRequest.playlist_title,
    visibility: updateRequest.visibility,
  }
}
