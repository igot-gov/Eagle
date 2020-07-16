import { ILanguages } from './user-profile.model'
export namespace NsUserProfileDetails {
  export interface IUserProfileFields {
    'firstname': string
    'motherTongue': string
    'secondaryEmail': string
    'gender': string
    'mobile': number
    'middlename': string
    'telephone': number
    'primaryEmailType': string
    'knownLanguages': ILanguages[]
    'nationality': string
    'surname': string
    'dob': string
    'category': string
    'primaryEmail': string
    'maritalStatus': string
    'residenceAddress': string
  }
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
