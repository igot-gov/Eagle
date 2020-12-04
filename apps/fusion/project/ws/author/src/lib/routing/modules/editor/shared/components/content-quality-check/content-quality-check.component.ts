import { Component, Input, OnInit, Output } from '@angular/core'
import { WidgetBaseComponent, NsWidgetResolver } from '@ws-widget/resolver'
import { EventEmitter } from '@angular/core'
import _ from 'lodash'
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
instructionalMethods: number[] ,
assessmentDesign:number[],
competencyandSkills:number[], 
learnerEngagement:number[],
learnerSupport:number[],
accessibility:number[],
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
  myObject!: IMyObj

  constructor(){
    super()
    // this.myObject= new 
  }
  ngOnInit() {
  }
 
  selectValue(event: any, element: any) {
    const type=element.type +''
    console.log(this.myObject)
    let myvar=_.get(this.myObject,type)|| []
    console.log(myvar)
    myvar.push(event.weight)
    console.log(myvar)

    // switch (type) {
    // case 'instructionalMethods':
    //   let myvar=this.myObject[type]
    //   this.myObject[type[element.position  - 1]].push(event.weight)

    //   this.instructionalMethods = Array.from(this.instructionalMethods, item => item || 0)
    //   break
    // case 'AssessmentDesign':
    //   this.assessmentDesign[element.position - 1] = event.weight
    //   this.assessmentDesign = Array.from(this.assessmentDesign, item => item || 0)
    // break
    // case 'CompetencyandSkills':
    //   this.competencyandSkills[element.position - 1] = event.weight
    //   this.competencyandSkills = Array.from(this.competencyandSkills, item => item || 0)
    // break
    // case 'LearnerEngagement':
    //   this.learnerEngagement[element.position - 1] = event.weight
    //   this.learnerEngagement = Array.from(this.learnerEngagement, item => item || 0)
    // break
    // case 'LearnerSupport':
    //   this.learnerSupport[element.position - 1] = event.weight
    //   this.learnerSupport = Array.from(this.learnerSupport, item => item || 0)
    // break
    // case 'Accessibility':
    //   this.accessibility[element.position - 1] = event.weight
    //   this.accessibility = Array.from(this.accessibility, item => item || 0)
    // break
    }
    // const Obj={
    //   InstructionalMethods: this.instructionalMethods,
    //   AssessmentDesign: this.assessmentDesign,
    //   CompetencyandSkills: this.competencyandSkills,
    //   LearnerEngagement: this.learnerEngagement,
    //   LearnerSupport: this.learnerSupport,
    //   Accessibility: this.accessibility
    // }
    // this.finalDataEmit.emit(Obj);
  // }
}
