import { NgModule } from '@angular/core'
import { ProfileCareerComponent } from './profile-career.component'
import {
  MatButtonModule, MatCardModule, MatChipsModule, MatDividerModule,
  MatExpansionModule, MatIconModule, MatProgressSpinnerModule,
} from '@angular/material'
import { BrowserModule } from '@angular/platform-browser'
import { PipeRelativeTimeModule } from '@ws-widget/utils'

@NgModule({
  declarations: [ProfileCareerComponent],
  imports: [BrowserModule, MatButtonModule, MatCardModule, MatChipsModule,
    MatDividerModule, MatExpansionModule, MatIconModule, MatProgressSpinnerModule, PipeRelativeTimeModule],
  entryComponents: [ProfileCareerComponent],
})
export class ProfileCareerModule {

}
