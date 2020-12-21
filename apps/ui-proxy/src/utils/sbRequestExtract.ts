import { Request } from 'express'
import uuid from 'uuid'
export interface ISbAuthorizedRequest extends Request {
    kauth?: {
        grant: {
            access_token: {
                token: string
                content: {
                    phone: string;
                    phoneVerified: string;
                    emailVerified: string;
                    firstName: string;
                    lastName: string;
                    password: string;
                    channel: string;
                    userName: string;
                    email: string;
                    sub: string;
                    name: string;
                    session_state: string;
                    preferred_username?: string;

                };
            };
        };
    }
}
export const extractSbUserIdFromRequest = (req: ISbAuthorizedRequest): string => {
    const wid = req.header('wid')
    if (wid) {
        return wid
    }
    return (req.kauth && req.kauth.grant.access_token.content.sub) as string
}

export const extractSbAuthorizationFromRequest = (req: ISbAuthorizedRequest): string => {
    const authorization = req.header('Authorization')
    if (authorization) {
        return authorization
    }
    return (req.kauth && req.kauth.grant.access_token.content.sub) as string
}
export const extractSbUserTokenFromRequest = (req: ISbAuthorizedRequest): string => {
    const xAuthorization = req.header('X-Authenticated-User-Token')

    return xAuthorization as string

}

export const extractSbRootOrgNameFromRequest = (req: ISbAuthorizedRequest): string => {
    const rootOrgName = req.header('rootOrgName')

    return rootOrgName as string

}

export const extractSbUserNameFromRequest = (req: ISbAuthorizedRequest) =>
    (req.kauth && req.kauth.grant.access_token.content.name) as string

export const extractSbUserEmailFromRequest = (req: ISbAuthorizedRequest) =>
    ((req.kauth && req.kauth.grant.access_token.content.email) ||
        (req.kauth &&
            req.kauth.grant.access_token.content.preferred_username)) as string

export const extractSbUserSessionState = (req: ISbAuthorizedRequest) =>
    (req.kauth && req.kauth.grant.access_token.content.session_state) as string

export const extractSbUserTokenContent = (req: ISbAuthorizedRequest) => {
    return req.kauth && req.kauth.grant.access_token.content
}

export const extractSbUserToken = (req: ISbAuthorizedRequest) => {
    return req.kauth && req.kauth.grant.access_token.token
}

export const getUUID = () => uuid.v1()
