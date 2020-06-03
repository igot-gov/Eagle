import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'
import { AccountSettingsRoutingModule } from './account-settings-routing.module'
import { AccountSettingsComponent } from './components/account-settings/account-settings.component'
import { ReactiveFormsModule, FormsModule } from '@angular/forms'
import { MatInputModule } from '@angular/material/input'
import { MatFormFieldModule } from '@angular/material/form-field'
import { MatSelectModule } from '@angular/material/select'
import { MatIconModule } from '@angular/material/icon'
import { MatCardModule } from '@angular/material/card'
import { MatButtonModule } from '@angular/material/button'
import { MatToolbarModule } from '@angular/material/toolbar'
import { MatListModule } from '@angular/material/list'
import { MatCheckboxModule } from '@angular/material/checkbox'
import { MatSnackBarModule } from '@angular/material/snack-bar'
import { MatDialogModule } from '@angular/material/dialog'
import { LoaderService } from 'project/ws/author/src/lib/services/loader.service'
import { ImageCropModule } from '@ws-widget/utils/src/public-api'
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner'
import { AccountSettingsService } from './services/account-settings.service'
import { ApiService } from 'project/ws/author/src/lib/modules/shared/services/api.service'
import { ViewprofileResolverService } from './services/viewprofile-resolver.service'
import { BtnPageBackModule } from '../../../../../../../library/ws-widget/collection/src/lib/btn-page-back/btn-page-back.module'

@NgModule({
  declarations: [
    AccountSettingsComponent,
  ],
  imports: [
    CommonModule,
    AccountSettingsRoutingModule,
    ReactiveFormsModule,
    FormsModule,
    MatInputModule,
    MatFormFieldModule,
    MatSelectModule,
    MatIconModule,
    MatCardModule,
    MatButtonModule,
    MatToolbarModule,
    MatListModule,
    MatCheckboxModule,
    MatSnackBarModule,
    MatDialogModule,
    MatProgressSpinnerModule,
    ImageCropModule,
    BtnPageBackModule,
  ],
  providers: [
    LoaderService,
    AccountSettingsService,
    ApiService,
    ViewprofileResolverService,
  ],
  entryComponents: [

  ],
})
export class AccountSettingsModule { }
