import axios from 'axios'
import { Router } from 'express'
import { axiosRequestConfig } from '../../configs/request.config'
import { IBadgeRecent, IBadgeResponse } from '../../models/badge.model'
import { IUserNotification, IUserNotifications } from '../../models/notification.model'
import { appendUrl } from '../../utils/contentHelpers'
import { CONSTANTS } from '../../utils/env'
import { extractUserIdFromRequest } from '../../utils/requestExtract'
const API_END_POINTS = {
  badge: `${CONSTANTS.SB_EXT_API_BASE_2}/v3/users`,
  newBadges: (userId: string) => `${CONSTANTS.SB_EXT_API_BASE_2}/v1/users/${userId}/badges`,
  updateBadge: `${CONSTANTS.SB_EXT_API_BASE_2}/v1/User`,
}

const GENERAL_ERROR_MSG = 'Failed due to unknown reason'

export const badgeApi = Router()
// all badges data
badgeApi.get('/', async (req, res) => {
  const userId = req.query.wid || extractUserIdFromRequest(req)
  const rootOrg = req.header('rootOrg')
  const langCode = req.header('locale')
  const url = `${API_END_POINTS.badge}/${userId}/badges`
  try {
    const response = await axios.get(url, {
      ...axiosRequestConfig,
      headers: { rootOrg, langCode },
    })
    res.send(processAllBadges(response.data))
  } catch (err) {
    return err
  }
})

badgeApi.get('/for/:wid', async (req, res) => {
  const rootOrg = req.header('rootOrg')
  const langCode = req.header('locale')
  const forId = req.params.wid
  const url = `${API_END_POINTS.badge}/${forId}/badges`
  try {
    const response = await axios.get(url, {
      ...axiosRequestConfig,
      headers: { rootOrg, langCode },
    })
    res.send(processAllBadges(response.data))
  } catch (err) {
    return err
  }
})

badgeApi.get('/badgeDetail', async (req, res) => {
  const userId = extractUserIdFromRequest(req)
  const rootOrg = req.header('rootOrg')
  const langCode = req.header('locale')
  const badgeIds = req.query('badgeIds')
  const url = `${API_END_POINTS.newBadges(userId)}/newUser/${badgeIds}`
  try {
    const response = await axios.get(url, {
      ...axiosRequestConfig,
      headers: { rootOrg, langCode },
    })
    res.send(processAllBadges(response.data))
  } catch (err) {
    return err
  }
})

badgeApi.post('/newUser', async (req, res) => {
  const userId = extractUserIdFromRequest(req)
  const rootOrg = req.header('rootOrg')
  const url = `${API_END_POINTS.newBadges(userId)}/newUser`
  try {
    const response = await axios.post(
      url,
      {},
      {
        ...axiosRequestConfig,
        headers: { rootOrg },
      }
    )

    res.json(response.data)
  } catch (err) {
    res.status(500).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})

badgeApi.post('/update', async (req, res) => {
  const userId = extractUserIdFromRequest(req)
  const rootOrg = req.header('rootOrg')
  const url = `${API_END_POINTS.updateBadge}/${userId}/recalculatebadges`
  try {
    const response = await axios.post(
      url,
      {},
      {
        ...axiosRequestConfig,
        headers: { rootOrg },
      }
    )

    res.json(response.data)
  } catch (err) {
    res.status(500).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})

// recent badges
badgeApi.get('/notification', async (req, res) => {
  const userId = extractUserIdFromRequest(req)
  const rootOrg = req.header('rootOrg')
  const langCode = req.header('locale')
  const url = `${API_END_POINTS.badge}/${userId}/achievements/recent`

  try {
    const response = await axios.get(url, {
      ...axiosRequestConfig,
      headers: { rootOrg, langCode },
    })
    let result: IUserNotifications = {
      recent_badge: null,
      totalPoints: [],
    }
    if (response.data && response.data.result) {
      result = processRecentBadges(response.data.result.response)
    }
    res.send(result)
  } catch (err) {
    res.status((err && err.response && err.response.status) || 500).send(
      (err && err.response && err.response.data) || {
        error: GENERAL_ERROR_MSG,
      }
    )
  }
})

function processRecentBadges(badges: IUserNotifications): IUserNotifications {
  let recentBadgeData = badges.recent_badge
  const totalPointsData = badges.totalPoints
  if (recentBadgeData) {
    recentBadgeData = processRecent(recentBadgeData)
  }
  return {
    recent_badge: recentBadgeData,
    totalPoints: totalPointsData,
  }
}

function processRecent(badge: IUserNotification): IUserNotification {
  return {
    ...badge,
    image: appendUrl(badge.image),
  }
}

function processAllBadges(badges: IBadgeResponse): IBadgeResponse {
  let earnedData = badges.earned
  let canEarnData = badges.canEarn
  let closeToEarningData = badges.closeToEarning
  const lastUpdatedDate = badges.lastUpdatedDate
  let recentData = badges.recent
  const totalPointsData = badges.totalPoints

  earnedData = processBadgeRecentArray(earnedData)
  canEarnData = processBadgeRecentArray(canEarnData)
  closeToEarningData = processBadgeRecentArray(closeToEarningData)
  recentData = processBadgeRecentArray(recentData)

  return {
    canEarn: canEarnData,
    closeToEarning: closeToEarningData,
    earned: earnedData,
    lastUpdatedDate,
    recent: recentData,
    totalPoints: totalPointsData,
  }
}

function processBadgeRecentArray(badges: IBadgeRecent[]): IBadgeRecent[] {
  let count = 0
  badges.forEach((badge: IBadgeRecent) => {
    const dataFetch = processBadgesRecent(badge)
    badges[count] = dataFetch
    count += 1
  })
  return badges
}

function processBadgesRecent(badge: IBadgeRecent): IBadgeRecent {
  if (!badge) {
    return badge
  }
  if (badge.image.startsWith('/assets/instances/')) {
    return badge
  }
  return {
    ...badge,
    image: appendUrl(badge.image),
  }
}
