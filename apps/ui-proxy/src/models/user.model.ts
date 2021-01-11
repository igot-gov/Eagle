export interface IUserPreferencesResponse {
  data?: IUserPreferences
}

export interface IUserPreferences {
  selectedTheme: string
  selectedFont: string
  selectedLanguage: string
}

export interface IUserRolesResponse {
  data?: {
    id: string;
    ver: string;
    ts: string;
    params: {
      resmsgid?: string;
      msgid?: string;
      err?: string;
      status: string;
      errmsg?: string;
    };
    responseCode: string;
    result: {
      response: string[];
    };
  }
}

export interface IUserTncResponse {
  data?: {
    result?: {
      response?: {
        isAccepted?: boolean;
      };
    };
  }
}
export interface IUserGraphProfileResponse {
  id: string
  ver: string
  ts: string
  params?: string
  responseCode: string
  result: {
    response: IUserGraphProfile;
  }
}

export interface IUserGraphProfile {
  department: string
  jobTitle: string
  givenName: string
  surname: string
  imageUrl: string
  usageLocation: string
  onPremisesUserPrincipalName: string
  mobilePhone: string
  companyName: string
}
export interface IUserDetailsResponse {
  empNumber: number
  email: string
  name: string
  status: string
  onsiteOffshoreIndicator: string
  company: string
  jobLevel: string
  currentCity: string
  ibuCode: string
  puCode: string
  cuCode: string
  masterCustomerCode: string
  customerCode: string
  masterProjectCode: string
  projectCode: string
  joiningDate: string
  downloadAllowed: true
}

export interface IUserLoggedIn {
  preferences: IUserPreferences
  roles: string[]
  tncStatus: boolean
}
export interface IUserProfileResult {
  email?: string
  miscellaneous?: {
    empNumber?: number;
    email?: string;
    name?: string;
    status?: string;
    onsiteOffshoreIndicator?: string;
    company?: string;
    jobLevel?: string;
    currentCity?: string;
    ibuCode?: string;
    puCode?: string;
    cuCode?: string;
    masterCustomerCode?: string;
    customerCode?: string;
    masterProjectCode?: string;
    projectCode?: string;
    joiningDate?: string;
    downloadAllowed?: boolean;
    department?: string;
    jobTitle?: string;
    givenName?: string;
    surname?: string;
    imageUrl?: string;
    usageLocation?: string;
    onPremisesUserPrincipalName?: string;
    mobilePhone?: string;
    companyName?: string;
  }
  name?: string
}

export interface IUserAutocomplete {
  department_name: string
  email: string
  first_name: string
  last_name: string
  root_org: string
  wid: string
}

export interface ISbUserResponse {
  id: string
  ver: string
  ts: string
  params: IParams
  responseCode: string
  result: IResult
}
export interface IResult {
  response: IResponse
}
export interface IParams {
  resmsgid?: string
  msgid: string
  err?: string
  status: string
  errmsg?: string
}
export interface IOrganisation {
  updatedBy?: string
  organisationId: string
  orgName: string
  addedByName?: string
  addedBy?: string
  roles: string[]
  approvedBy?: string
  updatedDate?: string
  userId: string
  approvaldate?: string
  isDeleted: boolean
  parentOrgId?: string
  hashTagId: string
  isRejected?: string
  position?: string
  id: string
  orgjoindate: string
  isApproved?: string
  orgLeftDate?: string
}
export interface IRootOrg {
  dateTime?: string
  preferredLanguage?: string
  keys: IKeys
  channel: string
  approvedBy?: string
  description: string
  updatedDate?: string
  addressId?: string
  orgType?: string
  provider?: string
  orgCode?: string
  locationId?: string
  theme?: string
  id: string
  isApproved?: string
  communityId?: string
  slug: string
  email?: string
  isSSOEnabled: boolean
  thumbnail?: string
  updatedBy?: string
  orgName: string
  locationIds: string[]
  externalId?: string
  isRootOrg: boolean
  rootOrgId: string
  imgUrl?: string
  approvedDate?: string
  orgTypeId?: string
  homeUrl?: string
  isDefault?: string
  createdDate: string
  contactDetail?: string
  parentOrgId?: string
  createdBy?: string
  hashTagId: string
  noOfMembers?: string
  status: number
}
export interface IFramework {
  framework: string
}
export interface IResponse {
  webPages: string[]
  maskedPhone: string
  rootOrgName: string
  subject: string[]
  channel: string
  language: string[]
  updatedDate?: string
  managedBy?: string
  flagsValue: number
  id: string
  recoveryEmail: string
  identifier: string
  thumbnail?: string
  updatedBy?: string
  accesscode?: string
  locationIds: string[]
  registryId?: string
  rootOrgId: string
  prevUsedEmail: string
  firstName: string
  tncAcceptedOn?: string
  phone: string
  dob?: string
  grade: string[]
  currentLoginTime?: string
  userType: string
  status: number
  lastName: string
  gender?: string
  roles: string[]
  prevUsedPhone: string
  stateValidated: boolean
  isDeleted: boolean
  organisations: IOrganisation[]
  countryCode?: string
  maskedEmail?: string
  tempPassword?: string
  email: string
  rootOrg: IRootOrg
  profileSummary?: string
  phoneVerified: boolean
  recoveryPhone: string
  avatar?: string
  userName: string
  lastLoginTime?: string
  emailVerified: boolean
  framework: IFramework
  createdDate: string
  createdBy: string
  location?: string
  tncAcceptedVersion?: string
}

export interface IKeys {
  keys: string
}

export interface ISunbirdbUserResponse {
  email: string
  firstName: string
  lastName: string
  userId: string
}
