import { Pipe, PipeTransform } from '@angular/core'
// commercial_begin
import { ConfigurationsService, EInstance } from '@ws-widget/utils'
// commercial_end
import { NsContent } from '../../_services/widget-content.model'
import { viewerRouteGenerator } from '../../_services/viewer-route-util'
@Pipe({
  name: 'pipeContentRoute',
})
export class PipeContentRoutePipe implements PipeTransform {
  constructor(private configSvc: ConfigurationsService) {}

  transform(
    content: NsContent.IContent,
    forPreview = false,
  ): { url: string; queryParams: { [key: string]: any } } {
    // commercial_begin
    const location = forPreview ? '/author' : '/app'
    if (
      this.configSvc.rootOrg === EInstance.PATHFINDERS &&
      content.contentType === NsContent.EContentTypes.KNOWLEDGE_ARTIFACT
    ) {
      return {
        url: forPreview
          ? `${location}/toc/${content.identifier}/overview`
          : `${location}/toc/knowledge-artifact/${content.identifier}`,
        queryParams: this.getQueryParams(),
      }
    }
    // commercial_end
    if (content.contentType === 'Knowledge Board') {
      return {
        url: forPreview
          ? `${location}/toc/${content.identifier}/overview`
          : `${location}/knowledge-board/${content.identifier}`,
        queryParams: this.getQueryParams(),
      }
    }
    if (content.contentType === 'Channel') {
      return {
        url: `/${forPreview ? 'author/viewer/channel' : 'page'}/${content.identifier}`,
        queryParams: this.getQueryParams(),
      }
    }
    if (content.contentType === 'Learning Journeys') {
      if (content.resourceType === 'Dynamic Learning Paths') {
        return {
          url: `/app/learning-journey/dlp/${content.identifier}/0`,
          queryParams: this.getQueryParams(),
        }
      }
      return {
        url: `/app/learning-journey/cdp/${content.identifier}`,
        queryParams: this.getQueryParams(),
      }
    }
    if (content.continueLearningData
    && content.continueLearningData.contextType === 'playlist'
      && content.continueLearningData.contextPathId) {
        const generatedUrl = viewerRouteGenerator(
          content.identifier,
          content.mimeType,
          content.continueLearningData.contextPathId,
          'Playlist'
        )
      return generatedUrl
    }
    return {
      url: `${location}/toc/${content.identifier}/overview`,
      queryParams: this.getQueryParams(),
    }
  }

  private getQueryParams() {
    return {}
  }
}
