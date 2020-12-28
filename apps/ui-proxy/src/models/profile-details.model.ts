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
        PersonalDetails: any
    }
}

export interface IUserRegistry {
    firstname: string,
    surname: string,
    mobile: number,
    primaryEmail: string
    userId: string
}
export interface IUpdateUserRegistry {
    firstname: string,
    motherTongue: string,
    secondaryEmail: string,
    gender: string,
    '@type': string,
    mobile: number,
    middlename: string,
    telephone: number,
    osid: 'string',
    primaryEmailType: string,
    language: string[],
    nationality: string,
    surname: string,
    dob: string,
    id: string,
    '@id': string,
    category: string,
    primaryEmail: string,
    maritalStatus: string,
    residenceAddress: string
}
