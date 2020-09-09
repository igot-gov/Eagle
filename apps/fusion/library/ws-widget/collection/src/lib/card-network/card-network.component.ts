import { Component, Input, OnInit } from '@angular/core'
import { Router } from '@angular/router'
import { NsWidgetResolver, WidgetBaseComponent } from '@ws-widget/resolver'


@Component({
  selector: 'ws-widget-card-welcome',
  templateUrl: './card-network.component.html',
  styleUrls: ['./card-network.component.scss'],
})
export class CardNetworkComponent extends WidgetBaseComponent

  implements OnInit, NsWidgetResolver.IWidgetData<any> {
  enableFeature = true
  enablePeopleSearch = true
  @Input() widgetData: any
  givenName: string | undefined
  userEmail: string | undefined
  keyTag: string[] = []

  constructor(private router: Router) {

    super()

  }

  cardArray = [{
    first_name: 'Amit',
    last_name: 'Yadav',
    email: 'amitkumar.yadav@tarento.com',
    desc: 'Last active 8 days ago',
    user_id: null,
    department: 'igot',
    phone_No: '0',
    designation: '',
    userLocation: '',
    city: '',
  }, {
    first_name: 'Amit',
    last_name: 'Yadav',
    email: 'amitkumar.yadav@tarento.com',
    desc: 'Last active 8 days ago',
    user_id: null,
    department: 'igot',
    phone_No: '0',
    designation: '',
    userLocation: '',
    city: '',
  }, {
    first_name: 'Amit',
    last_name: 'Yadav',
    email: 'amitkumar.yadav@tarento.com',
    desc: 'Last active 8 days ago',
    user_id: null,
    department: 'igot',
    phone_No: '0',
    designation: '',
    userLocation: '',
    city: '',
  }, {
    first_name: 'Amit',
    last_name: 'Yadav',
    email: 'amitkumar.yadav@tarento.com',
    desc: 'Last active 8 days ago',
    user_id: null,
    department: 'igot',
    phone_No: '0',
    designation: '',
    userLocation: '',
    city: '',
  }, {
    first_name: 'Amit',
    last_name: 'Yadav',
    email: 'amitkumar.yadav@tarento.com',
    desc: 'Last active 8 days ago',
    user_id: null,
    department: 'igot',
    phone_No: '0',
    designation: '',
    userLocation: '',
    city: '',
  }, {
    first_name: 'Amit',
    last_name: 'Yadav',
    email: 'amitkumar.yadav@tarento.com',
    desc: 'Last active 8 days ago',
    user_id: null,
    department: 'igot',
    phone_No: '0',
    designation: '',
    userLocation: '',
    city: '',
  }, {
    first_name: 'Amit',
    last_name: 'Yadav',
    email: 'amitkumar.yadav@tarento.com',
    desc: 'Last active 8 days ago',
    user_id: null,
    department: 'igot',
    phone_No: '0',
    designation: '',
    userLocation: '',
    city: '',
  }, {
    first_name: 'Amit',
    last_name: 'Yadav',
    email: 'amitkumar.yadav@tarento.com',
    desc: 'Last active 8 days ago',
    user_id: null,
    department: 'igot',
    phone_No: '0',
    designation: '',
    userLocation: '',
    city: '',
  }]
  ngOnInit() {

  }

  getUserFullName(user: any) {
    if (user && user.first_name && user.last_name) {
      return `${user.first_name.trim()} ${user.last_name.trim()}`
    }
    return ''
  }
  goToUserProfile(user: any) {
    this.router.navigate(['/app/person-profile'], { queryParams: { emailId: user.email } })
  }

}
