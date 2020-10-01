import { Component, OnInit, ElementRef, ViewChild } from '@angular/core'
import { NSDiscussData } from '../../../discuss/models/discuss.model'
import { ActivatedRoute } from '@angular/router'
import { MatSnackBar } from '@angular/material'
import { MatDialog } from '@angular/material/dialog'
import { DiscussService } from '../../../discuss/services/discuss.service'
import { FormGroup } from '@angular/forms'
/* tslint:disable */
import _ from 'lodash'
/* tslint:enable */


@Component({
  selector: 'ws-app-career-detail',
  templateUrl: './career-detail.component.html',
  styleUrls: ['./career-detail.component.scss']
})
export class CareerDetailComponent implements OnInit {
  @ViewChild('toastSuccess', { static: true }) toastSuccess!: ElementRef<any>
  @ViewChild('toastError', { static: true }) toastError!: ElementRef<any>
  postAnswerForm!: FormGroup
  data!: NSDiscussData.IDiscussionData
  similarPosts!: any
  timer: any
  defaultError = 'Something went wrong, Please try again after sometime!'
  currentFilter = 'recent'
  discussionList!: NSDiscussData.IDiscussionData[]
  topicId!: number
  fetchSingleCategoryLoader = false

  constructor(
    public dialog: MatDialog,
    private route: ActivatedRoute,
    private discussService: DiscussService,
    private snackBar: MatSnackBar,
  ) { }

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.topicId = params.topicId
      this.getTIDData()
    })
    this.data = this.route.snapshot.data.topic.data
    this.fetchSingleCategoryDetails(this.data.cid)
  }

  filter(key: string | 'recent' | 'popular' | 'watched') {
    if (key) {
      this.currentFilter = key
    }
    switch (key) {
      case 'recent':
        this.fillrecent()
        break
      case 'popular':
        this.fillPopular()
        break
      default:
        break
    }
  }
  fillrecent() {
    this.discussionList = this.route.snapshot.data.recent.data.topics || []
  }
  fillPopular() {
    // this.discussionList =;
    this.discussService.fetchPopularD().subscribe(response => {
      this.discussionList = _.get(response, 'topics')
    })
    // , () => {
    //   // IN TROUBL
    // })
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

  private openSnackbar(primaryMsg: string, duration: number = 5000) {
    this.snackBar.open(primaryMsg, 'X', {
      duration,
    })
  }

  getTIDData() {
    this.discussService.fetchTopicById(this.topicId).subscribe(
    (data: NSDiscussData.IDiscussionData) => {
      this.data = data
    },
    (err: any) => {
      this.openSnackbar(err.error.message.split('|')[1] || this.defaultError)
    })
  }

}
