import { Component, OnInit } from '@angular/core'
import { NSDiscussData } from '../../models/discuss.model'
import { Router, ActivatedRoute } from '@angular/router'
import { DiscussService } from '../../services/discuss.service'
import { DiscussUtilsService } from '../../services/discuss-utils.service'
import { MatSnackBar } from '@angular/material'
/* tslint:disable */
import _ from 'lodash'
/* tslint:enable */
@Component({
  selector: 'ws-app-tag-all-discussions',
  templateUrl: './tag-all-discussions.component.html',
  styleUrls: ['./tag-all-discussions.component.scss'],
})
export class TagAllDiscussionsComponent implements OnInit {
  tagName!: any
  similarPosts!: any
  queryParam: any
  fetchSingleCategoryLoader = false
  currentActivePage!: any
  defaultError = 'Something went wrong, Please try again after sometime!'
  pager = {}
  paginationData!: any
  fetchNewData = false

  constructor(
      private snackBar: MatSnackBar,
      private route: ActivatedRoute,
      private router: Router,
      private discussService: DiscussService,
      private discussUtils: DiscussUtilsService) { }

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      this.queryParam = params['tagname']
      this.tagName = this.queryParam
      this.currentActivePage = params.page || 1
      this.refreshData(this.queryParam, this.currentActivePage)
    })
    this.fetchSingleTagDetails(this.queryParam, this.currentActivePage)
  }

  fetchSingleTagDetails(tagname: string, page: any) {
    this.fetchSingleCategoryLoader = true
    this.discussService.fetchSingleTagDetails(tagname, page).subscribe(
      (data: NSDiscussData.IDiscussionData) => {
        this.similarPosts = data.topics
        this.paginationData = data.pagination
        this.fetchSingleCategoryLoader = false
        this.setPagination()
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

  // for pagination
  getNextData(tagname: string, page: any) {
    return this.discussService.fetchNextTagD(tagname, page).subscribe(
      (data: any) => {
        this.paginationData = data.pagination
        this.setPagination()
        this.similarPosts = _.get(data, 'topics')
      })
  }

  setPagination() {
    this.pager = {
      startIndex: this.paginationData.first.page,
      endIndex: this.paginationData.last.page,
      pages: this.paginationData.pages,
      currentPage: this.paginationData.currentPage,
      totalPage: this.paginationData.pageCount,
    }
  }

  navigateWithPage(page: any) {
    if (page !== this.currentActivePage) {
      this.fetchNewData = true
      this.router.navigate([`/app/discuss/tags/tag-discussions`], { queryParams: { page, tagname : this.queryParam } })
    }
  }

  refreshData(tagname: string, page: any) {
    if (this.fetchNewData) {
      this.getNextData(tagname, page)
    }
  }

  // for tag color
  public getBgColor(tagTitle: any) {
    const bgColor = this.discussUtils.stringToColor(tagTitle.toLowerCase())
    const color = this.discussUtils.getContrast(bgColor)
    return { color, 'background-color': bgColor }
  }
}
