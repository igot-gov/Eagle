import { Component, OnInit, Inject } from '@angular/core'
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material'
import { WsDiscussionForumService, NsDiscussionForum } from '@ws-widget/collection/src/public-api'

@Component({
  selector: 'ws-app-confirm-publish',
  templateUrl: './confirm-publish.component.html',
  styleUrls: ['./confirm-publish.component.scss'],
})
export class ConfirmPublishComponent implements OnInit {
  isPublishing = false

  constructor(
    public dialogRef: MatDialogRef<ConfirmPublishComponent>,
    private discussionSvc: WsDiscussionForumService,
    @Inject(MAT_DIALOG_DATA)
    public data: { postPublishRequest: NsDiscussionForum.IPostPublishRequest },
  ) {}

  ngOnInit() {}

  publishPost() {
    this.isPublishing = true
    this.discussionSvc.publishPost(this.data.postPublishRequest).subscribe(
      data => {
        this.dialogRef.close(data)
        this.isPublishing = false
      },
      () => {
        this.dialogRef.close('error')
        this.isPublishing = false
      },
    )
  }
}
