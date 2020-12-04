import { Component, OnInit, Inject,} from '@angular/core'
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog'
export interface IDialogData {
  animal: string
  name: string
  data: any
}

@Component({
  selector: 'ws-auth-content-quality',
  templateUrl: './content-quality-popup.html',
  styleUrls: ['./content-quality-popup.scss'],
})
export class ContentQualityCheckPopupComponent implements OnInit {

  dataSources =  [
   [{position: 1,  type:'InstructionalMethods', question: 'The course puts the learner at the centre of the learning experience.', ans: [{name:'Strongly Agree', weight:4}, {name:'Agree', weight:3} , {name:'Disagree', weight:2}, {name:'Strongly Disagree', weight:1} ]},
    {position: 2,  type:'InstructionalMethods',question: 'The course is designed according to the Watch-Think-Do-Explore-Test content framework and all the elements have been incorporated in the course',ans: [{name:'Strongly Agree', weight:4}, {name:'Agree', weight:3} , {name:'Disagree', weight:2}, {name:'Strongly Disagree', weight:1} ] },
    {position: 3,  type:'InstructionalMethods',question: 'The course is not heavily reliant on the traditional lecture/didactic approach and engages the learner on a more interactive journey through use of animations and simulations',ans: [{name:'Strongly Agree', weight:4}, {name:'Agree', weight:3} , {name:'Disagree', weight:2}, {name:'Strongly Disagree', weight:1} ]},
    {position: 4,  type:'InstructionalMethods',question: 'The course taps into learner motivations, emotions, and needs.',ans: [{name:'Strongly Agree', weight:4}, {name:'Agree', weight:3} , {name:'Disagree', weight:2}, {name:'Strongly Disagree', weight:1} ] },
    {position: 5,  type:'InstructionalMethods',question: 'Learners are encouraged to engage in higher-order thinking / build upon prior learning through scenario based questionnaire at end of each module',ans: [{name:'Strongly Agree', weight:4}, {name:'Agree', weight:3} , {name:'Disagree', weight:2}, {name:'Strongly Disagree', weight:1} ] },
    {position: 6,  type:'InstructionalMethods',question: 'The course instruction includes activities that promote active learning.',ans: [{name:'Strongly Agree', weight:4}, {name:'Agree', weight:3} , {name:'Disagree', weight:2}, {name:'Strongly Disagree', weight:1} ] },
    {position: 7,  type:'InstructionalMethods',question: 'Practice and reinforcement activities provide diagnostic feedback.',ans: [{name:'Strongly Agree', weight:4}, {name:'Agree', weight:3} , {name:'Disagree', weight:2}, {name:'Strongly Disagree', weight:1} ] },
    {position: 8,  type:'InstructionalMethods',question: 'Learning activities cater for a variety of learning styles (Visual, Auditory, Kinesthetic).',ans: [{name:'Strongly Agree', weight:4}, {name:'Agree', weight:3} , {name:'Disagree', weight:2}, {name:'Strongly Disagree', weight:1} ] },
    {position: 9,  type:'InstructionalMethods',question: 'Readability levels are appropriate for the target audience.',ans: [{name:'Strongly Agree', weight:4}, {name:'Agree', weight:3} , {name:'Disagree', weight:2}, {name:'Strongly Disagree', weight:1} ] },
  ]
    ,
   [{position: 1,  type:'AssessmentDesign',question: 'The course features a post-assessment.',ans: [{name:'Strongly Agree', weight:4}, {name:'Agree', weight:3} , {name:'Disagree', weight:2}, {name:'Strongly Disagree', weight:1} ]},
    {position: 2, type:'AssessmentDesign',question: 'Passing criteria is clearly stated.',ans: [{name:'Strongly Agree', weight:4}, {name:'Agree', weight:3} , {name:'Disagree', weight:2}, {name:'Strongly Disagree', weight:1} ] },
    {position: 3, type:'AssessmentDesign',question: 'The assessment includes a variety of question formats to test mastery.',ans: [{name:'Strongly Agree', weight:4}, {name:'Agree', weight:3} , {name:'Disagree', weight:2}, {name:'Strongly Disagree', weight:1} ]},
    {position: 4, type:'AssessmentDesign',question: 'Distractors are plausible.',ans: [{name:'Strongly Agree', weight:4}, {name:'Agree', weight:3} , {name:'Disagree', weight:2}, {name:'Strongly Disagree', weight:1} ] },
    {position: 5, type:'AssessmentDesign',question: 'The assessment uses easy-to-understand language and terms.',ans: [{name:'Strongly Agree', weight:4}, {name:'Agree', weight:3} , {name:'Disagree', weight:2}, {name:'Strongly Disagree', weight:1} ] },
    {position: 6, type:'AssessmentDesign',question: 'The assessment does not overly rely on True/False questions.',ans: [{name:'Strongly Agree', weight:4}, {name:'Agree', weight:3} , {name:'Disagree', weight:2}, {name:'Strongly Disagree', weight:1} ] },
    {position: 7, type:'AssessmentDesign',question: 'The assessment avoids "All of the above" and/or "None of the above" answers. (They call attention to themselves and are often correct!)',ans: [{name:'Strongly Agree', weight:4}, {name:'Agree', weight:3} , {name:'Disagree', weight:2}, {name:'Strongly Disagree', weight:1} ] }],
  
    [{position: 1,  type:'CompetencyandSkills',question: 'Target competencies are clearly stated at the beginning and their fulfillment is measured at the end of the course.',ans: [{name:'Strongly Agree', weight:4}, {name:'Agree', weight:3} , {name:'Disagree', weight:2}, {name:'Strongly Disagree', weight:1} ]},
    {position: 2, type:'CompetencyandSkills',question: 'Learning materials support the fulfillment of target competencies and/or skill acquisition.',ans: [{name:'Strongly Agree', weight:4}, {name:'Agree', weight:3} , {name:'Disagree', weight:2}, {name:'Strongly Disagree', weight:1} ] },
    {position: 3, type:'CompetencyandSkills',question: 'Target competencies and skills readiness are assessed in the course.',ans: [{name:'Strongly Agree', weight:4}, {name:'Agree', weight:3} , {name:'Disagree', weight:2}, {name:'Strongly Disagree', weight:1} ]},
    {position: 4, type:'CompetencyandSkills',question: 'Target competencies are relevant to learners needs.',ans: [{name:'Strongly Agree', weight:4}, {name:'Agree', weight:3} , {name:'Disagree', weight:2}, {name:'Strongly Disagree', weight:1} ] },
    ]
    ,
    [{position: 1,  type:'LearnerEngagement',question: 'The course can be completed in a self-paced manner over several short sessions.',ans: [{name:'Strongly Agree', weight:4}, {name:'Agree', weight:3} , {name:'Disagree', weight:2}, {name:'Strongly Disagree', weight:1} ]},
     {position: 2, type:'LearnerEngagement',question: 'Resources are bye sized learning (ideally 3-5 minutes long)',ans: [{name:'Strongly Agree', weight:4}, {name:'Agree', weight:3} , {name:'Disagree', weight:2}, {name:'Strongly Disagree', weight:1} ] },
     {position: 3, type:'LearnerEngagement',question: 'The technical quality of all media is good, i.e. videos and audios play with no distortion.',ans: [{name:'Strongly Agree', weight:4}, {name:'Agree', weight:3} , {name:'Disagree', weight:2}, {name:'Strongly Disagree', weight:1} ]},
     {position: 4, type:'LearnerEngagement',question: 'Reading content (e.g. PDF slides) is designed for on-the-go consumption and contains visual summaries, infographics and other similar techniques.',ans: [{name:'Strongly Agree', weight:4}, {name:'Agree', weight:3} , {name:'Disagree', weight:2}, {name:'Strongly Disagree', weight:1} ] },
     {position: 5, type:'LearnerEngagement',question: 'The assessment uses easy-to-understand language and terms.',ans: [{name:'Strongly Agree', weight:4}, {name:'Agree', weight:3} , {name:'Disagree', weight:2}, {name:'Strongly Disagree', weight:1} ] },
     {position: 6, type:'LearnerEngagement',question: 'The course features frequent reinforcement activities.',ans: [{name:'Strongly Agree', weight:4}, {name:'Agree', weight:3} , {name:'Disagree', weight:2}, {name:'Strongly Disagree', weight:1} ] },
     {position: 7, type:'LearnerEngagement',question: 'Real-life examples, scenarios and/or case studies are used in the course to increase engagement with learning.',ans: [{name:'Strongly Agree', weight:4}, {name:'Agree', weight:3} , {name:'Disagree', weight:2}, {name:'Strongly Disagree', weight:1} ] }],

  ]
  constructor(
    public dialogRef: MatDialogRef<ContentQualityCheckPopupComponent>,
    @Inject(MAT_DIALOG_DATA) public data: IDialogData,
  ) {
  }

  ngOnInit(){
    
  }
  onNoClick(): void {
    this.dialogRef.close()
  }
  markAsComplete(){
    // this.dialogRef.close({ event: 'close', data: 'wait' })
  }
  dataHandler($event: any){
    // console.log($event)
  }
}
