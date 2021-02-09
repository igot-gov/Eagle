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

export interface ISBUser {
  email: string
  emailVerified: boolean
  firstName: string
  lastName: string
  channel: string
  userName: string
  password: string
}

export interface IPersonalDetails {
  firstname: string
  surname: string
  primaryEmail: string
  username: string
}

export interface IUser {
  personalDetails: IPersonalDetails
}

export interface ISunbirdbUserResponse {
  email: string
  firstName: string
  lastName: string
  userId: string
}
