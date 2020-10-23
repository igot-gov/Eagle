
export namespace NSProfileDataV2 {
  export interface IProfileJsonData {
    tabs: IProfileTab[]
  }

  export interface IProfileTab {
    name: string
    key: string
    render: boolean
    badges: {
      enabled: boolean
      uri?: string
    }
    enabled: boolean
    routerLink: string
  }
  export interface IProfile {
    id: string
    userId: string
    academics?: IAcademics[]
    employmentDetails?: IEmploymentDetails
    interests: IInterests
    photo: string | null | undefined
    osCreatedAt: string
    osCreatedBy: string
    osUpdatedAt: string
    osUpdatedBy: string
    osid: string
    personalDetails: IPersonalDetails
    professionalDetails: IProfessionalDetails[]
    skills: ISkills

  }
  export interface IAcademics {
    nameOfInstitute: string
    nameOfQualification: string
    osCreatedAt: string
    osCreatedBy: string
    osUpdatedAt: string
    osUpdatedBy: string
    osid: string
    type: string
    yearOfPassing: string
  }

  export interface IEmploymentDetails {
    allotmentYearOfService: string
    cadre: string
    civilListNo: string
    departmentName: string
    dojOfService: string
    employeeCode: string
    officialPostalAddress: string
    osCreatedAt: string
    osCreatedBy: string
    osUpdatedAt: string
    osUpdatedBy: string
    osid: string
    payType: string
    pinCode: string
    service: string
  }

  export interface IInterests {
    hobbies: any[]
    osCreatedAt: string
    osCreatedBy: string
    osUpdatedAt: string
    osUpdatedBy: string
    osid: string
    professional: any[]
  }
  export interface IPersonalDetails {
    category: string
    countryCode: string
    dob: string
    domicileMedium: string
    firstname: string
    lasttname: string
    gender: string
    knownLanguages: any[]
    maritalStatus: string
    middlename: string
    mobile: number
    nationality: string
    officialEmail: string
    osCreatedAt: string
    osCreatedBy: string
    osUpdatedAt: string
    osUpdatedBy: string
    osid: string
    personalEmail: string
    pincode: string
    postalAddress: string
    primaryEmail: string
    surname: string
    telephone: string
  }
  export interface IProfessionalDetails {
    additionalAttributes: { osid: string }
    completePostalAddress: string
    description: string
    designation: string
    designationOther: string
    doj: string
    industry: string
    industryOther: string
    location: string
    name: string
    nameOther: string
    organisationType: string
    osCreatedAt: string
    osCreatedBy: string
    osUpdatedAt: string
    osUpdatedBy: string
    osid: string
    responsibilities: string
  }
  export interface ISkills {
    additionalSkills: string
    certificateDetails: string
    osCreatedAt: string
    osCreatedBy: string
    osUpdatedAt: string
    osUpdatedBy: string
    osid: string
  }

  export interface IPortalProfile {
    department_name: string
    email: string
    first_name: string
    kid: string
    last_name: string
    rank: number
    wid: string
  }
  export interface IBadgeResponse {
    canEarn: IBadge[]
    closeToEarning: IBadge[]
    earned: IBadgeRecent[]
    lastUpdatedDate: string
    recent: IBadgeRecent[]
    totalPoints: [
      {
        collaborative_points: number
        learning_points: number
      }
    ]
  }

  export interface IBadge {
    Description: string
    BadgeName: string
    BadgeImagePath: string
    badge_group: string
    badge_id: string
    badge_name: string
    badge_order: string
    badge_type: 'O' | 'R'
    hover_text: string
    how_to_earn: string
    image: string
    is_new: number
    progress: number
    received_count: number
    threshold: number
  }

  export interface IBadgeRecent extends IBadge {
    first_received_date: string
    last_received_date: string
    message: string
    image: string
  }

  export interface IUserNotification {
    image: string
    badge_group: string
    is_new: number
    received_count: number
    badge_id: string
    how_to_earn: string
    progress: number
    threshold: number
    badge_type: string
    badge_name: string
    last_received_date: string
    first_received_date: string
    hover_text: string
    message: string
  }

  export interface IUserTotalPoints {
    learning_points: number
    collaborative_points: number
  }

  export interface IUserNotifications {
    totalPoints: IUserTotalPoints[]
    recent_badge: IUserNotification
  }

}
