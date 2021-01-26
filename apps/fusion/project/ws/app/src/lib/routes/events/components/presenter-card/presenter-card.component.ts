import { Component, OnInit, Input, OnChanges } from '@angular/core'

@Component({
    selector: 'app-presenter-card',
    templateUrl: './presenter-card.component.html',
    styleUrls: ['./presenter-card.component.scss'],
})
export class PresenterCardComponent implements OnInit, OnChanges {
    constructor() { }
    @Input() data?: []
    @Input() host?: any

    presenters: any = []
    userdata: any = []
    shortName: any

    ngOnInit() {

    }

    ngOnChanges() {
        if (this.data !== undefined) {
            const userData: any = []
            userData.push(this.data)
            Object.keys(userData).forEach((index: any) => {
                const obj = {
                    id: userData[index].id,
                    name: userData[index].name,
                    shortName: this.getShortName(userData[index].name),
                    color: this.random_rgba(),
                    type: userData[index].type,
                }
                this.userdata.push(obj)
            })
        }
    }

    getShortName(name: any) {
        const matches = name.match(/\b(\w)/g)
        return matches.join('').toLocaleUpperCase()
    }

    random_rgba() {
      const letters = '0123456789ABCDEF'
      let color = '#'
      for (let i = 0; i < 6; i = i + 1) {
        color += letters[Math.floor(Math.random() * 16)]
      }
      return color
    }

}
