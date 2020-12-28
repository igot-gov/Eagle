import { Router } from 'express'
import { categoriesApi } from './categories'
import { notificationsApi } from './notifications'
import { postsApi } from './posts'
import { tagsApi } from './tags'
import { topicsApi } from './topics'
import { usersApi } from './users'
import { writeApi } from './writeApi'

export const discussionHubApi = Router()

discussionHubApi.use('/posts', postsApi)
discussionHubApi.use('/topics', topicsApi)
discussionHubApi.use('/tags', tagsApi)
discussionHubApi.use('/categories', categoriesApi)
discussionHubApi.use('/notifications', notificationsApi)
discussionHubApi.use('/users', usersApi)
discussionHubApi.use('/writeApi/v2', writeApi)
