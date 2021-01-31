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
                if(userData[index].name != undefined) {
                    const obj = {
                        id: userData[index].id,
                        name: userData[index].name,
                        shortName: this.getShortName(userData[index].name),
                        type: userData[index].type,
                        designation: userData[index].designation,
                    }
                    this.userdata.push(obj)
                } else {
                    const name = `${userData[index].first_name} ${userData[index].last_name}`
                    const obj = {
                        id: userData[index].user_id,
                        name: name,
                        shortName: this.getShortName(name),
                        type: '',
                        designation: userData[index].designation
                    }
                    this.userdata.push(obj)
                }
            })
        }
    }

    getShortName(name: any) {
        const matches = name.match(/\b(\w)/g)
        return matches.join('').toLocaleUpperCase()
    }
}
