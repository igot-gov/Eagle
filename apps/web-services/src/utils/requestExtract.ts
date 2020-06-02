import { Request } from 'express'
import uuid from 'uuid'
export interface IAuthorizedRequest extends Request {
  kauth?: {
    grant: {
      access_token: {
        token: string
        content: {
          given_name: string;
          family_name: string;
          sub: string;
          name: string;
          email?: string;
          preferred_username?: string;
          session_state: string;
        };
      };
    };
  }
}
export const extractUserIdFromRequest = (req: IAuthorizedRequest): string => {
  const wid = req.header('wid')
  if (wid) {
    return wid
  }
  return (req.kauth && req.kauth.grant.access_token.content.sub) as string
}
export const extractUserNameFromRequest = (req: IAuthorizedRequest) =>
  (req.kauth && req.kauth.grant.access_token.content.name) as string

export const extractUserEmailFromRequest = (req: IAuthorizedRequest) =>
  ((req.kauth && req.kauth.grant.access_token.content.email) ||
    (req.kauth &&
      req.kauth.grant.access_token.content.preferred_username)) as string

export const extractUserSessionState = (req: IAuthorizedRequest) =>
  (req.kauth && req.kauth.grant.access_token.content.session_state) as string

export const extractUserTokenContent = (req: IAuthorizedRequest) => {
  return req.kauth && req.kauth.grant.access_token.content
}

export const extractUserToken = (req: IAuthorizedRequest) => {
  return req.kauth && req.kauth.grant.access_token.token
}

export const getUUID = () => uuid.v1()
