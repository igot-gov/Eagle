import { Pipe, PipeTransform } from '@angular/core'
import * as _ from 'lodash'

@Pipe({
  name: 'listPipe',
})
export class PipeListPipe implements PipeTransform {

  transform(value: any, args?: any): any {
    // return (value || []).map(i => ' '+i[args]); older
    return _.chain(value).map(i => i[args]).orderBy('asc').join('<br />').value()
  }
}
