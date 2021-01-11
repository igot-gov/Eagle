import { HttpClient } from '@angular/common/http'
import { Injectable } from '@angular/core'
import { Observable, ReplaySubject, throwError } from 'rxjs'
import { first, tap } from 'rxjs/operators'
import { ConfigurationsService } from '@ws-widget/utils'
// map, mergeMap,
import { NsContent } from '../_services/widget-content.model'
import { NsPlaylist } from './btn-playlist.model'

const API_END_POINTS = {
  featureConfig: `/assets/configurations/feature/playlist.json`,
  getAllPlaylists: `/apis/protected/v8/user/playlist`,
  deletePlaylist: `/apis/protected/v8/user/playlist`,
  playlist: (type: NsPlaylist.EPlaylistTypes) => `/apis/protected/v8/user/playlist/${type}`,
  createPlaylist: `/apis/protected/v8/user/playlist`,
  upsertPlaylist: (playlistId: string) => `/apis/protected/v8/user/playlist/${playlistId}`,
  acceptPlaylist: (playlistId: string) => `/apis/protected/v8/user/playlist/accept/${playlistId}`,
  rejectPlaylist: (playlistId: string) => `/apis/protected/v8/user/playlist/reject/${playlistId}`,
  sharePlaylist: '/apis/protected/v8/user/playlist/share',
  updatePlaylists: (playlistId: string) => `/apis/protected/v8/user/playlist/${playlistId}`,
  getSearchData: `/apis/proxies/v8/sunbirdigot/search`,
  getPlaylistData: (playlistId: string) => `/apis/proxies/v8/action/content/v3/hierarchy/${playlistId}?mode=edit`,
}

@Injectable({
  providedIn: 'root',
})
export class BtnPlaylistService {
  private playlistSubject: { [key: string]: ReplaySubject<NsPlaylist.IPlaylist[]> } = {}
  isFetchingPlaylists = false
  private userName!: string
  private userId!: string
  constructor(private http: HttpClient, private configSvc: ConfigurationsService) {
    if (this.configSvc.userProfile) {
      this.userName = (`${this.configSvc.userProfile.firstName}''${this.configSvc.userProfile.lastName}`)
      this.userId = this.configSvc.userProfile.userId
    }
  }

  upsertPlaylist(playlistCreateRequest: any, updatePlaylists = true) {
    playlistCreateRequest.createdBy = this.userName
    return this.http.post<string>(`${API_END_POINTS.createPlaylist}/create`, playlistCreateRequest).pipe(
      tap(_ => {
        if (updatePlaylists) {
          this.updatePlaylists()
        }
      }),
    )
  }

  addToPlaylist(playlistId: string, playlistAddRequest: NsPlaylist.IPlaylistUpsertRequest, updatePlaylists = true) {
    return this.http.post<string>(`${API_END_POINTS.upsertPlaylist(playlistId)}/add`, playlistAddRequest).pipe(
      tap(_ => {
        if (updatePlaylists) {
          this.updatePlaylists()
        }
      }),
    )
  }

  syncPlaylist(playlistId: string, updatePlaylists = true) {
    return this.http.get<NsContent.IContent[]>(`${API_END_POINTS.upsertPlaylist}/sync/${playlistId}`).pipe(
      tap(_ => {
        if (updatePlaylists) {
          this.updatePlaylists()
        }
      }),
    )
  }

  deleteContent(playlistId: string, deleteRequest: NsPlaylist.IPlaylistUpsertRequest, updatePlaylists = true) {
    return this.http.post<string>(`${API_END_POINTS.upsertPlaylist(playlistId)}/delete`, deleteRequest).pipe(
      tap(_ => {
        if (updatePlaylists) {
          this.updatePlaylists()
        }
      }),
    )
  }

  getAllPlaylistsApi(_detailsRequired: boolean) {
    return this.getPlaylists('user')
  }

  getPlaylists(type: string) {
    // if (!this.playlistSubject[type]) {
    //   this.initSubjects()
    // }
    // this.updatePlaylists()
    // return this.playlistSubject[type].asObservable()
    const obj: any = {
      request: {
        filters: {
          primaryCategory: 'Playlist',
          visibility: 'Private',
          status: ['Draft', 'Live'],
          sharedWith: [],
        },
        fields: [],
        limit: 100,
        facets: [

        ],
      },
    }

    const sharedWith: any = obj.request.filters.sharedWith

    if (type === 'share') {
      sharedWith.push(this.userId)
    } else {
      obj.request.filters.createdBy = this.userId
    }

    return this.http
      .post(API_END_POINTS.getSearchData, obj)

  }

  getAllPlaylists() {
    return this.playlistSubject['type'].asObservable()
  }

  getPlaylist(playlistId: string): Observable<NsPlaylist.IPlaylist | null> {
    // const params = new HttpParams().set('sourceFields', sourceFields)
    return this.http
      .get<NsPlaylist.IPlaylist>(`${API_END_POINTS.getPlaylistData(playlistId)}`)
  }

  deletePlaylist(playlistId: string, type: NsPlaylist.EPlaylistTypes) {
    return this.http.delete(`${API_END_POINTS.deletePlaylist}/${playlistId}`).pipe(
      tap(() => {
        if (this.playlistSubject[type]) {
          this.playlistSubject[type].pipe(first()).subscribe((playlists: NsPlaylist.IPlaylist[]) => {
            this.playlistSubject[type].next(playlists.filter(playlist => playlist.id !== playlistId))
          })
        }
      }),
    )
  }

  patchPlaylist(playlist: NsPlaylist.IPlaylist, newIDs?: string[]) {
    const contentIds = playlist.children.map((content: { identifier: any }) => {
      const id = content.identifier
      return id
    })
    if (newIDs && newIDs.length > 0) {
      newIDs.forEach(content => {
        contentIds.push(content)
      })
    }

    return this.http.patch(`${API_END_POINTS.updatePlaylists(playlist.identifier)}`, {
      contentIds,
      playlist_title: playlist.name,
      versionKey: playlist.versionKey,
    })
  }

  addPlaylistContent(playlist: any, contentIds: string[], updatePlaylists = true) {
    return this.addToPlaylist(
      playlist[0].identifier,
      {
        contentIds,
      },
      updatePlaylists,
    )
  }

  deletePlaylistContent(playlist: any | undefined, contentIds: string[]) {
    if (playlist) {
      return this.deleteContent(
        playlist[0].identifier,
        {
          contentIds,
        },
      )
    }
    return throwError({ error: 'ERROR_PLAYLIST_UNDEFINED' })
  }

  acceptPlaylist(playlistId: string) {
    return this.http.post(API_END_POINTS.acceptPlaylist(playlistId), null).pipe()
  }

  rejectPlaylist(playlistId: string) {
    return this.http.post(API_END_POINTS.rejectPlaylist(playlistId), null).pipe(
      tap(() => {
        if (this.playlistSubject[NsPlaylist.EPlaylistTypes.PENDING]) {
          this.playlistSubject[NsPlaylist.EPlaylistTypes.PENDING].pipe(first()).subscribe((playlists: NsPlaylist.IPlaylist[]) => {
            this.playlistSubject[NsPlaylist.EPlaylistTypes.PENDING].next(
              playlists.filter(playlist => playlist.id !== playlistId),
            )
          })
        }
      }),
    )
  }

  sharePlaylist(shareRequest: any, playlistId: string) {
    return this.http.post(`${API_END_POINTS.sharePlaylist}/${playlistId}`, shareRequest)
  }

  private updatePlaylists() {
    if (this.isFetchingPlaylists) {
      return
    }
    this.isFetchingPlaylists = true
    if (!Object.entries(this.playlistSubject).length) {
      this.initSubjects()
    }
    this.http
      .get<NsPlaylist.IPlaylistResponse>(API_END_POINTS.getAllPlaylists).subscribe(
        (playlists: NsPlaylist.IPlaylistResponse) => {
          this.playlistSubject[NsPlaylist.EPlaylistTypes.ME].next(playlists.user)
          this.playlistSubject[NsPlaylist.EPlaylistTypes.SHARED].next(playlists.share)
          this.playlistSubject[NsPlaylist.EPlaylistTypes.PENDING].next(playlists.pending)
          this.isFetchingPlaylists = false
        },
        error => {
          this.playlistSubject[NsPlaylist.EPlaylistTypes.ME].error(error)
          this.playlistSubject[NsPlaylist.EPlaylistTypes.SHARED].error(error)
          this.playlistSubject[NsPlaylist.EPlaylistTypes.PENDING].error(error)
          this.isFetchingPlaylists = false
        })
  }

  private initSubjects() {
    this.playlistSubject[NsPlaylist.EPlaylistTypes.ME] = new ReplaySubject<NsPlaylist.IPlaylist[]>()
    this.playlistSubject[NsPlaylist.EPlaylistTypes.SHARED] = new ReplaySubject<NsPlaylist.IPlaylist[]>()
    this.playlistSubject[NsPlaylist.EPlaylistTypes.PENDING] = new ReplaySubject<NsPlaylist.IPlaylist[]>()
  }
}
