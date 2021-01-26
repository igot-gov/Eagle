import { Component, Inject, OnInit } from '@angular/core'
import { FormControl } from '@angular/forms'
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog'
import { SelectionModel } from '@angular/cdk/collections'
import { MatTableDataSource } from '@angular/material'
import { debounceTime, distinctUntilChanged } from 'rxjs/operators'
import { EventsService } from '../../services/events.service'
import { HttpClient } from '@angular/common/http'

export interface IParticipantElement {
    firstname: string,
    lastname: string,
    email: number
}

@Component({
    selector: 'ws-app-participants',
    templateUrl: './participants.component.html',
    styleUrls: ['./participants.component.scss'],
})
export class ParticipantsComponent implements OnInit {

    participants: any = []
    displayedColumns: string[] = ['select', 'fullname', 'email']
    dataSource: any
    selection = new SelectionModel<IParticipantElement>(true, [])
    searchUserCtrl = new FormControl()
    filteredUsers: any
    isLoading = false
    errorMsg: any

    constructor(
        public eventSrc: EventsService,
        public http: HttpClient,
        public dialogRef: MatDialogRef<ParticipantsComponent>,
        @Inject(MAT_DIALOG_DATA) public data: any
    ) { }

    isAllSelected() {
        const numSelected = this.selection.selected.length
        const numRows = this.dataSource.data.length
        return numSelected === numRows
    }

    masterToggle() {
        this.isAllSelected() ?
        this.selection.clear() :
        this.dataSource.data.forEach((row: any) => this.selection.select(row))
    }

    ngOnInit() {
        const requestObj = {
            departments: [
            'igot',
            'istm',
            'iGOT',
            'NPA',
            'NACIN',
            'LSNAA',
            ],
        }
        this.searchUserCtrl.valueChanges.pipe(
            debounceTime(200),
            distinctUntilChanged(),
        ).subscribe(() => {
            this.eventSrc.searchUser(this.searchUserCtrl.value, requestObj).subscribe((data: any) => {
                this.dataSource = new MatTableDataSource<IParticipantElement>(data)
                this.participants = []
                Object.keys(data).forEach((key: any) => {
                    const obj = data[key]
                    if (obj.email !== undefined) {
                        const participantObj = {
                            firstname: obj.first_name,
                            lastname: obj.last_name,
                            email: obj.email,
                            id: obj.wid,
                        }
                        this.participants.push(participantObj)
                    }
                })
            })
        })
    }

    confirm() {
        this.dialogRef.close({ data: this.selection.selected })
    }

}
