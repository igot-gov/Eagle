import { Router } from 'express'
import { categoriesApi } from './categories'
import { notificationsApi } from './notifications'
import { postsApi } from './posts'
import { tagsApi } from './tags'
import { topicsApi } from './topics'
import { usersApi } from './users'
import { writeApi } from './writeApi'

export const nodebbApi = Router()

nodebbApi.use('/posts', postsApi)
nodebbApi.use('/topics', topicsApi)
nodebbApi.use('/tags', tagsApi)
nodebbApi.use('/categories', categoriesApi)
nodebbApi.use('/notifications', notificationsApi)
nodebbApi.use('/users', usersApi)
nodebbApi.use('/writeApi/v2', writeApi)
