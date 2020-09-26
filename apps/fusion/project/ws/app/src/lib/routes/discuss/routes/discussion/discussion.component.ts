
import { Component, OnInit, OnDestroy, ChangeDetectorRef, AfterViewInit, ViewChild, ElementRef } from '@angular/core'
import { ActivatedRoute } from '@angular/router'
import { NSDiscussData } from '../../models/discuss.model'
import { FormGroup, FormBuilder } from '@angular/forms'
import { CONTENT_BASE_STREAM } from '@ws/author/src/lib/constants/apiEndpoints'
import { LoaderService } from '../../../../../../../author/src/public-api'
import { DiscussService } from '../../services/discuss.service'
import { MatSnackBar } from '@angular/material'

@Component({
  selector: 'app-discuss-discussion',
  templateUrl: './discussion.component.html',
  styleUrls: ['./discussion.component.scss'],
  // tslint:disable-next-line
  host: { class: 'flex flex-1 margin-top-l' }
})
export class DiscussionComponent implements OnInit, OnDestroy, AfterViewInit {
  @ViewChild('toastSuccess', { static: true }) toastSuccess!: ElementRef<any>
  @ViewChild('toastError', { static: true }) toastError!: ElementRef<any>
  postAnswerForm!: FormGroup
  data!: NSDiscussData.IDiscussionData
  currentFilter = 'timestamp' //  'recent'
  location = CONTENT_BASE_STREAM
  timer: any
  defaultError = 'Something went wrong, Please try again after sometime!'
  topicId!: number
  constructor(
    private formBuilder: FormBuilder,
    private loader: LoaderService,
    private ref: ChangeDetectorRef,
    private route: ActivatedRoute,
    private discussService: DiscussService,
    private snackBar: MatSnackBar,
  ) {
  }
  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.topicId = params.topicId
    })
    this.data = this.route.snapshot.data.topic.data
    this.postAnswerForm = this.formBuilder.group({
      answer: [],
    })
  }
  ngAfterViewInit() {
    this.ref.detach()
    this.timer = setInterval(() => {
      this.ref.detectChanges()
      // tslint:disable-next-line: align
    }, 100)
  }

  ngOnDestroy() {
    this.loader.changeLoad.next(false)
    this.ref.detach()
    clearInterval(this.timer)
  }
  updatedata(meta: string, value: any, event = false) {
    this.postAnswerForm.controls[meta].setValue(value, { events: event })
    // this.contentService.setUpdatedMeta({ [meta]: value } as any, this.contentMeta.identifier)
  }

  upvote(discuss: NSDiscussData.IDiscussionData) {
    const req = {
      delta: 1,
    }
    this.processVote(discuss, req)
  }

  downvote(discuss: NSDiscussData.IDiscussionData) {
    const req = {
      delta: -1,
    }
    this.processVote(discuss, req)
  }

  bookmark(discuss: any) {
    this.discussService.bookmarkPost(discuss.pid).subscribe(
      _data => {
        this.openSnackbar('Bookmark added successfully!')
        this.refreshPostData()
      },
      (err: any) => {
        this.openSnackbar(err.error.message.split('|')[1] || this.defaultError)
      })
  }
  unBookMark(discuss: any) {
    this.discussService.deleteBookmarkPost(discuss.pid).subscribe(
      _data => {
        this.openSnackbar('Bookmark Removed successfully!')
        this.refreshPostData()
      },
      (err: any) => {
        this.openSnackbar(err.error.message.split('|')[1] || this.defaultError)
      })
  }

  delteVote(discuss: any) {
    this.discussService.deleteVotePost(discuss.pid).subscribe(
      _data => {
        this.refreshPostData()
      },
      (err: any) => {
        this.openSnackbar(err.error.message.split('|')[1] || this.defaultError)
      })
  }

  private async processVote(discuss: any, req: any) {
    if (discuss && discuss.uid) {
      this.discussService.votePost(discuss.pid, req).subscribe(
        () => {
          this.openSnackbar(this.toastSuccess.nativeElement.value)
          this.postAnswerForm.reset()
          this.refreshPostData()
        },
        (err: any) => {
          this.openSnackbar(err.error.message.split('|')[1] || this.defaultError)
        })
    }
  }

  postReply(post: NSDiscussData.IDiscussionData) {
    const req = {
      content: this.postAnswerForm.controls['answer'].value,
    }
    this.postAnswerForm.controls['answer'].setValue('')
    if (post && post.tid) {
      this.discussService.replyPost(post.tid, req).subscribe(
        () => {
          this.openSnackbar('Your reply was saved succesfuly!')
          this.refreshPostData()
        },
        (err: any) => {
          this.openSnackbar(err.error.message.split('|')[1] || this.defaultError)
        })
    }
  }

  postCommentsReply(post: NSDiscussData.IPosts, comment: string) {
    const req = {
      content: comment,
      toPid: post.pid,
    }
    if (post && post.tid) {
      this.discussService.replyPost(post.tid, req).subscribe(
        () => {
          this.openSnackbar('Your reply was saved succesfuly!')
          this.refreshPostData()
        },
        (err: any) => {
          this.openSnackbar(err.error.message.split('|')[1] || this.defaultError)
        })
    }
  }

  filter(key: string | 'timestamp' | 'upvotes') {
    if (key) {
      this.currentFilter = key
    }
  }
  showError(meta: string) {
    if (meta) {
      return true
    }
    return false
  }

  private openSnackbar(primaryMsg: string, duration: number = 5000) {
    this.snackBar.open(primaryMsg, 'X', {
      duration,
    })
  }

  refreshPostData() {
    this.discussService.fetchTopicById(this.topicId).subscribe(
      data => {
        this.data = data
      },
      (err: any) => {
        this.openSnackbar(err.error.message.split('|')[1] || this.defaultError)
      })
  }
}
