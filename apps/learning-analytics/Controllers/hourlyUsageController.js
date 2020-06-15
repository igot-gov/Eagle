const { getElasticSearchDbConnection } = require("../Utils/dbConnection");
const { index_names,hours,chartMapping } = require("../Config/constants");

const hourlyUsage = async (req, res) => {
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
    if(req.query.week>=0){
        term.push({
            "term": {
                "day_of_week": req.query.week
                }
              });
    }
        let esClient = getElasticSearchDbConnection();
        let result = await esClient.search({
        index: `${req.client_name}_${index_names.hourly_feature_usage}`,
          "type": "doc",
          body: {
            "size": 0,
            "query": {
              "bool": {
                "must": term
              }
            },
            "aggs":{
               "hour": {
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
              }}
          }
        });

        hourly_usage= result.aggregations.hour.buckets.map(q => { return { "key": q.key.toString(), "doc_count": q.unique.value } });
        hours.map(q=>{if(hourly_usage.filter(w=>w.key==q).length==0)hourly_usage.push({ "key": q.toString(), "doc_count": 0 })})
        hourly_usage.sort((a, b) => a.key - b.key)
        res.send({hourly_usage});

    }
    catch (err) {
      console.log(err.toString());
    }
  }



  exports.hourlyUsage = hourlyUsage;