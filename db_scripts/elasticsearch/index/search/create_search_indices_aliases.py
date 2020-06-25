from search_mappings_settings import mappings

from elasticsearch5 import Elasticsearch
from elasticsearch5 import helpers
import os

esUrl = os.environ['esurl']
esPort = os.environ['esport']
esPass = os.environ['espass']
esUser = os.environ['esuser']
version = os.environ['version']
oldversion = os.environ['oldversion']
aliases = os.environ['aliases']
indices = os.environ['indices']

# esObj = Elasticsearch([{"host":"10.177.63.206","port":"9200"}])
esObj = Elasticsearch([{"host":esUrl,"port":esPort}],http_auth=(esUser,esPass))

# version = "v12"
# oldversion = "v11"
# indices="yes"
# aliases="no"
locales = {"da":"danish_rebuilt","hr":"standard","pl":"standard","sl":"standard","el":"greek_rebuilt","ja":"cjk_rebuilt","ko":"cjk_rebuilt","ar": "arabic_rebuilt","de": "german_rebuilt","zh":"cjk_rebuilt","id": "indonesian_rebuilt","th": "thai_rebuilt","sv": "swedish_rebuilt","tr": "turkish_rebuilt","ru": "russian_rebuilt","pt": "portuguese_rebuilt","br": "brazilian_rebuilt","it": "italian_rebuilt","hu": "hungarian_rebuilt","nl": "dutch_rebuilt","no": "norwegian_rebuilt","es": "spanish_rebuilt","fr": "french_rebuilt","fr_ca": "french_rebuilt","cz": "czech_rebuilt","en": "english_rebuilt"}

if indices == "yes":
  for locale in locales:
      tempmapping = mappings
      for item in tempmapping["mappings"]["searchresources"]["properties"]:
          if tempmapping["mappings"]["searchresources"]["properties"][item].get("type",False):
              if tempmapping["mappings"]["searchresources"]["properties"][item]["type"] == "text":
                  if not "_" in item:
                      tempmapping["mappings"]["searchresources"]["properties"][item]["analyzer"] = locales[locale]
      esObj.indices.create(index=locale+"_mlsearch_"+version,body=tempmapping)

if aliases == "yes":
  esObj.indices.update_aliases(
    {
"actions":[
{
  "remove": {
    "index": "fr_ca_mlsearch_" + oldversion,
    "alias": "mlsearch_fr_ca"
  }
},
{
  "add": {
    "index": "fr_ca_mlsearch_" + version,
    "alias": "mlsearch_fr_ca"
  }
},
{
  "remove": {
    "index": "da_mlsearch_" + oldversion,
    "alias": "mlsearch_da"
  }
},
{
  "add": {
    "index": "da_mlsearch_" + version,
    "alias": "mlsearch_da"
  }
}
,
{
  "remove": {
    "index": "hr_mlsearch_" + oldversion,
    "alias": "mlsearch_hr"
  }
},
{
  "add": {
    "index": "hr_mlsearch_" + version,
    "alias": "mlsearch_hr"
  }
}
,
{
  "remove": {
    "index": "pl_mlsearch_" + oldversion,
    "alias": "mlsearch_pl"
  }
},
{
  "add": {
    "index": "pl_mlsearch_" + version,
    "alias": "mlsearch_pl"
  }
}
,
{
  "remove": {
    "index": "sl_mlsearch_" + oldversion,
    "alias": "mlsearch_sl"
  }
},
{
  "add": {
    "index": "sl_mlsearch_" + version,
    "alias": "mlsearch_sl"
  }
}
,
{
  "remove": {
    "index": "el_mlsearch_" + oldversion,
    "alias": "mlsearch_el"
  }
},
{
  "add": {
    "index": "el_mlsearch_" + version,
    "alias": "mlsearch_el"
  }
}
,
{
  "remove": {
    "index": "ja_mlsearch_" + oldversion,
    "alias": "mlsearch_ja"
  }
},
{
  "add": {
    "index": "ja_mlsearch_" + version,
    "alias": "mlsearch_ja"
  }
}
,
{
  "remove": {
    "index": "ko_mlsearch_" + oldversion,
    "alias": "mlsearch_ko"
  }
},
{
  "add": {
    "index": "ko_mlsearch_" + version,
    "alias": "mlsearch_ko"
  }
}
,
{
  "remove": {
    "index": "ar_mlsearch_" + oldversion,
    "alias": "mlsearch_ar"
  }
},
{
  "add": {
    "index": "ar_mlsearch_" + version,
    "alias": "mlsearch_ar"
  }
}
,
{
  "remove": {
    "index": "de_mlsearch_" + oldversion,
    "alias": "mlsearch_de"
  }
},
{
  "add": {
    "index": "de_mlsearch_" + version,
    "alias": "mlsearch_de"
  }
}
,
{
  "remove": {
    "index": "zh_mlsearch_" + oldversion,
    "alias": "mlsearch_zh"
  }
},
{
  "add": {
    "index": "zh_mlsearch_" + version,
    "alias": "mlsearch_zh"
  }
}
,
{
  "remove": {
    "index": "id_mlsearch_" + oldversion,
    "alias": "mlsearch_id"
  }
},
{
  "add": {
    "index": "id_mlsearch_" + version,
    "alias": "mlsearch_id"
  }
}
,
{
  "remove": {
    "index": "th_mlsearch_" + oldversion,
    "alias": "mlsearch_th"
  }
},
{
  "add": {
    "index": "th_mlsearch_" + version,
    "alias": "mlsearch_th"
  }
}
,
{
  "remove": {
    "index": "sv_mlsearch_" + oldversion,
    "alias": "mlsearch_sv"
  }
},
{
  "add": {
    "index": "sv_mlsearch_" + version,
    "alias": "mlsearch_sv"
  }
}
,
{
  "remove": {
    "index": "tr_mlsearch_" + oldversion,
    "alias": "mlsearch_tr"
  }
},
{
  "add": {
    "index": "tr_mlsearch_" + version,
    "alias": "mlsearch_tr"
  }
}
,
{
  "remove": {
    "index": "ru_mlsearch_" + oldversion,
    "alias": "mlsearch_ru"
  }
},
{
  "add": {
    "index": "ru_mlsearch_" + version,
    "alias": "mlsearch_ru"
  }
}
,
{
  "remove": {
    "index": "pt_mlsearch_" + oldversion,
    "alias": "mlsearch_pt"
  }
},
{
  "add": {
    "index": "pt_mlsearch_" + version,
    "alias": "mlsearch_pt"
  }
}
,
{
  "remove": {
    "index": "br_mlsearch_" + oldversion,
    "alias": "mlsearch_br"
  }
},
{
  "add": {
    "index": "br_mlsearch_" + version,
    "alias": "mlsearch_br"
  }
}
,
{
  "remove": {
    "index": "it_mlsearch_" + oldversion,
    "alias": "mlsearch_it"
  }
},
{
  "add": {
    "index": "it_mlsearch_" + version,
    "alias": "mlsearch_it"
  }
}
,
{
  "remove": {
    "index": "hu_mlsearch_" + oldversion,
    "alias": "mlsearch_hu"
  }
},
{
  "add": {
    "index": "hu_mlsearch_" + version,
    "alias": "mlsearch_hu"
  }
}
,
{
  "remove": {
    "index": "nl_mlsearch_" + oldversion,
    "alias": "mlsearch_nl"
  }
},
{
  "add": {
    "index": "nl_mlsearch_" + version,
    "alias": "mlsearch_nl"
  }
}
,
{
  "remove": {
    "index": "no_mlsearch_" + oldversion,
    "alias": "mlsearch_no"
  }
},
{
  "add": {
    "index": "no_mlsearch_" + version,
    "alias": "mlsearch_no"
  }
}
,
{
  "remove": {
    "index": "es_mlsearch_" + oldversion,
    "alias": "mlsearch_es"
  }
},
{
  "add": {
    "index": "es_mlsearch_" + version,
    "alias": "mlsearch_es"
  }
}
,
{
  "remove": {
    "index": "fr_mlsearch_" + oldversion,
    "alias": "mlsearch_fr"
  }
},
{
  "add": {
    "index": "fr_mlsearch_" + version,
    "alias": "mlsearch_fr"
  }
}
,
{
  "remove": {
    "index": "cz_mlsearch_" + oldversion,
    "alias": "mlsearch_cz"
  }
},
{
  "add": {
    "index": "cz_mlsearch_" + version,
    "alias": "mlsearch_cz"
  }
},
{
  "remove": {
    "index": "en_mlsearch_" + oldversion,
    "alias": "mlsearch_en"
  }
},
{
  "add": {
    "index": "en_mlsearch_" + version,
    "alias": "mlsearch_en"
  }
}
]}
)