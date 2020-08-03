import { Injectable, Type } from '@angular/core'
import { AppTocSinglePageComponent } from '../../components/app-toc-single-page/app-toc-single-page.component'

@Injectable({
  providedIn: 'root',
})

export class AppTocSinglePageService {

  constructor(
  ) { }

  getComponent(): Type<any> {
    return AppTocSinglePageComponent
  }
}
