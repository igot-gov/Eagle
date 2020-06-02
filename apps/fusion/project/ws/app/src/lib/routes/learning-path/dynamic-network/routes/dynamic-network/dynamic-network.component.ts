import { Component, OnInit, ElementRef, ViewChild } from '@angular/core'
import { Network, Node, Edge, Options } from 'vis-network'
import { dynamicData, salesData } from '../../utils/dynamic-network.data'
import { INodeData } from '../../../career-development-path/models/career-development-path.model'
import { ActivatedRoute, Router } from '@angular/router'
import { MatSnackBar } from '@angular/material'
import { NsPage, ConfigurationsService } from '@ws-widget/utils'
import {
  IDynamicDemoNew,
  IDlpBreadCrumb,
  IWidgetData,
  ITitleData,
} from '../../models/dynamicNetwork.model'
import { DynamicLearningSrvService } from '../../services/dynamic-learning-srv.service'
import { NsContent } from '@ws-widget/collection'
@Component({
  selector: 'ws-app-dynamic-network',
  templateUrl: './dynamic-network.component.html',
  styleUrls: ['./dynamic-network.component.scss'],
})
export class DynamicNetworkComponent implements OnInit {
  error = false
  network: Network = {} as Network
  data = 'Sales'
  subNode = 0
  sessionData: any
  grandChildObj: INodeData[] = []
  newChildren: INodeData[] = salesData['Value Selling']
  childName = ''
  showCards = false
  dynamicData = false
  graphData: any
  displayData: { [key: string]: IDynamicDemoNew } = {}
  breadCrumb: IDlpBreadCrumb[] | any | null = []
  options: Options = {}
  dataSet: { nodes: Node[]; edges: Edge[] } = { nodes: [], edges: [] }
  content: NsContent.IContent = {} as NsContent.IContent
  loader = false
  titleName = ''
  selectedNode: string | undefined = ''
  nodeId = 0
  container: HTMLElement = {} as HTMLElement
  pageNavbar: Partial<NsPage.INavBackground> = this.configSvc.pageNavBar
  widget: IWidgetData = {} as IWidgetData
  @ViewChild('contentCards', { static: true }) myElement: ElementRef = {} as ElementRef
  constructor(
    private activatedRoute: ActivatedRoute,
    private route: Router,
    private configSvc: ConfigurationsService,
    public matSnackBar: MatSnackBar,
    private dynamicSrv: DynamicLearningSrvService,
  ) { }

  ngOnInit() {
    this.activatedRoute.params.subscribe(params => {
      this.data = params.child
      this.graphData = this.activatedRoute.snapshot.data.pageData.data[this.data];
      (this.titleName = this.activatedRoute.snapshot.data.pageData.data.lexIdData.filter(
        (obj: ITitleData) => obj.titleKey === params.child.trim(),
      )[0].displayName),
        (this.subNode = parseInt(params.subNode, 10) || 0)
      // this.breadCrumb = []
      this.getContent(params.child)
      this.generateNode()
    })
  }

  generateNode() {
    this.container = document.getElementById('vizDemo') || ({} as HTMLElement)
    this.dataSet = {
      nodes: this.graphData[this.subNode].nodes || [],
      edges: this.graphData[this.subNode].edges || [],
    }
    const groupSales = {
      third: {
        size: 30,
        shape: 'dot',
        color: '#8d38d6',
        font: {
          color: 'gray',
        },
      },
      fourth: {
        size: 20,
        shape: 'dot',
        color: '#474747',
        font: {
          color: 'gray',
        },
      },
      fifth: {
        size: 10,
        shape: 'dot',
        color: '#57c2b6',
        font: {
          color: 'gray',
        },
      },
    }
    const groupOther = {
      digiGen: {
        size: 30,
        shape: 'dot',
        color: 'lightgreen',
        font: {
          color: 'gray',
        },
      },
      digiTek: {
        size: 30,
        shape: 'dot',
        color: 'blue',
        font: {
          color: 'gray',
        },
      },
      digiMgm: {
        size: 30,
        shape: 'dot',
        color: 'red',
        font: {
          color: 'gray',
        },
      },
      digiBui: {
        size: 30,
        shape: 'dot',
        color: 'gray',
        font: {
          color: 'gray',
        },
      },
      second: {
        size: 30,
        shape: 'dot',
        color: '#8d38d6',
        font: {
          color: 'gray',
        },
      },
      third: {
        size: 20,
        shape: 'dot',
        color: '#474747',
        font: {
          color: 'gray',
        },
      },
      fourth: {
        size: 10,
        shape: 'dot',
        color: '#57c2b6',
        font: {
          color: 'gray',
        },
      },
      fifth: {
        size: 5,
        shape: 'dot',
        color: '#57c2b6',
        font: {
          color: 'gray',
        },
      },
    }
    this.options = {
      autoResize: true,
      interaction: {
        hover: true,
      },
      nodes: {
        shape: 'circle',
        borderWidth: 4,
        font: {
          color: 'gray',
        },
      },
      edges: {
        width: 1,
        color: 'lightgray',
      },
      physics: {},
      groups: this.data === 'Sales' ? groupSales : groupOther,
    }
    // this.sessionData = sessionStorage.getItem('Bread_Crumb')
    // if (this.sessionData === undefined || this.sessionData === null || this.sessionData.length === 2) {
    //   // Do nothing
    // } else {
    //   this.breadCrumb = JSON.parse(this.sessionData)
    // }
    // if (this.data === 'lex_auth_01285522520727552015') {
    //   if (this.breadCrumb.length === 2) {
    //     this.breadCrumb.forEach((crumb: any) => {
    //       if (crumb.title === 'lex_auth_01285522270547148814') {
    //         this.breadCrumb.splice(0, 1)
    //       }
    //     })
    //   }
    // }
    if (this.data === 'lex_auth_01285522520727552015') {
      if (this.breadCrumb.length) {
        if (this.breadCrumb[this.breadCrumb.length - 1].title === 'lex_auth_01285522270547148814') {
          this.breadCrumb.pop()
        }
      }
    }
    // this.breadCrumb.forEach((node: any) => {
    const nodeObj = { title: this.data, displayName: this.titleName, link: '', id: 0 }
    if (this.breadCrumb.indexOf(nodeObj) === -1) {
      this.breadCrumb.push(nodeObj)
      if (this.breadCrumb.length >= 2) {
        this.breadCrumb.pop()
      }
    }
    // })

    this.network = new Network(this.container, this.dataSet, this.options)
    this.network.on('click', params => {
      this.loader = true
      this.dynamicData = false
      this.nodeId = params.nodes[0]
      if (params.nodes.length !== 0) {
        const dataTemp = Object.assign(this.dataSet)
        if (this.data) {
          dataTemp.nodes.map((cur: Node, i: number) => {
            const id = cur.id as number
            if (id && id === params.nodes[0]) {
              if (cur.label !== 'New Work') {
                if (
                  dynamicData[this.data][id] &&
                  dynamicData[this.data][id].nodes &&
                  !dynamicData[this.data][id].opened
                ) {
                  this.dataSet.nodes = [...(dynamicData[this.data][id].nodes || [])]
                  this.dataSet.edges = [...(dynamicData[this.data][id].edges || [])]
                  // this.childName = this.dataSet.nodes[0].label || ''
                  // this.childName = this.childName.replace(/\n/g, ' ')
                  this.showCards = false
                  this.loader = false
                  this.network.setData({ nodes: this.dataSet.nodes, edges: this.dataSet.edges })
                  this.route.navigate([`/app/learning-journey/dlp/`, this.data, this.nodeId])
                  const route: IDlpBreadCrumb = {
                    title: cur.label || '',
                    displayName: cur.label || '',
                    id: cur.id as number,
                    link: '',
                  }
                  this.selectedNode = cur.label
                  const breadData: IDlpBreadCrumb[] = Object.assign(this.breadCrumb)
                  let count = 0
                  breadData.forEach((curnt: IDlpBreadCrumb) => {
                    if (curnt.id === cur.id) {
                      count += 1
                    }
                  })
                  if (count === 0) {
                    this.breadCrumb.push(route)
                    // localStorage.setItem('Bread_Crumb', JSON.stringify(this.breadCrumb))
                  }
                } else if (
                  dynamicData[this.data][id] === undefined ||
                  !dynamicData[this.data][id].opened
                ) {
                  this.childName = this.dataSet.nodes[i].label || ''
                  this.childName = this.childName.replace(/\n/g, ' ')
                  this.showCards = true
                  // this.grandChildObj = nodeLevelData['Selling Methods/Approaches']
                  // if (!this.activatedRoute.snapshot.data.pageData.data.widgetData[this.childName]) {
                  let flag = true
                  this.graphData.forEach(
                    (obj1: { edges: { forEach: (arg0: (obj2: any) => void) => void } }) => {
                      if (obj1.edges) {
                        obj1.edges.forEach(obj2 => {
                          if (obj2.from === this.nodeId) {
                            flag = false
                          }
                        })
                      }
                    },
                  )
                  if (!flag) {
                    this.route.navigate([`/app/learning-journey/dlp/`, this.data, this.nodeId])
                  } else {
                    const isDataAvailable = this.activatedRoute.snapshot.data.pageData.data
                      .widgetData[this.childName]
                    if (isDataAvailable) {
                      // this.route.navigate([`/app/learning-journey/dlp/`, this.data, this.nodeId])
                    } else {
                      this.loader = false
                      this.error = true
                    }
                  }
                  // }
                  const widget = {
                    widgetData: {
                      strips: [
                        {
                          key: 'DLP',
                          title: '',
                          request: {
                            ids:
                              this.activatedRoute.snapshot.data.pageData.data.widgetData[this.childName] || [],
                          },
                        },
                      ],
                      loader: true,
                    },
                    widgetSubType: 'contentStripMultiple',
                    widgetType: 'contentStrip',
                    widgetHostClass: '',
                  }
                  this.widget = widget
                  // 'Business Modelling based on BizMo TM (BizMo)'
                  this.loader = false
                  this.dynamicData = true
                  if (this.myElement) {
                    setTimeout(
                      () => {
                        this.myElement.nativeElement.scrollIntoView({
                          behavior: 'smooth',
                          block: 'start',
                        })
                      },
                      0,
                    )
                  }
                }
              } else {
                this.route.navigate([`/app/learning-journey/dlp/lex_auth_01285522520727552015/0`], {
                  relativeTo: this.activatedRoute.parent,
                })
              }
            }
          })
        }
      } else {
        this.showCards = false
        this.loader = false
      }
    })
  }

  goToRoute(obj: IDlpBreadCrumb) {
    this.showCards = false
    if (obj.title === 'Learning Path') {
      this.route.navigate([obj.link])
    } else {
      const breadValue: IDlpBreadCrumb[] = []
      this.breadCrumb.forEach((cur: IDlpBreadCrumb) => {
        if (cur.id <= obj.id) {
          if (breadValue.indexOf(cur) === -1) {
            breadValue.push(cur)
          }
        }
      })
      this.breadCrumb = breadValue
      this.childName = obj.title || ''
      this.route.navigate([`/app/learning-journey/dlp/`, this.data, obj.id])
      // this.dataSet.nodes = [...(dynamicData[this.data][obj.id].nodes || [])]
      // this.dataSet.edges = [...(dynamicData[this.data][obj.id].edges || [])]
      // this.network.setData({ nodes: this.dataSet.nodes, edges: this.dataSet.edges })
    }
  }

  collapseOrExpand(type: string) {
    try {
      this.loader = true
      this.network.setData({ nodes: [], edges: [] })
      if (type === 'collapse') {
        this.dataSet.nodes = dynamicData[this.data][0].nodes || []
        this.dataSet.edges = dynamicData[this.data][0].edges || []
        this.network.setData({ nodes: this.dataSet.nodes, edges: this.dataSet.edges })
        this.loader = false
      } else {
        let nodes: Node[] = []
        let edges: Edge[] = []
        dynamicData[this.data].forEach(
          (cur: { nodes?: Node[]; edges?: Edge[]; opened?: boolean }, i: number) => {
            if (cur.nodes && cur.edges && i !== 0) {
              nodes = [...nodes, ...cur.nodes.slice(1, cur.nodes.length)]
              edges = [...edges, ...cur.edges]
            } else if (cur.nodes && cur.edges) {
              nodes = [...nodes, ...cur.nodes]
              edges = [...edges, ...cur.edges]
            }
          },
        )
        this.loader = false
        this.dataSet.nodes = nodes
        this.dataSet.edges = edges
        this.network.setData({ nodes: this.dataSet.nodes, edges: this.dataSet.edges })
      }
    } catch (e) {
      throw e
    }
  }
  openSnackBar() {
    this.matSnackBar.open('Coming Soon...', 'close', {
      duration: 2000,
    })
  }
  getContent(contentId: string) {
    try {
      this.dynamicSrv.getContent(contentId).subscribe(
        data => {
          this.content = data
          this.displayData = this.activatedRoute.snapshot.data.pageData.data.displayData
        },
        error => {
          throw error
        },
      )
    } catch (e) {
      throw e
    }
  }
}
