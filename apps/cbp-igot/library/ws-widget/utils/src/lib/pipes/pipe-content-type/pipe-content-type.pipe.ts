import { Pipe, PipeTransform } from '@angular/core'

@Pipe({
  name: 'pipeContentType',
})
export class PipeContentTypePipe implements PipeTransform {
  transform(value: any): string {
    if (value) {
      switch (value) {
        case 'learning path':
        case 'Learning Path':
          return 'Program'
        case 'course':
        case 'Course':
          return 'Course'
        case 'collection':
        case 'Collection':
          return 'Module'
        case 'resource':
        case 'Resource':
          return 'Resource'
        case 'certification':
        case 'Certification':
          return 'Certification'
        default:
          return value
      }
    }
    return ''
  }

}
