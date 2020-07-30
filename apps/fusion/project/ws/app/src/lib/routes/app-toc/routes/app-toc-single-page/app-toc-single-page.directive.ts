import { Directive, ViewContainerRef } from '@angular/core'

@Directive({
  selector: '[wsAppAppTocSinglePage]',
})
export class AppTocSinglePageDirective {

  constructor(
    public viewContainerRef: ViewContainerRef,
  ) { }

}
