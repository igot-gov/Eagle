import { Injectable } from '@angular/core'

@Injectable({
  providedIn: 'root',
})
export class DiscussUtilsService {

  constructor() { }

  /*Get color Hex code by passing a string*/
  stringToColor(str: string) {
    let hash = 0
    // tslint:disable-next-line: no-increment-decrement
    for (let i = 0; i < str.length; i++) {
      // tslint:disable-next-line: no-bitwise
      hash = str.charCodeAt(i) + ((hash << 5) - hash)
    }
    let colour = '#'
    // tslint:disable-next-line: no-increment-decrement
    for (let i = 0; i < 3; i++) {
      // tslint:disable-next-line: no-bitwise
      const value = (hash >> (i * 8)) & 0xFF
      colour += (`00${value.toString(16)}`).substr(-2)
    }
    return colour
  }

  /*Get text contrast by passing background hex color*/
  getContrast(hexcolor: any) {
    const r = parseInt(hexcolor.substr(1, 2), 16)
    const g = parseInt(hexcolor.substr(3, 2), 16)
    const b = parseInt(hexcolor.substr(5, 2), 16)
    const color = ((r * 299) + (g * 587) + (b * 114)) / 1000
    return (color >= 160) ? '#000000' : '#ffffff'
  }
}
