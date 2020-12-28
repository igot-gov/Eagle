export = CassandraStore
interface CassandraStoreOptions {
  clientOptions: any
  client: any
  table: any
}

declare class CassandraStore {
  clientOptions: any
  client: any
  table: any
  constructor(opts: CassandraStoreOptions, callback?: any);
  addListener(type: any, listener: any): any
  all(callback: any): void
  clear(callback: any): void
  createSession(req: any, sess: any): any
  destroy(sid: any, callback: any): void
  emit(type: any, args: any): any
  eventNames(): any
  get(sid: any, callback: any): void
  getMaxListeners(): any
  length(callback: any): void
  listenerCount(type: any): any
  listeners(type: any): any
  load(sid: any, fn: any): void
  off(type: any, listener: any): any
  on(type: any, listener: any): any
  once(type: any, listener: any): any
  prependListener(type: any, listener: any): any
  prependOnceListener(type: any, listener: any): any
  rawListeners(type: any): any
  regenerate(req: any, fn: any): void
  removeAllListeners(type: any, ...args: any[]): any
  removeListener(type: any, listener: any): any
  set(sid: any, sess: any, callback: any): void
  setMaxListeners(n: any): any
  touch(sid: any, session: any, callback: any): void
}
