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
  @Input() initials?: string
  public showInitials = false
  public circleColor!: string

  // public initials!: string

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
      if (!this.initials) {
        this.createInititals()
      }
      const randomIndex = Math.floor(Math.random() * Math.floor(this.colors.length))
      this.circleColor = this.colors[randomIndex]
    }

  }

  private createInititals(): void {
    let initials = ''
    const array = `${this.name} `.toString().split(' ')
    if (array[0] !== 'undefined' && typeof array[1] !== 'undefined') {
      initials += array[0].charAt(0)
      initials += array[1].charAt(0)
    } else {
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
    }
    this.initials = initials.toUpperCase()
  }
}
