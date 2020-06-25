from acmappings import mappings

from elasticsearch5 import Elasticsearch
from elasticsearch5 import helpers
import os

esUrl = os.environ['esurl']
esPort = os.environ['esport']
esPass = os.environ['espass']
esUser = os.environ['esuser']
aliases = os.environ['aliases']
indices = os.environ['indices']

esObj = Elasticsearch([{"host":esUrl,"port":esPort}],http_auth=(esUser,esPass))

version = ""
oldversion = ""

with open("sacindexcreator_count","r") as file:
  version,oldversion = file.readline().split(",")
  version = str(int(version) + 1)
  oldversion = str(int(oldversion) + 1)

locales = {"da":"danish_rebuilt","hr":"standard","pl":"standard","sl":"standard","el":"greek_rebuilt","ja":"cjk_rebuilt","ko":"cjk_rebuilt","ar": "arabic_rebuilt","de": "german_rebuilt","zh":"cjk_rebuilt","id": "indonesian_rebuilt","th": "thai_rebuilt","sv": "swedish_rebuilt","tr": "turkish_rebuilt","ru": "russian_rebuilt","pt": "portuguese_rebuilt","br": "brazilian_rebuilt","it": "italian_rebuilt","hu": "hungarian_rebuilt","nl": "dutch_rebuilt","no": "norwegian_rebuilt","es": "spanish_rebuilt","fr": "french_rebuilt","fr_ca": "french_rebuilt","cz": "czech_rebuilt","en": "english_rebuilt"}

if indices == "yes":
  for locale in locales:
      tempmapping = mappings
      for item in tempmapping["mappings"]["autocomplete"]["properties"]:
          if tempmapping["mappings"]["autocomplete"]["properties"][item].get("type",False):
              if tempmapping["mappings"]["autocomplete"]["properties"][item]["type"] == "text":
                  if not "_" in item:
                      tempmapping["mappings"]["autocomplete"]["properties"][item]["analyzer"] = locales[locale]
      esObj.indices.create(index=locale+"_searchautocomplete_v"+version,body=tempmapping)

if aliases == "yes":
  esObj.indices.update_aliases(
    {
"actions":[
    {
  "remove": {
    "index": "da_searchautocomplete_v" + oldversion,
    "alias": "searchautocomplete_da"
  }
},
{
  "add": {
    "index": "da_searchautocomplete_v" + version,
    "alias": "searchautocomplete_da"
  }
}
,
{
  "remove": {
    "index": "hr_searchautocomplete_v" + oldversion,
    "alias": "searchautocomplete_hr"
  }
},
{
  "add": {
    "index": "hr_searchautocomplete_v" + version,
    "alias": "searchautocomplete_hr"
  }
}
,
{
  "remove": {
    "index": "pl_searchautocomplete_v" + oldversion,
    "alias": "searchautocomplete_pl"
  }
},
{
  "add": {
    "index": "pl_searchautocomplete_v" + version,
    "alias": "searchautocomplete_pl"
  }
}
,
{
  "remove": {
    "index": "sl_searchautocomplete_v" + oldversion,
    "alias": "searchautocomplete_sl"
  }
},
{
  "add": {
    "index": "sl_searchautocomplete_v" + version,
    "alias": "searchautocomplete_sl"
  }
}
,
{
  "remove": {
    "index": "el_searchautocomplete_v" + oldversion,
    "alias": "searchautocomplete_el"
  }
},
{
  "add": {
    "index": "el_searchautocomplete_v" + version,
    "alias": "searchautocomplete_el"
  }
}
,
{
  "remove": {
    "index": "ja_searchautocomplete_v" + oldversion,
    "alias": "searchautocomplete_ja"
  }
},
{
  "add": {
    "index": "ja_searchautocomplete_v" + version,
    "alias": "searchautocomplete_ja"
  }
}
,
{
  "remove": {
    "index": "ko_searchautocomplete_v" + oldversion,
    "alias": "searchautocomplete_ko"
  }
},
{
  "add": {
    "index": "ko_searchautocomplete_v" + version,
    "alias": "searchautocomplete_ko"
  }
}
,
{
  "remove": {
    "index": "ar_searchautocomplete_v" + oldversion,
    "alias": "searchautocomplete_ar"
  }
},
{
  "add": {
    "index": "ar_searchautocomplete_v" + version,
    "alias": "searchautocomplete_ar"
  }
}
,
{
  "remove": {
    "index": "de_searchautocomplete_v" + oldversion,
    "alias": "searchautocomplete_de"
  }
},
{
  "add": {
    "index": "de_searchautocomplete_v" + version,
    "alias": "searchautocomplete_de"
  }
}
,
{
  "remove": {
    "index": "zh_searchautocomplete_v" + oldversion,
    "alias": "searchautocomplete_zh"
  }
},
{
  "add": {
    "index": "zh_searchautocomplete_v" + version,
    "alias": "searchautocomplete_zh"
  }
}
,
{
  "remove": {
    "index": "id_searchautocomplete_v" + oldversion,
    "alias": "searchautocomplete_id"
  }
},
{
  "add": {
    "index": "id_searchautocomplete_v" + version,
    "alias": "searchautocomplete_id"
  }
}
,
{
  "remove": {
    "index": "th_searchautocomplete_v" + oldversion,
    "alias": "searchautocomplete_th"
  }
},
{
  "add": {
    "index": "th_searchautocomplete_v" + version,
    "alias": "searchautocomplete_th"
  }
}
,
{
  "remove": {
    "index": "sv_searchautocomplete_v" + oldversion,
    "alias": "searchautocomplete_sv"
  }
},
{
  "add": {
    "index": "sv_searchautocomplete_v" + version,
    "alias": "searchautocomplete_sv"
  }
}
,
{
  "remove": {
    "index": "tr_searchautocomplete_v" + oldversion,
    "alias": "searchautocomplete_tr"
  }
},
{
  "add": {
    "index": "tr_searchautocomplete_v" + version,
    "alias": "searchautocomplete_tr"
  }
}
,
{
  "remove": {
    "index": "ru_searchautocomplete_v" + oldversion,
    "alias": "searchautocomplete_ru"
  }
},
{
  "add": {
    "index": "ru_searchautocomplete_v" + version,
    "alias": "searchautocomplete_ru"
  }
}
,
{
  "remove": {
    "index": "pt_searchautocomplete_v" + oldversion,
    "alias": "searchautocomplete_pt"
  }
},
{
  "add": {
    "index": "pt_searchautocomplete_v" + version,
    "alias": "searchautocomplete_pt"
  }
}
,
{
  "remove": {
    "index": "br_searchautocomplete_v" + oldversion,
    "alias": "searchautocomplete_br"
  }
},
{
  "add": {
    "index": "br_searchautocomplete_v" + version,
    "alias": "searchautocomplete_br"
  }
}
,
{
  "remove": {
    "index": "it_searchautocomplete_v" + oldversion,
    "alias": "searchautocomplete_it"
  }
},
{
  "add": {
    "index": "it_searchautocomplete_v" + version,
    "alias": "searchautocomplete_it"
  }
}
,
{
  "remove": {
    "index": "hu_searchautocomplete_v" + oldversion,
    "alias": "searchautocomplete_hu"
  }
},
{
  "add": {
    "index": "hu_searchautocomplete_v" + version,
    "alias": "searchautocomplete_hu"
  }
}
,
{
  "remove": {
    "index": "nl_searchautocomplete_v" + oldversion,
    "alias": "searchautocomplete_nl"
  }
},
{
  "add": {
    "index": "nl_searchautocomplete_v" + version,
    "alias": "searchautocomplete_nl"
  }
}
,
{
  "remove": {
    "index": "no_searchautocomplete_v" + oldversion,
    "alias": "searchautocomplete_no"
  }
},
{
  "add": {
    "index": "no_searchautocomplete_v" + version,
    "alias": "searchautocomplete_no"
  }
}
,
{
  "remove": {
    "index": "es_searchautocomplete_v" + oldversion,
    "alias": "searchautocomplete_es"
  }
},
{
  "add": {
    "index": "es_searchautocomplete_v" + version,
    "alias": "searchautocomplete_es"
  }
}
,
{
  "remove": {
    "index": "fr_searchautocomplete_v" + oldversion,
    "alias": "searchautocomplete_fr"
  }
},
{
  "add": {
    "index": "fr_searchautocomplete_v" + version,
    "alias": "searchautocomplete_fr"
  }
}
,
{
  "remove": {
    "index": "fr_ca_searchautocomplete_v" + oldversion,
    "alias": "searchautocomplete_fr_ca"
  }
},
{
  "add": {
    "index": "fr_ca_searchautocomplete_v" + version,
    "alias": "searchautocomplete_fr_ca"
  }
}
,
{
  "remove": {
    "index": "cz_searchautocomplete_v" + oldversion,
    "alias": "searchautocomplete_cz"
  }
},
{
  "add": {
    "index": "cz_searchautocomplete_v" + version,
    "alias": "searchautocomplete_cz"
  }
},
{
  "remove": {
    "index": "en_searchautocomplete_v" + oldversion,
    "alias": "searchautocomplete_en"
  }
},
{
  "add": {
    "index": "en_searchautocomplete_v" + version,
    "alias": "searchautocomplete_en"
  }
}
]}
)

if indices == "yes":
  with open("sacindexcreator_count","w") as file:
    file.write(version+","+oldversion)