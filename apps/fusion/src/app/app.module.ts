import { FullscreenOverlayContainer, OverlayContainer } from '@angular/cdk/overlay'
import { APP_BASE_HREF, PlatformLocation } from '@angular/common'
import { HttpClientJsonpModule, HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http'
import { APP_INITIALIZER, Injectable, NgModule, ErrorHandler } from '@angular/core'
import {
  GestureConfig,
  MatButtonModule,
  MatCardModule,
  MatDialogModule,
  MatDividerModule,
  MatExpansionModule,
  MatIconModule,
  MatMenuModule,
  MatProgressBarModule,
  MatRippleModule,
  MatSliderModule,
  MatToolbarModule,
  MatTooltipModule,
  MatProgressSpinnerModule,
  MAT_PROGRESS_SPINNER_DEFAULT_OPTIONS,
  MAT_SNACK_BAR_DEFAULT_OPTIONS,
  MatInputModule,
  MatFormFieldModule,
} from '@angular/material'
import { BrowserModule, HAMMER_GESTURE_CONFIG } from '@angular/platform-browser'
import { BrowserAnimationsModule } from '@angular/platform-browser/animations'
import {
  BtnFeatureModule,
  ErrorResolverModule,
  TourModule,
  WIDGET_REGISTERED_MODULES,
  WIDGET_REGISTRATION_CONFIG,
  PipeContentRoutePipe,
} from '@ws-widget/collection'
import { StickyHeaderModule } from '@ws-widget/collection/src/lib/_common/sticky-header/sticky-header.module'
import { WidgetResolverModule } from '@ws-widget/resolver'
import { LoggerService, PipeSafeSanitizerModule } from '@ws-widget/utils'
import { SearchModule } from '@ws/app/src/public-api'
import 'hammerjs'
import { KeycloakAngularModule } from 'keycloak-angular'
import { AppRoutingModule } from './app-routing.module'
import { InitService } from './services/init.service'
import { GlobalErrorHandlingService } from './services/global-error-handling.service'
import { AppTocResolverService } from '@ws/app/src/lib/routes/app-toc/resolvers/app-toc-resolver.service'

import { RootComponent } from './component/root/root.component'
import { LoginComponent } from './component/login/login.component'
import { AppChatbotComponent } from './component/app-chatbot/app-chatbot.component'
import { AppFooterComponent } from './component/app-footer/app-footer.component'
import { AppNavBarComponent } from './component/app-nav-bar/app-nav-bar.component'
import { AppPublicNavBarComponent } from './component/app-public-nav-bar/app-public-nav-bar.component'
// import { ServiceWorkerModule } from '@angular/service-worker'
// import { environment } from '../environments/environment'
import { DialogConfirmComponent } from './component/dialog-confirm/dialog-confirm.component'
import { FordLoginComponent } from './component/ford-login/ford-login.component'
import { InvalidUserComponent } from './component/invalid-user/invalid-user.component'
import { LoginRootComponent } from './component/login-root/login-root.component'
import { LoginRootDirective } from './component/login-root/login-root.directive'
import { PathfindersLoginComponent } from './component/pathfinders-login/pathfinders-login.component'
import { TncRendererComponent } from './component/tnc-renderer/tnc-renderer.component'
import { MobileAppModule } from './routes/public/mobile-app/mobile-app.module'
import { PublicAboutModule } from './routes/public/public-about/public-about.module'
import { PublicContactModule } from './routes/public/public-contact/public-contact.module'
import { PublicFaqModule } from './routes/public/public-faq/public-faq.module'
import { TncComponent } from './routes/tnc/tnc.component'
import { UploadPdfComponent } from './routes/upload-pdf/upload-pdf.component'
import { AppInterceptorService } from './services/app-interceptor.service'
import { AppRetryInterceptorService } from './services/app-retry-interceptor.service'
import { TncAppResolverService } from './services/tnc-app-resolver.service'
import { TncPublicResolverService } from './services/tnc-public-resolver.service'
import { LoginSiemensComponent } from './component/login-siemsns/login-siemsns.component'
import { DialogUserDetailsComponent } from './component/dialog-user-details/dialog-user-details.component'
import { FormsModule, ReactiveFormsModule } from '@angular/forms'
import { EpochLoginComponent } from './component/epoch-login/epoch-login.component'
import { AcademyLoginComponent } from './component/academy-login/academy-login.component'
import { AssistedgeLoginComponent } from './component/assistedge-login/assistedge-login.component'
import { EpochLoginV2Component } from './component/epoch-login-v2/epoch-login-v2.component'
import { UrlSerializer } from '@angular/router'
import { UrlSerializerService } from './services/url-serializer.service'
// import { ServiceWorkerModule } from '@angular/service-worker'
// import { environment } from '../environments/environment'

@Injectable()
export class HammerConfig extends GestureConfig {
  buildHammer(element: HTMLElement) {
    return new GestureConfig({ touchAction: 'pan-y' }).buildHammer(element)
  }
}
const appInitializer = (initSvc: InitService, logger: LoggerService) => async () => {
  try {
    await initSvc.init()
  } catch (error) {
    logger.error('ERROR DURING APP INITIALIZATION >', error)
  }
}

const getBaseHref = (platformLocation: PlatformLocation): string => {
  return platformLocation.getBaseHrefFromDOM()
}

// tslint:disable-next-line: max-classes-per-file
@NgModule({
  declarations: [
    RootComponent,
    LoginComponent,
    AppNavBarComponent,
    AppPublicNavBarComponent,
    TncComponent,
    TncRendererComponent,
    AppFooterComponent,
    InvalidUserComponent,
    DialogConfirmComponent,
    AppChatbotComponent,
    PathfindersLoginComponent,
    LoginRootComponent,
    LoginRootDirective,
    FordLoginComponent,
    LoginSiemensComponent,
    DialogUserDetailsComponent,
    EpochLoginComponent,
    AcademyLoginComponent,
    UploadPdfComponent,
    AssistedgeLoginComponent,
    EpochLoginV2Component,
  ],
  imports: [
    FormsModule,
    ReactiveFormsModule,
    BrowserModule,
    HttpClientModule,
    HttpClientJsonpModule,
    BrowserAnimationsModule,
    KeycloakAngularModule,
    AppRoutingModule,
    ...WIDGET_REGISTERED_MODULES,
    WidgetResolverModule.forRoot(WIDGET_REGISTRATION_CONFIG),
    StickyHeaderModule,
    ErrorResolverModule,
    // Material Imports
    MatSliderModule,
    MatButtonModule,
    MatCardModule,
    MatToolbarModule,
    MatIconModule,
    MatMenuModule,
    MatDividerModule,
    MatProgressBarModule,
    MatExpansionModule,
    MatRippleModule,
    MatProgressSpinnerModule,
    MatDialogModule,
    MatInputModule,
    MatFormFieldModule,
    MatTooltipModule,
    SearchModule,
    BtnFeatureModule,
    PublicAboutModule,
    PublicContactModule,
    PublicFaqModule,
    MobileAppModule,
    PipeSafeSanitizerModule,
    TourModule,
    // ServiceWorkerModule.register('ngsw-worker.js', { enabled: environment.production }),
  ],
  exports: [TncComponent],
  bootstrap: [RootComponent],
  entryComponents: [
    DialogConfirmComponent,
    LoginComponent,
    PathfindersLoginComponent,
    AcademyLoginComponent,
    FordLoginComponent,
    LoginSiemensComponent,
    DialogUserDetailsComponent,
    EpochLoginComponent,
    AssistedgeLoginComponent,
    EpochLoginV2Component,
  ],
  providers: [
    {
      deps: [InitService, LoggerService],
      multi: true,
      provide: APP_INITIALIZER,
      useFactory: appInitializer,
    },
    {
      provide: MAT_SNACK_BAR_DEFAULT_OPTIONS,
      useValue: { duration: 5000 },
    },
    {
      provide: MAT_PROGRESS_SPINNER_DEFAULT_OPTIONS,
      useValue: {
        diameter: 55,
        strokeWidth: 4,
      },
    },
    { provide: HTTP_INTERCEPTORS, useClass: AppInterceptorService, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: AppRetryInterceptorService, multi: true },
    TncAppResolverService,
    TncPublicResolverService,
    PipeContentRoutePipe,
    AppTocResolverService,
    {
      provide: APP_BASE_HREF,
      useFactory: getBaseHref,
      deps: [PlatformLocation],
    },
    { provide: OverlayContainer, useClass: FullscreenOverlayContainer },
    { provide: HAMMER_GESTURE_CONFIG, useClass: HammerConfig },
    { provide: ErrorHandler, useClass: GlobalErrorHandlingService },
    {
      provide: UrlSerializer,
      useClass: UrlSerializerService,
    },
  ],
})
export class AppModule {}
