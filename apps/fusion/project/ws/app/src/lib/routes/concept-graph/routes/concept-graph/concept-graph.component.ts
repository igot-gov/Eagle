import { Component, OnDestroy, OnInit, ViewEncapsulation } from '@angular/core'
import { ActivatedRoute, Router } from '@angular/router'
import { NsError, ROOT_WIDGET_CONFIG, NSSearch } from '@ws-widget/collection'
import { NsWidgetResolver } from '@ws-widget/resolver'
import { ConfigurationsService, NsPage, ValueService } from '@ws-widget/utils'
import * as d3 from 'd3'
import { drag } from 'd3-drag'
import { forceCenter, forceLink, forceManyBody, forceSimulation } from 'd3-force'
import { zoom } from 'd3-zoom'
import { Subscription } from 'rxjs'
import { SearchServService } from '../../../search/services/search-serv.service'
import { SearchApiService } from '../../../search/apis/search-api.service'
// import { WsSharedValuesService } from '@ws-shared/services'
import { IConceptResult, IGraphData, IGraphDataV2, IGraphRootNode, ILinkedIndex, IRelationChildren } from '../../models/conceptGraph.model'
import { ConceptGraphService } from '../../services/concept-graph.service'
// import { map } from 'rxjs/operators'  contentUnavailable

export interface ISearchRequest {
  filters: {
    [key: string]: string[]
  }
  advancedFilters?: {
    title: string,
    filters: {
      [type: string]: string[]
    }
  }[]
  query: string
  isStandAlone?: boolean
  instanceCatalog?: boolean
  pageNo?: number
  pageSize?: number
  sortBy?: 'lastUpdatedOn'
  sortOrder?: 'ASC' | 'DESC'
  locale?: string[]
}
@Component({
  selector: 'ws-app-concept-graph-graph',
  templateUrl: './concept-graph.component.html',
  styleUrls: ['./concept-graph.component.scss'],
  // tslint:disable-next-line: use-component-view-encapsulation
  encapsulation: ViewEncapsulation.None,
})
export class ConceptGraphComponent implements OnInit, OnDestroy {
  sideNavSubscription: Subscription | null = null
  isLtMedium = false
  sideNavBarOpened = !this.isLtMedium
  errorWidget: NsWidgetResolver.IRenderConfigWithTypedData<NsError.IWidgetErrorResolver> = {
    widgetType: ROOT_WIDGET_CONFIG.errorResolver._type,
    widgetSubType: ROOT_WIDGET_CONFIG.errorResolver.errorResolver,
    widgetData: {
      errorType: 'contentUnavailable',
    },
  }
  pageNavbar: Partial<NsPage.INavBackground> = this.configSvc.pageNavBar
  panelOpenState = true
  graphData = {
    loader: false,
    noId: false,
    name: '',
    id: '',
    id2: '',
    children: [],
    nodeDetails: {
      fetching: false,
      fetchedFor: '',
      program: 0,
      course: 0,
      module: 0,
      resource: 0,
    },
  }
  public windowWidth = 0
  searchTab = {
    proName: 'programs',
    courseName: 'courses',
    moduleName: 'modules',
    resourceName: 'resources',
  }
  searchV2Tab: { [key: string]: string } = {
    programs: 'Learning Path',
    modules: 'Collection',
    courses: 'Course',
    resources: 'Resource',
  }
  displayTable: any = {
    show: 'show',
    hide: 'hide',
  }
  data: IGraphData = {
    nodes: [],
    links: [],
  }
  rootNode: IGraphRootNode = {
    name: '',
    id: 0,
    children: [],
  }
  graphV2Data: IGraphDataV2 = {
    raw: {
      children: [],
      id: 0,
      isRoot: false,
      name: '',
    },
    nodes: [],
    links: [],
  }
  noResultFound = true
  searchRequestObject: ISearchRequest = {} as ISearchRequest
  searchResultsSubscription: Subscription | undefined
  routeSubscription: Subscription | undefined
  public touchDevice = 'ontouchstart' in document.documentElement
  browserName = navigator.appName === 'Netscape' ? 'chrome' : navigator.appName
  defs: d3.Selection<d3.BaseType, { id: string; opacity: number }, d3.BaseType, {}> | undefined
  svg: d3.Selection<d3.BaseType, {}, HTMLElement, any> | undefined
  constructor(
    private conceptServ: ConceptGraphService,
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private valueSvc: ValueService,
    private configSvc: ConfigurationsService,
    private searchServ: SearchServService,
    private searchApiSvc: SearchApiService,
  ) { }

  getActiveLocale() {
    const locale = (this.configSvc.activeLocale && this.configSvc.activeLocale.locals[0]) || ''
    return this.searchServ.getLanguageSearchIndex(locale)
  }

  ngOnInit() {
    this.sideNavSubscription = this.valueSvc.isLtMedium$.subscribe(isLtMedium => {
      this.isLtMedium = isLtMedium
      this.sideNavBarOpened = !this.isLtMedium
    })
    this.routeSubscription = this.activatedRoute.params.subscribe(params => {
      this.graphData.id = params.ids
      if (this.graphData.id !== undefined) {
        this.getTopics(this.graphData.id, 'root')
      } else {
        this.graphData.noId = true
      }
    })
    // this.getTopics('2571', 'root')
  }

  ngOnDestroy() {
    if (this.routeSubscription) {
      this.routeSubscription.unsubscribe()
    }
    if (this.sideNavSubscription) {
      this.sideNavSubscription.unsubscribe()
    }
    if (this.searchResultsSubscription) {
      this.searchResultsSubscription.unsubscribe()
    }
  }

  getTopics(id: string, type: string) {
    try {
      let reqId: string = id
      if (!reqId.length) {
        reqId = '-1'
      }
      // setting loader true.
      this.graphData.nodeDetails.fetching = false
      this.graphData.loader = true

      this.conceptServ.getConcepts(reqId).subscribe(
        (response: IConceptResult[]) => {
          if (response.length > 0) {
            const data = response
            // if type is root set the basic data to graph data obj
            if (type === 'root') {
              this.searchRequestObject.query = data[0].name
              this.searchRequestObject.pageNo = 0
              this.graphData.nodeDetails.fetchedFor = this.searchRequestObject.query
              this.rootNode.id = data[0].id
              this.rootNode.name = data[0].name
              this.rootNode.children = data[0].related
              // form children ids.
              const temp = this.rootNode.children.map(d => {
                return d.id
              })
              const temp2: string = temp.join(',')
              // again call the same function with different param. (Children)
              // new graph
              const tempChild = {
                id: data[0].id,
                name: data[0].name,
                children: data[0].related,
                isRoot: true,
              }
              this.graphV2Data.raw = tempChild
              this.initiateSearchRequest(this.searchRequestObject)
              this.graphData.loader = false
              this.getTopics(temp2, 'children')
            } else {
              const rawGraph = this.graphV2Data.raw.children
              // create key name as childred, delete related.
              const data1: IRelationChildren[] = []
              data.map((current: IConceptResult) => {
                const obj = {
                  name: current.name,
                  children: current.related,
                  id: current.id,
                  score: 0,
                  synonyms: null,
                }
                data1.push(obj)
              })
              // later added
              data1.map((current: IRelationChildren) => {
                rawGraph.map((innerCurrent: IRelationChildren) => {
                  // if ids are same then add to children
                  if (innerCurrent.id === current.id) {
                    innerCurrent.children = current.children
                  }
                })
              })

              const relations = this.conceptServ.findRelations(this.graphV2Data.raw)
              this.graphV2Data.nodes = relations.nodes
              this.graphV2Data.links = relations.links
              this.data = {
                nodes: relations.nodes,
                links: relations.links,
              }
              this.initRelationGraph(this.data)
            }
          } else {
            if (type === 'children') {
              // go to top,and show only first level graph
              // (document.documentElement || document.body.parentNode || document.body).scrollTop = 0;
              const relationsx = this.conceptServ.findRelations(this.graphV2Data.raw)
              this.graphV2Data.nodes = relationsx.nodes
              this.graphV2Data.links = relationsx.links
              this.data = {
                nodes: relationsx.nodes,
                links: relationsx.links,
              }
              this.initRelationGraph(this.data)
            } else {
              this.graphData.loader = false
              d3.select('#conceptGraphV3 g').remove()
              this.graphData.noId = true
            }
          }
        },
        (error: string) => {
          this.graphData.noId = true
          throw error
        },
      )
    } catch (e) {
      throw e
    }
  }

  async initiateSearchRequest(request: ISearchRequest) {
    this.searchRequestObject = request
    const phraseSearch = await this.searchServ.getApplyPhraseSearch()
    if (phraseSearch) {
      this.searchRequestObject.query = `"${this.searchRequestObject.query}"`
    }
    this.searchRequestObject.pageNo = 0
    this.searchRequestObject.locale = [this.getActiveLocale()]
    this.search()
  }

  private search(withQuotes?: boolean) {
    if (withQuotes) {
      this.searchRequestObject.query = this.searchRequestObject.query.replace(/['"]+/g, '')
    }
    const searchReq: NSSearch.ISearchV6Request = {
      query: this.searchRequestObject.query,
      pageNo: this.searchRequestObject.pageNo,
      locale: this.searchRequestObject.locale,
      visibleFilters: {
        contentType: {
          displayName: 'Content Type',
        },
      },
    }
    this.searchResultsSubscription = this.searchApiSvc.getSearchV6Results(searchReq).subscribe(
      async data => {
        // let result: any = data.filters;
        this.graphData.nodeDetails.fetching = true
        if (data.totalHits > 0) {
          this.noResultFound = false
          this.graphData.nodeDetails.program = 0
          this.graphData.nodeDetails.course = 0
          this.graphData.nodeDetails.module = 0
          this.graphData.nodeDetails.resource = 0
          data.filters.forEach((key: any) => {
            if (key.type === 'contentType') {
              key.content.forEach((value: any) => {
                if (value.type === 'Learning Path') {
                  this.graphData.nodeDetails.program = value.count
                } else if (value.type === 'Course') {
                  this.graphData.nodeDetails.course = value.count
                } else if (value.type === 'Collection') {
                  this.graphData.nodeDetails.module = value.count
                } else if (value.type === 'Resource') {
                  this.graphData.nodeDetails.resource = value.count
                }
              })
            }
          })
        } else if (withQuotes === undefined && await this.searchServ.getApplyPhraseSearch()) {
          this.noResultFound = true
          this.search(true)
        }
      },
      (error: string) => {
        throw error
      },
    )
  }

  initRelationGraph(data: IGraphData) {
    try {
      d3.select('#conceptGraphV3 g').remove()
      const graphData = Object.assign(data)
      let width = window.innerWidth - 100
      const height = window.innerHeight - 40
      const linkedByIndex: ILinkedIndex = {}
      let num: any = -70
      const R = 8
      let num2 = 80
      let heightDiv = 2
      let widthDiv = 2.2
      const mobile = this.touchDevice
      if (window.innerWidth <= 650) {
        num2 = 55
        num = -40
        heightDiv = 2.2
        widthDiv = 2.2
        width = window.innerWidth
      } else if (window.innerWidth > 650 && window.innerWidth <= 760) {
        num2 = 70
        num = -40
        width = window.innerWidth - 50
        heightDiv = 2.2
        widthDiv = 2.2
      } else if (window.innerWidth > 760 && window.innerWidth < 1000) {
        num2 = 80
        num = -50
        width = window.innerWidth - 50
      }

      this.svg = d3.select('#conceptGraphV3')
      this.svg.attr('width', width)
      this.svg.attr('height', height)
      const g = this.svg.append('g')
      // g.attr("transform", function() {
      //   // if (data.links.length <= 20) return "translate(0,-150)";
      //   // return "translate(-10,-100)";
      //   return "translate(0,-100)";
      // });

      // is connected
      const isConnected = (a: any, b: any) => {
        return (
          linkedByIndex[`${a.index},${b.index}`] ||
          linkedByIndex[`${b.index},${a.index}`] ||
          a.index === b.index
        )
      }
      const node = g
        .selectAll('.node')
        .data(data.nodes)
        .enter()
        .append('g')
        // .attr("class", "node");
        .attr('class', (d: any) => {
          if (d.node === 'secondLevel' && mobile) {
            return 'node1'
          }
          return 'node'
        })

      const link = g
        .selectAll('line')
        .data(data.links)
        .enter()
        .append('line')
      // fade activity
      const fade = (opacity: number) => {
        // if (mobile) return;
        return (d: any) => {
          if (d.isRoot && mobile) {
            return
          }
          node.style('stroke-opacity', (o: any) => {
            const thisOpacity = isConnected(d, o) ? 1 : opacity
            //   d.setAttribute('fill-opacity', thisOpacity);
            return thisOpacity
          })
          node.style('fill-opacity', (o: any) => {
            const thisOpacity = isConnected(d, o) ? 1 : opacity
            //   that.setAttribute('fill-opacity', thisOpacity);
            return thisOpacity
          })
          link.style('stroke-opacity', (o: any) => {
            return o.source === d || o.target === d ? 1 : opacity
          })

          link.attr('marker-end', (o: any) => {
            return opacity === 1 || o.source === d || o.target === d
              ? 'url(#end-arrow)'
              : 'url(#end-arrow-fade)'
          })
          link.attr('id', (o: any) => {
            return opacity === 1 || o.source === d || o.target === d
              ? 'strokeColorFadeIn'
              : 'strokeColorFadeOut'
          })
        }
      }
      const fade2 = (opacity: number) => {
        if (opacity === 1) {
          {
            node.attr('class', () => {
              return 'node'
            })
            node.selectAll('circle').attr('class', (o: any) => {
              if (o.isRoot) {
                return 'root-node'
              }
              if (o.node === 'firstLevel') {
                return 'first-level-node'
              }
              if (o.node === 'secondLevel') {
                return 'second-level-node'
              }
              return ''
            })
            link.attr('class', () => {
              if (
                this.browserName === 'Netscape' ||
                this.browserName === 'Microsoft Internet Explorer'
              ) {
                return 'IE-links'
              }
              return 'linkss'
            })
            link.attr('marker-end', () => {
              return 'url(#end-arrow)'
            })
          }
        } else {
          node.attr('class', (o: any) => {
            if (o.node === 'secondLevel' && mobile) {
              return 'node1'
            }
            return 'node'
          })
          node.selectAll('circle').attr('class', (o: any) => {
            if (o.isRoot) {
              return 'root-node'
            }
            if (o.node === 'firstLevel') {
              return 'first-level-node'
            }
            if (o.node === 'secondLevel' && !mobile) {
              return 'second-level-node'
            }
            if (o.node === 'secondLevel' && mobile) {
              return 'second-level-node2'
            }
            return ''
          })
          link.attr('class', (o: any) => {
            if (
              (this.browserName === 'Netscape' ||
                this.browserName === 'Microsoft Internet Explorer') &&
              !mobile
            ) {
              return 'IE-links'
            }
            if (!mobile) {
              return 'linkss'
            }
            if (mobile && o.level !== 'second') {
              return 'linkss'
            }
            return ''
          })
          link.attr('marker-end', (o: any) => {
            if (o.level !== 'second') {
              return 'url(#end-arrow)'
            }
            return ''
          })
        }
      }
      const zoomActions = () => {
        g.attr('transform', (d3.event as any).transform)
      }

      const ticked = () => {
        link
          .attr('x1', (d: any) => {
            const dis = Math.sqrt(Math.pow((d.target.x - d.source.x), 2) + Math.pow((d.target.y - d.source.y), 2))
            const dis2 = dis - R
            const ratio = dis2 / dis
            const dx = (d.target.x - d.source.x) * ratio
            return d.target.x - dx
          })
          .attr('y1', (d: any) => {
            const dis = Math.sqrt(Math.pow((d.target.x - d.source.x), 2) + Math.pow((d.target.y - d.source.y), 2))
            const dis2 = dis - R
            const ratio = dis2 / dis
            const dy = (d.target.y - d.source.y) * ratio
            return d.target.y - dy
          })

          .attr('x2', (d: any) => {
            const dis = Math.sqrt(Math.pow((d.target.x - d.source.x), 2) + Math.pow((d.target.y - d.source.y), 2))
            const dis2 = dis - R
            const ratio = dis2 / dis
            const dx = (d.target.x - d.source.x) * ratio
            return d.source.x + dx
          })

          .attr('y2', (d: any) => {
            const dis = Math.sqrt(Math.pow((d.target.x - d.source.x), 2) + Math.pow((d.target.y - d.source.y), 2))
            const dis2 = dis - R
            const ratio = dis2 / dis
            const dy = (d.target.y - d.source.y) * ratio
            return d.source.y + dy
          })
        node.attr('transform', (d: any) => {
          return `translate(${d.x},${d.y})`
        })
      }
      // return;

      const simulation: any = forceSimulation()
        .nodes(graphData.nodes)
        .force(
          'link',
          forceLink().id((d: any) => {
            return d.id
          }),
        )
        .force('charge', forceManyBody().strength(num))
        .force('center', forceCenter(width / widthDiv, height / heightDiv))
        // .force("columnX", d3.forceX(n => n.position * 100 + width / 1.8))
        .on('tick', ticked)

      simulation
        .force('link')
        .links(graphData.links)
        .distance([num2])

      // add defs-marker
      // add defs-markers
      this.defs = g
        .append('svg:defs')
        .selectAll('marker')
        .data([{ id: 'end-arrow', opacity: 1 }, { id: 'end-arrow-fade', opacity: 0.1 }])
        .enter()
        .append('marker')
        .attr('id', d => {
          return d.id
        })
        .attr('viewBox', '0 0 10 10')
        .attr('refX', 10)
        .attr('refY', 5)
        .attr('markerWidth', 8)
        .attr('markerHeight', 8)
        .attr('orient', 'auto')
        .append('g:path')
        .attr('d', 'M0,0 L0,10 L10,5 z')
        .style('opacity', d => {
          return d.opacity
        })
      link
        .attr('class', d => {
          if (
            (this.browserName === 'Netscape' ||
              this.browserName === 'Microsoft Internet Explorer') &&
            !mobile
          ) {
            return 'IE-links'
          }
          if (!mobile) {
            return 'linkss'
          }
          if (mobile && d.level !== 'second') {
            return 'linkss'
          }
          return ''
        })
        .attr('marker-end', (d: any) => {
          if (d.level !== 'second') {
            return 'url(#end-arrow)'
          }
          return ''
        })
        .attr('data-links', (d: any) => {
          return `${d.source.name}--${d.target.name}`
        })

      node
        .append('circle')
        .attr('r', R)
        .attr('class', d => {
          if (d.isRoot) {
            return 'root-node'
          }
          if (d.node === 'firstLevel') {
            return 'first-level-node'
          }
          if (d.node === 'secondLevel' && !mobile) {
            return 'second-level-node'
          }
          if (d.node === 'secondLevel' && mobile) {
            return 'second-level-node2'
          }
          return ''
        })
        .on('mouseover', fade(1))
        .on('mouseout', fade(1))
        .on('click', (d: any) => {
          if (!this.graphData.loader && !this.touchDevice) {
            if (d.isRoot) {
              const queryParams: any = {}
              queryParams.q = this.searchRequestObject.query
              this.router.navigate(['/app/search/learning'], { queryParams })
            } else {
              d3.select('#conceptGraphV3 g').remove()
              // this.getTopics(d.id, 'root')
              this.router.navigate([`${d.id}`], { relativeTo: this.activatedRoute.parent })
            }
          } else {
            if (d.count === 0 && d.isRoot) {
              d.count += 1
              fade2(1)
            } else if (d.count === 1 && d.isRoot) {
              d.count = 0
              fade2(0)
            } else {
              d3.select('#conceptGraphV3 g').remove()
              // this.getTopics(d.id, 'root')
              this.router.navigate([`${d.id}`], { relativeTo: this.activatedRoute.parent })
            }
          }
        })
        .call(
          // d3
          drag<any, any>()
            .on('start', d => {
              if (!(d3.event as any).active) {
                simulation.alphaTarget(0.8).restart()
              }
              d.fx = d.x
              d.fy = d.y
            })
            .on('drag', d => {
              d.fx = (d3.event as any).x
              d.fy = (d3.event as any).y
            })
            .on('end', d => {
              if (!(d3.event as any).active) {
                simulation.alphaTarget(0.1)
              }
              d.fx = null
              d.fy = null
            }),
        )
      node.append('title').text((d: any) => {
        // return d.name+" "+d.id;
        return d.name
      })
      node
        .append('text')
        .attr('x', 0)
        .attr('dy', '-1em')
        .text(d => {
          return d.name
        })
        .style('fill', d => {
          if (d.node === 'secondLevel') {
            return '#999'
          }
          return ''
        })
        .on('mouseover', fade(0.1))
        .on('mouseout', fade(1))

      const zoomHandler = zoom<any, any>().on('zoom', zoomActions)
      zoomHandler(this.svg)

      data.links.forEach((d: any) => {
        return (linkedByIndex[`${d.source.index},${d.target.index}`] = 1)
      })

      this.graphData.loader = false
      this.windowWidth = window.innerWidth
    } catch (e) {
      throw e
    }
  }
  onResize() {
    this.touchDevice = 'ontouchstart' in document.documentElement
    if (this.data !== undefined) {
      this.initRelationGraph(this.data)
    }
  }
  backToSearch(tab: string) {
    try {
      const queryParams = {
        q: '',
        f: '',
      }
      queryParams.q = this.graphData.nodeDetails.fetchedFor
      queryParams.f = JSON.stringify({
        contentType: [`${this.searchV2Tab[tab]}`],
      })
      this.router.navigate(['app/search/learning'], { queryParams })
    } catch (e) {
      throw e
    }
  }
}
