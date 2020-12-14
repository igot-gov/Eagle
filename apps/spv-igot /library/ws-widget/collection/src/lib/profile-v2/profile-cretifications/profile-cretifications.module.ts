import { NgModule } from '@angular/core'
import { ProfileCretificationsComponent } from './profile-cretifications.component'
import {
  MatButtonModule, MatCardModule, MatChipsModule, MatDividerModule,
  MatExpansionModule, MatIconModule, MatProgressSpinnerModule,
} from '@angular/material'
import { BrowserModule } from '@angular/platform-browser'

@NgModule({
  declarations: [ProfileCretificationsComponent],
  imports: [BrowserModule, MatButtonModule, MatCardModule, MatChipsModule,
    MatDividerModule, MatExpansionModule, MatIconModule, MatProgressSpinnerModule],
  entryComponents: [ProfileCretificationsComponent],
})
export class ProfileCretificationsModule {

}
