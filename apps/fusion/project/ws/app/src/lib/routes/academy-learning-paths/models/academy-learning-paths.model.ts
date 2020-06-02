import { NsContentStripMultiple } from '@ws-widget/collection'
import { NsWidgetResolver } from '@ws-widget/resolver'

export interface ILearningPaths {
  learningPaths: ILearningPath[]
}

export interface ILearningPath {
  id: string
  thumbnail?: string
  name: string
  description: string
  contents: NsWidgetResolver.IRenderConfigWithTypedData<
    NsContentStripMultiple.IContentStripMultiple
  >
}
