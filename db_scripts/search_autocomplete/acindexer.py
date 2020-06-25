from elasticsearch5 import Elasticsearch
from elasticsearch5 import helpers
import os
import uuid
import datetime

esUrl = os.environ['esurl']
esPort = os.environ['esport']
esPass = os.environ['espass']
esUser = os.environ['esuser']
rootOrg = os.environ['rootOrg']
org = os.environ['org']

esObj = Elasticsearch([{"host":esUrl,"port":esPort}],http_auth=(esUser,esPass))

response = esObj.search("mlsearch_*","searchresources",
 '''{
        "size":1000,
        "_source":["locale","keywords","catalogPaths","name","sourceName","sourceShortName"]
    }''',
    scroll="5s")
result_pending = [response]
cnt = 1
indexDocs = []
stData = {}
while result_pending:
    print("OBJECT NUMBER " + str(cnt))
    cnt += 1
    curr_obj = result_pending.pop()
    scroll_id = curr_obj.get("_scroll_id")
    for hit in curr_obj["hits"]["hits"]:
        hitSource = hit["_source"]
        for key,val in hitSource.items():
            if not key == "locale":
                if isinstance(val,(list)):
                    for item in val:
                        x = stData.get(hitSource["locale"],set())
                        x.add(item.strip())
                        stData[hitSource["locale"]] = x
                else:
                    x = stData.get(hitSource["locale"],set())
                    x.add(val.strip())
                    stData[hitSource["locale"]] = x
    response = esObj.scroll(body='''{"scroll":"5s","scroll_id":"'''+scroll_id+'''"}''')
    if response["hits"]["hits"]:
        result_pending.append(response)

for key,val in stData.items():
    for item in val:
        doc = {
            "dateInserted": datetime.datetime.now().strftime("%Y%m%dT%H%M%S+0000"),
            "id": str(uuid.uuid4()),
            "org":org,
            "rootOrg":rootOrg,
            "searchTerm":item,
            "searchTermAnalysed":item.lower(),
            "isSuggested":False,
            "locale":key
        }
        indexDocs.append(doc)

def update_obj(id,obj,locale):
    try:
        indexName = "searchautocomplete_"+locale
        obj.pop("locale",None)
        es_update_obj = {
            '_op_type':'index',
            '_index': indexName,
            '_type': 'autocomplete',
            '_id':id,
            '_source':obj
        }
        return es_update_obj
    except Exception as e:
        print(repr(e))
updateList = []
for item in indexDocs:
    updateList.append(update_obj(item.get("id"),item,item.get("locale")))
    if len(updateList) % 1000 == 0:
        try:
            res = helpers.bulk(esObj,updateList)
            print(str(res))
        except Exception as e:
            print(e)
        updateList = []
if(len(updateList)>0):
    try:
        res = helpers.bulk(esObj,updateList)
        print(str(res))
    except Exception as e:
        print(repr(e))