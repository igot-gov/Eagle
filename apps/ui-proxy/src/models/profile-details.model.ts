export interface IUserRegistryRequest {
    id: string | 'open-saber.registry.create',
    ver: string | '1.0',
    ets: string | '11234',
    params: {
        did: '',
        key: '',
        msgid: ''
    },
    request: {
        // tslint:disable-next-line: no-any
        PersonalDetails: any
    }
}

export interface IUserRegistrySearchRequest {
    id: 'open-saber.registry.search',
    ver: string | '1.0',
    ets: string | '11234',
    params: {
        did: '',
        key: '',
        msgid: ''
    },
    request: {
        entityType: string[],
        // tslint:disable-next-line: no-any
        filters: any,
        limit?: number,
        offset?: number
    }
}

export interface IUserRegistryReadRequest {
    id: 'open-saber.registry.read',
    ver: '1.0',
    ets: '11234',
    params: {
        did: '',
        key: '',
        msgid: ''
    },
    request: {
        // tslint:disable-next-line: no-any
        UserProfile: any,
        includeSignatures: true

    }
}

export interface IUserRegistryUpdateRequest {

    id: 'open-saber.registry.update',
    ver: '1.0',
    ets: '11234',
    params: {
        'did': '',
        'key': '',
        'msgid': ''
    },
    request: {
        // tslint:disable-next-line: no-any
        UserProfile: any
    }
}

export interface IUserRegistry {
    firstname: string,
    surname: string,
    mobile: number,
    primaryEmail: string
}
