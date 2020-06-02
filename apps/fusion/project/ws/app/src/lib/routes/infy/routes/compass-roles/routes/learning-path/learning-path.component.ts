import { Component, OnInit } from '@angular/core'
import { ActivatedRoute, Router } from '@angular/router'
import { ConfigurationsService, NsPage } from '@ws-widget/utils'
import { RouteDataService } from '../../services/route-data.service'
@Component({
  selector: 'ws-app-learning-path',
  templateUrl: './learning-path.component.html',
  styleUrls: ['./learning-path.component.scss'],
})
export class LearningPathComponent implements OnInit {
  pageNavbar: Partial<NsPage.INavBackground> = this.configSvc.pageNavBar
  courses: any = []
  roleId = ''
  mandatoryCourses: any = []
  preRequisiteCourses: any = []
  desirableCourses: any = []
  constructor(
    private configSvc: ConfigurationsService,
    private routeSvc: RouteDataService,
    private route: ActivatedRoute,
    private router: Router,
  ) { }

  ngOnInit() {
    this.roleId = this.route.snapshot.params.roleId
    this.courses = this.routeSvc.getStoredData('availableCourses')
    if (this.courses === undefined) {
      this.router.navigate([`/app/infy/skills-role/${this.roleId}`])
    }
    this.fetchData()
  }
  fetchData() {
    if (this.courses.available_courses !== undefined) {
      this.courses.available_courses.map((cur: any) => {
        if (cur.learning_type === 'Mandatory') {
          this.mandatoryCourses.push(cur)
        } else if (cur.learning_type === 'Pre-requisite') {
          this.preRequisiteCourses.push(cur)
        } else {
          this.desirableCourses.push(cur)
        }
      })
    } else {
      this.router.navigate([`/app/infy/skills-role/${this.roleId}`])
    }
  }
}
