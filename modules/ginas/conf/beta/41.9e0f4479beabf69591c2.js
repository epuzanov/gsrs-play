(window.webpackJsonp=window.webpackJsonp||[]).push([[41],{bBkp:function(n,l,e){"use strict";e.d(l,"a",(function(){return o}));var t=e("CcnG"),o=function(){return function(){this.menuLabelUpdate=new t.n,this.hiddenStateUpdate=new t.n,this.canAddItemUpdate=new t.n,this.componentDestroyed=new t.n}}()},jAYK:function(n,l,e){"use strict";e.r(l);var t=e("CcnG"),o=function(){return function(){}}(),a=e("NcP4"),i=e("t68o"),c=e("pMnS"),r=e("HvtJ"),u=e("/J3S"),d=e("R/n8"),s=e("ThfK"),b=e("ldJ0"),m=e("OvbY"),p=e("Ok+c"),f=e("Pj+I"),g=e("Cka/"),h=e("UMU1"),x=e("dCG0"),v=e("B/2v"),y=e("TtEo"),_=e("LC5p"),C=e("xZkp"),w=e("hifq"),M=e("bujt"),P=e("UodH"),k=e("lLAP"),F=e("wFw1"),D=e("v9Dh"),O=e("eDkP"),I=e("qAlS"),S=e("dWZg"),j=e("Fzqc"),L=e("ZYjt"),T=e("Mr+X"),N=e("SMsm"),U=e("Ip0R"),z=e("s7Fu"),A=e("khmc"),q=e("YLZ7"),E=e("o3x0"),R=e("6E2U"),K=e("4S5B"),Y=e("Vurf"),$=e("dJrM"),G=e("seP3"),H=e("Wf4p"),V=e("gIcY"),J=e("b716"),Z=e("/VYK"),W=e("o6iZ"),B=function(){function n(n,l,e,o,a){this.cvService=n,this.dialog=l,this.utilsService=e,this.overlayContainerService=o,this.substanceFormService=a,this.modDeleted=new t.n,this.modTypeList=[],this.modRoleList=[],this.modProcessList=[],this.subscriptions=[]}return n.prototype.ngOnInit=function(){this.getVocabularies(),this.overlayContainer=this.overlayContainerService.getContainerElement()},Object.defineProperty(n.prototype,"mod",{get:function(){return this.privateMod},set:function(n){this.privateMod=n},enumerable:!0,configurable:!0}),n.prototype.getVocabularies=function(){var n=this;this.cvService.getDomainVocabulary("PHYSICAL_MODIFICATION_ROLE").subscribe((function(l){n.modRoleList=l.PHYSICAL_MODIFICATION_ROLE.list}))},n.prototype.deleteMod=function(){var n=this;this.privateMod.$$deletedCode=this.utilsService.newUUID(),this.privateMod||(this.deleteTimer=setTimeout((function(){n.modDeleted.emit(n.mod),n.substanceFormService.emitOtherLinkUpdate()}),1e3))},n.prototype.undoDelete=function(){clearTimeout(this.deleteTimer),delete this.privateMod.$$deletedCode},n.prototype.openParameterDialog=function(){var n=this;this.mod.parameters||(this.mod.parameters=[]);var l=this.dialog.open(W.a,{data:this.mod.parameters,width:"1080px"});this.overlayContainer.style.zIndex="1002";var e=l.afterClosed().subscribe((function(l){n.overlayContainer.style.zIndex=null,l&&(n.mod.parameters=l)}));this.subscriptions.push(e)},n.prototype.displayAmount=function(n){return this.utilsService.displayAmount(n)},n}(),X=e("Jj5M"),Q=t.rb({encapsulation:0,styles:[['.physical-form-container[_ngcontent-%COMP%]{padding:30px 10px 12px;position:relative}.form-sub-row[_ngcontent-%COMP%]{max-width:90%}.related-substance[_ngcontent-%COMP%]{max-width:175px}.related-substance[_ngcontent-%COMP%]   img[_ngcontent-%COMP%]{max-width:150px}.form-row[_ngcontent-%COMP%]   .code-system[_ngcontent-%COMP%], .form-row[_ngcontent-%COMP%]   .code-system-type[_ngcontent-%COMP%], .form-row[_ngcontent-%COMP%]   .type[_ngcontent-%COMP%]{-webkit-box-flex:1;-ms-flex-positive:1;flex-grow:1;padding-right:15px}.form-row[_ngcontent-%COMP%]   .code-text[_ngcontent-%COMP%], .form-row[_ngcontent-%COMP%]   .url[_ngcontent-%COMP%]{-webkit-box-flex:1;-ms-flex-positive:1;flex-grow:1}.key-value-pair[_ngcontent-%COMP%]{display:-webkit-box;display:-ms-flexbox;display:flex;-webkit-box-orient:vertical;-webkit-box-direction:normal;-ms-flex-direction:column;flex-direction:column;-ms-flex-item-align:start;align-self:flex-start}.key-value-pair[_ngcontent-%COMP%]   .key[_ngcontent-%COMP%]{font-size:11px;padding-bottom:3.5px;line-height:11px;color:rgba(0,0,0,.54);font-weight:400;font-family:Roboto,"Helvetica Neue",sans-serif}.key-value-pair[_ngcontent-%COMP%]   .value[_ngcontent-%COMP%]{font-size:15.5px}.form-row[_ngcontent-%COMP%]{display:-webkit-box;display:-ms-flexbox;display:flex;-webkit-box-pack:justify;-ms-flex-pack:justify;justify-content:space-between;padding:0 10px;-webkit-box-align:end;-ms-flex-align:end;align-items:flex-end}.form-actions[_ngcontent-%COMP%]{-webkit-box-pack:start;-ms-flex-pack:start;justify-content:flex-start;margin:5px 0 10px}.form-content[_ngcontent-%COMP%]{-webkit-box-flex:1;-ms-flex-positive:1;flex-grow:1}.group-access[_ngcontent-%COMP%], .location-type[_ngcontent-%COMP%], .mod-type[_ngcontent-%COMP%], .sites[_ngcontent-%COMP%]{width:33%}.amount[_ngcontent-%COMP%], .extent[_ngcontent-%COMP%]{width:40%}.group[_ngcontent-%COMP%]{width:75px}.type[_ngcontent-%COMP%]{max-width:225px}.access[_ngcontent-%COMP%]{width:30%}.name-form-container[_ngcontent-%COMP%]{padding:30px 10px 12px;position:relative}.notification-backdrop[_ngcontent-%COMP%]{position:absolute;top:0;right:0;bottom:0;left:0;display:-webkit-box;display:-ms-flexbox;display:flex;z-index:10;background-color:rgba(255,255,255,.8);-webkit-box-pack:center;-ms-flex-pack:center;justify-content:center;-webkit-box-align:center;-ms-flex-align:center;align-items:center;font-size:30px;font-weight:700;color:#666}.form-row[_ngcontent-%COMP%]{display:-webkit-box;display:-ms-flexbox;display:flex;-webkit-box-pack:justify;-ms-flex-pack:justify;justify-content:space-between;-webkit-box-align:end;-ms-flex-align:end;align-items:flex-end}.form-row[_ngcontent-%COMP%]   .delete-container[_ngcontent-%COMP%]{padding:0 10px 8px 0}.form-row[_ngcontent-%COMP%]   .checkbox-container[_ngcontent-%COMP%], .form-row[_ngcontent-%COMP%]   .radio-container[_ngcontent-%COMP%]{padding-bottom:18px;padding-right:15px}.form-row[_ngcontent-%COMP%]   .amount[_ngcontent-%COMP%], .form-row[_ngcontent-%COMP%]   .domains[_ngcontent-%COMP%], .form-row[_ngcontent-%COMP%]   .name[_ngcontent-%COMP%], .form-row[_ngcontent-%COMP%]   .param-display[_ngcontent-%COMP%], .form-row[_ngcontent-%COMP%]   .tags[_ngcontent-%COMP%], .form-row[_ngcontent-%COMP%]   .type[_ngcontent-%COMP%]{-webkit-box-flex:1;-ms-flex-positive:1;flex-grow:1;padding-right:15px}.form-row[_ngcontent-%COMP%]   .jurisdiction[_ngcontent-%COMP%]{-webkit-box-flex:1;-ms-flex-positive:1;flex-grow:1}.nameorgs-container[_ngcontent-%COMP%], .references-container[_ngcontent-%COMP%]{width:100%}.column-radio[_ngcontent-%COMP%]     .mat-radio-label{-webkit-box-orient:vertical;-webkit-box-direction:reverse;-ms-flex-direction:column-reverse;flex-direction:column-reverse}.column-radio[_ngcontent-%COMP%]     .mat-radio-label-content{padding-left:0;font-size:11px;padding-bottom:4px;color:rgba(0,0,0,.54);font-weight:400;font-family:Roboto,"Helvetica Neue",sans-serif}.column-checkbox[_ngcontent-%COMP%]     .mat-checkbox-layout{-webkit-box-orient:vertical;-webkit-box-direction:reverse;-ms-flex-direction:column-reverse;flex-direction:column-reverse;-webkit-box-align:center;-ms-flex-align:center;align-items:center}.column-checkbox[_ngcontent-%COMP%]     .mat-checkbox-inner-container{margin-right:unset;margin-left:unset}.column-checkbox[_ngcontent-%COMP%]     .mat-checkbox-layout .mat-checkbox-label{padding-left:0;font-size:11px;padding-bottom:2px;color:rgba(0,0,0,.54);font-weight:400;font-family:Roboto,"Helvetica Neue",sans-serif}.amount[_ngcontent-%COMP%]{display:-webkit-box;display:-ms-flexbox;display:flex;padding-bottom:10px}.amt-label[_ngcontent-%COMP%]{padding-top:11px}.param-display[_ngcontent-%COMP%]{padding-top:10px;width:100%;display:-webkit-box;display:-ms-flexbox;display:flex;-webkit-box-orient:vertical;-webkit-box-direction:normal;-ms-flex-direction:column;flex-direction:column}.param-container[_ngcontent-%COMP%]{width:100%;display:-webkit-box;display:-ms-flexbox;display:flex;-webkit-box-pack:justify;-ms-flex-pack:justify;justify-content:space-between;-webkit-box-align:end;-ms-flex-align:end;align-items:flex-end;padding-bottom:7px;padding-top:7px}.param-container[_ngcontent-%COMP%]   .param-display[_ngcontent-%COMP%]{max-width:40%;padding-right:15px}.param-container[_ngcontent-%COMP%]   .param-amount[_ngcontent-%COMP%]{max-width:60%}']],data:{}});function nn(n){return t.Pb(0,[(n()(),t.tb(0,0,null,null,6,"div",[["class","notification-backdrop"]],null,null,null,null,null)),(n()(),t.Nb(-1,null,[" Deleted  "])),(n()(),t.tb(2,16777216,null,null,4,"button",[["mat-icon-button",""],["matTooltip","Undo"]],[[1,"disabled",0],[2,"_mat-animation-noopable",null]],[[null,"click"],[null,"longpress"],[null,"keydown"],[null,"touchend"]],(function(n,l,e){var o=!0,a=n.component;return"longpress"===l&&(o=!1!==t.Fb(n,4).show()&&o),"keydown"===l&&(o=!1!==t.Fb(n,4)._handleKeydown(e)&&o),"touchend"===l&&(o=!1!==t.Fb(n,4)._handleTouchend()&&o),"click"===l&&(o=!1!==a.undoDelete()&&o),o}),M.d,M.b)),t.sb(3,180224,null,0,P.b,[t.k,k.f,[2,F.a]],null,null),t.sb(4,212992,null,0,D.d,[O.c,t.k,I.b,t.P,t.z,S.a,k.c,k.f,D.b,[2,j.b],[2,D.a],[2,L.f]],{message:[0,"message"]},null),(n()(),t.tb(5,0,null,0,1,"mat-icon",[["class","mat-icon notranslate"],["role","img"],["svgIcon","undo"]],[[2,"mat-icon-inline",null],[2,"mat-icon-no-color",null]],null,null,T.b,T.a)),t.sb(6,9158656,null,0,N.b,[t.k,N.d,[8,null],[2,N.a],[2,t.m]],{svgIcon:[0,"svgIcon"]},null)],(function(n,l){n(l,4,0,"Undo"),n(l,6,0,"undo")}),(function(n,l){n(l,2,0,t.Fb(l,3).disabled||null,"NoopAnimations"===t.Fb(l,3)._animationMode),n(l,5,0,t.Fb(l,6).inline,"primary"!==t.Fb(l,6).color&&"accent"!==t.Fb(l,6).color&&"warn"!==t.Fb(l,6).color)}))}function ln(n){return t.Pb(0,[(n()(),t.tb(0,0,null,null,1,"div",[["class","param-name"]],null,null,null,null,null)),(n()(),t.Nb(1,null,[""," "]))],null,(function(n,l){n(l,1,0,l.parent.context.$implicit.parameterName)}))}function en(n){return t.Pb(0,[(n()(),t.tb(0,0,null,null,1,"div",[["class","param-amount"]],null,null,null,null,null)),(n()(),t.Nb(1,null,["",""]))],null,(function(n,l){n(l,1,0,l.component.displayAmount(l.parent.context.$implicit.amount))}))}function tn(n){return t.Pb(0,[(n()(),t.tb(0,0,null,null,7,"div",[["class","param-container"]],null,null,null,null,null)),t.Kb(512,null,U.z,U.A,[t.s,t.t,t.k,t.E]),t.sb(2,278528,null,0,U.k,[U.z],{klass:[0,"klass"],ngClass:[1,"ngClass"]},null),t.Ib(3,{"bottom-border":0}),(n()(),t.jb(16777216,null,null,1,null,ln)),t.sb(5,16384,null,0,U.m,[t.P,t.M],{ngIf:[0,"ngIf"]},null),(n()(),t.jb(16777216,null,null,1,null,en)),t.sb(7,16384,null,0,U.m,[t.P,t.M],{ngIf:[0,"ngIf"]},null)],(function(n,l){var e=n(l,3,0,l.context.index<l.component.mod.parameters.length);n(l,2,0,"param-container",e),n(l,5,0,l.context.$implicit.parameterName),n(l,7,0,l.context.$implicit.amount)}),null)}function on(n){return t.Pb(0,[(n()(),t.tb(0,0,null,null,44,"div",[["class","physical-form-container"]],null,null,null,null,null)),(n()(),t.jb(16777216,null,null,1,null,nn)),t.sb(2,16384,null,0,U.m,[t.P,t.M],{ngIf:[0,"ngIf"]},null),(n()(),t.tb(3,0,null,null,41,"div",[["class","form-row"]],null,null,null,null,null)),(n()(),t.tb(4,0,null,null,5,"div",[["class","delete-container"]],null,null,null,null,null)),(n()(),t.tb(5,16777216,null,null,4,"button",[["mat-icon-button",""],["matTooltip","Delete name"]],[[1,"disabled",0],[2,"_mat-animation-noopable",null]],[[null,"click"],[null,"longpress"],[null,"keydown"],[null,"touchend"]],(function(n,l,e){var o=!0,a=n.component;return"longpress"===l&&(o=!1!==t.Fb(n,7).show()&&o),"keydown"===l&&(o=!1!==t.Fb(n,7)._handleKeydown(e)&&o),"touchend"===l&&(o=!1!==t.Fb(n,7)._handleTouchend()&&o),"click"===l&&(o=!1!==a.deleteMod()&&o),o}),M.d,M.b)),t.sb(6,180224,null,0,P.b,[t.k,k.f,[2,F.a]],null,null),t.sb(7,212992,null,0,D.d,[O.c,t.k,I.b,t.P,t.z,S.a,k.c,k.f,D.b,[2,j.b],[2,D.a],[2,L.f]],{message:[0,"message"]},null),(n()(),t.tb(8,0,null,0,1,"mat-icon",[["class","mat-icon notranslate"],["role","img"],["svgIcon","delete_forever"]],[[2,"mat-icon-inline",null],[2,"mat-icon-no-color",null]],null,null,T.b,T.a)),t.sb(9,9158656,null,0,N.b,[t.k,N.d,[8,null],[2,N.a],[2,t.m]],{svgIcon:[0,"svgIcon"]},null),(n()(),t.tb(10,0,null,null,1,"app-cv-input",[["class","type"],["title","Modification Role"]],null,[[null,"valueChange"]],(function(n,l,e){var t=!0;return"valueChange"===l&&(t=!1!==(n.component.mod.physicalModificationRole=e)&&t),t}),z.b,z.a)),t.sb(11,245760,null,0,A.a,[q.a,E.e,R.a,O.e,K.a,Y.a],{vocabulary:[0,"vocabulary"],title:[1,"title"],model:[2,"model"]},{valueChange:"valueChange"}),(n()(),t.tb(12,0,null,null,10,"div",[["class","amount"]],null,null,null,null,null)),(n()(),t.tb(13,0,null,null,1,"div",[["class","label amt-label"]],null,null,null,null,null)),(n()(),t.Nb(-1,null,[" Parameters "])),(n()(),t.tb(15,16777216,null,null,4,"button",[["mat-icon-button",""],["matTooltip","add / edit parameters"]],[[1,"disabled",0],[2,"_mat-animation-noopable",null]],[[null,"click"],[null,"longpress"],[null,"keydown"],[null,"touchend"]],(function(n,l,e){var o=!0,a=n.component;return"longpress"===l&&(o=!1!==t.Fb(n,17).show()&&o),"keydown"===l&&(o=!1!==t.Fb(n,17)._handleKeydown(e)&&o),"touchend"===l&&(o=!1!==t.Fb(n,17)._handleTouchend()&&o),"click"===l&&(o=!1!==a.openParameterDialog()&&o),o}),M.d,M.b)),t.sb(16,180224,null,0,P.b,[t.k,k.f,[2,F.a]],null,null),t.sb(17,212992,null,0,D.d,[O.c,t.k,I.b,t.P,t.z,S.a,k.c,k.f,D.b,[2,j.b],[2,D.a],[2,L.f]],{message:[0,"message"]},null),(n()(),t.tb(18,0,null,0,1,"mat-icon",[["class","mat-icon notranslate"],["role","img"],["svgIcon","add_circle_outline"]],[[2,"mat-icon-inline",null],[2,"mat-icon-no-color",null]],null,null,T.b,T.a)),t.sb(19,9158656,null,0,N.b,[t.k,N.d,[8,null],[2,N.a],[2,t.m]],{svgIcon:[0,"svgIcon"]},null),(n()(),t.tb(20,0,null,null,2,"div",[["class","param-display"]],null,null,null,null,null)),(n()(),t.jb(16777216,null,null,1,null,tn)),t.sb(22,278528,null,0,U.l,[t.P,t.M,t.s],{ngForOf:[0,"ngForOf"]},null),(n()(),t.tb(23,0,null,null,21,"div",[],null,null,null,null,null)),(n()(),t.tb(24,0,null,null,20,"mat-form-field",[["class","group mat-form-field"]],[[2,"mat-form-field-appearance-standard",null],[2,"mat-form-field-appearance-fill",null],[2,"mat-form-field-appearance-outline",null],[2,"mat-form-field-appearance-legacy",null],[2,"mat-form-field-invalid",null],[2,"mat-form-field-can-float",null],[2,"mat-form-field-should-float",null],[2,"mat-form-field-has-label",null],[2,"mat-form-field-hide-placeholder",null],[2,"mat-form-field-disabled",null],[2,"mat-form-field-autofilled",null],[2,"mat-focused",null],[2,"mat-accent",null],[2,"mat-warn",null],[2,"ng-untouched",null],[2,"ng-touched",null],[2,"ng-pristine",null],[2,"ng-dirty",null],[2,"ng-valid",null],[2,"ng-invalid",null],[2,"ng-pending",null],[2,"_mat-animation-noopable",null]],null,null,$.b,$.a)),t.sb(25,7520256,null,9,G.c,[t.k,t.h,[2,H.j],[2,j.b],[2,G.a],S.a,t.z,[2,F.a]],null,null),t.Lb(603979776,1,{_controlNonStatic:0}),t.Lb(335544320,2,{_controlStatic:0}),t.Lb(603979776,3,{_labelChildNonStatic:0}),t.Lb(335544320,4,{_labelChildStatic:0}),t.Lb(603979776,5,{_placeholderChild:0}),t.Lb(603979776,6,{_errorChildren:1}),t.Lb(603979776,7,{_hintChildren:1}),t.Lb(603979776,8,{_prefixChildren:1}),t.Lb(603979776,9,{_suffixChildren:1}),(n()(),t.tb(35,0,null,1,9,"input",[["class","mat-input-element mat-form-field-autofill-control"],["matInput",""],["name","group"],["placeholder","Group"],["required",""]],[[1,"required",0],[2,"ng-untouched",null],[2,"ng-touched",null],[2,"ng-pristine",null],[2,"ng-dirty",null],[2,"ng-valid",null],[2,"ng-invalid",null],[2,"ng-pending",null],[2,"mat-input-server",null],[1,"id",0],[1,"placeholder",0],[8,"disabled",0],[8,"required",0],[1,"readonly",0],[1,"aria-describedby",0],[1,"aria-invalid",0],[1,"aria-required",0]],[[null,"ngModelChange"],[null,"input"],[null,"blur"],[null,"compositionstart"],[null,"compositionend"],[null,"focus"]],(function(n,l,e){var o=!0,a=n.component;return"input"===l&&(o=!1!==t.Fb(n,36)._handleInput(e.target.value)&&o),"blur"===l&&(o=!1!==t.Fb(n,36).onTouched()&&o),"compositionstart"===l&&(o=!1!==t.Fb(n,36)._compositionStart()&&o),"compositionend"===l&&(o=!1!==t.Fb(n,36)._compositionEnd(e.target.value)&&o),"blur"===l&&(o=!1!==t.Fb(n,43)._focusChanged(!1)&&o),"focus"===l&&(o=!1!==t.Fb(n,43)._focusChanged(!0)&&o),"input"===l&&(o=!1!==t.Fb(n,43)._onInput()&&o),"ngModelChange"===l&&(o=!1!==(a.mod.modificationGroup=e)&&o),o}),null,null)),t.sb(36,16384,null,0,V.d,[t.E,t.k,[2,V.a]],null,null),t.sb(37,16384,null,0,V.t,[],{required:[0,"required"]},null),t.Kb(1024,null,V.l,(function(n){return[n]}),[V.t]),t.Kb(1024,null,V.m,(function(n){return[n]}),[V.d]),t.sb(40,671744,null,0,V.r,[[8,null],[6,V.l],[8,null],[6,V.m]],{name:[0,"name"],model:[1,"model"]},{update:"ngModelChange"}),t.Kb(2048,null,V.n,null,[V.r]),t.sb(42,16384,null,0,V.o,[[4,V.n]],null,null),t.sb(43,999424,null,0,J.a,[t.k,S.a,[6,V.n],[2,V.q],[2,V.j],H.d,[8,null],Z.a,t.z],{placeholder:[0,"placeholder"],required:[1,"required"]},null),t.Kb(2048,[[1,4],[2,4]],G.d,null,[J.a])],(function(n,l){var e=l.component;n(l,2,0,e.mod.$$deletedCode),n(l,7,0,"Delete name"),n(l,9,0,"delete_forever"),n(l,11,0,e.modRoleList,"Modification Role",e.mod.physicalModificationRole),n(l,17,0,"add / edit parameters"),n(l,19,0,"add_circle_outline"),n(l,22,0,e.mod.parameters),n(l,37,0,""),n(l,40,0,"group",e.mod.modificationGroup),n(l,43,0,"Group","")}),(function(n,l){n(l,5,0,t.Fb(l,6).disabled||null,"NoopAnimations"===t.Fb(l,6)._animationMode),n(l,8,0,t.Fb(l,9).inline,"primary"!==t.Fb(l,9).color&&"accent"!==t.Fb(l,9).color&&"warn"!==t.Fb(l,9).color),n(l,15,0,t.Fb(l,16).disabled||null,"NoopAnimations"===t.Fb(l,16)._animationMode),n(l,18,0,t.Fb(l,19).inline,"primary"!==t.Fb(l,19).color&&"accent"!==t.Fb(l,19).color&&"warn"!==t.Fb(l,19).color),n(l,24,1,["standard"==t.Fb(l,25).appearance,"fill"==t.Fb(l,25).appearance,"outline"==t.Fb(l,25).appearance,"legacy"==t.Fb(l,25).appearance,t.Fb(l,25)._control.errorState,t.Fb(l,25)._canLabelFloat,t.Fb(l,25)._shouldLabelFloat(),t.Fb(l,25)._hasFloatingLabel(),t.Fb(l,25)._hideControlPlaceholder(),t.Fb(l,25)._control.disabled,t.Fb(l,25)._control.autofilled,t.Fb(l,25)._control.focused,"accent"==t.Fb(l,25).color,"warn"==t.Fb(l,25).color,t.Fb(l,25)._shouldForward("untouched"),t.Fb(l,25)._shouldForward("touched"),t.Fb(l,25)._shouldForward("pristine"),t.Fb(l,25)._shouldForward("dirty"),t.Fb(l,25)._shouldForward("valid"),t.Fb(l,25)._shouldForward("invalid"),t.Fb(l,25)._shouldForward("pending"),!t.Fb(l,25)._animationsEnabled]),n(l,35,1,[t.Fb(l,37).required?"":null,t.Fb(l,42).ngClassUntouched,t.Fb(l,42).ngClassTouched,t.Fb(l,42).ngClassPristine,t.Fb(l,42).ngClassDirty,t.Fb(l,42).ngClassValid,t.Fb(l,42).ngClassInvalid,t.Fb(l,42).ngClassPending,t.Fb(l,43)._isServer,t.Fb(l,43).id,t.Fb(l,43).placeholder,t.Fb(l,43).disabled,t.Fb(l,43).required,t.Fb(l,43).readonly&&!t.Fb(l,43)._isNativeSelect||null,t.Fb(l,43)._ariaDescribedby||null,t.Fb(l,43).errorState,t.Fb(l,43).required.toString()])}))}var an=e("mrSG"),cn=function(n){function l(l,e,t){var o=n.call(this,t)||this;return o.substanceFormService=l,o.scrollToService=e,o.gaService=t,o.subscriptions=[],o.analyticsEventCategory="substance form agent modifications",o}return an.b(l,n),l.prototype.ngOnInit=function(){this.canAddItemUpdate.emit(!0),this.menuLabelUpdate.emit("Physical Modifications")},l.prototype.ngAfterViewInit=function(){var n=this,l=this.substanceFormService.substancePhysicalModifications.subscribe((function(l){n.modifications=l}));this.subscriptions.push(l)},l.prototype.ngOnDestroy=function(){this.componentDestroyed.emit(),this.subscriptions.forEach((function(n){n.unsubscribe()}))},l.prototype.addItem=function(){this.addStructuralModification()},l.prototype.addStructuralModification=function(){var n=this;this.substanceFormService.addSubstancePhysicalModification(),setTimeout((function(){n.scrollToService.scrollToElement("substance-physical-modification-0","center")}))},l.prototype.deletePhysicalModification=function(n){this.substanceFormService.deleteSubstancePhysicalModification(n)},l}(e("xhaW").a),rn=e("HECD"),un=t.rb({encapsulation:0,styles:[[".mat-divider.mat-divider-inset[_ngcontent-%COMP%]{margin-left:0}.substance-form-row[_ngcontent-%COMP%]{display:-webkit-box;display:-ms-flexbox;display:flex;-webkit-box-pack:justify;-ms-flex-pack:justify;justify-content:space-between}.mat-divider[_ngcontent-%COMP%]{border-top-color:rgba(0,0,0,.12)}.code[_ngcontent-%COMP%]:nth-child(odd){background-color:rgba(68,138,255,.07)}.code[_ngcontent-%COMP%]:nth-child(odd)     .mat-expansion-panel:not(.mat-expanded):not([aria-disabled=true]) .mat-expansion-panel-header:hover{background-color:rgba(68,138,255,.15)}.code[_ngcontent-%COMP%]:nth-child(even)     .mat-expansion-panel:not(.mat-expanded):not([aria-disabled=true]) .mat-expansion-panel-header:hover{background-color:rgba(128,128,128,.15)}.code[_ngcontent-%COMP%]     .mat-expansion-panel, .code[_ngcontent-%COMP%]     .mat-table, .code[_ngcontent-%COMP%]     textarea{background-color:transparent}"]],data:{}});function dn(n){return t.Pb(0,[(n()(),t.tb(0,0,null,null,1,"mat-divider",[["class","form-divider mat-divider"],["role","separator"]],[[1,"aria-orientation",0],[2,"mat-divider-vertical",null],[2,"mat-divider-horizontal",null],[2,"mat-divider-inset",null]],null,null,y.b,y.a)),t.sb(1,49152,null,0,_.a,[],{inset:[0,"inset"]},null)],(function(n,l){n(l,1,0,!0)}),(function(n,l){n(l,0,0,t.Fb(l,1).vertical?"vertical":"horizontal",t.Fb(l,1).vertical,!t.Fb(l,1).vertical,t.Fb(l,1).inset)}))}function sn(n){return t.Pb(0,[(n()(),t.tb(0,0,null,null,5,"div",[["appScrollToTarget",""],["class","alternate-backgrounds"]],[[8,"id",0]],null,null,null,null)),t.sb(1,4341760,null,0,C.a,[t.k,w.a],null,null),(n()(),t.tb(2,0,null,null,1,"app-physical-modification-form",[],null,[[null,"modDeleted"]],(function(n,l,e){var t=!0;return"modDeleted"===l&&(t=!1!==n.component.deletePhysicalModification(e)&&t),t}),on,Q)),t.sb(3,114688,null,0,B,[q.a,E.e,R.a,O.e,X.a],{mod:[0,"mod"]},{modDeleted:"modDeleted"}),(n()(),t.jb(16777216,null,null,1,null,dn)),t.sb(5,16384,null,0,U.m,[t.P,t.M],{ngIf:[0,"ngIf"]},null)],(function(n,l){n(l,3,0,l.context.$implicit),n(l,5,0,!l.context.last)}),(function(n,l){n(l,0,0,"substance-physical-modification-"+l.context.index)}))}function bn(n){return t.Pb(0,[(n()(),t.tb(0,0,null,null,1,"div",[["class","flex-row"]],null,null,null,null,null)),(n()(),t.tb(1,0,null,null,0,"span",[["class","middle-fill"]],null,null,null,null,null)),(n()(),t.jb(16777216,null,null,1,null,sn)),t.sb(3,278528,null,0,U.l,[t.P,t.M,t.s],{ngForOf:[0,"ngForOf"]},null)],(function(n,l){n(l,3,0,l.component.modifications)}),null)}function mn(n){return t.Pb(0,[(n()(),t.tb(0,0,null,null,1,"app-substance-form-physical-modifications",[],null,null,null,bn,un)),t.sb(1,4440064,null,0,cn,[X.a,w.a,rn.a],null,null)],(function(n,l){n(l,1,0)}),null)}var pn=t.pb("app-substance-form-physical-modifications",cn,mn,{},{menuLabelUpdate:"menuLabelUpdate",hiddenStateUpdate:"hiddenStateUpdate",canAddItemUpdate:"canAddItemUpdate",componentDestroyed:"componentDestroyed"},[]),fn=e("M2Lx"),gn=e("mVsa"),hn=e("uGex"),xn=e("4tE/"),vn=e("4epT"),yn=e("EtvR"),_n=e("4c35"),Cn=e("de3e"),wn=e("La40"),Mn=e("/dO6"),Pn=e("NYLF"),kn=e("y4qS"),Fn=e("BHnd"),Dn=e("YhbO"),On=e("jlZm"),In=e("6Wmm"),Sn=e("9It4"),jn=e("PnCX"),Ln=e("IyAz"),Tn=e("ZYCi"),Nn=e("5uHe"),Un=e("vfGX"),zn=e("0/Q6"),An=e("jS4w"),qn=e("u7R8"),En=e("NnTW"),Rn=e("7fs6"),Kn=e("YSh2"),Yn=e("6jyQ");e.d(l,"SubstanceFormPhysicalModificationsModuleNgFactory",(function(){return $n}));var $n=t.qb(o,[],(function(n){return t.Cb([t.Db(512,t.j,t.bb,[[8,[a.a,i.a,c.a,r.a,u.a,d.a,s.a,b.b,m.a,p.a,f.a,g.a,h.a,x.a,v.a,pn]],[3,t.j],t.x]),t.Db(4608,U.o,U.n,[t.u,[2,U.E]]),t.Db(4608,V.e,V.e,[]),t.Db(4608,V.w,V.w,[]),t.Db(4608,fn.c,fn.c,[]),t.Db(4608,O.c,O.c,[O.i,O.e,t.j,O.h,O.f,t.r,t.z,U.d,j.b,[2,U.i]]),t.Db(5120,O.j,O.k,[O.c]),t.Db(5120,gn.c,gn.j,[O.c]),t.Db(5120,D.b,D.c,[O.c]),t.Db(4608,L.e,H.e,[[2,H.i],[2,H.n]]),t.Db(5120,hn.a,hn.b,[O.c]),t.Db(4608,H.d,H.d,[]),t.Db(5120,xn.b,xn.c,[O.c]),t.Db(5120,E.c,E.d,[O.c]),t.Db(135680,E.e,E.e,[O.c,t.r,[2,U.i],[2,E.b],E.c,[3,E.e],O.e]),t.Db(5120,vn.c,vn.a,[[3,vn.c]]),t.Db(1073742336,U.c,U.c,[]),t.Db(1073742336,yn.a,yn.a,[]),t.Db(1073742336,V.v,V.v,[]),t.Db(1073742336,V.s,V.s,[]),t.Db(1073742336,V.k,V.k,[]),t.Db(1073742336,fn.d,fn.d,[]),t.Db(1073742336,G.e,G.e,[]),t.Db(1073742336,j.a,j.a,[]),t.Db(1073742336,H.n,H.n,[[2,H.f],[2,L.f]]),t.Db(1073742336,S.b,S.b,[]),t.Db(1073742336,H.x,H.x,[]),t.Db(1073742336,_n.g,_n.g,[]),t.Db(1073742336,I.c,I.c,[]),t.Db(1073742336,O.g,O.g,[]),t.Db(1073742336,gn.i,gn.i,[]),t.Db(1073742336,gn.f,gn.f,[]),t.Db(1073742336,Cn.d,Cn.d,[]),t.Db(1073742336,Cn.c,Cn.c,[]),t.Db(1073742336,P.c,P.c,[]),t.Db(1073742336,N.c,N.c,[]),t.Db(1073742336,k.a,k.a,[]),t.Db(1073742336,D.e,D.e,[]),t.Db(1073742336,wn.j,wn.j,[]),t.Db(1073742336,_.b,_.b,[]),t.Db(1073742336,H.v,H.v,[]),t.Db(1073742336,H.s,H.s,[]),t.Db(1073742336,hn.d,hn.d,[]),t.Db(1073742336,Z.c,Z.c,[]),t.Db(1073742336,J.b,J.b,[]),t.Db(1073742336,Mn.f,Mn.f,[]),t.Db(1073742336,xn.e,xn.e,[]),t.Db(1073742336,Pn.a,Pn.a,[]),t.Db(1073742336,E.k,E.k,[]),t.Db(1073742336,kn.p,kn.p,[]),t.Db(1073742336,Fn.m,Fn.m,[]),t.Db(1073742336,Dn.c,Dn.c,[]),t.Db(1073742336,On.d,On.d,[]),t.Db(1073742336,In.b,In.b,[]),t.Db(1073742336,Sn.d,Sn.d,[]),t.Db(1073742336,jn.a,jn.a,[]),t.Db(1073742336,Ln.a,Ln.a,[]),t.Db(1073742336,Tn.p,Tn.p,[[2,Tn.u],[2,Tn.m]]),t.Db(1073742336,Nn.a,Nn.a,[]),t.Db(1073742336,Un.a,Un.a,[]),t.Db(1073742336,H.o,H.o,[]),t.Db(1073742336,zn.d,zn.d,[]),t.Db(1073742336,An.b,An.b,[]),t.Db(1073742336,qn.e,qn.e,[]),t.Db(1073742336,En.b,En.b,[]),t.Db(1073742336,Rn.a,Rn.a,[]),t.Db(1073742336,vn.d,vn.d,[]),t.Db(1073742336,o,o,[]),t.Db(256,Mn.a,{separatorKeyCodes:[Kn.f]},[]),t.Db(1024,Tn.j,(function(){return[[]]}),[]),t.Db(256,Yn.a,cn,[])])}))}}]);
//# sourceMappingURL=41.9e0f4479beabf69591c2.js.map