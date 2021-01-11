// import { AUTHORING_CONTENT_BASE } from '@ws/author/src/lib/constants/apiEndpoints'
import { Pipe, PipeTransform } from '@angular/core'

const AUTHORING_CONTENT_BASE = '/apis/authContent/'
@Pipe({
  name: 'relativeUrlTable',
})
export class RelativeUrlTablePipe implements PipeTransform {

  transform(value: string): any {
    return value ? `${AUTHORING_CONTENT_BASE}${encodeURIComponent(value)}` : ''
  }

}
