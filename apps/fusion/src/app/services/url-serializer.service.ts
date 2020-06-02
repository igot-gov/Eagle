import { Injectable } from '@angular/core'
import { DefaultUrlSerializer, UrlTree } from '@angular/router'

@Injectable({
  providedIn: 'root',
})
export class UrlSerializerService extends DefaultUrlSerializer {
  // constructor() {}
  parse(url: string): UrlTree {
    return super.parse(url.toLowerCase())
  }
}
