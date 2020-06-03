export interface IClassDiagram {
    timeLimit: number
    problemStatement: string
    options: IOptionObject
}

export interface IOptionObject {
    classes: IClassObject[]
    relations: IRelationObject[]
}

export interface IClassObject {
    name: string
    type: string
    access: string
    belongsTo: string
}

export interface IRelationObject {
    source: string
    relation: string
    target: string
}
