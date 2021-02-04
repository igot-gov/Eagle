import { AfterViewInit, Component, OnInit, OnDestroy } from '@angular/core'
import { Router } from '@angular/router'
import { EventsService } from '../../services/events.service'
import { ConfigurationsService } from '@ws-widget/utils/src/public-api'
import * as moment from 'moment'

@Component({
    selector: 'ws-app-list-event',
    templateUrl: './list-event.component.html',
    styleUrls: ['./list-event.component.scss'],
})
export class ListEventComponent implements OnInit, AfterViewInit, OnDestroy {
    tabledata: any = []
    data: any = []
    eventData: any = []
    math: any
    currentFilter = 'upcoming'
    discussionList!: any
    discussProfileData!: any
    userDetails: any
    location!: string | null
    tabs: any
    currentUser!: string | null
    connectionRequests!: any[]
    usersData!: any
    department: any

    constructor(
        private router: Router,
        private eventSvc: EventsService,
        private configSvc: ConfigurationsService,
        ) {
        this.math = Math

        this.currentUser = this.configSvc.userProfile && this.configSvc.userProfile.userId
        this.department = this.configSvc.userProfile && this.configSvc.userProfile.departmentName
    }

    ngOnInit() {
        this.tabledata = {
            columns: [
            { displayName: 'Cover Picture', key: 'eventThumbnail' },
            { displayName: 'Title', key: 'eventName' },
            { displayName: 'Date and time', key: 'eventDate' },
            { displayName: 'Created On', key: 'eventUpdatedOn' },
            { displayName: 'Duration', key: 'eventDuration' },
            { displayName: 'Joined', key: 'eventjoined' },
            ],
            needCheckBox: false,
            needHash: false,
            sortColumn: '',
            sortState: 'asc',
        }
        this.fetchEvents()
    }

    ngAfterViewInit() {
    }

    onEventClick(event: any) {
        this.router.navigate([`/app/events/${event.id}`])
    }

    fetchEvents() {
        const requestObj = {
            locale: ['en'],
            pageSize: 25,
            query: 'all',
            didYouMean: true,
            filters: [
            {
                andFilters: [
                { lastUpdatedOn: ['month'] },
                { contentType: ['Event'] },
                { sourceName: [this.department] },
                ],
            },
            ],
            includeSourceFields: ['creatorLogo', 'thumbnail'],
        }
        this.eventSvc.searchEvent(requestObj).subscribe(events => {
            this.setEventListData(events)
        })
    }

    setEventListData(eventObj: any) {
        if (eventObj !== undefined) {
            const data = eventObj.result
            this.eventData['pastEvents'] = []
            this.eventData['upcomingEvents'] = []
            Object.keys(data).forEach((index: any) => {
                const obj = data[index]
                const expiryDateFormat = this.customDateFormat(obj.expiryDate)
                const floor = Math.floor
                const hours = floor(obj.duration / 60)
                const minutes = obj.duration % 60
                const duration = (hours === 0) ? ((minutes === 0) ? '---' : `${minutes} minutes`) : (minutes === 0) ? (hours === 1) ?
                `${hours} hour` : `${hours} hours` :  (hours === 1) ? `${hours} hour ${minutes} minutes` :
                `${hours} hours ${minutes} minutes`
                const eventDataObj = {
                    eventName: obj.name.substring(0, 30),
                    eventDate: this.allEventDateFormat(obj.expiryDate, true),
                    eventUpdatedOn: this.allEventDateFormat(obj.lastUpdatedOn, false),
                    eventDuration: duration,
                    eventjoined: (obj.creatorDetails !== undefined && obj.creatorDetails.length > 0) ?
                    ((obj.creatorDetails.length === 1) ? '1 person' :  `${obj.creatorDetails.length} people`) : ' --- ',
                    eventThumbnail: (obj.appIcon !== null || obj.appIcon !== undefined) ? obj.appIcon : '---',
                }
                const isPast = this.compareDate(expiryDateFormat);
                (isPast) ? this.eventData['pastEvents'].push(eventDataObj) : this.eventData['upcomingEvents'].push(eventDataObj)
            })
            this.filter('upcoming')
        }
    }

    customDateFormat(date: any) {
        const year  = date.split('T')[0].substring(0, 4)
        const month = date.split('T')[0].substring(4, 6)
        const dDate  = date.split('T')[0].substring(6, 8)
        const hour  = date.split('T')[1].substring(0, 2)
        const min  = date.split('T')[1].substring(2, 4)
        return `${dDate}-${month}-${year} ${hour}:${min}`
    }

    filter(key: string | 'timestamp' | 'best' | 'saved') {
        const upcomingEventsData: any[] = []
        const pastEventsData: any[] = []
        if (this.eventData['pastEvents'] && this.eventData['pastEvents'].length > 0) {
            this.eventData['pastEvents'].forEach((event: any) => {
                pastEventsData.push(event)
            })
        }

        if (this.eventData['upcomingEvents'] && this.eventData['upcomingEvents'].length > 0) {
            this.eventData['upcomingEvents'].forEach((event: any) => {
                upcomingEventsData.push(event)
            })
        }

        if (key) {
            this.currentFilter = key
            switch (key) {
                case 'upcoming':
                this.data = upcomingEventsData
                break
                case 'past':
                this.data = pastEventsData
                break
                default:
                this.data = upcomingEventsData
                break
            }
        }
    }

    onCreateClick() {
        this.router.navigate([`/app/users/create-user`])
    }

    onRoleClick(user: any) {
        this.router.navigate([`/app/users/${user.userId}/details`])
    }

    ngOnDestroy() {

    }

    compareDate(selectedDate: any) {
        const now = new Date()
        const today = moment(now).format('DD-MM-YYYY HH:mm')
        return (selectedDate < today) ? true : false
    }

    allEventDateFormat(datetime: any, timeAllow: any) {
        const dateTimeArr = datetime.split('T')
        const date = dateTimeArr[0]
        const year = date.substr(0, 4)
        const month = date.substr(4, 2)
        const day = date.substr(6, 2)
        const time = dateTimeArr[1]
        const hours = time.substr(0, 2)
        const minutes = time.substr(2, 2)
        const seconds = time.substr(4, 2)
        const formatedDate = new Date(year, month - 1, day, hours, minutes, seconds, 0)
        let format = 'YYYY-MM-DD hh:mm a'
        if (!timeAllow) {
            format = 'YYYY-MM-DD'
        }
        const readableDateMonth = moment(formatedDate).format(format)
        const finalDateTimeValue = `${readableDateMonth}`
        return finalDateTimeValue
    }

    formatTimeAmPm(futureDate: any) {
        let hours = futureDate.getHours()
        let minutes = futureDate.getMinutes()
        const ampm = hours >= 12 ? 'pm' : 'am'
        hours = hours % 12
        hours = hours ? hours : 12
        minutes = minutes < 10 ? `0${minutes}` : minutes
        const strTime = `${hours}:${minutes} ${ampm}`
        return strTime
    }
}
