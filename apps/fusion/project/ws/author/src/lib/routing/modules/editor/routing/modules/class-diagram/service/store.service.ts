import { Injectable } from '@angular/core'
import { ClassDiagram } from '../component/class-diagram.class'

@Injectable()
export class ClassDiagramStoreService {

  collectiveData: { [key: string]: ClassDiagram } = {}
  hasChanged = false

  constructor() { }

  updateData(id: string, data: any) {
    this.collectiveData[id] = { ...this.collectiveData[id], ...data }
  }

}
