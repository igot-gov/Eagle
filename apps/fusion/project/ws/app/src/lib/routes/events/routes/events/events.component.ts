import { Component, OnInit } from '@angular/core'
import { Router } from '@angular/router'
import { FormControl } from '@angular/forms'
import { EventsService } from '../../services/events.service'
import * as moment from 'moment'
import { ConfigurationsService } from '@ws-widget/utils/src/public-api'

@Component({
    selector: 'ws-app-events',
    templateUrl: './events.component.html',
    styleUrls: ['./events.component.scss'],
})
export class EventsComponent implements OnInit {
    queryControl = new FormControl('')
    currentFilter = 'timestamp'
    pager = {}
    paginationData!: any
    currentActivePage!: any
    fetchNewData = false
    eventData: any = []
    todayEventsCount: any
    joinedByMeEventsCount: any
    allEventsCount: any
    department: any
    data: any = []
    subData: any = []
    currentSubFilter = 'upcoming'
    mymodel: any
    participantsArr: any = []
    subDataCount: any

    constructor(
        private router: Router,
        private eventSrvc: EventsService,
        private configSvc: ConfigurationsService,
        ) {
        this.getAllEventData()
        this.department = this.configSvc.userProfile && this.configSvc.userProfile.departmentName
        this.allEventsCount = 0
        this.todayEventsCount = 0
        this.joinedByMeEventsCount = 0
    }

    ngOnInit() {
    }

    navigateWithPage(page: any) {
        if (page !== this.currentActivePage) {
            this.router.navigate([`/app/event-hub/home`], { queryParams: { page } })
            this.fetchNewData = true
        }
    }

    getAllEventData() {
        const reqObj = {
            locale: [
            'en',
            ],
            pageSize: 50,
            query: 'all',
            didYouMean: true,
            filters: [
            {
                andFilters: [
                {
                    lastUpdatedOn: [
                    'month',
                    ],
                },
                {
                    contentType: [
                    'Event',
                    ],
                },
                ],
            },
            ],
            includeSourceFields: [
            'creatorLogo',
            'appIcon',
            ],
        }

        this.eventSrvc.getEvents(reqObj).subscribe((eventResponse: any) => {
            this.eventData['todayEvents'] = []
            this.eventData['allEvents'] = []
            this.eventData['joinedByMe'] = []

            if (eventResponse.result !== undefined) {
                const eventList = eventResponse.result
                this.allEventsCount = eventList.length

                Object.keys(eventList).forEach((index: any) => {
                    const eventObj = eventList[index]
                    const expiryDateFormat = this.customDateFormat(eventObj.expiryDate)
                    const eventStartEndDateArr = this.eventStartEndDateFormat(eventObj.expiryDate, eventObj.duration).split(' - ')
                    this.eventSrvc.getParticipants(eventObj.identifier).subscribe((participantsResponse: any) => {
                        this.participantsArr = participantsResponse
                        const joinEventDataObj = {
                            eventName: eventObj.name.replace(/http?.*?(?= |$)/g, ''),
                            eventDate: expiryDateFormat,
                            eventUpdatedOn: eventObj.lastUpdatedOn,
                            eventDuration: eventObj.duration,
                            eventjoined: (eventObj.creatorDetails !== undefined && eventObj.creatorDetails.length > 0) ?
                            ((eventObj.creatorDetails.length === 1) ? '1 person' :  `${eventObj.creatorDetails.length} people`) : ' --- ',
                            eventThumbnail: (eventObj.appIcon !== null || eventObj.appIcon !== undefined) ? eventObj.appIcon : '---',
                            eventDescription: eventObj.description,
                            eventStatus: eventObj.status,
                            eventObjective: eventObj.learningObjective,
                            eventPresenters: (eventObj.creatorContacts !== undefined && eventObj.creatorContacts.length > 0)
                            ? eventObj.creatorContacts : '',
                            identifier: eventObj.identifier,
                            presenters: eventObj.creatorDetails,
                            eventJoinURL: eventObj.artifactUrl,
                            eventSource: eventObj.sourceName,
                            expirtyDate: eventObj.expiryDate,
                            todayEventDate: this.eventDateFormat(eventObj.expiryDate, ''),
                            todayEventDateStr: this.eventDateFormat(eventObj.expiryDate, eventObj.duration),
                            allEventDate: this.allEventDateFormat(eventObj.expiryDate),
                            eventStartDate: eventStartEndDateArr[0],
                            eventEndDate: eventStartEndDateArr[1],
                            isPast: this.compareDate(eventStartEndDateArr[0], eventStartEndDateArr[1]),
                            participants: (this.participantsArr !== undefined) ? this.participantsArr : [],
                        }
                        if (this.setJoinedByMeEvents(this.participantsArr)) {
                            this.eventData['joinedByMe'].push(joinEventDataObj)
                        }
                        this.eventData['allEvents'].push(joinEventDataObj)
                        if (moment(eventStartEndDateArr[0]).isSame(moment(), 'day')) {
                            const todayEventObj = {
                                eventId: eventObj.identifier,
                                eventName: eventObj.name.replace(/http?.*?(?= |$)/g, ''),
                                todayEventDate: this.eventDateFormat(eventObj.expiryDate, ''),
                                todayEventDateStr: this.eventDateFormat(eventObj.expiryDate, eventObj.duration),
                                expirtyDate: eventObj.expirtyDate,
                                duration: eventObj.eventDuration,
                            }
                            this.eventData['todayEvents'].push(todayEventObj)
                        }
                    })
                })
            }
            setTimeout(
                () => {
                    this.todayEventsCount = this.eventData['todayEvents'].length
                    this.joinedByMeEventsCount = this.eventData['joinedByMe'].length
                    this.filter('all')
                    if (this.todayEventsCount > 0) {
                        this.sortTodayEvents()
                    }
                    this.getMyMDOEvents()
                },
                1000
            )
        })
    }

    getMyMDOEvents() {
        const reqObj = {
            locale: [
            'en',
            ],
            pageSize: 50,
            query: 'all',
            didYouMean: true,
            filters: [
            {
                andFilters: [
                {
                    lastUpdatedOn: [
                    'month',
                    ],
                },
                {
                    contentType: [
                    'Event',
                    ],
                },
                {
                    sourceName: [
                    this.department,
                    ],
                },
                ],
            },
            ],
            includeSourceFields: [
            'creatorLogo',
            'appIcon',
            ],
        }

        this.eventSrvc.getEvents(reqObj).subscribe((res: any) => {
            this.setMyMDOEventData(res)
        })
    }

    setMyMDOEventData(responseObj: any) {
        if (responseObj.result !== undefined) {
            const eventList = responseObj.result
            this.eventData['myMDOEvents'] = []
            Object.keys(eventList).forEach((index: any) => {
                const eventObj = eventList[index]
                const expiryDateFormat = this.customDateFormat(eventObj.expiryDate)
                const eventStartEndDateArr = this.eventStartEndDateFormat(eventObj.expiryDate, eventObj.duration).split(' - ')
                const eventDataObj = {
                    eventName: eventObj.name.replace(/http?.*?(?= |$)/g, ''),
                    eventDate: expiryDateFormat,
                    eventUpdatedOn: eventObj.lastUpdatedOn,
                    eventDuration: eventObj.duration,
                    eventjoined: (eventObj.creatorDetails !== undefined && eventObj.creatorDetails.length > 0) ?
                    ((eventObj.creatorDetails.length === 1) ? '1 person' :  `${eventObj.creatorDetails.length} people`) : ' --- ',
                    eventThumbnail: (eventObj.appIcon !== null || eventObj.appIcon !== undefined) ? eventObj.appIcon : '---',
                    eventDescription: eventObj.description,
                    eventStatus: eventObj.status,
                    eventObjective: eventObj.learningObjective,
                    eventPresenters: (eventObj.creatorContacts !== undefined && eventObj.creatorContacts.length > 0)
                    ? eventObj.creatorContacts : '',
                    identifier: eventObj.identifier,
                    presenters: eventObj.creatorDetails,
                    eventJoinURL: eventObj.artifactUrl,
                    eventSource: eventObj.sourceName,
                    expirtyDate: eventObj.expiryDate,
                    todayEventDate: this.eventDateFormat(eventObj.expiryDate, ''),
                    todayEventDateStr: this.eventDateFormat(eventObj.expiryDate, eventObj.duration),
                    allEventDate: this.allEventDateFormat(eventObj.expiryDate),
                    eventStartDate: eventStartEndDateArr[0],
                    eventEndDate: eventStartEndDateArr[1],
                    isPast: this.compareDate(eventStartEndDateArr[0], eventStartEndDateArr[1]),
                    participants: [],
                }
                this.eventData['myMDOEvents'].push(eventDataObj)
                this.joinedByMeEventsCount = this.eventData['joinedByMe'].length
            })
        }
    }

    setJoinedByMeEvents(participantsArr: any) {
        if (participantsArr.length > 0) {
            const myEmail = this.configSvc.userProfile && this.configSvc.userProfile.email
            if (participantsArr.find((x: any) => x.email === myEmail)) {
                return true
            }
        }
        return false
    }

    setParticipants(startDate: any, endDate: any, identifier: any) {
        const isPast = this.compareDate(startDate, endDate)
        const isBetween = moment(new Date()).isBetween(startDate, endDate)
        const usersArr: any = []
        if (isPast || isBetween) {
            this.eventSrvc.getParticipants(identifier).subscribe((res: any) => {
                if (res.length > 0) {
                    Object.keys(res).forEach((index: any) => {
                        const dataObj = res[index]
                        const userObj = {
                            first_name: dataObj.first_name,
                            last_name: dataObj.last_name,
                            email: dataObj.email,
                        }
                        usersArr.push(userObj)
                    })
                }
            })
            return usersArr
        }
    }

    sortTodayEvents() {
        this.eventData['todayEvents'].sort((a: any, b: any) => {
            const date1 = new Date(`1970/01/01 ${a.todayEventDate}`)
            const date2 = new Date(`1970/01/01 ${b.todayEventDate}`)
            return date1.getTime() - date2.getTime()
        })
    }

    scroll(el: HTMLElement) {
        el.scrollIntoView()
    }

    customDateFormat(date: any) {
        const year  = date.split('T')[0].substring(0, 4)
        const month = date.split('T')[0].substring(4, 6)
        const dDate  = date.split('T')[0].substring(6, 8)
        const hour  = date.split('T')[1].substring(0, 2)
        const min  = date.split('T')[1].substring(2, 4)
        return `${dDate}-${month}-${year} ${hour}:${min}`
    }

    compareDate(startDate: any, endDate: any) {
        const now = new Date()
        const today = moment(now).format('YYYY-MM-DD hh:mm a')
        const isBetween = moment(new Date()).isBetween(startDate, endDate)
        const isAfter = moment(endDate).isAfter(today)
        if (isAfter || isBetween) {
            return false
        }
        return true
    }

    isToday(eventDate: any) {
        const year  = eventDate.split('T')[0].substring(0, 4)
        const month = eventDate.split('T')[0].substring(4, 6)
        const dDate  = eventDate.split('T')[0].substring(6, 8)
        const today = new Date()
        const monthVal = `0${today.getMonth() + 1}`
        const returnVal = (parseInt(dDate, 10) === today.getDate() &&
            month === (monthVal).slice(-2) &&
            parseInt(year, 10) === today.getFullYear())
        return returnVal
    }

    isJoinedByme(userDetails: any) {
        const myUserId = this.configSvc.userProfile && this.configSvc.userProfile.userId
        Object.keys(userDetails).forEach((index: any) => {
            return (userDetails[index].id === myUserId) ? true : false
        })
    }

    filter(key: string | 'timestamp' | 'best' | 'saved') {
        const allData: any[] = []
        const mdoData: any[] = []
        if (this.eventData['allEvents'] && this.eventData['allEvents'].length > 0) {
            this.eventData['allEvents'].forEach((event: any) => {
                allData.push(event)
            })
        }

        if (this.eventData['myMDOEvents'] && this.eventData['myMDOEvents'].length > 0) {
            this.eventData['myMDOEvents'].forEach((event: any) => {
                mdoData.push(event)
            })
        }

        if (key) {
            this.currentFilter = key
            switch (key) {
                case 'all':
                this.data = allData
                break
                case 'myMDO':
                this.data = mdoData
                break
                default:
                this.data = allData
                break
            }
        }
        this.setEventSubFilter('upcoming')
    }

    applyFilter(newValue: any) {
        if (newValue !== '') {
            const input = newValue.toLowerCase()
            this.subData = this.subData.filter((tag: any) => {
                return tag.eventName.toLowerCase().indexOf(input) !== -1
            })
        } else {
            this.setEventSubFilter(this.currentSubFilter)
        }
    }

    setEventSubFilter(eventValue: any) {
        const upcomingEvents: any[] = []
        const pastEvents: any[] = []
        if (this.data && this.data.length > 0) {
            this.data.forEach((event: any) => {
                const isPast = this.compareDate(event.eventStartDate, event.eventEndDate)
                if (isPast) {
                    pastEvents.push(event)
                } else {
                    upcomingEvents.push(event)
                }
            })
        }
        if (eventValue) {
            this.currentSubFilter = eventValue
            switch (eventValue) {
                case 'upcoming':
                this.subData = upcomingEvents
                break
                case 'past':
                this.subData = pastEvents
                break
                default:
                this.subData = upcomingEvents
                break
            }
        }
        this.subData.sort((a: any, b: any) => {
            return b.allEventDate - a.allEventDate
        })
        this.subDataCount = this.subData.length
    }

    allEventDateFormat(datetime: any) {
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
        const readableDateMonth = moment(formatedDate).format('YYYY-MM-DD hh:mm a')
        const finalDateTimeValue = `${readableDateMonth}`
        return finalDateTimeValue
    }

    eventDateFormat(datetime: any, duration: any) {
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
        let finalDateTimeValue = ''
        let readableDateMonth = ''
        if (duration === '') {
            readableDateMonth = moment(formatedDate).format('hh:mm a')
            finalDateTimeValue = `${readableDateMonth}`
        } else {
            const getTime = formatedDate.getTime()
            const futureDate = new Date(getTime + duration * 60000)
            const formatedHoursMin = this.formatTimeAmPm(futureDate)
            readableDateMonth = moment(formatedDate).format('hh:mm a')
            finalDateTimeValue = `${readableDateMonth} - ${formatedHoursMin}`
        }
        return finalDateTimeValue
    }

    eventStartEndDateFormat(datetime: any, duration: any) {
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
        let finalDateTimeValue = ''
        let readableDateMonth = ''
        const getTime = formatedDate.getTime()
        const futureDate = new Date(getTime + duration * 60000)
        // const formatedHoursMin = this.formatTimeAmPm(futureDate)
        readableDateMonth = moment(formatedDate).format('YYYY-MM-DD hh:mm a')
        const endDate = moment(futureDate).format('YYYY-MM-DD hh:mm a')
        finalDateTimeValue = `${readableDateMonth} - ${endDate}`
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
