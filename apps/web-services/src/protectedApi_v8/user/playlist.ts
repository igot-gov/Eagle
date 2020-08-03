import axios from 'axios'
import { Router } from 'express'
import { axiosRequestConfig } from '../../configs/request.config'
import { IPaginatedApiResponse } from '../../models/paginatedApi.model'
import {
  EPlaylistUpsertTypes,
  IPlaylist,
  IPlaylistCreateRequest,
  IPlaylistParams,
  IPlaylistSbExtResponse,
  IPlaylistShareRequest,
  IPlaylistUpdateTitleRequest,
  IPlaylistUpsertRequest
} from '../../models/playlist.model'
import {
  transformToPlaylistV2,
  transformToPlaylistV3,
  transformToSbExtCreateRequest,
  transformToSbExtDeleteRequest,
  transformToSbExtSyncRequest,
  transformToSbExtUpdateRequest,
  transformToSbExtUpsertRequest
} from '../../service/playlist'
import { CONSTANTS } from '../../utils/env'
import { getStringifiedQueryParams } from '../../utils/helpers'
import { logError } from '../../utils/logger'
import { ERROR } from '../../utils/message'
import { extractUserIdFromRequest } from '../../utils/requestExtract'

const API_END_POINTS = {
  playlist: (userId: string, playlistId: string) =>
    `${CONSTANTS.PLAYLIST_API_BASE}/v1/users/${userId}/playlist/${playlistId}`,
  playlistV1: (userId: string) => `${CONSTANTS.PLAYLISTV1_API_BASE}/v1/users/${userId}`,
}

const GENERAL_ERROR_MSG = 'Failed due to unknown reason'

async function sharePlaylist(
  userId: string,
  playlistId: string,
  request: IPlaylistShareRequest,
  rootOrg: string
) {
  /* for sharing a playlist with another user */
  const url = `${API_END_POINTS.playlistV1(userId)}/playlists/${playlistId}/share`
  return axios({
    ...axiosRequestConfig,
    data: {
      message: request.message,
      users: request.users,
    },
    headers: {
      rootOrg,
    },
    method: 'POST',
    url,
  })
}

async function getPlaylistsAllTypes(
  userId: string,
  rootOrg: string,
  params: IPlaylistParams | null
) {
  /* function to get user, shared, and pending playlists*/
  try {
    const playlistsPromise = await axios.get(
      `${API_END_POINTS.playlistV1(userId)}/playlists`,
      { ...axiosRequestConfig, headers: { rootOrg }, params }
    ) /* get request to fetch user and shared playlists*/

    const pendingPlaylistsPromise = await axios.get(
      `${API_END_POINTS.playlistV1(userId)}/shared-playlist`,
      { ...axiosRequestConfig, headers: { rootOrg }, params }
    ) /* get request to fetch pending playlists awaiting acceptance or refusal*/
    const playlists: IPlaylistSbExtResponse = {
      result: {
        response: playlistsPromise.data,
      },
    }
    const pendingPlaylists: IPlaylistSbExtResponse = {
      result: {
        response: pendingPlaylistsPromise.data,
      },
    }
    /* content.shared_by is null for user playlists */
    return {
      data: {
        pending: pendingPlaylists.result.response.map(transformToPlaylistV2),
        share: playlists.result.response
          .filter((content) => content.shared_by)
          .map(transformToPlaylistV2),
        user: playlists.result.response
          .filter((content) => !content.shared_by)
          .map(transformToPlaylistV2),
      },
      error: undefined,
    }
  } catch (err) {
    return { data: undefined, error: err }
  }
}

export async function getPlaylist(userId: string, playlistId: string, rootOrg: string) {
  const response = await axios.get(`${API_END_POINTS.playlistV1(userId)}/playlists/${playlistId}`, {
    ...axiosRequestConfig,
    headers: { rootOrg },
  })
  return transformToPlaylistV3(response.data, playlistId)
}

async function getPlaylists(userId: string, rootOrg: string): Promise<IPlaylist[]> {
  /* get pending playlists */
  const response = await axios.get(`${API_END_POINTS.playlistV1(userId)}/shared-playlist`, {
    ...axiosRequestConfig,
    headers: { rootOrg },
  })
  const playlistSbExtResponse: IPlaylistSbExtResponse = {
    result: {
      response: response.data,
    },
  }
  if (
    playlistSbExtResponse &&
    playlistSbExtResponse.result &&
    playlistSbExtResponse.result.response &&
    playlistSbExtResponse.result.response.length
  ) {
    return playlistSbExtResponse.result.response.map(transformToPlaylistV2)
  }

  return []
}

export const playlistApi = Router()

playlistApi.get('/sync/:playlistId', async (req, res) => {
  const playlistId = req.params.playlistId
  const userId = extractUserIdFromRequest(req)
  try {
    const rootOrg = req.header('rootOrg')
    if (!rootOrg) {
      res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
      return
    }

    const response = await axios({
      method: 'GET',
      url: `${API_END_POINTS.playlistV1(userId)}/playlists/${playlistId}/sync-info`,
      ...axiosRequestConfig,
      headers: {
        rootOrg,
      },
    })

    const result = transformToSbExtSyncRequest(response.data)
    const url = `${API_END_POINTS.playlistV1(userId)}/playlists/${playlistId}/contents`

    await axios({
      ...axiosRequestConfig,
      data: result,
      headers: {
        rootOrg,
      },
      method: 'POST',
      url,
    })
    res.send(result.content)
    return
  } catch (err) {
    logError('SYNC PLAYLIST ERROR >', err)
    res
      .status((err && err.response && err.response.status) || 500)
      .send((err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      })
  }
})

playlistApi.get('/recent', async (req, res) => {
  /* get recent contents added to any playlist */
  const userId = extractUserIdFromRequest(req)
  try {
    const pageState = req.query.pageState
    const pageSize = req.query.pageSize || 50
    const isCompleted = req.query.isCompleted || false
    const sourceFields = req.query.sourceFields
    const org = req.header('org')
    const rootOrg = req.header('rootOrg')
    if (!org || !rootOrg) {
      res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
      return
    }
    const queryParams = getStringifiedQueryParams({
      isCompleted,
      pageSize,
      pageState,
      sourceFields,
    })
    const response = await axios({
      method: 'GET',
      url: `${API_END_POINTS.playlistV1(userId)}/playlist-contents?${queryParams}`,
      ...axiosRequestConfig,
      headers: {
        rootOrg,
      },
    })
    const result: IPaginatedApiResponse = {
      contents: response.data.recentContents,
      hasMore: false,
    }
    res.send(result)
  } catch (err) {
    logError('RECENT PLAYLIST CONTENTS FETCH ERROR >', err)
    res
      .status((err && err.response && err.response.status) || 500)
      .send((err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      })
  }
})

playlistApi.post('/accept/:playlistId', async (req, res) => {
  /* accept a pending playlist */
  const playlistId = req.params.playlistId
  const userId = extractUserIdFromRequest(req)
  try {
    const rootOrg = req.header('rootOrg')
    if (!rootOrg) {
      res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
      return
    }
    const playlists = await getPlaylists(userId, rootOrg)
    const playlist = playlists.find((p: IPlaylist) => p.id === playlistId)
    if (playlist) {
      const url = `${API_END_POINTS.playlistV1(userId)}/shared-playlists/${playlistId}/accept`
      const response = await axios({
        ...axiosRequestConfig,
        data: {},
        headers: {
          rootOrg,
        },
        method: 'POST',
        url,
      })

      res.status(response.status).send(true)
      return
    }

    res.status(404).send()
  } catch (err) {
    res
      .status((err && err.response && err.response.status) || 500)
      .send((err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      })
  }
})

playlistApi.post('/reject/:playlistId', async (req, res) => {
  /* axios request to reject a pending playlist */
  const playlistId = req.params.playlistId
  const userId = extractUserIdFromRequest(req)
  try {
    const rootOrg = req.header('rootOrg')
    if (!rootOrg) {
      res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
      return
    }
    const url = `${API_END_POINTS.playlistV1(userId)}/shared-playlists/${playlistId}/reject`
    const response = await axios({
      ...axiosRequestConfig,
      data: {},
      headers: {
        rootOrg,
      },
      method: 'POST',
      url,
    })
    res.status(response.status).send()
    return
  } catch (err) {
    res
      .status((err && err.response && err.response.status) || 500)
      .send((err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      })
  }
})

playlistApi.post('/share/:playlistId', async (req, res) => {
  /* post request to share a playlist with an user */
  const userId = extractUserIdFromRequest(req)
  try {
    const rootOrg = req.header('rootOrg')
    if (!rootOrg) {
      res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
      return
    }
    const request = req.body
    const playlistId = req.params.playlistId
    const response = await sharePlaylist(userId, playlistId, request, rootOrg)
    res.status(response.status).send()
  } catch (err) {
    res
      .status((err && err.response && err.response.status) || 500)
      .send((err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      })
  }
})

playlistApi.get('/:type/:playlistId', async (req, res) => {
  /* get request to fetch details of a playlist by its playlistId */

  const userId = extractUserIdFromRequest(req)
  try {
    const rootOrg = req.header('rootOrg')
    if (!rootOrg) {
      res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
      return
    }
    const { playlistId } = req.params
    const params = req.query
    const response = await axios({
      method: 'GET',
      url: `${API_END_POINTS.playlistV1(userId)}/playlists/${playlistId}`,
      ...axiosRequestConfig,
      headers: {
        rootOrg,
      },
      params,
    })
    res.status(response.status).send(transformToPlaylistV3(response.data, playlistId))
  } catch (err) {
    res
      .status((err && err.response && err.response.status) || 500)
      .send((err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      })
  }
})

playlistApi.delete('/:playlistId', async (req, res) => {
  /* axios request to delete an entire playlist */
  const userId = extractUserIdFromRequest(req)
  try {
    const rootOrg = req.header('rootOrg')
    if (!rootOrg) {
      res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
      return
    }
    const playlistId = req.params.playlistId
    const url = `${API_END_POINTS.playlistV1(userId)}/playlists/${playlistId}`
    const response = await axios({
      ...axiosRequestConfig,
      headers: {
        rootOrg,
      },
      method: 'DELETE',
      url,
    })
    res.status(response.status).send(true)
  } catch (err) {
    res
      .status((err && err.response && err.response.status) || 500)
      .send((err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      })
  }
})

playlistApi.get('/', async (req, res) => {
  /* get all playlists of an user */
  const userId = req.query.wid || extractUserIdFromRequest(req)
  const rootOrg = req.header('rootOrg')
  if (!rootOrg) {
    res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
    return
  }
  const params = req.query
  const allPlaylists = await getPlaylistsAllTypes(userId, rootOrg, params)

  if (allPlaylists.error) {
    const err = allPlaylists.error
    res
      .status((err && err.response && err.response.status) || 500)
      .send((err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      })
    return
  }
  res.send(allPlaylists.data)
})

playlistApi.patch('/:playlistId', async (req, res) => {
  /* Patch request to update the title of a playlist */
  const userId = extractUserIdFromRequest(req)
  try {
    const request: IPlaylistUpdateTitleRequest = req.body
    const rootOrg = req.header('rootOrg')
    if (!rootOrg) {
      res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
      return
    }
    const playlistId = req.params.playlistId
    const url = API_END_POINTS.playlistV1(userId)
    const response = await axios({
      ...axiosRequestConfig,
      data: transformToSbExtUpdateRequest(request),
      headers: {
        rootOrg,
      },
      method: 'PATCH',
      url: `${url}/playlists/${playlistId}`,
    })
    res.status(response.status).send()
  } catch (err) {
    res
      .status((err && err.response && err.response.status) || 500)
      .send((err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      })
  }
})

playlistApi.post('/create', async (req, res) => {
  /*Post request to create a playlist */

  const userId = extractUserIdFromRequest(req)
  try {
    const request: IPlaylistCreateRequest = req.body
    const rootOrg = req.header('rootOrg')
    if (!rootOrg) {
      res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
      return
    }
    const url = `${API_END_POINTS.playlistV1(userId)}/playlists`
    const response = await axios({
      ...axiosRequestConfig,
      data: transformToSbExtCreateRequest(request),
      headers: {
        rootOrg,
      },
      method: 'POST',
      url,
    })
    const userResponse = await getPlaylistsAllTypes(userId, rootOrg, null)
    if (userResponse.data) {
      const createdPlaylistId = userResponse.data.user[0].id
      if (createdPlaylistId && request.shareWith && request.shareWith.length) {
        const shareResponse = await sharePlaylist(
          userId,
          createdPlaylistId,
          {
            message: request.shareMsg,
            users: request.shareWith,
          },
          rootOrg
        )
        res.status(shareResponse.status).send()
      }
    }
    res.status(response.status).send()
  } catch (err) {
    res
      .status((err && err.response && err.response.status) || 500)
      .send((err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      })
  }
})

playlistApi.post('/:playlistId/:type', async (req, res) => {
  /*Post request add content or delete content from a playlist */
  const userId = extractUserIdFromRequest(req)
  try {
    const request: IPlaylistUpsertRequest = req.body
    const rootOrg = req.header('rootOrg')
    if (!rootOrg) {
      res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
      return
    }
    /* axios request to add/delete playlist content is done*/
    const type = req.params.type
    const playlistId = req.params.playlistId
    const url = `${API_END_POINTS.playlistV1(userId)}/playlists/${playlistId}/contents`
    if (type === EPlaylistUpsertTypes.add) {
      const response = await axios({
        ...axiosRequestConfig,
        data: transformToSbExtUpsertRequest(request),
        headers: {
          rootOrg,
        },
        method: 'POST',
        url,
      })
      res.status(response.status).send()

      return
    } else if (type === EPlaylistUpsertTypes.delete) {
      const response = await axios({
        ...axiosRequestConfig,
        data: transformToSbExtDeleteRequest(request),
        headers: {
          rootOrg,
        },
        method: 'DELETE',
        url,
      })
      res.status(response.status).send()
      return
    }
    res.status(500).send()
  } catch (err) {
    res
      .status((err && err.response && err.response.status) || 500)
      .send((err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      })
  }
})

playlistApi.get('/:type', async (req, res) => {
  /*get pending playlists*/
  const userId = extractUserIdFromRequest(req)
  try {
    const rootOrg = req.header('rootOrg')
    if (!rootOrg) {
      res.status(400).send(ERROR.ERROR_NO_ORG_DATA)
      return
    }
    const playlists = await getPlaylists(userId, rootOrg)
    res.send(playlists)
  } catch (err) {
    res
      .status((err && err.response && err.response.status) || 500)
      .send((err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      })
  }
})
