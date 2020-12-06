import { Component, Input, OnInit, Output, EventEmitter } from '@angular/core'
import { WidgetBaseComponent, NsWidgetResolver } from '@ws-widget/resolver'
import { ContentQualityService } from './content-quality-check.service'
export interface IQuestionList {
  type: string
  question: string
  position: number
  ans: IAnswers[]
}
export interface IAnswers {
  name: string,
  weight: number
}
export interface IMyObj {
  instructionalMethods: number[],
  assessmentDesign: number[],
  competencyandSkills: number[],
  learnerEngagement: number[],
  learnerSupport: number[],
  accessibility: number[],
}

@Component({
  selector: 'ws-auth-content-quality',
  templateUrl: './content-quality-check.component.html',
  styleUrls: ['./content-quality-check.component.scss'],

})
export class ContentQualityCheckComponent extends WidgetBaseComponent implements OnInit, NsWidgetResolver.IWidgetData<any>  {
  @Input()
  widgetData!: any
  displayedColumns: string[] = ['QuestionNo', 'Question', 'Answers']
  @Input()
  dataSource!: any
  @Output() finalDataEmit = new EventEmitter<any>()
  constructor(private contentQualtityService: ContentQualityService) {
    super()
    // this.myObject= new
  }
  ngOnInit() {
  }
  getContentQualityCheckValue(event: any, element: any) {
    const type = element.type
    if(this.contentQualtityService.myObject[type][0]===0){
      this.contentQualtityService.myObject[type][0]=event.weight
    }else{
      this.contentQualtityService.myObject[type].push(event.weight)
    }
    this.finalDataEmit.emit(this.contentQualtityService.myObject)
  }

}
