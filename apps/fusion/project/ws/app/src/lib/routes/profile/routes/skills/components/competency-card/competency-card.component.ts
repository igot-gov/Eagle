import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core'
import { MatDialog } from '@angular/material'
import { Router } from '@angular/router'
import { ConfigurationsService } from '@ws-widget/utils'
import { DeleteRoleComponent } from '../../routes/delete-role/delete-role.component'

@Component({
  selector: 'ws-app-competency-card',
  templateUrl: './competency-card.component.html',
  styleUrls: ['./competency-card.component.scss'],
})
export class CompetencyCardComponent implements OnInit {
  @Input() cardData = ''
  @Input() rolesCard = ''
  @Input() skillManager = ''
  @Input() isClient!: boolean
  @Input() owner!: boolean
  @Input() assign!: boolean
  defaultThumbnail = ''
  fetchImage = false
  @Output() deleteRoleEvent = new EventEmitter()
  constructor(
    private configSvc: ConfigurationsService,
    private router: Router,
    public dialog: MatDialog,
  ) { }

  ngOnInit() {
    const instanceConfig = this.configSvc.instanceConfig
    if (instanceConfig) {
      this.defaultThumbnail = instanceConfig.logos.defaultContent
      // //console.log('instanceConfig logos >', instanceConfig.logos.defaultContent)
    }
    this.fetchImage = true
  }
  onClickRole(action: string, cardId: any) {
    if (action === 'edit') {
      this.router.navigateByUrl(`/app/profile/skills/edit-role/${encodeURIComponent(cardId)}`)
    } else if (action === 'share') {
      this.router.navigateByUrl(`/app/profile/skills/assign-role/${encodeURIComponent(cardId)}`)
    } else {
      this.router.navigateByUrl(`/app/profile/skills/copy-role/${encodeURIComponent(cardId)}`)
    }
  }
  onCardClick(cardId: any) {
    this.router.navigateByUrl(`/app/profile/skills/role-details/${encodeURIComponent(cardId)}`)
  }
  openDialog(roleId: string): void {
    const dialogRef = this.dialog.open(DeleteRoleComponent, { data: roleId })
    dialogRef.afterClosed().subscribe((result: any) => {
      if (result === 'deleted') {
        this.deleteRoleEvent.emit('deleted')
      }
    })
  }
}
