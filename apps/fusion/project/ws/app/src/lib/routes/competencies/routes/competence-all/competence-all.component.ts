
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
  ngAfterViewInit(): void {
    // throw new Error('Method not implemented.')
  }
  ngOnInit() {
    // load page based on 'page' query param or default to 1
  }
}
