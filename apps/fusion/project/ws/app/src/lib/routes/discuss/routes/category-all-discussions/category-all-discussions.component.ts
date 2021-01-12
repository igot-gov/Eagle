import { Component, OnInit } from '@angular/core'
import { NSDiscussData } from '../../models/discuss.model'
import { Router, ActivatedRoute } from '@angular/router'
import { DiscussService } from '../../services/discuss.service'
import { MatSnackBar } from '@angular/material'
/* tslint:disable */
import _ from 'lodash'
/* tslint:enable */

@Component({
  selector: 'ws-app-category-all-discussions',
  templateUrl: './category-all-discussions.component.html',
  styleUrls: ['./category-all-discussions.component.scss'],
})
export class CategoryAllDiscussionsComponent implements OnInit {
  categoryData!: any
  similarPosts!: any
  fetchSingleCategoryLoader = false
  defaultError = 'Something went wrong, Please try again after sometime!'
  queryParam: any
  pager = {}
  paginationData!: any
  currentActivePage!: any
  fetchNewData = false

  constructor(
      private snackBar: MatSnackBar,
      private discussService: DiscussService,
      private route: ActivatedRoute,
      private router: Router) {
  }

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      this.queryParam = params['cid']
      this.currentActivePage = params.page || 1
      this.refreshData(this.queryParam, this.currentActivePage)
    })
    this.fetchSingleCategoryDetails(this.queryParam, this.currentActivePage)
  }

  fetchSingleCategoryDetails(cid: number, page: any) {
    this.fetchSingleCategoryLoader = true
    this.discussService.fetchSingleCategoryDetails(cid, page).subscribe(
      (data: NSDiscussData.ICategoryData) => {
        this.categoryData = data.name
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

  getNextData(cid: any, page: any) {
    return this.discussService.fetchNextD(cid, page).subscribe(
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
      this.router.navigate([`/app/discuss/categories/category-discussions`], { queryParams: { page, cid : this.queryParam } })
    }
  }

  refreshData(cid: any, page: any) {
    if (this.fetchNewData) {
      this.getNextData(cid, page)
    }
  }

}
