import { CONSTANTS } from '../utils/env'

export function getKeycloakConfig(url?: string, realm?: string) {
  return {
    bearerOnly: true,
    realm: realm ? realm : CONSTANTS.KEYCLOAK_REALM,
    resource: 'portal',
    serverUrl: url ? `${url}` : `${CONSTANTS.HTTPS_HOST}/auth`,
  }
}
