import { Injectable } from '@angular/core'
import { BehaviorSubject, Subject, Observable } from 'rxjs'
import { finalize } from 'rxjs/operators'
import { HttpClient } from '@angular/common/http'
import * as fileSaver from 'file-saver'

const API_ENDPOINTS = {
  bulkUpload: `http://localhost:3003/protected/v8/admin/userRegistration/bulkUpload`,
}

@Injectable()
export class FileService {
    // tslint:disable-next-line: prefer-array-literal
    private fileList: string[] = new Array<string>()
    private fileList$: Subject<string[]> = new Subject<string[]>()
    private displayLoader$: Subject<boolean> = new BehaviorSubject<boolean>(false)

    constructor(private http: HttpClient) { }

    public isLoading(): Observable<boolean> {
      return this.displayLoader$
    }

    public upload(fileName: string, fileContent: string): void {
      this.displayLoader$.next(true)
      this.http.post(API_ENDPOINTS.bulkUpload, { name: fileName, content: fileContent })
      .pipe(finalize(() => this.displayLoader$.next(false)))
      .subscribe(_res => {
        this.fileList.push(fileName)
        this.fileList$.next(this.fileList)
      },         _error => {
        this.displayLoader$.next(false)
      })
    }

    public download(_fileName: string): void {
      // const httpOptions = {
      //   headers: new HttpHeaders({ responseType:  'blob',
      //   'Content-Type':  'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'}),
      // }
      this.http.get('/assets/common/user-bulk-upload-sample.xlsx', { responseType:  'blob' }).subscribe((res: any) => {
        // window.open(window.URL.createObjectURL(res))
        fileSaver.saveAs(res, 'user-bulk-upload-sample.xlsx')
      })
    }

    downloadFile(): Observable<any> {
      return this.http.get('/assets/common/sample.xls', { responseType: 'blob' })
    }
    public remove(fileName: any): void {
      this.http.delete('/files/${fileName}').subscribe(() => {
        this.fileList.splice(this.fileList.findIndex(name => name === fileName), 1)
        this.fileList$.next(this.fileList)
      })
    }

    public list(): Observable<string[]> {
      return this.fileList$
    }

    // private addFileToList(fileName: string): void {
    //   this.fileList.push(fileName)
    //   this.fileList$.next(this.fileList)
    // }

    validateFile(name: String) {
        const ext = name.substring(name.lastIndexOf('.') + 1)
        console.log('validateFile: ', ext)
        if (ext.toLowerCase() === 'xlsx') {
            return true
        // tslint:disable-next-line: no-else-after-return
        } else {
            return false
        }
    }
}
