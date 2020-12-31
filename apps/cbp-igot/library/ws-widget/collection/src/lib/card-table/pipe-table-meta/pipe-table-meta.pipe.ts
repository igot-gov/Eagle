import { Pipe, PipeTransform } from '@angular/core'

@Pipe({
  name: 'metaPipe',
})
export class PipeMetaPipe implements PipeTransform {
  transform(val: any, pipes: any[]) {
    var result = val
    for (var pipe of pipes) {
      result = (new pipe()).transform(result)
    }
    return result
  }
}
