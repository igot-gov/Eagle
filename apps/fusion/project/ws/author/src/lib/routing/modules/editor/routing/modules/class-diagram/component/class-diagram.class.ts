import { IOptionObject } from '../interface/class-diagram.interface'

export class ClassDiagram {
  public timeLimit: number
  public problemStatement: string
  public options: IOptionObject
  constructor(init?: Partial<ClassDiagram>) {
    this.problemStatement = init ? init.problemStatement || '' : ''
    this.options = init ? init.options || { classes: [], relations: [] } : { classes: [], relations: [] }
    this.timeLimit = init ? init.timeLimit || 300 : 300
  }
}
