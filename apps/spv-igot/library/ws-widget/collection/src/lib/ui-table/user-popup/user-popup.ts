import { Component, OnInit, Inject } from '@angular/core'
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog'
// tslint:disable-next-line:import-spacing
// import  *  as  contentQuality  from  './content-quality.json'
export interface IDialogData {
  animal: string
  name: string
  data: any
}

@Component({
  selector: 'ws-widget-user-list-popup',
  templateUrl: './user-popup.html',
  styleUrls: ['./user-popup.scss'],
})
export class UserPopupComponent implements OnInit {

  dataSources: any
  finalArray = []
  showTable = true
  score: any
  currentSelection = false
  first = true
  constructor(
    public dialogRef: MatDialogRef<UserPopupComponent>,

    @Inject(MAT_DIALOG_DATA) public data: IDialogData,
  ) {
  }

  ngOnInit() {
    // tslint:disable-next-line:max-line-length
    this.dataSources = [
      [{
        position: 1, type: 'InstructionalMethods',
        question: 'The course puts the learner at the centre of the learning experience.',
        ans: [{ name: 'Strongly Agree', weight: 5 }, { name: 'Agree', weight: 3 },
        { name: 'Disagree', weight: 1 }, { name: 'Strongly Disagree', weight: 0 }],
      },
      // tslint:disable-next-line:max-line-length
      {
        position: 2, type: 'InstructionalMethods',
        // tslint:disable-next-line:max-line-length
        question: 'The course is designed according to the Watch-Think-Do-Explore-Test content framework and all the elements have been incorporated in the course', ans: [{ name: 'Strongly Agree', weight: 5 }, { name: 'Agree', weight: 3 }, { name: 'Disagree', weight: 1 }, { name: 'Strongly Disagree', weight: 0 }],
      },
      // tslint:disable-next-line:max-line-length
      { position: 3, type: 'InstructionalMethods', question: 'The course is not heavily reliant on the traditional lecture/didactic approach and engages the learner on a more interactive journey through use of animations and simulations', ans: [{ name: 'Strongly Agree', weight: 5 }, { name: 'Agree', weight: 3 }, { name: 'Disagree', weight: 1 }, { name: 'Strongly Disagree', weight: 0 }] },
      // tslint:disable-next-line:max-line-length
      { position: 4, type: 'InstructionalMethods', question: 'The course taps into learner motivations, emotions, and needs.', ans: [{ name: 'Strongly Agree', weight: 5 }, { name: 'Agree', weight: 3 }, { name: 'Disagree', weight: 1 }, { name: 'Strongly Disagree', weight: 0 }] },
      // tslint:disable-next-line:max-line-length
      { position: 5, type: 'InstructionalMethods', question: 'Learners are encouraged to engage in higher-order thinking / build upon prior learning through scenario based questionnaire at end of each module', ans: [{ name: 'Strongly Agree', weight: 5 }, { name: 'Agree', weight: 3 }, { name: 'Disagree', weight: 1 }, { name: 'Strongly Disagree', weight: 0 }] },
      // tslint:disable-next-line:max-line-length
      { position: 6, type: 'InstructionalMethods', question: 'The course instruction includes activities that promote active learning.', ans: [{ name: 'Strongly Agree', weight: 5 }, { name: 'Agree', weight: 3 }, { name: 'Disagree', weight: 1 }, { name: 'Strongly Disagree', weight: 0 }] },
      // tslint:disable-next-line:max-line-length
      { position: 7, type: 'InstructionalMethods', question: 'Practice and reinforcement activities provide diagnostic feedback.', ans: [{ name: 'Strongly Agree', weight: 5 }, { name: 'Agree', weight: 3 }, { name: 'Disagree', weight: 1 }, { name: 'Strongly Disagree', weight: 0 }] },
      // tslint:disable-next-line:max-line-length
      { position: 8, type: 'InstructionalMethods', question: 'Learning activities cater for a variety of learning styles (Visual, Auditory, Kinesthetic).', ans: [{ name: 'Strongly Agree', weight: 5 }, { name: 'Agree', weight: 3 }, { name: 'Disagree', weight: 1 }, { name: 'Strongly Disagree', weight: 0 }] },
      // tslint:disable-next-line:max-line-length
      { position: 9, type: 'InstructionalMethods', question: 'Readability levels are appropriate for the target audience.', ans: [{ name: 'Strongly Agree', weight: 5 }, { name: 'Agree', weight: 3 }, { name: 'Disagree', weight: 1 }, { name: 'Strongly Disagree', weight: 0 }] },
      ],
      [
        // tslint:disable-next-line:max-line-length
        { position: 1, type: 'AssessmentDesign', question: 'The course features a post-assessment.', ans: [{ name: 'Strongly Agree', weight: 5 }, { name: 'Agree', weight: 3 }, { name: 'Disagree', weight: 1 }, { name: 'Strongly Disagree', weight: 0 }] },
        // tslint:disable-next-line:max-line-length
        { position: 2, type: 'AssessmentDesign', question: 'Passing criteria is clearly stated.', ans: [{ name: 'Strongly Agree', weight: 5 }, { name: 'Agree', weight: 3 }, { name: 'Disagree', weight: 1 }, { name: 'Strongly Disagree', weight: 0 }] },
        // tslint:disable-next-line:max-line-length
        { position: 3, type: 'AssessmentDesign', question: 'The assessment includes a variety of question formats to test mastery.', ans: [{ name: 'Strongly Agree', weight: 5 }, { name: 'Agree', weight: 3 }, { name: 'Disagree', weight: 1 }, { name: 'Strongly Disagree', weight: 0 }] },
        // tslint:disable-next-line:max-line-length
        { position: 4, type: 'AssessmentDesign', question: 'Distractors are plausible.', ans: [{ name: 'Strongly Agree', weight: 5 }, { name: 'Agree', weight: 3 }, { name: 'Disagree', weight: 1 }, { name: 'Strongly Disagree', weight: 0 }] },
        // tslint:disable-next-line:max-line-length
        { position: 5, type: 'AssessmentDesign', question: 'The assessment uses easy-to-understand language and terms.', ans: [{ name: 'Strongly Agree', weight: 5 }, { name: 'Agree', weight: 3 }, { name: 'Disagree', weight: 1 }, { name: 'Strongly Disagree', weight: 0 }] },
        // tslint:disable-next-line:max-line-length
        { position: 6, type: 'AssessmentDesign', question: 'The assessment does not overly rely on True/False questions.', ans: [{ name: 'Strongly Agree', weight: 5 }, { name: 'Agree', weight: 3 }, { name: 'Disagree', weight: 1 }, { name: 'Strongly Disagree', weight: 0 }] },
        // tslint:disable-next-line:max-line-length
        { position: 7, type: 'AssessmentDesign', question: 'The assessment avoids "All of the above" and/or "None of the above" answers. (They call attention to themselves and are often correct!)', ans: [{ name: 'Strongly Agree', weight: 5 }, { name: 'Agree', weight: 3 }, { name: 'Disagree', weight: 1 }, { name: 'Strongly Disagree', weight: 0 }] }],
      // tslint:disable-next-line:max-line-length
      [{ position: 1, type: 'CompetencyandSkills', question: 'Target competencies are clearly stated at the beginning and their fulfillment is measured at the end of the course.', ans: [{ name: 'Strongly Agree', weight: 5 }, { name: 'Agree', weight: 3 }, { name: 'Disagree', weight: 1 }, { name: 'Strongly Disagree', weight: 0 }] },
      // tslint:disable-next-line:max-line-length
      { position: 2, type: 'CompetencyandSkills', question: 'Learning materials support the fulfillment of target competencies and/or skill acquisition.', ans: [{ name: 'Strongly Agree', weight: 5 }, { name: 'Agree', weight: 3 }, { name: 'Disagree', weight: 1 }, { name: 'Strongly Disagree', weight: 0 }] },
      // tslint:disable-next-line:max-line-length
      { position: 3, type: 'CompetencyandSkills', question: 'Target competencies and skills readiness are assessed in the course.', ans: [{ name: 'Strongly Agree', weight: 5 }, { name: 'Agree', weight: 3 }, { name: 'Disagree', weight: 1 }, { name: 'Strongly Disagree', weight: 0 }] },
      // tslint:disable-next-line:max-line-length
      { position: 4, type: 'CompetencyandSkills', question: 'Target competencies are relevant to learners needs.', ans: [{ name: 'Strongly Agree', weight: 5 }, { name: 'Agree', weight: 3 }, { name: 'Disagree', weight: 1 }, { name: 'Strongly Disagree', weight: 0 }] },
        // tslint:disable-next-line:max-line-length
      ],
      // tslint:disable-next-line:max-line-length
      [{ position: 1, type: 'LearnerEngagement', question: 'The course can be completed in a self-paced manner over several short sessions.', ans: [{ name: 'Strongly Agree', weight: 5 }, { name: 'Agree', weight: 3 }, { name: 'Disagree', weight: 1 }, { name: 'Strongly Disagree', weight: 0 }] },
      // tslint:disable-next-line:max-line-length
      { position: 2, type: 'LearnerEngagement', question: 'Resources are bye sized learning (ideally 3-5 minutes long)', ans: [{ name: 'Strongly Agree', weight: 5 }, { name: 'Agree', weight: 3 }, { name: 'Disagree', weight: 1 }, { name: 'Strongly Disagree', weight: 0 }] },
      // tslint:disable-next-line:max-line-length
      { position: 3, type: 'LearnerEngagement', question: 'The technical quality of all media is good, i.e. videos and audios play with no distortion.', ans: [{ name: 'Strongly Agree', weight: 5 }, { name: 'Agree', weight: 3 }, { name: 'Disagree', weight: 1 }, { name: 'Strongly Disagree', weight: 0 }] },
      // tslint:disable-next-line:max-line-length
      { position: 4, type: 'LearnerEngagement', question: 'Reading content (e.g. PDF slides) is designed for on-the-go consumption and contains visual summaries, infographics and other similar techniques.', ans: [{ name: 'Strongly Agree', weight: 5 }, { name: 'Agree', weight: 3 }, { name: 'Disagree', weight: 1 }, { name: 'Strongly Disagree', weight: 0 }] },
      // tslint:disable-next-line:max-line-length
      // tslint:disable-next-line:max-line-length
      { position: 5, type: 'LearnerEngagement', question: 'The course features frequent reinforcement activities.', ans: [{ name: 'Strongly Agree', weight: 5 }, { name: 'Agree', weight: 3 }, { name: 'Disagree', weight: 1 }, { name: 'Strongly Disagree', weight: 0 }] },
      // tslint:disable-next-line:max-line-length
      { position: 6, type: 'LearnerEngagement', question: 'Real-life examples, scenarios and/or case studies are used in the course to increase engagement with learning.', ans: [{ name: 'Strongly Agree', weight: 5 }, { name: 'Agree', weight: 3 }, { name: 'Disagree', weight: 1 }, { name: 'Strongly Disagree', weight: 0 }] }],

      // tslint:disable-next-line:max-line-length
      [{ position: 1, type: 'LearnerSupport', question: 'Learners are able to download courses and complete in offline mode (e.g. Course avoids using YouTube links/ external to videos).', ans: [{ name: 'Strongly Agree', weight: 5 }, { name: 'Agree', weight: 3 }, { name: 'Disagree', weight: 1 }, { name: 'Strongly Disagree', weight: 0 }] },
      // tslint:disable-next-line:max-line-length
      { position: 2, type: 'LearnerSupport', question: 'Learners have access to additional resources that enrich the course content.', ans: [{ name: 'Strongly Agree', weight: 5 }, { name: 'Agree', weight: 3 }, { name: 'Disagree', weight: 1 }, { name: 'Strongly Disagree', weight: 0 }] },
      // tslint:disable-next-line:max-line-length
      { position: 3, type: 'LearnerSupport', question: 'Learners are given examples of and asked to explore practical applications of the course in their setting.', ans: [{ name: 'Strongly Agree', weight: 5 }, { name: 'Agree', weight: 3 }, { name: 'Disagree', weight: 1 }, { name: 'Strongly Disagree', weight: 0 }] },
      // tslint:disable-next-line:max-line-length
      { position: 4, type: 'LearnerSupport', question: 'Learners have access to troubleshooting resource(s) and contact details.', ans: [{ name: 'Strongly Agree', weight: 5 }, { name: 'Agree', weight: 3 }, { name: 'Disagree', weight: 1 }, { name: 'Strongly Disagree', weight: 0 }] },
      // tslint:disable-next-line:max-line-length
      { position: 5, type: 'LearnerSupport', question: 'The course features navigational help.', ans: [{ name: 'Strongly Agree', weight: 5 }, { name: 'Agree', weight: 3 }, { name: 'Disagree', weight: 1 }, { name: 'Strongly Disagree', weight: 0 }] },
        // tslint:disable-next-line:max-line-length
      ],
      // tslint:disable-next-line:max-line-length
      [{ position: 1, type: 'Accessibility', question: 'Videos feature closed captions.', ans: [{ name: 'Strongly Agree', weight: 5 }, { name: 'Agree', weight: 3 }, { name: 'Disagree', weight: 1 }, { name: 'Strongly Disagree', weight: 0 }] },
      // tslint:disable-next-line:max-line-length
      { position: 2, type: 'Accessibility', question: 'Images include descriptive alt tags.', ans: [{ name: 'Strongly Agree', weight: 5 }, { name: 'Agree', weight: 3 }, { name: 'Disagree', weight: 1 }, { name: 'Strongly Disagree', weight: 0 }] },
      // tslint:disable-next-line:max-line-length
      { position: 3, type: 'Accessibility', question: 'The course uses both colour and symbols to convey messages or status changes.', ans: [{ name: 'Strongly Agree', weight: 5 }, { name: 'Agree', weight: 3 }, { name: 'Disagree', weight: 1 }, { name: 'Strongly Disagree', weight: 0 }] },
      // tslint:disable-next-line:max-line-length
      { position: 4, type: 'Accessibility', question: 'The course uses patterns and textures as opposed to only contrasting colours for elements that require emphasis.', ans: [{ name: 'Strongly Agree', weight: 5 }, { name: 'Agree', weight: 3 }, { name: 'Disagree', weight: 1 }, { name: 'Strongly Disagree', weight: 0 }] },
      ],

    ]
  }
  onNoClick(): void {
    this.dialogRef.close()
  }
  markAsComplete() {

    if (!this.currentSelection && this.first) {
      this.currentSelection = true
      this.first = false
    }

    // this.contentQualityService.getContentQualityScore(this.finalArray).subscribe(data => {
    //   this.score = (data.compositeScore || 0).toFixed('2')
    //   this.showTable = false
    //   this.updateSize()
    // })
  }
  dataHandler($event: any) {
    this.finalArray = $event
    this.currentSelection = false
    this.first = false
  }
  updateSize() {
    this.dialogRef.updateSize('auto', 'auto')
  }
}
