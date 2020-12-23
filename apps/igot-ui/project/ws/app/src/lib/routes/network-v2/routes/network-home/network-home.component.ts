import { Component, OnInit } from '@angular/core'
import { ActivatedRoute, Router } from '@angular/router'
import { NSNetworkDataV2 } from '../../models/network-v2.model'
import { NetworkV2Service } from '../../services/network-v2.service'
import { ConfigurationsService } from '@ws-widget/utils/src/public-api'
import { CardNetWorkService } from '@ws-widget/collection/src/lib/card-network/card-network.service'

@Component({
  selector: 'ws-app-network-home',
  templateUrl: './network-home.component.html',
  styleUrls: ['./network-home.component.scss'],
  /* tslint:disable */
  host: { class: 'flex flex-1 mt-6 ' },
  /* tslint:enable */
})
export class NetworkHomeComponent implements OnInit {
  tabsData: NSNetworkDataV2.IProfileTab[]
  recommendedUsers!: NSNetworkDataV2.IRecommendedUserResult
  connectionRequests!: any
  enableFeature = true
  nameFilter = ''
  searchSpinner = false
  searchResultUserArray: any = []
  establishedConnections!: NSNetworkDataV2.INetworkUser[]
  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private networkV2Service: NetworkV2Service,
    private configSvc: ConfigurationsService,
    private cardNetworkService: CardNetWorkService,
  ) {
    this.tabsData = this.route.parent && this.route.parent.snapshot.data.pageData.data.tabs || []
    if (this.route.snapshot.data.recommendedUsers && this.route.snapshot.data.recommendedUsers.data.result) {
      this.recommendedUsers = this.route.snapshot.data.recommendedUsers.data.result.data.
      find((item: any) => item.field === 'employmentDetails.departmentName').results
    }
    this.establishedConnections = this.route.snapshot.data.myConnectionList.data.result.data.map((v: NSNetworkDataV2.INetworkUser) => {
      if (v && v.personalDetails && v.personalDetails.firstname) {
        v.personalDetails.firstname = v.personalDetails.firstname.toLowerCase()
      }
      return v
    })
    this.connectionRequests = this.route.snapshot.data.connectionRequests.data.result.data

  }

  ngOnInit() {
  }

  goToMyMdo() {
    this.router.navigate(['/app/network-v2/my-mdo'])
  }

  goToConnectionRequests() {
    this.router.navigate(['/app/network-v2/connection-requests'])
  }

  connectionUpdate(event: any) {
    if (event === 'connection-updated') {
      this.networkV2Service.fetchAllReceivedConnectionRequests().subscribe(
        (data: any) => {
          this.connectionRequests = data.result.data
        },
        (_err: any) => {
          // this.openSnackbar(err.error.message.split('|')[1] || this.defaultError)
        })
    }
  }

  connectionUpdateSearchCard(event: any) {
    if (event === 'connection-updated') {
      this.searchUser()
    }
  }

  connectionUpdatePeopleCard(event: any) {
    if (event === 'connection-updated') {
      let usrDept = 'iGOT'
      if (this.configSvc.userProfile) {
        usrDept = this.configSvc.userProfile.departmentName || 'iGOT'
      }
      let req: NSNetworkDataV2.IRecommendedUserReq
      req = {
        size: 50,
        offset: 0,
        search: [
          {
            field: 'employmentDetails.departmentName',
            values: [usrDept],
          },
        ],
      }
      this.networkV2Service.fetchAllRecommendedUsers(req).subscribe(
        (data: any) => {
          this.recommendedUsers = data.result.data
            .find((item: any) => item.field === 'employmentDetails.departmentName').results
        },
        (_err: any) => {
          // this.openSnackbar(err.error.message.split('|')[1] || this.defaultError)
        })
    }
  }

  searchUser() {
    if (this.nameFilter.length === 0) {
      this.enableFeature = true
    } else {
      this.searchSpinner = true
      this.enableFeature = false
      this.getSearchResult()
    }

  }

  getSearchResult() {
    this.cardNetworkService.fetchSearchUserInfo(this.nameFilter.trim()).subscribe(data => {
      this.searchResultUserArray = data.result.UserProfile
      this.networkV2Service.fetchAllConnectionRequests().subscribe(
        requests => {
          // Filter all the connection requests sent
          if (requests && requests.result && requests.result.data) {
            requests.result.data.map(user => {
              if (user.id) {
                this.searchResultUserArray.map((autoCompleteUser: any) => {
                  if (autoCompleteUser.wid === user.id) {
                    autoCompleteUser['requestSent'] = true
                  }
                })
              }
            })
          }
          // Filter all the connection requests recieved
          if (this.connectionRequests) {
            this.connectionRequests.map((con: any) => {
              if (con.id) {
                this.searchResultUserArray.map((autoCompleteUser: any) => {
                  if (autoCompleteUser.wid === con.id) {
                    autoCompleteUser['requestRecieved'] = true
                  }
                })
              }
            })
          }
          // Filter all the estalished connections
          if (this.establishedConnections) {
            this.establishedConnections.map((con: any) => {
              if (con.id) {
                this.searchResultUserArray.map((autoCompleteUser: any) => {
                  if (autoCompleteUser.wid === con.id) {
                    autoCompleteUser['connectionEstablished'] = true
                  }
                })
              }
            })
          }
          this.searchSpinner = false
        },
        (_err: any) => {
          this.searchSpinner = false
        })
      // this.searchResultUserArray.splice(this.searchResultUserArray.findIndex((el: any) => {
      //   if (this.configSvc.userProfile && this.configSvc.userProfile.userId) {
      //     return el.wid === this.configSvc.userProfile.userId
      //   }
      //   return -1
      // // tslint:disable-next-line: align
      // }), 0)
      this.searchResultUserArray = this.searchResultUserArray.filter((el: any) => {
        if (this.configSvc.userProfile && this.configSvc.userProfile.userId) {
          if (el.wid === this.configSvc.userProfile.userId) {
            return false
          }
        }
        return el
      })
    })
  }

}
