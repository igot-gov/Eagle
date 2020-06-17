exports.validateLexId = (value) => {
    var regex = /^\w{3}\_\d.*$/;
    return !regex.test(value);
}

exports.validateDateFormat = (value) => {
    var regex = /^\d{4}-\d{1,2}-\d{1,2}$/
    if (!regex.test(value)) {
        return true;
    }
    if (Number(value.split("-")[1]) < 1 || Number(value.split("-")[1]) > 12) {
        return true;
    }
    return false;
}

exports.validateNumber = (value) => {
    return Number(value) < 0 || value === undefined;
}

const { handleError } = require("../Utils/utilMethods");
const { getElasticSearchDbConnection } = require("../Utils/dbConnection");

