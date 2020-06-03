import { Component, OnInit, Input, HostListener, SimpleChanges, OnChanges } from '@angular/core'
import { IDndData } from '../../interface/iap-assessment.interface'

@Component({
  selector: 'ws-auth-view-dnd-question',
  templateUrl: './view-dnd-question.component.html',
  styleUrls: ['./view-dnd-question.component.scss'],
})
export class ViewDndQuestionComponent implements OnInit, OnChanges {

  @Input() data!: IDndData
  // @ViewChild('form') metaForm:GenericMetaFormComponent;
  categories: string[] = []
  items: string[] = []
  itemsTemp: string[] = []
  openedCategoryName = ''

  itemsAreUnique = true

  rdnd = false
  cateOpened = false
  cMap: boolean[] = []
  smallScreen = false

  questionData: any = null
  dataLoaded = false
  soln!: any
  showingSol = false
  resultView = false

  constructor() { }

  @HostListener('window:resize', ['$event'])
  onResize() {
    this.checkIfSmallScreen()
    if (this.smallScreen && this.openedCategoryName !== '') {
      this.openCategory(this.openedCategoryName)
    }
  }

  ngOnChanges(changes: SimpleChanges) {

    if (changes.data.currentValue) {
      this.dataLoaded = false
      this.data = changes.data.currentValue
      this.setData()
    }
    this.checkIfSmallScreen()
  }

  ngOnInit() {
  }

  setData() {
    // let data = this.data;
    // if(this.data.hasOwnProperty('userSolution')){
    //   this.questionData = this.data;
    //   this.soln = this.data.userSolution ? this.data.userSolution : {};
    //   this.resultView = true;
    // }else{
    //   this.questionData = this.data;
    //   this.resultView = false;
    // }

    // if (this.questionData.questionType === 'dnd') {
    //   this.rdnd = false;
    //   this.categories = data.categoryList? data.categoryList : [];
    //   // this.items = this.data.itemList;
    //   if (data.dndSolution!=null && !this.resultView
    //       || data.userSolution!===null && this.resultView
    //       && this.questionData ) {
    //     this.soln = this.resultView ? this.data.userSolution : this.questionData.dndSolution;
    //     this.getUndroppedItemsWhileSettingData()
    //     console.log("this.questionData processed",this.soln,this.openedCategoryName,this.items)
    //   } else {
    //     console.log("irtems",this.items)
    //     this.items = this.questionData.itemList? this.questionData.itemList : [];
    //     console.log("this.questionData processed",this.soln,this.openedCategoryName,this.items)
    //   }
    // } else {
    //   this.rdnd = true;
    //   if(this.questionData.dndSolution){
    //     this.items = Array.from(this.questionData.dndSolution);
    //   }else{
    //     this.items = this.questionData.itemList? Array.from(this.questionData.itemList): [];
    //   }
    // }
    // this.checkIfSmallScreen();
    // this.dataLoaded = true;

    const data = this.data

    if (data.hasOwnProperty('userSolution')) {
      this.resultView = true
    } else {
      this.resultView = false
    }

    if (data.questionType === 'dnd') {
      this.rdnd = false
      this.itemsAreUnique = data.itemsAreUnique
      this.categories = data.categoryList ? data.categoryList : []

      if (!this.resultView && data.dndSolution !== null || this.resultView && data.userSolution !== null && data) {
        this.soln = this.resultView ? data.userSolution : data.dndSolution
        data.itemsAreUnique ? this.getUndroppedItemsWhileSettingData() : this.setDataWhenItemsAreNotUnique()

      } else {
        this.items = data.itemList ? data.itemList : []
      }

    } else {
      this.rdnd = true
      if (this.resultView) {
        // this.items = data.userSolution ? Array.from(data.userSolution) : data.itemList;
      } else if (!this.resultView) {
        this.items = data.itemList ? Array.from(data.itemList) : data.itemList
      } else {
        this.items = []
      }
    }

    this.checkIfSmallScreen()
    this.dataLoaded = true
  }

  getUndroppedItemsWhileSettingData() {
    let droppedItems: any = []
    // this.openedCategoryName = "";
    for (const key in this.soln) {
      if (this.soln.hasOwnProperty(key)) {
        const arr = this.soln[key]

        if (this.openedCategoryName === '' && arr.length > 0) {
          this.openedCategoryName = key
          this.markOpenedCategory()
        }

        droppedItems = droppedItems.concat(arr)
      }
    }
    this.cateOpened = droppedItems.length > 0 ? true : false
    this.items = this.data.itemList.filter(x => !droppedItems.includes(x))

  }

  setDataWhenItemsAreNotUnique() {
    this.items = this.data.itemList ? this.data.itemList : []
    for (const key in this.soln) {
      if (this.soln.hasOwnProperty(key)) {
        const arr = this.soln[key]
        if (this.openedCategoryName === '' && arr.length > 0) {
          this.openedCategoryName = key
          this.markOpenedCategory()
          this.cateOpened = true
          break
        }
      }
    }
  }

  openCategory(cName: string) {
    if (!this.soln) {
      this.soln = {}
    }

    const sCat = this.soln.hasOwnProperty(cName)
    if (!sCat) {
      this.soln[cName] = []
      this.openedCategoryName = cName
    } else {
      this.openedCategoryName = cName
    }

    this.markOpenedCategory()
    this.cateOpened = true
  }

  markOpenedCategory() {
    const i = this.categories.indexOf(this.openedCategoryName)
    this.cMap = Array(this.categories.length).fill(false)
    this.cMap[i] = true
  }

  checkIfSmallScreen() {
    this.smallScreen = window.innerWidth < 700 ? true : false
  }

  // showAndHideSol() {
  //   this.showingSol = !this.showingSol;
  //   if(!this.rdnd){
  //     if (this.showingSol) {
  //       console.log("opening sol");

  //       this.cateOpened = true;
  //       this.getUndroppedItemsWhileSettingData();
  //       this.markOpenedCategory();
  //     } else {
  //       console.log("closin sol");
  //       this.openCategory("");
  //       this.showingSol = false;
  //       this.cateOpened = false;
  //       this.items = this.questionData.itemList;
  //       this.cMap = new Array(this.categories.length).fill(false);
  //     }
  //   }else{
  //     if(this.showingSol){
  //       this.items = this.data.itemList ? Array.from(this.data.itemList) : [];
  //     }
  //   }
  // }

}
