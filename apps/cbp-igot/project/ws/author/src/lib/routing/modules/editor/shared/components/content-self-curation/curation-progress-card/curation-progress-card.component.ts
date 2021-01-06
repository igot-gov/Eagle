import { Component, Input, OnDestroy, OnInit } from '@angular/core'
import { NSContent } from '@ws/author/src/lib/interface/content'
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
  @Input() contentMeta!: NSContent.IContentMeta
  @Input() parentName = 'CBP'
  @Input() parentId = ''
  progressData!: NSISelfCuration.ISelfCurationData[]
  constructor(
    // private curationService: SelfCurationService

  ) {
  }
  ngOnInit(): void {
    this.fetchProgress()
  }

  ngOnDestroy(): void {

  }
  get getFileName(): string | undefined {
    if (this.contentMeta.artifactUrl) {
      return _.last(this.contentMeta.artifactUrl.split('/'))
    }
    return ''
  }
  fetchProgress() {
    // const data = {
    //   contentId: this.contentMeta.identifier,
    //   fileName: this.getFileName,
    // }
    // this.curationService.fetchresult(data).subscribe(result => {
    //   this.progressData = result
    // })
  }
}
