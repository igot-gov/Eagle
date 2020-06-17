
const { Client, types } = require('pg');
var configs = require("../Config/keys");
const elasticsearch = require("elasticsearch")
exports.getMssqlConnection = (connectionString) => {
    const sqlConection = new sql.ConnectionPool(connectionString || configs.mssqlConfig);
    return sqlConection;
}

exports.getMssqlConnect = (connectionString) => {
    const sqlConection = new sql.connect(connectionString || configs.mssqlConfig);
    return sqlConection;
}



var clientEs;

var clientEs1;
exports.getElasticSearchDbConnection = (esDbConnection) => {
    // console.log(esDbConnection)
    if (esDbConnection) {
        console.log("here second client")
        if (clientEs1 === undefined || clientEs1 === null) {
            clientEs1 = new elasticsearch.Client(esDbConnection);
        }
        return clientEs1;
    }
    if (clientEs === undefined || clientEs === null) {
        console.log("here",clientEs);
        clientEs = new elasticsearch.Client(configs.esDbConnection);

    }
    // console.log(clientEs)
    return clientEs ;
    
}


exports.connectMssqlDb = () => {
    var config = {
        driver: 'msnodesqlv8',
        connectionString: "Driver={SQL Server Native Client 11.0};Server={blrkecetawsr30};Database={LA};Trusted_Connection={yes};"
    };
    const s = new winSql.ConnectionPool(config);
    return s;
}

let db_connection={};
// let clientPg;
exports.getPgConnection = async (db) => {
    console.log(db);
    let connectionString = configs.pg_connection_string+db;
    db_connection[db] = new Client(connectionString);
    db_connection[db].connect();
    return db_connection[db];
}
