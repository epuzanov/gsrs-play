(window.webpackJsonp=window.webpackJsonp||[]).push([[45],{bBkp:function(n,l,e){"use strict";e.d(l,"a",(function(){return o}));var t=e("CcnG"),o=function(){return function(){this.menuLabelUpdate=new t.n,this.hiddenStateUpdate=new t.n,this.canAddItemUpdate=new t.n,this.componentDestroyed=new t.n}}()},btq3:function(n,l,e){"use strict";e.r(l);var t=e("CcnG"),o=function(){return function(){}}(),a=e("NcP4"),i=e("t68o"),u=e("pMnS"),r=e("HvtJ"),c=e("/J3S"),d=e("R/n8"),s=e("ThfK"),b=e("ldJ0"),m=e("OvbY"),p=e("Ok+c"),f=e("Pj+I"),g=e("Cka/"),h=e("UMU1"),v=e("dCG0"),F=e("B/2v"),y=e("TtEo"),_=e("LC5p"),C=e("xZkp"),x=e("hifq"),M=e("bujt"),w=e("UodH"),D=e("lLAP"),P=e("wFw1"),k=e("v9Dh"),S=e("eDkP"),O=e("qAlS"),I=e("dWZg"),T=e("Fzqc"),L=e("ZYjt"),U=e("Mr+X"),E=e("SMsm"),j=e("dJrM"),A=e("seP3"),N=e("Wf4p"),q=e("gIcY"),z=e("b716"),R=e("/VYK"),Y=e("Ip0R"),K=e("v0ZX"),G=e("Z16F"),V=e("CQqH"),Z=e("s7Fu"),H=e("khmc"),J=e("YLZ7"),$=e("o3x0"),X=e("6E2U"),W=e("4S5B"),B=e("Vurf"),Q=e("jEQs"),nn=e("gvL1"),ln=e("rMNG"),en=e("oY6q"),tn=function(){function n(n,l,e,o,a){this.cvService=n,this.dialog=l,this.utilsService=e,this.overlayContainerService=o,this.substanceFormService=a,this.modDeleted=new t.n,this.modExtentList=[],this.modLocationList=[],this.modTypeList=[],this.subscriptions=[]}return n.prototype.ngOnInit=function(){this.getVocabularies(),this.overlayContainer=this.overlayContainerService.getContainerElement(),this.updateDisplay(),this.getSubstanceType()},n.prototype.ngAfterViewInit=function(){},Object.defineProperty(n.prototype,"mod",{get:function(){return this.privateMod},set:function(n){this.privateMod=n,this.relatedSubstanceUuid=this.privateMod.molecularFragment&&this.privateMod.molecularFragment.refuuid||""},enumerable:!0,configurable:!0}),n.prototype.getSubstanceType=function(){var n=this;this.substanceFormService.definition.subscribe((function(l){n.substanceType=l.substanceClass})).unsubscribe()},n.prototype.getVocabularies=function(){var n=this;this.cvService.getDomainVocabulary("STRUCTURAL_MODIFICATION_TYPE","LOCATION_TYPE","EXTENT_TYPE").subscribe((function(l){n.modTypeList=l.STRUCTURAL_MODIFICATION_TYPE.list,n.modLocationList=l.LOCATION_TYPE.list,n.modExtentList=l.EXTENT_TYPE.list}))},n.prototype.deleteMod=function(){var n=this;this.privateMod.$$deletedCode=this.utilsService.newUUID(),this.deleteTimer=setTimeout((function(){n.modDeleted.emit(n.privateMod)}),2e3)},n.prototype.undoDelete=function(){clearTimeout(this.deleteTimer),delete this.privateMod.$$deletedCode},n.prototype.updateAccess=function(n){this.mod.access=n},n.prototype.relatedSubstanceUpdated=function(n){this.mod.molecularFragment={refPname:n._name,name:n._name,refuuid:n.uuid,substanceClass:"reference",approvalID:n.approvalID},this.relatedSubstanceUuid=this.mod.molecularFragment.refuuid},n.prototype.openDialog=function(){var n=this,l=this.dialog.open(ln.a,{data:{card:"other",link:this.mod.sites},width:"1040px"});this.overlayContainer.style.zIndex="1002";var e=l.afterClosed().subscribe((function(l){n.overlayContainer.style.zIndex=null,n.mod.sites=l,n.updateDisplay(),n.substanceFormService.emitStructuralModificationsUpdate()}));this.subscriptions.push(e)},n.prototype.openAmountDialog=function(){var n=this;this.mod.extentAmount||(this.mod.extentAmount={});var l=this.dialog.open(en.a,{data:{subsAmount:this.mod.extentAmount},width:"1040px"});this.overlayContainer.style.zIndex="1002";var e=l.afterClosed().subscribe((function(l){n.overlayContainer.style.zIndex=null,n.mod.extentAmount=l}));this.subscriptions.push(e)},n.prototype.updateDisplay=function(){this.siteDisplay=this.substanceFormService.siteString(this.mod.sites)},n.prototype.displayAmount=function(n){return this.utilsService.displayAmount(n)},n.prototype.formatValue=function(n){return n?"object"==typeof n?n.display?n.display:n.value?n.value:null:n:null},n}(),on=e("Jj5M"),an=t.rb({encapsulation:0,styles:[['.code-form-container[_ngcontent-%COMP%]{padding:30px 10px 12px;position:relative;display:-webkit-box;display:-ms-flexbox;display:flex}.related-substance[_ngcontent-%COMP%]{max-width:25%;width:25%}  .related-substance img{max-width:125px!important;margin:auto}.notification-backdrop[_ngcontent-%COMP%]{position:absolute;top:0;right:0;bottom:0;left:0;display:-webkit-box;display:-ms-flexbox;display:flex;z-index:10;background-color:rgba(255,255,255,.8);-webkit-box-pack:center;-ms-flex-pack:center;justify-content:center;-webkit-box-align:center;-ms-flex-align:center;align-items:center;font-size:30px;font-weight:700;color:#666}.form-row[_ngcontent-%COMP%]{display:-webkit-box;display:-ms-flexbox;display:flex;-webkit-box-pack:justify;-ms-flex-pack:justify;justify-content:space-between;-webkit-box-align:end;-ms-flex-align:end;align-items:flex-end}.form-row[_ngcontent-%COMP%]   .delete-container[_ngcontent-%COMP%]{padding:0 10px 8px 0}.form-row[_ngcontent-%COMP%]   .code-system[_ngcontent-%COMP%], .form-row[_ngcontent-%COMP%]   .code-system-type[_ngcontent-%COMP%], .form-row[_ngcontent-%COMP%]   .type[_ngcontent-%COMP%]{-webkit-box-flex:1;-ms-flex-positive:1;flex-grow:1;padding-right:15px}.form-row[_ngcontent-%COMP%]   .code-text[_ngcontent-%COMP%], .form-row[_ngcontent-%COMP%]   .url[_ngcontent-%COMP%]{-webkit-box-flex:1;-ms-flex-positive:1;flex-grow:1}.key-value-pair[_ngcontent-%COMP%]{display:-webkit-box;display:-ms-flexbox;display:flex;-webkit-box-orient:vertical;-webkit-box-direction:normal;-ms-flex-direction:column;flex-direction:column;-ms-flex-item-align:start;align-self:flex-start}.key-value-pair[_ngcontent-%COMP%]   .key[_ngcontent-%COMP%]{font-size:11px;padding-bottom:3.5px;line-height:11px;color:rgba(0,0,0,.54);font-weight:400;font-family:Roboto,"Helvetica Neue",sans-serif}.key-value-pair[_ngcontent-%COMP%]   .value[_ngcontent-%COMP%]{font-size:15.5px}.references-container[_ngcontent-%COMP%]{width:100%}.form-row[_ngcontent-%COMP%]{display:-webkit-box;display:-ms-flexbox;display:flex;-webkit-box-pack:justify;-ms-flex-pack:justify;justify-content:space-between;padding:0 10px;-webkit-box-align:end;-ms-flex-align:end;align-items:flex-end}.form-row[_ngcontent-%COMP%]   .checkbox-container[_ngcontent-%COMP%]{padding-bottom:18px}.form-row[_ngcontent-%COMP%]   .location-type[_ngcontent-%COMP%]   .extent[_ngcontent-%COMP%]   .sites[_ngcontent-%COMP%]{-webkit-box-flex:1;-ms-flex-positive:1;flex-grow:1}.form-row[_ngcontent-%COMP%]   .amount[_ngcontent-%COMP%], .form-row[_ngcontent-%COMP%]   .comment[_ngcontent-%COMP%]{width:45%;padding-right:15px;-webkit-box-flex:1;-ms-flex-positive:1;flex-grow:1}.padded[_ngcontent-%COMP%]{padding-right:20px}.amount-display[_ngcontent-%COMP%]{padding-top:11px}.form-actions[_ngcontent-%COMP%]{-webkit-box-pack:start;-ms-flex-pack:start;justify-content:flex-start;margin:5px 0 10px}.form-content[_ngcontent-%COMP%]{-webkit-box-flex:1;-ms-flex-positive:1;flex-grow:1}.amount[_ngcontent-%COMP%], .extent[_ngcontent-%COMP%], .group-access[_ngcontent-%COMP%], .location-type[_ngcontent-%COMP%], .mod-type[_ngcontent-%COMP%], .sites[_ngcontent-%COMP%]{width:33%}.access[_ngcontent-%COMP%], .group[_ngcontent-%COMP%]{width:45%}.residues[_ngcontent-%COMP%]{width:60%}']],data:{}});function un(n){return t.Pb(0,[(n()(),t.tb(0,0,null,null,6,"div",[["class","notification-backdrop"]],null,null,null,null,null)),(n()(),t.Nb(-1,null,[" Deleted  "])),(n()(),t.tb(2,16777216,null,null,4,"button",[["mat-icon-button",""],["matTooltip","Undo"]],[[1,"disabled",0],[2,"_mat-animation-noopable",null]],[[null,"click"],[null,"longpress"],[null,"keydown"],[null,"touchend"]],(function(n,l,e){var o=!0,a=n.component;return"longpress"===l&&(o=!1!==t.Fb(n,4).show()&&o),"keydown"===l&&(o=!1!==t.Fb(n,4)._handleKeydown(e)&&o),"touchend"===l&&(o=!1!==t.Fb(n,4)._handleTouchend()&&o),"click"===l&&(o=!1!==a.undoDelete()&&o),o}),M.d,M.b)),t.sb(3,180224,null,0,w.b,[t.k,D.f,[2,P.a]],null,null),t.sb(4,212992,null,0,k.d,[S.c,t.k,O.b,t.P,t.z,I.a,D.c,D.f,k.b,[2,T.b],[2,k.a],[2,L.f]],{message:[0,"message"]},null),(n()(),t.tb(5,0,null,0,1,"mat-icon",[["class","mat-icon notranslate"],["role","img"],["svgIcon","undo"]],[[2,"mat-icon-inline",null],[2,"mat-icon-no-color",null]],null,null,U.b,U.a)),t.sb(6,9158656,null,0,E.b,[t.k,E.d,[8,null],[2,E.a],[2,t.m]],{svgIcon:[0,"svgIcon"]},null)],(function(n,l){n(l,4,0,"Undo"),n(l,6,0,"undo")}),(function(n,l){n(l,2,0,t.Fb(l,3).disabled||null,"NoopAnimations"===t.Fb(l,3)._animationMode),n(l,5,0,t.Fb(l,6).inline,"primary"!==t.Fb(l,6).color&&"accent"!==t.Fb(l,6).color&&"warn"!==t.Fb(l,6).color)}))}function rn(n){return t.Pb(0,[(n()(),t.tb(0,0,null,null,8,"div",[["class","sites"]],null,null,null,null,null)),(n()(),t.tb(1,0,null,null,1,"div",[["class","label"]],null,null,null,null,null)),(n()(),t.Nb(-1,null,["Sites"])),(n()(),t.Nb(3,null,[" "," "])),(n()(),t.tb(4,16777216,null,null,4,"button",[["mat-icon-button",""],["matTooltip","Undo"]],[[1,"disabled",0],[2,"_mat-animation-noopable",null]],[[null,"click"],[null,"longpress"],[null,"keydown"],[null,"touchend"]],(function(n,l,e){var o=!0,a=n.component;return"longpress"===l&&(o=!1!==t.Fb(n,6).show()&&o),"keydown"===l&&(o=!1!==t.Fb(n,6)._handleKeydown(e)&&o),"touchend"===l&&(o=!1!==t.Fb(n,6)._handleTouchend()&&o),"click"===l&&(o=!1!==a.openDialog()&&o),o}),M.d,M.b)),t.sb(5,180224,null,0,w.b,[t.k,D.f,[2,P.a]],null,null),t.sb(6,212992,null,0,k.d,[S.c,t.k,O.b,t.P,t.z,I.a,D.c,D.f,k.b,[2,T.b],[2,k.a],[2,L.f]],{message:[0,"message"]},null),(n()(),t.tb(7,0,null,0,1,"mat-icon",[["class","mat-icon notranslate"],["role","img"],["svgIcon","edit"]],[[2,"mat-icon-inline",null],[2,"mat-icon-no-color",null]],null,null,U.b,U.a)),t.sb(8,9158656,null,0,E.b,[t.k,E.d,[8,null],[2,E.a],[2,t.m]],{svgIcon:[0,"svgIcon"]},null)],(function(n,l){n(l,6,0,"Undo"),n(l,8,0,"edit")}),(function(n,l){n(l,3,0,l.component.siteDisplay),n(l,4,0,t.Fb(l,5).disabled||null,"NoopAnimations"===t.Fb(l,5)._animationMode),n(l,7,0,t.Fb(l,8).inline,"primary"!==t.Fb(l,8).color&&"accent"!==t.Fb(l,8).color&&"warn"!==t.Fb(l,8).color)}))}function cn(n){return t.Pb(0,[(n()(),t.tb(0,0,null,null,19,"div",[["class","residues"]],null,null,null,null,null)),(n()(),t.tb(1,0,null,null,18,"mat-form-field",[["class","group mat-form-field"]],[[2,"mat-form-field-appearance-standard",null],[2,"mat-form-field-appearance-fill",null],[2,"mat-form-field-appearance-outline",null],[2,"mat-form-field-appearance-legacy",null],[2,"mat-form-field-invalid",null],[2,"mat-form-field-can-float",null],[2,"mat-form-field-should-float",null],[2,"mat-form-field-has-label",null],[2,"mat-form-field-hide-placeholder",null],[2,"mat-form-field-disabled",null],[2,"mat-form-field-autofilled",null],[2,"mat-focused",null],[2,"mat-accent",null],[2,"mat-warn",null],[2,"ng-untouched",null],[2,"ng-touched",null],[2,"ng-pristine",null],[2,"ng-dirty",null],[2,"ng-valid",null],[2,"ng-invalid",null],[2,"ng-pending",null],[2,"_mat-animation-noopable",null]],null,null,j.b,j.a)),t.sb(2,7520256,null,9,A.c,[t.k,t.h,[2,N.j],[2,T.b],[2,A.a],I.a,t.z,[2,P.a]],null,null),t.Lb(603979776,1,{_controlNonStatic:0}),t.Lb(335544320,2,{_controlStatic:0}),t.Lb(603979776,3,{_labelChildNonStatic:0}),t.Lb(335544320,4,{_labelChildStatic:0}),t.Lb(603979776,5,{_placeholderChild:0}),t.Lb(603979776,6,{_errorChildren:1}),t.Lb(603979776,7,{_hintChildren:1}),t.Lb(603979776,8,{_prefixChildren:1}),t.Lb(603979776,9,{_suffixChildren:1}),(n()(),t.tb(12,0,null,1,7,"input",[["class","mat-input-element mat-form-field-autofill-control"],["matInput",""],["name","Residue Modified"],["placeholder","Residue Modified"]],[[2,"ng-untouched",null],[2,"ng-touched",null],[2,"ng-pristine",null],[2,"ng-dirty",null],[2,"ng-valid",null],[2,"ng-invalid",null],[2,"ng-pending",null],[2,"mat-input-server",null],[1,"id",0],[1,"placeholder",0],[8,"disabled",0],[8,"required",0],[1,"readonly",0],[1,"aria-describedby",0],[1,"aria-invalid",0],[1,"aria-required",0]],[[null,"ngModelChange"],[null,"input"],[null,"blur"],[null,"compositionstart"],[null,"compositionend"],[null,"focus"]],(function(n,l,e){var o=!0,a=n.component;return"input"===l&&(o=!1!==t.Fb(n,13)._handleInput(e.target.value)&&o),"blur"===l&&(o=!1!==t.Fb(n,13).onTouched()&&o),"compositionstart"===l&&(o=!1!==t.Fb(n,13)._compositionStart()&&o),"compositionend"===l&&(o=!1!==t.Fb(n,13)._compositionEnd(e.target.value)&&o),"blur"===l&&(o=!1!==t.Fb(n,18)._focusChanged(!1)&&o),"focus"===l&&(o=!1!==t.Fb(n,18)._focusChanged(!0)&&o),"input"===l&&(o=!1!==t.Fb(n,18)._onInput()&&o),"ngModelChange"===l&&(o=!1!==(a.mod.residueModified=e)&&o),o}),null,null)),t.sb(13,16384,null,0,q.d,[t.E,t.k,[2,q.a]],null,null),t.Kb(1024,null,q.m,(function(n){return[n]}),[q.d]),t.sb(15,671744,null,0,q.r,[[8,null],[8,null],[8,null],[6,q.m]],{name:[0,"name"],model:[1,"model"]},{update:"ngModelChange"}),t.Kb(2048,null,q.n,null,[q.r]),t.sb(17,16384,null,0,q.o,[[4,q.n]],null,null),t.sb(18,999424,null,0,z.a,[t.k,I.a,[6,q.n],[2,q.q],[2,q.j],N.d,[8,null],R.a,t.z],{placeholder:[0,"placeholder"]},null),t.Kb(2048,[[1,4],[2,4]],A.d,null,[z.a])],(function(n,l){n(l,15,0,"Residue Modified",l.component.mod.residueModified),n(l,18,0,"Residue Modified")}),(function(n,l){n(l,1,1,["standard"==t.Fb(l,2).appearance,"fill"==t.Fb(l,2).appearance,"outline"==t.Fb(l,2).appearance,"legacy"==t.Fb(l,2).appearance,t.Fb(l,2)._control.errorState,t.Fb(l,2)._canLabelFloat,t.Fb(l,2)._shouldLabelFloat(),t.Fb(l,2)._hasFloatingLabel(),t.Fb(l,2)._hideControlPlaceholder(),t.Fb(l,2)._control.disabled,t.Fb(l,2)._control.autofilled,t.Fb(l,2)._control.focused,"accent"==t.Fb(l,2).color,"warn"==t.Fb(l,2).color,t.Fb(l,2)._shouldForward("untouched"),t.Fb(l,2)._shouldForward("touched"),t.Fb(l,2)._shouldForward("pristine"),t.Fb(l,2)._shouldForward("dirty"),t.Fb(l,2)._shouldForward("valid"),t.Fb(l,2)._shouldForward("invalid"),t.Fb(l,2)._shouldForward("pending"),!t.Fb(l,2)._animationsEnabled]),n(l,12,1,[t.Fb(l,17).ngClassUntouched,t.Fb(l,17).ngClassTouched,t.Fb(l,17).ngClassPristine,t.Fb(l,17).ngClassDirty,t.Fb(l,17).ngClassValid,t.Fb(l,17).ngClassInvalid,t.Fb(l,17).ngClassPending,t.Fb(l,18)._isServer,t.Fb(l,18).id,t.Fb(l,18).placeholder,t.Fb(l,18).disabled,t.Fb(l,18).required,t.Fb(l,18).readonly&&!t.Fb(l,18)._isNativeSelect||null,t.Fb(l,18)._ariaDescribedby||null,t.Fb(l,18).errorState,t.Fb(l,18).required.toString()])}))}function dn(n){return t.Pb(0,[(n()(),t.tb(0,0,null,null,1,"div",[["class","amount-display"]],null,null,null,null,null)),(n()(),t.Nb(1,null,[" "," "]))],null,(function(n,l){var e=l.component;n(l,1,0,e.displayAmount(e.mod.extentAmount))}))}function sn(n){return t.Pb(0,[(n()(),t.tb(0,0,null,null,9,"div",[["class","amount"]],null,null,null,null,null)),(n()(),t.tb(1,0,null,null,1,"div",[["class","label padded"]],null,null,null,null,null)),(n()(),t.Nb(-1,null,[" Amount "])),(n()(),t.tb(3,16777216,null,null,4,"button",[["mat-icon-button",""],["matTooltip","add"]],[[1,"disabled",0],[2,"_mat-animation-noopable",null]],[[null,"click"],[null,"longpress"],[null,"keydown"],[null,"touchend"]],(function(n,l,e){var o=!0,a=n.component;return"longpress"===l&&(o=!1!==t.Fb(n,5).show()&&o),"keydown"===l&&(o=!1!==t.Fb(n,5)._handleKeydown(e)&&o),"touchend"===l&&(o=!1!==t.Fb(n,5)._handleTouchend()&&o),"click"===l&&(o=!1!==a.openAmountDialog()&&o),o}),M.d,M.b)),t.sb(4,180224,null,0,w.b,[t.k,D.f,[2,P.a]],null,null),t.sb(5,212992,null,0,k.d,[S.c,t.k,O.b,t.P,t.z,I.a,D.c,D.f,k.b,[2,T.b],[2,k.a],[2,L.f]],{message:[0,"message"]},null),(n()(),t.tb(6,0,null,0,1,"mat-icon",[["class","mat-icon notranslate"],["role","img"],["svgIcon","edit"]],[[2,"mat-icon-inline",null],[2,"mat-icon-no-color",null]],null,null,U.b,U.a)),t.sb(7,9158656,null,0,E.b,[t.k,E.d,[8,null],[2,E.a],[2,t.m]],{svgIcon:[0,"svgIcon"]},null),(n()(),t.jb(16777216,null,null,1,null,dn)),t.sb(9,16384,null,0,Y.m,[t.P,t.M],{ngIf:[0,"ngIf"]},null)],(function(n,l){var e=l.component;n(l,5,0,"add"),n(l,7,0,"edit"),n(l,9,0,e.mod.extentAmount)}),(function(n,l){n(l,3,0,t.Fb(l,4).disabled||null,"NoopAnimations"===t.Fb(l,4)._animationMode),n(l,6,0,t.Fb(l,7).inline,"primary"!==t.Fb(l,7).color&&"accent"!==t.Fb(l,7).color&&"warn"!==t.Fb(l,7).color)}))}function bn(n){return t.Pb(0,[(n()(),t.tb(0,0,null,null,54,"div",[["class","code-form-container"]],null,null,null,null,null)),(n()(),t.jb(16777216,null,null,1,null,un)),t.sb(2,16384,null,0,Y.m,[t.P,t.M],{ngIf:[0,"ngIf"]},null),(n()(),t.tb(3,0,null,null,5,"div",[["class","delete-container"]],null,null,null,null,null)),(n()(),t.tb(4,16777216,null,null,4,"button",[["mat-icon-button",""],["matTooltip","Delete code"]],[[1,"disabled",0],[2,"_mat-animation-noopable",null]],[[null,"click"],[null,"longpress"],[null,"keydown"],[null,"touchend"]],(function(n,l,e){var o=!0,a=n.component;return"longpress"===l&&(o=!1!==t.Fb(n,6).show()&&o),"keydown"===l&&(o=!1!==t.Fb(n,6)._handleKeydown(e)&&o),"touchend"===l&&(o=!1!==t.Fb(n,6)._handleTouchend()&&o),"click"===l&&(o=!1!==a.deleteMod()&&o),o}),M.d,M.b)),t.sb(5,180224,null,0,w.b,[t.k,D.f,[2,P.a]],null,null),t.sb(6,212992,null,0,k.d,[S.c,t.k,O.b,t.P,t.z,I.a,D.c,D.f,k.b,[2,T.b],[2,k.a],[2,L.f]],{message:[0,"message"]},null),(n()(),t.tb(7,0,null,0,1,"mat-icon",[["class","mat-icon notranslate"],["role","img"],["svgIcon","delete_forever"]],[[2,"mat-icon-inline",null],[2,"mat-icon-no-color",null]],null,null,U.b,U.a)),t.sb(8,9158656,null,0,E.b,[t.k,E.d,[8,null],[2,E.a],[2,t.m]],{svgIcon:[0,"svgIcon"]},null),(n()(),t.tb(9,0,null,null,3,"div",[["class","flex-column related-substance"]],null,null,null,null,null)),(n()(),t.tb(10,0,null,null,2,"div",[["class","related-holder"]],null,null,null,null,null)),(n()(),t.tb(11,0,null,null,1,"app-substance-selector",[["eventCategory","substanceRelationshipRelatedSub"],["header","Molecular Fragment"],["placeholder","Molecular Fragment"]],null,[[null,"selectionUpdated"]],(function(n,l,e){var t=!0;return"selectionUpdated"===l&&(t=!1!==n.component.relatedSubstanceUpdated(e)&&t),t}),K.b,K.a)),t.sb(12,114688,null,0,G.a,[V.a],{eventCategory:[0,"eventCategory"],placeholder:[1,"placeholder"],header:[2,"header"],subuuid:[3,"subuuid"]},{selectionUpdated:"selectionUpdated"}),(n()(),t.tb(13,0,null,null,41,"div",[["class","flex-column form-content"]],null,null,null,null,null)),(n()(),t.tb(14,0,null,null,6,"div",[["class","form-row"]],null,null,null,null,null)),(n()(),t.tb(15,0,null,null,1,"app-cv-input",[["key","Structural Modification Structural Modification Type"],["title","Modification Type"]],null,[[null,"valueChange"]],(function(n,l,e){var t=!0;return"valueChange"===l&&(t=!1!==(n.component.mod.structuralModificationType=e)&&t),t}),Z.b,Z.a)),t.sb(16,245760,null,0,H.a,[J.a,$.e,X.a,S.e,W.a,B.a],{title:[0,"title"],key:[1,"key"],model:[2,"model"]},{valueChange:"valueChange"}),(n()(),t.jb(16777216,null,null,1,null,rn)),t.sb(18,16384,null,0,Y.m,[t.P,t.M],{ngIf:[0,"ngIf"]},null),(n()(),t.jb(16777216,null,null,1,null,cn)),t.sb(20,16384,null,0,Y.m,[t.P,t.M],{ngIf:[0,"ngIf"]},null),(n()(),t.tb(21,0,null,null,30,"div",[["class","form-row"]],null,null,null,null,null)),(n()(),t.tb(22,0,null,null,1,"app-cv-input",[["title","Extent"]],null,[[null,"valueChange"]],(function(n,l,e){var t=!0;return"valueChange"===l&&(t=!1!==(n.component.mod.extent=e)&&t),t}),Z.b,Z.a)),t.sb(23,245760,null,0,H.a,[J.a,$.e,X.a,S.e,W.a,B.a],{vocabulary:[0,"vocabulary"],title:[1,"title"],model:[2,"model"]},{valueChange:"valueChange"}),(n()(),t.tb(24,0,null,null,1,"app-cv-input",[["title","Location"]],null,[[null,"valueChange"]],(function(n,l,e){var t=!0;return"valueChange"===l&&(t=!1!==(n.component.mod.locationType=e)&&t),t}),Z.b,Z.a)),t.sb(25,245760,null,0,H.a,[J.a,$.e,X.a,S.e,W.a,B.a],{vocabulary:[0,"vocabulary"],title:[1,"title"],model:[2,"model"]},{valueChange:"valueChange"}),(n()(),t.tb(26,0,null,null,25,"div",[["class","group-access"]],null,null,null,null,null)),(n()(),t.tb(27,0,null,null,24,"div",[["class","form-row"]],null,null,null,null,null)),(n()(),t.tb(28,0,null,null,20,"mat-form-field",[["class","group mat-form-field"]],[[2,"mat-form-field-appearance-standard",null],[2,"mat-form-field-appearance-fill",null],[2,"mat-form-field-appearance-outline",null],[2,"mat-form-field-appearance-legacy",null],[2,"mat-form-field-invalid",null],[2,"mat-form-field-can-float",null],[2,"mat-form-field-should-float",null],[2,"mat-form-field-has-label",null],[2,"mat-form-field-hide-placeholder",null],[2,"mat-form-field-disabled",null],[2,"mat-form-field-autofilled",null],[2,"mat-focused",null],[2,"mat-accent",null],[2,"mat-warn",null],[2,"ng-untouched",null],[2,"ng-touched",null],[2,"ng-pristine",null],[2,"ng-dirty",null],[2,"ng-valid",null],[2,"ng-invalid",null],[2,"ng-pending",null],[2,"_mat-animation-noopable",null]],null,null,j.b,j.a)),t.sb(29,7520256,null,9,A.c,[t.k,t.h,[2,N.j],[2,T.b],[2,A.a],I.a,t.z,[2,P.a]],null,null),t.Lb(603979776,10,{_controlNonStatic:0}),t.Lb(335544320,11,{_controlStatic:0}),t.Lb(603979776,12,{_labelChildNonStatic:0}),t.Lb(335544320,13,{_labelChildStatic:0}),t.Lb(603979776,14,{_placeholderChild:0}),t.Lb(603979776,15,{_errorChildren:1}),t.Lb(603979776,16,{_hintChildren:1}),t.Lb(603979776,17,{_prefixChildren:1}),t.Lb(603979776,18,{_suffixChildren:1}),(n()(),t.tb(39,0,null,1,9,"input",[["class","mat-input-element mat-form-field-autofill-control"],["matInput",""],["name","group"],["placeholder","Group"],["required",""]],[[1,"required",0],[2,"ng-untouched",null],[2,"ng-touched",null],[2,"ng-pristine",null],[2,"ng-dirty",null],[2,"ng-valid",null],[2,"ng-invalid",null],[2,"ng-pending",null],[2,"mat-input-server",null],[1,"id",0],[1,"placeholder",0],[8,"disabled",0],[8,"required",0],[1,"readonly",0],[1,"aria-describedby",0],[1,"aria-invalid",0],[1,"aria-required",0]],[[null,"ngModelChange"],[null,"input"],[null,"blur"],[null,"compositionstart"],[null,"compositionend"],[null,"focus"]],(function(n,l,e){var o=!0,a=n.component;return"input"===l&&(o=!1!==t.Fb(n,40)._handleInput(e.target.value)&&o),"blur"===l&&(o=!1!==t.Fb(n,40).onTouched()&&o),"compositionstart"===l&&(o=!1!==t.Fb(n,40)._compositionStart()&&o),"compositionend"===l&&(o=!1!==t.Fb(n,40)._compositionEnd(e.target.value)&&o),"blur"===l&&(o=!1!==t.Fb(n,47)._focusChanged(!1)&&o),"focus"===l&&(o=!1!==t.Fb(n,47)._focusChanged(!0)&&o),"input"===l&&(o=!1!==t.Fb(n,47)._onInput()&&o),"ngModelChange"===l&&(o=!1!==(a.mod.modificationGroup=e)&&o),o}),null,null)),t.sb(40,16384,null,0,q.d,[t.E,t.k,[2,q.a]],null,null),t.sb(41,16384,null,0,q.t,[],{required:[0,"required"]},null),t.Kb(1024,null,q.l,(function(n){return[n]}),[q.t]),t.Kb(1024,null,q.m,(function(n){return[n]}),[q.d]),t.sb(44,671744,null,0,q.r,[[8,null],[6,q.l],[8,null],[6,q.m]],{name:[0,"name"],model:[1,"model"]},{update:"ngModelChange"}),t.Kb(2048,null,q.n,null,[q.r]),t.sb(46,16384,null,0,q.o,[[4,q.n]],null,null),t.sb(47,999424,null,0,z.a,[t.k,I.a,[6,q.n],[2,q.q],[2,q.j],N.d,[8,null],R.a,t.z],{placeholder:[0,"placeholder"],required:[1,"required"]},null),t.Kb(2048,[[10,4],[11,4]],A.d,null,[z.a]),(n()(),t.tb(49,0,null,null,2,"div",[["class","access"]],null,null,null,null,null)),(n()(),t.tb(50,0,null,null,1,"app-access-manager",[],null,[[null,"accessOut"]],(function(n,l,e){var t=!0;return"accessOut"===l&&(t=!1!==n.component.updateAccess(e)&&t),t}),Q.b,Q.a)),t.sb(51,4308992,null,0,nn.a,[J.a,t.k],{access:[0,"access"]},{accessOut:"accessOut"}),(n()(),t.tb(52,0,null,null,2,"div",[["class","form-row"]],null,null,null,null,null)),(n()(),t.jb(16777216,null,null,1,null,sn)),t.sb(54,16384,null,0,Y.m,[t.P,t.M],{ngIf:[0,"ngIf"]},null)],(function(n,l){var e=l.component;n(l,2,0,e.mod.$$deletedCode),n(l,6,0,"Delete code"),n(l,8,0,"delete_forever"),n(l,12,0,"substanceRelationshipRelatedSub","Molecular Fragment","Molecular Fragment",e.relatedSubstanceUuid),n(l,16,0,"Modification Type","Structural Modification Structural Modification Type",e.mod.structuralModificationType),n(l,18,0,"RESIDUE_SPECIFIC"!==e.mod.locationType),n(l,20,0,"RESIDUE_SPECIFIC"===e.mod.locationType),n(l,23,0,e.modExtentList,"Extent",e.mod.extent),n(l,25,0,e.modLocationList,"Location",e.mod.locationType),n(l,41,0,""),n(l,44,0,"group",e.mod.modificationGroup),n(l,47,0,"Group",""),n(l,51,0,e.mod.access),n(l,54,0,"PARTIAL"==e.mod.extent)}),(function(n,l){n(l,4,0,t.Fb(l,5).disabled||null,"NoopAnimations"===t.Fb(l,5)._animationMode),n(l,7,0,t.Fb(l,8).inline,"primary"!==t.Fb(l,8).color&&"accent"!==t.Fb(l,8).color&&"warn"!==t.Fb(l,8).color),n(l,28,1,["standard"==t.Fb(l,29).appearance,"fill"==t.Fb(l,29).appearance,"outline"==t.Fb(l,29).appearance,"legacy"==t.Fb(l,29).appearance,t.Fb(l,29)._control.errorState,t.Fb(l,29)._canLabelFloat,t.Fb(l,29)._shouldLabelFloat(),t.Fb(l,29)._hasFloatingLabel(),t.Fb(l,29)._hideControlPlaceholder(),t.Fb(l,29)._control.disabled,t.Fb(l,29)._control.autofilled,t.Fb(l,29)._control.focused,"accent"==t.Fb(l,29).color,"warn"==t.Fb(l,29).color,t.Fb(l,29)._shouldForward("untouched"),t.Fb(l,29)._shouldForward("touched"),t.Fb(l,29)._shouldForward("pristine"),t.Fb(l,29)._shouldForward("dirty"),t.Fb(l,29)._shouldForward("valid"),t.Fb(l,29)._shouldForward("invalid"),t.Fb(l,29)._shouldForward("pending"),!t.Fb(l,29)._animationsEnabled]),n(l,39,1,[t.Fb(l,41).required?"":null,t.Fb(l,46).ngClassUntouched,t.Fb(l,46).ngClassTouched,t.Fb(l,46).ngClassPristine,t.Fb(l,46).ngClassDirty,t.Fb(l,46).ngClassValid,t.Fb(l,46).ngClassInvalid,t.Fb(l,46).ngClassPending,t.Fb(l,47)._isServer,t.Fb(l,47).id,t.Fb(l,47).placeholder,t.Fb(l,47).disabled,t.Fb(l,47).required,t.Fb(l,47).readonly&&!t.Fb(l,47)._isNativeSelect||null,t.Fb(l,47)._ariaDescribedby||null,t.Fb(l,47).errorState,t.Fb(l,47).required.toString()])}))}var mn=e("mrSG"),pn=function(n){function l(l,e,t){var o=n.call(this,t)||this;return o.substanceFormService=l,o.scrollToService=e,o.gaService=t,o.subscriptions=[],o.analyticsEventCategory="substance form structural modifications",o}return mn.b(l,n),l.prototype.ngOnInit=function(){this.canAddItemUpdate.emit(!0),this.menuLabelUpdate.emit("Structural Modifications")},l.prototype.ngAfterViewInit=function(){var n=this,l=this.substanceFormService.substanceStructuralModifications.subscribe((function(l){n.modifications=l}));this.subscriptions.push(l)},l.prototype.ngOnDestroy=function(){this.componentDestroyed.emit(),this.subscriptions.forEach((function(n){n.unsubscribe()}))},l.prototype.addItem=function(){this.addStructuralModification()},l.prototype.addStructuralModification=function(){var n=this;this.substanceFormService.addSubstanceStructuralModification(),setTimeout((function(){n.scrollToService.scrollToElement("substance-structural-modification-0","center")}))},l.prototype.deleteStructuralModification=function(n){this.substanceFormService.deleteSubstanceStructuralModification(n)},l}(e("xhaW").a),fn=e("HECD"),gn=t.rb({encapsulation:0,styles:[[".mat-divider.mat-divider-inset[_ngcontent-%COMP%]{margin-left:0}.mat-divider[_ngcontent-%COMP%]{border-top-color:rgba(0,0,0,.12)}.code[_ngcontent-%COMP%]:nth-child(odd){background-color:rgba(68,138,255,.07)}.code[_ngcontent-%COMP%]:nth-child(odd)     .mat-expansion-panel:not(.mat-expanded):not([aria-disabled=true]) .mat-expansion-panel-header:hover{background-color:rgba(68,138,255,.15)}.code[_ngcontent-%COMP%]:nth-child(even)     .mat-expansion-panel:not(.mat-expanded):not([aria-disabled=true]) .mat-expansion-panel-header:hover{background-color:rgba(128,128,128,.15)}.code[_ngcontent-%COMP%]     .mat-expansion-panel, .code[_ngcontent-%COMP%]     .mat-table, .code[_ngcontent-%COMP%]     textarea{background-color:transparent}.search[_ngcontent-%COMP%]{width:400px;max-width:100%}"]],data:{}});function hn(n){return t.Pb(0,[(n()(),t.tb(0,0,null,null,1,"mat-divider",[["class","form-divider mat-divider"],["role","separator"]],[[1,"aria-orientation",0],[2,"mat-divider-vertical",null],[2,"mat-divider-horizontal",null],[2,"mat-divider-inset",null]],null,null,y.b,y.a)),t.sb(1,49152,null,0,_.a,[],{inset:[0,"inset"]},null)],(function(n,l){n(l,1,0,!0)}),(function(n,l){n(l,0,0,t.Fb(l,1).vertical?"vertical":"horizontal",t.Fb(l,1).vertical,!t.Fb(l,1).vertical,t.Fb(l,1).inset)}))}function vn(n){return t.Pb(0,[(n()(),t.tb(0,0,null,null,5,"div",[["appScrollToTarget",""],["class","alternate-backgrounds"]],[[8,"id",0]],null,null,null,null)),t.sb(1,4341760,null,0,C.a,[t.k,x.a],null,null),(n()(),t.tb(2,0,null,null,1,"app-structural-modification-form",[],null,[[null,"modDeleted"]],(function(n,l,e){var t=!0;return"modDeleted"===l&&(t=!1!==n.component.deleteStructuralModification(e)&&t),t}),bn,an)),t.sb(3,4308992,null,0,tn,[J.a,$.e,X.a,S.e,on.a],{mod:[0,"mod"]},{modDeleted:"modDeleted"}),(n()(),t.jb(16777216,null,null,1,null,hn)),t.sb(5,16384,null,0,Y.m,[t.P,t.M],{ngIf:[0,"ngIf"]},null)],(function(n,l){n(l,3,0,l.context.$implicit),n(l,5,0,!l.context.last)}),(function(n,l){n(l,0,0,"substance-structural-modification-"+l.context.index)}))}function Fn(n){return t.Pb(0,[(n()(),t.tb(0,0,null,null,1,"div",[["class","flex-row"]],null,null,null,null,null)),(n()(),t.tb(1,0,null,null,0,"span",[["class","middle-fill"]],null,null,null,null,null)),(n()(),t.jb(16777216,null,null,1,null,vn)),t.sb(3,278528,null,0,Y.l,[t.P,t.M,t.s],{ngForOf:[0,"ngForOf"]},null)],(function(n,l){n(l,3,0,l.component.modifications)}),null)}function yn(n){return t.Pb(0,[(n()(),t.tb(0,0,null,null,1,"app-substance-form-structural-modifications",[],null,null,null,Fn,gn)),t.sb(1,4440064,null,0,pn,[on.a,x.a,fn.a],null,null)],(function(n,l){n(l,1,0)}),null)}var _n=t.pb("app-substance-form-structural-modifications",pn,yn,{},{menuLabelUpdate:"menuLabelUpdate",hiddenStateUpdate:"hiddenStateUpdate",canAddItemUpdate:"canAddItemUpdate",componentDestroyed:"componentDestroyed"},[]),Cn=e("M2Lx"),xn=e("mVsa"),Mn=e("uGex"),wn=e("4tE/"),Dn=e("4epT"),Pn=e("EtvR"),kn=e("4c35"),Sn=e("de3e"),On=e("La40"),In=e("/dO6"),Tn=e("NYLF"),Ln=e("y4qS"),Un=e("BHnd"),En=e("YhbO"),jn=e("jlZm"),An=e("6Wmm"),Nn=e("9It4"),qn=e("PnCX"),zn=e("IyAz"),Rn=e("ZYCi"),Yn=e("5uHe"),Kn=e("vfGX"),Gn=e("0/Q6"),Vn=e("jS4w"),Zn=e("u7R8"),Hn=e("NnTW"),Jn=e("7fs6"),$n=e("YSh2"),Xn=e("6jyQ");e.d(l,"SubstanceFormStructuralModificationsModuleNgFactory",(function(){return Wn}));var Wn=t.qb(o,[],(function(n){return t.Cb([t.Db(512,t.j,t.bb,[[8,[a.a,i.a,u.a,r.a,c.a,d.a,s.a,b.b,m.a,p.a,f.a,g.a,h.a,v.a,F.a,_n]],[3,t.j],t.x]),t.Db(4608,Y.o,Y.n,[t.u,[2,Y.E]]),t.Db(4608,q.e,q.e,[]),t.Db(4608,q.w,q.w,[]),t.Db(4608,Cn.c,Cn.c,[]),t.Db(4608,S.c,S.c,[S.i,S.e,t.j,S.h,S.f,t.r,t.z,Y.d,T.b,[2,Y.i]]),t.Db(5120,S.j,S.k,[S.c]),t.Db(5120,xn.c,xn.j,[S.c]),t.Db(5120,k.b,k.c,[S.c]),t.Db(4608,L.e,N.e,[[2,N.i],[2,N.n]]),t.Db(5120,Mn.a,Mn.b,[S.c]),t.Db(4608,N.d,N.d,[]),t.Db(5120,wn.b,wn.c,[S.c]),t.Db(5120,$.c,$.d,[S.c]),t.Db(135680,$.e,$.e,[S.c,t.r,[2,Y.i],[2,$.b],$.c,[3,$.e],S.e]),t.Db(5120,Dn.c,Dn.a,[[3,Dn.c]]),t.Db(1073742336,Y.c,Y.c,[]),t.Db(1073742336,Pn.a,Pn.a,[]),t.Db(1073742336,q.v,q.v,[]),t.Db(1073742336,q.s,q.s,[]),t.Db(1073742336,q.k,q.k,[]),t.Db(1073742336,Cn.d,Cn.d,[]),t.Db(1073742336,A.e,A.e,[]),t.Db(1073742336,T.a,T.a,[]),t.Db(1073742336,N.n,N.n,[[2,N.f],[2,L.f]]),t.Db(1073742336,I.b,I.b,[]),t.Db(1073742336,N.x,N.x,[]),t.Db(1073742336,kn.g,kn.g,[]),t.Db(1073742336,O.c,O.c,[]),t.Db(1073742336,S.g,S.g,[]),t.Db(1073742336,xn.i,xn.i,[]),t.Db(1073742336,xn.f,xn.f,[]),t.Db(1073742336,Sn.d,Sn.d,[]),t.Db(1073742336,Sn.c,Sn.c,[]),t.Db(1073742336,w.c,w.c,[]),t.Db(1073742336,E.c,E.c,[]),t.Db(1073742336,D.a,D.a,[]),t.Db(1073742336,k.e,k.e,[]),t.Db(1073742336,On.j,On.j,[]),t.Db(1073742336,_.b,_.b,[]),t.Db(1073742336,N.v,N.v,[]),t.Db(1073742336,N.s,N.s,[]),t.Db(1073742336,Mn.d,Mn.d,[]),t.Db(1073742336,R.c,R.c,[]),t.Db(1073742336,z.b,z.b,[]),t.Db(1073742336,In.f,In.f,[]),t.Db(1073742336,wn.e,wn.e,[]),t.Db(1073742336,Tn.a,Tn.a,[]),t.Db(1073742336,$.k,$.k,[]),t.Db(1073742336,Ln.p,Ln.p,[]),t.Db(1073742336,Un.m,Un.m,[]),t.Db(1073742336,En.c,En.c,[]),t.Db(1073742336,jn.d,jn.d,[]),t.Db(1073742336,An.b,An.b,[]),t.Db(1073742336,Nn.d,Nn.d,[]),t.Db(1073742336,qn.a,qn.a,[]),t.Db(1073742336,zn.a,zn.a,[]),t.Db(1073742336,Rn.p,Rn.p,[[2,Rn.u],[2,Rn.m]]),t.Db(1073742336,Yn.a,Yn.a,[]),t.Db(1073742336,Kn.a,Kn.a,[]),t.Db(1073742336,N.o,N.o,[]),t.Db(1073742336,Gn.d,Gn.d,[]),t.Db(1073742336,Vn.b,Vn.b,[]),t.Db(1073742336,Zn.e,Zn.e,[]),t.Db(1073742336,Hn.b,Hn.b,[]),t.Db(1073742336,Jn.a,Jn.a,[]),t.Db(1073742336,Dn.d,Dn.d,[]),t.Db(1073742336,o,o,[]),t.Db(256,In.a,{separatorKeyCodes:[$n.f]},[]),t.Db(1024,Rn.j,(function(){return[[]]}),[]),t.Db(256,Xn.a,pn,[])])}))}}]);
//# sourceMappingURL=45.82896bd91af355eed1d2.js.map