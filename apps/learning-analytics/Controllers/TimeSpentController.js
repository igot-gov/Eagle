const { handleError } = require("../Utils/utilMethods");
const constants = require("../Config/constants");
const { getElasticSearchDbConnection } = require("../Utils/dbConnection");

// const { badges } = require("./BadgesController");
const { pointsAndRank } = require("./PointsAndRankController");
const { trackTimeSpent } = require("./TrackTimeSpentController");

exports.timeSpent = async (req, res) => {
    try {
        let resObj = {};
        let callback = (key, value) => {
           
            resObj[key] = value;
            console.log(Object.keys(resObj).length);
            if (Object.keys(resObj).length == 1) {
                sendResponse(req, res, resObj);
            }
        }
        timespentData(req, res, callback);
        // trackTimeSpent(req, res, callback);
        // badges(req, res, callback);
        // pointsAndRank(req, res, callback);
        // fetchOrgWideStats(callback);
    }
    catch (err) {
        handleError(res, err.toString());
    }
}

const processFilters = (req, res) => {
    let processedFilters = [];
    if (req.query.refinementFilter) {
        let filters = req.query.refinementFilter.split("$");
        for (let i = 0; i < filters.length; i++) {
            let terms = {};
            let kv = filters[i].split(":");
            terms[kv[0].replace(/\'/g, '')] = kv[1].replace(/\'/g, "").replace("[", "").replace("]", "").split(",");
            processedFilters.push({ terms });
        }
    }
    return processedFilters;
}

const timespentData = async (req, res, callback) => {
    let esClient = getElasticSearchDbConnection(undefined);
    try {
        let must = [
            {
                "terms": {
                    "user_id.keyword": [req.get("wid")]
                }
            },
            {
                "range": {
                    "date": {
                        "gte": req.query.startDate,
                        "lte": req.query.endDate
                    }
                }
            }
        ];
        let filters = processFilters(req, res);
        filters.map((f) => must.push(f));
        let aggsFilters = Array.from(must);
        aggsFilters.splice(0, 1);
        
        let result = await esClient.search({
              index: `${req.client_name}_${constants.index_names.user_content_session}`,
            body: {
                "size": 0,
                "query": {
                    "bool": {
                        must
                    }
                },
                "aggs": {
                    
                    "time_spent_by_user": {
                        "sum": {
                            "field": "learning_time"
                        }
                    },
                    "date_wise": {
                        "date_histogram": {
                            "field": "date",
                            "interval": "day"
                        },
                        "aggs": {
                            "date_wise_timespent": {
                                "sum": {
                                    "field": "learning_time"
                                }
                            }
                        }
                    },
                    "org_wide": {
                        "global": {},
                        "aggs": {
                            "filters": {
                                "filter": {
                                    "bool": {
                                        must: aggsFilters
                                    }
                                },
                                "aggs": {
                                    "unq_users": {
                                        "cardinality": {
                                            "field": "user_id.keyword"
                                        }
                                    },
                                    "total_timespent": {
                                        "sum": {
                                            "field": "learning_time"
                                        }
                                    }
                            }
                        }
                    }
                }}
            }
        });
       
        callback("result", result);
        // return result;
    } catch (err) {
        console.log(err);
        throw err;
    }
}

const fetchOrgWideStats = async (callback) => {
    console.log("org_wide_start", new Date().toISOString());
    let esClient = getElasticSearchDbConnection(undefined);
    try {
        let result = await esClient.get({
            index: constants.index_names.la_org_wide_daily_stats,
            type: "doc",
            id: 1
        });
        console.log("org_wide_end", new Date().toISOString());
        callback("orgWideStats", result._source);
    } catch (err) {
        console.log(err);
        throw err;
    }
}

const sendResponse = (req, res, { track_wise_user_timespent, badges_earned, result, orgWideStats, points_and_ranks }) => {
    // let total_badges_earned = badges_earned[0];
    // let badges_details = badges_earned[2];
    let date_wise = [];
    // let category_wise = [];
    // let JL_wise = [];
    // let unit_wise = [];
    console.log(result.aggregations.date_wise.buckets)
    let data = result.aggregations.date_wise.buckets;
    // let category = result.aggregations.categoryTimeSpent.buckets;
    // let jltimespent = result.aggregations.org_wide.filters.JLTimeSpent.category.buckets;
    // let unitTimeSpent = result.aggregations.org_wide.filters.unitTimeSpent.category.buckets;
    let org_wide_category_time_spent_kv = {};
    let last_updated_on=new Date().toISOString().split("T")[0]

    // let last_updated_on = orgWideStats.last_updated_on.value_as_string;
    // let unquie_users = result.aggregations.org_wide.unq_users.value;
    //calculating org wide avg category wise
    // orgWideStats.org_wide_category_time_spent.buckets.forEach(ts => {
        // org_wide_category_time_spent_kv[ts.key] = ts.time_spent.value / ts.unique_user.value;
    // // });
    // for (let i = 0; i < unitTimeSpent.length; i++) {
    //     let a = {}
    //     a["key"] = unitTimeSpent[i]["key"];
    //     a["value"] = unitTimeSpent[i]["timeSpent"].value;
    //     unit_wise.push(a);
    // }
    // for (let i = 0; i < jltimespent.length; i++) {
    //     let a = {}
    //     a["key"] = jltimespent[i]["key"];
    //     a["value"] = jltimespent[i]["timeSpent"].value;
    //     JL_wise.push(a);
    // }
    // let category_wise_kv = {};
    // for (let i = 0; i < category.length; i++) {
    //     category_wise_kv[category[i]["key"]] = category[i]["timespent"].value;
    // }
    // let user_category_wise_time_spent = [];
    // let org_wide_category_time_spent = [];
    //category wise time spent by user and org wide
    // constants.category.map(c => {
    //     if (category_wise_kv[c]) {
    //         user_category_wise_time_spent.push({ key: c, value: c == "Technology" ? category_wise_kv[c] / 10 : category_wise_kv[c] });
    //     } else if (c == "Marketing and Sales") {
    //         user_category_wise_time_spent.push({ key: c, value: category_wise_kv["Sales"] || 0 + category_wise_kv["Marketing"] || 0 })
    //     } else {
    //         user_category_wise_time_spent.push({ key: c, value: 0 })
    //     }

    //     if (org_wide_category_time_spent_kv[c]) {
    //         org_wide_category_time_spent.push({ key: c, value: org_wide_category_time_spent_kv[c] })
    //     } else if (c == "Marketing and Sales") {
    //         org_wide_category_time_spent.push({ key: c, value: org_wide_category_time_spent_kv["Sales"] || 0 + org_wide_category_time_spent_kv["Marketing"] || 0 })
    //     } else {
    //         org_wide_category_time_spent.push({ key: c, value: 0 })
    //     }
    // });
    // category_wise = user_category_wise_time_spent;
    
    for (let i = 0; i < data.length; i++) {
        let d = {}
        d["key"] = data[i]["key"];
        d["value"] = data[i]["date_wise_timespent"].value;
        date_wise.push(d);
    }
    // let avg_time_spent_org_wide = Math.ceil(result.aggregations.org_wide.filters.total_timespent.value / result.aggregations.org_wide.filters.unq_users.value);
    // let timespent_user_vs_org_wide = {
    //     time_spent_by_user: result.aggregations.time_spent_by_user.value,
    //     usage_percent: ((result.aggregations.time_spent_by_user.value / avg_time_spent_org_wide) * 100) - 100
    // }
    console.log(req.query.endDate)
    date_wise.sort((a, b) => a.key - b.key);
    let end_date = Math.round((new Date(new Date(req.query.endDate) > new Date(last_updated_on) ? last_updated_on : req.query.endDate)).getTime());
    let max_date = date_wise[date_wise.length - 1] ? date_wise[date_wise.length - 1].key + 86400000
        : Math.round((new Date(req.query.startDate)).getTime());
    let next_date = max_date;
    while (true) {
        if (next_date > end_date) break;
        date_wise.push({ key: next_date, value: 0 });
        next_date += 86400000;
    }
    res.send({
        // last_updated_on,
        // badges_details,
        // track_wise_user_timespent,
        // unit_wise,
        // JL_wise, //jl data
        // category_wise,
        date_wise,
        time_spent_by_user: result.aggregations.time_spent_by_user.value,
        // org_wide_avg_time_spent: avg_time_spent_org_wide ? avg_time_spent_org_wide : 0,
        // total_badges_earned,
        // points_and_ranks,
        // timespent_user_vs_org_wide,
        // org_wide_category_time_spent
    });
}