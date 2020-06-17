mappings = {
  "settings": {
    "analysis": {
      "filter": {
        "arabic_stop": {
          "type": "stop",
          "stopwords": "_arabic_"
        },
        "brazilian_stop": {
          "type": "stop",
          "stopwords": "_brazilian_"
        },
        "czech_stop": {
          "type": "stop",
          "stopwords": "_czech_"
        },
        "danish_stop": {
          "type": "stop",
          "stopwords": "_danish_"
        },
        "german_stop": {
          "type": "stop",
          "stopwords": "_german_"
        },
        "greek_stop": {
          "type": "stop",
          "stopwords": "_greek_"
        },
        "spanish_stop": {
          "type": "stop",
          "stopwords": "_spanish_"
        },
        "french_stop": {
          "type": "stop",
          "stopwords": "_french_"
        },
        "hungarian_stop": {
          "type": "stop",
          "stopwords": "_hungarian_"
        },
        "indonesian_stop": {
          "type": "stop",
          "stopwords": "_indonesian_"
        },
        "italian_stop": {
          "type": "stop",
          "stopwords": "_italian_"
        },
		"english_stop": {
          "type": "stop",
          "stopwords": "_english_"
        },
        "dutch_stop": {
          "type": "stop",
          "stopwords": "_dutch_"
        },
        "norwegian_stop": {
          "type": "stop",
          "stopwords": "_norwegian_"
        },
        "portuguese_stop": {
          "type": "stop",
          "stopwords": "_portuguese_"
        },
        "russian_stop": {
          "type": "stop",
          "stopwords": "_russian_"
        },
        "swedish_stop": {
          "type": "stop",
          "stopwords": "_swedish_"
        },
        "thai_stop": {
          "type": "stop",
          "stopwords": "_thai_"
        },
        "turkish_stop": {
          "type": "stop",
          "stopwords": "_turkish_"
        },
        "turkish_lowercase": {
          "type": "lowercase",
          "language": "turkish"
        }
      },
      "analyzer": {
        "arabic_rebuilt": {
          "tokenizer": "standard",
          "filter": [
            "lowercase",
            "decimal_digit",
            "arabic_stop"
          ]
        },
        "brazilian_rebuilt": {
          "tokenizer": "standard",
          "filter": [
            "lowercase",
            "brazilian_stop"
          ]
        },
        "czech_rebuilt": {
          "tokenizer": "standard",
          "filter": [
            "lowercase",
            "czech_stop"
          ]
        },
        "danish_rebuilt": {
          "tokenizer": "standard",
          "filter": [
            "lowercase",
            "danish_stop"
          ]
        },
        "german_rebuilt": {
          "tokenizer": "standard",
          "filter": [
            "lowercase",
            "german_stop"
          ]
        },
        "greek_rebuilt": {
          "tokenizer": "standard",
          "filter": [
            "lowercase",
            "greek_stop"
          ]
        },
        "spanish_rebuilt": {
          "tokenizer": "standard",
          "filter": [
            "lowercase",
            "spanish_stop"
          ]
        },
        "french_rebuilt": {
          "tokenizer": "standard",
          "filter": [
            "lowercase",
            "french_stop"
          ]
        },
        "hungarian_rebuilt": {
          "tokenizer": "standard",
          "filter": [
            "lowercase",
            "hungarian_stop"
          ]
        },
        "indonesian_rebuilt": {
          "tokenizer": "standard",
          "filter": [
            "lowercase",
            "indonesian_stop"
          ]
        },
        "italian_rebuilt": {
          "tokenizer": "standard",
          "filter": [
            "lowercase",
            "italian_stop"
          ]
        },
        "cjk_rebuilt": {
          "tokenizer": "standard",
          "filter": [
            "cjk_width",
            "lowercase",
            "cjk_bigram",
            "english_stop"
          ]
        },
        "dutch_rebuilt": {
          "tokenizer": "standard",
          "filter": [
            "lowercase",
            "dutch_stop"
          ]
        },
        "norwegian_rebuilt": {
          "tokenizer": "standard",
          "filter": [
            "lowercase",
            "norwegian_stop"
          ]
        },
        "portuguese_rebuilt": {
          "tokenizer": "standard",
          "filter": [
            "lowercase",
            "portuguese_stop"
          ]
        },
        "russian_rebuilt": {
          "tokenizer": "standard",
          "filter": [
            "lowercase",
            "russian_stop"
          ]
        },
        "swedish_rebuilt": {
          "tokenizer": "standard",
          "filter": [
            "lowercase",
            "swedish_stop"
          ]
        },
        "thai_rebuilt": {
          "tokenizer": "thai",
          "filter": [
            "lowercase",
            "decimal_digit",
            "thai_stop"
          ]
        },
        "turkish_rebuilt": {
          "tokenizer": "standard",
          "filter": [
            "turkish_lowercase",
            "turkish_stop"
          ]
        },
		"english_rebuilt":{
			"tokenizer": "standard",
			"filter": [
				"lowercase",
				"english_stop"
			]
		}
      }
    }
  }
,
  "mappings" : {
      "autocomplete" : {
        "properties" : {
          "dateInserted" : {
            "type" : "date",
            "format":"basic_date_time_no_millis"
          },
          "id" : {
            "type" : "keyword"
          },
          "org" : {
            "type" : "keyword"
          },
          "rootOrg" : {
            "type" : "keyword"
          },
          "isSuggested" : {
            "type" : "boolean"
          },
          "searchTerm" : {
            "type" : "keyword"
          },
          "searchTermAnalysed" : {
            "type" : "text",
            "fields" : {
              "keyword" : {
                "type" : "keyword",
                "ignore_above" : 256
              }
            },
            "analyzer":"standard"
          }
        }
      }
    }
}