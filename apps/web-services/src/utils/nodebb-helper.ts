import { CONSTANTS } from './env'
import { logError } from './logger'

export function getWriteApiToken(): string {
    try {
        return `Bearer ${CONSTANTS.NODE_BB_WRITE_API_KEY}`
    } catch (err) {
        logError('Reading token from .env failed!')
        throw err
    }
}

export function getWriteApiUID(): number {
    try {
        return +CONSTANTS.NODE_BB_WRITE_API_UID
    } catch (err) {
        logError('Reading UID from .env failed!')
        throw err
    }
}
