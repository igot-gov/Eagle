import { COMMA, ENTER } from '@angular/cdk/keycodes'
import {
  AfterViewInit,
  ChangeDetectorRef,
  Component,
  ElementRef,
  EventEmitter,
  Input,
  OnDestroy,
  OnInit,
  Output,
  ViewChild,
  Inject,
} from '@angular/core'
import { FormBuilder, FormControl, FormGroup } from '@angular/forms'
import { MatAutocompleteSelectedEvent } from '@angular/material'
import { MatChipInputEvent } from '@angular/material/chips'
import { MatDialog, MAT_DIALOG_DATA } from '@angular/material/dialog'
import { MatSnackBar } from '@angular/material/snack-bar'
import { VIEWER_ROUTE_FROM_MIME } from '@ws-widget/collection/src/public-api'
import { ConfigurationsService } from '@ws-widget/utils'
import { ImageCropComponent } from '@ws-widget/utils/src/public-api'
import { CONTENT_BASE_STATIC, CONTENT_BASE_STREAM } from '@ws/author/src/lib/constants/apiEndpoints'
import { NOTIFICATION_TIME } from '@ws/author/src/lib/constants/constant'
import { Notify } from '@ws/author/src/lib/constants/notificationMessage'
import { IMAGE_MAX_SIZE, IMAGE_SUPPORT_TYPES } from '@ws/author/src/lib/constants/upload'
import { NSContent } from '@ws/author/src/lib/interface/content'
import { NotificationComponent } from '@ws/author/src/lib/modules/shared/components/notification/notification.component'
import { EditorContentService } from '@ws/author/src/lib/routing/modules/editor/services/editor-content.service'
import { EditorService } from '@ws/author/src/lib/routing/modules/editor/services/editor.service'
import { Observable, of, Subscription } from 'rxjs'
import { InterestService } from '../../../../../../../../../app/src/lib/routes/profile/routes/interest/services/interest.service'
import { UploadService } from '../../services/upload.service'
import { CatalogSelectComponent } from '../catalog-select/catalog-select.component'
import { IFormMeta } from './../../../../../../interface/form'
import { AccessControlService } from './../../../../../../modules/shared/services/access-control.service'
import { AuthInitService } from './../../../../../../services/init.service'
import { LoaderService } from './../../../../../../services/loader.service'
import { CompetencyAddPopUpComponent } from '../competency-add-popup/competency-add-popup'
import {
  debounceTime,
  distinctUntilChanged,
  filter,
  startWith,
  switchMap,
  map,
} from 'rxjs/operators'
// import { STEPPER_GLOBAL_OPTIONS } from '@angular/cdk/stepper'

export interface IUsersData {
  name?: string
  id: string
  srclang: string
  languages: any[]
}

@Component({
  selector: 'ws-auth-edit-meta',
  templateUrl: './edit-meta.component.html',
  styleUrls: ['./edit-meta.component.scss'],
  // providers: [{
  //   provide: STEPPER_GLOBAL_OPTIONS, useValue: { displayDefaultIndicatorType: false },
  // }],
})
export class EditMetaComponent implements OnInit, OnDestroy, AfterViewInit {
  contentMeta!: NSContent.IContentMeta
  @Output() data = new EventEmitter<string>()
  @Input() isSubmitPressed = false
  @Input() nextAction = 'done'
  @Input() stage = 1
  @Input() type = ''
  location = CONTENT_BASE_STATIC
  selectable = true
  removable = true
  addOnBlur = true
  addConcepts = false
  isFileUploaded = false
  fileUploadForm!: FormGroup
  creatorContactsCtrl!: FormControl
  trackContactsCtrl!: FormControl
  publisherDetailsCtrl!: FormControl
  editorsCtrl!: FormControl
  creatorDetailsCtrl!: FormControl
  audienceCtrl!: FormControl
  jobProfileCtrl!: FormControl
  regionCtrl!: FormControl
  accessPathsCtrl!: FormControl
  keywordsCtrl!: FormControl
  competencyCtrl!: FormControl
  contentForm!: FormGroup
  selectedSkills: string[] = []
  canUpdate = true
  ordinals!: any
  resourceTypes: string[] = []
  employeeList: any[] = []
  audienceList: any[] = []
  jobProfileList: any[] = []
  regionList: any[] = []
  accessPathList: any[] = []
  competencyValue: any[] = []
  infoType = ''
  fetchTagsStatus: 'done' | 'fetching' | null = null
  readonly separatorKeysCodes: number[] = [ENTER, COMMA]
  selectedIndex = 0
  hours = 0
  minutes = 0
  seconds = 0
  @Input() parentContent: string | null = null
  routerSubscription!: Subscription
  imageTypes = IMAGE_SUPPORT_TYPES
  canExpiry = true
  showMoreGlance = false
  complexityLevelList: string[] = []
  isEditEnabled = false
  banners = [{ color: '#003F5C', isDefault: true }, { color: '#59468B', isDefault: false },
  { color: '#185F49', isDefault: false }, { color: '#126489', isDefault: false }]

  workFlow = [{ isActive: true, isCompleted: false, name: 'Basic Details', step: 0 },
  { isActive: false, isCompleted: false, name: 'Classification', step: 1 },
  { isActive: false, isCompleted: false, name: 'Intended for', step: 2 }]

  allLanguages: any[] = []

  file?: File

  @ViewChild('creatorContactsView', { static: false }) creatorContactsView!: ElementRef
  @ViewChild('trackContactsView', { static: false }) trackContactsView!: ElementRef
  @ViewChild('publisherDetailsView', { static: false }) publisherDetailsView!: ElementRef
  @ViewChild('editorsView', { static: false }) editorsView!: ElementRef
  @ViewChild('creatorDetailsView', { static: false }) creatorDetailsView!: ElementRef
  @ViewChild('audienceView', { static: false }) audienceView!: ElementRef
  @ViewChild('jobProfileView', { static: false }) jobProfileView!: ElementRef
  @ViewChild('regionView', { static: false }) regionView!: ElementRef
  @ViewChild('accessPathsView', { static: false }) accessPathsView!: ElementRef
  @ViewChild('keywordsSearch', { static: true }) keywordsSearch!: ElementRef<any>
  @ViewChild('competencyView', { static: true }) competencyView!: ElementRef<any>

  timer: any

  filteredOptions$: Observable<string[]> = of([])
  competencyOptions$: Observable<any[]> = of([])

  constructor(
    private formBuilder: FormBuilder,
    private uploadService: UploadService,
    private snackBar: MatSnackBar,
    public dialog: MatDialog,
    private editorService: EditorService,
    private contentService: EditorContentService,
    private configSvc: ConfigurationsService,
    private ref: ChangeDetectorRef,
    private interestSvc: InterestService,
    private loader: LoaderService,
    private authInitService: AuthInitService,
    private accessService: AccessControlService,
    @Inject(MAT_DIALOG_DATA) public data1: IUsersData,
  ) {
    // console.log("Parent component", this.parentContent)

  }

  ngAfterViewInit() {
    this.ref.detach()
    this.timer = setInterval(() => {
      this.ref.detectChanges()
      // tslint:disable-next-line: align
    }, 100)
  }

  ngOnInit() {
    this.typeCheck()
    // tslint:disable-next-line: max-line-length
    this.ordinals = { categoryType: ['Article', 'Lecture', 'Quiz', 'Exercise', 'Prelude', 'Tryout', 'Assessment', 'Offering', 'Webinar-Recording', 'Case-Study', 'Insights', 'How-To', 'Brochure', 'Lesson', 'Research-Study', 'Livestream', 'Elevator-Pitch', 'Primer', 'Capstone-Project', 'Client-Deck', 'Podcast', 'Body-of-Knowledge', 'Code-Component', 'Analyst-Report', 'Point-of-View', 'Infographics', 'HandsOnAssessment', 'Competitive-Insights', 'References', 'Concepts', 'Facts', 'Processes', 'Procedures', 'Principles'], skills: [{ identifier: '1', name: 'Computer Science', skill: 'Computer Science', category: 'Computer Science' }, { identifier: '2', name: 'Robotics', skill: 'Robotics', category: 'Robotics' }], accessPaths: ['igot', 'igot/dopt', 'igot/istm', 'igot/npa'], complexityLevel: ['Beginner', 'Intermediate', 'Advanced', 'Expert'], audience: ['All', 'Developers', 'Managers', 'Sales', 'Onboarding'], licenses: ['CC BY (Attribution)', 'CC BY-SA (Attribution-Share-Alike)', 'CC BY-ND (Attribution-NoDerivs)', 'CC BY-NC (Attribution-NonCommercial)', 'CC BY-NC-SA (Attribution-NonCommercial-ShareAlike)', 'CC BY-NC-ND (Attribution-NonCommercial-NoDerivs )'], subTitles: [{ label: 'Abkhazian', srclang: 'ab' }, { label: 'Afar', srclang: 'aa' }, { label: 'Afrikaans', srclang: 'af' }, { label: 'Akan', srclang: 'ak' }, { label: 'Albanian', srclang: 'sq' }, { label: 'Amharic', srclang: 'am' }, { label: 'Arabic', srclang: 'ar' }, { label: 'Aragonese', srclang: 'an' }, { label: 'Armenian', srclang: 'hy' }, { label: 'Assamese', srclang: 'as' }, { label: 'Avaric', srclang: 'av' }, { label: 'Avestan', srclang: 'ae' }, { label: 'Aymara', srclang: 'ay' }, { label: 'Azerbaijani', srclang: 'az' }, { label: 'Bambara', srclang: 'bm' }, { label: 'Bashkir', srclang: 'ba' }, { label: 'Basque', srclang: 'eu' }, { label: 'Belarusian', srclang: 'be' }, { label: 'Bengali (Bangla)', srclang: 'bn' }, { label: 'Bihari', srclang: 'bh' }, { label: 'Bislama', srclang: 'bi' }, { label: 'Bosnian', srclang: 'bs' }, { label: 'Breton', srclang: 'br' }, { label: 'Bulgarian', srclang: 'bg' }, { label: 'Burmese', srclang: 'my' }, { label: 'Catalan', srclang: 'ca' }, { label: 'Chamorro', srclang: 'ch' }, { label: 'Chechen', srclang: 'ce' }, { label: 'Chichewa', srclang: ' ny' }, { label: 'Chewa', srclang: 'ny' }, { label: 'Nyanja', srclang: 'ny' }, { label: 'Chinese', srclang: 'zh' }, { label: 'Chinese (Simplified)', srclang: 'zh-Hans' }, { label: 'Chinese (Traditional)', srclang: 'zh-Hant' }, { label: 'Chuvash', srclang: 'cv' }, { label: 'Cornish', srclang: 'kw' }, { label: 'Corsican', srclang: 'co' }, { label: 'Cree', srclang: 'cr' }, { label: 'Croatian', srclang: 'hr' }, { label: 'Czech', srclang: 'cs' }, { label: 'Danish', srclang: 'da' }, { label: 'Divehi', srclang: 'dv' }, { label: 'Dhivehi', srclang: 'dv' }, { label: 'Maldivian', srclang: 'dv' }, { label: 'Dutch', srclang: 'nl' }, { label: 'Dzongkha', srclang: 'dz' }, { label: 'English', srclang: 'en' }, { label: 'Esperanto', srclang: 'eo' }, { label: 'Estonian', srclang: 'et' }, { label: 'Ewe', srclang: 'ee' }, { label: 'Faroese', srclang: 'fo' }, { label: 'Fijian', srclang: 'fj' }, { label: 'Finnish', srclang: 'fi' }, { label: 'French', srclang: 'fr' }, { label: 'Fula', srclang: 'ff' }, { label: 'Fulah', srclang: 'ff' }, { label: 'Pulaar', srclang: 'ff' }, { label: 'Pular', srclang: 'ff' }, { label: 'Galician', srclang: 'gl' }, { label: 'Gaelic (Scottish)', srclang: 'gd' }, { label: 'Gaelic (Manx)', srclang: 'gv' }, { label: 'Georgian', srclang: 'ka' }, { label: 'German', srclang: 'de' }, { label: 'Greek', srclang: 'el' }, { label: 'Greenlandickl' }, { label: 'Guarani', srclang: 'gn' }, { label: 'Gujarati', srclang: 'gu' }, { label: 'Haitian Creole', srclang: 'ht' }, { label: 'Hausa', srclang: 'ha' }, { label: 'Hebrew', srclang: 'he' }, { label: 'Herero', srclang: 'hz' }, { label: 'Hindi', srclang: 'hi' }, { label: 'Hiri Motu', srclang: 'ho' }, { label: 'Hungarian', srclang: 'hu' }, { label: 'Icelandic', srclang: 'is' }, { label: 'Ido', srclang: 'io' }, { label: 'Igbo', srclang: 'ig' }, { label: 'Indonesian', srclang: 'id' }, { label: 'Interlingua', srclang: 'ia' }, { label: 'Interlingue', srclang: 'ie' }, { label: 'Inuktitut', srclang: 'iu' }, { label: 'Inupiak', srclang: 'ik' }, { label: 'Irish', srclang: 'ga' }, { label: 'Italian', srclang: 'it' }, { label: 'Japanese', srclang: 'ja' }, { label: 'Javanese', srclang: 'jv' }, { label: 'Kalaallisut', srclang: 'kl' }, { label: 'Greenlandic', srclang: 'kl' }, { label: 'Kannada', srclang: 'kn' }, { label: 'Kanuri', srclang: 'kr' }, { label: 'Kashmiri', srclang: 'ks' }, { label: 'Kazakh', srclang: 'kk' }, { label: 'Khmer', srclang: 'km' }, { label: 'Kikuyu', srclang: 'ki' }, { label: 'Kinyarwanda (Rwanda)', srclang: 'rw' }, { label: 'Kirundi', srclang: 'rn' }, { label: 'Kyrgyz', srclang: 'ky' }, { label: 'Komi', srclang: 'kv' }, { label: 'Kongo', srclang: 'kg' }, { label: 'Korean', srclang: 'ko' }, { label: 'Kurdish', srclang: 'ku' }, { label: 'Kwanyama', srclang: 'kj' }, { label: 'Lao', srclang: 'lo' }, { label: 'Latin', srclang: 'la' }, { label: 'Latvian (Lettish)', srclang: 'lv' }, { label: 'Limburgish ( Limburger)', srclang: 'li' }, { label: 'Lingala', srclang: 'ln' }, { label: 'Lithuanian', srclang: 'lt' }, { label: 'Luga-Katanga', srclang: 'lu' }, { label: 'Luganda, Ganda', srclang: 'lg' }, { label: 'Luxembourgish', srclang: 'lb' }, { label: 'Manx', srclang: 'gv' }, { label: 'Macedonian', srclang: 'mk' }, { label: 'Malagasy', srclang: 'mg' }, { label: 'Malay', srclang: 'ms' }, { label: 'Malayalam', srclang: 'ml' }, { label: 'Maltese', srclang: 'mt' }, { label: 'Maori', srclang: 'mi' }, { label: 'Marathi', srclang: 'mr' }, { label: 'Marshallese', srclang: 'mh' }, { label: 'Moldavian', srclang: 'mo' }, { label: 'Mongolian', srclang: 'mn' }, { label: 'Nauru', srclang: 'na' }, { label: 'Navajo', srclang: 'nv' }, { label: 'Ndonga', srclang: 'ng' }, { label: 'Northern Ndebele', srclang: 'nd' }, { label: 'Nepali', srclang: 'ne' }, { label: 'Norwegian', srclang: 'no' }, { label: 'Norwegian bokmÃ¥l', srclang: 'nb' }, { label: 'Norwegian nynorsk', srclang: 'nn' }, { label: 'Nuosu', srclang: 'ii' }, { label: 'Occitan', srclang: 'oc' }, { label: 'Ojibwe', srclang: 'oj' }, { label: 'Old Church Slavonic', srclang: 'cu' }, { label: 'Old Bulgarian', srclang: 'cu' }, { label: 'Oriya', srclang: 'or' }, { label: 'Oromo (Afaan Oromo)', srclang: 'om' }, { label: 'Ossetian', srclang: 'os' }, { label: 'PÄ�li', srclang: 'pi' }, { label: 'Pashto', srclang: 'ps' }, { label: 'Pushto', srclang: 'ps' }, { label: 'Persian', srclang: ' (Farsi)fa' }, { label: 'Polish', srclang: 'pl' }, { label: 'Portuguese', srclang: 'pt' }, { label: 'Punjabi (Eastern)', srclang: 'pa' }, { label: 'Quechua', srclang: 'qu' }, { label: 'Romansh', srclang: 'rm' }, { label: 'Romanian', srclang: 'ro' }, { label: 'Russian', srclang: 'ru' }, { label: 'Sami', srclang: 'se' }, { label: 'Samoan', srclang: 'sm' }, { label: 'Sango', srclang: 'sg' }, { label: 'Sanskrit', srclang: 'sa' }, { label: 'Serbian', srclang: 'sr' }, { label: 'Serbo-Croatian', srclang: 'sh' }, { label: 'Sesotho', srclang: 'st' }, { label: 'Setswana', srclang: 'tn' }, { label: 'Shona', srclang: 'sn' }, { label: 'Sichuan Yi', srclang: 'ii' }, { label: 'Sindhi', srclang: 'sd' }, { label: 'Sinhalese', srclang: 'si' }, { label: 'Siswati', srclang: 'ss' }, { label: 'Slovak', srclang: 'sk' }, { label: 'Slovenian', srclang: 'sl' }, { label: 'Somali', srclang: 'so' }, { label: 'Southern Ndebele', srclang: 'nr' }, { label: 'Spanish', srclang: 'es' }, { label: 'Sundanese', srclang: 'su' }, { label: 'Swahili (Kiswahili)', srclang: 'sw' }, { label: 'Swati', srclang: 'ss' }, { label: 'Swedish', srclang: 'sv' }, { label: 'Tagalog', srclang: 'tl' }, { label: 'Tahitian', srclang: 'ty' }, { label: 'Tajik', srclang: 'tg' }, { label: 'Tamil', srclang: 'ta' }, { label: 'Tatar', srclang: 'tt' }, { label: 'Telugu', srclang: 'te' }, { label: 'Thai', srclang: 'th' }, { label: 'Tibetan', srclang: 'bo' }, { label: 'Tigrinya', srclang: 'ti' }, { label: 'Tonga', srclang: 'to' }, { label: 'Tsonga', srclang: 'ts' }, { label: 'Turkish', srclang: 'tr' }, { label: 'Turkmen', srclang: 'tk' }, { label: 'Twi', srclang: 'tw' }, { label: 'Uyghur', srclang: 'ug' }, { label: 'Ukrainian', srclang: 'uk' }, { label: 'Urdu', srclang: 'ur' }, { label: 'Uzbek', srclang: 'uz' }, { label: 'Venda', srclang: 've' }, { label: 'Vietnamese', srclang: 'vi' }, { label: 'VolapÃ¼k', srclang: 'vo' }, { label: 'Wallon', srclang: 'wa' }, { label: 'Welsh', srclang: 'cy' }, { label: 'Wolof', srclang: 'wo' }, { label: 'Western Frisian', srclang: 'fy' }, { label: 'Xhosa', srclang: 'xh' }, { label: 'Yiddish', srclang: 'yi' }, { label: 'Yoruba', srclang: 'yo' }, { label: 'Zhuang', srclang: 'za' }, { label: ' Chuang', srclang: 'za' }, { label: 'Zulu', srclang: 'zu' }], currency: ['INR'], sourceName: ['ISTM (Institute of Secretariat Training and Management)', 'DoPT(Department of Personnel and Training', 'LBSNNA', 'SVPNPA', 'Enthralltech', 'Wadhwani Foundation', 'Harappa Education', 'J-PAL SA'], contentType: ['Channel', 'Course', 'Knowledge Artifact', 'Knowledge Board', 'Module', 'Program', 'Resource'], learningMode: ['Self-Paced', 'Instructor-Led'], resourceType: ['Article', 'Lecture', 'Quiz', 'Exercise', 'Prelude', 'Tryout', 'Assessment', 'Offering', 'Webinar-Recording', 'Case-Study', 'Insights', 'How-To', 'Brochure', 'Lesson', 'Research-Study', 'Livestream', 'Elevator-Pitch', 'Primer', 'Capstone-Project', 'Client-Deck', 'Podcast', 'Body-of-Knowledge', 'Code-Component', 'Analyst-Report', 'Point-of-View', 'Infographics', 'HandsOnAssessment', 'Competitive-Insights', 'References', 'Concepts', 'Facts', 'Processes', 'Procedures', 'Principles'] }
    // this.authInitService.ordinals
    if (this.ordinals && Object.keys(this.ordinals).length > 0) {
      this.audienceList = this.ordinals.audience
      this.jobProfileList = this.ordinals.jobProfile
      this.complexityLevelList = this.ordinals.audience
    }
    this.creatorContactsCtrl = new FormControl()
    this.trackContactsCtrl = new FormControl()
    this.publisherDetailsCtrl = new FormControl()
    this.editorsCtrl = new FormControl()
    this.creatorDetailsCtrl = new FormControl()
    this.keywordsCtrl = new FormControl('')
    this.competencyCtrl = new FormControl('')

    this.audienceCtrl = new FormControl()
    this.jobProfileCtrl = new FormControl()
    this.regionCtrl = new FormControl()
    this.accessPathsCtrl = new FormControl()
    this.accessPathsCtrl.disable()

    this.creatorContactsCtrl.valueChanges
      .pipe(
        debounceTime(500),
        distinctUntilChanged(),
        filter(val => typeof val === 'string'),
        switchMap((value: string) => {
          if (typeof value === 'string' && value) {
            this.employeeList = <any[]>[]
            this.fetchTagsStatus = 'fetching'
            return this.editorService.fetchEmployeeList(value)
          }
          return of([])
        }),
      )
      .subscribe(
        users => {
          this.employeeList = users || <string[]>[]
          this.fetchTagsStatus = 'done'
        },
        () => {
          this.fetchTagsStatus = 'done'
        },
      )

    this.trackContactsCtrl.valueChanges
      .pipe(
        debounceTime(500),
        distinctUntilChanged(),
        filter(val => typeof val === 'string'),
        switchMap((value: string) => {
          if (typeof value === 'string' && value) {
            this.employeeList = <any[]>[]
            this.fetchTagsStatus = 'fetching'
            return this.editorService.fetchEmployeeList(value)
          }
          return of([])
        }),
      )
      .subscribe(
        users => {
          this.employeeList = users || <string[]>[]
          this.fetchTagsStatus = 'done'
        },
        () => {
          this.fetchTagsStatus = 'done'
        },
      )

    this.publisherDetailsCtrl.valueChanges
      .pipe(
        debounceTime(500),
        distinctUntilChanged(),
        filter(val => typeof val === 'string'),
        switchMap((value: string) => {
          if (typeof value === 'string' && value) {
            this.employeeList = <any[]>[]
            this.fetchTagsStatus = 'fetching'
            return this.editorService.fetchEmployeeList(value)
          }
          return of([])
        }),
      )
      .subscribe(
        users => {
          this.employeeList = users || <string[]>[]
          this.fetchTagsStatus = 'done'
        },
        () => {
          this.fetchTagsStatus = 'done'
        },
      )

    this.editorsCtrl.valueChanges
      .pipe(
        debounceTime(500),
        distinctUntilChanged(),
        filter(val => typeof val === 'string'),
        switchMap((value: string) => {
          if (typeof value === 'string' && value) {
            this.employeeList = <any[]>[]
            this.fetchTagsStatus = 'fetching'
            return this.editorService.fetchEmployeeList(value)
          }
          return of([])
        }),
      )
      .subscribe(
        users => {
          this.employeeList = users || <string[]>[]
          this.fetchTagsStatus = 'done'
        },
        () => {
          this.fetchTagsStatus = 'done'
        },
      )

    this.creatorDetailsCtrl.valueChanges
      .pipe(
        debounceTime(500),
        distinctUntilChanged(),
        filter(val => typeof val === 'string'),
        switchMap((value: string) => {
          if (typeof value === 'string' && value) {
            this.employeeList = <any[]>[]
            this.fetchTagsStatus = 'fetching'
            return this.editorService.fetchEmployeeList(value)
          }
          return of([])
        }),
      )
      .subscribe(
        users => {
          this.employeeList = users || <string[]>[]
          this.fetchTagsStatus = 'done'
        },
        () => {
          this.fetchTagsStatus = 'done'
        },
      )

    this.audienceCtrl.valueChanges.subscribe(() => this.fetchAudience())

    this.jobProfileCtrl.valueChanges.subscribe(() => this.fetchJobProfile())

    this.regionCtrl.valueChanges
      .pipe(
        debounceTime(400),
        filter(v => v),
      )
      .subscribe(() => this.fetchRegion())

    this.accessPathsCtrl.valueChanges.pipe(
      debounceTime(400),
      filter(v => v),
    ).subscribe(() => this.fetchAccessRestrictions())

    this.contentService.changeActiveCont.subscribe(data => {
      if (this.contentMeta && this.canUpdate) {
        this.storeData()
      }
      this.content = this.contentService.getUpdatedMeta(data)
    })

    this.filteredOptions$ = this.keywordsCtrl.valueChanges.pipe(
      startWith(this.keywordsCtrl.value),
      debounceTime(500),
      distinctUntilChanged(),
      switchMap(value => this.interestSvc.fetchAutocompleteInterestsV2(value)),
    )
    this.competencyOptions$ = this.competencyCtrl.valueChanges.pipe(
      startWith(this.competencyCtrl.value),
      debounceTime(500),
      distinctUntilChanged(),
      switchMap(value => this.interestSvc.fetchAutocompleteCompetencyV2(value)),
    )

    this.allLanguages = this.data1.languages
  }
  start() {
    const dialogRef = this.dialog.open(CompetencyAddPopUpComponent, {
      minHeight: 'auto',
      width: '80%',
      panelClass: 'remove-pad',
    })
    dialogRef.afterClosed().subscribe((response: any) => {
      if (response === 'postCreated') {
        // this.refreshData(this.currentActivePage)
      }
    })
  }
  typeCheck() {
    if (this.type) {
      let dataName = ''
      switch (this.type) {
        case 'URL':
          dataName = 'Attach Link'
          break
        case 'UPLOAD':
          dataName = 'Upload'
          break
        case 'ASSE':
          dataName = 'Assessment'
          break
        case 'WEB':
          dataName = 'Web Pages'
          break

        default:
          break
      }
      if (dataName) {
        this.workFlow.push({
          isActive: false,
          isCompleted: true,
          name: dataName,
          step: -1,
        })
      }
    }
  }

  optionSelected(keyword: string) {
    this.keywordsCtrl.setValue(' ')
    this.keywordsSearch.nativeElement.blur()
    if (keyword && keyword.length) {
      const value = this.contentForm.controls.keywords.value || []
      if (value.indexOf(keyword) === -1) {
        value.push(keyword)
        this.contentForm.controls.keywords.setValue(value)
      }
    }
  }

  canPush(arr: any[], obj: any) {
    for (const test of arr) {
      if (test.id === obj.id) {
        return false
      }
    }
    return true

  }

  ngOnDestroy() {
    if (this.routerSubscription) {
      this.routerSubscription.unsubscribe()
    }
    this.loader.changeLoad.next(false)
    this.ref.detach()
    clearInterval(this.timer)
  }
  customStepper(step: number) {
    if (step >= 0) {
      if (this.selectedIndex !== step) {
        this.selectedIndex = step
        const oldStrip = this.workFlow.find(i => i.isActive)

        this.workFlow[step].isActive = true
        this.workFlow[step].isCompleted = false
        if (oldStrip && oldStrip.step >= 0) {
          this.workFlow[oldStrip.step].isActive = false
          this.workFlow[oldStrip.step].isCompleted = true
        }
      }
    } else {
      this.data.emit('back')
    }
  }
  private set content(contentMeta: NSContent.IContentMeta) {
    this.contentMeta = contentMeta
    this.isEditEnabled = this.contentService.hasAccess(
      contentMeta,
      false,
      // this.parentContent ? this.contentService.getUpdatedMeta(this.parentContent) : undefined,
    )
    this.contentMeta.name = contentMeta.name === 'Untitled Content' ? '' : contentMeta.name
    if (!this.contentMeta.posterImage) {
      this.contentMeta.posterImage = this.banners[0].color
    }
    // this.contentMeta.posterImage = contentMeta.posterImage
    this.canExpiry = this.contentMeta.expiryDate !== '99991231T235959+0000'
    if (this.canExpiry) {
      this.contentMeta.expiryDate =
        contentMeta.expiryDate && contentMeta.expiryDate.indexOf('+') === 15
          ? <any>this.convertToISODate(contentMeta.expiryDate)
          : ''
    }
    this.assignFields()
    this.setDuration(contentMeta.duration || 0)
    this.filterOrdinals()
    this.changeResourceType()
  }

  filterOrdinals() {
    this.complexityLevelList = []
    if (this.ordinals) {
      this.ordinals.complexityLevel.map((v: any) => {
        if (v.condition) {
          let canAdd = false
            // tslint:disable-next-line: whitespace
            ; (v.condition.showFor || []).map((con: any) => {
              let innerCondition = false
              Object.keys(con).map(meta => {
                if (
                  con[meta].indexOf(
                    (this.contentForm.controls[meta] && this.contentForm.controls[meta].value) ||
                    this.contentMeta[meta as keyof NSContent.IContentMeta],
                  ) > -1
                ) {
                  innerCondition = true
                }
              })
              if (innerCondition) {
                canAdd = true
              }
            })
          if (canAdd) {
            // tslint:disable-next-line: semicolon // tslint:disable-next-line: whitespace
            ; (v.condition.nowShowFor || []).map((con: any) => {
              let innerCondition = false
              Object.keys(con).map(meta => {
                if (
                  con[meta].indexOf(
                    (this.contentForm.controls[meta] && this.contentForm.controls[meta].value) ||
                    this.contentMeta[meta as keyof NSContent.IContentMeta],
                  ) < 0
                ) {
                  innerCondition = true
                }
              })
              if (innerCondition) {
                canAdd = false
              }
            })
          }
          if (canAdd) {
            this.complexityLevelList.push(v.value)
          }
        } else {
          if (typeof v === 'string') {
            this.complexityLevelList.push(v)
          } else {
            this.complexityLevelList.push(v.value)
          }
        }
      })
    }
  }

  assignExpiryDate() {
    this.canExpiry = !this.canExpiry
    this.contentForm.controls.expiryDate.setValue(
      this.canExpiry
        ? new Date(new Date().setMonth(new Date().getMonth() + 6))
        : '99991231T235959+0000',
    )
  }
  assignFields() {
    if (!this.contentForm) {
      this.createForm()
    }
    this.canUpdate = false
    Object.keys(this.contentForm.controls).map(v => {
      try {
        if (
          this.contentMeta[v as keyof NSContent.IContentMeta] ||
          (this.authInitService.authConfig[v as keyof IFormMeta].type === 'boolean' &&
            this.contentMeta[v as keyof NSContent.IContentMeta] === false)
        ) {
          this.contentForm.controls[v].setValue(this.contentMeta[v as keyof NSContent.IContentMeta])
        } else {
          if (v === 'expiryDate') {
            this.contentForm.controls[v].setValue(
              new Date(new Date().setMonth(new Date().getMonth() + 6)),
            )
          } else {
            this.contentForm.controls[v].setValue(
              JSON.parse(
                JSON.stringify(
                  this.authInitService.authConfig[v as keyof IFormMeta].defaultValue[
                    this.contentMeta.contentType
                    // tslint:disable-next-line: ter-computed-property-spacing
                  ][0].value,
                ),
              ),
            )
          }
        }
        if (this.isSubmitPressed) {
          this.contentForm.controls[v].markAsDirty()
          this.contentForm.controls[v].markAsTouched()
        } else {
          this.contentForm.controls[v].markAsPristine()
          this.contentForm.controls[v].markAsUntouched()
        }
      } catch (ex) { }
    })
    this.canUpdate = true
    this.storeData()
    if (this.isSubmitPressed) {
      this.contentForm.markAsDirty()
      this.contentForm.markAsTouched()
    } else {
      this.contentForm.markAsPristine()
      this.contentForm.markAsUntouched()
    }
  }

  convertToISODate(date = ''): Date {
    try {
      return new Date(
        `${date.substring(0, 4)}-${date.substring(4, 6)}-${date.substring(6, 8)}${date.substring(
          8,
          11,
        )}:${date.substring(11, 13)}:${date.substring(13, 15)}.000Z`,
      )
    } catch (ex) {
      return new Date(new Date().setMonth(new Date().getMonth() + 6))
    }
  }

  changeMimeType() {
    const artifactUrl = this.contentForm.controls.artifactUrl.value
    if (this.contentForm.controls.contentType.value === 'Course') {
      this.contentForm.controls.mimeType.setValue('application/vnd.ekstep.content-collection')
    } else {
      this.contentForm.controls.mimeType.setValue('application/html')
      if (
        this.configSvc.instanceConfig &&
        this.configSvc.instanceConfig.authoring &&
        this.configSvc.instanceConfig.authoring.urlPatternMatching
      ) {
        this.configSvc.instanceConfig.authoring.urlPatternMatching.map(v => {
          if (artifactUrl.match(v.pattern) && v.allowIframe && v.source === 'youtube') {
            this.contentForm.controls.mimeType.setValue('video/x-youtube')
          }
        })
      }
    }
  }

  changeResourceType() {
    if (this.contentForm.controls.contentType.value === 'Resource') {
      this.resourceTypes = (this.ordinals) ? (this.ordinals.resourceType || this.ordinals.categoryType || []) : []
    } else {
      this.resourceTypes = (this.ordinals) ? (this.ordinals['Offering Mode'] || this.ordinals.categoryType || []) : []
    }

    if (this.resourceTypes.indexOf(this.contentForm.controls.categoryType.value) < 0) {
      this.contentForm.controls.resourceType.setValue('')
    }
  }

  private setDuration(seconds: any) {
    const minutes = seconds > 59 ? Math.floor(seconds / 60) : 0
    const second = seconds % 60
    this.hours = minutes ? (minutes > 59 ? Math.floor(minutes / 60) : 0) : 0
    this.minutes = minutes ? minutes % 60 : 0
    this.seconds = second || 0
  }

  timeToSeconds() {
    let total = 0
    total += this.seconds ? (this.seconds < 60 ? this.seconds : 59) : 0
    total += this.minutes ? (this.minutes < 60 ? this.minutes : 59) * 60 : 0
    total += this.hours ? this.hours * 60 * 60 : 0
    this.contentForm.controls.duration.setValue(total)
  }

  showInfo(type: string) {
    this.infoType = this.infoType === type ? '' : type
  }

  storeData() {
    try {
      const originalMeta = this.contentService.getOriginalMeta(this.contentMeta.identifier)
      if (originalMeta && this.isEditEnabled) {
        const expiryDate = this.contentForm.value.expiryDate
        const currentMeta: NSContent.IContentMeta = JSON.parse(JSON.stringify(this.contentForm.value))
        if (originalMeta.mimeType) {
          currentMeta.mimeType = originalMeta.mimeType
        }
        const meta = <any>{}
        if (this.canExpiry) {
          currentMeta.expiryDate = `${expiryDate
            .toISOString()
            .replace(/-/g, '')
            .replace(/:/g, '')
            .split('.')[0]
            }+0000`
        }
        Object.keys(currentMeta).map(v => {
          if (
            v !== 'versionKey' &&
            JSON.stringify(currentMeta[v as keyof NSContent.IContentMeta]) !==
            JSON.stringify(originalMeta[v as keyof NSContent.IContentMeta])
          ) {
            if (
              currentMeta[v as keyof NSContent.IContentMeta] ||
              (this.authInitService.authConfig[v as keyof IFormMeta].type === 'boolean' &&
                currentMeta[v as keyof NSContent.IContentMeta] === false)
            ) {
              meta[v as keyof NSContent.IContentMeta] = currentMeta[v as keyof NSContent.IContentMeta]
            } else {
              meta[v as keyof NSContent.IContentMeta] = JSON.parse(
                JSON.stringify(
                  this.authInitService.authConfig[v as keyof IFormMeta].defaultValue[
                    originalMeta.contentType
                    // tslint:disable-next-line: ter-computed-property-spacing
                  ][0].value,
                ),
              )
            }
          } else if (v === 'versionKey') {
            meta[v as keyof NSContent.IContentMeta] = originalMeta[v as keyof NSContent.IContentMeta]
          }
        })
        // Quick FIX
        // if (this.stage >= 1 && !this.type) {
        //   delete meta.artifactUrl
        //   delete meta.size
        //   if (meta.creatorDetails && meta.creatorDetails.length === 0)
        //     delete meta.creatorDetails
        //   if (meta.trackContacts && meta.trackContacts.length === 0)
        //     delete meta.trackContacts
        //   if (meta.creatorContacts && meta.creatorContacts.length === 0)
        //     delete meta.creatorContacts
        //   if (meta.publisherDetails && meta.publisherDetails.length === 0)
        //     delete meta.publisherDetails
        //   delete meta.duration
        // }
        // if (this.stage >= 2) {
        //   delete meta.name
        //   delete meta.body
        //   delete meta.subTitle
        //   delete meta.description
        //   delete meta.thumbnail
        //   delete meta.creatorLogo
        //   delete meta.creatorThumbnail
        //   delete meta.creatorPosterImage
        //   delete meta.posterImage
        //   delete meta.sourceName
        //   delete meta.categoryType
        // }
        // if (this.stage >= 3) {
        //   delete meta.complexityLevel
        //   delete meta.idealScreenSize
        //   delete meta.learningMode
        //   delete meta.visibility
        //   delete meta.exclusiveContent
        //   delete meta.keywords
        //   delete meta.catalogPaths
        // }
        this.contentService.setUpdatedMeta(meta, this.contentMeta.identifier)
      }
    } catch (ex) {
      this.snackBar.open('Please Save Parent first and refresh page.')
    }
  }

  updateContentService(meta: string, value: any, event = false) {
    this.contentForm.controls[meta].setValue(value, { events: event })
    this.contentService.setUpdatedMeta({ [meta]: value } as any, this.contentMeta.identifier)
  }

  formNext(index: number) {
    // this.selectedIndex = index
    this.customStepper(index)
  }

  addKeyword(event: MatChipInputEvent): void {
    const input = event.input
    event.value
      .split(/[,]+/)
      .map((val: string) => val.trim())
      .forEach((value: string) => this.optionSelected(value))
    input.value = ''
  }

  addReferences(event: MatChipInputEvent): void {
    const input = event.input
    const value = event.value

    // Add our fruit
    if ((value || '').trim().length) {
      const oldArray = this.contentForm.controls.references.value || []
      oldArray.push({ title: '', url: value })
      this.contentForm.controls.references.setValue(oldArray)
    }

    // Reset the input value
    if (input) {
      input.value = ''
    }
  }

  removeKeyword(keyword: any): void {
    const index = this.contentForm.controls.keywords.value.indexOf(keyword)
    this.contentForm.controls.keywords.value.splice(index, 1)
    this.contentForm.controls.keywords.setValue(this.contentForm.controls.keywords.value)
  }

  removeCompetency(competencies: any): void {
    const index = this.contentForm.controls.competencies.value.indexOf(competencies)
    this.contentForm.controls.competencies.value.splice(index, 1)
    this.contentForm.controls.competencies.setValue(this.contentForm.controls.competencies.value)
  }

  removeReferences(index: number): void {
    this.contentForm.controls.references.value.splice(index, 1)
    this.contentForm.controls.references.setValue(this.contentForm.controls.references.value)
  }

  compareSkillFn(value1: { identifier: string }, value2: { identifier: string }) {
    return value1 && value2 ? value1.identifier === value2.identifier : value1 === value2
  }

  addCreatorDetails(event: MatChipInputEvent): void {
    const input = event.input
    const value = (event.value || '').trim()
    if (value) {
      this.contentForm.controls.creatorDetails.value.push({ id: '', name: value })
      this.contentForm.controls.creatorDetails.setValue(
        this.contentForm.controls.creatorDetails.value,
      )
    }
    // Reset the input value
    if (input) {
      input.value = ''
    }
  }

  removeCreatorDetails(keyword: any): void {
    const index = this.contentForm.controls.creatorDetails.value.indexOf(keyword)
    this.contentForm.controls.creatorDetails.value.splice(index, 1)
    this.contentForm.controls.creatorDetails.setValue(
      this.contentForm.controls.creatorDetails.value,
    )
  }

  addToFormControl(event: MatAutocompleteSelectedEvent, fieldName: string): void {
    const value = (event.option.value || '').trim()
    if (value && this.contentForm.controls[fieldName].value.indexOf(value) === -1) {
      this.contentForm.controls[fieldName].value.push(value)
      this.contentForm.controls[fieldName].setValue(this.contentForm.controls[fieldName].value)
    }

    this[`${fieldName}View` as keyof EditMetaComponent].nativeElement.value = ''
    this[`${fieldName}Ctrl` as keyof EditMetaComponent].setValue(null)
  }

  removeFromFormControl(keyword: any, fieldName: string): void {
    const index = this.contentForm.controls[fieldName].value.indexOf(keyword)
    this.contentForm.controls[fieldName].value.splice(index, 1)
    this.contentForm.controls[fieldName].setValue(this.contentForm.controls[fieldName].value)
  }

  conceptToggle() {
    this.addConcepts = !this.addConcepts
  }

  uploadAppIcon(file: File) {
    const formdata = new FormData()
    const fileName = file.name.replace(/[^A-Za-z0-9.]/g, '')
    if (
      !(
        IMAGE_SUPPORT_TYPES.indexOf(
          `.${fileName
            .toLowerCase()
            .split('.')
            .pop()}`,
        ) > -1
      )
    ) {
      this.snackBar.openFromComponent(NotificationComponent, {
        data: {
          type: Notify.INVALID_FORMAT,
        },
        duration: NOTIFICATION_TIME * 1000,
      })
      return
    }

    if (file.size > IMAGE_MAX_SIZE) {
      this.snackBar.openFromComponent(NotificationComponent, {
        data: {
          type: Notify.SIZE_ERROR,
        },
        duration: NOTIFICATION_TIME * 1000,
      })
      return
    }

    const dialogRef = this.dialog.open(ImageCropComponent, {
      width: '70%',
      data: {
        isRoundCrop: false,
        imageFile: file,
        width: 265,
        height: 150,
        isThumbnail: true,
        imageFileName: fileName,
      },
    })

    dialogRef.afterClosed().subscribe({
      next: (result: File) => {
        if (result) {
          formdata.append('content', result, fileName)
          this.loader.changeLoad.next(true)
          this.uploadService
            .upload(formdata, {
              contentId: this.contentMeta.identifier,
              contentType: CONTENT_BASE_STATIC,
            })
            .subscribe(
              data => {
                if (data.code) {
                  this.loader.changeLoad.next(false)
                  this.canUpdate = false
                  this.contentForm.controls.appIcon.setValue(data.artifactURL)
                  this.contentForm.controls.thumbnail.setValue(data.artifactURL)
                  // this.contentForm.controls.posterImage.setValue(data.artifactURL)
                  this.canUpdate = true
                  this.storeData()
                  this.snackBar.openFromComponent(NotificationComponent, {
                    data: {
                      type: Notify.UPLOAD_SUCCESS,
                    },
                    duration: NOTIFICATION_TIME * 1000,
                  })
                }
              },
              () => {
                this.loader.changeLoad.next(false)
                this.snackBar.openFromComponent(NotificationComponent, {
                  data: {
                    type: Notify.UPLOAD_FAIL,
                  },
                  duration: NOTIFICATION_TIME * 1000,
                })
              },
            )
        }
      },
    })
  }
  uploadSourceIcon(file: File) {
    const formdata = new FormData()
    const fileName = file.name.replace(/[^A-Za-z0-9.]/g, '')
    if (
      !(
        IMAGE_SUPPORT_TYPES.indexOf(
          `.${fileName
            .toLowerCase()
            .split('.')
            .pop()}`,
        ) > -1
      )
    ) {
      this.snackBar.openFromComponent(NotificationComponent, {
        data: {
          type: Notify.INVALID_FORMAT,
        },
        duration: NOTIFICATION_TIME * 1000,
      })
      return
    }

    if (file.size > IMAGE_MAX_SIZE) {
      this.snackBar.openFromComponent(NotificationComponent, {
        data: {
          type: Notify.SIZE_ERROR,
        },
        duration: NOTIFICATION_TIME * 1000,
      })
      return
    }

    const dialogRef = this.dialog.open(ImageCropComponent, {
      width: '70%',
      data: {
        isRoundCrop: false,
        imageFile: file,
        width: 72,
        height: 72,
        isThumbnail: true,
        imageFileName: fileName,
      },
    })

    dialogRef.afterClosed().subscribe({
      next: (result: File) => {
        if (result) {
          formdata.append('content', result, fileName)
          this.loader.changeLoad.next(true)
          this.uploadService
            .upload(formdata, {
              contentId: this.contentMeta.identifier,
              contentType: CONTENT_BASE_STATIC,
            })
            .subscribe(
              data => {
                if (data.code) {
                  this.loader.changeLoad.next(false)
                  this.canUpdate = false
                  this.contentForm.controls.creatorLogo.setValue(data.artifactURL)
                  this.contentForm.controls.creatorThumbnail.setValue(data.artifactURL)
                  this.contentForm.controls.creatorPosterImage.setValue(data.artifactURL)
                  this.canUpdate = true
                  this.storeData()
                  this.snackBar.openFromComponent(NotificationComponent, {
                    data: {
                      type: Notify.UPLOAD_SUCCESS,
                    },
                    duration: NOTIFICATION_TIME * 1000,
                  })
                }
              },
              () => {
                this.loader.changeLoad.next(false)
                this.snackBar.openFromComponent(NotificationComponent, {
                  data: {
                    type: Notify.UPLOAD_FAIL,
                  },
                  duration: NOTIFICATION_TIME * 1000,
                })
              },
            )
        }
      },
    })
  }
  changeToDefaultImg($event: any) {
    $event.target.src = this.configSvc.instanceConfig
      ? this.configSvc.instanceConfig.logos.defaultContent
      : ''
  }

  showError(meta: string) {
    if (
      this.contentService.checkCondition(this.contentMeta.identifier, meta, 'required') &&
      !this.contentService.isPresent(meta, this.contentMeta.identifier)
    ) {
      if (this.isSubmitPressed) {
        return true
      }
      if (this.contentForm.controls[meta] && this.contentForm.controls[meta].touched) {
        return true
      }
      return false
    }
    return false
  }

  removeEmployee(employee: NSContent.IAuthorDetails, field: string): void {
    const index = this.contentForm.controls[field].value.indexOf(employee)
    this.contentForm.controls[field].value.splice(index, 1)
    this.contentForm.controls[field].setValue(this.contentForm.controls[field].value)
  }
  optionSelectedCompetency(competencies: any) {
    this.competencyCtrl.setValue(' ')
    // this.competencySearch.nativeElement.blur()

    if (competencies) {
      const value = this.contentForm.controls.competencies.value || []
      const tempObj = {
        id: competencies.id,
        name: competencies.name,
        description: competencies.description,
        competencyType: competencies.additionalProperties.competencyType,
      }
      if (this.canPush(value, tempObj)) {
        value.push(tempObj)
        this.contentForm.controls.competencies.setValue(value)
      }

    }
  }

  addEmployee(event: MatAutocompleteSelectedEvent, field: string) {
    if (event.option.value && event.option.value.id) {
      this.loader.changeLoad.next(true)
      const observable = ['trackContacts', 'publisherDetails'].includes(field) &&
        this.accessService.authoringConfig.doUniqueCheck
        ? this.editorService
          .checkRole(event.option.value.id)
          .pipe(
            map(
              (v: string[]) =>
                v.includes('admin') ||
                v.includes('editor') ||
                (field === 'trackContacts' && v.includes('reviewer')) ||
                (field === 'publisherDetails' && v.includes('publisher')) ||
                (field === 'publisherDetails' && event.option.value.id === this.accessService.userId),
            ),
          )
        : of(true)
      observable.subscribe(
        (data: boolean) => {
          if (data) {
            this.contentForm.controls[field].value.push({
              id: event.option.value.id,
              name: event.option.value.displayName,
            })
            this.contentForm.controls[field].setValue(this.contentForm.controls[field].value)
          } else {
            this.snackBar.openFromComponent(NotificationComponent, {
              data: {
                type: Notify.NO_ROLE,
              },
              duration: NOTIFICATION_TIME * 1000,
            })
          }
          this[`${field}View` as keyof EditMetaComponent].nativeElement.value = ''
          this[`${field}Ctrl` as keyof EditMetaComponent].setValue(null)
        },
        () => {
          this.snackBar.openFromComponent(NotificationComponent, {
            data: {
              type: Notify.FAIL,
            },
            duration: NOTIFICATION_TIME * 1000,
          })
        },
        () => {
          this.loader.changeLoad.next(false)
          this[`${field}View` as keyof EditMetaComponent].nativeElement.value = ''
          this[`${field}Ctrl` as keyof EditMetaComponent].setValue(null)
        },
      )
    }
  }

  removeField(event: MatChipInputEvent) {
    // Reset the input value
    if (event.input) {
      event.input.value = ''
    }
  }

  private fetchAudience() {
    if ((this.audienceCtrl.value || '').trim()) {
      this.audienceList = this.ordinals.audience.filter(
        (v: any) => v.toLowerCase().indexOf(this.audienceCtrl.value.toLowerCase()) > -1,
      )
    } else {
      this.audienceList = this.ordinals.audience.slice()
    }
  }

  private fetchJobProfile() {
    if ((this.jobProfileCtrl.value || '').trim()) {
      this.jobProfileList = this.ordinals.jobProfile.filter(
        (v: any) => v.toLowerCase().indexOf(this.jobProfileCtrl.value.toLowerCase()) > -1,
      )
    } else {
      this.jobProfileList = this.ordinals.jobProfile.slice()
    }
  }

  private fetchRegion() {
    if ((this.regionCtrl.value || '').trim()) {
      this.regionList = this.ordinals.region.filter(
        (v: any) => v.toLowerCase().indexOf(this.regionCtrl.value.toLowerCase()) > -1,
      )
    } else {
      this.regionList = []
    }
  }

  private fetchAccessRestrictions() {
    if (this.accessPathsCtrl.value.trim()) {
      this.accessPathList = this.ordinals.accessPaths.filter((v: any) => v.toLowerCase().
        indexOf(this.accessPathsCtrl.value.toLowerCase()) === 0)
    } else {
      this.accessPathList = this.ordinals.accessPaths.slice()
    }
  }

  checkCondition(meta: string, type: 'show' | 'required' | 'disabled'): boolean {
    if (type === 'disabled' && !this.isEditEnabled) {
      return true
    }
    return this.contentService.checkCondition(this.contentMeta.identifier, meta, type)
  }

  createForm() {
    this.contentForm = this.formBuilder.group({
      accessPaths: [],
      accessibility: [],
      appIcon: [],
      artifactUrl: [],
      audience: [],
      body: [],
      catalogPaths: [],
      category: [],
      categoryType: [],
      certificationList: [],
      certificationUrl: [],
      clients: [],
      complexityLevel: [],
      concepts: [],
      contentIdAtSource: [],
      contentType: [],
      creatorContacts: [],
      customClassifiers: [],
      description: [],
      dimension: [],
      duration: [],
      editors: [],
      equivalentCertifications: [],
      expiryDate: [],
      exclusiveContent: [],
      idealScreenSize: [],
      identifier: [],
      introductoryVideo: [],
      introductoryVideoIcon: [],
      isExternal: [],
      isIframeSupported: [],
      isRejected: [],
      fileType: [],
      jobProfile: [],
      kArtifacts: [],
      keywords: [],
      competencies: [],
      learningMode: [],
      learningObjective: [],
      learningTrack: [],
      license: [],
      locale: [],
      mimeType: [],
      name: [],
      nodeType: [],
      org: [],
      creatorDetails: [],
      passPercentage: [],
      plagScan: [],
      playgroundInstructions: [],
      playgroundResources: [],
      postContents: [],
      posterImage: [],
      preContents: [],
      preRequisites: [],
      projectCode: [],
      publicationId: [],
      publisherDetails: [],
      references: [],
      region: [],
      registrationInstructions: [],
      resourceCategory: [],
      resourceType: [],
      sampleCertificates: [],
      skills: [],
      softwareRequirements: [],
      sourceName: [],
      creatorLogo: [],
      creatorPosterImage: [],
      creatorThumbnail: [],
      status: [],
      studyDuration: [],
      studyMaterials: [],
      subTitle: [],
      subTitles: [],
      systemRequirements: [],
      thumbnail: [],
      trackContacts: [],
      transcoding: [],
      unit: [],
      verifiers: [],
      visibility: [],
      versionKey: '',
    })

    this.contentForm.valueChanges.pipe(debounceTime(700)).subscribe(() => {
      if (this.canUpdate) {
        this.storeData()
      }
    })

    this.contentForm.controls.contentType.valueChanges.subscribe(() => {
      this.changeResourceType()
      this.filterOrdinals()
      this.changeMimeType()
      this.contentForm.controls.category.setValue(this.contentForm.controls.contentType.value)
    })

    if (this.stage === 1) {
      this.contentForm.controls.creatorContacts.valueChanges.subscribe(() => {
        this.contentForm.controls.publisherDetails.setValue(
          this.contentForm.controls.creatorContacts.value || [],
        )
      })
    }
    // resourceType
    this.contentForm.controls.resourceType.valueChanges.subscribe(() => {
      this.contentForm.controls.categoryType.setValue(this.contentForm.controls.resourceType.value)
    })

    this.contentForm.controls.resourceCategory.valueChanges.subscribe(() => {
      this.contentForm.controls.customClassifiers.setValue(
        this.contentForm.controls.resourceCategory.value,
      )
    })
  }
  openCatalogSelector() {
    const oldCatalogs = this.addCommonToCatalog(this.contentForm.controls.catalogPaths.value)
    const dialogRef = this.dialog.open(CatalogSelectComponent, {
      width: '70%',
      maxHeight: '90vh',
      data: JSON.parse(JSON.stringify(oldCatalogs)),
    })
    dialogRef.afterClosed().subscribe((response: string[]) => {
      // const catalogs = this.removeCommonFromCatalog(response)
      this.contentForm.controls.catalogPaths.setValue(response)
    })
  }

  removeSkill(skill: string) {
    const index = this.selectedSkills.indexOf(skill)
    this.selectedSkills.splice(index, 1)
  }

  // removeCatalog(index: number) {
  //   const catalogs = this.contentForm.controls.catalogPaths.value
  //   catalogs.splice(index, 1)
  //   this.contentForm.controls.catalogPaths.setValue(catalogs)
  // }

  // removeCommonFromCatalog(catalogs: string[]): string[] {
  //   const newCatalog: any[] = []
  //   catalogs.forEach(catalog => {
  //     let start = 0
  //     let end = 0
  //     start = catalog.indexOf('>')
  //     end = catalog.length
  //     newCatalog.push(catalog.slice(start + 1, end))
  //   })
  //   return newCatalog
  // }

  copyData(type: 'keyword' | 'previewUrl') {
    const selBox = document.createElement('textarea')
    selBox.style.position = 'fixed'
    selBox.style.left = '0'
    selBox.style.top = '0'
    selBox.style.opacity = '0'
    if (type === 'keyword') {
      selBox.value = this.contentForm.controls.keywords.value
    } else if (type === 'previewUrl') {
      selBox.value =
        // tslint:disable-next-line: max-line-length
        `${window.location.origin}/viewer/${VIEWER_ROUTE_FROM_MIME(
          this.contentForm.controls.mimeType.value,
        )}/${this.contentMeta.identifier}?preview=true`
    }
    document.body.appendChild(selBox)
    selBox.focus()
    selBox.select()
    document.execCommand('copy')
    document.body.removeChild(selBox)
    this.snackBar.openFromComponent(NotificationComponent, {
      data: {
        type: Notify.COPY,
      },
      duration: NOTIFICATION_TIME * 1000,
    })
  }

  addCommonToCatalog(catalogs: string[]): string[] {
    const newCatalog: any[] = []
    catalogs.forEach(catalog => {
      const prefix = 'Common>'
      if (catalog.indexOf(prefix) > -1) {
        newCatalog.push(catalog)
      } else {
        newCatalog.push(prefix.concat(catalog))
      }
    })
    return newCatalog
  }

  onDrop(file: any) {
    const fileName = file.name.replace(/[^A-Za-z0-9.]/g, '')
    if (!fileName.toLowerCase().endsWith('.vtt')) {
      this.snackBar.openFromComponent(NotificationComponent, {
        data: {
          type: Notify.INVALID_FORMAT,
        },
        duration: NOTIFICATION_TIME * 1000,
      })
    } else {
      this.file = file
      // this.getDuration()
      this.upload()
    }
  }

  clearUploadedFile() {
    this.contentForm.controls.subTitles.setValue([])
    this.file = undefined
  }

  upload() {

    this.loader.changeLoad.next(true)
    const formdata = new FormData()
    formdata.append(
      'content',
      this.file as Blob,
      (this.file as File).name.replace(/[^A-Za-z0-9.]/g, ''),
    )
    this.uploadService
      .upload(
        formdata, {
        contentId: this.contentMeta.identifier,
        contentType: CONTENT_BASE_STREAM,
      }).subscribe(vtt => {

        this.loader.changeLoad.next(false)

        this.contentForm.controls.subTitles.setValue([{
          url: vtt.artifactURL,
        }])

      })
  }

}
