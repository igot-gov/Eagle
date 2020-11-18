import { getUserByUsername } from '../protectedApi_v8/discussionHub/users'
import { CONSTANTS } from './env'
import { logError, logInfo } from './logger'
const usrNotFound = 'User not found'
export function getWriteApiToken(): string {
    try {
        return `Bearer ${CONSTANTS.DISCUSSION_HUB_WRITE_API_KEY}`
    } catch (err) {
        logError('Reading token from .env failed!')
        throw err
    }
}

export function getWriteApiAdminUID(): number {
    try {
        return +CONSTANTS.DISCUSSION_HUB_WRITE_API_UID
    } catch (err) {
        logError('Reading UID from .env failed!')
        throw err
    }
}

// tslint:disable-next-line: no-any
export async function getUserUID(wid: any) {
    // tslint:disable-next-line: no-any
    const userPresent = await getUserByUsername(wid).catch(async (_err: any) => {
        // If user is not already present in nodeBB NodeBB DiscussionHub
        logError(usrNotFound)
        return Promise.reject(new Error(usrNotFound))
    })
    if (userPresent && userPresent.uid) {
        logInfo('user found - uid: ', userPresent.uid)
        return Promise.resolve(userPresent.uid)
    }
}

// tslint:disable-next-line: no-any
export async function getUserSlug(wid: any) {
    // tslint:disable-next-line: no-any
    const userPresent = await getUserByUsername(wid).catch(async (_err: any) => {
        // If user is not already present in nodeBB NodeBB DiscussionHub
        logError(usrNotFound)
        return Promise.reject(new Error('User not found'))
    })
    if (userPresent && userPresent.userslug) {
        logInfo('user found - uid: ', userPresent.userslug)
        return Promise.resolve(userPresent.userslug)
    }
}
