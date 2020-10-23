import { Pipe, PipeTransform } from '@angular/core'

@Pipe({
  name: 'pipeFilter',
})
export class PipeFilterPipe implements PipeTransform {

  transform(value: any, keys: string, term: string): string {
    if (!term) {
      return value
    }
    return (value || []).filter((item: any) => keys.split(',')
      .some(key => item.hasOwnProperty(key) && new RegExp(`^${term}$`, 'gi').test(item[key])))

  }

}
