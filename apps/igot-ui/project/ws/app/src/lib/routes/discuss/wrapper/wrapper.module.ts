import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'
import { DiscussionUiModule } from '@project-sunbird/discussions-ui-v8'

import { CsModule } from '@project-sunbird/client-services'

@NgModule({
    declarations: [],
    imports: [
        CommonModule,
        DiscussionUiModule,
    ],
    exports: [DiscussionUiModule],
})
export class WrapperModule {
    constructor() {
        CsModule.instance.init({
            core: {
                httpAdapter: 'HttpClientBrowserAdapter',
                global: {
                    channelId: '', // required
                    producerId: '', // required
                    deviceId: '', // required
                },
                api: {
                    host: 'http://localhost:3002', // default host
                    authentication: {
                        // userToken: string; // optional
                        // bearerToken: string; // optional
                    },
                },
            },
            services: {
                groupServiceConfig: {
                    apiPath: '/learner/group/v1',
                    dataApiPath: '/learner/data/v1/group',
                    updateGroupGuidelinesApiPath: '/learner/group/membership/v1',
                },
                userServiceConfig: {
                    apiPath: '/learner/user/v2',
                },
                formServiceConfig: {
                    apiPath: '/learner/data/v1/form',
                },
                courseServiceConfig: {
                    apiPath: '/learner/course/v1',
                    certRegistrationApiPath: '/learner/certreg/v2/certs',
                },
                discussionServiceConfig: {
                    apiPath: '/discussion',
                },
            },
        })
    }
}
