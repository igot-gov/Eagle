import axios from 'axios'
import { Router } from 'express'
import { axiosRequestConfig } from '../configs/request.config'
import { IFsData, IGroup, ILpData, INsoData, IOfferings, IProfile, IRole, IVariant } from '../models/navigator.model'
import { filterOnTopics, findRoleVariant, transformNsoData } from '../service/navigator'
import { appendProxiesUrl } from '../utils/contentHelpers'
import { CONSTANTS } from '../utils/env'
import { logError } from '../utils/logger'
import { ERROR } from '../utils/message'

const NAVIGATOR_BASE_API = `${CONSTANTS.WEB_HOST_PROXY}/web-hosted/navigator/json`
const API_END_POINTS = {
  accountsData: `${NAVIGATOR_BASE_API}/accounts_data.json`,
  bpmData: `${NAVIGATOR_BASE_API}/bpm_data.json`,
  commonGoalsData: `${NAVIGATOR_BASE_API}/common_goal_mapping.json`,
  commonsData: `${NAVIGATOR_BASE_API}/commonsdata.json`,
  deliveryPartnerData: `${NAVIGATOR_BASE_API}/dpn_data.json`,
  dmData: `${NAVIGATOR_BASE_API}/dmdata.json`,
  fullStackData: `${NAVIGATOR_BASE_API}/fsdata.json`,
  industriesData: `${NAVIGATOR_BASE_API}/industries_data.json`,
  learningPathData: `${NAVIGATOR_BASE_API}/data.json`,
  nsoData: `${NAVIGATOR_BASE_API}/nsodata.json`,
  subDomainsData: `${NAVIGATOR_BASE_API}/industries_subdomain.json`,
}

export const navigatorApi = Router()

navigatorApi.get('/roles', async (_req, res) => {
  try {
    const nsoData = await axios.get(API_END_POINTS.nsoData, axiosRequestConfig)
    const response = nsoData.data.nso_data.reduce(
      (map: { [key: string]: INsoData }, obj: INsoData) => {
        map[obj.arm_name] = transformNsoData(obj)
        return map
      },
      {}
    )
    res.json(processRolesData(response))
  } catch (err) {
    logError('ERR FETCHING NSODATA -> ', err)
    res.status((err && err.response && err.response.status) || 500).send(
      (err && err.response && err.response.data) || {
        error: 'Failed due to unknown reason',
      }
    )
  }
})

navigatorApi.get('/role/:roleId/:variantId', async (req, res) => {
  const [roleId, variantId] = [req.params.roleId, req.params.variantId]
  const nsoData = await axios.get(API_END_POINTS.nsoData, axiosRequestConfig)

  if (!nsoData.data) {
    res.status(nsoData.status).send({ error: 'Error fetching NSO data' })
  }
  const { roleVariant, error } = findRoleVariant(
    nsoData.data.nso_data,
    roleId,
    variantId
  )

  if (error) {
    res.status(404).send({ error })
  } else {
    if (roleVariant) {
      res.send(processVariant(roleVariant))
    }
  }
})

navigatorApi.get('/lp', async (req, res) => {
  const [pageNumber, pageSize, topics] = [
    Number(req.query.pageNumber) || 0,
    Number(req.query.pageSize) || 10000,
    req.query.topics ? req.query.topics.split(',') : [],
  ]
  if (isNaN(pageNumber) || isNaN(pageSize)) {
    res
      .status(400)
      .send({ error: 'Page number and Page size should be integers' })
  } else {
    const lpDataResponse = await axios.get(
      API_END_POINTS.learningPathData,
      axiosRequestConfig
    )
    const lpData =
      topics && topics.length
        ? filterOnTopics(lpDataResponse.data.lp_data, topics)
        : lpDataResponse.data.lp_data
    if (!lpData) {
      res
        .status(lpDataResponse.status)
        .send({ error: ERROR.fetchErrorLearningPaths })
    } else {
      const size = lpData.length
      const [start, end] = [pageSize * pageNumber, pageSize * (pageNumber + 1)]
      if (start >= size) {
        res.status(400).send({ error: 'Out of Range Error.' })
      } else {
        res.send(processAllLpData(lpData.slice(start, end)))
      }
    }
  }
})

navigatorApi.get('/lp/:lpId', async (req, res) => {
  const lpId = req.params.lpId

  const lpData = await axios.get(
    API_END_POINTS.learningPathData,
    axiosRequestConfig
  )
  if (!lpData.data) {
    res.status(lpData.status).send({ error: ERROR.fetchErrorLearningPaths })
  } else {
    const learningPath = lpData.data.lp_data.find(
      (lp: ILpData) => String(lp.lp_id) === lpId
    )
    if (learningPath) {
      res.send(processLpData(learningPath))
    } else {
      res.status(404).send({ error: 'Incorrect Learning Path Id' })
    }
  }
})

navigatorApi.get('/fp', async (req, res) => {
  const [pageNumber, pageSize] = [
    Number(req.query.pageNumber) || 0,
    Number(req.query.pageSize) || 10000,
  ]
  if (isNaN(pageNumber) || isNaN(pageSize)) {
    res
      .status(400)
      .send({ error: 'Page number and Page size should be integers' })
  } else {
    const fsData = await axios.get(
      API_END_POINTS.fullStackData,
      axiosRequestConfig
    )
    if (!fsData.data) {
      res
        .status(fsData.status)
        .send({ error: 'Error fetching fullstack data' })
    } else {
      const size = fsData.data.fs_data.length
      const [start, end] = [pageSize * pageNumber, pageSize * (pageNumber + 1)]
      if (start >= size) {
        res.status(400).send({ error: 'Out of Range Error.' })
      } else {
        res.send(processAllFsData(fsData.data.fs_data.slice(start, end)))
      }
    }
  }
})

navigatorApi.get('/topics', async (_req, res) => {
  const lpData = await axios.get(
    API_END_POINTS.learningPathData,
    axiosRequestConfig
  )
  if (!lpData.data) {
    res.status(lpData.status).send({ error: ERROR.fetchErrorLearningPaths })
  } else {
    const topics = new Set()
    lpData.data.lp_data.forEach((lp: ILpData) => {
      lp.profiles.forEach((profile: IProfile) => {
        profile.technology.forEach((technology: string) => {
          topics.add(technology.trim())
        })
      })
    })

    if (topics.size) {
      res.send(Array.from(topics))
    } else {
      res.status(204)
    }
  }
})

navigatorApi.get('/bpm', async (_req, res) => {
  const bpm = await axios.get(
    API_END_POINTS.bpmData,
    axiosRequestConfig
  )
  if (bpm) {
    res.send(bpm.data)
  } else {
    res.status(404).send({ message: 'Json not found' })
  }
})

// navigatorApi.get('/industries/:industryId', async (req, res) => {
//     const industryId = req.params.industryId
//     const industriesResponse = await axios.get(API_END_POINTS.industriesData, axiosRequestConfig)
//     con
// })

function processRolesData(roleOfferings: IOfferings): IOfferings {
  const accelerateData = roleOfferings.Accelerate
  const experienceData = roleOfferings.Experience
  const innovateData = roleOfferings.Innovate
  const insightData = roleOfferings.Insight
  const assureData = roleOfferings.Assure

  accelerateData.roles = processRolesArray(accelerateData.roles)
  experienceData.roles = processRolesArray(experienceData.roles)
  innovateData.roles = processRolesArray(innovateData.roles)
  insightData.roles = processRolesArray(insightData.roles)
  assureData.roles = processRolesArray(assureData.roles)

  return {
    Accelerate: accelerateData,
    Assure: assureData,
    Experience: experienceData,
    Innovate: innovateData,
    Insight: insightData,
  }
}

function processRolesArray(rolesArray: IRole[]): IRole[] {
  let count = 0
  rolesArray.forEach((role: IRole) => {
    const dataFetch = processRoles(role)
    rolesArray[count] = dataFetch
    count += 1
  })
  return rolesArray
}

function processRoles(role: IRole): IRole {
  return {
    ...role,
    courses_available: role.courses_available,
    role_description: role.role_description,
    role_id: role.role_id,
    role_image: appendProxiesUrl(role.role_image),
    role_name: role.role_name,
    roles_defined: role.roles_defined,
    variants: role.variants,
  }
}

function processVariant(variant: IVariant): IVariant {
  let count = 0
  if (variant && variant.group) {
    variant.group.forEach((element: IGroup) => {
      const dataChange = processGroup(element)
      variant.group[count] = dataChange
      count += 1
    })
  }
  return {
    ...variant,
    group: variant.group,
    variant_description: variant.variant_description,
    variant_id: variant.variant_id,
    variant_image: appendProxiesUrl(variant.variant_image),
    variant_name: variant.variant_name,
  }
}

function processGroup(group: IGroup): IGroup {
  return {
    ...group,
    certification_mandatory: group.certification_mandatory,
    group_member: group.group_member,
    lp_groupdesc: group.lp_groupdesc,
    lp_groupid: group.lp_groupid,
    lp_groupimage: appendProxiesUrl(group.lp_groupimage),
    lp_groupname: group.lp_groupname,
  }
}

function processLpData(lpData: ILpData): ILpData {
  return {
    ...lpData,
    capstone_description: lpData.capstone_description,

    linked_program: lpData.linked_program,

    lp_alternate: lpData.lp_alternate,
    lp_capstone: lpData.lp_capstone,
    lp_description: lpData.lp_description,
    lp_external_certification: lpData.lp_external_certification,
    lp_id: lpData.lp_id,
    lp_image: appendProxiesUrl(lpData.lp_image),
    lp_internal_certification: lpData.lp_internal_certification,
    lp_name: lpData.lp_name,
    lp_playground: lpData.lp_playground,
    lp_recommendation: lpData.lp_recommendation,
    profiles: lpData.profiles,
  }
}

function processAllLpData(lpData: ILpData[]): ILpData[] {
  let count = 0
  lpData.forEach((lp: ILpData) => {
    lpData[count] = processLpData(lp)
    count += 1
  })
  return lpData
}

function processFsData(fsData: IFsData): IFsData {
  return {
    ...fsData,
    fs_course: fsData.fs_course,
    fs_desc: fsData.fs_desc,
    fs_external_certification: fsData.fs_external_certification,
    fs_id: fsData.fs_id,
    fs_image: appendProxiesUrl(fsData.fs_image),
    fs_internal_certification: fsData.fs_internal_certification,
    fs_linked_program: fsData.fs_linked_program,
    fs_name: fsData.fs_name,
    fs_playground: fsData.fs_playground,
  }
}

function processAllFsData(fsData: IFsData[]): IFsData[] {
  let count = 0
  fsData.forEach((fs: IFsData) => {
    fsData[count] = processFsData(fs)
    count += 1
  })
  return fsData
}
