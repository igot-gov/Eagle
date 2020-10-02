import { Pipe, PipeTransform } from '@angular/core'

@Pipe({
  name: 'pipeFilterSearch',
})
export class PipeFilterSearchPipe implements PipeTransform {

  transform(items: any[], searchText: string): any[] {
    if (!items) { return [] }
    if (!searchText) { return items }
    const searchTextLowerCase = searchText.toLowerCase()
    return items.filter(it => {
      return it.title.toLowerCase().includes(searchTextLowerCase)
    })
  }

}
