export interface IAccountSettings {

    user_id: string,
    user_name: string,
    role: string,
    role_privacy?: boolean,
    teaching_state: string,
    teaching_state_privacy?: boolean,
    organization: string,
    organization_privacy?: boolean,
    profile_image?: string,
    phone?: number [],
    phone_privacy?: boolean,
    public_profiles?: IAccountSocialDetails[]
}
export interface IAccountSocialDetails {
    field: string,
    data: string,
    privacy: boolean
}
