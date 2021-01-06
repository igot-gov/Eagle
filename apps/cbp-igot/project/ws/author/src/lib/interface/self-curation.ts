export namespace NSISelfCuration {
  export interface ISelfCurationData {
    completed: boolean
    errorMessage: any
    image_occurances: string
    noOfPagesCompleted: number
    overall_text_classification: string
    primaryKey: IPrimaryKey
    profanityWordList: IProfanityWordList[]
    profanity_word_count: number
    score: number
    total_page_images: number
    total_pages: number
  }
  export interface IProfanityWordList {
    no_of_occurrence: number
    pageOccurred: number[]
    word: string
    level: any
    category: any
  }
  export interface IPrimaryKey {
    contentId: string
    pdfFileName: string

  }
}
