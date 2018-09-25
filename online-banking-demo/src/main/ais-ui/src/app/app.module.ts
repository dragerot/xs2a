import { BrowserModule } from '@angular/platform-browser';
import { NgModule, APP_INITIALIZER, LOCALE_ID } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';
import { AppRoutingModule } from './app-routing.module';

import { AppComponent } from './app.component';
import { AisConsentConfirmationPageComponent } from './components/ais-consent-confirmation-page/ais-consent-confirmation-page.component';
import { AisTanConfirmationPageComponent } from './components/ais-tan-confirmation-page/ais-tan-confirmation-page.component';
import { AisConsentConfirmationErrorComponent } from './components/ais-consent-confirmation-error/ais-consent-confirmation-error.component';
import { AisConsentConfirmationDeniedComponent } from './components/ais-consent-confirmation-denied/ais-consent-confirmation-denied.component';
import { AisTanConfirmationCanceledComponent } from './components/ais-tan-confirmation-canceled/ais-tan-confirmation-canceled.component';
import { AisTanConfirmationErrorComponent } from './components/ais-tan-confirmation-error/ais-tan-confirmation-error.component';
import { AisTanConfirmationSuccessfulComponent } from './components/ais-tan-confirmation-successful/ais-tan-confirmation-successful.component';
import { initializer } from './utils/app-init';
import { KeycloakAngularModule, KeycloakService } from '../../node_modules/keycloak-angular';
import { FormsModule } from '@angular/forms';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { registerLocaleData } from '@angular/common';
import localeDE from '@angular/common/locales/de';
import { AisHelpPageComponent } from './components/ais-help-page/ais-help-page.component';

registerLocaleData(localeDE);



@NgModule({
  declarations: [
    AppComponent,
    AisTanConfirmationPageComponent,
    AisTanConfirmationErrorComponent,
    AisTanConfirmationCanceledComponent,
    AisTanConfirmationSuccessfulComponent,
    AisConsentConfirmationPageComponent,
    AisConsentConfirmationDeniedComponent,
    AisConsentConfirmationErrorComponent,
    AisHelpPageComponent,
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    AppRoutingModule,
    KeycloakAngularModule,
    FormsModule,
    NgbModule.forRoot(),
  ],
  providers: [{
    provide: APP_INITIALIZER,
    useFactory: initializer,
    multi: true,
    deps: [KeycloakService],
  }, {
    provide: LOCALE_ID, useValue: 'de'
  },
  ],
  bootstrap: [AppComponent],
})
export class AppModule { }
