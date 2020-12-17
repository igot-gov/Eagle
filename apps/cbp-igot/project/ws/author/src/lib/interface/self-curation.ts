export namespace NSISelfCuration {
  export interface ISelfCurationData {
    primaryKey: IPrimaryKey
    total_pages: number
    profanity_word_count: number
    total_page_images: any
    score: number
    image_occurances: string
    overall_text_classification: string
    errorMessage: string
    profanityWordList: IProfanityWordList[]
    completed: true
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
