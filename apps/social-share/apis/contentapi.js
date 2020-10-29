var Promise = require("bluebird");
var request = require("request");

const getContent = (contentId) => {
  return new Promise(function (resolve, reject) {
    let URL = `http://lex-core:7001/v1/content/hierarchy/${contentId}?hierarchyType=minimal`;
    request(
      {
        headers: {
          "content-type": "application/json",
          rootorg: "igot",
          org: "dopt",
          wid: "9cdd9f76-2584-4ae5-940d-3f51dce020dc",
        },
        uri: URL,
        method: "POST",
        body: JSON.stringify({
          fetchOneLevel: false,
          fields: ["name", "description", "appIcon"],
          fieldsPassed: true,
          org: "dopt",
          rootOrg: "igot",
          userId: "9cdd9f76-2584-4ae5-940d-3f51dce020dc",
        }),
      },
      function (err, response, body) {
        if (err) {
          return reject(err);
        } else {
          return resolve(JSON.parse(body));
        }
      }
    );
  });
};

exports.getContent = getContent;
