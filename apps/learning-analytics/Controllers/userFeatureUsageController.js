const { handleError } = require("../Utils/utilMethods");
const constants = require("../Config/constants");
const { getElasticSearchDbConnection } = require("../Utils/dbConnection");
let feature_usage_types = [
    "navigator",
    "search",
    "onboarding",
    "feedback",
    "marketing",
    "radio",
    "tv",
    "from_leaders",
    "live",
    "tour"
];

exports.userFeatureUsage = async (req, res) => {
    try {
        let must = [
            {
                "term": {
                    "email_id.keyword": req.query.emailId
                }
            }
        ];
        if (req.query.startDate && req.query.endDate) {
            must.push(
                {
                    "range": {
                        "date_of_use": {
                            "gte": req.query.startDate,
                            "lte": req.query.endDate
                        }
                    }
                }
            )
        }
        let aggs = {};
        feature_usage_types.forEach(fut => aggs[`${fut}_count`] = { "sum": { "field": `${fut}_count` } });
        let esClient = getElasticSearchDbConnection(undefined);
        aggs["terms_type"] = {
            "terms": {
                "field": "type.keyword"
            },
            "aggs": {
                "type_top_hits": {
                    "top_hits": {
                        "size": 100,
                        "sort": [
                            {
                                "date_of_use": {
                                    "order": "desc"
                                }
                            }
                        ]
                    }
                }
            }
        };
        let result = await esClient.search({
            "index": constants.index_names.la_user_feature_usage,
            "type": "doc",
            body: {
                "sort": [
                    {
                        "date_of_use": {
                            "order": "desc"
                        }
                    }
                ],
                "size": 0,
                "query": {
                    "bool": {
                        must
                    }
                },
                aggs
            }
        });
        let expertsContacted = [];
        let resourcesShared = [];
        let playgroundUsed = [];
        let likes = [];
        result.aggregations.terms_type.buckets.forEach(b => {
            switch (b.key) {
                case "experts_contacted":
                    b.type_top_hits.hits.hits.forEach(h => {
                        expertsContacted.push(h._source);
                    });
                    break;
                case "artifact_share":
                    b.type_top_hits.hits.hits.forEach(h => {
                        resourcesShared.push(h._source);
                    });
                    break;
                case "like":
                    b.type_top_hits.hits.hits.forEach(h => {
                        likes.push(h._source);
                    });
                    break;
                case "playground":
                    b.type_top_hits.hits.hits.forEach(h => {
                        playgroundUsed.push(h._source);
                    });
                    break;
                default:
                    break;
            }
        });
        let featureUsage = [expertsContacted, resourcesShared, likes, playgroundUsed];
        delete result.aggregations.terms_type;
        let featureUsageCount = {};
        Object.keys(result.aggregations).map(k => featureUsageCount[k] = result.aggregations[k].value);
        featureUsage.push(featureUsageCount);
        return featureUsage;
    } catch (err) {
        handleError(res, err.toString());
    } finally { }
}