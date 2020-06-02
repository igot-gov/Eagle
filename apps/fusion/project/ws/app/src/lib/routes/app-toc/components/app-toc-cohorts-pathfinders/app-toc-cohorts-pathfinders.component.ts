import { Component, Input, OnInit } from '@angular/core'
import { MatDialog } from '@angular/material'
import { ActivatedRoute } from '@angular/router'
import { MiniProfileComponent, NsContent } from '@ws-widget/collection'
import { ConfigurationsService } from '../../../../../../../../../library/ws-widget/utils/src/public-api'
import { NsTocPathfinders } from '../../models/app-toc-pathfinders-model'
import { AppTocPathfindersService } from '../../services/app-toc-pathfinders.service'

@Component({
  selector: 'ws-app-app-toc-cohorts-pathfinders',
  templateUrl: './app-toc-cohorts-pathfinders.component.html',
  styleUrls: ['./app-toc-cohorts-pathfinders.component.scss'],
})
export class AppTocCohortsPathfindersComponent implements OnInit {
  @Input() content!: NsContent.IContent
  @Input() forPreview = window.location.href.includes('/author/')
  cohortsDetails: NsTocPathfinders.IAttendedUsers[] = []
  fetchingCohorts = true

  constructor(
    private tocSvc: AppTocPathfindersService,
    private activatedRoute: ActivatedRoute,
    private dialog: MatDialog,
    private configSvc: ConfigurationsService,
  ) {}

  ngOnInit() {
    if (this.activatedRoute.parent && this.activatedRoute.parent.data) {
      this.activatedRoute.parent.data.subscribe((data: any) => {
        if (data && data.content && data.content.data) {
          this.content = data.content.data
          this.getCohorts()
        }
      })
    }
  }
  // getCohorts() {
  //   if (this.content && this.content.labels) {
  //     this.tocSvc.fetchCohortGroupUsers(parseInt(this.content.labels[0], 10)).subscribe(
  //       (res: NsCohorts.ICohortsGroupUsers[]) => {
  //         if (this.configSvc.userProfile) {
  //           this.cohortsDetails = res.filter(cohort =>
  //             cohort.email !== (this.configSvc.userProfile ? this.configSvc.userProfile.email : null))
  //         } else {
  //           this.cohortsDetails = res
  //         }
  //         this.cohortsDetails.forEach(user => {
  //           user.name = `${user.first_name} ${user.last_name}`
  //         })
  //         this.fetchingCohorts = false
  //       },
  //       _ => {
  //         this.fetchingCohorts = false
  //       },
  //     )
  //   }
  // }

  getCohorts() {
    if (this.content && !this.forPreview) {
      this.tocSvc.fetchAttendedUsers(this.content.identifier).subscribe(
        (response: NsTocPathfinders.IAttendedUsers[]) => {
          this.cohortsDetails = response
            ? response.filter(
                cohort =>
                  cohort.email !==
                  (this.configSvc.userProfile ? this.configSvc.userProfile.email : null),
              )
            : []
          this.fetchingCohorts = false
        },
        _ => {
          this.fetchingCohorts = false
        },
      )
    } else {
      this.fetchingCohorts = false
    }
  }
  openDialog(wid: string): void {
    const dialogRef = this.dialog.open(MiniProfileComponent, {
      width: '410px',
      data: wid,
    })
    dialogRef.afterClosed().subscribe((_result: any) => {})
  }
}
