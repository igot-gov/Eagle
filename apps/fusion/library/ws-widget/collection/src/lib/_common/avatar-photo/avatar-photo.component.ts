import { Component, OnInit, Input } from '@angular/core'

@Component({
  selector: 'ws-widget-avatar-photo',
  templateUrl: './avatar-photo.component.html',
  styleUrls: ['./avatar-photo.component.scss'],
})
export class AvatarPhotoComponent implements OnInit {

  @Input()
  public photoUrl!: string

  @Input()
  public name!: string
  @Input() public size = ''
  public showInitials = false
  public initials!: string
  public circleColor!: string

  private colors = [
    // '#EB7181', // red
    '#468547', // green
    // '#000000', // black
    // '#3670B2', // blue
    // '#4E9E87',
    // '#7E4C8D',
  ]

  ngOnInit() {

    if (!this.photoUrl) {
      this.showInitials = true
      this.createInititals()
      const randomIndex = Math.floor(Math.random() * Math.floor(this.colors.length))
      this.circleColor = this.colors[randomIndex]
    }

  }

  private createInititals(): void {
    let initials = ''

    for (let i = 0; i < this.name.length; i += 1) {
      if (this.name.charAt(i) === ' ') {
        continue
      }

      if (this.name.charAt(i) === this.name.charAt(i)) {
        initials += this.name.charAt(i)

        if (initials.length === 2) {
          break
        }
      }
    }

    this.initials = initials.toUpperCase()
  }
}
