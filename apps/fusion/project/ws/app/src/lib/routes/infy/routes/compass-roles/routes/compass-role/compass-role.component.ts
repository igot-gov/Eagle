import { Component, OnInit } from '@angular/core'
import { ActivatedRoute, Router } from '@angular/router'
import { ConfigurationsService, NsPage, TFetchStatus } from '@ws-widget/utils'
import { IAvailableCourses, ICompassRolesResponse, IProgramList } from '../../models/compass-role.model'
import { CompassRoleService } from '../../services/compass-role.service'
import { RouteDataService } from '../../services/route-data.service'
@Component({
  selector: 'ws-app-compass-role',
  templateUrl: './compass-role.component.html',
  styleUrls: ['./compass-role.component.scss'],
})
export class CompassRoleComponent implements OnInit {
  roleId = ''
  defaultThumbnail = ''
  roleData: ICompassRolesResponse | null = null
  availableCourses: IAvailableCourses[] = []
  fetchStatus: TFetchStatus = 'fetching'
  pageNavbar: Partial<NsPage.INavBackground> = this.configSvc.pageNavBar

  constructor(
    private compassSrv: CompassRoleService,
    private configSvc: ConfigurationsService,
    private router: Router,
    private routeSvc: RouteDataService,
    private route: ActivatedRoute,
  ) { }

  ngOnInit() {
    this.roleId = this.route.snapshot.params.roleId
    const instanceConfig = this.configSvc.instanceConfig
    if (instanceConfig) {
      this.defaultThumbnail = instanceConfig.logos.defaultContent
    }
    this.fetchStatus = 'fetching'
    this.compassSrv.getRoles(this.roleId).subscribe((roles: ICompassRolesResponse) => {
      // //console.log(roles)
      this.roleData = roles
      this.fetchStatus = 'done'
    },
      // tslint:disable-next-line:align
      () => {
        this.roleData = null
        this.fetchStatus = 'error'
      },
    )
  }
  onCourseClick(course: IProgramList) {
    this.routeSvc.setStoredData('availableCourses', course)
    this.router.navigateByUrl(`/app/infy/skills-role/${this.roleId}/lp/${course.lex_id}`)
  }

}
