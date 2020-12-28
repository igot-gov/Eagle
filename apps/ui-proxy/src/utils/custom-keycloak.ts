import * as express from 'express'
import expressSession from 'express-session'
import keycloakConnect from 'keycloak-connect'
import { getKeycloakConfig } from '../configs/keycloak.config'
import { CONSTANTS } from './env'
import { logInfo } from './logger'

const composable = require('composable-middleware')

export class CustomKeycloak {
  private multiTenantKeycloak = new Map<string, keycloakConnect>()

  constructor(sessionConfig: expressSession.SessionOptions) {
    if (CONSTANTS.MULTI_TENANT_KEYCLOAK) {
      CONSTANTS.MULTI_TENANT_KEYCLOAK.split(';').forEach((v: string) => {
        const domainUrlMap = v.split(',')
        this.multiTenantKeycloak.set(
          domainUrlMap[0],
          this.generateKeyCloak(sessionConfig, domainUrlMap[1], domainUrlMap[2])
        )
      })
    }
    this.multiTenantKeycloak.set('common', this.generateKeyCloak(sessionConfig))
  }

  middleware = (req: express.Request, res: express.Response, next: express.NextFunction) => {
    const keycloak = this.getKeyCloakObject(req)
    const middleware = composable(
      keycloak.middleware({
        admin: '/callback',
        logout: '/logout',
      })
    )
    middleware(req, res, next)
  }

  getKeyCloakObject(req: express.Request): keycloakConnect {
    const rootOrg =
      (req.headers ? req.header('rootOrg') : '') || (req.cookies ? req.cookies.rootorg : '')
    let domain = ''
    if (rootOrg) {
      this.multiTenantKeycloak.forEach((_value, key) => {
        if (key.toLowerCase().includes(rootOrg.toLowerCase())) {
          domain = key
        }
      })
    }
    return (this.multiTenantKeycloak.get(req.hostname) ||
      this.multiTenantKeycloak.get(domain) ||
      this.multiTenantKeycloak.get('common')) as keycloakConnect
  }

  authenticated = () => {
    logInfo(`${process.pid}: User authenticated`)
  }
  deauthenticated = () => {
    logInfo(`${process.pid}: User Deauthenticated`)
  }

  protect = (req: express.Request, res: express.Response, next: express.NextFunction) => {
    const keycloak = this.getKeyCloakObject(req)
    keycloak.protect()(req, res, next)
  }

  private generateKeyCloak(
    sessionConfig: expressSession.SessionOptions,
    url?: string,
    realm?: string
  ): keycloakConnect {
    const keycloak = new keycloakConnect(
      { store: sessionConfig.store },
      getKeycloakConfig(url, realm)
    )
    keycloak.authenticated = this.authenticated
    keycloak.deauthenticated = this.deauthenticated
    return keycloak
  }
}
