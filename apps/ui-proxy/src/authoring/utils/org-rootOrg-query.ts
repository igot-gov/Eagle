export function setOrgRootOrgAsQuery(url: string, org: string, rootOrg: string): string {
  return `${url}${url.includes('?') ? '&' : '?'}org=${org}&rootOrg=${rootOrg}`
}
