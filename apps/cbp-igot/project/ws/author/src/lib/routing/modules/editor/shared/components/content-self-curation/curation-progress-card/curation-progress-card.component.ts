import { Component, Input, OnDestroy, OnInit } from '@angular/core'
/* tslint:disable */
import _ from 'lodash'
import { NSISelfCuration } from '../../../../../../../interface/self-curation'
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

}
