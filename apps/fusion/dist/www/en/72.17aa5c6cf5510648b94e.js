(window.webpackJsonp=window.webpackJsonp||[]).push([[72],{"7ckY":function(l,n,t){"use strict";t.r(n);var u=t("8Y7J");class a{}var e=t("pMnS"),i=t("iInd"),c=t("SVse"),o=t("rWV4"),r=t("/HVE"),b=t("Xd0L"),s=t("5GAg"),m=t("omvX"),p=t("Rlre"),g=t("IP0z"),f=t("hOhj"),h=t("FbN9"),d=t("BzsH"),v=t("ggGX"),k=t("rX66"),w=t("xQS/"),x=t("88ff");t("g6cB");class D{constructor(l,n){this.route=l,this.configSvc=n,this.channelsConfig=null,this.errorFetchingJson=!1,this.selectedIndex=0,this.tabs=[],this.tabData=null}ngOnInit(){this.route.data.subscribe(l=>{l.channelsData.data?(this.channelsConfig=l.channelsData.data,this.getDetails()):l.channelsData.error&&(this.errorFetchingJson=!0)})}getDetails(){const l=this.channelsConfig;l&&(this.tabs=l.tabs.map(l=>l.tabDetails),this.route.paramMap.subscribe(n=>{const t=n.get("tab")?n.get("tab"):this.tabs[0].key;if(t){const n=l.tabs.map(l=>l.tabDetails.key).indexOf(t);this.tabData=l.tabs[n].tabContent}}))}}var O=u.sb({encapsulation:0,styles:[[""]],data:{}});function G(l){return u.Qb(0,[(l()(),u.ub(0,0,null,null,6,"a",[["class","mat-tab-link"],["mat-tab-link",""],["routerLinkActive",""]],[[1,"target",0],[8,"href",4],[1,"aria-current",0],[1,"aria-disabled",0],[1,"tabIndex",0],[2,"mat-tab-disabled",null],[2,"mat-tab-label-active",null]],[[null,"click"]],(function(l,n,t){var a=!0;return"click"===n&&(a=!1!==u.Gb(l,1).onClick(t.button,t.ctrlKey,t.metaKey,t.shiftKey)&&a),a}),null,null)),u.tb(1,671744,[[4,4]],0,i.r,[i.o,i.a,c.l],{routerLink:[0,"routerLink"]},null),u.tb(2,1720320,[["rla",4]],2,i.q,[i.o,u.k,u.D,[2,i.p],[2,i.r]],{routerLinkActive:[0,"routerLinkActive"]},null),u.Mb(603979776,3,{links:1}),u.Mb(603979776,4,{linksWithHrefs:1}),u.tb(5,147456,[[2,4]],0,o.k,[o.l,u.k,u.y,r.a,[2,b.k],[8,null],s.h,[2,m.a]],{active:[0,"active"]},null),(l()(),u.Ob(6,null,[" "," "]))],(function(l,n){l(n,1,0,n.context.$implicit.routerLink),l(n,2,0,""),l(n,5,0,u.Gb(n,2).isActive)}),(function(l,n){l(n,0,0,u.Gb(n,1).target,u.Gb(n,1).href,u.Gb(n,5).active?"page":null,u.Gb(n,5).disabled,u.Gb(n,5).tabIndex,u.Gb(n,5).disabled,u.Gb(n,5).active),l(n,6,0,n.context.$implicit.name)}))}function y(l){return u.Qb(0,[(l()(),u.ub(0,0,null,null,4,"nav",[["class","mat-tab-nav-bar mat-tab-header"],["mat-align-tabs","center"],["mat-tab-nav-bar",""]],[[2,"mat-tab-header-pagination-controls-enabled",null],[2,"mat-tab-header-rtl",null],[2,"mat-primary",null],[2,"mat-accent",null],[2,"mat-warn",null]],null,null,p.e,p.c)),u.tb(1,7520256,null,1,o.l,[u.k,[2,g.b],u.y,u.h,f.e,[2,r.a],[2,m.a]],null,null),u.Mb(603979776,2,{_items:1}),(l()(),u.kb(16777216,null,0,1,null,G)),u.tb(4,278528,null,0,c.o,[u.P,u.M,u.r],{ngForOf:[0,"ngForOf"]},null)],(function(l,n){l(n,4,0,n.component.tabs)}),(function(l,n){l(n,0,0,u.Gb(n,1)._showPaginationControls,"rtl"==u.Gb(n,1)._getLayoutDirection(),"warn"!==u.Gb(n,1).color&&"accent"!==u.Gb(n,1).color,"accent"===u.Gb(n,1).color,"warn"===u.Gb(n,1).color)}))}function I(l){return u.Qb(0,[(l()(),u.ub(0,0,null,null,9,"mat-toolbar",[["class","ws-mat-light-background mat-toolbar"]],[[2,"mat-toolbar-multiple-rows",null],[2,"mat-toolbar-single-row",null]],null,null,h.b,h.a)),u.Lb(512,null,c.K,c.L,[u.k,u.s,u.D]),u.tb(2,278528,null,0,c.u,[c.K],{ngStyle:[0,"ngStyle"]},null),u.tb(3,4243456,null,1,d.a,[u.k,r.a,c.d],null,null),u.Mb(603979776,1,{_toolbarRows:1}),(l()(),u.ub(5,0,null,0,2,"ws-widget-btn-page-back",[],[[8,"id",0],[8,"style",2],[8,"className",0]],null,null,v.c,v.b)),u.tb(6,4308992,null,0,k.a,[w.a,i.o,x.a],{widgetData:[0,"widgetData"]},null),u.Jb(7,{url:0}),(l()(),u.ub(8,0,null,0,1,"span",[["class","margin-left-s"]],null,null,null,null,null)),(l()(),u.Ob(-1,null,["Channels"])),(l()(),u.kb(16777216,null,null,1,null,y)),u.tb(11,16384,null,0,c.p,[u.P,u.M],{ngIf:[0,"ngIf"]},null),(l()(),u.ub(12,16777216,null,null,1,"router-outlet",[],null,null,null,null,null)),u.tb(13,212992,null,0,i.t,[i.b,u.P,u.j,[8,null],u.h],null,null)],(function(l,n){var t=n.component;l(n,2,0,null==t.configSvc||null==t.configSvc.pageNavBar?null:t.configSvc.pageNavBar.styles);var u=l(n,7,0,"/page/home");l(n,6,0,u),l(n,11,0,null==t.channelsConfig?null:t.channelsConfig.tabs),l(n,13,0)}),(function(l,n){l(n,0,0,u.Gb(n,3)._toolbarRows.length>0,0===u.Gb(n,3)._toolbarRows.length),l(n,5,0,u.Gb(n,6).id,u.Gb(n,6).widgetSafeStyle,u.Gb(n,6).className)}))}function M(l){return u.Qb(0,[(l()(),u.ub(0,0,null,null,1,"ws-app-channels-home",[],null,null,null,I,O)),u.tb(1,114688,null,0,D,[i.a,x.a],null,null)],(function(l,n){l(n,1,0)}),null)}var C=u.qb("ws-app-channels-home",D,M,{},{},[]),P=t("kToz"),$=t("JUD0");class Q{constructor(l){this.http=l,this.baseUrl="assets/configurations/"+location.host.replace(":","_")}ngOnInit(){this.http.get(this.baseUrl+"/feature/channel-hubs.json").subscribe(l=>{this.pageData=l})}}var E=t("IheW"),S=u.sb({encapsulation:0,styles:[[""]],data:{}});function L(l){return u.Qb(0,[(l()(),u.ub(0,0,null,null,2,"div",[["class","w-full sm:w-1/2 lg:w-1/3"]],null,null,null,null,null)),(l()(),u.ub(1,0,null,null,1,"ws-widget-card-channel",[],[[8,"id",0],[8,"style",2],[8,"className",0]],null,null,P.c,P.b)),u.tb(2,4308992,null,0,$.a,[],{widgetData:[0,"widgetData"]},null)],(function(l,n){l(n,2,0,n.context.$implicit)}),(function(l,n){l(n,1,0,u.Gb(n,2).widgetInstanceId,u.Gb(n,2).widgetSafeStyle,u.Gb(n,2).className)}))}function T(l){return u.Qb(0,[(l()(),u.ub(0,0,null,null,5,"section",[["class","max-w-5xl m-auto p-8 mb-8"]],null,null,null,null,null)),(l()(),u.ub(1,0,null,null,1,"h1",[["class","mat-h1"]],null,null,null,null,null)),(l()(),u.Ob(2,null,[" "," "])),(l()(),u.ub(3,0,null,null,2,"section",[["class","flex flex-wrap -mr-8"]],null,null,null,null,null)),(l()(),u.kb(16777216,null,null,1,null,L)),u.tb(5,278528,null,0,c.o,[u.P,u.M,u.r],{ngForOf:[0,"ngForOf"]},null)],(function(l,n){l(n,5,0,null==n.context.$implicit?null:n.context.$implicit.channels)}),(function(l,n){l(n,2,0,null==n.context.$implicit?null:n.context.$implicit.name)}))}function _(l){return u.Qb(0,[(l()(),u.kb(16777216,null,null,1,null,T)),u.tb(1,278528,null,0,c.o,[u.P,u.M,u.r],{ngForOf:[0,"ngForOf"]},null)],(function(l,n){l(n,1,0,n.component.pageData)}),null)}class F{constructor(){this.leaderData=null}ngOnInit(){}}var K=u.sb({encapsulation:0,styles:[["img[_ngcontent-%COMP%]{width:11em;height:11em;border-radius:50%;margin-top:12px}.card[_ngcontent-%COMP%]{width:14em}"]],data:{}});function N(l){return u.Qb(0,[(l()(),u.ub(0,0,null,null,7,"a",[["class","mat-card card margin-m"]],[[1,"target",0],[8,"href",4]],[[null,"click"]],(function(l,n,t){var a=!0;return"click"===n&&(a=!1!==u.Gb(l,1).onClick(t.button,t.ctrlKey,t.metaKey,t.shiftKey)&&a),a}),null,null)),u.tb(1,671744,null,0,i.r,[i.o,i.a,c.l],{routerLink:[0,"routerLink"]},null),(l()(),u.ub(2,0,null,null,0,"img",[["alt","Leader"],["class","mat-lite-background"]],[[8,"src",4]],null,null,null,null)),(l()(),u.ub(3,0,null,null,4,"div",[["class","text-center"]],null,null,null,null,null)),(l()(),u.ub(4,0,null,null,1,"h2",[["class","margin-remove mat-h2 margin-top-xs"]],null,null,null,null,null)),(l()(),u.Ob(5,null,["",""])),(l()(),u.ub(6,0,null,null,1,"h3",[["class","mat-h3"]],null,null,null,null,null)),(l()(),u.Ob(7,null,["",""]))],(function(l,n){l(n,1,0,n.context.$implicit.routerLink)}),(function(l,n){l(n,0,0,u.Gb(n,1).target,u.Gb(n,1).href),l(n,2,0,null==n.context.$implicit?null:n.context.$implicit.profileImage),l(n,5,0,null==n.context.$implicit?null:n.context.$implicit.name),l(n,7,0,null==n.context.$implicit?null:n.context.$implicit.designation)}))}function J(l){return u.Qb(0,[(l()(),u.ub(0,0,null,null,2,"div",[["class","text-center flex flex-wrapped margin-top-m flex-around"]],null,null,null,null,null)),(l()(),u.kb(16777216,null,null,1,null,N)),u.tb(2,278528,null,0,c.o,[u.P,u.M,u.r],{ngForOf:[0,"ngForOf"]},null)],(function(l,n){l(n,2,0,n.component.leaderData)}),null)}function z(l){return u.Qb(0,[(l()(),u.ub(0,0,null,null,1,"h2",[["class","mat-h2"]],null,null,null,null,null)),(l()(),u.Ob(-1,null,[" Leaders details will appear here "]))],null,null)}function A(l){return u.Qb(0,[(l()(),u.kb(16777216,null,null,1,null,J)),u.tb(1,16384,null,0,c.p,[u.P,u.M],{ngIf:[0,"ngIf"]},null),(l()(),u.ub(2,0,null,null,2,"div",[["class","text-center margin-top-m"]],null,null,null,null,null)),(l()(),u.kb(16777216,null,null,1,null,z)),u.tb(4,16384,null,0,c.p,[u.P,u.M],{ngIf:[0,"ngIf"]},null)],(function(l,n){var t=n.component;l(n,1,0,null==t.leaderData?null:t.leaderData.length),l(n,4,0,!(null!=t.leaderData&&t.leaderData.length))}),null)}var j=t("lzlj"),q=t("igqZ"),R=t("Mr+X"),X=t("Gi4r");class B{constructor(){this.initiativesData=null}ngOnInit(){}}var U=u.sb({encapsulation:0,styles:[[".icon[_ngcontent-%COMP%]{height:1em;width:1em;font-size:4em}.card[_ngcontent-%COMP%]{width:15em;min-height:17em}.title[_ngcontent-%COMP%]{line-height:1.2;height:2.4em}"]],data:{}});function V(l){return u.Qb(0,[(l()(),u.ub(0,0,null,null,1,"h2",[["class","mat-h2 title font-weight-bold margin-top-l"]],null,null,null,null,null)),(l()(),u.Ob(1,null,[" "," "]))],null,(function(l,n){l(n,1,0,null==n.parent.context.$implicit?null:n.parent.context.$implicit.title)}))}function W(l){return u.Qb(0,[(l()(),u.ub(0,0,null,null,1,"div",[["class","margin-top-l"]],null,null,null,null,null)),(l()(),u.Ob(1,null,["",""]))],null,(function(l,n){l(n,1,0,null==n.parent.context.$implicit?null:n.parent.context.$implicit.desc)}))}function H(l){return u.Qb(0,[(l()(),u.ub(0,0,null,null,10,"a",[["class","margin-m"]],[[1,"target",0],[8,"href",4]],[[null,"click"]],(function(l,n,t){var a=!0;return"click"===n&&(a=!1!==u.Gb(l,1).onClick(t.button,t.ctrlKey,t.metaKey,t.shiftKey)&&a),a}),null,null)),u.tb(1,671744,null,0,i.r,[i.o,i.a,c.l],{routerLink:[0,"routerLink"]},null),(l()(),u.ub(2,0,null,null,8,"mat-card",[["class","card cursor-pointer mat-card"]],[[2,"_mat-animation-noopable",null]],null,null,j.d,j.a)),u.tb(3,49152,null,0,q.a,[[2,m.a]],null,null),(l()(),u.ub(4,0,null,0,2,"mat-icon",[["class","icon mat-icon notranslate"],["role","img"]],[[2,"mat-icon-inline",null],[2,"mat-icon-no-color",null]],null,null,R.b,R.a)),u.tb(5,9158656,null,0,X.b,[u.k,X.d,[8,null],[2,X.a]],null,null),(l()(),u.Ob(6,0,["",""])),(l()(),u.kb(16777216,null,0,1,null,V)),u.tb(8,16384,null,0,c.p,[u.P,u.M],{ngIf:[0,"ngIf"]},null),(l()(),u.kb(16777216,null,0,1,null,W)),u.tb(10,16384,null,0,c.p,[u.P,u.M],{ngIf:[0,"ngIf"]},null)],(function(l,n){l(n,1,0,null==n.context.$implicit?null:n.context.$implicit.routerLink),l(n,5,0),l(n,8,0,null==n.context.$implicit?null:n.context.$implicit.title),l(n,10,0,n.context.$implicit.desc)}),(function(l,n){l(n,0,0,u.Gb(n,1).target,u.Gb(n,1).href),l(n,2,0,"NoopAnimations"===u.Gb(n,3)._animationMode),l(n,4,0,u.Gb(n,5).inline,"primary"!==u.Gb(n,5).color&&"accent"!==u.Gb(n,5).color&&"warn"!==u.Gb(n,5).color),l(n,6,0,null==n.context.$implicit?null:n.context.$implicit.iconName)}))}function Y(l){return u.Qb(0,[(l()(),u.ub(0,0,null,null,2,"div",[["class","text-center margin-top-m flex flex-wrapped flex-around"]],null,null,null,null,null)),(l()(),u.kb(16777216,null,null,1,null,H)),u.tb(2,278528,null,0,c.o,[u.P,u.M,u.r],{ngForOf:[0,"ngForOf"]},null)],(function(l,n){l(n,2,0,n.component.initiativesData)}),null)}function Z(l){return u.Qb(0,[(l()(),u.kb(16777216,null,null,1,null,Y)),u.tb(1,16384,null,0,c.p,[u.P,u.M],{ngIf:[0,"ngIf"]},null)],(function(l,n){var t=n.component;l(n,1,0,t.initiativesData&&(null==t.initiativesData?null:t.initiativesData.length))}),null)}class ll{constructor(){this.eventsData=null}ngOnInit(){}isCurrentTimeSmall(l){return new Date<new Date(l)}}var nl=u.sb({encapsulation:0,styles:[[".event[_ngcontent-%COMP%]{width:14em}.event-icon[_ngcontent-%COMP%]{width:1em;height:1em;font-size:3em;margin-right:16px}@media only screen and (min-width:600px){.event-icon[_ngcontent-%COMP%]{margin-right:0}}.event-name[_ngcontent-%COMP%]{line-height:1.2;height:2.4em}"]],data:{}});function tl(l){return u.Qb(0,[(l()(),u.ub(0,0,null,null,1,"h2",[["class","mat-h2 event-name font-weight-bold margin-top-l"]],null,null,null,null,null)),(l()(),u.Ob(1,null,[" "," "]))],null,(function(l,n){l(n,1,0,null==n.parent.context.$implicit?null:n.parent.context.$implicit.eventName)}))}function ul(l){return u.Qb(0,[(l()(),u.ub(0,0,null,null,15,"a",[["class","margin-m"]],[[1,"target",0],[8,"href",4]],[[null,"click"]],(function(l,n,t){var a=!0;return"click"===n&&(a=!1!==u.Gb(l,1).onClick(t.button,t.ctrlKey,t.metaKey,t.shiftKey)&&a),a}),null,null)),u.tb(1,671744,null,0,i.r,[i.o,i.a,c.l],{routerLink:[0,"routerLink"]},null),(l()(),u.ub(2,0,null,null,13,"mat-card",[["class","event cursor-pointer mat-card"]],[[2,"_mat-animation-noopable",null]],null,null,j.d,j.a)),u.tb(3,49152,null,0,q.a,[[2,m.a]],null,null),(l()(),u.ub(4,0,null,0,2,"mat-icon",[["class","event-icon mat-icon notranslate"],["role","img"]],[[2,"mat-icon-inline",null],[2,"mat-icon-no-color",null]],null,null,R.b,R.a)),u.tb(5,9158656,null,0,X.b,[u.k,X.d,[8,null],[2,X.a]],null,null),(l()(),u.Ob(-1,0,["event"])),(l()(),u.kb(16777216,null,0,1,null,tl)),u.tb(8,16384,null,0,c.p,[u.P,u.M],{ngIf:[0,"ngIf"]},null),(l()(),u.ub(9,0,null,0,6,"div",[],null,null,null,null,null)),(l()(),u.ub(10,16777216,null,null,2,"div",[],null,null,null,null,null)),u.tb(11,540672,null,0,c.y,[u.P],{ngTemplateOutletContext:[0,"ngTemplateOutletContext"],ngTemplateOutlet:[1,"ngTemplateOutlet"]},null),u.Jb(12,{$implicit:0}),(l()(),u.ub(13,16777216,null,null,2,"div",[],null,null,null,null,null)),u.tb(14,540672,null,0,c.y,[u.P],{ngTemplateOutletContext:[0,"ngTemplateOutletContext"],ngTemplateOutlet:[1,"ngTemplateOutlet"]},null),u.Jb(15,{$implicit:0})],(function(l,n){l(n,1,0,null==n.context.$implicit?null:n.context.$implicit.routerLink),l(n,5,0),l(n,8,0,null==n.context.$implicit?null:n.context.$implicit.eventName);var t=l(n,12,0,n.context.$implicit);l(n,11,0,t,u.Gb(n.parent.parent,3));var a=l(n,15,0,n.context.$implicit);l(n,14,0,a,u.Gb(n.parent.parent,4))}),(function(l,n){l(n,0,0,u.Gb(n,1).target,u.Gb(n,1).href),l(n,2,0,"NoopAnimations"===u.Gb(n,3)._animationMode),l(n,4,0,u.Gb(n,5).inline,"primary"!==u.Gb(n,5).color&&"accent"!==u.Gb(n,5).color&&"warn"!==u.Gb(n,5).color)}))}function al(l){return u.Qb(0,[(l()(),u.ub(0,0,null,null,2,"div",[["class","margin-top-m text-center flex flex-wrapped flex-around"]],null,null,null,null,null)),(l()(),u.kb(16777216,null,null,1,null,ul)),u.tb(2,278528,null,0,c.o,[u.P,u.M,u.r],{ngForOf:[0,"ngForOf"]},null)],(function(l,n){l(n,2,0,n.component.eventsData)}),null)}function el(l){return u.Qb(0,[(l()(),u.ub(0,0,null,null,1,null,null,null,null,null,null,null)),(l()(),u.Ob(-1,null,["Starts: "]))],null,null)}function il(l){return u.Qb(0,[(l()(),u.Ob(-1,null,["Started: "]))],null,null)}function cl(l){return u.Qb(0,[(l()(),u.ub(0,0,null,null,3,"span",[["class","mat-body-strong"]],null,null,null,null,null)),(l()(),u.kb(16777216,null,null,1,null,el)),u.tb(2,16384,null,0,c.p,[u.P,u.M],{ngIf:[0,"ngIf"],ngIfElse:[1,"ngIfElse"]},null),(l()(),u.kb(0,[["eventStarted",2]],null,0,null,il)),(l()(),u.ub(4,0,null,null,2,"span",[["class","mat-body date-value"]],null,null,null,null,null)),(l()(),u.Ob(5,null,["",""])),u.Kb(6,1)],(function(l,n){l(n,2,0,n.component.isCurrentTimeSmall(n.context.$implicit.startTime),u.Gb(n,3))}),(function(l,n){var t=u.Pb(n,5,0,l(n,6,0,u.Gb(n.parent,0),n.context.$implicit.startTime));l(n,5,0,t)}))}function ol(l){return u.Qb(0,[(l()(),u.ub(0,0,null,null,1,null,null,null,null,null,null,null)),(l()(),u.Ob(-1,null,["Ends: "]))],null,null)}function rl(l){return u.Qb(0,[(l()(),u.Ob(-1,null,["Ended: "]))],null,null)}function bl(l){return u.Qb(0,[(l()(),u.ub(0,0,null,null,3,"span",[["class","mat-body-strong"]],null,null,null,null,null)),(l()(),u.kb(16777216,null,null,1,null,ol)),u.tb(2,16384,null,0,c.p,[u.P,u.M],{ngIf:[0,"ngIf"],ngIfElse:[1,"ngIfElse"]},null),(l()(),u.kb(0,[["eventEnded",2]],null,0,null,rl)),(l()(),u.ub(4,0,null,null,2,"span",[["class","mat-body date-value"]],null,null,null,null,null)),(l()(),u.Ob(5,null,["",""])),u.Kb(6,1)],(function(l,n){l(n,2,0,n.component.isCurrentTimeSmall(n.context.$implicit.endTime),u.Gb(n,3))}),(function(l,n){var t=u.Pb(n,5,0,l(n,6,0,u.Gb(n.parent,0),n.context.$implicit.endTime));l(n,5,0,t)}))}function sl(l){return u.Qb(0,[u.Ib(0,c.e,[u.t]),(l()(),u.kb(16777216,null,null,1,null,al)),u.tb(2,16384,null,0,c.p,[u.P,u.M],{ngIf:[0,"ngIf"]},null),(l()(),u.kb(0,[["startTime",2]],null,0,null,cl)),(l()(),u.kb(0,[["endTime",2]],null,0,null,bl))],(function(l,n){var t=n.component;l(n,2,0,t.eventsData&&(null==t.eventsData?null:t.eventsData.length))}),null)}class ml{constructor(l){this.route=l,this.channelsConfig=null,this.errorFetchingJson=!1,this.selectedIndex=0,this.tabs=[],this.tabData=null}ngOnInit(){const l=this.route.parent;l&&l.data.subscribe(l=>{l.channelsData.data?(this.channelsConfig=l.channelsData.data,this.getDetails()):l.channelsData.error&&(this.errorFetchingJson=!0)})}getDetails(){const l=this.channelsConfig;l&&(this.tabs=l.tabs.map(l=>l.tabDetails),this.route.paramMap.subscribe(n=>{const t=n.get("tab")?n.get("tab"):this.tabs[0].key;if(t){const n=l.tabs.map(l=>l.tabDetails.key).indexOf(t);this.tabData=l.tabs[n].tabContent}}))}}var pl=u.sb({encapsulation:0,styles:[[""]],data:{}});function gl(l){return u.Qb(0,[(l()(),u.ub(0,0,null,null,2,"div",[],null,null,null,null,null)),(l()(),u.ub(1,0,null,null,1,"ws-app-channel-hubs",[],null,null,null,_,S)),u.tb(2,114688,null,0,Q,[E.c],null,null)],(function(l,n){l(n,2,0)}),null)}function fl(l){return u.Qb(0,[(l()(),u.ub(0,0,null,null,2,"div",[],null,null,null,null,null)),(l()(),u.ub(1,0,null,null,1,"ws-app-leader-card",[],null,null,null,A,K)),u.tb(2,114688,null,0,F,[],{leaderData:[0,"leaderData"]},null)],(function(l,n){var t=n.component;l(n,2,0,null==t.tabData?null:t.tabData.data)}),null)}function hl(l){return u.Qb(0,[(l()(),u.ub(0,0,null,null,2,"div",[],null,null,null,null,null)),(l()(),u.ub(1,0,null,null,1,"ws-app-initiatives-card",[],null,null,null,Z,U)),u.tb(2,114688,null,0,B,[],{initiativesData:[0,"initiativesData"]},null)],(function(l,n){var t=n.component;l(n,2,0,null==t.tabData?null:t.tabData.data)}),null)}function dl(l){return u.Qb(0,[(l()(),u.ub(0,0,null,null,2,"div",[],null,null,null,null,null)),(l()(),u.ub(1,0,null,null,1,"ws-app-events-card",[],null,null,null,sl,nl)),u.tb(2,114688,null,0,ll,[],{eventsData:[0,"eventsData"]},null)],(function(l,n){var t=n.component;l(n,2,0,null==t.tabData?null:t.tabData.data)}),null)}function vl(l){return u.Qb(0,[(l()(),u.ub(0,0,null,null,1,"div",[],null,null,null,null,null)),(l()(),u.Ob(-1,null,[" This type is not yet supported. "]))],null,null)}function kl(l){return u.Qb(0,[(l()(),u.ub(0,0,null,null,12,"div",[],null,null,null,null,null)),(l()(),u.ub(1,0,null,null,11,"div",[],null,null,null,null,null)),u.tb(2,16384,null,0,c.v,[],{ngSwitch:[0,"ngSwitch"]},null),(l()(),u.kb(16777216,null,null,1,null,gl)),u.tb(4,278528,null,0,c.w,[u.P,u.M,c.v],{ngSwitchCase:[0,"ngSwitchCase"]},null),(l()(),u.kb(16777216,null,null,1,null,fl)),u.tb(6,278528,null,0,c.w,[u.P,u.M,c.v],{ngSwitchCase:[0,"ngSwitchCase"]},null),(l()(),u.kb(16777216,null,null,1,null,hl)),u.tb(8,278528,null,0,c.w,[u.P,u.M,c.v],{ngSwitchCase:[0,"ngSwitchCase"]},null),(l()(),u.kb(16777216,null,null,1,null,dl)),u.tb(10,278528,null,0,c.w,[u.P,u.M,c.v],{ngSwitchCase:[0,"ngSwitchCase"]},null),(l()(),u.kb(16777216,null,null,1,null,vl)),u.tb(12,16384,null,0,c.x,[u.P,u.M,c.v],null,null)],(function(l,n){var t=n.component;l(n,2,0,null==t.tabData?null:t.tabData.cardType),l(n,4,0,"hubs"),l(n,6,0,"leader-card"),l(n,8,0,"initiatives-card"),l(n,10,0,"events-card")}),null)}function wl(l){return u.Qb(0,[(l()(),u.kb(16777216,null,null,1,null,kl)),u.tb(1,16384,null,0,c.p,[u.P,u.M],{ngIf:[0,"ngIf"]},null)],(function(l,n){l(n,1,0,n.component.tabData)}),null)}function xl(l){return u.Qb(0,[(l()(),u.ub(0,0,null,null,1,"ws-app-channels-resolver",[],null,null,null,wl,pl)),u.tb(1,114688,null,0,ml,[i.a],null,null)],(function(l,n){l(n,1,0)}),null)}var Dl=u.qb("ws-app-channels-resolver",ml,xl,{},{},[]),Ol=t("POq0");const Gl={pageType:"feature",pageKey:"channels"};class yl{}var Il=t("cUpR"),Ml=t("NCrm"),Cl=t("Fwaw"),Pl=t("zMNK"),$l=t("CQWV"),Ql=t("I+L6");t.d(n,"ChannelsModuleNgFactory",(function(){return El}));var El=u.rb(a,[],(function(l){return u.Db([u.Eb(512,u.j,u.bb,[[8,[e.a,C,Dl,P.a,v.a]],[3,u.j],u.w]),u.Eb(4608,c.r,c.q,[u.t,[2,c.N]]),u.Eb(4608,Ol.c,Ol.c,[]),u.Eb(1073742336,c.c,c.c,[]),u.Eb(1073742336,i.s,i.s,[[2,i.x],[2,i.o]]),u.Eb(1073742336,yl,yl,[]),u.Eb(1073742336,g.a,g.a,[]),u.Eb(1073742336,b.l,b.l,[[2,b.d],[2,Il.f]]),u.Eb(1073742336,q.h,q.h,[]),u.Eb(1073742336,X.c,X.c,[]),u.Eb(1073742336,Ml.a,Ml.a,[]),u.Eb(1073742336,r.b,r.b,[]),u.Eb(1073742336,b.w,b.w,[]),u.Eb(1073742336,Cl.c,Cl.c,[]),u.Eb(1073742336,d.b,d.b,[]),u.Eb(1073742336,Pl.g,Pl.g,[]),u.Eb(1073742336,Ol.d,Ol.d,[]),u.Eb(1073742336,s.a,s.a,[]),u.Eb(1073742336,o.m,o.m,[]),u.Eb(1073742336,$l.a,$l.a,[]),u.Eb(1073742336,a,a,[]),u.Eb(256,u.t,"en",[]),u.Eb(1024,i.m,(function(){return[[{path:"",component:D,data:Gl,resolve:{channelsData:Ql.a},children:[{path:"",pathMatch:"full",component:D},{path:":tab",component:ml}]}]]}),[])])}))}}]);