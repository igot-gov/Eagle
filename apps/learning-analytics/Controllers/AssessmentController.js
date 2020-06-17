const { handleError } = require("../Utils/utilMethods");
const constants = require("../Config/constants");
const { getElasticSearchDbConnection } = require("../Utils/dbConnection");

exports.assessment = async (req, res) => {
    try {
        let esClient = getElasticSearchDbConnection(undefined);
        let result = await esClient.search({
            "index": constants.index_names.la_self_assessment,
            "type": "doc",
            body: {
                "_source": [
                    "lex_id",
                    "type",
                    "content_name",
                    "assessment_score",
                    "assessment_date",
                    "min_score",
                    "max_score",
                    "percentile",
                    "processed_assessment_score"
                ],
                "sort": [
                    {
                        "assessment_date": {
                            "order": "desc"
                        }
                    }
                ],
                "size": 10000,
                "query": {
                    "bool": {
                        "must": [
                            {
                                "terms": {
                                    "email_id": [
                                        req.query.emailId
                                    ]
                                }
                            },
                            {
                                "range": {
                                    "assessment_date": {
                                        "gte": req.query.startDate,
                                        "lte": req.query.endDate
                                    }
                                }
                            }
                        ]
                    }
                },
                "aggs": {
                    "global_filter": {
                        "global": {},
                        "aggs": {
                            "filter_by_assessment": {
                                "filter": {
                                    "term": {
                                        "type": "assessment"
                                    }
                                },
                                "aggs": {
                                    "unq_users": {
                                        "cardinality": {
                                            "field": "email_id.keyword"
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });
        let hits = result.hits.hits;
        let unq_users = result.aggregations.global_filter.filter_by_assessment.unq_users.value;
        let total_assessments_taken = result.aggregations.global_filter.filter_by_assessment.doc_count;
        let avg_assessments_count_org_wide = Math.ceil(total_assessments_taken / unq_users);
        let lex_assessment_id = [];
        let certification_list = [];
        let certification_id_list = [];
        hits.map(h => {
            if (h._source.type == "assessment") {
                lex_assessment_id.push(h._source.lex_id);
            } else {
                h._source["assessment_score"] = h._source["processed_assessment_score"];
                delete h._source["processed_assessment_score"];
                certification_list.push(h._source);
                certification_id_list.push(h._source.lex_id);
            }
        });
        let percentiles = await esClient.search({
            "index": constants.index_names.la_self_assessment,
            "type": "doc",
            body: {
                "size": 0,
                "query": {
                    "bool": {
                        "must": [
                            {
                                "terms": {
                                    "lex_id.keyword": lex_assessment_id.concat(certification_id_list)
                                }
                            }
                        ]
                    }
                },
                "aggs": {
                    "lex_id_terms": {
                        "terms": {
                            "field": "lex_id.keyword",
                            "size": 10000
                        },
                        "aggs": {
                            "range": {
                                "range": {
                                    "field": "assessment_score",
                                    "ranges": [
                                        {
                                            "from": 0,
                                            "to": 25
                                        },
                                        {
                                            "from": 25,
                                            "to": 50
                                        },
                                        {
                                            "from": 50,
                                            "to": 75
                                        },
                                        {
                                            "from": 75,
                                            "to": 100
                                        }
                                    ]
                                }
                            },
                            "assessment_content_name": {
                                "top_hits": {
                                    "_source": {
                                        "includes": ["content_name", "type"]
                                    },
                                    "size": 1
                                }
                            }
                        }
                    }
                }
            }
        });
        let records = result.hits.hits;
        let max_score = {};
        let min_score = {};
        let percentile = {};
        records.map(r => {
            max_score[r._source.lex_id] = r._source.max_score;
            min_score[r._source.lex_id] = r._source.min_score;
            percentile[r._source.lex_id] = r._source.percentile;
        })
        let assessment = [];
        for (let i = 0; i < hits.length; i++) {
            if (hits[i]._source.type == "assessment") {
                assessment.push(hits[i]._source);
            }
        }
        let certification_range_scores = percentiles.aggregations.lex_id_terms.buckets;
        let assessment_score_ranges = {};
        certification_range_scores.map(crs => {
            assessment_score_ranges[crs.key] = {};
            assessment_score_ranges[crs.key]["content_name"] = crs.assessment_content_name.hits.hits[0]._source.content_name || "";
            assessment_score_ranges[crs.key]["type"] = crs.assessment_content_name.hits.hits[0]._source.type;
            if (assessment_score_ranges[crs.key]["type"] == "certification") {
                assessment_score_ranges[crs.key]["count"] = crs.doc_count;
            }
            assessment_score_ranges[crs.key]["25"] = crs.range.buckets[0].doc_count;
            assessment_score_ranges[crs.key]["50"] = crs.range.buckets[1].doc_count;
            assessment_score_ranges[crs.key]["75"] = crs.range.buckets[2].doc_count;
            assessment_score_ranges[crs.key]["100"] = crs.range.buckets[3].doc_count;
        });
        let user_assessment_count_vs_org_wide = ((assessment.length / avg_assessments_count_org_wide) * 100) - 100
        res.send({ user_assessment_count_vs_org_wide, certification_list, assessment, certifications_count: certification_list.length, max_score, min_score, percentile, assessment_score_ranges });
    }
    catch (err) {
        handleError(res, err.toString());
    }
    finally {
    }
}