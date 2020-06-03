export namespace NsMiniProfile {
  export interface IMiniProfileData {
    user_id: string,
    user_name?: string,
    pid_name?: string,
    role: string,
    role_privacy?: boolean,
    teaching_state: string,
    teaching_state_privacy?: boolean,
    organization?: string,
    organization_privacy?: boolean,
    profile_image?: string,
    phone?: number[],
    phone_privacy?: boolean,
    public_profiles?: IUserSocialDetails[]
    email: string
    emailprivacy: boolean
  }
  export interface IUserSocialDetails {
    field: string,
    data: string,
    privacy: boolean
  }
  export interface IUserGroupDetails {
    group_id: number,
    friendly_name: string,
    description: string
  }
}
