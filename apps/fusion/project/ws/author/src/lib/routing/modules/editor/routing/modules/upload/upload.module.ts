import { CommonModule } from '@angular/common'
import { NgModule } from '@angular/core'
import { SharedModule } from '@ws/author/src/lib/modules/shared/shared.module'
import { AuthViewerModule } from '@ws/author/src/lib/modules/viewer/viewer.module'
import { EditorSharedModule } from '../../../shared/shared.module'
import { FileUploadComponent } from './components/file-upload/file-upload.component'
import { UploadComponent } from './components/upload/upload.component'
import { UploadRoutingModule } from './upload-routing.module'
import { MatProgressBarModule } from '@angular/material'
import { ProfanityPopUpComponent } from './components/profanity-popup/profanity-popup'
import { ProfanityMessagePopUpComponent } from './components/profanity-message-popup/profanity-message-popup'
import { ProfanityService } from './services/profanity.service'

@NgModule({
  declarations: [UploadComponent, FileUploadComponent, ProfanityPopUpComponent, ProfanityMessagePopUpComponent],
  exports: [FileUploadComponent, ProfanityPopUpComponent],
  imports: [CommonModule, SharedModule, EditorSharedModule, UploadRoutingModule, AuthViewerModule, MatProgressBarModule],
  entryComponents: [ProfanityPopUpComponent, ProfanityMessagePopUpComponent],
  providers: [ProfanityService],
})
export class UploadModule { }
