/* tslint:disable */
// import { HttpClient } from '@angular/common/http'
import { Injectable } from '@angular/core'
import { Storage, IScromData } from './storage'
import { errorCodes } from './errors'
import _ from 'lodash'
import { HttpClient } from '@angular/common/http'
const API_END_POINTS = {
  SCROM_ADD_UPDTE: '/apis/protected/v8/scrom/add',
  SCROM_FETCH: '/apis/protected/v8/scrom/get',
}
@Injectable({
  providedIn: 'root',
})
export class SCORMAdapterService {
  id = ''
  constructor(private store: Storage, private http: HttpClient) { }

  set contentId(id: string) {
    this.store.key = id
    this.id = id
  }

  get contentId() {
    return this.id
  }

  LMSInitialize() {
    this.store.contentKey = this.contentId
    this.store.setItem('Initialized', true)
    return true
  }

  LMSFinish() {
    if (!this._isInitialized()) {
      this._setError(301)
      return false
    }
    let _return = this.LMSCommit()
    this.store.setItem('Initialized', false)
    this.store.clearAll()
    return _return
  }

  LMSGetValue(element: any) {
    if (!this._isInitialized()) {
      this._setError(301)
      return false
    }
    let value = this.store.getItem(element)
    if (!value) {
      this._setError(201)
      return ""
    }
    return value
  }

  LMSSetValue(element: any, value: any) {
    if (!this._isInitialized()) {
      this._setError(301)
      return false
    }
    this.store.setItem(element, value)
    return this.store.getItem(element)
  }

  LMSCommit() {
    let data = this.store.getAll()
    if (data) {
      delete data['errors']
      // delete data['Initialized']
      let newData = JSON.stringify(data)
      // data = Base64.encode(newData)
      let _return = false
      this.addData(newData).subscribe((response) => {
        console.log(response)
        _return = true
      }, (error) => {
        this._setError(101)
        if (error) {
          console.log(error)
        }
      })
      return _return
    }
    return false
  }

  LMSGetLastError() {
    const newErrors = JSON.parse(this.store.getItem('errors') || '[]')
    if (newErrors && newErrors.length > 0) {
      return newErrors.pop()
    }
    return ""
  }

  LMSGetErrorString(errorCode: number) {
    let error = errorCodes[errorCode]
    if (!error) return ""
    return error[errorCode]["errorString"]
  }

  LMSGetDiagnostic(errorCode: number) {
    let error = errorCodes[errorCode]
    if (!error) return ""
    return error[errorCode]["diagnostic"]
  }

  _isInitialized() {
    let initialized = this.store.getItem('Initialized')
    return initialized
  }

  _setError(errorCode: number) {
    let errors = this.store.getItem('errors')
    if (!errors) errors = '[]'
    const newErrors = JSON.parse(errors)
    if (newErrors && typeof (newErrors) === 'object') {
      newErrors.push(errorCode)
    }
    this.store.setItem('errors', errors)
  }
  loadData() {
    this.http.get<any>(API_END_POINTS.SCROM_FETCH + '/' + this.contentId).subscribe((response) => {
      console.log(response.result.data)
      const data = response.result.data
      const loadData: IScromData = {
        "cmi.core.exit": data["cmi.core.exit"],
        "cmi.core.lesson_status": data["cmi.core.lesson_status"],
        "cmi.core.session_time": data["cmi.core.session_time"],
        "cmi.suspend_data": data["cmi.suspend_data"],
        Initialized: data["Initialized"],
        errors: data["errors"]
      }
      this.store.setAll(loadData)
    }, (error) => {
      if (error) {
        console.log(error)
        this._setError(101)
      }
    })
  }
  addData(postData: any) {
    return this.http.post<any>(API_END_POINTS.SCROM_ADD_UPDTE + '/' + this.contentId, postData)
  }
}