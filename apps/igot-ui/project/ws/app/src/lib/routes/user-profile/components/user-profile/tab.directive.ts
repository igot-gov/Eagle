import { Directive, AfterViewInit, OnDestroy, Optional } from '@angular/core'
import { NgControl } from '@angular/forms'
import { MatAutocompleteTrigger } from '@angular/material'

@Directive({ selector: '[tab-directive]' })
export class TabDirective implements AfterViewInit, OnDestroy {
  observable: any
  constructor(@Optional() private autoTrigger: MatAutocompleteTrigger,
              @Optional() private control: NgControl) { }
  ngAfterViewInit() {
    this.observable = this.autoTrigger.panelClosingActions.subscribe(_x => {
      if (this.autoTrigger.activeOption) {
        const value = this.autoTrigger.activeOption.value
        if (this.control) {
          // tslint:disable-next-line: no-non-null-assertion
          this.control.control!.setValue(value, { emit: false })
        }
        this.autoTrigger.writeValue(this.autoTrigger.activeOption.value)
      }
    })
  }
  ngOnDestroy() {
    this.observable.unsubscribe()
  }
}
