import { Component, Input, OnInit } from '@angular/core'
import { NsContent, NsDiscussionForum } from '@ws-widget/collection'
import { NsWidgetResolver } from '@ws-widget/resolver'
import { ActivatedRoute } from '@angular/router'
import { ConfigurationsService } from '../../../../../../../library/ws-widget/utils/src/public-api'

@Component({
  selector: 'viewer-audio-native-container',
  templateUrl: './audio-native.component.html',
  styleUrls: ['./audio-native.component.scss'],
})
export class AudioNativeComponent implements OnInit {
  @Input() isScreenSizeSmall = false
  @Input() forPreview = false
  @Input() isFetchingDataComplete = false
  @Input() audioData: NsContent.IContent | null = null
  @Input() discussionForumWidget: NsWidgetResolver.IRenderConfigWithTypedData<
    NsDiscussionForum.IDiscussionForumInput
  > | null = null
  @Input() defaultThumbnail = ''
  @Input() isPreviewMode = false
  isTypeOfCollection = false
  isRestricted = false

  constructor(private activatedRoute: ActivatedRoute, private configSvc: ConfigurationsService) { }

  ngOnInit() {
    if (this.configSvc.restrictedFeatures) {
      this.isRestricted =
        !this.configSvc.restrictedFeatures.has('disscussionForum')
    }
    this.isTypeOfCollection = this.activatedRoute.snapshot.queryParams.collectionType ? true : false
  }
}
