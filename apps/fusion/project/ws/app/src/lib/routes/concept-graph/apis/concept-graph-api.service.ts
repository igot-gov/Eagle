import { Injectable } from '@angular/core'
import { HttpClient } from '@angular/common/http'
import { IConceptResult, IConceptAutoComplete } from '../models/conceptGraph.model'
import { Observable } from 'rxjs'
const API_ENDPOINTS = {
  concept: '/apis/protected/v8/concept/',
  conceptAutoComplete: '/apis/protected/v8/concept/autocomplete?pgno=0&pgsize=20',
}

@Injectable({
  providedIn: 'root',
})
export class ConceptGraphApiService {
  constructor(private http: HttpClient) { }

  getConcepts(ids: string): Observable<IConceptResult[]> {
    try {
      return this.http.get<IConceptResult[]>(`${API_ENDPOINTS.concept}${ids}`)
    } catch (e) {
      throw e
    }
  }

  getConceptsForQuery(query: string): Observable<IConceptAutoComplete[]> {
    try {
      const request = {
        query,
      }
      return this.http.post<IConceptAutoComplete[]>(
        `${API_ENDPOINTS.conceptAutoComplete}`,
        request,
      )
    } catch (e) {
      throw e
    }
  }
  getAutoCompleteDummy() {
    return [
      {
        concept_id: 281,
        name: 'Artificial intelligence',
        id: '695b2210-ab1f-41ef-a071-66cae463cf48',
      },
      {
        concept_id: 11712,
        name: 'Distributed artificial intelligence',
        id: '837927a7-4493-4711-a669-14769a30a02b',
      },
      {
        name: 'Association for the Advancement of Artificial Intelligence',
        id: '7b6b1199-e494-4a99-aae0-1588e289cb9c',
      },
      {
        concept_id: 3006,
        name: 'MIT Computer Science and Artificial Intelligence Laboratory',
        id: 'a4e84191-2507-4135-9e4a-3d5b2b66203b',
      },
    ]
  }
}
