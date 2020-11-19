import { Pipe, PipeTransform } from '@angular/core'

@Pipe({
  name: 'pipeListFilter',
})
export class PipeListFilterPipe implements PipeTransform {

  transform(value: any, keys: string, term: string): string {
    if (!term) {
      return value
    }
    return (value || []).filter((item: any) => keys.split(',')
      .some(key => item.hasOwnProperty(key)
        && item[key] && (`${item[key]} `).toLocaleLowerCase().includes(term.toLocaleLowerCase())))
    // new RegExp(`^${term}$`, 'gi').test(item[key])

  }

}
