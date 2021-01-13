import { Component, Inject } from '@angular/core'
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { SelectionModel } from '@angular/cdk/collections';
import { MatTableDataSource } from '@angular/material';

export interface ParticipantElement {
  fullname: string;
  email: number;
}

@Component({
  selector: 'ws-app-participants',
  templateUrl: './participants.component.html',
  styleUrls: ['./participants.component.scss'],
})
export class ParticipantsComponent {
  
  participants: any = [];
  displayedColumns: string[] = ['select', 'fullname', 'email'];

  dataSource: any;
  selection = new SelectionModel<ParticipantElement>(true, []);

  constructor(
    public dialogRef: MatDialogRef<ParticipantsComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) { 
    this.dataSource = new MatTableDataSource<ParticipantElement>(this.data);
    console.log(this.data);
    Object.keys(this.data).forEach((key: any) => {
      let obj = this.data[key];
      let participantObj = {
        fullname: obj.firstName + ' ' + obj.lastName,
        email: obj.emailId
      };
      this.participants.push(participantObj);
    });

  }



  /** Whether the number of selected elements matches the total number of rows. */
  isAllSelected() {
    const numSelected = this.selection.selected.length;
    const numRows = this.dataSource.data.length;
    return numSelected === numRows;
  }

  /** Selects all rows if they are not all selected; otherwise clear selection. */
  masterToggle() {
    this.isAllSelected() ?
        this.selection.clear() :
        this.dataSource.data.forEach((row: any) => this.selection.select(row));
  }

  ngOnInit() {
  
  }

  confirm() {
    this.dialogRef.close({ data: this.selection.selected })
  }

}
