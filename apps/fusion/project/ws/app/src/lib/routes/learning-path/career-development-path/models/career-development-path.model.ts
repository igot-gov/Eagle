import { ThemePalette } from '@angular/material/core'

export interface ITile {
  cols: number
  rows: number
  text?: string
  imageUrl?: string
  width: string
  height?: string
  top?: string
  title?: string
  displayName?: string
}
export interface ILpHomeTile {
  titleKey: string
  displayName: string
  description?: string
  link?: string
  cols: number
  rows: number
  imageUrl: string
  width: string
  type: string
  opacity: number
  greyScale?: string
}

export interface ICdpLandTile {
  titleKey: string
  displayName: string
  description: string
  link: string
  cols: number
  rows: number
  imageUrl: string
  width: string
  type: string
  opacity: number
  greyScale?: string
}
export interface ICdpChild {
  title: string
  children?: ICdpChild[]
  strips?: IWidgetData
  progress?: number
  img?: string
}

export interface ICdpTopicChild {
  title: string
  children: ICdpChild[]
  progress?: number
  img?: string
}

export interface IChipColor {
  name: string
  selectedColor: ThemePalette
  color: string
}

export interface IGrandChildData {
  name: string
  description: string
}

export interface INodeData {
  name: string
  type: string
  description: string
  imageUrl: string
  views?: number
  tags?: string
  completed?: boolean
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
