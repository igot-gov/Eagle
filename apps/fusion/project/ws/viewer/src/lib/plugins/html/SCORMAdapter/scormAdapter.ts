/* tslint:disable */
// import { HttpClient } from '@angular/common/http'
import { Injectable } from '@angular/core'
import { Storage } from './storage'
import { errorCodes } from './errors'
import _ from 'lodash'

@Injectable({
  providedIn: 'root',
})
export class SCORMAdapterService {
  apiURL = null
  constructor(private store: Storage) { }

  set uri(uri) {
    this.apiURL = uri
  }

  get uri() {
    return this.apiURL
  }

  LMSInitialize() {
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
    // delete data['errors']
    // delete data['Initialized']
    // data = JSON.stringify(data)
    // data = Base64.encode(data)
    // let response
    let _return = data ? true : false
    // $.post({
    //   url: this.apiURL,
    //   async: false,
    //   data: { data },
    //   success: res => {
    //     response = res
    //   },
    //   error: () => {
    //     this._setError(101)
    //     _return = false
    //   }
    // })
    // if (!response.success) {
    //   this._setError(101)
    //   return false
    // }
    return _return
  }

  LMSGetLastError() {
    let errors = this.store.getItem('errors')
    errors = JSON.parse(errors || '[]')
    if (errors && errors.length > 0) {
      // return errors.pop()
    }
    return ""
  }

  LMSGetErrorString(errorCode: number) {
    // let errorCodes = errorCode.toString()
    let error = errorCodes[errorCode]
    if (!error) return ""
    return error[errorCode]["errorString"]
  }

  LMSGetDiagnostic(errorCode: number) {
    // let errorCodes = errorCode.toString()
    let error = errorCodes[errorCode]
    if (!error) return ""
    return error[errorCode]["diagnostic"]
  }

  _isInitialized() {
    let initialized = this.store.getItem('Initialized')
    return initialized
  }

  _setError(errorCode: number) {
    // let errorCodes = errorCode.toString()
    let errors = this.store.getItem('errors')
    if (!errors) errors = '[]'
    errors = JSON.parse(errors)
      (errors).push(errorCode)
    errors = JSON.stringify(errors)
    this.store.setItem('errors', errors)
  }
}