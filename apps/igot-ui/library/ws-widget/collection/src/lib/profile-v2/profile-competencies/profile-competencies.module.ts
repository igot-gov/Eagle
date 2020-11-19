import { NgModule } from '@angular/core'
import { ProfileCompetenciesComponent } from './profile-competencies.component'
import {
  MatButtonModule, MatCardModule, MatChipsModule, MatDividerModule,
  MatExpansionModule, MatIconModule, MatProgressSpinnerModule,
} from '@angular/material'
import { BrowserModule } from '@angular/platform-browser'

@NgModule({
  declarations: [ProfileCompetenciesComponent],
  imports: [BrowserModule, MatButtonModule, MatCardModule, MatChipsModule,
    MatDividerModule, MatExpansionModule, MatIconModule, MatProgressSpinnerModule],
  entryComponents: [ProfileCompetenciesComponent],
})
export class ProfileCompetenciesModule {

}
