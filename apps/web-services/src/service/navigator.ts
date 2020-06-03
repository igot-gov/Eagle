import { ILpData, INsoData, IProfile, IRole } from '../models/navigator.model'

export function transformNsoData(nsoData: INsoData) {
  nsoData.roles = nsoData.roles.map((role) => {
    role.variants.forEach((variant) => {
      delete variant.variant_image
      delete variant.variant_description
      delete variant.group
      return variant
    })

    return role
  })

  return nsoData
}

export function findRoleVariant(nsoData: INsoData[], roleId: string, variantId: string) {
  let roles: IRole[] = []
  nsoData.forEach((arm) => {
    roles = roles.concat(arm.roles)
  })

  const currRole = roles.find((role) => role.role_id === roleId)
  if (currRole) {
    const currVariant = currRole.variants.find((variant) => variant.variant_id === variantId)
    if (currVariant) {
      return { roleVariant: currVariant, error: undefined }
    }

    return { roleVariant: undefined, error: 'Variant Id incorrect' }
  }

  return { roleVariant: undefined, error: 'Role Id incorrect' }
}

export function filterOnTopics(learningPaths: ILpData[], topics: string[]) {

  return learningPaths.filter((lp: ILpData) => {
    const allTech: string[] = []
    lp.profiles.forEach((profile: IProfile) => allTech.push(...profile.technology))
    return allTech.filter((value: string) => -1 !== topics.indexOf(value)).length > 0
  })
}
