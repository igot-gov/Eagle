import { CONSTANTS } from './env'
import { logError } from './logger'

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
