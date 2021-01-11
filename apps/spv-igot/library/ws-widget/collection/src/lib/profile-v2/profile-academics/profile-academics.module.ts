import { NgModule } from '@angular/core'
import { ProfileAcademicsComponent } from './profile-academics.component'
import {
  MatButtonModule, MatCardModule, MatChipsModule, MatDividerModule,
  MatExpansionModule, MatIconModule, MatProgressSpinnerModule,
} from '@angular/material'
import { BrowserModule } from '@angular/platform-browser'
import { PipeOrderByModule } from '@ws-widget/utils'

@NgModule({
  declarations: [ProfileAcademicsComponent],
  imports: [BrowserModule, MatButtonModule, MatCardModule, MatChipsModule,
    MatDividerModule, MatExpansionModule, MatIconModule, MatProgressSpinnerModule, PipeOrderByModule],
  entryComponents: [ProfileAcademicsComponent],
})
export class ProfileAcademicsModule {

}
