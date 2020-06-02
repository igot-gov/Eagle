import { Component, OnInit, Input } from '@angular/core'
import { Router } from '@angular/router'
import { ConfigurationsService } from '@ws-widget/utils/src/public-api'
import { IAvailableCourses } from '../../models/compass-role.model'

@Component({
  selector: 'ws-app-role-card',
  templateUrl: './role-card.component.html',
  styleUrls: ['./role-card.component.scss'],
})
export class RoleCardComponent implements OnInit {
  defaultThumbnail = ''
  @Input() courseDetails: IAvailableCourses = {} as IAvailableCourses
  constructor(
    private router: Router,
    private configSvc: ConfigurationsService,
  ) { }

  ngOnInit() {
    const instanceConfig = this.configSvc.instanceConfig
    if (instanceConfig) {
      this.defaultThumbnail = instanceConfig.logos.defaultContent
    }
  }

  onCourseClick(courseDetail: IAvailableCourses) {
    this.router.navigateByUrl(`/app/toc/${courseDetail.lex_course_id}/overview`)
  }
}
