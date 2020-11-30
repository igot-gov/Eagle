import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'
import { PublicReleaseComponent } from './public-release.component'
import { ReleaseNotesModule } from '@ws-widget/collection'
// import { HorizontalScrollerModule, PipeSafeSanitizerModule } from '@ws-widget/utils'

@NgModule({
  declarations: [PublicReleaseComponent],
  imports: [
    CommonModule,
    ReleaseNotesModule,

  ],

  exports: [PublicReleaseComponent],
})
export class PublicReleaseModule { }
