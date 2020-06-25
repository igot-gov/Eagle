mappings={
	
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
  },
	"mappings": {
		"searchresources": {
			"dynamic": "strict",
			"properties": {
				"name_da": {
					"analyzer": "danish_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"name_hr": {
					"analyzer": "standard",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"name_pl": {
					"analyzer": "standard",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"name_sl": {
					"analyzer": "standard",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"name_el": {
					"analyzer": "greek_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"name_ja": {
					"analyzer": "cjk_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"name_ko": {
					"analyzer": "cjk_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"name_ar": {
					"analyzer": "arabic_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"name_de": {
					"analyzer": "german_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"name_zh": {
					"analyzer": "cjk_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"name_id": {
					"analyzer": "indonesian_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"name_th": {
					"analyzer": "thai_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"name_sv": {
					"analyzer": "swedish_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"name_tr": {
					"analyzer": "turkish_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"name_ru": {
					"analyzer": "russian_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"name_pt": {
					"analyzer": "portuguese_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"name_br": {
					"analyzer": "portuguese_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"name_it": {
					"analyzer": "italian_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"name_hu": {
					"analyzer": "hungarian_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"name_nl": {
					"analyzer": "dutch_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"name_no": {
					"analyzer": "norwegian_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"name_es": {
					"analyzer": "spanish_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"name_fr": {
					"analyzer": "french_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"name_cz": {
					"analyzer": "czech_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"name_en": {
					"analyzer": "english_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"description_da": {
					"analyzer": "danish_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"description_hr": {
					"analyzer": "standard",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"description_pl": {
					"analyzer": "standard",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"description_sl": {
					"analyzer": "standard",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"description_el": {
					"analyzer": "greek_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"description_ja": {
					"analyzer": "cjk_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"description_ko": {
					"analyzer": "cjk_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"description_ar": {
					"analyzer": "arabic_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"description_de": {
					"analyzer": "german_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"description_zh": {
					"analyzer": "cjk_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"description_id": {
					"analyzer": "indonesian_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"description_th": {
					"analyzer": "thai_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"description_sv": {
					"analyzer": "swedish_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"description_tr": {
					"analyzer": "turkish_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"description_ru": {
					"analyzer": "russian_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"description_pt": {
					"analyzer": "portuguese_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"description_br": {
					"analyzer": "portuguese_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"description_it": {
					"analyzer": "italian_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"description_hu": {
					"analyzer": "hungarian_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"description_nl": {
					"analyzer": "dutch_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"description_no": {
					"analyzer": "norwegian_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"description_es": {
					"analyzer": "spanish_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"description_fr": {
					"analyzer": "french_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"description_cz": {
					"analyzer": "czech_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"description_en": {
					"analyzer": "english_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"keywords_da": {
					"analyzer": "danish_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"keywords_hr": {
					"analyzer": "standard",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"keywords_pl": {
					"analyzer": "standard",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"keywords_sl": {
					"analyzer": "standard",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"keywords_el": {
					"analyzer": "greek_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"keywords_ja": {
					"analyzer": "cjk_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"keywords_ko": {
					"analyzer": "cjk_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"keywords_ar": {
					"analyzer": "arabic_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"keywords_de": {
					"analyzer": "german_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"keywords_zh": {
					"analyzer": "cjk_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"keywords_id": {
					"analyzer": "indonesian_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"keywords_th": {
					"analyzer": "thai_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"keywords_sv": {
					"analyzer": "swedish_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"keywords_tr": {
					"analyzer": "turkish_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"keywords_ru": {
					"analyzer": "russian_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"keywords_pt": {
					"analyzer": "portuguese_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"keywords_br": {
					"analyzer": "portuguese_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"keywords_it": {
					"analyzer": "italian_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"keywords_hu": {
					"analyzer": "hungarian_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"keywords_nl": {
					"analyzer": "dutch_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"keywords_no": {
					"analyzer": "norwegian_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"keywords_es": {
					"analyzer": "spanish_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"keywords_fr": {
					"analyzer": "french_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"keywords_cz": {
					"analyzer": "czech_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"keywords_en": {
					"analyzer": "english_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"childrenTitle_da": {
					"analyzer": "danish_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"childrenTitle_hr": {
					"analyzer": "standard",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"childrenTitle_pl": {
					"analyzer": "standard",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"childrenTitle_sl": {
					"analyzer": "standard",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"childrenTitle_el": {
					"analyzer": "greek_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"childrenTitle_ja": {
					"analyzer": "cjk_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"childrenTitle_ko": {
					"analyzer": "cjk_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"childrenTitle_ar": {
					"analyzer": "arabic_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"childrenTitle_de": {
					"analyzer": "german_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"childrenTitle_zh": {
					"analyzer": "cjk_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"childrenTitle_id": {
					"analyzer": "indonesian_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"childrenTitle_th": {
					"analyzer": "thai_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"childrenTitle_sv": {
					"analyzer": "swedish_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"childrenTitle_tr": {
					"analyzer": "turkish_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"childrenTitle_ru": {
					"analyzer": "russian_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"childrenTitle_pt": {
					"analyzer": "portuguese_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"childrenTitle_br": {
					"analyzer": "portuguese_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"childrenTitle_it": {
					"analyzer": "italian_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"childrenTitle_hu": {
					"analyzer": "hungarian_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"childrenTitle_nl": {
					"analyzer": "dutch_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"childrenTitle_no": {
					"analyzer": "norwegian_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"childrenTitle_es": {
					"analyzer": "spanish_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"childrenTitle_fr": {
					"analyzer": "french_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"childrenTitle_cz": {
					"analyzer": "czech_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"childrenTitle_en": {
					"analyzer": "english_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"childrenDescription_da": {
					"analyzer": "danish_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"childrenDescription_hr": {
					"analyzer": "standard",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"childrenDescription_pl": {
					"analyzer": "standard",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"childrenDescription_sl": {
					"analyzer": "standard",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"childrenDescription_el": {
					"analyzer": "greek_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"childrenDescription_ja": {
					"analyzer": "cjk_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"childrenDescription_ko": {
					"analyzer": "cjk_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"childrenDescription_ar": {
					"analyzer": "arabic_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"childrenDescription_de": {
					"analyzer": "german_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"childrenDescription_zh": {
					"analyzer": "cjk_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"childrenDescription_id": {
					"analyzer": "indonesian_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"childrenDescription_th": {
					"analyzer": "thai_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"childrenDescription_sv": {
					"analyzer": "swedish_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"childrenDescription_tr": {
					"analyzer": "turkish_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"childrenDescription_ru": {
					"analyzer": "russian_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"childrenDescription_pt": {
					"analyzer": "portuguese_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"childrenDescription_br": {
					"analyzer": "portuguese_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"childrenDescription_it": {
					"analyzer": "italian_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"childrenDescription_hu": {
					"analyzer": "hungarian_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"childrenDescription_nl": {
					"analyzer": "dutch_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"childrenDescription_no": {
					"analyzer": "norwegian_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"childrenDescription_es": {
					"analyzer": "spanish_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"childrenDescription_fr": {
					"analyzer": "french_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"childrenDescription_cz": {
					"analyzer": "czech_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"childrenDescription_en": {
					"analyzer": "english_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"catalogPaths_da": {
					"analyzer": "danish_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"catalogPaths_hr": {
					"analyzer": "standard",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"catalogPaths_pl": {
					"analyzer": "standard",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"catalogPaths_sl": {
					"analyzer": "standard",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"catalogPaths_el": {
					"analyzer": "greek_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"catalogPaths_ja": {
					"analyzer": "cjk_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"catalogPaths_ko": {
					"analyzer": "cjk_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"catalogPaths_ar": {
					"analyzer": "arabic_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"catalogPaths_de": {
					"analyzer": "german_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"catalogPaths_zh": {
					"analyzer": "cjk_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"catalogPaths_id": {
					"analyzer": "indonesian_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"catalogPaths_th": {
					"analyzer": "thai_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"catalogPaths_sv": {
					"analyzer": "swedish_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"catalogPaths_tr": {
					"analyzer": "turkish_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"catalogPaths_ru": {
					"analyzer": "russian_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"catalogPaths_pt": {
					"analyzer": "portuguese_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"catalogPaths_br": {
					"analyzer": "portuguese_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"catalogPaths_it": {
					"analyzer": "italian_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"catalogPaths_hu": {
					"analyzer": "hungarian_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"catalogPaths_nl": {
					"analyzer": "dutch_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"catalogPaths_no": {
					"analyzer": "norwegian_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"catalogPaths_es": {
					"analyzer": "spanish_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"catalogPaths_fr": {
					"analyzer": "french_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"catalogPaths_cz": {
					"analyzer": "czech_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"catalogPaths_en": {
					"analyzer": "english_rebuilt",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"viewCount": {
					"dynamic": True,
					"type": "nested",
					"properties": {
						"Infosys": {
							"type": "long"
						}
					}
				},
				"averageRating": {
					"dynamic": True,
					"type": "nested",
					"properties": {
						"Infosys": {
							"type": "long"
						}
					}
				},
				"uniqueUsersCount": {
					"dynamic": True,
					"type": "nested",
					"properties": {
						"Infosys": {
							"type": "long"
						}
					}
				},
				"totalRating": {
					"dynamic": True,
					"type": "nested",
					"properties": {
						"Infosys": {
							"type": "long"
						}
					}
				},
				"expiryDate": {
					"type": "date",
					"format": "basic_date_time_no_millis"
				},
				"locale": {
					"type": "keyword"
				},
				"accessPaths": {
					"type": "keyword"
				},
				"accessibility": {
					"type": "keyword"
				},
				"appIcon": {
					"type": "keyword"
				},
				"artifactUrl": {
					"type": "keyword"
				},
				"audience": {
					"type": "keyword"
				},
				"authoringDisabled": {
					"type": "boolean"
				},
				"body": {
					"type": "keyword",
					"fields": {
						"keyword": {
							"type": "keyword",
							"ignore_above": 512
						}
					}
				},
				"catalog": {
					"properties": {
						"id": {
							"type": "keyword"
						},
						"type": {
							"type": "keyword"
						},
						"value": {
							"type": "keyword"
						}
					}
				},
				"catalogPaths": {
					"type": "text",
					"analyzer": "tags_relation_analyzer",
					"fields": {
						"keyword": {
							"type": "keyword",
							"ignore_above": 512
						}
					}
				},
				"category": {
					"type": "keyword"
				},
				"contentType": {
					"type": "keyword"
				},
				"categoryType": {
					"type": "keyword"
				},
				"resourceType": {
					"type": "keyword"
				},
				"resourceCategory": {
					"type": "keyword"
				},
				"certificationUrl": {
					"type": "keyword"
				},
				"checksum": {
					"type": "keyword"
				},
				"children": {
					"type": "nested",
					"properties": {
						"contentType": {
							"type": "keyword"
						},
						"childrenClassifiers": {
							"type": "keyword"
						},
						"compatibilityLevel": {
							"type": "keyword"
						},
						"depth": {
							"type": "keyword"
						},
						"description": {
							"type": "keyword"
						},
						"identifier": {
							"type": "keyword"
						},
						"index": {
							"type": "keyword"
						},
						"mimeType": {
							"type": "keyword"
						},
						"name": {
							"type": "keyword"
						},
						"objectType": {
							"type": "keyword"
						},
						"relation": {
							"type": "keyword"
						},
						"status": {
							"type": "keyword"
						},
						"visibility": {
							"type": "keyword"
						},
						"addedOn": {
							"type": "date",
							"format": "basic_date_time_no_millis"
						},
						"reason": {
							"type": "keyword"
						}
					}
				},
				"childrenDescription": {
					"type": "text"
				},
				"childrenTitle": {
					"type": "text"
				},
				"clients": {
					"type": "nested",
					"properties": {
						"displayName": {
							"type": "keyword"
						},
						"id": {
							"type": "keyword"
						},
						"name": {
							"type": "keyword"
						}
					}
				},
				"collections": {
					"type": "nested",
					"properties": {
						"contentType": {
							"type": "keyword"
						},
						"childrenClassifiers": {
							"type": "keyword"
						},
						"compatibilityLevel": {
							"type": "keyword"
						},
						"depth": {
							"type": "keyword"
						},
						"description": {
							"type": "keyword"
						},
						"identifier": {
							"type": "keyword"
						},
						"index": {
							"type": "keyword"
						},
						"mimeType": {
							"type": "keyword"
						},
						"name": {
							"type": "keyword"
						},
						"objectType": {
							"type": "keyword"
						},
						"relation": {
							"type": "keyword"
						},
						"status": {
							"type": "keyword"
						},
						"visibility": {
							"type": "keyword"
						},
						"addedOn": {
							"type": "date",
							"format": "basic_date_time_no_millis"
						},
						"reason": {
							"type": "keyword"
						}
					}
				},
				"comments": {
					"type": "nested",
					"properties": {
						"comment": {
							"type": "keyword"
						},
						"commentedBy": {
							"type": "keyword"
						},
						"date": {
							"type": "date",
							"format": "basic_date_time_no_millis"
						},
						"name": {
							"type": "keyword"
						},
						"id": {
							"type": "keyword"
						}
					}
				},
				"complexityLevel": {
					"type": "keyword"
				},
				"concepts": {
					"type": "nested",
					"properties": {
						"identifier": {
							"type": "keyword"
						},
						"name": {
							"type": "text",
							"fields": {
								"keyword": {
									"type": "keyword",
									"ignore_above": 512
								}
							}
						},
						"objectType": {
							"type": "keyword"
						},
						"relation": {
							"type": "keyword"
						},
						"status": {
							"type": "keyword"
						}
					}
				},
				"contentFeedbackTemplateId": {
					"type": "keyword"
				},
				"contentIdAtSource": {
					"type": "keyword"
				},
				"contentLanguage": {
					"type": "keyword"
				},
				"contentUrlAtSource": {
					"type": "keyword"
				},
				"copyright": {
					"type": "keyword"
				},
				"creator": {
					"type": "keyword"
				},
				"creatorContacts": {
					"type": "nested",
					"properties": {
						"id": {
							"type": "keyword"
						},
						"name": {
							"type": "keyword"
						}
					}
				},
				"creatorDetails": {
					"type": "nested",
					"properties": {
						"about": {
							"type": "keyword"
						},
						"id": {
							"type": "keyword"
						},
						"name": {
							"type": "keyword"
						}
					}
				},
				"customClassifiers": {
					"type": "keyword"
				},
				"description": {
					"analyzer": "standard",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword",
							"ignore_above": 512
						}
					}
				},
				"downloadUrl": {
					"type": "keyword"
				},
				"duration": {
					"type": "long"
				},
				"eligibility": {
					"type": "nested",
					"properties": {
						"eligibleJL": {
							"type": "keyword"
						},
						"eligibleUnit": {
							"type": "keyword"
						},
						"eligibleRole": {
							"type": "keyword"
						}
					}
				},
				"certificationList": {
					"type": "nested",
					"properties": {
						"name": {
							"type": "keyword"
						},
						"identifier": {
							"type": "keyword"
						},
						"description": {
							"type": "keyword"
						},
						"appIcon": {
							"type": "keyword"
						},
						"artifactUrl": {
							"type": "keyword"
						},
						"isExternalCertificate": {
							"type": "boolean"
						}
					}
				},
				"kArtifacts": {
					"type": "nested",
					"properties": {
						"name": {
							"type": "keyword"
						},
						"identifier": {
							"type": "keyword"
						}
					}
				},
				"equivalentCertifications": {
					"properties": {
						"identifier": {
							"type": "keyword"
						},
						"name": {
							"type": "keyword"
						}
					}
				},
				"externalCertificate": {
					"type": "boolean"
				},
				"externalData": {
					"properties": {
						"code": {
							"type": "keyword"
						},
						"isProctored": {
							"type": "boolean"
						},
						"name": {
							"type": "keyword"
						}
					}
				},
				"extractedText": {
					"analyzer": "standard",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword",
							"ignore_above": 512
						}
					}
				},
				"grayScaleAppIcon": {
					"type": "keyword"
				},
				"hasAssessment": {
					"type": "keyword"
				},
				"idealScreenSize": {
					"type": "keyword"
				},
				"identifier": {
					"type": "keyword"
				},
				"imageCredits": {
					"type": "keyword"
				},
				"instructorFeedbackTemplateId": {
					"type": "keyword"
				},
				"interactivityLevel": {
					"type": "keyword"
				},
				"introductoryVideo": {
					"type": "keyword"
				},
				"introductoryVideoIcon": {
					"type": "keyword"
				},
				"ipReview": {
					"type": "keyword"
				},
				"isExternal": {
					"type": "boolean"
				},
				"isIframeSupported": {
					"type": "keyword"
				},
				"isInDefaultCorpus": {
					"type": "boolean"
				},
				"isRejected": {
					"type": "boolean"
				},
				"isSearchable": {
					"type": "boolean"
				},
				"isStandAlone": {
					"type": "boolean"
				},
				"keywords": {
					"analyzer": "standard",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword",
							"ignore_above": 512
						}
					}
				},
				"lastPublishedOn": {
					"type": "date",
					"format": "basic_date_time_no_millis"
				},
				"publishedBy": {
					"type": "keyword"
				},
				"lastUpdatedBy": {
					"type": "keyword"
				},
				"lastUpdatedOn": {
					"type": "date",
					"format": "basic_date_time_no_millis"
				},
				"learningMode": {
					"type": "keyword"
				},
				"learningObjective": {
					"analyzer": "standard",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword",
							"ignore_above": 512
						}
					}
				},
				"learningTrack": {
					"type": "keyword"
				},
				"license": {
					"type": "keyword"
				},
				"loadingMessage": {
					"type": "keyword"
				},
				"me_averageInteractionsPerMin": {
					"type": "float"
				},
				"me_averageRating": {
					"type": "float"
				},
				"me_averageSessionsPerDevice": {
					"type": "float"
				},
				"me_averageTimespentPerSession": {
					"type": "float"
				},
				"me_totalComments": {
					"type": "long"
				},
				"me_totalDevices": {
					"type": "long"
				},
				"me_totalDownloads": {
					"type": "long"
				},
				"me_totalInteractions": {
					"type": "long"
				},
				"me_totalRatings": {
					"type": "long"
				},
				"me_totalSessionsCount": {
					"type": "long"
				},
				"me_totalSideloads": {
					"type": "long"
				},
				"me_totalTimespent": {
					"type": "long"
				},
				"mediaType": {
					"type": "keyword"
				},
				"mimeType": {
					"type": "keyword"
				},
				"fileType": {
					"type": "keyword"
				},
				"minWingspanVersion": {
					"type": "keyword"
				},
				"msArtifactDetails": {
					"properties": {
						"channelId": {
							"type": "keyword"
						},
						"videoId": {
							"type": "keyword"
						}
					}
				},
				"name": {
					"analyzer": "standard",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword",
							"ignore_above": 512
						}
					}
				},
				"org": {
					"type": "nested",
					"properties": {
						"org": {
							"type": "keyword"
						},
						"validTill": {
							"type": "date",
							"format": "basic_date_time_no_millis"
						}
					}
				},
				"passPercentage": {
					"type": "long"
				},
				"plagScan": {
					"type": "nested",
					"properties": {
						"docId": {
							"type": "keyword"
						},
						"status": {
							"type": "keyword"
						}
					}
				},
				"playgroundInstructions": {
					"type": "keyword"
				},
				"playgroundResources": {
					"type": "nested",
					"properties": {
						"appIcon": {
							"type": "keyword"
						},
						"artifactUrl": {
							"type": "keyword"
						},
						"identifier": {
							"type": "keyword"
						},
						"name": {
							"type": "keyword"
						}
					}
				},
				"postContents": {
					"properties": {
						"identifier": {
							"type": "keyword"
						},
						"name": {
							"type": "keyword"
						}
					}
				},
				"posterImage": {
					"type": "keyword"
				},
				"preContents": {
					"properties": {
						"identifier": {
							"type": "keyword"
						},
						"name": {
							"type": "keyword"
						}
					}
				},
				"preRequisites": {
					"analyzer": "standard",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword",
							"ignore_above": 512
						}
					}
				},
				"projectCode": {
					"type": "keyword"
				},
				"publicationId": {
					"type": "keyword"
				},
				"publishedOn": {
					"type": "date",
					"format": "basic_date_time_no_millis"
				},
				"publisher": {
					"type": "keyword"
				},
				"publisherDetails": {
					"type": "nested",
					"properties": {
						"id": {
							"type": "keyword"
						},
						"name": {
							"type": "keyword"
						}
					}
				},
				"references": {
					"properties": {
						"title": {
							"type": "keyword"
						},
						"url": {
							"type": "keyword"
						}
					}
				},
				"registrationInstructions": {
					"type": "keyword"
				},
				"releaseNotes": {
					"type": "keyword"
				},
				"rootOrg": {
					"type": "keyword"
				},
				"sampleCertificates": {
					"type": "keyword"
				},
				"scoreType": {
					"properties": {
						"allowedGrade": {
							"type": "keyword"
						},
						"maxValue": {
							"type": "long"
						},
						"passValue": {
							"type": "long"
						}
					}
				},
				"size": {
					"type": "float"
				},
				"skills": {
					"type": "nested",
					"properties": {
						"category": {
							"type": "keyword"
						},
						"identifier": {
							"type": "keyword"
						},
						"name": {
							"type": "keyword"
						},
						"skill": {
							"type": "keyword"
						}
					}
				},
				"softwareRequirements": {
					"properties": {
						"title": {
							"type": "keyword"
						},
						"url": {
							"type": "keyword"
						}
					}
				},
				"soundCredits": {
					"type": "keyword"
				},
				"sourceIconUrl": {
					"type": "keyword"
				},
				"sourceName": {
					"analyzer": "standard",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword",
							"ignore_above": 512
						}
					}
				},
				"sourceShortName": {
					"analyzer": "standard",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword",
							"ignore_above": 512
						}
					}
				},
				"sourceUrl": {
					"type": "keyword"
				},
				"ssoEnabled": {
					"type": "boolean"
				},
				"status": {
					"type": "keyword"
				},
				"studyDuration": {
					"type": "long"
				},
				"studyMaterials": {
					"properties": {
						"identifier": {
							"type": "keyword"
						},
						"name": {
							"type": "keyword"
						}
					}
				},
				"subTitle": {
					"analyzer": "standard",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword",
							"ignore_above": 512
						}
					}
				},
				"subTitles": {
					"type": "nested",
					"properties": {
						"srclang": {
							"type": "keyword"
						},
						"url": {
							"type": "keyword"
						},
						"label": {
							"type": "keyword"
						}
					}
				},
				"submitterDetails": {
					"type": "nested",
					"properties": {
						"id": {
							"type": "keyword"
						},
						"name": {
							"type": "keyword"
						}
					}
				},
				"systemRequirements": {
					"type": "keyword"
				},
				"templateType": {
					"type": "keyword"
				},
				"thumbnail": {
					"type": "keyword"
				},
				"trackContacts": {
					"type": "nested",
					"properties": {
						"id": {
							"type": "keyword"
						},
						"name": {
							"type": "keyword"
						}
					}
				},
				"trackOwner": {
					"type": "keyword"
				},
				"transcript": {
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword",
							"ignore_above": 512
						}
					}
				},
				"uniqueLearners": {
					"type": "long"
				},
				"unit": {
					"type": "keyword"
				},
				"verifiers": {
					"type": "nested",
					"properties": {
						"id": {
							"type": "keyword"
						},
						"name": {
							"type": "keyword"
						}
					}
				},
				"versionDate": {
					"type": "date",
					"format": "basic_date_time_no_millis"
				},
				"versionKey": {
					"type": "long"
				},
				"visibility": {
					"type": "keyword"
				},
				"voiceCredits": {
					"type": "keyword"
				},
				"jobProfile": {
					"type": "keyword"
				},
				"objectType": {
					"type": "keyword"
				},
				"nodeType": {
					"type": "keyword"
				},
				"nodeId": {
					"type": "keyword"
				},
				"region": {
					"type": "keyword"
				},
				"catalogPathsIds": {
					"type": "keyword"
				},
				"dimension": {
					"type": "keyword"
				},
				"actor": {
					"type": "keyword"
				},
				"transcoding": {
					"type": "nested",
					"properties": {
						"status": {
							"type": "keyword"
						},
						"retryCount": {
							"type": "long"
						},
						"lastTranscodedOn": {
							"type": "keyword"
						}
					}
				},
				"hasTranslations": {
					"type": "nested",
					"properties": {
						"identifier": {
							"type": "keyword"
						},
						"locale": {
							"type": "keyword"
						}
					}
				},
				"isTranslationOf": {
					"properties": {
						"identifier": {
							"type": "keyword"
						},
						"locale": {
							"type": "keyword"
						}
					}
				},
				"metaDataLanguage": {
					"type": "keyword"
				},
				"isRegistrationRequired": {
					"type": "boolean"
				},
				"registrationUrl": {
					"type": "keyword"
				},
				"isCuratedContent": {
					"type": "boolean"
				},
				"accessPathsLockedStatus": {
					"type": "boolean"
				},
				"curatedTags": {
					"analyzer": "standard",
					"type": "text",
					"fields": {
						"keyword": {
							"type": "keyword"
						}
					}
				},
				"labels":{
					"type":"keyword"
				},
				"exclusiveContent":{
					"type":"boolean"
				},
				"instanceCatalog":{
					"type":"boolean"
				},
				"isInIntranet":{
					"type":"boolean"
				},
				"editors":{
					"type": "nested",
					"properties": {
						"id": {
							"type": "keyword"
						},
						"name": {
							"type": "keyword"
						}
					}
				},
				"costCenter":{
					"type":"keyword"
				},
				"price":{
					"type":"nested",
					"properties":{
						"value":{
							"type": "double"
						},
						"currency":{
							"type": "keyword"
						}
					}
				},
				"deliveryLanguages":{
					"type":"keyword"
				},
				"deliveryCountries":{
					"type":"keyword"
				},
				"contentIdAtSource2":{
					"type":"keyword"
				},
				"isContentEditingDisabled":{
					"type":"boolean"
				},
				"isMetaEditingDisabled":{
					"type":"boolean"
				},
				"trendingScore": {
					"dynamic": True,
					"type": "nested",
					"properties": {
						"Infosys": {
							"type": "long"
						}
					}
				},
				"completedCount": {
					"dynamic": True,
					"type": "nested",
					"properties": {
						"Infosys": {
							"type": "long"
						}
					}
				},
				"contentStructure": {
					"type": "keyword"
				}
			}
		}
	}
}