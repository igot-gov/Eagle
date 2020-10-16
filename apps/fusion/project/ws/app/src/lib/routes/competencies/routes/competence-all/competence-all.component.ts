
import { Component, OnInit, ViewChild, ElementRef, AfterViewInit } from '@angular/core'
import { NSCompetenciesData } from '../../models/competencies.model'
import { MatDialog } from '@angular/material/dialog'
import { ActivatedRoute } from '@angular/router'
// import { CompetenceService } from '../../services/competence.service'
/* tslint:disable */
import _ from 'lodash'
/* tslint:enable */

@Component({
  selector: 'app-competence-all',
  templateUrl: './competence-all.component.html',
  styleUrls: ['./competence-all.component.scss'],
  /* tslint:disable */
  host: { class: 'flex flex-1 margin-top-l' },
  /* tslint:enable */
})
export class CompetenceAllComponent implements OnInit, AfterViewInit {
  @ViewChild('stickyMenu', { static: true }) menuElement!: ElementRef
  sticky = false
  elementPosition: any
  currentFilter = 'recent'
  unread: any
  tabsData: NSCompetenciesData.ICompetenciesTab[]

  constructor(
    public dialog: MatDialog,
    private route: ActivatedRoute,
    //  private router: Router
  ) {
    this.tabsData = this.route.parent && this.route.parent.snapshot.data.pageData.data.tabs || []
  }
  ngOnInit() {
    // load page based on 'page' query param or default to 1
  }
  ngAfterViewInit(): void {
    // throw new Error('Method not implemented.')
  }

  updateQuery(key: string) {
    if (key) {

    }
  }
  filter(key: string | 'timestamp' | 'viewcount') {
    if (key) {
      this.currentFilter = key
      // this.refreshData(this.currentActivePage)
    }
  }

  refreshData(page: any) {
    if (page && this.currentFilter === 'recent') {
      // this.discussService.fetchSingleCategoryDetails(this.categoryId, page).subscribe(
      //   (data: any) => {
      //     this.data = data
      //     this.paginationData = data.pagination
      //     this.setPagination()
      //   },
      //   (_err: any) => {
      //   })
    } else {
      // this.discussService.fetchSingleCategoryDetailsSort(this.categoryId, 'voted', page).subscribe(
      //   (data: any) => {
      //     this.data = data
      //     this.paginationData = data.pagination
      //     this.setPagination()
      //   },
      //   (_err: any) => {
      //   })
    }
  }

  navigateWithPage() {
    // if (page !== this.currentActivePage) {
    //   this.router.navigate([`/app/careers/home`], { queryParams: { page } })
    // }
  }

  setPagination() {
    // this.pager = {
    //   startIndex: this.paginationData.first.page,
    //   endIndex: this.paginationData.last.page,
    //   // pages: Array.from(Array(this.paginationData.pageCount), (_x, index) => index + 1),
    //   pages: this.paginationData.pages,
    //   currentPage: this.paginationData.currentPage,
    //   totalPage: this.paginationData.pageCount,
    // }
  }
}
