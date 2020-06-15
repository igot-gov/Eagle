const { handleError } = require("../Utils/utilMethods");
const constants = require("../Config/constants");
const { getElasticSearchDbConnection } = require("../Utils/dbConnection");
// const { goals } = require("./GoalAndPlaylistController");

exports.userProgress = async (req, res) => {
  try {
    let term = [
      {
        "term": {
          "user_id.keyword": {
            "value": req.get("wid")
          }
        }
      },
      {
        "term": {
          "content_type.keyword": req.query.contentType || "Resource"
        }
      }
    ]

    if (req.query.progressSource) {
      term.push({
        "term": {
          "progress_source.keyword": req.query.progressSource
        }
      })
    }
    let esClient = getElasticSearchDbConnection(undefined);
    // let goalAndPlaylist = await goals(req, res);
    let result = await esClient.search({
      index: `${req.client_name}_${constants.index_names.user_content_progress}`,
      type: "doc",
      body: {
        "size": 10000,
        "sort": [
          {
            "last_accessed_on": {
              "order": "desc"
            }
          }
        ],
        "query": {
          "bool": {
            "must": term
          }
        }
      }
    });

    let learning_history_lex_id = [];



    let learning_history = result.hits.hits.map(d => {
      learning_history_lex_id.push(d._source.content_id);
      return {
        lex_id: d._source.content_id,
        content_name: d._source.content_name,
        progress: d._source.progress,
        last_progress_date: d._source.last_progress_date,
        source: d._source.source_name,
        is_external: d._source.is_external,
        content_url: d._source.content_url
      }
    });
    //  console.log(learning_history_lex_id);
    let learning_history_progress_result = await esClient.search({
      index: `${req.client_name}_${constants.index_names.user_content_progress}`,
      "type": "doc",
      body: {
        "size": 10,
        "query": {
          "bool": {
            "must": [
              {
                "terms": {
                  "content_id.keyword": learning_history_lex_id
                }
              }
            ]
          }
        },
        "aggs": {
          "terms_progress": {
            "terms": {
              "field": "content_id.keyword",
              "size": 10000
            },
            "aggs": {
              "progress_range": {
                "histogram": {
                  "field": "progress",
                  "interval": 0.25
                }
              }
            }
          }
        }
      }
    });
    let learning_history_progress_range = {};
    let ranges = [0, 0.25, 0.5, 0.75, 1];
    let newLearningHistory = [];
    learning_history_progress_result.aggregations.terms_progress.buckets.map(d => {
      ranges.map(r => {
        let arr = d.progress_range.buckets.map(b => b.key);
        if (arr.indexOf(r) < 0) {
          d.progress_range.buckets.push({ key: r, doc_count: 0 })
        }
      })
      // console.log(d.progress_range.buckets[4].doc_count)
      if (d.progress_range.buckets[0].doc_count != 0) {

        learning_history_progress_range[d.key] = d.progress_range.buckets.sort((a,b)=>a.key-b.key);
        doc = learning_history.filter(f => f.lex_id == d.key)[0];
        doc.num_of_users = d.doc_count
        newLearningHistory.push(doc);
      }
    });
    let last_updated_on=await lastUpdateOn(req,res);
    
    let response = {
      last_updated_on,
      learning_history: newLearningHistory,
      learning_history_progress_range
    };
    res.send(response);
  } catch (err) {
    handleError(res, err.toString());
  } finally { }
}


const getCourseOrgData = async (course_list) => {
  try {
    let esClient = getElasticSearchDbConnection(undefined);
    let learning_history_progress_result = await esClient.search({
      index: `${req.client_name}_${constants.index_names.user_content_progress}`,
      "type": "doc",
      body: {
        "size": 0,
        "query": {
          "bool": {
            "must": [
              {
                "terms": {
                  "content_id.keyword": course_list
                }
              }
            ]
          }
        },
        "aggs": {
          "terms_progress": {
            "terms": {
              "field": "content_id.keyword",
              "size": 10000
            },
            "aggs": {
              "progress_range": {
                "histogram": {
                  "field": "progress",
                  "interval": 0.25
                }
              }
            }
          }
        }
      }
    });
    let learning_history_progress_range = {};
    let ranges = [0, 0.25, 0.5, 0.75, 1]
    learning_history_progress_result.aggregations.terms_progress.buckets.map(d => {
      ranges.map(r => {
        let arr = d.progress_range.buckets.map(b => b.key);
        if (arr.indexOf(r) < 0) {
          d.progress_range.buckets.push({ key: r, doc_count: 0 })
        }
      })
      learning_history_progress_range[d.key] = d.progress_range.buckets;
    });
    course_list.map(c => {
      if (!learning_history_progress_range[c]) {
        learning_history_progress_range[c] = [{ "key": 0, "doc_count": 0 }, { "key": 0.25, "doc_count": 0 }, { "key": 0.5, "doc_count": 0 }, { "key": 0.75, "doc_count": 0 }, { "key": 1, "doc_count": 0 }]
      }
    })
    return learning_history_progress_range;
  } catch (err) {
    handleError(res, err.toString());
  } finally { }
}

const getProgressSource = async (req, res) => {
  try {
    let esClient = getElasticSearchDbConnection(undefined);
    let result = await esClient.search({
      index: `${req.client_name}_${constants.index_names.user_content_progress}`,
      "type": "doc",
      body: {
        "size": 0,
        "aggs": {
          "progress_source": {
            "terms": {
              "field": "progress_source.keyword",
              "size": 100
            }
          }
        }
      }
    });
    progress_source = result.aggregations.progress_source.buckets.map(q => q.key)
    res.send({ progress_source });
  } catch (err) {
    handleError(res, err.toString());
    console.log(err);
  } finally { }
}

const lastUpdateOn = async (req, res) => {
  try {
    let esClient = getElasticSearchDbConnection(undefined);
    let result = await esClient.search({
    index: `${req.client_name}_${constants.index_names.user_content_progress}`,
      "type": "doc",
      body: {
        "size": 0,
        "aggs": {
          "last_updated_on": {
            "max": {
              "field": "date"
            }
          }
        }
      }
    });
    let last_updated_on = result.aggregations.last_updated_on.value
    return last_updated_on
  } catch (err) {
    console.log(err);
    handleError(res, err.toString());
   
  } finally { }
}


exports.getProgressSource = getProgressSource;
exports.getCourseOrgData = getCourseOrgData;
exports.lastUpdateOn=lastUpdateOn