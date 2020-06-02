export interface IDynamicNodes {
  id: number
  label: string
  group: number
}

export interface IDynamicEdges {
  from: number
  to: number
}
export interface IDynamicData {
  nodes: IDynamicNodes[]
  edges: IDynamicEdges[]
}

export interface IDynamicDataContent {
  name: string
  type: string
  description: string
  imageUrl: string
  views: number
  tags: string
  completed?: boolean
}

export interface IDynamicDemoNew {
  noOfContent: number
  childData: string[]
  author: string
}

export interface IDlpBreadCrumb {
  title: string
  displayName: string
  link: string
  id: number
}
export interface IGroupsDlp {
  size: number
  shape: string
}
export interface IWidgetData {
  widgetData: {
    strips: {
      key: string;
      title: string;
      request: {
        ids: string[];
      };
    }[];
  }
  widgetSubType: string
  widgetType: string
  widgetHostClass: string
}
export interface ITitleData {
  titleKey: string
  displayName: string
}
