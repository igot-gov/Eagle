const { getElasticSearchDbConnection } = require("../Utils/dbConnection");
const { index_names } = require("../Config/constants");


const contentAnalytics = async (req, res, term) => {
    try {
const  type={
'channel':'channel_id',
'knowledge-board':'knowledgeboard_ids',
'course':'content_id',
}

        filter = type[req.query.type] || 'content_id'
        let esClient = getElasticSearchDbConnection();
        let result = await esClient.search({
            index: `${req.client_name}_${index_names.hourly_feature_usage}`,
            type: "doc",
            body: {
                "size": 0,
                "query": {
                    "term": {
                        [filter]: {
                            "value": req.query.content_id
                        }
                    }
                }, "aggs": {
                    "contentFilter": {
                        "filters": {
                            "filters": {
                                "content": {
                                    "terms": {
                                        "feature.keyword": ["content-learning","browse"]
                                    }
                                }
                            }
                        },
                        "aggs": {
                            "unique": {
                                "cardinality": {
                                    "field": "user_id.keyword"
                                }
                            },
                            "department": {
                                "terms": {
                                    "field": "department_name.keyword",
                                    "size": 100
                                },
                                "aggs": {
                                    "unique": {
                                        "cardinality": {
                                            "field": "user_id.keyword"
                                        }
                                    }, "total_hits": {
                                        "sum": {
                                            "field": "count"
                                        }
                                    }
                                }
                            },
                            "city": {
                                "terms": {
                                    "field": "organization_location_city.keyword",
                                    "size": 100
                                },
                                "aggs": {
                                    "unique": {
                                        "cardinality": {
                                            "field": "user_id.keyword"
                                        }
                                    }, "total_hits": {
                                        "sum": {
                                            "field": "count"
                                        }
                                    }
                                }
                            },"country": {
                                "terms": {
                                    "field": "organization_location_country.keyword",
                                    "size": 100
                                },
                                "aggs": {
                                    "unique": {
                                        "cardinality": {
                                            "field": "user_id.keyword"
                                        }
                                    }, "total_hits": {
                                        "sum": {
                                            "field": "count"
                                        }
                                    }
                                }
                            }, "device": {
                                "terms": {
                                    "field": "device.keyword",
                                    "size": 10
                                },
                                "aggs": {
                                    "unique": {
                                        "cardinality": {
                                            "field": "user_id.keyword"
                                        }
                                    }, "total_hits": {
                                        "sum": {
                                            "field": "count"
                                        }
                                    }
                                }
                            },
                            "total_hits": {
                                "sum": {
                                    "field": "count"
                                }
                            }, "date_wise": {
                                "date_histogram": {
                                    "field": "date",
                                    "interval": "day"
                                },
                                "aggs": {
                                    "unique": {
                                        "cardinality": {
                                            "field": "user_id.keyword"
                                        }
                                    },"hits":{
                                        "sum": {
                                          "field": "count"
                                        }
                                      }
                                }
                            }
                        }
                    }
                }
            }

        });

        let result2 = await esClient.search({
            index: `${req.client_name}_${index_names.user_content_session}`,
            type: "doc",
            body: {
                "size": 0,
                "aggs": {
                    "avg_time_spent": {
                        "filter": {
                            "term": {
                                [filter]: req.query.content_id
                            }
                        },
                        "aggs": {
                            "users_count": {
                                "cardinality": {
                                    "field": "user_id.keyword"
                                }
                            },
                            "time": {
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
                            }
                        }
                    }
                }
            }

        });
        day_wise_users = result.aggregations.contentFilter.buckets.content.date_wise.buckets.map(q => {
            q.doc_count = q.unique.value;
            q.hits_count=q.hits.value;
            delete q.hits;
            delete q.unique;
            return q;
        })
        avg_time_spent = result2.aggregations.avg_time_spent.time.value / result2.aggregations.avg_time_spent.users_count.value;
        day_wise_time_spent= result2.aggregations.avg_time_spent.buckets;
        res.send({
            // day_wise_time_spent,
            day_wise_users,
            avg_time_spent,
            hits: result.aggregations.contentFilter.buckets.content.total_hits.value,
            userCount: result.aggregations.contentFilter.buckets.content.unique.value,
            city: result.aggregations.contentFilter.buckets.content.city.buckets.map(q => { return { "key": q.key, "doc_count": q.unique.value, "total_hits": q.total_hits.value } }).sort((a, b) => b.doc_count - a.doc_count),
            department: result.aggregations.contentFilter.buckets.content.department.buckets.map(q => { return { "key": q.key, "doc_count": q.unique.value, "total_hits": q.total_hits.value } }).sort((a, b) => b.doc_count - a.doc_count),
            device: result.aggregations.contentFilter.buckets.content.device.buckets.map(q => { return { "key": q.key, "doc_count": q.unique.value, "total_hits": q.total_hits.value } }).sort((a, b) => b.doc_count - a.doc_count),
            country: result.aggregations.contentFilter.buckets.content.country.buckets.map(q => { return { "key": q.key, "doc_count": q.unique.value, "total_hits": q.total_hits.value } }).sort((a, b) => b.doc_count - a.doc_count),

        })
    } catch (err) {
        console.log(err.toString());
    }
}



const contentViews = async (req, res) => {
    try {
      
        let esClient = getElasticSearchDbConnection();
        let result = await esClient.search({
            index: `${req.client_name}_${index_names.content_view_count}`,
            type: "doc",
            body: {
                "query": {"bool": {"must": [
                  {"terms": {
                    "content_id.keyword": req.body.contentId
                  }}
                ]}},"aggs": {
                  "content_id": {
                    "terms": {
                      "field": "content_id.keyword"
                    },"aggs": {
                      "views": {
                        "sum": {
                          "field": "views"
                        }
                      }
                    }
                  }
                }
              }
            })
content_views=result.aggregations.content_id.buckets.map(q=>{
    let a={};
    a.content_id=q.key;
    a.views=q.views.value;
    return a
});
res.send({content_views});
            } catch (err) {
                console.log(err.toString());
            }
        }
        
exports.contentAnalytics = contentAnalytics;
exports.contentViews = contentViews;