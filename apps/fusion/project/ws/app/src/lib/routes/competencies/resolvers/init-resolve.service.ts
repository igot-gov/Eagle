import { Injectable } from '@angular/core'
import { ActivatedRouteSnapshot, Resolve } from '@angular/router'
import { CKEditorService } from 'library/ws-widget/collection/src/lib/_common/ck-editor/ck-editor.service'
import { Observable, forkJoin, of } from 'rxjs'
@Injectable()
export class InitResolver implements Resolve<any> {
  constructor(
    private ckEditorInject: CKEditorService,
  ) { }

  resolve(route: ActivatedRouteSnapshot): Observable<any> {
    const forkProcess: Observable<any>[] = [of(undefined)]
    const data: string[] = route.data ? route.data.load || [] : []

    if (data.includes('ckeditor')) {
      forkProcess.push(this.ckEditorInject.inject())
    }
    return forkJoin(forkProcess).pipe()
  }
}
