import { Component, OnInit, Input, Output, EventEmitter, ViewChild } from '@angular/core'
import { FormControl, FormGroup, Validators } from '@angular/forms'
import { EventsService } from '../../services/events.service'
import { MatSnackBar } from '@angular/material'
import { MatPaginator } from '@angular/material'
import { MatSort } from '@angular/material/sort'
import { ITableData } from '../../interfaces/interfaces'
import { MatDialog } from '@angular/material/dialog';
import { ParticipantsComponent } from "../../components/participants/participants.component";

@Component({
  selector: 'ws-app-create-event',
  templateUrl: './create-event.component.html',
  styleUrls: ['./create-event.component.scss'],
})
export class CreateEventComponent implements OnInit {
  
  artifactURL: any;
  participantsArr: any = [];
  displayedColumns: string[] = ['fullname', 'email', 'type'];
  @Input() tableData!: ITableData | undefined
  @Input() data?: []
  @Input() isUpload?: boolean
  @Input() isCreate?: boolean

  @Output() clicked?: EventEmitter<any>
  @Output() actionsClick?: EventEmitter<any>
  @Output() eOnRowClick = new EventEmitter<any>()
  @Output() eOnCreateClick = new EventEmitter<any>()

  createEventForm: FormGroup
  namePatern = `^[a-zA-Z\\s\\']{1,32}$`
  department: any = {}
  departmentName = ''
  toastSuccess: any;
  pictureObj: any;
  
  eventTypes = [
    { title: 'Webinar', desc: 'General discussion involving' },
    { title: 'Ask me anything', desc: 'Session targeted at answering questions from attendees' },
    { title: 'Workshop', desc: 'Live learning session' },
    { title: 'Interview', desc: 'Interview session involving one or more guests' }
  ];

  timeArr = [
    { value: '00:00' }, { value: '00:30' }, { value: '01:00' }, { value: '01:30' },
    { value: '02:00' }, { value: '02:30' }, { value: '03:00' }, { value: '03:30' },
    { value: '04:00' }, { value: '04:30' }, { value: '05:00' }, { value: '05:30' },
    { value: '06:00' }, { value: '06:30' }, { value: '07:00' }, { value: '07:30' },
    { value: '08:00' }, { value: '08:30' }, { value: '09:00' }, { value: '09:30' },
    { value: '10:00' }, { value: '10:30' }, { value: '11:00' }, { value: '11:30' },
  ];

  @ViewChild(MatPaginator, { static: true }) paginator!: MatPaginator
  @ViewChild(MatSort, { static: true }) sort?: MatSort

  dataSource!: any
  widgetData: any
  length!: number
  pageSize = 5
  pageSizeOptions = [5, 10, 20];
  dialogRef: any;
  activeUsers: any;
  imageSrc: any;
  imageSrcURL: any;
  tabsData!: any[];
  currentTab = 'eventInfo'

  constructor(private snackBar: MatSnackBar,
              private eventsSvc: EventsService,
              private matDialog: MatDialog
              ) {
    
    this.getParticipantsData();
    
    this.createEventForm = new FormGroup({
      eventPicture:new FormControl('', [Validators.required]),
      eventTitle: new FormControl('', [Validators.required]),
      summary: new FormControl('', [Validators.required]),
      description: new FormControl('', [Validators.required]),
      agenda: new FormControl('', [Validators.required]),
      eventType: new FormControl('', [Validators.required]),
      eventDate: new FormControl('', [Validators.required]),
      eventTime: new FormControl('', [Validators.required]),
      eventDuration: new FormControl('', [Validators.required]),
      conferenceLink: new FormControl('', [Validators.required]),
      presenters: new FormControl(''),
    })
  }

  ngOnInit() {

    this.tabsData = [
      {
        name: 'Event details',
        key: 'eventInfo',
        render: true,
        enabled: true,
      },
      {
        name: 'Date and time',
        key: 'datetime',
        render: true,
        enabled: true,
      },
      {
        name: 'Video conferencing',
        key: 'videoinfo',
        render: true,
        enabled: true,
      },
      {
        name: 'Presenters',
        key: 'presenter',
        render: true,
        enabled: true,
      }]
  }

  onSideNavTabClick(id: string) {
    this.currentTab = id
    const el = document.getElementById(id)
    if (el != null) {
      el.scrollIntoView({ behavior: 'smooth', block: 'start', inline: 'start' })
    }
  }

  ngAfterViewInit() { }

  getParticipantsData() {
    this.eventsSvc.getParticipants().subscribe(res => {
      this.activeUsers = res.active_users;
    }, (err: { error: string }) => {
        console.log(err);
    })
  }

  openDialog() {
    this.dialogRef = this.matDialog.open(ParticipantsComponent, {
      width: '850px',
      height: '600px',
      data: this.activeUsers
    });
    this.dialogRef.afterClosed().subscribe((response: any) => {
        if(response) {

          Object.keys(response.data).forEach((index: any) => {
          let obj = response.data[index];
            let setSelectedPresentersObj = {
              fullname: obj.fullname,
              email: obj.email,
              type: 'Karmayogi User'
            };
            this.participantsArr.push(setSelectedPresentersObj);
          });
          this.createEventForm.controls['presenters'].setValue(this.participantsArr);
        }
    });
  }

  close() {
    this.dialogRef.afterClosed().subscribe((res: any) => {
        console.log(res); 
    });
  }

  selectCover() {
    this.pictureObj = document.getElementById('coverPicture');
    this.pictureObj.click();
  }

  onFileSelect(event:any) {
    if (event.target.files.length > 0) {
      var reader = new FileReader();
      const file = event.target.files[0];
      reader.onload = () => this.imageSrcURL = reader.result;
      reader.readAsDataURL(file);
      this.imageSrc = file;
      this.fileSubmit();
    }
  }

    fileSubmit() {
      const formData = new FormData();
      formData.append('file', this.imageSrc);
      this.eventsSvc.uploadCoverImage(formData).subscribe(res => {
        this.artifactURL = res.artifactURL;
        this.createEventForm.controls['eventPicture'].setValue(this.artifactURL);
      }, (err: { error: string }) => {
        console.log(err);
      });
    }

  changeEventType(event:any) {
   this.createEventForm.controls['eventType'].setValue(event.target.value); 
  }

  onSubmit() {

    // let formBody = {
    //   content:{
    //     ContentType:"Event",
    //     //createdBy:"b5e7a871-37d1-469b-a9d6-25353961637e",
    //     Name: this.createEventForm.controls['eventTitle'].value,
    //     Description: this.createEventForm.controls['summary'].value,
    //     //Description: this.createEventForm.controls['description'].value,
    //     LearningObjectives: this.createEventForm.controls['agenda'].value,
    //     ResourceType: this.createEventForm.controls['eventType'].value,
    //     ExpiryDate: this.createEventForm.controls['eventDate'].value,
    //     //eventTime: this.createEventForm.controls['eventTime'].value,
    //     Duration: this.createEventForm.controls['eventDuration'].value,
    //     ArtifactUrl: this.createEventForm.controls['conferenceLink'].value,
    //     CreatorDetails:this.createEventForm.controls['presenters'].value,
    //     Thumbnail: this.createEventForm.controls['eventPicture'].value
    //   }
    // };

    let form = {
       "content":{
          "contentType":"Event",
          "mimeType":"application/vnd.ekstep.content-collection",
          "locale":"en",
          "name":"Test Event",
          "description":"",
          "category":"Event",
          "createdBy":"b5e7a871-37d1-469b-a9d6-25353961637e",
          "authoringDisabled":false,
          "isContentEditingDisabled":false,
          "isMetaEditingDisabled":false,
          "isExternal":false
       }
    };

    let formJson = this.encodeToBase64(form);
    this.eventsSvc.createEvent(formJson).subscribe(res => {
      let identifier = res.identifier;
      this.updateEventData(identifier);
    }, (err: { error: string }) => {
      this.openSnackbar(err.error.split(':')[1])
    })
  }

  updateEventData(identifier : any) {

    // lex_auth_013174922651705344298:{
    //   isNew:false,
    //   root:true,
    //   metadata:{
    //     name:"Sample Course From Karthik"
    //   }
    // }
    let identifier_key = identifier;
    let formBody = {
      nodesModified:{
        [identifier_key]: {
          isNew:false,
          root:true,
          metadata:{
            Name: this.createEventForm.controls['eventTitle'].value,
            Description: this.createEventForm.controls['summary'].value,
            LearningObjectives: this.createEventForm.controls['agenda'].value,
            ResourceType: this.createEventForm.controls['eventType'].value,
            ExpiryDate: this.createEventForm.controls['eventDate'].value,
            //eventTime: this.createEventForm.controls['eventTime'].value,
            Duration: this.createEventForm.controls['eventDuration'].value,
            ArtifactUrl: this.createEventForm.controls['conferenceLink'].value,
            CreatorDetails:this.createEventForm.controls['presenters'].value,
            Thumbnail: this.createEventForm.controls['eventPicture'].value
          }
        }
      },hierarchy:{}
    };

    let formJson = this.encodeToBase64(formBody);
    this.eventsSvc.updateEvent(formJson).subscribe(res => {
      let identifier = res.identifier;
      this.updateEventData(identifier);
    }, (err: { error: string }) => {
      this.openSnackbar(err.error.split(':')[1])
    })
  }

  encodeToBase64 (body:any) {
    const sString = JSON.stringify(body);
    const aUTF16CodeUnits = new Uint16Array(sString.length);
    Array.prototype.forEach.call(aUTF16CodeUnits, (_el, idx, arr) => arr[idx] = sString.charCodeAt(idx));
    return { data: btoa(new Uint8Array(aUTF16CodeUnits.buffer).reduce((data, byte) => data + String.fromCharCode(byte), '')) };
  }

  private openSnackbar(primaryMsg: string, duration: number = 5000) {
    this.snackBar.open(primaryMsg, 'X', {
      duration,
    })
  }

}
