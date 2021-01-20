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
  eventDetails: any = []

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
        const eventDataObj = {
          eventName: eventObj[0].name,
          eventDate: eventObj[0].expiryDate,
          eventUpdatedOn: eventObj[0].lastUpdatedOn,
          eventDuration: eventObj[0].duration,
          eventjoined: eventObj[0].creatorContacts,
          eventThumbnail: (eventObj[0].thumbnail !== null || eventObj[0].thumbnail !== undefined) ? eventObj[0].thumbnail : '---',
          eventDescription: eventObj[0].description,
          eventStatus: eventObj[0].status,
          eventObjective: eventObj[0].learningObjective,
          eventPresenters: eventObj[0].creatorDetails,
          identifier: eventObj[0].identifier,
        }
        this.eventDetails.push(eventDataObj)
        console.log(this.eventDetails);
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
