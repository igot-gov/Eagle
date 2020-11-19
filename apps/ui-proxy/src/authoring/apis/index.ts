import { Router } from 'express'
import { editorApi } from './editor'

export const api = Router()

api.use('/editor', editorApi)
