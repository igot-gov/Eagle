import { Component, ElementRef, Inject, TemplateRef, ViewChild, OnInit } from '@angular/core'
import { MatDialogRef, MatSnackBar, MAT_DIALOG_DATA } from '@angular/material'
import { BtnPlaylistService, NsAutoComplete, NsPlaylist } from '@ws-widget/collection'
import { TFetchStatus, ConfigurationsService } from '@ws-widget/utils'

@Component({
  selector: 'ws-app-playlist-share-dialog',
  templateUrl: './playlist-share-dialog.component.html',
  styleUrls: ['./playlist-share-dialog.component.scss'],
})
export class PlaylistShareDialogComponent implements OnInit {
  @ViewChild('shareError', { static: true }) shareErrorMessage!: ElementRef<any>
  @ViewChild('contentDeletedError', { static: true }) contentDeletedErrorMessage!: TemplateRef<any>

  users: NsAutoComplete.IUserAutoComplete[] = []
  sharePlaylistStatus: TFetchStatus = 'none'
  isSocialMediaShareEnabled = false
  constructor(
    private snackBar: MatSnackBar,
    private playlistSvc: BtnPlaylistService,
    private configSvc: ConfigurationsService,
    private dialogRef: MatDialogRef<PlaylistShareDialogComponent>,
    @Inject(MAT_DIALOG_DATA)
    public data: {
      playlist: NsPlaylist.IPlaylist
      deleted: string[]
    },
  ) { }

  ngOnInit() {
    if (this.configSvc.restrictedFeatures) {
      this.isSocialMediaShareEnabled =
        !this.configSvc.restrictedFeatures.has('socialMediaFacebookShare') ||
        !this.configSvc.restrictedFeatures.has('socialMediaLinkedinShare') ||
        !this.configSvc.restrictedFeatures.has('socialMediaTwitterShare')
    }
  }

  sharePlaylist(shareMsg: string, successToast: string) {
    if (this.data.playlist) {
      this.sharePlaylistStatus = 'fetching'
      this.playlistSvc
        .sharePlaylist(
          {
            name: this.data.playlist.result.content.name,
            versionKey: this.data.playlist.result.content.versionKey,
            message: shareMsg,
            users: this.users.map(user => user.userId),
          },
          this.data.playlist.result.content.identifier,
        )
        .subscribe(
          () => {
            this.sharePlaylistStatus = 'done'
            this.snackBar.open(successToast, 'X')
            this.dialogRef.close()
          },
          (err: any) => {
            this.sharePlaylistStatus = 'error'
            if (err.error.errorMessage !== 'content.deleted') {
              this.snackBar.open(this.shareErrorMessage.nativeElement.value, 'X')
            } else {
              this.snackBar.openFromTemplate(this.contentDeletedErrorMessage)
            }
          },
        )
    }
  }

  updateUsers(users: NsAutoComplete.IUserAutoComplete[]) {
    if (Array.isArray(users)) {
      this.users = users
    }
  }
}
