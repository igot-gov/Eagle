import { Component, ViewChild, ElementRef } from '@angular/core'
import { NsPlaylist, BtnPlaylistService, NsContent } from '@ws-widget/collection'
import { TFetchStatus, NsPage, ConfigurationsService } from '@ws-widget/utils'
import { ActivatedRoute, Router } from '@angular/router'
import { MatSnackBar } from '@angular/material'

@Component({
  selector: 'ws-app-playlist-edit',
  templateUrl: './playlist-edit.component.html',
  styleUrls: ['./playlist-edit.component.scss'],
})
export class PlaylistEditComponent {

  @ViewChild('editPlaylistError', { static: true }) editPlaylistErrorMessage!: ElementRef<any>

  playlist: NsPlaylist.IPlaylist = this.route.snapshot.data.playlist.data
  error = this.route.snapshot.data.playlist.error
  type = this.route.snapshot.data.type
  upsertPlaylistStatus: TFetchStatus = 'none'

  selectedContentIds = new Set<string>()
  changedContentIds = new Set<string>()
  pageNavbar: Partial<NsPage.INavBackground> = this.configurationSvc.pageNavBar

  constructor(
    private route: ActivatedRoute,
    public router: Router,
    private playlistSvc: BtnPlaylistService,
    private snackBar: MatSnackBar,
    private configurationSvc: ConfigurationsService,
  ) {
    this.selectedContentIds = new Set<string>(
      (this.playlist && this.playlist.contents || []).map(content => content.identifier),
    )
  }

  contentChanged(content: Partial<NsContent.IContent>, checked: boolean) {
    if (content && content.identifier) {
      checked ? this.changedContentIds.add(content.identifier) : this.changedContentIds.delete(content.identifier)
    }
  }

  editPlaylist() {
    this.upsertPlaylistStatus = 'fetching'
    this.playlistSvc.addPlaylistContent(this.playlist, Array.from(this.changedContentIds)).subscribe(
      () => {
        this.router.navigate([this.router.url.replace('/edit', '')])
      },
      () => {
        this.upsertPlaylistStatus = 'error'
        this.snackBar.open(this.editPlaylistErrorMessage.nativeElement.value)
      },
    )
  }
}
