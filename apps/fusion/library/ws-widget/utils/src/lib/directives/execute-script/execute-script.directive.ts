import { Directive, OnInit, ElementRef } from '@angular/core'

@Directive({
  selector: '[wsUtilsExecuteScript]',
})
export class ExecuteScriptDirective implements OnInit {
  constructor(private elementRef: ElementRef) {}

  ngOnInit() {
    // wait for DOM rendering
    setTimeout(() => {
      this.reinsertScripts()
      this.reinsertCSS()
    })
  }

  reinsertScripts() {
    const scripts = <HTMLScriptElement[]>(
      this.elementRef.nativeElement.getElementsByTagName('script')
    )
    const scriptsInitialLength = scripts.length || 0
    for (let i = 0; i < scriptsInitialLength; i += 1) {
      const script = scripts[i] as HTMLScriptElement
      const scriptCopy = <HTMLScriptElement>document.createElement('script')
      scriptCopy.type = script.type ? script.type : 'text/javascript'
      if (script.innerHTML) {
        scriptCopy.innerHTML = script.innerHTML
      } else if (script.src) {
        scriptCopy.src = script.src
      }
      scriptCopy.async = false
      if (script.parentNode) {
        script.parentNode.replaceChild(scriptCopy, script)
      }
    }
  }

  reinsertCSS() {
    const styles = <HTMLStyleElement[]>this.elementRef.nativeElement.getElementsByTagName('style')
    const styleInitialLength = styles.length || 0
    for (let i = 0; i < styleInitialLength; i += 1) {
      const styele = styles[i] as HTMLStyleElement
      const styleCopy = <HTMLStyleElement>document.createElement('style')
      if (styele.innerHTML) {
        styleCopy.innerHTML = styele.innerHTML
      }
      if (styele.parentNode) {
        styele.parentNode.replaceChild(styleCopy, styele)
      }
    }
  }
}
