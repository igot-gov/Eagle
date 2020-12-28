export interface IConceptItem {
  score: string
  synonyms: string | null
  name: string
  id: number
}
export interface IConceptResult {
  related: IConceptItem[]
  synonyms: string | null
  name: string
  id: number
}
