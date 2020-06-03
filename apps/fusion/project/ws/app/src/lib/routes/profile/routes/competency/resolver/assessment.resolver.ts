import { Injectable } from '@angular/core'
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router'
import { Observable } from 'rxjs'
import { AssessmentService } from '../services/competency.service'
import { take } from 'rxjs/operators'
import { NSCompetency } from '../models/competency.model'

@Injectable()
export class CompetencyResolverService
  implements Resolve<Observable<NSCompetency.IAchievementsRes>> {
  startDate = '2018-04-01'
  endDate = '2020-03-31'

  constructor(private assessSvc: AssessmentService) {}

  resolve(_route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<any> {
    const type: string = state.url.split('/')[state.url.split('/').length - 1]
    if (type === 'assessment') {
      return this.assessSvc.getAssessmentDetails(this.startDate, this.endDate).pipe(take(1))
    }
    return this.assessSvc.getCertificateDetails(this.startDate, this.endDate).pipe(take(1))
  }
}
