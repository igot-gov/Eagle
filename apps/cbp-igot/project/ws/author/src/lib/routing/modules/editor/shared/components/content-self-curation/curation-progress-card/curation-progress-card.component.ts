import { Component, Input, OnDestroy, OnInit } from '@angular/core'
import { MatDialog } from '@angular/material'
/* tslint:disable */
import _ from 'lodash'
import { NSISelfCuration } from '../../../../../../../interface/self-curation'
import { CurationDetailComponent } from '../curation-detail/curation-detail.component'
// import { SelfCurationService } from '../../../services/self-curation.service'
/* tslint:enable */
@Component({
  selector: 'ws-auth-curation-progress-card',
  templateUrl: './curation-progress-card.component.html',
  styleUrls: ['./curation-progress-card.component.scss'],
})
export class CurationProgressCardComponent implements OnInit, OnDestroy {
  @Input() parentName = 'CBP'
  @Input() parentId = ''
  @Input() resourseName = ''
  @Input() progressData!: NSISelfCuration.ISelfCurationData
  constructor(
    // private curationService: SelfCurationService
    public dialog: MatDialog,
  ) {
  }
  ngOnInit(): void {
  }

  ngOnDestroy(): void {

  }
  get getPotentialIssues(): number {
    if (this.progressData && this.progressData.profanity_word_count > 0) {
      return _.chain(this.progressData).get('profanityWordList')
        .filter(i => i.category === 'offensive' || i.category === 'lightly offensive')
        .sumBy('no_of_occurrence').value()
    }
    return 0
  }
  get getCriticalIssues(): number {
    if (this.progressData && this.progressData.profanity_word_count > 0) {
      return _.chain(this.progressData).get('profanityWordList')
        .filter(i => i.category === 'exptermly offensive')
        .sumBy('no_of_occurrence').value()
    }
    return 0
  }

  get getFileName(): string | undefined {
    if (this.progressData.primaryKey && this.progressData.primaryKey.pdfFileName) {
      return _.get(this.progressData, 'primaryKey.pdfFileName')
    }
    return ''
  }
  displayResult() {
    const dialogRef = this.dialog.open(CurationDetailComponent, {
      // minHeight: 'auto',
      width: '80%',
      maxHeight: '90vh',
      panelClass: 'add-border',
      data: {
        parentName: this.parentName,
        parentId: this.parentId,
        resourseName: this.resourseName,
        progressData: { ...this.progressData },
      },
    })
    dialogRef.afterClosed().subscribe((response: any) => {
      if (response === 'postCreated') {
        // this.refreshData(this.currentActivePage)
      }
    })

  }
}
