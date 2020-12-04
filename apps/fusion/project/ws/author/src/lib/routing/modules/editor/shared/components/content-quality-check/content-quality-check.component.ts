import { Component, Input, OnInit, Output } from '@angular/core'
import { WidgetBaseComponent, NsWidgetResolver } from '@ws-widget/resolver'
import { EventEmitter } from 'protractor';
export interface QuestionList {
  type:string
  question: string;
  position: number;
  ans: Answers[];
}

export interface Answers{
  name: string,
  weight:number
}

@Component({
  selector: 'ws-widget-content-quality',
  templateUrl: './content-quality-check.component.html',
  styleUrls: ['./content-quality-check.component.scss'],

})

export class ContentQualityCheckComponent extends WidgetBaseComponent implements OnInit, NsWidgetResolver.IWidgetData<any>  {

  @Input()
  widgetData!: any
  displayedColumns: string[] = ['QuestionNo', 'Question', 'Answers'];
  InstructionalMethods:any=[]
  AssessmentDesign:any=[]
  CompetencyandSkills:any=[]
  LearnerEngagement:any=[]
  LearnerSupport:any=[]
  Accessibility:any=[]
  @Input()
  dataSource!:any;
  
  @Output() finalDataEmit= new EventEmitter<>();
  ngOnInit(){
  }
  selectValue(event: Event, element: any){
    switch(element.type){
    case "InstructionalMethods":
      this.InstructionalMethods[element.position-1] =event.weight
      this.InstructionalMethods = Array.from(this.InstructionalMethods, item => item || 0);
      break;
    case "AssessmentDesign":
      this.AssessmentDesign[element.position-1] =event.weight
      this.AssessmentDesign = Array.from(this.AssessmentDesign, item => item || 0);
    break;
    case "CompetencyandSkills":
      this.CompetencyandSkills[element.position-1] =event.weight
      this.CompetencyandSkills = Array.from(this.CompetencyandSkills, item => item || 0);
    break;
    case "LearnerEngagement":
      this.LearnerEngagement[element.position-1] =event.weight
      this.LearnerEngagement = Array.from(this.LearnerEngagement, item => item || 0);
    break;
    case "LearnerSupport":
      this.LearnerSupport[element.position-1] =event.weight
      this.LearnerSupport = Array.from(this.LearnerSupport, item => item || 0);
    break;
    case "Accessibility":
      this.Accessibility[element.position-1] =event.weight
      this.Accessibility = Array.from(this.Accessibility, item => item || 0);
    break;
    }
    // const Obj={
    //   InstructionalMethods: this.InstructionalMethods,
    //   AssessmentDesign: this.AssessmentDesign,
    //   CompetencyandSkills: this.CompetencyandSkills,
    //   LearnerEngagement: this.LearnerEngagement,
    //   LearnerSupport: this.LearnerSupport,
    //   Accessibility: this.Accessibility
    // }
    // this.finalDataEmit.emit(Obj);
  }
}
