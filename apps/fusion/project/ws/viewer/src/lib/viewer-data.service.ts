import { Injectable } from '@angular/core'
import { NsContent } from '@ws-widget/collection'
import { Subject, ReplaySubject } from 'rxjs'

export interface IViewerTocChangeEvent {
  tocAvailable: boolean
  nextResource: string | null
  prevResource: string | null
}
export interface IViewerResourceOptions {
  page?: {
    min: number
    max: number
    current: number
    queryParamKey: string
  }
  zoom?: {
    min: number
    max: number
    current: number
    queryParamKey: string
  }
}
export type TStatus = 'pending' | 'done' | 'error' | 'none'

@Injectable({
  providedIn: 'root',
})
export class ViewerDataService {
  resourceId: string | null = null
  resource: NsContent.IContent | null = null
  error: any
  status: TStatus = 'none'
  resourceChangedSubject = new Subject<string>()
  changedSubject = new ReplaySubject(1)
  tocChangeSubject = new ReplaySubject<IViewerTocChangeEvent>(1)
  navSupportForResource = new ReplaySubject<IViewerResourceOptions>(1)
  constructor() { }

  reset(resourceId: string | null = null, status: TStatus = 'none') {
    this.resourceId = resourceId
    this.resource = null
    this.error = null
    this.status = status
    this.changedSubject.next()
  }
  updateResource(resource: NsContent.IContent | null = null, error: any | null = null) {
    if (resource) {
      this.resource = resource
      if (resource && resource.identifier) {
        this.resourceId = resource.identifier
      }
      this.error = null
      this.status = 'done'
    } else {
      this.resource = null
      this.error = error
      this.status = 'error'
    }
    this.changedSubject.next()
  }
  updateNextPrevResource(isValid = true, prev: string | null = null, next: string | null = null) {
    this.tocChangeSubject.next(
      {
        tocAvailable: isValid,
        nextResource: next,
        prevResource: prev,
      },
    )
  }

}
