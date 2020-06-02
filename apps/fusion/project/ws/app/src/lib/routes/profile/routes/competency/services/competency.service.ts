import { Injectable } from '@angular/core'
import { Observable, ReplaySubject } from 'rxjs'
import { HttpClient, HttpHeaders } from '@angular/common/http'
import { NSCompetency } from '../models/competency.model'
import { map } from 'rxjs/operators'
import { ConfigurationsService } from '@ws-widget/utils'
// import { map } from 'rxjs/operators'

const PROTECTED_SLAG_V8 = `/LA1/api`
// const PROTECTED_SLAG_V8 = `https://wingspan-staging.infosysapps.com/LA1/api`

const LA_API_END_POINTS = {
  ASSESSMENTS: `${PROTECTED_SLAG_V8}/v1/assessment`,
  CERTIFICATES: `${PROTECTED_SLAG_V8}/v1/certification`,
}

@Injectable({
  providedIn: 'root',
})
export class AssessmentService {
  httpOptions = {
    headers: new HttpHeaders({
      validator_URL: `https://${this.configSvc.hostPath}/apis/protected/v8/user/validate`,
    }),
  }
  private assessmentSubject: ReplaySubject<NSCompetency.IAchievementsRes> | null = null
  private certificateSubject: ReplaySubject<NSCompetency.IAchievementsRes> | null = null
  constructor(private http: HttpClient, private configSvc: ConfigurationsService) {
  }

  getAssessmentDetails(
    startDate: string,
    endDate: string,
  ): Observable<NSCompetency.IAchievementsRes> {
    if (!this.assessmentSubject) {
      this.assessmentSubject = new ReplaySubject()
      this.fetchAssessments(startDate, endDate)
    }
    return this.assessmentSubject.asObservable()
  }

  getCertificateDetails(
    startDate: string,
    endDate: string,
  ): Observable<NSCompetency.IAchievementsRes> {
    if (!this.certificateSubject) {
      this.certificateSubject = new ReplaySubject()
      this.fetchCertificates(startDate, endDate)
    }
    return this.certificateSubject.asObservable()
  }

  getAssessmentForID(id: string, startDate: string, endDate: string) {
    if (!this.assessmentSubject) {
      this.assessmentSubject = new ReplaySubject()
      this.fetchAssessments(startDate, endDate)
    }

    return this.assessmentSubject
      .asObservable()
      .pipe(
        map((data: NSCompetency.IAchievementsRes) => {
          if (data.achievements) {
            data.achievements.find(assessment => assessment.id === id)
          }
        }),
      )
  }

  getCertificatesForID(id: string, startDate: string, endDate: string) {
    if (!this.certificateSubject) {
      this.certificateSubject = new ReplaySubject()
      this.fetchCertificates(startDate, endDate)
    }

    return this.certificateSubject
      .asObservable()
      .pipe(
        map((data: NSCompetency.IAchievementsRes) => {
          if (data.achievements) {
            data.achievements.find(certificate => certificate.id === id)
          }
        }),
      )
  }

  private fetchAssessments(startDate: string, endDate: string) {
    this.http
      .get<NSCompetency.IAchievementsRes>(
        `${LA_API_END_POINTS.ASSESSMENTS}?startDate=${startDate}&endDate=${endDate}`,
        this.httpOptions,
      )
      .subscribe(
        data => {
          if (!this.assessmentSubject) {
            this.assessmentSubject = new ReplaySubject(1)
          }
          const response: NSCompetency.IAchievementsRes = {
            ...data,
            achievements: data.assessments,
          }
          this.assessmentSubject.next(response)
        },
        _ => {
          if (!this.assessmentSubject) {
            this.assessmentSubject = new ReplaySubject(1)
          }
          this.assessmentSubject.next()
        },
      )
  }

  private fetchCertificates(startDate: string, endDate: string) {
    this.http
      .get<NSCompetency.IAchievementsRes>(
        `${LA_API_END_POINTS.CERTIFICATES}?startDate=${startDate}&endDate=${endDate}`,
        this.httpOptions,
      )
      .subscribe(
        data => {
          if (!this.certificateSubject) {
            this.certificateSubject = new ReplaySubject(1)
          }
          const response: NSCompetency.IAchievementsRes = {
            ...data,
            achievements: data.certifications,
          }
          this.certificateSubject.next(response)
        },
        _ => {
          if (!this.certificateSubject) {
            this.certificateSubject = new ReplaySubject(1)
          }
          this.certificateSubject.next()
        },
      )
  }

  getDetails(startDate: string, endDate: string): Observable<NSCompetency.IAchievementsRes> {
    return this.http
      .get<NSCompetency.IAchievementsRes>(
        `${LA_API_END_POINTS.ASSESSMENTS}?startDate=${startDate}&endDate=${endDate}`,
        this.httpOptions,
      )
  }
  getDetailsCertification(startDate: string, endDate: string): Observable<NSCompetency.IAchievementsRes> {
    return this.http
      .get<NSCompetency.IAchievementsRes>(
        `${LA_API_END_POINTS.CERTIFICATES}?startDate=${startDate}&endDate=${endDate}`,
        this.httpOptions,
      )
  }
}
