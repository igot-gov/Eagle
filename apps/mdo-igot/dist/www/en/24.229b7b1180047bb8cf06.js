(window.webpackJsonp=window.webpackJsonp||[]).push([[24],{"5o8Q":function(t,n,l){"use strict";l.r(n);var a=l("8Y7J");class e{}var r=l("pMnS"),i=l("SVse"),c=(l("g6cB"),l("88ff")),u=l("IheW");let s=(()=>{class t{constructor(t,n){this.configSvc=t,this.http=n}fetchFrac(){return this.http.get(this.configSvc.baseUrl+"/feature/frac.json").toPromise()}}return t.ngInjectableDef=a.Tb({factory:function(){return new t(a.Ub(c.a),a.Ub(u.c))},token:t,providedIn:"root"}),t})();class o{constructor(t,n){this.domSanitizer=t,this.fracService=n,this.widgetData={iframeId:"fracData",title:"Frac",containerStyle:"",containerClass:"",iframeSrc:"https://google.com"},this.iframeSrc=null}ngOnInit(){this.fracService.fetchFrac().then(t=>{t?(this.widgetData=t,this.widgetData&&this.widgetData.iframeSrc&&(this.iframeSrc=this.domSanitizer.bypassSecurityTrustResourceUrl(this.widgetData.iframeSrc))):this.iframeSrc=this.domSanitizer.bypassSecurityTrustResourceUrl(window.location.origin+"/frac")})}ngOnDestroy(){}}var b=l("cUpR"),f=a.rb({encapsulation:0,styles:[[""]],data:{}});function D(t){return a.Pb(0,[(t()(),a.tb(0,0,null,null,4,"iframe",[["allow","autoplay *; fullscreen *; encrypted-media *; microphone; camera;"],["allowfullscreen",""],["frameborder","0"],["height","100%"],["mozAllowFullScreen",""],["style","height: -webkit-fill-available;"],["webkitallowfullscreen",""],["width","100%"]],[[8,"id",0],[8,"src",5],[8,"title",0]],null,null,null,null)),a.Kb(512,null,i.F,i.G,[a.r,a.s,a.k,a.D]),a.sb(2,278528,null,0,i.m,[i.F],{ngClass:[0,"ngClass"]},null),a.Kb(512,null,i.H,i.I,[a.k,a.s,a.D]),a.sb(4,278528,null,0,i.r,[i.H],{ngStyle:[0,"ngStyle"]},null)],(function(t,n){var l=n.component;t(n,2,0,null==l.widgetData?null:l.widgetData.containerClass),t(n,4,0,null==l.widgetData?null:l.widgetData.containerStyle)}),(function(t,n){var l=n.component;t(n,0,0,null==l.widgetData?null:l.widgetData.iframeId,l.iframeSrc,null==l.widgetData?null:l.widgetData.title)}))}function g(t){return a.Pb(0,[(t()(),a.jb(16777216,null,null,1,null,D)),a.sb(1,16384,null,0,i.o,[a.O,a.L],{ngIf:[0,"ngIf"]},null)],(function(t,n){t(n,1,0,n.component.iframeSrc)}),null)}function d(t){return a.Pb(0,[(t()(),a.tb(0,0,null,null,1,"ws-app-frac",[],null,null,null,g,f)),a.sb(1,245760,null,0,o,[b.b,s],null,null)],(function(t,n){t(n,1,0)}),null)}var w=a.pb("ws-app-frac",o,d,{},{},[]),h=l("iInd");class p{}var m=l("IP0z"),S=l("Xd0L"),y=l("igqZ"),v=l("/HVE"),I=l("Fwaw"),F=l("Gi4r"),k=l("W5yJ"),U=l("xolN");class j{}l.d(n,"RouteFracModuleNgFactory",(function(){return z}));var z=a.qb(e,[],(function(t){return a.Cb([a.Db(512,a.j,a.ab,[[8,[r.a,w]],[3,a.j],a.w]),a.Db(4608,i.q,i.p,[a.t,[2,i.K]]),a.Db(1073742336,i.c,i.c,[]),a.Db(1073742336,h.s,h.s,[[2,h.x],[2,h.o]]),a.Db(1073742336,p,p,[]),a.Db(1073742336,m.a,m.a,[]),a.Db(1073742336,S.n,S.n,[[2,S.f],[2,b.f]]),a.Db(1073742336,y.g,y.g,[]),a.Db(1073742336,v.b,v.b,[]),a.Db(1073742336,S.y,S.y,[]),a.Db(1073742336,I.c,I.c,[]),a.Db(1073742336,F.c,F.c,[]),a.Db(1073742336,k.c,k.c,[]),a.Db(1073742336,U.a,U.a,[]),a.Db(1073742336,j,j,[]),a.Db(1073742336,e,e,[]),a.Db(256,a.t,"en",[]),a.Db(1024,h.m,(function(){return[[{path:"",component:o,children:[]}]]}),[])])}))}}]);