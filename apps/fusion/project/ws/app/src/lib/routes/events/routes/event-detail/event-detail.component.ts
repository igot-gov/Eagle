import { Component, OnInit, ElementRef, ViewChild } from '@angular/core'
// import { NSDiscussData } from '../../../discuss/models/discuss.model'
import { ActivatedRoute } from '@angular/router'
// import { MatSnackBar } from '@angular/material'
import { MatDialog } from '@angular/material/dialog'
// import { DiscussService } from '../../../discuss/services/discuss.service'
/* tslint:disable */
import _ from 'lodash'
import { EventsService } from '../../services/events.service'
/* tslint:enable */

@Component({
  selector: 'ws-app-event-detail',
  templateUrl: './event-detail.component.html',
  styleUrls: ['./event-detail.component.scss'],
})
export class EventDetailComponent implements OnInit {
  @ViewChild('toastSuccess', { static: true }) toastSuccess!: ElementRef<any>
  @ViewChild('toastError', { static: true }) toastError!: ElementRef<any>
  // data!: NSDiscussData.IDiscussionData
  similarPosts!: any
  defaultError = 'Something went wrong, Please try again after sometime!'
  eventId!: number
  fetchSingleCategoryLoader = false
  // fetchNewData = false

  constructor(
    public dialog: MatDialog,
    private route: ActivatedRoute,
    private eventSrvc: EventsService
    // private discussService: DiscussService,
    // private snackBar: MatSnackBar,
  ) { }

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.eventId = params.eventId
      if(this.eventId) {
        this.getEventDetails(this.eventId)
      }
      // if (this.fetchNewData) {
      //   this.getTIDData()
      // }
      // this.data = this.route.snapshot.data.topic.data
    })
    // this.fetchSingleCategoryDetails(this.data.cid)
  }

  getEventDetails(eventIdentifier: any) {
    let reqObj = {
        "locale": [
          "en"
        ],
        "pageSize": 25,
        "query": "all",
        "didYouMean": true,
        "filters": [
            {
                "andFilters": [
                    {
                        "contentType": [
                            "Event",
                         ]
                    },
                    {
                        "identifier": [
                          eventIdentifier,
                        ]
                    }
                ]
            }
        ],
        "includeSourceFields": [
            "creatorLogo", "thumbnail"
        ]
    }

    this.eventSrvc.getEvents(reqObj).subscribe((res: any) => {
      this.setEventData(res);
    })
  }

  setEventData(responseObj: any) {
    if(responseObj.result != undefined) {
        let eventObj = responseObj.result;
        console.log(eventObj[0])
        //const expiryDateFormat = this.customDateFormat(eventObj[0].lastUpdatedOn)
        //const eventUpdateDate = this.customDateFormat(eventObj[0].publishedOn)
        const eventDataObj = {
          eventName: eventObj.name,
          eventDate: expiryDateFormat,
          eventUpdatedOn: eventUpdateDate,
          eventDuration: eventObj.duration,
          eventjoined: (eventObj.creatorDetails !== undefined && eventObj.creatorDetails.length > 0) ?  ((eventObj.creatorDetails.length === 1) ?
            '1 person' :  `${eventObj.creatorDetails.length} people`) : ' --- ',
          eventThumbnail: (eventObj.thumbnail !== null || eventObj.thumbnail !== undefined) ? eventObj.thumbnail : '---',
          eventDescription: eventObj.description,
          eventStatus: eventObj.status,
          eventObjective: eventObj.learningObjective,
          eventPresenters: (eventObj.creatorDetails !== undefined && eventObj.creatorDetails.length > 0) ? eventObj.creatorDetails : '',
          identifier: eventObj.identifier,
        }
    }
  }

  // fetchSingleCategoryDetails(cid: number) {
    // this.fetchSingleCategoryLoader = true
    // this.discussService.fetchSingleCategoryDetails(cid).subscribe(
    //   (data: NSDiscussData.ICategoryData) => {
    //     this.similarPosts = data.topics
    //     this.fetchSingleCategoryLoader = false
    //   },
    //   (err: any) => {
    //     this.openSnackbar(err.error.message.split('|')[1] || this.defaultError)
    //     this.fetchSingleCategoryLoader = false
    //   })
  // }

  // private openSnackbar(primaryMsg: string, duration: number = 5000) {
  //   this.snackBar.open(primaryMsg, 'X', {
  //     duration,
  //   })
  // }

}
