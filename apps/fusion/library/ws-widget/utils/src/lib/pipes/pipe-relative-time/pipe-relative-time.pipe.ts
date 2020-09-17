import { Pipe, PipeTransform } from '@angular/core'
import moment from 'moment'

@Pipe({
  name: 'pipeRelativeTime',
})
export class PipeRelativeTimePipe implements PipeTransform {
  transform(value: number): string {
    if (value) {
      return moment(value).fromNow()
    }
    return moment().startOf('hour').fromNow()
  }
}
