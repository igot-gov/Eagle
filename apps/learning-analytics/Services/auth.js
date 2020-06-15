const fetch = require("node-fetch");
const { getElasticSearchDbConnection } = require("../Utils/dbConnection");
const { writeLog } = require("../Utils/utilMethods");
const constants = require("../Config/constants");



exports.auth = async (req, res, next) => {
    const { request_id } = req;
    writeLog(`${request_id}-request_for:${req.originalUrl}-auth_started-i`);
    try {
        if (!req.get("Authorization")) throw "unauthorised";
        let responseData = await getEmployee(req.get("Authorization"), req.get("env") || req.get("host"));
        writeLog(`${request_id}-request_for:${req.originalUrl}-auth_success for ${req.get('wid')} having token as ${req.get("Authorization")}-i-${req.get("host")}`);
       console.log("authenticated")
        next();
    } catch (err) {
        writeLog(`${request_id}-auth_failed-request_for:${req.originalUrl}-e-${err.toString()}-having token as ${req.get("Authorization")}`);
        console.log(err.toString());
        res.status(401);
        res.send("unauthorized");
    }
}

const getEmployee = async (token, env) => {
    // let url = `https://${env}/clientApi/v2/user/validate`;
    let url=`https://${env}/apis/protected/v8/user/validate`
    let response = await fetch(`${url}`, {
        method: "GET",
        "rejectUnauthorized": false,
        headers: {
            "Authorization": `${token}`
        }
    });
    let responseJson = await response.json();
    return responseJson;
}

const fetchEmployee = async (id) => {
    let esClient = getElasticSearchDbConnection(undefined);
    let employee = await esClient.get({
        index: constants.index_names.tnc,
        type: 'doc',
        id
    });
    return employee._source;
}
