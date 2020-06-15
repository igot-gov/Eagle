const fs = require("fs");
const fetch = require("node-fetch");

let logFileStream;

exports.handleError = (res, message) => {
    logWriter(`handle_error-${message}`);
    message='Error'
    res.status(404);
    res.send({ message });
}

const logWriter = (message) => {
    if (!logFileStream) {
        logFileStream = fs.createWriteStream("request-logs.log", { "flags": "a" });
    }
    logFileStream.write(`${message}-${new Date().toISOString()}\n`);
}

getUnique = (value, index, self) => {
    return self.indexOf(value) === index;
}



exports.writeLog = logWriter;
exports.getUnique = getUnique;
