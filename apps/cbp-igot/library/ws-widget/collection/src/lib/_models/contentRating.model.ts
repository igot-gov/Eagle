export interface IContentRating {
  averageRating: string
  ratingCount: number
  ratingInfo: IRatingInfo[]
}
export interface IRatingInfo {
  rating: number
  count: number
}
