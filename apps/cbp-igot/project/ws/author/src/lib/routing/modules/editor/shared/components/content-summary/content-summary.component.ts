import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core'
import { NSContent } from '@ws/author/src/lib/interface/content'
/* tslint:disable */
import _ from 'lodash'
/* tslint:enable */

import { IAtGlanceComponentData } from '@ws-widget/collection'
import { NSISelfCuration } from '../../../../../../interface/self-curation'
import { SelfCurationService } from '../../services/self-curation.service'
import { EditorContentService } from '../../../services/editor-content.service'
import { MyTocService } from '../../../../home/components/content-detail/services/my-toc.service'
import { ContentQualityService } from '../../services/content-quality.service'
import { NSIQuality } from '../../../../../../interface/content-quality'
import { ConfigurationsService } from '@ws-widget/utils'
@Component({
  selector: 'ws-auth-content-summary',
  templateUrl: './content-summary.component.html',
  styleUrls: ['./content-summary.component.scss'],
})
export class ContentSummaryComponent implements OnInit, OnDestroy {
  contentMeta!: NSContent.IContentMeta
  @Output() data = new EventEmitter<string>()
  @Input() isSubmitPressed = false
  @Input() nextAction = 'done'
  @Input() stage = 1
  @Input() type = ''
  @Input() parentContent: string | null = null
  contentQualityPercent = '0'
  leftmenudata!: any
  contentQualityData!: NSIQuality.IQualityResponse
  tocStructure: IAtGlanceComponentData.ICounts | null = null
  // qualityForm!: FormGroup
  currentContent!: string
  progressData: NSISelfCuration.ISelfCurationData[] = []
  constructor(
    private contentService: EditorContentService,
    private curationService: SelfCurationService,
    private myTocService: MyTocService,
    private cqs: ContentQualityService,
    private _configurationsService: ConfigurationsService,

  ) {
    this.fetchContentMeta()
  }
  ngOnInit(): void {
    // this.fetchProgress()
    this.resetAndFetchTocStructure()
  }
  fetchContentMeta() {
    this.contentService.changeActiveCont.subscribe(data => {
      this.currentContent = data
      this.contentMeta = this.contentService.getUpdatedMeta(data)
      this.contentQualityData = this.cqs.getScore(data)
      if (this.contentQualityData) {
        this.contentQualityPercent = (this.contentQualityData.finalWeightedScore).toFixed(2)
      } else if (this._configurationsService.userProfile) {
        this.contentQualityPercent = '0'
        const params = {
          getLatestRecordEnabled: true,
          resourceId: data,
          resourceType: 'content',
          userId: this._configurationsService.userProfile.userId,
        }
        this.cqs.fetchresult(params).subscribe(response => {
          if (response && _.get(response, 'result')) {
            this.contentQualityData = this.cqs.getScore(data)
            if (this.contentQualityData && this.contentQualityData.finalWeightedScore) {
              this.contentQualityPercent = this.contentQualityData.finalWeightedScore.toFixed(2)
            } else {
              this.contentQualityPercent = '0'
            }
          }
        })
      }
      this.fetchSelfCurationProgress()
    })
  }
  // getTableData(idx: number) {
  //   return _.map(this.contentQualityData.criteriaModels[idx].qualifiers, row => {
  //     return row
  //   })
  // }
  download() {
    const data = _.map(this.contentQualityData.criteriaModels, ii => ii.qualifiers)
    this.cqs.getFile({ ...data }, `Content-Quality-Report`, true)
  }
  ngOnDestroy(): void {

  }
  getFileName(path: string): string | undefined {
    if (path) {
      return _.last(path.split('/'))
    }
    return ''
  }

  resetAndFetchTocStructure() {
    this.tocStructure = {
      assessment: 0,
      course: 0,
      handsOn: 0,
      interactiveVideo: 0,
      learningModule: 0,
      other: 0,
      pdf: 0,
      podcast: 0,
      quiz: 0,
      video: 0,
      webModule: 0,
      webPage: 0,
      youtube: 0,
    }
    if (this.contentMeta) {
      this.tocStructure.learningModule = this.contentMeta.contentType === 'Collection' ? -1 : 0
      this.tocStructure.course = this.contentMeta.contentType === 'Course' ? -1 : 0
      this.tocStructure = this.myTocService.getTocStructure(this.contentMeta, this.tocStructure)
      // for (const progType in this.tocStructure) {
      //   if (this.tocStructure[progType] > 0) {
      //     break
      //   }
      // }
    }
  }
  getGlanceData(): IAtGlanceComponentData.IData | null {
    // let needToCall = true
    // _.forOwn(this.tocStructure, (k, v) => {
    //   if (v && k > 0) {
    //     false
    //   }
    // })
    // if (needToCall) {
    //   this.resetAndFetchTocStructure()
    //   setTimeout(() => { }, 1000)
    // }
    if (this.currentContent && this.contentMeta && this.tocStructure) {
      return {
        displayName: 'At a glance', // now not using JSON
        contentId: this.contentMeta.identifier,
        contentType: this.contentMeta.categoryType,
        cost: this.contentMeta.exclusiveContent ? 'Paid' : 'Free',
        duration: this.contentMeta.duration.toString(),
        lastUpdate: this.contentMeta.lastUpdatedOn,
        counts: this.tocStructure,
      }
    }
    return null
  }
  fetchSelfCurationProgress() {
    this.curationService.fetchresult(this.contentService.parentContent).subscribe(data => {
      this.progressData = data

      if (this.progressData.length > 0) {
        this.leftmenudata = [{
          count: this.getCriticalIssues,
          critical: true,
          name: 'Critical Issues',
        },
        {
          count: this.getPotentialIssues,
          potential: true,
          name: 'Potential issues',
        }]
      }
    })
  }

  get getPotentialIssues(): number {
    if (this.progressData && this.progressData.length > 0) {
      return _.chain(this.progressData).map(i => i.profanityWordList)
        .compact().flatten()
        .filter(i => i.category === 'offensive' || i.category === 'lightly offensive')
        .sumBy('no_of_occurrence').value()
    }
    return 0
  }
  get getCriticalIssues(): number {
    if (this.progressData && this.progressData.length > 0) {
      return _.chain(this.progressData).map('profanityWordList')
        .compact().flatten()
        .filter(i => i.category === 'exptermly offensive')
        .sumBy('no_of_occurrence').value()
    }
    return 0
  }
  get getCleanIssues(): number {
    if (this.progressData && this.progressData.length > 0) {
      return _.chain(this.progressData).map(i => {
        if (i.profanity_word_count === 0) {
          return i
        }
        return null
      }).compact().flatten()
        .value().length
    }
    return 0
  }
  get getProgressPercent(): number {
    if (this.progressData && this.progressData.length > 0) {
      const completed = _.chain(this.progressData).map(i => i.completed)
        .compact().flatten()
        .value().length

      return parseFloat(((completed / this.progressData.length) * 100).toFixed(2))
    }
    return 0
  }
  changeToDefaultImg($event: any) {
    $event.target.src = '/assets/instances/eagle/app_logos/default.png'
  }
}
