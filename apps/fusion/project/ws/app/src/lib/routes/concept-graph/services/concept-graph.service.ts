import { Injectable } from '@angular/core'
import { Observable } from 'rxjs'
import { ConceptGraphApiService } from '../apis/concept-graph-api.service'
// tslint:disable-next-line: max-line-length
import {
  IConceptAutoComplete,
  IConceptResult,
  ILinkedIndex,
  IRelationChild,
  IRelationChildren,
  IRelationData,
  IRelationLinks,
  IReturnChild,
} from '../models/conceptGraph.model'
@Injectable({
  providedIn: 'root',
})
export class ConceptGraphService {
  constructor(private conceptApi: ConceptGraphApiService) {}
  getConcepts(ids: string): Observable<IConceptResult[]> {
    try {
      return this.conceptApi.getConcepts(ids)
    } catch (e) {
      throw e
    }
  }

  getAutoComplete(query: string): Observable<IConceptAutoComplete[]> {
    try {
      return this.conceptApi.getConceptsForQuery(query)
    } catch (e) {
      throw e
    }
  }
  getDummyAuto() {
    return this.conceptApi.getAutoCompleteDummy()
  }

  findRelations(raw: IRelationData) {
    try {
      const links: IRelationLinks[] = []
      let nodes: IReturnChild[] = []
      const data = this.deduplicatev2(raw)
      nodes = data.nodes
      // const idArray = data.idArray
      raw.children.map((current: IRelationChildren) => {
        links.push({
          source: raw.id,
          target: current.id,
          type: 'high',
          level: 'root',
        })
      })
      raw.children.map((outerTopic: IRelationChildren) => {
        if (outerTopic.children && outerTopic.children.length > 0) {
          outerTopic.children.map((relatedTopics: IRelationChild) => {
            links.push({
              source: outerTopic.id,
              target: relatedTopics.id,
              type: 'high',
              level: 'second',
            })
          })
        } else {
          links.push({
            source: raw.id,
            target: outerTopic.id,
            type: 'high',
            level: 'one',
          })
        }
      })
      return {
        nodes,
        links,
      }
    } catch (e) {
      throw e
    }
  }

  deduplicatev2(topicsRelations: IRelationData) {
    try {
      const tempArray = Object.assign(topicsRelations.children)
      const firstLevelTopicIds: number[] = [topicsRelations.id]
      // first level will not have duplicates.
      tempArray.map((current: IRelationChildren) => {
        firstLevelTopicIds.push(current.id)
      })
      tempArray.map((current: IRelationChildren) => {
        const newArray: IRelationChild[] = []
        // check with first level ids, and dont add these to new array if exists in first level ids.
        if (current.children && current.children.length > 0) {
          current.children.map((innerCurrent: IRelationChild) => {
            if (firstLevelTopicIds.indexOf(innerCurrent.id) === -1) {
              newArray.push(innerCurrent)
            }
            // //console.log(innerCurrent)
          })
          // replace children with de duplicated children.
          current.children = newArray
        } else {
          current.children = []
        }
      })

      // temp array has second first level dups.with parent per child. we will check now child vs child
      const secondLevelIds = [topicsRelations.id, undefined]
      tempArray.map((current: IRelationChildren) => {
        const newChilds: IRelationChild[] = []
        if (current.children && current.children.length > 0) {
          current.children.map((innerCurrent: IRelationChild, i: number) => {
            // if i=0 means its first childrens of first elem. we take thses as reference to de-duplicate from other children.
            if (i === 0) {
              secondLevelIds.push(innerCurrent.id)
              newChilds.push(innerCurrent)
            } else {
              // check with second level ids.
              if (secondLevelIds.indexOf(innerCurrent.id) === -1) {
                secondLevelIds.push(innerCurrent.id)
                newChilds.push(innerCurrent)
              }
            }
          })
          // /replace second level ids.
          current.children = newChilds
        } else {
          current.children = []
        }
      })

      // return new array
      const temp1: IRelationData = topicsRelations
      temp1.children = tempArray
      const idArr = []
      let finalarray: IReturnChild[] = []
      idArr.push(temp1.id)
      const obj = {
        id: temp1.id,
        name: temp1.name,
        isRoot: true,
        level: 'high',
        node: 'root',
        count: 0,
        index: 0,
      }
      finalarray.push(obj)
      temp1.children.map((crr: IRelationChildren) => {
        finalarray.push({
          id: crr.id,
          level: 'high',
          node: 'firstLevel',
          isRoot: false,
          name: crr.name,
          count: 0,
          index: 0,
        })
        if (crr.children && crr.children.length) {
          crr.children.map((inr: IRelationChild) => {
            finalarray.push({
              id: inr.id,
              level: 'low',
              node: 'secondLevel',
              isRoot: false,
              name: inr.name,
              count: 0,
              index: 0,
            })
          })
        }
      })
      const prefinalArray: IReturnChild[] = []
      const idArray: ILinkedIndex = {}
      let count = 0
      finalarray.map((current: IReturnChild) => {
        if (idArray[current.id] === undefined) {
          idArray[`${current.id}`] = count
          current.index = count
          count += 1
          prefinalArray.push(current)
        }
      })
      finalarray = Object.assign(prefinalArray)
      return {
        nodes: finalarray,
      }
    } catch (e) {
      throw e
    }
  }
}
