import { Component, OnInit, OnDestroy, AfterViewInit, EventEmitter, Input, Output } from '@angular/core'
import { AccessControlService } from '@ws/author/src/lib/modules/shared/services/access-control.service'
import { EditorContentService } from '@ws/author/src/lib/routing/modules/editor/services/editor-content.service'
import { NSContent } from '@ws/author/src/lib/interface/content'
import { NsContent } from '../../../../../../../../../../../library/ws-widget/collection/src/public-api'

@Component({
  selector: 'ws-auth-options',
  templateUrl: './options.component.html',
  styleUrls: ['./options.component.scss'],
})
export class OptionsComponent implements OnInit, OnDestroy, AfterViewInit {
  contents: NSContent.IContentMeta[] = []
  @Input() currentContent = ''
  @Output() action = new EventEmitter<string>()
  showSettingButtons = false
  constructor(
    private accessService: AccessControlService,
    private contentService: EditorContentService,
  ) { }

  ngAfterViewInit() {

  }

  ngOnInit() {

  }

  toggleSettingButtons() {
    this.showSettingButtons = !this.showSettingButtons
  }

  isPublisherSame(): boolean {
    const publisherDetails =
      this.contentService.getUpdatedMeta(this.currentContent).publisherDetails || []
    return publisherDetails.find(v => v.id === this.accessService.userId) ? true : false
  }

  isDirectPublish(): boolean {
    return (
      ['Draft', 'Live'].includes(this.contentService.originalContent[this.currentContent].status) &&
      this.isPublisherSame()
    )
  }
  getPreview() {
    return this.contentService.originalContent[this.currentContent].category === NsContent.EContentTypes.RESOURCE
  }

  getAction(): string {
    if (this.contentService.getParentUpdatedMeta().identifier === this.currentContent) {
      if (
        ((this.accessService.authoringConfig.isMultiStepFlow && this.isDirectPublish()) ||
          !this.accessService.authoringConfig.isMultiStepFlow) &&
        this.accessService.rootOrg.toLowerCase() === 'client1'
      ) {
        return 'publish'
      }
      if (this.contentService.originalContent &&
        this.contentService.originalContent[this.currentContent] &&
        this.contentService.originalContent[this.currentContent].contentType === 'Knowledge Artifact'
      ) {
        return 'publish'
      }
      switch (this.contentService.originalContent[this.currentContent].status) {
        case 'Draft':
          return 'sendForReview'
        case 'InReview':
          return 'review'
        case 'Reviewed':
          return 'publish'
        case 'Live':
          return 'sendForReview'
        default:
          return 'sendForReview'
      }
    }
    return ''
  }
  canDelete() {
    return this.accessService.hasRole(['editor', 'admin']) ||
      (['Draft', 'Live'].includes(this.contentService.originalContent[this.currentContent].status) &&
        this.contentService.originalContent[this.currentContent].creatorContacts.find(v => v.id === this.accessService.userId)
      )
  }
  ngOnDestroy() {

  }

}
