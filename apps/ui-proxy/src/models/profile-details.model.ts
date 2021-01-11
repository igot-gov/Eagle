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
        UserProfile: any
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
export interface IUserProfile {
    photo: string
    personalDetails: Partial<IPersonalDetails>
    academics: Array<Partial<IAcademic>>
    employmentDetails: Partial<IEmploymentDetails>
    professionalDetails: Array<Partial<IProfessionalDetail>>
    skills: Partial<ISkills>
    interests: Partial<IInterests>
    userId: string
    id: string
    '@id': string
    osCreatedAt: string
    osUpdatedAt: string
    osCreatedBy: string
    osUpdatedBy: string
    osid: string
    '@type': string
}

export interface IInterests {
    professional: string[]
    hobbies: string[]
    osCreatedAt: string
    osUpdatedAt: string
    osCreatedBy: string
    osUpdatedBy: string
    osid: string
    _osroot: string
    '@type': string
}
export interface ISkills {
    additionalSkills: string
    certificateDetails: string
    osCreatedAt: string
    osUpdatedAt: string
    osCreatedBy: string
    osUpdatedBy: string
    osid: string
    '@type': string
    _osroot: string
}
export interface IProfessionalDetail {
    osUpdatedAt: string
    osUpdatedBy: string
    description: string
    industry: string
    designationOther: string
    osid: string
    nameOther: string
    organisationType: string
    responsibilities: string
    osCreatedAt: string
    name: string
    osCreatedBy: string
    location: string
    designation: string
    industryOther: string
    completePostalAddress: string
    doj: string
    additionalAttributes: IAdditionalAttributes
}
export interface IAdditionalAttributes {
    osUpdatedAt: string
    osCreatedAt: string
    osUpdatedBy: string
    osCreatedBy: string
    osid: string
}
export interface IAcademic {
    osUpdatedAt: string
    nameOfQualification: string
    yearOfPassing: string
    osCreatedAt: string
    osUpdatedBy: string
    osCreatedBy: string
    nameOfInstitute: string
    osid: string
    type: string
}

export interface IEmploymentDetails {
    service: string
    cadre: string
    allotmentYearOfService: string
    dojOfService: string
    payType: string
    civilListNo: string
    employeeCode: string
    officialPostalAddress: string
    pinCode: string
    departmentName: string
    osCreatedAt: string
    osUpdatedAt: string
    osCreatedBy: string
    osUpdatedBy: string
    osid: string
    _osroot: string
    '@type': string
}
export interface IPersonalDetails {
    firstname: string
    middlename: string
    surname: string
    dob: string
    nationality: string
    domicileMedium: string
    gender: string
    maritalStatus: string
    category: string
    knownLanguages: string[]
    language: string[]
    countryCode: string
    mobile: number
    telephone: string
    primaryEmail: string
    officialEmail: string
    personalEmail: string
    postalAddress: string
    pincode: string
    osCreatedAt: string
    osUpdatedAt: string
    osCreatedBy: string
    osUpdatedBy: string
    osid: string
    _osroot: string
    '@type': string
}

export interface IResponseRootUserProfile {
    id: string
    ver: string
    ets: number
    params: IResponseParams
    responseCode: string
    result: IResult
}

export interface IResponseParams {
    resmsgid: string
    msgid: string
    err: string
    status: string
    errmsg: string
}

export interface IResult {
    UserProfile: IResponseUserProfile[]
    totalHits: number
}

export interface IResponseUserProfile {
    photo: string
    personalDetails: IResponsePersonalDetails
    academics: IResponseAcademic[]
    employmentDetails: IResponseEmploymentDetails
    professionalDetails: IResponseProfessionalDetail[]
    skills: IResponseInterests
    interests: IResponseInterests
    userId: string
    id: string
    '@id': string
    osCreatedAt: string
    osUpdatedAt: string
    osCreatedBy: string
    osUpdatedBy: string
    osid: string
    '@type': string
}

export interface IResponseAcademic {
    osUpdatedAt: string
    nameOfQualification: string
    yearOfPassing: string
    osCreatedAt: Date
    osUpdatedBy: string
    osCreatedBy: string
    nameOfInstitute: string
    osid: string
    type: string
}

export interface IResponseEmploymentDetails {
    service: string
    cadre: string
    allotmentYearOfService: string
    dojOfService: string
    payType: string
    civilListNo: string
    employeeCode: string
    officialPostalAddress: string
    pinCode: string
    departmentName: string
    osCreatedAt: string
    osUpdatedAt: string
    osCreatedBy: string
    osUpdatedBy: string
    osid: string
    _osroot: string
    '@type': string
}

export interface IResponseInterests {
    professional?: string[]
    hobbies?: string[]
    osCreatedAt: string
    osUpdatedAt: string
    osCreatedBy: string
    osUpdatedBy: string
    osid: string
    _osroot: string
    '@type': string
    additionalSkills?: string
    certificateDetails?: string
}

export interface IResponsePersonalDetails {
    firstname: string
    middlename: string
    surname: string
    dob: string
    nationality: string
    domicileMedium: string
    gender: string
    maritalStatus: string
    category: string
    knownLanguages: string[]
    countryCode: string
    mobile: number
    telephone: string
    primaryEmail: string
    officialEmail: string
    personalEmail: string
    postalAddress: string
    pincode: string
    osCreatedAt: string
    osUpdatedAt: string
    osCreatedBy: string
    osUpdatedBy: string
    osid: string
    _osroot: string
    '@type': string
}

export interface IResponseProfessionalDetail {
    osUpdatedAt: string
    osUpdatedBy: string
    description: string
    industry: string
    designationOther: string
    osid: string
    nameOther: string
    organisationType: string
    responsibilities: string
    osCreatedAt: Date
    name: string
    osCreatedBy: string
    location: string
    designation: string
    industryOther: string
    completePostalAddress: string
    doj: string
    additionalAttributes: IResponseAdditionalAttributes
}

export interface IResponseAdditionalAttributes {
    osUpdatedAt: string
    osCreatedAt: string
    osUpdatedBy: string
    osCreatedBy: string
    osid: string
}
export interface ISBUser {
    email: string
    emailVerified: boolean
    firstName: string
    lastName: string
    channel: string
    userName: string
}

//////
export interface IRootSbUser {
    UserProfile: Partial<ISBUserProfile[]>
}

export interface ISBUserProfile {
    id: string
    personalDetails: Partial<ISBPersonalDetails>
    photo: string
    userId: string
    employmentDetails: Partial<ISBEmploymentDetails>
    academics: Partial<ISBAcademic[]>
    skills: Partial<ISBSkills>
    professionalDetails: Partial<ISBProfessionalDetail[]>
    interests: Partial<ISBInterests>
}

export interface ISBAcademic {
    osUpdatedAt: string
    nameOfQualification: string
    yearOfPassing: string
    osCreatedAt: string
    osUpdatedBy: string
    osCreatedBy: string
    nameOfInstitute: string
    osid: string
    type: string
}

export interface ISBEmploymentDetails {
    departmentName: string
    osUpdatedBy: string
    officialPostalAddress: string
    employeeCode: string
    allotmentYearOfService: string
    payType: string
    civilListNo: string
    service: string
    dojOfService: string
    pinCode: string
    osCreatedBy: string
    cadre: string
}

export interface ISBInterests {
    osUpdatedAt: string
    osCreatedAt: string
    hobbies: string[]
    osUpdatedBy: string
    osCreatedBy: string
    professional: string[]
}

export interface ISBPersonalDetails {
    countryCode: string
    domicileMedium: string
    knownLanguages: string[]
    middlename: string
    mobile: number
    nationality: string
    officialEmail: string
    osUpdatedBy: string
    personalEmail: string
    pincode: string
    postalAddress: string
    primaryEmail: string
    telephone: string
    lastName: string
    gender: string
    language: string[]
    firstName: string
    dob: string
}

export interface ISBProfessionalDetail {
    osUpdatedAt: string
    osUpdatedBy: string
    description: string
    industry: string
    designationOther: string
    osid: string
    nameOther: string
    organisationType: string
    responsibilities: string
    osCreatedAt: string
    name: string
    osCreatedBy: string
    location: string
    designation: string
    industryOther: string
    completePostalAddress: string
    doj: string
    additionalAttributes: Partial<ISBAdditionalAttributes>
}

export interface ISBAdditionalAttributes {
    osUpdatedAt: string
    osCreatedAt: string
    osUpdatedBy: string
    osCreatedBy: string
    osid: string
}

export interface ISBSkills {
    osUpdatedAt: string
    certificateDetails: string
    osCreatedAt: string
    osUpdatedBy: string
    additionalSkills: string
    osCreatedBy: string
}

export interface IRootObject {
    id: string
    ver: string
    ets: number
    params: IParams
    responseCode: string
}

export interface IParams {
    resmsgid: string
    msgid: string
    err: string
    status: string
    errmsg: string
}
