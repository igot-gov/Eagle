
import { Component, OnInit, ViewChild, ElementRef } from '@angular/core'
import { NSCompetencie } from '../../models/competencies.model'
import { MatDialog } from '@angular/material/dialog'
import { ActivatedRoute } from '@angular/router'
import { CompetenceService } from '../../services/competence.service'
/* tslint:disable */
import _ from 'lodash'
import { FormControl } from '@angular/forms'
import { CompetenceViewComponent } from '../../components/competencies-view/competencies-view.component'
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
  filteredCompetencies!: NSCompetencie.ICompetencie[]
  searchJson!: NSCompetencie.ISearch[]
  searchKey = ''
  queryControl = new FormControl('')
  selectedId = ''
  currentProfile: any
  constructor(
    public dialog: MatDialog,
    private route: ActivatedRoute,
    private competencySvc: CompetenceService
  ) {
    this.tabsData = this.route.parent && this.route.parent.snapshot.data.pageData.data.tabs || []
    if (this.route.snapshot.data &&
      this.route.snapshot.data.profile &&
      this.route.snapshot.data.profile.data &&
      this.route.snapshot.data.profile.data[0]
    ) {
      if (this.route.snapshot.data.profile.data[0].competencies) {
        this.myCompetencies = this.route.snapshot.data.profile.data[0].competencies
      }
      this.currentProfile = this.route.snapshot.data.profile.data[0]
    }
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
        this.resetcomp()
      }
    })
  }

  updateQuery(key: string) {
    if (key) {
      this.searchKey = key
      this.refreshData()
    }
  }

  reset() {
    this.searchKey = ''
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
      this.addToProfile(vc)
      this.resetcomp()
    }
  }
  deleteCompetency(id: string) {
    if (id) {
      // API is not available
      // const vc = _.chain(this.allCompetencies).filter(i => {
      //   return i.id === id
      // }).first().value()
      const vc = _.remove(this.myCompetencies, itm => _.get(itm, "id") === id)
      // this.myCompetencies.push(vc)
      if (vc && vc[0]) {
        this.removeFromProfile(vc[0])
        this.resetcomp()
      }
    }
  }
  addToProfile(item: NSCompetencie.ICompetencie) {
    if (item) {
      const newCompetence = {
        type: item.type || 'COMPETENCY',
        id: item.id,
        name: item.name || '',
        description: item.description || '',
        status: item.status || '',
        source: item.source || '',
        competencyType: item.additionalProperties.competencyType,
      }
      const updatedProfile = { ...this.currentProfile }
      if (_.get(this, 'currentProfile.competencies')) {
        _.remove(updatedProfile.competencies, itm => _.get(itm, "id") === item.id)
        updatedProfile.competencies.push(newCompetence)
      } else {
        updatedProfile.competencies = []
        updatedProfile.competencies.push(newCompetence)
      }
      this.competencySvc.updateProfile(updatedProfile).subscribe(response => {
        if (response) {
          // success
          // this.myCompetencies.push(item)

        }
      })
    }
  }
  removeFromProfile(item: NSCompetencie.ICompetencie) {
    if (item) {
      let currentCompetencies = _.get(this, 'currentProfile.competencies')
      const updatedProfile = { ...this.currentProfile }
      _.remove(currentCompetencies, itm => _.get(itm, "id") === item.id)
      if (updatedProfile) {
        updatedProfile.competencies = currentCompetencies
      }
      this.competencySvc.updateProfile(updatedProfile).subscribe(response => {
        if (response) {
          // success => removed
        }
      })
    }
  }
  resetcomp() {
    let data: any[] = []
    const allCompetencies = [...this.allCompetencies]
    if (this.myCompetencies && this.myCompetencies.length > 0) {

      data = _.flatten(_.map(this.myCompetencies, (item: NSCompetencie.ICompetencie) => _.filter(allCompetencies, (i: NSCompetencie.ICompetencie) => i.id === item.id)))

      this.filteredCompetencies = this.allCompetencies.filter(obj => {
        return data.indexOf(obj) === -1
      })
      // this.filteredCompetencies = data
    } else {
      this.filteredCompetencies = allCompetencies
    }
  }
  refreshData() {
    this.searchJson = [
      { type: 'COMPETENCY', field: 'name', keyword: this.searchKey },
      { type: 'COMPETENCY', field: 'status', keyword: 'VERIFIED' },
    ]
    const searchObj = {
      searches: this.searchJson,
    }
    this.competencySvc.fetchCompetency(searchObj).subscribe((reponse: NSCompetencie.ICompetencieResponse) => {
      if (reponse.statusInfo && reponse.statusInfo.statusCode === 200) {
        let data = reponse.responseData
        if (this.myCompetencies && this.myCompetencies.length > 0) {
          data = _.flatten(_.map(this.myCompetencies, item => {
            return _.filter(reponse.responseData, item)
          }))
          this.allCompetencies = reponse.responseData.filter(obj => {
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

  view(item?: NSCompetencie.ICompetencie) {
    const dialogRef = this.dialog.open(CompetenceViewComponent, {
      minHeight: 'auto',
      // width: '80%',
      panelClass: 'remove-pad',
      data: item,
    })
    let instance = dialogRef.componentInstance
    instance.isUpdate = true
    dialogRef.afterClosed().subscribe((response: any) => {
      if (response && response.action === 'ADD') {
        this.addCompetency(response.id)
        // this.refreshData(this.currentActivePage)
      } else if (response && response.action === 'DELETE') {
        this.deleteCompetency(response.id)
      }
    })
  }
}
