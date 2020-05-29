(window.webpackJsonp=window.webpackJsonp||[]).push([[29],{EfhQ:function(n,l,t){"use strict";t.d(l,"a",(function(){return e}));var u=t("CcnG"),e=function(){return function(){this.countUpdate=new u.n}}()},sZIX:function(n,l,t){"use strict";t.r(l);var u=t("CcnG"),e=function(){return function(){}}(),s=t("NcP4"),i=t("pMnS"),c=t("Ip0R"),o=t("v9Dh"),a=t("eDkP"),b=t("qAlS"),r=t("dWZg"),d=t("lLAP"),p=t("Fzqc"),g=t("ZYjt"),f=t("ZYCi"),h=t("gIcY"),m=t("u7R8"),v=t("ure7"),x=t("Mr+X"),y=t("SMsm"),C=t("mrSG"),w=t("EfhQ"),k=t("K9Ia"),P=function(n){function l(l,t){var u=n.call(this)||this;return u.cvService=l,u.gaService=t,u.subunits=[],u.subunitSequences=[],u.vocabulary={},u.view="details",u.substanceUpdated=new k.a,u.cvType="AMINO_ACID_RESIDUE",u}return C.b(l,n),l.prototype.ngOnInit=function(){null!=this.substance&&null!=this.substance.protein&&null!=this.substance.protein.subunits&&this.substance.protein.subunits.length?(this.subunits=this.substance.protein.subunits,this.countUpdate.emit(this.subunits.length),this.cvType="AMINO_ACID_RESIDUE",this.substanceType="protein",this.getVocabularies(),this.uuid=this.substance.uuid):null!=this.substance&&null!=this.substance.nucleicAcid&&null!=this.substance.nucleicAcid.subunits&&this.substance.nucleicAcid.subunits.length&&(this.subunits=this.substance.nucleicAcid.subunits,this.countUpdate.emit(this.subunits.length),this.cvType="NUCLEIC_ACID_BASE",this.substanceType="nucleicAcid",this.getVocabularies(),this.uuid=this.substance.uuid)},l.prototype.ngAfterViewInit=function(){var n=this;this.substanceUpdated.subscribe((function(l){n.substance=l,n.uuid=n.substance.uuid,n.substance.protein?n.subunits=n.substance.protein.subunits:n.substance.nucleicAcid&&(n.subunits=n.substance.nucleicAcid.subunits),n.countUpdate.emit(n.subunits.length)}))},l.prototype.getVocabularies=function(){var n=this;this.cvService.getDomainVocabulary(this.cvType).subscribe((function(l){n.vocabulary=l[n.cvType].dictionary,n.processSubunits()}),(function(l){n.processSubunits()}))},l.prototype.processSubunits=function(){var n=this;this.subunits.forEach((function(l){var t={fullSequence:l.sequence,subunitIndex:l.subunitIndex,uuid:l.uuid,sequencesSectionGroups:[]};n.subunitSequences.push(t),n.addSequenceSectionsGroup(t,l.sequence)}))},l.prototype.addSequenceSectionsGroup=function(n,l,t){if(void 0===l&&(l=""),void 0===t&&(t=0),l.length>t+1){var u={sequenceSections:[]};n.sequencesSectionGroups.push(u);var e=l.substr(t,50);this.addSequenceSections(u,e,t),50===e.length&&this.addSequenceSectionsGroup(n,l,t+=50)}},l.prototype.addSequenceSections=function(n,l,t,u,e){if(void 0===l&&(l=""),void 0===u&&(u=0),void 0===e&&(e=9),""!==l){for(var s={sectionNumber:0,sectionUnits:[]};u<=e&&l[u];){var i={unitIndex:u+t+1,unitValue:l[u]};this.vocabulary[i.unitValue.toUpperCase()]||(i.class="error"),s.sectionUnits.push(i),u++}s.sectionNumber=u+t,n.sequenceSections.push(s),l.length>u&&this.addSequenceSections(n,l,t,u,e+=10)}},l.prototype.getTooltipMessage=function(n,l,t){var u;return u=this.vocabulary[t.toUpperCase()]?this.vocabulary[t.toUpperCase()].display:"UNDEFINED",n+" - "+l+": "+t.toUpperCase()+" ("+u+")"},l.prototype.updateView=function(n){this.gaService.sendEvent(this.analyticsEventCategory,"button:view-update",n.value),this.view=n.value},l}(w.a),M=t("YLZ7"),F=t("HECD"),O=u.rb({encapsulation:0,styles:[[".filters-container[_ngcontent-%COMP%]{display:-webkit-box;display:-ms-flexbox;display:flex;-webkit-box-pack:end;-ms-flex-pack:end;justify-content:flex-end}.subunit-title[_ngcontent-%COMP%]{color:#1565c0;text-decoration:none}.subunit-label[_ngcontent-%COMP%]{font-weight:500;font-size:16px}.form-row[_ngcontent-%COMP%]{display:-webkit-box;display:-ms-flexbox;display:flex;-webkit-box-pack:justify;-ms-flex-pack:justify;justify-content:space-between;padding-bottom:5px}.sequence-container[_ngcontent-%COMP%]{padding-top:20px}error[_ngcontent-%COMP%]{color:red}.subunit-row[_ngcontent-%COMP%]{padding-top:10px}.details[_ngcontent-%COMP%]   .subunit[_ngcontent-%COMP%]{display:none}.details[_ngcontent-%COMP%]   .subunit-sequence[_ngcontent-%COMP%], .show[_ngcontent-%COMP%]{display:block}.hide[_ngcontent-%COMP%]{display:none}.raw[_ngcontent-%COMP%]   .subunit[_ngcontent-%COMP%]{display:block}.raw[_ngcontent-%COMP%]   .subunit-sequence[_ngcontent-%COMP%]{display:none}.subunit-sequence[_ngcontent-%COMP%]{margin-bottom:30px}.sequence-section-group[_ngcontent-%COMP%]{width:100%;display:-webkit-box;display:-ms-flexbox;display:flex;margin-bottom:10px}.section-number[_ngcontent-%COMP%]{text-align:right;font-size:.8em}.sequence-section[_ngcontent-%COMP%]{-webkit-box-flex:0;-ms-flex-positive:0;flex-grow:0;padding:0 10px;-ms-flex-preferred-size:20%;flex-basis:20%;-ms-flex-negative:0;flex-shrink:0}.section-units-container[_ngcontent-%COMP%]{display:-webkit-box;display:-ms-flexbox;display:flex}.section-units-container[_ngcontent-%COMP%]   .section-unit[_ngcontent-%COMP%]:hover{cursor:default}.subunit[_ngcontent-%COMP%]{padding-top:5px;padding-bottom:20px}.subunit[_ngcontent-%COMP%]:not(:last-child){border-bottom:1px solid rgba(0,0,0,.12)}.raw-sequence[_ngcontent-%COMP%]{word-break:break-all;line-height:26px}.siteref[_ngcontent-%COMP%]{display:block;width:15.5px!important;text-align:center}.last-section[_ngcontent-%COMP%]{-ms-flex-preferred-size:0!important;flex-basis:0!important}"]],data:{}});function S(n){return u.Pb(0,[(n()(),u.tb(0,16777216,null,null,4,"span",[["class","section-unit  selectedSite siteref"]],null,[[null,"longpress"],[null,"keydown"],[null,"touchend"]],(function(n,l,t){var e=!0;return"longpress"===l&&(e=!1!==u.Fb(n,3).show()&&e),"keydown"===l&&(e=!1!==u.Fb(n,3)._handleKeydown(t)&&e),"touchend"===l&&(e=!1!==u.Fb(n,3)._handleTouchend()&&e),e}),null,null)),u.Kb(512,null,c.z,c.A,[u.s,u.t,u.k,u.E]),u.sb(2,278528,null,0,c.k,[c.z],{klass:[0,"klass"],ngClass:[1,"ngClass"]},null),u.sb(3,212992,null,0,o.d,[a.c,u.k,b.b,u.P,u.z,r.a,d.c,d.f,o.b,[2,p.b],[2,o.a],[2,g.f]],{message:[0,"message"]},null),(n()(),u.Nb(4,null,[" "," "])),(n()(),u.jb(0,null,null,0))],(function(n,l){var t=l.component;n(l,2,0,"section-unit  selectedSite siteref",l.context.$implicit.class?"error":""),n(l,3,0,t.getTooltipMessage(l.parent.parent.parent.context.$implicit.subunitIndex,l.context.$implicit.unitIndex,l.context.$implicit.unitValue))}),(function(n,l){n(l,4,0,l.context.$implicit.unitValue)}))}function _(n){return u.Pb(0,[(n()(),u.tb(0,0,null,null,7,"div",[["class","sequence-section"]],null,null,null,null,null)),u.Kb(512,null,c.z,c.A,[u.s,u.t,u.k,u.E]),u.sb(2,278528,null,0,c.k,[c.z],{klass:[0,"klass"],ngClass:[1,"ngClass"]},null),(n()(),u.tb(3,0,null,null,1,"div",[["class","section-number"]],null,null,null,null,null)),(n()(),u.Nb(4,null,["",""])),(n()(),u.tb(5,0,null,null,2,"div",[["class","section-units-container"]],null,null,null,null,null)),(n()(),u.jb(16777216,null,null,1,null,S)),u.sb(7,278528,null,0,c.l,[u.P,u.M,u.s],{ngForOf:[0,"ngForOf"]},null)],(function(n,l){n(l,2,0,"sequence-section",l.parent.context.last&&l.context.last?"last-section":""),n(l,7,0,l.context.$implicit.sectionUnits)}),(function(n,l){n(l,4,0,l.context.$implicit.sectionNumber)}))}function q(n){return u.Pb(0,[(n()(),u.tb(0,0,null,null,2,"div",[["class","sequence-section-group"]],null,null,null,null,null)),(n()(),u.jb(16777216,null,null,1,null,_)),u.sb(2,278528,null,0,c.l,[u.P,u.M,u.s],{ngForOf:[0,"ngForOf"]},null)],(function(n,l){n(l,2,0,l.context.$implicit.sequenceSections)}),null)}function D(n){return u.Pb(0,[(n()(),u.tb(0,0,null,null,13,"div",[["class","subunit-sequence"]],null,null,null,null,null)),(n()(),u.tb(1,0,null,null,8,"div",[["class","form-row subunit-row"]],null,null,null,null,null)),(n()(),u.tb(2,0,null,null,1,"div",[["class","subunit-label"]],null,null,null,null,null)),(n()(),u.Nb(3,null,[" Subunit ",""])),(n()(),u.tb(4,0,null,null,5,"div",[],null,null,null,null,null)),(n()(),u.tb(5,0,null,null,4,"a",[["class","subunit-title"]],[[1,"target",0],[8,"href",4]],[[null,"click"]],(function(n,l,t){var e=!0;return"click"===l&&(e=!1!==u.Fb(n,6).onClick(t.button,t.ctrlKey,t.metaKey,t.shiftKey)&&e),e}),null,null)),u.sb(6,671744,null,0,f.o,[f.m,f.a,c.j],{queryParams:[0,"queryParams"],routerLink:[1,"routerLink"]},null),u.Ib(7,{substance:0,subunit:1,seq_type:2}),u.Gb(8,1),(n()(),u.Nb(-1,null,["similarity search"])),(n()(),u.tb(10,0,null,null,3,"div",[["class","subunit-sequence"]],null,null,null,null,null)),(n()(),u.tb(11,0,null,null,2,"div",[["class","responsive"]],null,null,null,null,null)),(n()(),u.jb(16777216,null,null,1,null,q)),u.sb(13,278528,null,0,c.l,[u.P,u.M,u.s],{ngForOf:[0,"ngForOf"]},null)],(function(n,l){var t=l.component,u=n(l,7,0,t.uuid,l.context.$implicit.uuid,t.substanceType),e=n(l,8,0,"/sequence-search");n(l,6,0,u,e),n(l,13,0,l.context.$implicit.sequencesSectionGroups)}),(function(n,l){n(l,3,0,l.context.$implicit.subunitIndex),n(l,5,0,u.Fb(l,6).target,u.Fb(l,6).href)}))}function I(n){return u.Pb(0,[(n()(),u.tb(0,0,null,null,4,"div",[["class","subunit"]],null,null,null,null,null)),(n()(),u.tb(1,0,null,null,1,"h4",[],null,null,null,null,null)),(n()(),u.Nb(2,null,["Subunit ",""])),(n()(),u.tb(3,0,null,null,1,"div",[["class","raw-sequence"]],null,null,null,null,null)),(n()(),u.Nb(4,null,["",""]))],null,(function(n,l){n(l,2,0,l.context.$implicit.subunitIndex),n(l,4,0,l.context.$implicit.sequence)}))}function A(n){return u.Pb(0,[(n()(),u.tb(0,0,null,null,13,"div",[["class","filters-container"]],null,null,null,null,null)),(n()(),u.tb(1,0,null,null,12,"mat-button-toggle-group",[["class","mat-button-toggle-group"],["role","group"]],[[1,"aria-disabled",0],[2,"mat-button-toggle-vertical",null],[2,"mat-button-toggle-group-appearance-standard",null]],[[null,"change"]],(function(n,l,t){var u=!0;return"change"===l&&(u=!1!==n.component.updateView(t)&&u),u}),null,null)),u.Kb(5120,null,h.m,(function(n){return[n]}),[m.c]),u.Kb(6144,null,m.d,null,[m.c]),u.sb(4,1130496,null,1,m.c,[u.h,[2,m.a]],{value:[0,"value"]},{change:"change"}),u.Lb(603979776,1,{_buttonToggles:1}),(n()(),u.tb(6,0,null,null,3,"mat-button-toggle",[["class","mat-button-toggle"],["value","details"]],[[2,"mat-button-toggle-standalone",null],[2,"mat-button-toggle-checked",null],[2,"mat-button-toggle-disabled",null],[2,"mat-button-toggle-appearance-standard",null],[1,"tabindex",0],[1,"id",0],[1,"name",0]],[[null,"focus"]],(function(n,l,t){var e=!0;return"focus"===l&&(e=!1!==u.Fb(n,7).focus()&&e),e}),v.b,v.a)),u.sb(7,245760,[[1,4]],0,m.b,[[2,m.c],u.h,u.k,d.f,[8,null],[2,m.a]],{value:[0,"value"]},null),(n()(),u.tb(8,0,null,0,1,"mat-icon",[["class","mat-icon notranslate"],["role","img"],["svgIcon","list"]],[[2,"mat-icon-inline",null],[2,"mat-icon-no-color",null]],null,null,x.b,x.a)),u.sb(9,9158656,null,0,y.b,[u.k,y.d,[8,null],[2,y.a],[2,u.m]],{svgIcon:[0,"svgIcon"]},null),(n()(),u.tb(10,0,null,null,3,"mat-button-toggle",[["class","mat-button-toggle"],["value","raw"]],[[2,"mat-button-toggle-standalone",null],[2,"mat-button-toggle-checked",null],[2,"mat-button-toggle-disabled",null],[2,"mat-button-toggle-appearance-standard",null],[1,"tabindex",0],[1,"id",0],[1,"name",0]],[[null,"focus"]],(function(n,l,t){var e=!0;return"focus"===l&&(e=!1!==u.Fb(n,11).focus()&&e),e}),v.b,v.a)),u.sb(11,245760,[[1,4]],0,m.b,[[2,m.c],u.h,u.k,d.f,[8,null],[2,m.a]],{value:[0,"value"]},null),(n()(),u.tb(12,0,null,0,1,"mat-icon",[["class","mat-icon notranslate"],["role","img"],["svgIcon","view_stream"]],[[2,"mat-icon-inline",null],[2,"mat-icon-no-color",null]],null,null,x.b,x.a)),u.sb(13,9158656,null,0,y.b,[u.k,y.d,[8,null],[2,y.a],[2,u.m]],{svgIcon:[0,"svgIcon"]},null),(n()(),u.tb(14,0,null,null,6,"div",[],null,null,null,null,null)),u.Kb(512,null,c.z,c.A,[u.s,u.t,u.k,u.E]),u.sb(16,278528,null,0,c.k,[c.z],{ngClass:[0,"ngClass"]},null),(n()(),u.jb(16777216,null,null,1,null,D)),u.sb(18,278528,null,0,c.l,[u.P,u.M,u.s],{ngForOf:[0,"ngForOf"]},null),(n()(),u.jb(16777216,null,null,1,null,I)),u.sb(20,278528,null,0,c.l,[u.P,u.M,u.s],{ngForOf:[0,"ngForOf"]},null)],(function(n,l){var t=l.component;n(l,4,0,t.view),n(l,7,0,"details"),n(l,9,0,"list"),n(l,11,0,"raw"),n(l,13,0,"view_stream"),n(l,16,0,t.view),n(l,18,0,t.subunitSequences),n(l,20,0,t.subunits)}),(function(n,l){n(l,1,0,u.Fb(l,4).disabled,u.Fb(l,4).vertical,"standard"===u.Fb(l,4).appearance),n(l,6,0,!u.Fb(l,7).buttonToggleGroup,u.Fb(l,7).checked,u.Fb(l,7).disabled,"standard"===u.Fb(l,7).appearance,-1,u.Fb(l,7).id,null),n(l,8,0,u.Fb(l,9).inline,"primary"!==u.Fb(l,9).color&&"accent"!==u.Fb(l,9).color&&"warn"!==u.Fb(l,9).color),n(l,10,0,!u.Fb(l,11).buttonToggleGroup,u.Fb(l,11).checked,u.Fb(l,11).disabled,"standard"===u.Fb(l,11).appearance,-1,u.Fb(l,11).id,null),n(l,12,0,u.Fb(l,13).inline,"primary"!==u.Fb(l,13).color&&"accent"!==u.Fb(l,13).color&&"warn"!==u.Fb(l,13).color)}))}function E(n){return u.Pb(0,[(n()(),u.tb(0,0,null,null,1,"app-substance-subunits",[],null,null,null,A,O)),u.sb(1,4308992,null,0,P,[M.a,F.a],null,null)],(function(n,l){n(l,1,0)}),null)}var U=u.pb("app-substance-subunits",P,E,{},{countUpdate:"countUpdate"},[]),j=t("M2Lx"),N=t("Wf4p"),T=t("EtvR"),z=t("4c35"),$=t("6jyQ");t.d(l,"SubstanceSubunitsModuleNgFactory",(function(){return G}));var G=u.qb(e,[],(function(n){return u.Cb([u.Db(512,u.j,u.bb,[[8,[s.a,i.a,U]],[3,u.j],u.x]),u.Db(4608,c.o,c.n,[u.u,[2,c.E]]),u.Db(4608,j.c,j.c,[]),u.Db(4608,a.c,a.c,[a.i,a.e,u.j,a.h,a.f,u.r,u.z,c.d,p.b,[2,c.i]]),u.Db(5120,a.j,a.k,[a.c]),u.Db(5120,o.b,o.c,[a.c]),u.Db(4608,g.e,N.e,[[2,N.i],[2,N.n]]),u.Db(1073742336,c.c,c.c,[]),u.Db(1073742336,T.a,T.a,[]),u.Db(1073742336,r.b,r.b,[]),u.Db(1073742336,j.d,j.d,[]),u.Db(1073742336,d.a,d.a,[]),u.Db(1073742336,p.a,p.a,[]),u.Db(1073742336,z.g,z.g,[]),u.Db(1073742336,b.c,b.c,[]),u.Db(1073742336,a.g,a.g,[]),u.Db(1073742336,N.n,N.n,[[2,N.f],[2,g.f]]),u.Db(1073742336,o.e,o.e,[]),u.Db(1073742336,N.x,N.x,[]),u.Db(1073742336,m.e,m.e,[]),u.Db(1073742336,y.c,y.c,[]),u.Db(1073742336,f.p,f.p,[[2,f.u],[2,f.m]]),u.Db(1073742336,e,e,[]),u.Db(1024,f.j,(function(){return[[]]}),[]),u.Db(256,$.a,P,[])])}))}}]);
//# sourceMappingURL=29.616d84f6b69068b236e3.js.map