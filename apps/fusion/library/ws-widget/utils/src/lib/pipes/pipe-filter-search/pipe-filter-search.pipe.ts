import { Pipe, PipeTransform } from '@angular/core'

@Pipe({
  name: 'pipeFilterSearch',
})
export class PipeFilterSearchPipe implements PipeTransform {

  transform(items: any[], searchText: string, ...keys: any): any[] {
    if (!items) { return [] }
    if (!searchText) { return items }
    const searchTextLowerCase = searchText.toLowerCase()
    if (keys && keys.length) {
      return items.filter(it => {
        return it['personalDetails'][keys[0]].toLowerCase().includes(searchTextLowerCase)
        // keys.map((key: any) => {
        //   console.log('key:', key)
        //   console.log(`it['personalDetails'][key]`, it['personalDetails'][key])
        //   return it['personalDetails'][key].toLowerCase().includes(searchTextLowerCase)
        // })
      })
    }
    return items.filter(it => {
      return it.title.toLowerCase().includes(searchTextLowerCase)
    })
  }

}
