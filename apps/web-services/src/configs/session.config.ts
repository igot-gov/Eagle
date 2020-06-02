import cassandraDriver from 'cassandra-driver'
import cassandraStore from 'cassandra-store'
import expressSession from 'express-session'
import { CONSTANTS } from '../utils/env'

let sessionConfig: expressSession.SessionOptions

const cassandraClientOptions: cassandraDriver.ClientOptions = {
  contactPoints: [CONSTANTS.CASSANDRA_IP],
  keyspace: 'portal',
  queryOptions: {
    prepare: true,
  },
}
if (
  CONSTANTS.IS_CASSANDRA_AUTH_ENABLED &&
  CONSTANTS.CASSANDRA_USERNAME &&
  CONSTANTS.CASSANDRA_PASSWORD
) {
  cassandraClientOptions.authProvider = new cassandraDriver.auth.PlainTextAuthProvider(
    CONSTANTS.CASSANDRA_USERNAME,
    CONSTANTS.CASSANDRA_PASSWORD
  )
}

export function getSessionConfig(
  isPersistant = true
): expressSession.SessionOptions {
  if (!sessionConfig) {
    sessionConfig = {
      resave: false,
      saveUninitialized: false,
      secret: '927yen45-i8j6-78uj-y8j6g9rf56hu',
      store: isPersistant
        ? new cassandraStore({
          client: null,
          clientOptions: cassandraClientOptions,
          table: 'sessions',
        })
        : new expressSession.MemoryStore(),
    }
  }
  return sessionConfig
}
