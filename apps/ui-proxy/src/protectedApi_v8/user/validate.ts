import { Router } from 'express'
import {
  extractUserEmailFromRequest,
  extractUserIdFromRequest,
  extractUserNameFromRequest,
} from '../../utils/requestExtract'

export const validateApi = Router()

validateApi.get('/', async (req, res) => {
  const body = {
    email: extractUserEmailFromRequest(req) || 'user@demo.com',
    name: extractUserNameFromRequest(req) || 'demo user',
    userId: extractUserIdFromRequest(req) || 'user@demo.com',
  }

  res.send(body)
})
