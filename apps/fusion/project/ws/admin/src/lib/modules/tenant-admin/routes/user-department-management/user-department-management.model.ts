export interface IUserDepartmentChange {
    user_id: string
    user_firstname: string
    user_lastname: string
    user_email: string
    existing_org_name: string
    new_org_name: string
    status: EChangeReqStatusTypes
    approver_id: string
    approver_firstname: string
    approver_lastname: string
    approver_email: string
}

export enum EChangeReqStatusTypes {
    pending = 'pending',
    approved = 'approved',
    rejected = 'rejected',
}

export enum EChangeReqActionTypes {
    Approve = 'approved',
    Reject = 'rejected',
}
