import { Component, OnInit, ElementRef, ViewChild } from '@angular/core'
import { NSDiscussData } from '../../../discuss/models/discuss.model'
import { ActivatedRoute } from '@angular/router'
import { MatSnackBar } from '@angular/material'
import { MatDialog } from '@angular/material/dialog'
import { DiscussService } from '../../../discuss/services/discuss.service'
/* tslint:disable */
import _ from 'lodash'
/* tslint:enable */

@Component({
  selector: 'ws-app-career-detail',
  templateUrl: './career-detail.component.html',
  styleUrls: ['./career-detail.component.scss'],
})
export class CareerDetailComponent implements OnInit {
  @ViewChild('toastSuccess', { static: true }) toastSuccess!: ElementRef<any>
  @ViewChild('toastError', { static: true }) toastError!: ElementRef<any>
  data!: NSDiscussData.IDiscussionData
  similarPosts!: any
  defaultError = 'Something went wrong, Please try again after sometime!'
  topicId!: number
  fetchSingleCategoryLoader = false
  // fetchNewData = false

  constructor(
    public dialog: MatDialog,
    private route: ActivatedRoute,
    private discussService: DiscussService,
    private snackBar: MatSnackBar,
  ) { }

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.topicId = params.topicId
      // if (this.fetchNewData) {
      //   this.getTIDData()
      // }
      this.data = this.route.snapshot.data.topic.data
    })
    this.fetchSingleCategoryDetails(this.data.cid)
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

  // getTIDData() {
  //   this.fetchNewData = true
  //   this.discussService.fetchTopicById(this.topicId).subscribe(
  //     (data: NSDiscussData.IDiscussionData) => {
  //       this.data = data
  //     },
  //     (err: any) => {
  //       this.openSnackbar(err.error.message.split('|')[1] || this.defaultError)
  //     })
  // }

}
