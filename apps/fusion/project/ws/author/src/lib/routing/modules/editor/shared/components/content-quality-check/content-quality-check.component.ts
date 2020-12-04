import { Component, Input, OnInit } from '@angular/core'
import { WidgetBaseComponent, NsWidgetResolver } from '@ws-widget/resolver'

export interface FinalObject{
  key : QuestionList;
}
export interface QuestionList {
  question: string;
  position: number;
  ans: Answers[];
}

export interface Answers{
  name: string,
  weight:number
}


const ELEMENT_DATA: QuestionList[] = [
  {position: 1, question: 'The course puts the learner at the centre of the learning experience.', ans: [{name:'Not Agree', weight:100}, {name:'Agree', weight:100} ]},
  {position: 2, question: 'The course is designed according to the Watch-Think-Do-Explore-Test content framework and all the elements have been incorporated in the course', ans: [{name:'Not Agree', weight:100}, {name:'Agree', weight:100} ] },
  {position: 3, question: 'The course is not heavily reliant on the traditional lecture/didactic approach and engages the learner on a more interactive journey through use of animations and simulations', ans: [{name:'Not Agree', weight:100}, {name:'Agree', weight:100} ]},
  {position: 4, question: 'The course taps into learner motivations, emotions, and needs.', ans: [{name:'Not Agree', weight:100}, {name:'Agree', weight:100} ] },
  {position: 5, question: 'Learners are encouraged to engage in higher-order thinking / build upon prior learning through scenario based questionnaire at end of each module', ans: [{name:'Not Agree', weight:100}, {name:'Agree', weight:100} ] },
];
@Component({
  selector: 'ws-widget-content-quality',
  templateUrl: './content-quality-check.component.html',
  styleUrls: ['./content-quality-check.component.scss'],

})

export class ContentQualityCheckComponent extends WidgetBaseComponent implements OnInit, NsWidgetResolver.IWidgetData<any>  {

  @Input()
  widgetData!: any
  displayedColumns: string[] = ['QuestionNo', 'Question', 'Answers'];
  dataSource = ELEMENT_DATA;
  ngOnInit(){
  }

}
