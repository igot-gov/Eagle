
import { Component, OnInit, ViewChild, ElementRef } from '@angular/core'
import { NSCompetencie } from '../../models/competencies.model'
import { MatDialog } from '@angular/material/dialog'
import { ActivatedRoute } from '@angular/router'
import { CompetenceService } from '../../services/competence.service'
/* tslint:disable */
import _ from 'lodash'
import { FormControl } from '@angular/forms'
/* tslint:enable */

@Component({
  selector: 'app-competence-all',
  templateUrl: './competence-all.component.html',
  styleUrls: ['./competence-all.component.scss'],
  /* tslint:disable */
  host: { class: 'flex flex-1 margin-top-l' },
  /* tslint:enable */
})
export class CompetenceAllComponent implements OnInit {
  @ViewChild('stickyMenu', { static: true }) menuElement!: ElementRef
  sticky = false
  elementPosition: any
  currentFilter = 'recent'
  myCompetencies: NSCompetencie.ICompetencie[] = []
  tabsData: NSCompetencie.ICompetenciesTab[]
  allCompetencies!: NSCompetencie.ICompetencie[]
  searchJson!: NSCompetencie.ISearch[]
  searchKey: string = ''
  queryControl = new FormControl('')
  selectedId: string = ''
  constructor(
    public dialog: MatDialog,
    private route: ActivatedRoute,
    private competencySvc: CompetenceService
  ) {
    this.tabsData = this.route.parent && this.route.parent.snapshot.data.pageData.data.tabs || []
  }
  ngOnInit() {
    // load page based on 'page' query param or default to 1
    this.searchJson = [
      { type: 'COMPETENCY', field: 'name', keyword: '' },
      { type: 'COMPETENCY', field: 'status', keyword: 'VERIFIED' },
    ]

    const searchObj = {
      searches: this.searchJson,
    }
    this.competencySvc.fetchCompetency(searchObj).subscribe((reponse: NSCompetencie.ICompetencieResponse) => {
      if (reponse.statusInfo && reponse.statusInfo.statusCode === 200) {
        this.allCompetencies = reponse.responseData
      }
    })
  }
  ngAfterViewInit(): void {
    // throw new Error('Method not implemented.')
  }

  updateQuery(key: string) {
    if (key) {
      this.searchKey = key
      this.refreshData()
    }
  }

  reset() {
    this.searchKey = '
    this.queryControl.setValue('')
    this.selectedId = ''
    this.refreshData()
  }
  resetSearch() {
    this.reset()
    this.refreshData()
  }
  addCompetency(id: string) {
    if (id) {
      // API is not available
      const vc = _.chain(this.allCompetencies).filter(i => {
        return i.id === id
      }).first().value()
      this.myCompetencies.push(vc)
      this.resetcomp()
    }
  }
  resetcomp() {
    let data: any[] = []
    let allCompetencies = this.allCompetencies
    if (this.myCompetencies && this.myCompetencies.length > 0) {
      data = _.flatten(_.map(this.myCompetencies, function (item: NSCompetencie.ICompetencie) {
        return _.filter(allCompetencies, item)
      }.bind(this)))

      this.allCompetencies = this.allCompetencies.filter(function (obj) {
        return data.indexOf(obj) === -1
      })
    } else {
      // this.allCompetencies = reponse.responseData
    }
  }
  refreshData() {
    this.searchJson = [
      { type: 'COMPETENCY', field: 'name', keyword: this.searchKey },
      { type: 'COMPETENCY', field: 'status', keyword: 'VERIFIED' }
    ]

    const searchObj = {
      'searches': this.searchJson
    }
    this.competencySvc.fetchCompetency(searchObj).subscribe((reponse: NSCompetencie.ICompetencieResponse) => {
      if (reponse.statusInfo && reponse.statusInfo.statusCode === 200) {
        let data = reponse.responseData
        if (this.myCompetencies && this.myCompetencies.length > 0) {
          data = _.flatten(_.map(this.myCompetencies, function (item) {
            return _.filter(reponse.responseData, item)
          }))
          this.allCompetencies = reponse.responseData.filter(function (obj) {
            return data.indexOf(obj) === -1
          })
        } else {
          this.allCompetencies = reponse.responseData
        }
      }
    })
  }
  setSelectedCompetency(id: string) {
    this.selectedId = id
  }
}
