import { NgModule } from '@angular/core'
import { ProfileHobbiesComponent } from './profile-hobbies.component'
import {
  MatButtonModule, MatCardModule, MatChipsModule, MatDividerModule,
  MatExpansionModule, MatIconModule, MatProgressSpinnerModule,
} from '@angular/material'
import { BrowserModule } from '@angular/platform-browser'

@NgModule({
  declarations: [ProfileHobbiesComponent],
  imports: [BrowserModule, MatButtonModule, MatCardModule, MatChipsModule,
    MatDividerModule, MatExpansionModule, MatIconModule, MatProgressSpinnerModule],
  entryComponents: [ProfileHobbiesComponent],
})
export class ProfileHobbiesModule {

}
