import { Router } from 'express'
import { bannerApi } from './banner'
import { userRegistrationApi } from './userRegistration'
import { userRolesApi } from './userRoles'

export const admin = Router()

admin.use('/userRegistration', userRegistrationApi)
admin.use('/banners', bannerApi)
admin.use('/userRoles', userRolesApi)
