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

export interface IChipItems {
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
