
import { Component, OnInit, ViewChild, ElementRef, AfterViewInit } from '@angular/core'
import { NSDiscussData } from '../../models/discuss.model'
import { MatDialog } from '@angular/material/dialog'
import { DiscussStartComponent } from '../../components/discuss-start/discuss-start.component'
import { ActivatedRoute, Router } from '@angular/router'
import { DiscussService } from '../../services/discuss.service'
/* tslint:disable */
import _ from 'lodash'
/* tslint:enable */

@Component({
  selector: 'app-discuss-all',
  templateUrl: './discuss-all.component.html',
  styleUrls: ['./discuss-all.component.scss'],
  /* tslint:disable */
  host: { class: 'flex flex-1 margin-top-l' },
  /* tslint:enable */
})
export class DiscussAllComponent implements OnInit, AfterViewInit {
  @ViewChild('stickyMenu', { static: true }) menuElement!: ElementRef
  sticky = false
  elementPosition: any
  currentFilter = 'recent'
  trendingTags!: NSDiscussData.ITag[]
  discussionList!: NSDiscussData.IDiscussionData
  unread: any
  pager = {}
  paginationData!: any
  currentActivePage!: any
  fetchNewData = false
  constructor(
    public dialog: MatDialog,
    private route: ActivatedRoute,
    private discussService: DiscussService,
    private router: Router
  ) {
    this.trendingTags = this.route.snapshot.data.availableTags.data.tags
    this.discussionList = this.route.snapshot.data.recent.data.topics || []
    this.paginationData = this.route.snapshot.data.recent.data.pagination
    this.setPagination()
    this.unread = this.route.snapshot.data.unread
  }
  ngAfterViewInit(): void {
    // throw new Error('Method not implemented.')
  }
  ngOnInit() {
    // load page based on 'page' query param or default to 1
    this.route.queryParams.subscribe(x => {
      this.currentActivePage = x.page || 1
      this.refreshData(this.currentActivePage)
    })
  }
  start() {
    const dialogRef = this.dialog.open(DiscussStartComponent, {
      minHeight: 'auto',
      width: '80%',
      panelClass: 'remove-pad',
    })
    dialogRef.afterClosed().subscribe((response: any) => {
      if (response === 'postCreated') {
        this.fetchNewData = true
        this.refreshData(this.currentActivePage)
      }
    })
  }
  filter(key: string | 'recent' | 'popular' | 'watched') {
    if (key) {
      this.currentFilter = key
    }
    switch (key) {
      case 'recent':
        this.fillrecent(this.currentActivePage)
        break
      case 'popular':
        this.fillPopular(this.currentActivePage)
        break
      default:
        break
    }
  }
  fillrecent(_page: any) {
    // this.discussionList = this.route.snapshot.data.recent.data.topics || []
    this.getRecentData(this.currentActivePage)
  }
  fillPopular(page: any) {
    // this.discussionList =;
    this.discussService.fetchPopularD(page).subscribe((response: any) => {
      this.paginationData = response.pagination
      this.setPagination()
      this.discussionList = _.get(response, 'topics')
    })
    // , () => {
    //   // IN TROUBL
    // })
  }

  refreshData(page: any) {
    if (this.fetchNewData) {
      if (this.currentFilter === 'recent') {
        this.getRecentData(page)
      } else {
        this.fillPopular(page)
      }
    }
  }

  getRecentData(page: any) {
    return this.discussService.fetchRecentD(page).subscribe(
      (data: any) => {
        this.paginationData = data.pagination
        this.setPagination()
        this.discussionList = _.get(data, 'topics')
      })
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

  navigateWithPage(page: any) {
    if (page !== this.currentActivePage) {
      this.fetchNewData = true
      this.router.navigate([`/app/discuss/home`], { queryParams: { page } })
    }
  }
}
