import { CdkDragDrop, moveItemInArray } from '@angular/cdk/drag-drop'
import { Component, OnDestroy, OnInit } from '@angular/core'
import { MatSnackBar } from '@angular/material'
import { MatDialog } from '@angular/material/dialog'
import { ActivatedRoute, Data, Router } from '@angular/router'
import {
  BtnKbService,
  IPickerContentData,
  NsContent,
  NsContentStripMultiple,
  NsDiscussionForum,
  NsError,
  ROOT_WIDGET_CONFIG,
} from '@ws-widget/collection'
import { NsWidgetResolver } from '@ws-widget/resolver'
import { ConfigurationsService, EventService, NsPage, WsEvents } from '@ws-widget/utils'
import { Subscription } from 'rxjs'
import { DeleteContentDialogComponent } from './components/delete-content-dialog/delete-content-dialog.component'

@Component({
  selector: 'ws-app-kb-detail',
  templateUrl: './kb-detail.component.html',
  styleUrls: ['./kb-detail.component.scss'],
})
export class KbDetailComponent implements OnInit, OnDestroy {
  discussionForumWidget: NsWidgetResolver.IRenderConfigWithTypedData<
    NsDiscussionForum.IDiscussionForumInput
  > | null = null
  disableReorder = false
  internalReorder = false
  content: NsContent.IContent | null = null
  contentWidgets: any[] = []
  error = this.route.snapshot.data.content.error

  pageNavbar: Partial<NsPage.INavBackground> = this.configSvc.pageNavBar

  contentCategory: { [reasonAdded: string]: NsContent.IContent[] } = {}
  multipleStripWidget: any
  widgetData: NsContentStripMultiple.IContentStripMultiple | null = null
  copyWidgetData: NsContentStripMultiple.IContentStripMultiple | null = null
  copyWidgetDataEdit: NsContentStripMultiple.IContentStripMultiple | null = null

  errorWidget: NsWidgetResolver.IRenderConfigWithTypedData<NsError.IWidgetErrorResolver> = {
    widgetType: ROOT_WIDGET_CONFIG.errorResolver._type,
    widgetSubType: ROOT_WIDGET_CONFIG.errorResolver.errorResolver,
    widgetData: {
      errorType: 'internalServer',
    },
  }
  pickerContentData: IPickerContentData = {
    availableFilters: ['contentType'],
  }
  preSelectedContentIds: Set<string> = new Set()
  selectedContentIds: Set<string> = new Set()
  reasonForAdd = ''
  selectedClassifier: string | null = null
  contentClassifiers: string[] = []
  createEnablerButtonEnabled = false
  rearrangeEnablerButtonEnabled = false
  userId = ''
  userName = ''
  rootOrg = ''
  createEnabled = false
  editEnabled = false
  reOrderEnabled = false
  selectedDeleteIds: Set<string> = new Set()
  isDeleting = false
  isAddingContent = false
  routeSubscription: Subscription | null = null
  followCount = 0
  inProgress = false

  constructor(
    private configSvc: ConfigurationsService,
    private eventSvc: EventService,
    private route: ActivatedRoute,
    private btnKbSvc: BtnKbService,
    public router: Router,
    private snackBar: MatSnackBar,
    public deleteDialog: MatDialog,
  ) { }

  ngOnInit() {
    if (this.configSvc.userProfile
      && this.configSvc.userProfile.userId
      && this.configSvc.userProfile.userName
      && this.configSvc.rootOrg
    ) {
      this.userId = this.configSvc.userProfile.userId
      this.userName = this.configSvc.userProfile.userName
      this.rootOrg = this.configSvc.rootOrg
    }
    if (this.route) {
      this.routeSubscription = this.route.data.subscribe((data: Data) => {
        this.initData(data)
      })
    }

    this.raiseEvent(WsEvents.EnumTelemetrySubType.Loaded)
  }

  ngOnDestroy() {
    this.raiseEvent(WsEvents.EnumTelemetrySubType.Unloaded)
    if (this.routeSubscription) {
      this.routeSubscription.unsubscribe()
    }
  }

  follow(_id: string) {
    this.followCount += 1
  }

  unfollow(_id: string) {
    this.followCount -= 1
  }

  initData(data: Data) {
    this.content = data.content.data || null

    if (this.content) {
      this.btnKbSvc.getFollowers(this.content.identifier).subscribe((followData: any) => {
        if (followData.length) {
          this.followCount = followData[0].count
        }
      })
      if (this.configSvc.restrictedFeatures && !this.configSvc.restrictedFeatures.has('kb-reorder')) {
        this.internalReorder = true
      }
      if (
        this.configSvc.restrictedFeatures &&
        (!this.configSvc.restrictedFeatures.has('kb_editor') ||
          (!this.configSvc.restrictedFeatures.has('knowledgeBoard') &&
            this.content.creatorDetails.some(u => u.id === this.userId)))
      ) {
        this.createEnablerButtonEnabled = true
        this.rearrangeEnablerButtonEnabled = true
      }
      if (this.configSvc.restrictedFeatures && !(this.configSvc.restrictedFeatures.has('disable_reorder'))) {
        this.disableReorder = true
      }
      // this.content.children.sort((prev, next) => (prev.addedOn < next.addedOn) ? 1 : -1)
      const contentClassifiers = new Set<string>()
      this.content.children = this.content.children.filter(
        child => child.status === 'Live' || child.status === 'MarkedForDeletion',
      )
      this.content.children.forEach(child => {
        this.preSelectedContentIds.add(child.identifier)
        if (child.childrenClassifiers) {
          child.childrenClassifiers.forEach(u => contentClassifiers.add(u))
        }
        if (child.childrenClassifiers && child.childrenClassifiers.length === 0) {
          contentClassifiers.add('Default')
          child.childrenClassifiers.push('Default')
        }
        if (!child.childrenClassifiers) {
          contentClassifiers.add('Default')
          child.childrenClassifiers = ['Default']
        }
      })
      this.contentClassifiers = [...contentClassifiers.values()]
      const children = this.content.children

      const classifiedStrips = [...contentClassifiers.values()]
        .map(classifier => ({
          classifier,
          contents: JSON.parse(
            JSON.stringify(
              children.filter(
                child =>
                  child.childrenClassifiers && child.childrenClassifiers.includes(classifier),
              ),
            ),
          ),
        }
        ))

        .map(({ contents, classifier }) => {
          contents.map((v: any) => {
            v.childrenClassifiers = [classifier]
            return v
          })
          return { contents, classifier }
        })
        .map(({ contents, classifier }) => ({
          key: `kb-strip-${classifier}`,
          title: classifier,
          newTitle: classifier,
          selectedDeleteIds: [],
          preWidgets: contents.map((content: any) => ({
            widgetType: 'card',
            widgetSubType: 'cardContent',
            widgetData: {
              content: { ...content, mode: NsContent.ETagType.NEWLY_ADDED },
              cardSubType: 'standard',
              intranetMode: 'greyOut',
              deletedMode: 'hide',
            },
          })),
        }))
      this.widgetData = {
        loader: true,
        strips: [
          // defaultStrip,
          ...classifiedStrips,
        ],
      }
      this.copyWidgetData = JSON.parse(JSON.stringify(this.widgetData))
      this.copyWidgetDataEdit = JSON.parse(JSON.stringify(this.widgetData))
      this.multipleStripWidget = {
        widgetType: 'contentStrip',
        widgetSubType: 'contentStripMultiple',
        widgetData: this.widgetData,
      }
      this.multipleStripWidget.widgetData.strips.forEach((strip: any) => {
        strip.preWidgets.sort((prev: any, next: any) =>
          (prev.widgetData.content.addedOn < next.widgetData.content.addedOn) ? 1 : -1)
      })

      this.discussionForumWidget = {
        widgetData: {
          description: this.content.description,
          id: this.content.identifier,
          name: NsDiscussionForum.EDiscussionType.LEARNING,
          title: this.content.name,
          initialPostCount: 2,
        },
        widgetSubType: 'discussionForum',
        widgetType: 'discussionForum',
      }
    }
  }

  onContentSelectionChanged(content: Partial<NsContent.IContent>, checked: boolean) {
    if (content && content.identifier) {
      checked
        ? this.selectedContentIds.add(content.identifier)
        : this.selectedContentIds.delete(content.identifier)
    }
  }

  enableCreate(classifier?: string) {
    if (classifier) {
      this.selectedClassifier = classifier
    }
    if (!this.reOrderEnabled) {
      this.copyWidgetData = JSON.parse(JSON.stringify(this.widgetData))
    }
    if (!this.editEnabled) {
      this.copyWidgetDataEdit = JSON.parse(JSON.stringify(this.widgetData))
    }
    this.createEnabled = !this.createEnabled
    if (this.createEnabled) {
      const elem = document.getElementById('id-for-create-kb')
      if (elem) {
        elem.scrollIntoView()
      }
    }
    this.editEnabled = false
    this.reOrderEnabled = false
    this.revertReorder()
    this.revertEdit()
  }

  reOrder(successMsg: string, failedMsg: string) {
    if (!this.reOrderEnabled) {
      this.copyWidgetData = JSON.parse(JSON.stringify(this.widgetData))
    }
    if (!this.editEnabled) {
      this.copyWidgetDataEdit = JSON.parse(JSON.stringify(this.widgetData))
    }
    this.createEnabled = false
    this.editEnabled = false
    this.reOrderEnabled = !this.reOrderEnabled
    this.revertEdit()
    if (!this.reOrderEnabled) {
      this.saveReorder(successMsg, failedMsg)
    }
  }

  startDelete() {
    // //console.log(this.widgetData)
    if (!this.reOrderEnabled) {
      this.copyWidgetData = JSON.parse(JSON.stringify(this.widgetData))
    }
    if (!this.editEnabled) {
      this.copyWidgetDataEdit = JSON.parse(JSON.stringify(this.widgetData))
    }
    this.createEnabled = false
    this.editEnabled = !this.editEnabled
    this.reOrderEnabled = false
    this.revertReorder()
    const elem = document.getElementById('delete-kb-content')
    if (elem && this.editEnabled) {
      elem.scrollIntoView({
        behavior: 'smooth',
        block: 'end',
      })
    }
  }

  revertEdit() {
    // //console.log(this.widgetData)
    // //console.log(this.copyWidgetDataEdit)
    this.widgetData = this.copyWidgetDataEdit
    this.copyWidgetData = JSON.parse(JSON.stringify(this.widgetData))
    this.multipleStripWidget = {}
    this.multipleStripWidget = {
      widgetType: 'contentStrip',
      widgetSubType: 'contentStripMultiple',
      widgetData: this.widgetData,
    }
    this.editEnabled = false
    this.router.navigate([], { queryParams: { ts: Date.now() } })
    // //console.log(this.widgetData)
  }

  revertReorder() {
    this.widgetData = this.copyWidgetData
    this.copyWidgetDataEdit = JSON.parse(JSON.stringify(this.widgetData))
    this.multipleStripWidget = {}
    this.multipleStripWidget = {
      widgetType: 'contentStrip',
      widgetSubType: 'contentStripMultiple',
      widgetData: this.widgetData,
    }
    this.reOrderEnabled = false
  }

  saveReorderV3(successMsg: string, failedMsg: string) {

    if (this.content && this.widgetData) {
      this.inProgress = true
      const secObj: any = {}
      this.widgetData.strips.forEach(strip => {
        if (strip && strip.preWidgets) {
          strip.preWidgets.forEach(widget => {
            if (secObj.hasOwnProperty(strip.title)) {
              secObj[strip.title].push(widget.widgetData.content.identifier)
            } else {
              secObj[strip.title] = [widget.widgetData.content.identifier]

            }

          })

        }
      })
      const req = {
        sections: secObj,
        identifier: this.content.identifier,
        actor: this.userId,
        actorName: this.userName,
      }
      setTimeout(
        () => {
          const element = document.getElementById('spinner_load')
          if (element) {
            element.scrollIntoView({
              behavior: 'smooth',
              block: 'center',
            })
          }
        },
        200,
      )
      this.btnKbSvc.reorderKBV3(req).subscribe(
        () => {
          this.inProgress = false
          this.reOrderEnabled = false
          this.snackBar.open(successMsg, undefined, {
            duration: 10000,
          })
          // this.snackBar.open(successMsg)
          // tslint:disable-next-line
          // //console.log('API Success', req);
        },
        _ => {
          this.inProgress = false
          this.reOrderEnabled = false
          this.revertReorder()
          this.snackBar.open(failedMsg, undefined, {
            duration: 10000,
          })
          // this.snackBar.open(failedMsg)
          // tslint:disable-next-line
          // console.error('API FAILED', req);
        }
      )
    }
  }

  saveReorder(successMsg: string, failedMsg: string) {
    if (this.internalReorder) {
      this.saveReorderV3(successMsg, failedMsg)
      return
    }

    if (this.content && this.widgetData) {
      this.inProgress = true
      const newSections = this.widgetData.strips.map(strip => strip.title)
      const req = {
        sections: newSections,
        identifier: this.content.identifier,
        actor: this.userId,
        actorName: this.userName,
      }
      setTimeout(
        () => {
          const element = document.getElementById('spinner_load')
          if (element) {
            element.scrollIntoView({
              behavior: 'smooth',
              block: 'center',
            })
          }
        },
        200,
      )
      this.btnKbSvc.modifyKBV2(req, 'reorder').subscribe(
        () => {
          this.inProgress = false
          this.reOrderEnabled = false
          this.snackBar.open(successMsg, undefined, {
            duration: 10000,
          })
          // this.snackBar.open(successMsg)
          // tslint:disable-next-line
          // //console.log('API Success', req);
        },
        _ => {
          this.inProgress = false
          this.reOrderEnabled = false
          this.revertReorder()
          this.snackBar.open(failedMsg, undefined, {
            duration: 10000,
          })
          // this.snackBar.open(failedMsg)
          // tslint:disable-next-line
          // console.error('API FAILED', req);
        }
      )
    }
  }

  onChangeDelete(deleteId: string, editStrip: any = null) {
    for (const strip of this.multipleStripWidget.widgetData.strips) {
      if (strip === editStrip) {
        strip.selectAll = false
        const selectedIdSet = new Set(strip.selectedDeleteIds)

        selectedIdSet.add(deleteId)

        strip.selectedDeleteIds = [...selectedIdSet]
      }
    }
  }

  openDeleteDialog(ids: string, successMsg: string, failedMsg: string, updateMsg: string, strip: any) {
    if (ids) {
      const dialogRef = this.deleteDialog.open(DeleteContentDialogComponent, {
        width: '450px',
        data: [ids].length,
      })
      dialogRef.afterClosed().subscribe(result => {
        if (result === 'delete') {
          this.deleteId([ids], successMsg, failedMsg, updateMsg, strip)
        }
      })
    } else {
      const msg = 'You have not selected any content to delete'
      this.snackBar.open(msg)
    }
  }

  openDeleteStrip(successMsg: string, failedMsg: string, updateMsg: string, editingStrip: any) {
    if (this.copyWidgetData) {
      for (const strip of this.multipleStripWidget.widgetData.strips) {
        if (editingStrip === strip) {
          const selectedIdSet = new Set(strip.selectedDeleteIds)
          for (const widget of editingStrip.preWidgets) {
            selectedIdSet.add(widget.widgetData.content.identifier)

          }
          strip.selectedDeleteIds = [...selectedIdSet]
          const idSet: Set<string> = new Set(strip.selectedDeleteIds)
          if ([...idSet].length) {
            const dialogRef = this.deleteDialog.open(DeleteContentDialogComponent, {
              width: '450px',
              data: [...idSet].length,
            })
            dialogRef.afterClosed().subscribe(result => {
              if (result === 'delete') {
                this.deleteId([...idSet], successMsg, failedMsg, updateMsg, strip)
              }
            })
          } else {
            const msg = 'You have not selected any content to delete'
            this.snackBar.open(msg)
          }
        }
      }

    }

  }

  deleteSection(editingStrip: any) {
    if (this.copyWidgetData) {
      for (const strip of this.multipleStripWidget.widgetData.strips) {
        if (editingStrip === strip) {
          const selectedIdSet = new Set(strip.selectedDeleteIds)
          for (const widget of editingStrip.preWidgets) {

            selectedIdSet.add(widget.widgetData.content.identifier)

          }
          strip.selectedDeleteIds = [...selectedIdSet]
        }
      }

    }
  }

  closeEdit() {
    this.editEnabled = false
    this.createEnabled = false
    this.reOrderEnabled = false
    this.router.navigate([], { queryParams: { ts: Date.now() } })
  }

  revertDelete(editingStrip: any) {
    if (this.copyWidgetData) {
      for (const strip of this.multipleStripWidget.widgetData.strips) {
        if (editingStrip === strip) {
          strip.delete = false
          strip.selectedDeleteIds = []
        }
      }
    }
    this.selectedDeleteIds.clear()
  }

  deleteIds(ids: string[], successMsg: string, failedMsg: string, updateMsg: string, editingStrip: any) {
    if (this.content) {
      this.isDeleting = true
      const removalIDs = new Set(ids)
      const newChildren: any[] = []
      if (this.widgetData) {
        if (this.widgetData.strips && this.widgetData.strips.length > 0) {
          this.widgetData.strips.forEach(strip => {
            if (strip.preWidgets && strip.preWidgets.length > 0) {
              strip.preWidgets.forEach(preWidget => {
                if (
                  preWidget.widgetData &&
                  preWidget.widgetData.content &&
                  !removalIDs.has(preWidget.widgetData.content.identifier)
                ) {
                  preWidget.widgetData.content.childrenClassifiers = [strip.title]

                  newChildren.push(preWidget.widgetData.content)
                }
              })
            }
          })
        }
      }
      const req = {
        nodesModified: {},
        hierarchy: {
          [this.content.identifier]: {
            children: [...newChildren],
            root: true,
          },
        },
      }

      this.btnKbSvc.addContentsToKb(req).subscribe(
        () => {
          this.router.navigate([], { queryParams: { ts: Date.now() } })
          // this.snackBar.open(successMsg)
        },
        _ => {
          this.router.navigate([], { queryParams: { ts: Date.now() } })
          // this.snackBar.open(failedMsg)
        },
      )

      this.deleteId(ids, successMsg, failedMsg, updateMsg, editingStrip)
    }
  }

  deleteId(ids: string[] = [], successMsg: string, failedMsg: string, updateMsg: string, editingStrip: any) {
    if (this.content && ids.length) {
      this.isDeleting = true
      this.inProgress = true
      setTimeout(
        () => {
          const element = document.getElementById('spinner_load')
          if (element) {
            element.scrollIntoView({
              block: 'center',
              behavior: 'smooth',
              inline: 'center',
            })
          }
        },
        200,
      )
      const removalIDs = new Set(ids)
      for (const strip of this.multipleStripWidget.widgetData.strips) {
        if (editingStrip === strip) {
          strip.isDeleting = true
        }

      }
      // const req = {
      //   identifier: this.content.identifier,
      //   children: Array.from(removalIDs),
      // }

      const req = {
        identifier: this.content.identifier,
        children: Array.from(removalIDs),
        actor: this.userId,
        actorName: this.userName,
      }

      this.btnKbSvc.modifyKBV2(req, 'delete').subscribe(
        () => {
          if (this.copyWidgetData) {
            for (const strip of this.multipleStripWidget.widgetData.strips) {
              if (editingStrip === strip) {
                strip.selectedDeleteIds = []
                strip.delete = false
                strip.isDeleting = false
                if (strip.preWidgets) {
                  if (!strip.selectAll) {
                    const widgetSet = new Set(strip.preWidgets)
                    for (const widget of strip.preWidgets) {
                      if (ids.includes(widget.identifier)) {
                        widgetSet.delete(widget)
                      }
                    }
                    strip.preWidgets = [...widgetSet]
                  } else {
                    strip.preWidgets = []
                  }
                }
              }
            }
            for (const strip of this.copyWidgetData.strips) {
              if (editingStrip === strip) {
                if (strip.preWidgets) {
                  if (!strip.selectAll) {
                    const widgetSet = new Set(strip.preWidgets)
                    for (const widget of strip.preWidgets) {
                      if (widget.widgetData.content && ids.includes(widget.widgetData.content.identifier)) {
                        widgetSet.delete(widget)
                      }
                    }
                    strip.preWidgets = [...widgetSet]
                  } else {
                    strip.preWidgets = []
                  }
                }
              }
            }
          }
          this.inProgress = false
          this.isDeleting = false
          this.selectedDeleteIds.clear()
          this.snackBar.open(ids.length ? successMsg : updateMsg, undefined, {
            duration: 10000,
          })
          // this.snackBar.open(successMsg)
        },
        _ => {
          for (const strip of this.multipleStripWidget.widgetData.strips) {
            if (editingStrip === strip) {
              strip.delete = false
              strip.isDeleting = false
              strip.selectedDeleteIds = []
            }
          }
          this.inProgress = false
          this.selectedDeleteIds.clear()
          this.snackBar.open(failedMsg, undefined, {
            duration: 10000,
          })
          // this.snackBar.open(failedMsg)
        },
      )
    } else {
      for (const strip of this.multipleStripWidget.widgetData.strips) {
        if (editingStrip === strip) {
          strip.delete = false
          strip.isDeleting = false
          strip.selectedDeleteIds = []
        }
      }
      this.selectedDeleteIds.clear()
      this.snackBar.open(ids.length ? successMsg : updateMsg, undefined, {
        duration: 10000,
      })
    }

    //   this.btnKbSvc.editKBBoards(req, 'delete').subscribe(
    //     () => {
    //       this.router.navigate([], { queryParams: { ts: Date.now() } })
    //       this.isDeleting = false
    //       this.editEnabled = false
    //       this.snackBar.open(ids.length ? successMsg : updateMsg, undefined, {
    //         duration: 10000,
    //       })
    //       // this.snackBar.open(successMsg)
    //     },
    //     _ => {
    //       this.isDeleting = false
    //       this.snackBar.open(failedMsg, undefined, {
    //         duration: 10000,
    //       })
    //       // this.snackBar.open(failedMsg)
    //     },
    //   )
    // } else {
    //   this.router.navigate([], { queryParams: { ts: Date.now() } })
    //   this.isDeleting = false
    //   this.editEnabled = false
    //   this.snackBar.open(ids.length ? successMsg : updateMsg, undefined, {
    //     duration: 10000,
    //   })
    // }
  }

  addContent(successMsg: string, failedMsg: string) {
    if (!this.selectedClassifier) {
      this.snackBar.open('Please select a section to add the contents')
      return
    }
    if (this.content) {
      this.inProgress = true
      this.isAddingContent = true

      // const newChildren = [...this.selectedContentIds.values()].map(identifier => ({
      //   identifier,
      //   reason: this.reasonForAdd,
      //   childrenClassifiers: this.selectedClassifier ? [this.selectedClassifier] : [],
      // }))
      // const req = {
      //   identifier: this.content.identifier,
      //   children: newChildren,
      // }
      // this.btnKbSvc.editKBBoards(req, 'add').subscribe(
      //   () => {
      //     this.selectedClassifier = null
      //     this.reasonForAdd = ''
      //     this.selectedContentIds.clear()
      //     this.createEnabled = false
      //     this.router.navigate([], { queryParams: { ts: Date.now() } })
      //     this.isAddingContent = false
      //     // this.snackBar.open(successMsg)
      //     this.snackBar.open(successMsg, undefined, {
      //       duration: 10000,
      //     })
      //   },
      //   () => {
      //     this.isAddingContent = false
      //     // this.snackBar.open(failedMsg)
      //     this.snackBar.open(failedMsg, undefined, {
      //       duration: 10000,
      //     })
      //     // tslint:disable-next-line
      //     console.error('API FAILED', req)
      //   },
      //   () => { },
      // )

      const newChildren = [...this.selectedContentIds.values()].map(identifier => ({
        identifier,
        reason: this.reasonForAdd,
        childrenClassifiers: this.selectedClassifier ? [this.selectedClassifier] : [],
      }))
      setTimeout(
        () => {
          const element = document.getElementById('spinner_load')
          if (element) {
            element.scrollIntoView({
              block: 'center',
              behavior: 'smooth',
              inline: 'center',
            })
          }
        },
        200,
      )
      const req = {
        identifier: this.content.identifier,
        children: newChildren,
        actor: this.userId,
        actorName: this.userName,
      }
      this.btnKbSvc.modifyKBV2(req, 'add').subscribe(
        () => {
          this.selectedClassifier = null
          this.reasonForAdd = ''
          this.selectedContentIds.clear()
          this.createEnabled = false
          this.router.navigate([], { queryParams: { ts: Date.now() } })
          this.isAddingContent = false
          this.inProgress = false
          // this.snackBar.open(successMsg)
          this.snackBar.open(successMsg, undefined, {
            duration: 10000,
          })
        },
        () => {
          this.isAddingContent = false
          this.inProgress = false
          // this.snackBar.open(failedMsg)
          this.snackBar.open(failedMsg, undefined, {
            duration: 10000,
          })
          // tslint:disable-next-line
          console.error('API FAILED', req)
        },
        () => { },
      )
    }
  }

  drop(event: CdkDragDrop<string[]>) {
    moveItemInArray(
      this.widgetData ? this.widgetData.strips : [],
      event.previousIndex,
      event.currentIndex,
    )
  }

  drop2(event: CdkDragDrop<string[]>, strips: any[]) {
    moveItemInArray(strips ? strips : [], event.previousIndex, event.currentIndex)
  }

  getRatingIcon(ratingIndex: number): 'star' | 'star_border' | 'star_half' {
    if (this.content && this.content.averageRating && this.content.averageRating[this.rootOrg]) {
      const avgRating = this.content.averageRating[this.rootOrg]
      const ratingFloor = Math.floor(avgRating)
      if (ratingIndex <= ratingFloor) {
        return 'star'
      }
      if (ratingFloor === ratingIndex - 1 && avgRating % 1 > 0) {
        return 'star_half'
      }
    }
    return 'star_border'
  }
  raiseEvent(state: WsEvents.EnumTelemetrySubType) {
    const path = window.location.pathname.replace('/', '')
    const event = {
      eventType: WsEvents.WsEventType.Telemetry,
      eventLogLevel: WsEvents.WsEventLogLevel.Info,
      from: 'knowledge-board',
      to: '',
      data: {
        state,
        type: WsEvents.WsTimeSpentType.Page,
        mode: WsEvents.WsTimeSpentMode.View,
        pageId: path,
      },
    }
    this.eventSvc.dispatchEvent(event)
  }

  renameSection(renaming: boolean, edit: boolean, title: string, newTitle: string) {
    if (this.content) {
      this.inProgress = true
      for (const strip of this.multipleStripWidget.widgetData.strips) {
        if (strip.title === title) {
          strip.edit = edit
          strip.isRenaming = renaming
        }
      }
      setTimeout(
        () => {
          const element = document.getElementById('spinner_load')
          if (element) {
            element.scrollIntoView({
              block: 'center',
              behavior: 'smooth',
              inline: 'center',
            })
          }
        },
        200,
      )
      for (const strip of this.multipleStripWidget.widgetData.strips) {
        if (strip.title === title) {

          strip.isRenaming = false
          if (newTitle) {
            strip.title = newTitle
          } else {
            strip.title = title
          }
        }
      }
      const req = {
        identifier: this.content.identifier,
        oldSection: title,
        newSection: newTitle,
        actor: this.userId,
        actorName: this.userName,
      }
      if (title !== newTitle) {
        this.btnKbSvc.modifyKBV2(req, 'section').subscribe(
          () => {
            if (this.copyWidgetData) {
              for (const strip of this.multipleStripWidget.widgetData.strips) {
                if (strip.title === title) {

                  strip.isRenaming = false
                  if (newTitle) {
                    strip.title = newTitle
                  } else {
                    strip.title = title
                  }
                }
              }
              for (const strip of this.copyWidgetData.strips) {
                if (strip.title === title) {
                  if (newTitle) {
                    strip.title = newTitle
                  } else {
                    strip.title = title
                  }
                }
              }
              this.inProgress = false
              this.snackBar.open(`The section has been renamed to ${newTitle}`, undefined, {
                duration: 10000,
              })
            }

            // this.snackBar.open(successMsg)
            // tslint:disable-next-line
            // //console.log('API Success', req);
          },
          () => {
            this.inProgress = false
            this.revertEdit()
            this.snackBar.open('Failed to rename the section', undefined, {
              duration: 10000,
            })
          })
      } else {
        this.inProgress = false
      }
    }
  }
}
