import { Injectable } from '@angular/core'
import { ApiService } from '@ws/author/src/lib/modules/shared/services/api.service'
const GET_CONTENT_QUALITY_CHECK_SCORE = '/apis/protected/v8/content/evaluation'

@Injectable({
  providedIn: 'root',
})
export class ContentQualityService {
  myObject: any = {
    InstructionalMethods: [0],
    AssessmentDesign: [0],
    CompetencyandSkills: [0],
    LearnerEngagement: [0],
    LearnerSupport: [0],
    Accessibility: [0],
  }
  constructor(private apiService: ApiService) { }

  getContentQualityScore(data: any) {
      return this.apiService.post<any>(
        `${GET_CONTENT_QUALITY_CHECK_SCORE}`, data
      )
    }
}
