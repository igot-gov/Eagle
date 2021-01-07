export namespace IAtGlanceComponentData {
  export interface IData {
    displayName: string
    buttonName?: string
    contentType: string
    cost: string
    lastUpdate: string
    duration: string
    contentId: string,
    counts: ICounts
    customLink?: string
  }

  export interface ICounts {
    assessment: number
    course: number
    handsOn: number
    interactiveVideo: number
    learningModule: number
    other: number
    pdf: number
    podcast: number
    quiz: number
    video: number
    webModule: number
    webPage: number
    youtube: number
    [key: string]: number
  }
}
