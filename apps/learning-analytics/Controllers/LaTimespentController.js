const { getElasticSearchDbConnection } = require("../Utils/dbConnection");
const { index_names,hours,chartMapping } = require("../Config/constants");

const timeSpent = async (req, res) => {
  let term = [
    {
      "range": {
        "date": {
          "gte": req.query.startDate || "2018-01-01",
          "lte": req.query.endDate || new Date().toISOString().split("T")[0]
        }
      }
    }
  ];
  if (req.query.search_query && req.query.search_query != "") {
    term.push({
      "multi_match": {
        query: req.query.search_query,
        "fields": ["content_name", "content_id", "content_type", "device", "feature", "sub_feature", "page_id", "user_id", "accessed_language", "residence_country", "residence_city", "department_name", "unit_name", "organization_location_country", "organization_location_city", "channel_id", "channel_name", "keywords", "source_name"],
        "type": "cross_fields",
        "operator": "and"

      }
    });
  }

  if (req.query.content_id) {
    term.push({
      "multi_match": {
        query: req.query.content_id,
        "fields": ["content_id"]
      }
    });
  }
  // if (req.query.refinementfilter) {
  //   req.query.refinementfilter.split("$").map(s => {
  //     req.query[s.split(":")[0].replace(/"/g, "")] = s.split(":")[1].replace(/"|\[|\]/g, "");
  //   });
  // }
  try {
    if (req.query.filters) {
      let filter = eval(req.query.filters)
      term
      filter.map(q => {
        let key = chartMapping[`${q.key}`]
        term.push({
          "term": {
            [`${key}.keyword`]: q.value

          }
        })
      })
    }

    let esClient = getElasticSearchDbConnection();
    let result = await esClient.search({
      "index": `${req.client_name}_${index_names.user_content_session}`,
      "type": "doc",
      body: {
        "size": 0,
        "query": {
          "bool": {
            "must": term
          }
        },
        "aggs": {
          "sum_time_spent": {
            "sum": {
              "field": "learning_time"
            }
          },
          "max_date": {
            "max": {
              "field": "date"
            }
          },
          "content_users": {
            "cardinality": {
              "field": "user_id.keyword"
            }
          },
          "active_users": {
            "date_histogram": {
              "field": "date",
              "interval": "day"
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
              "avg_time_spent": {
                "bucket_script": {
                  "buckets_path": {
                    "total_time": "time",
                    "total_users": "users_count"
                  },
                  "script": "params.total_time / params.total_users"
                }
              }
            }
          }
        }
      }
    });
    let active_users = [];
    result.aggregations.active_users.buckets.map(q => {
      active = {};
      active["userCount"] = q.users_count.value;
      active["avg_time_spent"] = q.avg_time_spent ? q.avg_time_spent.value : 0;
      active["timespent"] = q.time.value;
      active["key"] = q.key;
      active["date"] = q.key_as_string;
      active_users.push(active);
    });
    let sum_time_spent = result.aggregations.sum_time_spent.value;
    // let content_users = result.aggregations.content_users.value;
    let max_date = result.aggregations.max_date.value_as_string;
    let { dailyNewUsers, weeklyNewUsers, monthlyNewUsers, totalUsers1, /* organizationLocationCity */ } = await tnc(req, res, term);
    // let {
    //   org_assessment_ranges, certification, assessment, playground,
    //   assessment_users, certification_users, playground_users
    // } = await assessmentData(req, res, [term[0]]);
    const { learning_history, learning_history_progress_range, course_accessed_count, collection_accessed_count, resource_accessed_count, content_source,content_type,/* resource_type */ } = await progress(req, res, term);
    let { all_content_count, course_count, collection_count, resource_count, knowledge_board_count, channel_count, learning_journey_count/* ,content_type,resource_type  */} = await content(req, res, term);
    const { channel_users, content_users, dailyContentUsers, num_of_hits, totalUsers, all_content_accessed, devices, organizationLocationCity, department, organizationLocationCountry, subDepartment,hourly_usage,dealer,daily_users,region,resource_type,mime_type,guild_group,role,device_category,zones,classifications } = await hourlyFeatureUsage(req, res, term);
    // const { user_goal, user_playlist, shared_goal, user_shared_playlist } = await goalPlaylist(req, res, term);
    // let avg_time_spent = sum_time_spent / content_users;
    let last_updated_on = max_date//new Date().getTime(); // result.aggregations.max_date.value;
    //  course_count=course_accessed_count;
    //  resource_count=resource_accessed_count;
    res.send({
      domain: req.get("host"),
      content_users, /* active_users, */ totalUsers, devices, organizationLocationCity, department, organizationLocationCountry, subDepartment,hourly_usage,dealer,

      dailyNewUsers, weeklyNewUsers, monthlyNewUsers,
      // avg_time_spent,
      last_updated_on,
      // assessment_users, org_assessment_ranges, certification, assessment, playground,
      learning_history, learning_history_progress_range, course_accessed_count, collection_accessed_count,
      resource_accessed_count, content_source,
      knowledge_board_count, channel_count, learning_journey_count, all_content_accessed,content_type,resource_type,region,
      all_content_count, course_count, resource_count, collection_count, num_of_hits, dailyContentUsers, channel_users,daily_users,mime_type,guild_group,role,device_category,zones,classifications
      //  certification_users, playground_users, user_goal, user_playlist, shared_goal, user_shared_playlist
    });
  }
  catch (err) {
    console.log(err.toString());
  }
}

const tnc = async (req, res, term) => {
  try {
    let esClient = getElasticSearchDbConnection();
    let result = await esClient.search({
      "index": `${req.client_name}_${index_names.user_tnc}`,
      "type": "doc",
      body: {
        "size": 0,
        "query": {
          "bool": {
            "must": term
          }
        },
        "aggs": {
          "onboarding_daily": {
            "date_histogram": {
              "field": "first_accessed_on",
              "interval": "day"
            }
          },
          "onboarding_week": {
            "date_histogram": {
              "field": "first_accessed_on",
              "interval": "week"
            }
          },
          "onboarding_monthly": {
            "date_histogram": {
              "field": "first_accessed_on",
              "interval": "month"
            }
          }, 'organization_location_city': {

            "terms": { "field": "organization_location_city.keyword", "size": 200 }
          }
        }
      }
    });
    let totalUsers = result.hits.total;
    let dailyNewUsers = result.aggregations.onboarding_daily.buckets;
    let weeklyNewUsers = result.aggregations.onboarding_week.buckets;
    let monthlyNewUsers = result.aggregations.onboarding_monthly.buckets;
    let organizationLocationCity = result.aggregations.organization_location_city.buckets;
    return { dailyNewUsers, weeklyNewUsers, monthlyNewUsers, totalUsers, organizationLocationCity };
  }
  catch (err) {
    console.log(err.toString());
  }
}

const assessmentData = async (req, res, term) => {
  try {
    let esClient = getElasticSearchDbConnection();
    let result = await esClient.search({
      "index": `${req.client_name}_${index_names.assessment_certification}`,
      "type": "doc",
      body: {
        "size": 0,
        "query": {
          "bool": {
            "must": term
          }
        },
        "aggs": {
          "count": {
            "terms": {
              "field": "type.keyword",
              "size": 10
            },
            "aggs": {
              "unique_users": {
                "cardinality": {
                  "field": "user_id.keyword"
                }
              }
            }
          },
          "assessment": {
            "terms": {
              "field": "content_name.keyword",
              "size": 10
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
              }
            }
          }
        }
      }
    });
    let collaborator = {
      assessment: 0, certification: 0, playground: 0,
      assessment_users: 0, certification_users: 0, playground_users: 0
    };
    // res.send(result);
    result.aggregations.count.buckets.forEach(q => {
      collaborator[q.key] = q.doc_count
      collaborator[`${q.key}_users`] = q.unique_users.value || 0
    });
    let org_assessment_ranges = result.aggregations.assessment.buckets.map(b => ({
      content_name: b.key,
      assessment_ranges: b.range.buckets
    }));
    let { certification, assessment, playground,
      assessment_users, certification_users, playground_users } = collaborator;
    return {
      org_assessment_ranges, certification, assessment, playground,
      assessment_users, certification_users, playground_users
    };
  }
  catch (err) {
    console.log(err.toString());
  }
}

const progress = async (req, res, term) => {
  try {
    // console.log(term);
    let esClient = getElasticSearchDbConnection();
    let result = await esClient.search({
      index: `${req.client_name}_${index_names.user_content_progress}`,
      type: "doc",
      body: {
        "size": 0,
        "query": {
          "bool": {
            "must": term,
            "must_not": [
              { "term": { "progress_source.keyword": "GLP" }}
            ]
          }
        },
        "aggs": {
          "content_accessed_by_type": {
            "terms": {
              "field": "content_type.keyword",
              "size": 10
            },
            "aggs": {
              "unique_content_id": {
                "cardinality": {
                  "field": "content_id.keyword"
                }
              }
            }
          },
          "content_accessed_by_source": {
            "terms": {
              "field": "source_name.keyword",
              "size": 10000
            },
            "aggs": {
              "unique": {
                "cardinality": {
                  "field": "user_id.keyword"
                }
              }
            }
          },
          "filter_content_type": {
            "filter": {
              "term": {
                "content_type.keyword": req.query.contentType || 'Resource'
              }
            },
            "aggs": {
              "content_progress_aggs": {
                "terms": {
                  "field": "content_id.keyword",
                  "size": 100
                },
                "aggs": {
                  "content_details": {
                    "top_hits": {
                      "_source": [
                        "content_id",
                        "content_name",
                        "content_type",
                        "source_name",
                        "is_external",
                        "content_url"
                      ],
                      "size": 1
                    }
                  },
                  "progress_ranges": {
                    "range": {
                      "field": "progress",
                      "ranges": [
                        {
                          "from": 0,
                          "to": 0.25
                        },
                        {
                          "from": 0.25,
                          "to": 0.5
                        },
                        {
                          "from": 0.5,
                          "to": 0.75
                        },
                        {
                          "from": 0.75,
                          "to": 1.1
                        }
                      ]
                    }
                  }
                }
              }
            }
          }, "content_type": {
            "terms": {
              "field": "content_type.keyword",
              "size": 10
            }, "aggs": {
              "unique": {
                "cardinality": {
                  "field": "user_id.keyword"
                }
              }
            }
          }, "resource_type": {
            "terms": {
              "field": "resource_type.keyword",
              "size": 10
            }, "aggs": {
              "unique": {
                "cardinality": {
                  "field": "user_id.keyword"
                }
              }
            }
          }
        }
      }
    });
    //  console.log(result.aggregations);
    let content_source = result.aggregations.content_accessed_by_source.buckets.map(q => { return { "key": q.key, "doc_count": q.unique.value } }).sort((a, b) => b.doc_count - a.doc_count)

    let learning_history_progress_range = {};
    let course_accessed_count = result.aggregations.content_accessed_by_type.buckets.find(b => b.key == "Course") && result.aggregations.content_accessed_by_type.buckets.find(b => b.key == "Course").unique_content_id.value || 0;
    let resource_accessed_count = result.aggregations.content_accessed_by_type.buckets.find(b => b.key == "Resource") && result.aggregations.content_accessed_by_type.buckets.find(b => b.key == "Resource").unique_content_id.value || 0;
    let collection_accessed_count = result.aggregations.content_accessed_by_type.buckets.find(b => b.key == "Module") && result.aggregations.content_accessed_by_type.buckets.find(b => b.key == "Module").unique_content_id.value || 0;
    let knowledge_board_count = result.aggregations.content_accessed_by_type.buckets.find(b => b.key == "Knowledge Board") && result.aggregations.content_accessed_by_type.buckets.find(b => b.key == "Knowledge Board").unique_content_id.value || 0;
    let learning_history = result.aggregations.filter_content_type.content_progress_aggs.buckets.map(b => {
      let obj = {};
      // obj.total_view=15;
      // obj.avg_time=1000;
      obj["num_of_users"] = b.doc_count;
      Object.keys(b.content_details.hits.hits[0]._source).map(m => obj[m] = b.content_details.hits.hits[0]._source[m]);
      learning_history_progress_range[b.key] = b.progress_ranges.buckets;
      return obj;
    });
    let content_type=result.aggregations.content_type.buckets.map(q => { return { "key": q.key, "doc_count": q.unique.value } }).sort((a, b) => b.doc_count - a.doc_count);
    let resource_type=result.aggregations.resource_type.buckets.map(q => { return { "key": q.key, "doc_count": q.unique.value } }).sort((a, b) => b.doc_count - a.doc_count);
    return {
      learning_history,
      learning_history_progress_range,
      course_accessed_count, resource_accessed_count, collection_accessed_count, knowledge_board_count,
      all_content_accessed: course_accessed_count + resource_accessed_count + collection_accessed_count + knowledge_board_count, content_source,content_type,resource_type
    };
  } catch (err) {
    console.log(err.toString());
  }
}

const content = async (req, res, term) => {
  try {
    let esClient = getElasticSearchDbConnection();
    let result = await esClient.search({
      index: `${req.client_name}_${index_names.resources}`,
      type: "doc",
      body: {
        "size": 0,
        "query": {
          "bool": {
            "must": term
          }
        },
        "aggs": {
          "content_count": {
            "terms": {
              "field": "content_type.keyword",
              "size": 10
            }
          },
          "resource_type": {
            "terms": {
              "field": "resource_type.keyword",
              "size": 10
            }
          }
        }
      }
    });
    // console.log(result.aggregations)
    let all_content_count = 0
    let course_count = 0
    let collection_count = 0
    let resource_count = 0
    let knowledge_board_count = 0
    let learning_journey_count = 0
    let channel_count = 0
    let content_type=[]
    let resource_type=[]
    if (result.hits.total > 0) {
      all_content_count = result.hits.total || 0;
      resource_type=result.aggregations.resource_type.buckets.map(q => { return { "key": q.key, "doc_count": q.doc_count } });
      content_type=result.aggregations.content_count.buckets.map(q => { return { "key": q.key, "doc_count": q.doc_count } });
        course_count = result.aggregations.content_count.buckets.filter(q => q.key == "Course")[0]?result.aggregations.content_count.buckets.filter(q => q.key == "Course")[0].doc_count : 0;
        collection_count = result.aggregations.content_count.buckets.filter(q => q.key == "Collection")[0] ? result.aggregations.content_count.buckets.filter(q => q.key == "Collection")[0].doc_count : 0;
        resource_count = result.aggregations.content_count.buckets.filter(q => q.key == "Resource")[0] ? result.aggregations.content_count.buckets.filter(q => q.key == "Resource")[0].doc_count : 0;
        knowledge_board_count = result.aggregations.content_count.buckets.filter(q => q.key == "Knowledge Board")[0] ? result.aggregations.content_count.buckets.filter(q => q.key == "Knowledge Board")[0].doc_count : 0;
        channel_count = result.aggregations.content_count.buckets.filter(q => q.key == "Channel")[0] ? result.aggregations.content_count.buckets.filter(q => q.key == "Channel")[0].doc_count : 0;
      }
      
    return {
      all_content_count,
      course_count,
      collection_count,
      resource_count,
      knowledge_board_count,
      channel_count,
      learning_journey_count,
      content_type,
      resource_type

    }
  } catch (err) {
    console.log(err.toString());
  }
}

const hourlyFeatureUsage = async (req, res, term) => {
  try {
    let esClient = getElasticSearchDbConnection();
    let result = await esClient.search({
      index: `${req.client_name}_${index_names.hourly_feature_usage}`,
      type: "doc",
      body: {
        "size": 0,
        "query": {
          "bool": {
            "must": term
          }
        },
        "post_filter": {
          "bool": {
            "must": [
              {
                "exists": {
                  "field": "content_id"
                }
              }
            ]
          }
        },
        "aggs": {
          "num_of_hits": {
            "sum": {
              "field": "count"
            }
          },
          "distinct_users": {
            "cardinality": {
              "field": "user_id.keyword"
            }
          }, "devices": {
            "terms": {
              "field": "device.keyword",
              "size": 10
            }, "aggs": {
              "unique": {
                "cardinality": {
                  "field": "user_id.keyword"
                }
              }
            }
          }, "hour": {
            "terms": {
              "field": "hour",
              "size": 100
            }, "aggs": {
              "unique": {
                "cardinality": {
                  "field": "user_id.keyword"
                }
              }
            }
          }, "organizationLocationCity": {
            "terms": {
              "field": "organization_location_city.keyword",
              "size": 10000
            }, "aggs": {
              "unique": {
                "cardinality": {
                  "field": "user_id.keyword"
                }
              }
            }
          }, "dealer": {
            "terms": {
              "field": "dealer_code.keyword",
              "size": 10000
            }, "aggs": {
              "unique": {
                "cardinality": {
                  "field": "user_id.keyword"
                }
              }
            }
          }, "region": {
            "terms": {
              "field": "region.keyword",
              "size": 10000
            }, "aggs": {
              "unique": {
                "cardinality": {
                  "field": "user_id.keyword"
                }
              }
            }
          },"device_category": {
            "terms": {
              "field": "device_category.keyword",
              "size": 10000
            }, "aggs": {
              "unique": {
                "cardinality": {
                  "field": "user_id.keyword"
                }
              }
            }
          }, "guild_group": {
            "terms": {
              "field": "Guild_Group_or Tier Classification.keyword",
              "size": 10000
            }, "aggs": {
              "unique": {
                "cardinality": {
                  "field": "user_id.keyword"
                }
              }
            }
          }, "role": {
            "terms": {
              "field": "job_title.keyword",
              "size": 10000
            }, "aggs": {
              "unique": {
                "cardinality": {
                  "field": "user_id.keyword"
                }
              }
            }
          }
          , "organizationLocationCountry": {
            "terms": {
              "field": "organization_location_country.keyword",
              "size": 10000
            }, "aggs": {
              "unique": {
                "cardinality": {
                  "field": "user_id.keyword"
                }
              }
            }
          }
          , "department": {
            "terms": {
              "field": "department_name.keyword",
              "size": 10000
            }, "aggs": {
              "unique": {
                "cardinality": {
                  "field": "user_id.keyword"
                }
              }
            }
          }
          , "sub_department": {
            "terms": {
              "field": "sub_department_name.keyword",
              "size": 10000
            }, "aggs": {
              "unique": {
                "cardinality": {
                  "field": "user_id.keyword"
                }
              }
            }
          } , "zone": {
            "terms": {
              "field": "zone.keyword",
              "size": 10000
            }, "aggs": {
              "unique": {
                "cardinality": {
                  "field": "user_id.keyword"
                }
              }
            }}, "classification": {
              "terms": {
                "field": "classification.keyword",
                "size": 10000
              }, "aggs": {
                "unique": {
                  "cardinality": {
                    "field": "user_id.keyword"
                  }
                }
              }}
          , "resource_type": {
            "terms": {
                "field": "resource_type.keyword",
                "size": 1000
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
        }, "mime_type": {
          "terms": {
              "field": "mime_type.keyword",
              "size": 1000
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
      }
          , "content_users": {
            "filter": {
              "term": {
                "feature.keyword": "content-learning"
              }
            }, "aggs": {
              "day_wise": {
                "date_histogram": {
                  "field": "date",
                  "interval": "day"
                }, "aggs": {
                  "unique": {
                    "cardinality": {
                      "field": "user_id.keyword"
                    }
                  }
                }
              }, "total_users": {
                "cardinality": {
                  "field": "user_id.keyword"
                }
              }
            }
          }, 
              "daily_users": {
                "date_histogram": {
                  "field": "date",
                  "interval": "day"
                }, "aggs": {
                  "unique": {
                    "cardinality": {
                      "field": "user_id.keyword"
                    }
                  }
                }
              
            
          }, "channel_users": {
            "filters": {
              "filters": {
                "channel": {
                  "term": {
                    "sub_feature.keyword": "channel"
                  }
                }
              }
            }, "aggs": {
              "channel": {
                "terms": {
                  "field": "content_name.keyword",
                  "size": 10000
                }, "aggs": {
                  "unique": {
                    "cardinality": {
                      "field": "user_id.keyword"
                    }
                  }
                }
              }
            }
          }
        }
      }
    });
    // console.log(result.aggregations.organizationLocationCountry)
    let organizationLocationCity = result.aggregations.organizationLocationCity.buckets.map(q => { return { "key": q.key, "doc_count": q.unique.value } }).sort((a, b) => a.doc_count - b.doc_count);
    let region = result.aggregations.region.buckets.map(q => { return { "key": q.key, "doc_count": q.unique.value } }).sort((a, b) => b.doc_count - a.doc_count);
    let dealer = result.aggregations.dealer.buckets.map(q => { return { "key": q.key, "doc_count": q.unique.value } }).sort((a, b) => b.doc_count - a.doc_count);
    let mime_type= result.aggregations.mime_type.buckets.map(q => { return { "key": q.key, "doc_count": q.unique.value, "total_hits": q.total_hits.value } }).sort((a, b) => b.doc_count - a.doc_count);
    let role = result.aggregations.role.buckets.map(q => { return { "key": q.key, "doc_count": q.unique.value } }).sort((a, b) => b.doc_count - a.doc_count);
    
    let device_category = result.aggregations.device_category.buckets.map(q => { return { "key": q.key, "doc_count": q.unique.value } }).sort((a, b) => b.doc_count - a.doc_count);

    let zones = result.aggregations.zone.buckets.map(q => { return { "key": q.key, "doc_count": q.unique.value } }).sort((a, b) => b.doc_count - a.doc_count);

    let classifications = result.aggregations.classification.buckets.map(q => { return { "key": q.key, "doc_count": q.unique.value } }).sort((a, b) => b.doc_count - a.doc_count);

    let guild_group = result.aggregations.guild_group.buckets.map(q => { return { "key": q.key, "doc_count": q.unique.value } }).sort((a, b) => b.doc_count - a.doc_count);
    let organizationLocationCountry = result.aggregations.organizationLocationCountry.buckets.map(q => { return { "key": q.key, "doc_count": q.unique.value } })
    organizationLocationCity.sort((a, b) => b.doc_count - a.doc_count)
    organizationLocationCity = organizationLocationCity.filter(q => q.key != 'NULL');
    organizationLocationCountry.sort((a, b) => b.doc_count - a.doc_count)
    organizationLocationCountry = organizationLocationCountry.filter(q => q.key != 'NULL');
    hourly_usage= result.aggregations.hour.buckets.map(q => { return { "key": q.key.toString(), "doc_count": q.unique.value } }).sort((a, b) => a.key - b.key);
 
    hours.map(q=>{if(hourly_usage.filter(w=>w.key==q).length==0)hourly_usage.push({ "key": q.toString(), "doc_count": 0 })})
    hourly_usage.sort((a, b) => a.key - b.key)
    return {
      channel_users: result.aggregations.channel_users.buckets.channel.channel.buckets.map(q => {
        q.doc_count = q.unique.value;
        delete q.unique;
        return q;
      }),
      content_users: result.aggregations.content_users.total_users.value,
      dailyContentUsers: result.aggregations.content_users.day_wise.buckets.map(q => {
        q.doc_count = q.unique.value;
        delete q.unique;
        return q;
      }),
     
      num_of_hits: result.aggregations.num_of_hits.value,
      all_content_accessed: result.hits.total,
      totalUsers: result.aggregations.distinct_users.value,
      devices: result.aggregations.devices.buckets.map(q => { return { "key": q.key, "doc_count": q.unique.value } }).sort((a, b) => b.doc_count - a.doc_count),
      // country: result.aggregations.country.buckets.map(q => { return { "key": q.key, "doc_count": q.unique.value } }),

      // subDepartment: result.aggregations.sub_department.buckets.map(q => { return { "key": q.key, "doc_count": q.unique.value } }),


      organizationLocationCity,
      department: result.aggregations.department.buckets.map(q => { return { "key": q.key, "doc_count": q.unique.value } }).sort((a, b) => b.doc_count - a.doc_count),
      organizationLocationCountry,
      subDepartment: result.aggregations.sub_department.buckets.map(q => { return { "key": q.key, "doc_count": q.unique.value } }).sort((a, b) => b.doc_count - a.doc_count),
      hourly_usage ,
      dealer,
      daily_users: result.aggregations.daily_users.buckets.map(q => {  q.doc_count = q.unique.value;
        delete q.unique;
        return q;}),
        region,
        resource_type: result.aggregations.resource_type.buckets.map(q => { return { "key": q.key, "doc_count": q.unique.value, "total_hits": q.total_hits.value } }).sort((a, b) => b.doc_count - a.doc_count),
        mime_type,guild_group,role,device_category,zones,classifications

    }
  } catch (err) {
    console.log(err.toString());
  }
}



exports.tnc = tnc;
exports.timeSpent = timeSpent;
exports.content = content;
exports.progress = progress;
exports.assessmentData = assessmentData;
exports.hourlyFeatureUsage = hourlyFeatureUsage;
// exports.goalPlaylist = goalPlaylist;