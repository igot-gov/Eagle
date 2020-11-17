
import { Component, OnInit, OnDestroy, ChangeDetectorRef, AfterViewInit, ViewChild, ElementRef } from '@angular/core'
import { ActivatedRoute, Router } from '@angular/router'
import { NSDiscussData } from '../../models/discuss.model'
import { FormGroup, FormBuilder } from '@angular/forms'
// import { CONTENT_BASE_STREAM } from '@ws/author/src/lib/constants/apiEndpoints'
// import { LoaderService } from '../../../../../../../author/src/public-api'
import { DiscussService } from '../../services/discuss.service'
import { MatSnackBar } from '@angular/material'
import { DiscussUtilsService } from '../../services/discuss-utils.service'

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
  similarPosts!: any
  currentFilter = 'timestamp' //  'recent'
  location = 'CONTENT_BASE_STREAM'
  timer: any
  defaultError = 'Something went wrong, Please try again after sometime!'
  topicId!: number
  fetchSingleCategoryLoader = false
  pager = {}
  paginationData!: any
  currentActivePage!: any
  fetchNewData = false
  constructor(
    private formBuilder: FormBuilder,
    // private loader: LoaderService,
    private ref: ChangeDetectorRef,
    private route: ActivatedRoute,
    private discussService: DiscussService,
    private snackBar: MatSnackBar,
    private discussUtils: DiscussUtilsService,
    private router: Router
  ) {
  }
  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.data = this.route.snapshot.data.topic.data
      this.paginationData = this.route.snapshot.data.topic.data.pagination
      this.setPagination()
      this.topicId = params.topicId
      if (this.data.posts && this.data.posts.length && this.data.posts[0].tid !== Number(this.topicId)) {
        this.getTIDData(this.currentActivePage)
      }
    })
    this.route.queryParams.subscribe(x => {
      if (x.page) {
        this.currentActivePage = x.page || 1
        this.refreshPostData(this.currentActivePage)
      }
    })
    this.postAnswerForm = this.formBuilder.group({
      answer: [],
    })
    this.fetchSingleCategoryDetails(this.data.cid)
  }
  ngAfterViewInit() {
    this.ref.detach()
    this.timer = setInterval(() => {
      this.ref.detectChanges()
      // tslint:disable-next-line: align
    }, 100)
  }

  ngOnDestroy() {
    // this.loader.changeLoad.next(false)
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
    this.fetchNewData = true
    this.processVote(discuss, req)
  }

  downvote(discuss: NSDiscussData.IDiscussionData) {
    const req = {
      delta: -1,
    }
    this.fetchNewData = true
    this.processVote(discuss, req)
  }

  bookmark(discuss: any) {
    this.discussService.bookmarkPost(discuss.pid).subscribe(
      _data => {
        this.openSnackbar('Bookmark added successfully!')
        this.fetchNewData = true
        this.refreshPostData(this.currentActivePage)
      },
      (err: any) => {
        this.openSnackbar(err.error.message.split('|')[1] || this.defaultError)
      })
  }
  unBookMark(discuss: any) {
    this.discussService.deleteBookmarkPost(discuss.pid).subscribe(
      _data => {
        this.openSnackbar('Bookmark Removed successfully!')
        this.fetchNewData = true
        this.refreshPostData(this.currentActivePage)
      },
      (err: any) => {
        this.openSnackbar(err.error.message.split('|')[1] || this.defaultError)
      })
  }

  delteVote(discuss: any) {
    this.discussService.deleteVotePost(discuss.pid).subscribe(
      _data => {
        this.fetchNewData = true
        this.refreshPostData(this.currentActivePage)
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
          this.refreshPostData(this.currentActivePage)
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
          this.fetchNewData = true
          this.refreshPostData(this.currentActivePage)
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
          this.refreshPostData(this.currentActivePage)
        },
        (err: any) => {
          this.openSnackbar(err.error.message.split('|')[1] || this.defaultError)
        })
    }
  }

  filter(key: string | 'timestamp' | 'upvotes') {
    if (key) {
      this.currentFilter = key
      this.refreshPostData(this.currentActivePage)
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

  refreshPostData(page: any) {
    if (this.currentFilter === 'timestamp') {
      this.discussService.fetchTopicById(this.topicId, page).subscribe(
        (data: NSDiscussData.IDiscussionData) => {
          this.data = data
          this.paginationData = data.pagination
          this.setPagination()
        },
        (err: any) => {
          this.openSnackbar(err.error.message.split('|')[1] || this.defaultError)
        })
    } else {
      this.discussService.fetchTopicByIdSort(this.topicId, 'voted', page).subscribe(
        (data: NSDiscussData.IDiscussionData) => {
          this.data = data
          this.paginationData = data.pagination
          this.setPagination()
        },
        (err: any) => {
          this.openSnackbar(err.error.message.split('|')[1] || this.defaultError)
        })
    }
  }

  fetchSingleCategoryDetails(cid: number) {
    this.fetchSingleCategoryLoader = true
    this.discussService.fetchSingleCategoryDetails(cid).subscribe(
      (data: NSDiscussData.ICategoryData) => {
        this.similarPosts = data.topics
        this.fetchSingleCategoryLoader = false
      },
      (err: any) => {
        this.openSnackbar(err.error.message.split('|')[1] || this.defaultError)
        this.fetchSingleCategoryLoader = false
      })
  }

  getTIDData(page: any) {
    this.discussService.fetchTopicById(this.topicId, page).subscribe(
      (data: NSDiscussData.IDiscussionData) => {
        this.data = data
        this.paginationData = data.pagination
        this.setPagination()
      },
      (err: any) => {
        this.openSnackbar(err.error.message.split('|')[1] || this.defaultError)
      })
  }

  public getBgColor(tagTitle: any) {
    const bgColor = this.discussUtils.stringToColor(tagTitle.toLowerCase())
    const color = this.discussUtils.getContrast(bgColor)
    return { color, 'background-color': bgColor }
  }

  navigateWithPage(page: any) {
    if (page !== this.currentActivePage) {
      this.router.navigate([`/app/discuss/home/${this.topicId}`], { queryParams: { page } })
      this.fetchNewData = true
    }
  }

  setPagination() {
    this.pager = {
      startIndex: this.paginationData.first.page,
      endIndex: this.paginationData.last.page,
      // pages: Array.from(Array(this.paginationData.pageCount), (_x, index) => index + 1),
      pages: this.paginationData.pages,
      currentPage: this.paginationData.currentPage,
      totalPage: this.paginationData.pageCount,
    }
  }
}
