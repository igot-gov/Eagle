import chalk from 'chalk'

export const log = console.log // tslint:disable-line:no-console

type TObjectValueType = string | number | boolean | undefined | null
export function logObject(
  msgPrefix: string,
  obj: { [k: string]: TObjectValueType }
): void {
  const kv = Object.entries(obj)
    .map(([k, v]) => [k, String(v)])
    .sort((a, b) => a[0].localeCompare(b[0]))
  const padStart = Math.max(...kv.map(([k]) => k.length))
  const padEnd = Math.max(...kv.map(([, v]) => v.length))
  const msg = kv
    .map(([k, v]) => k.padStart(padStart) + ' : ' + v.padEnd(padEnd))
    .join('\n')
  logInfoHeading(msgPrefix)
  logInfo('_'.repeat(padStart + padEnd + 3))
  logInfo(msg)
}

export function logInfoHeading(msg: string) {
  log(chalk.bgBlue(msg))
}
export function logInfo(...msgs: string[]) {
  log(chalk.blue(...msgs))
}

export function logWarnHeading(msg: string) {
  log(chalk.bgYellow(msg))
}
export function logWarn(...msgs: string[]) {
  log(chalk.yellow(...msgs))
}

export function logErrorHeading(msg: string) {
  log(chalk.bgRed(msg))
}
export function logError(...msgs: string[]) {
  log(chalk.red(...msgs))
}

export function logSuccessHeading(msg: string) {
  log(chalk.bgGreen(msg))
}
export function logSuccess(...msgs: string[]) {
  log(chalk.green(...msgs))
}
