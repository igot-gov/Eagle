export interface IUserProfileDetails {
  first_name: string
  last_name: string
  email: string
  wid: string
}

export interface IUserProfileDetailsFromRegistry {
  'firstname': string,
  'motherTongue': string,
  'secondaryEmail': string,
  'gender': string,
  '@type': string,
  'mobile': number,
  'middlename': string,
  'telephone': number,
  'osid': string,
  'primaryEmailType': string,
  'knownLanguages': ILanguages[],
  'wid': string,
  'nationality': string,
  'surname': string,
  'dob': string,
  'category': string,
  'primaryEmail': string,
  'maritalStatus': string,
  'residenceAddress': string
}

export interface ILanguages {
  name: string
}

export interface ILanguagesApiData {
  languages: ILanguages[]
}
export interface INationality {
  name: string
}
export interface INationalityApiData {
  nationalities: INationality[]
}

export namespace NsUserProfileTypes {
  export enum EPrimaryEmailType {
    PERSONAL = 'personal',
    OFFICIAL = 'official',
  }

  export enum EUserGender {
    MALE = 'MALE',
    FEMALE = 'FEMALE',
    OTHER = 'OTHER',
  }

  export enum EMaritalStatus {
    SINGLE = 'single',
    MARRIED = 'married',
  }
  export enum ECategory {
    GENERAL = 'general',
    OBC = 'obc',
    SC = 'sc',
    ST = 'st',
  }
}
