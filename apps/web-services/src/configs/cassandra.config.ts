import cassandraDriver from 'cassandra-driver'
import { CONSTANTS } from '../utils/env'

export const cassandraClientOptions: cassandraDriver.ClientOptions = {
    contactPoints: [CONSTANTS.CASSANDRA_IP],
    keyspace: CONSTANTS.CASSANDRA_KEYSPACE,
    localDataCenter: 'datacenter1',
    queryOptions: {
        prepare: true,
    },
}
