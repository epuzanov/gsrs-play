(function () {
    var ginasForms = angular.module('ginasForms', ['bootstrap.fileField']);

    ginasForms.service('toggler', function ($compile, $templateRequest, spinnerService) {

        var childScope;
        this.stageCheck = function () {
            return this.stage;
        };

        this.show = function (scope, element, url) {
            if (_.isUndefined(scope.type)) {
                scope.type = element;
            }
            if (_.isUndefined(scope.stage)) {
                scope.stage = true;
            }
            var template = "";
            var result = document.getElementsByClassName(scope.type);
            var elementResult = angular.element(result);
            if (scope.stage === true) {
                scope.stage = false;
                $templateRequest(url).then(function (html) {
                    template = angular.element(html);
                    elementResult.append(template);
                    $compile(elementResult)(scope);
                });
            } else {
                elementResult.empty();
                scope.stage = true;
            }
        };

        this.refresh = function (scope, element, template, loading) {
            /*            if(scope.data.length > 0) {
             spinnerService.hideAll();
             spinnerService.show('drawSpinner');*/
            var result = angular.element(document.getElementsByClassName(element));
            result.html(template);
            $compile(result)(scope);
            //   }
        };

        this.toggle = function (scope, element, template) {
            var result = document.getElementsByClassName(element);
            var elementResult = angular.element(result);
            if (scope.stage === true) {
                scope.stage = false;
                childScope = scope.$new();
                var compiledDirective = $compile(template);
                var directiveElement = compiledDirective(childScope);
                elementResult.append(directiveElement);
            } else {
                if (childScope) {
                    childScope.$destroy();
                }
                elementResult.empty();
                scope.stage = true;
            }
        };
    });


    //generic controller that all forms will have. has acces to specific form scopes, and the more specific forms can call these methods
    ginasForms.controller('formController', function ($scope, $http) {

        //mainform is the form obj
        //location is the string of the array, can be nested
        //obj is a modified object that will be added
        $scope.addNew = function (mainForm, location, obj) {
            var temp = {};
            if (obj) {
                temp= obj;
            }
            var listObj = _.get($scope.parent, location);
            if (!_.isUndefined(listObj) && listObj.length > 0) {
                if (mainForm.form && mainForm.form.$invalid) {
                    mainForm.form.$flagged = true;
                }
                listObj.push(temp);
                _.set($scope.parent, location, listObj);
            } else {
                listObj =[];
                listObj.push(temp);
                _.set($scope.parent, location, listObj);
            }
        };
        
        $scope.submitFile = function (obj) {
            //create form data object
            var fd = new FormData();
            if (obj) {
                $scope.$$uploadFile = obj.$$uploadFile;
            }
            console.log($scope);
            fd.append('file-name', $scope.$$uploadFile);
            fd.append('file-type', $scope.$$uploadFile.type);
            //send the file / data to your server
            $http.post(baseurl + 'upload', fd, {
                transformRequest: angular.identity,
                headers: {'Content-Type': undefined}
            }).success(function (data) {
                if (obj) {
                    _.set(obj, 'uploadedFile', data.url);
                    delete obj.$$uploadFile;
                } else {
                    _.set($scope.obj, 'uploadedFile', data.url);
                }
            }).error(function (err) {
                _.set(obj, 'uploadedFile', false);
                _.set(obj, '$$uploadFile', false);
            });
            delete $scope.$$uploadFile;
        };


    });

    ginasForms.directive('accessForm', function () {
        return {
            restrict: 'E',
            replace: 'true',
            scope: {
                referenceobj: '=',
                parent: '='
            },
            templateUrl: baseurl + "assets/templates/forms/access-form.html"
        };
    });

    ginasForms.directive('agentModificationForm', function () {
        return {
            restrict: 'E',
            replace: true,
            controller: 'formController',
            scope: {
                parent: '='
            },
            templateUrl: baseurl + "assets/templates/forms/agent-modification-form.html"
        };
    });

    ginasForms.directive('amountForm', function () {
        return {
            restrict: 'E',
            replace: true,
            scope: {
                amount: '=',
                referenceobj: '=',
                parent: '=',
                field: '=',
                filter: '='
            },
            templateUrl: baseurl + "assets/templates/forms/amount-form.html"
        };
    });

    ginasForms.directive('codeForm', function (validatorFactory) {
        return {
            restrict: 'E',
            replace: true,
            controller: 'formController',
            templateUrl: baseurl + "assets/templates/forms/code-form.html",
            scope: {
                parent: '='
            },
            link: function (scope, element, attrs) {
                scope.parse = function (code) {
                    var errors = [];
                    if (_.isUndefined(code.formatter)) {
                        errors.push({text: 'no code system yet', type: 'warning'});
                    } else {
                        var valid = validatorFactory.validate(code.formatter.value, code.model);
                        if (valid == false) {
                            errors.push({text: 'invalid', type: 'danger'});
                        } else {
                            //  errors.push({text: 'valid', type: 'success'});
                        }
                    }
                    return errors;
                };
            }
        };
    });

    ginasForms.directive('conceptUpgradeForm', function ($window, localStorageService) {
        return {
            restrict: 'E',
            replace: true,
            templateUrl: baseurl + "assets/templates/forms/concept-upgrade-form.html",
            scope: {
                parent: '='
            },
            link: function (scope, element, attrs) {

                scope.iscollapsed = false;
                if (scope.parent.uuid) {
                    scope.editid = scope.parent.uuid.split('-')[0];
                }

                scope.changeClass = function (newClass) {
                    var upgradeSub = scope.parent.$$setClass(newClass);
                    _.set(upgradeSub, 'update', true);
                    localStorageService.set('tempsubstance', upgradeSub);
                    $window.location.href = $window.location.origin + baseurl + "wizard?kind=" + newClass;
                };
            }
        };
    });

    ginasForms.directive('disulfideLinkForm', function () {
        return {
            restrict: 'E',
            replace: true,
            controller: 'formController',
            scope: {
                parent: '=',
                referenceobj: '=',
                formype: '@',
                residueregex: '@'

            },
            templateUrl: baseurl + "assets/templates/forms/disulfide-link-form.html",
            link: function (scope, element, attrs) {

                scope.getAllCysteinesWithoutLinkage = function () {
                    var count = 0;
                    _.forEach(scope.parent.protein.subunits, function (subunit) {
                        if (!_.isUndefined(subunit.$$cysteineIndices)) {
                            count += subunit.$$cysteineIndices.length;
                        }
                    });
                    if (_.has(scope.parent, 'disulfideLinks')) {
                        count -= scope.parent.protein.disulfideLinks.length * 2;
                    }
                    return count;
                };
            }
        };
    });

    ginasForms.directive('diverseOrganismForm', function () {
        return {
            restrict: 'E',
            replace: true,
            scope: {
                parent: '='
            },
            templateUrl: baseurl + "assets/templates/forms/diverse-organism-form.html"
        };
    });

    ginasForms.directive('diverseSourceForm', function () {
        return {
            restrict: 'E',
            replace: true,
            scope: {
                parent: '='
            },
            templateUrl: baseurl + "assets/templates/forms/diverse-source-form.html",
            link: function(scope){
                scope.iscollapsed = false;
            }
        };
    });

    ginasForms.directive('diverseTypeForm', function () {
        return {
            restrict: 'E',
            replace: true,
            scope: {
                parent: '='
            },
            templateUrl: baseurl + "assets/templates/forms/diverse-type-form.html",
            link: function (scope, element, attrs) {
                scope.parent.$$diverseType = "whole";
                if(_.isUndefined(scope.parent.structurallyDiverse.part)){
                    scope.parent.structurallyDiverse.part = [];
                }
                if (scope.parent.structurallyDiverse.part.length > 0 && scope.parent.structurallyDiverse.part[0] != 'WHOLE') {
                    _.set(scope.parent, '$$diverseType', 'part');
                    _.set(scope, '$$temp', scope.parent.structurallyDiverse.part);
                }
                scope.checkType = function () {
                    if (scope.parent.$$diverseType === 'whole') {
                        scope.parent.$$diverseType = 'whole';
                        _.set(scope.parent.structurallyDiverse, 'part', ['WHOLE']);
                    } else {
                        if (scope.$$temp) {
                            _.set(scope.parent.structurallyDiverse, 'part', scope.$$temp);
                        } else {
                            _.set(scope.parent.structurallyDiverse, 'part', []);
                        }
                    }
                };
            }
        };
    });

    ginasForms.directive('glycosylationForm', function () {
        return {
            restrict: 'E',
            replace: true,
            controller: 'formController',
            scope: {
                parent: '=',
                referenceobj: '='
            },
            templateUrl: baseurl + "assets/templates/forms/glycosylation-form.html",
            link: function (scope, element, attrs) {
                _.forEach(scope.parent.protein.glycosylation, function (value) {
                    if (_.isArray(value) && value.length > 0) {
                        scope.iscollapsed = false;
                    }
                });
            }
        };
    });

    ginasForms.directive('headerForm', function () {
        return {
            restrict: 'E',
            replace: true,
            scope: {
                parent: '='
            },
            link: function (scope, element, attrs) {
                if (scope.parent._name) {
                    scope.formType = 'Editing';
                    scope.name = scope.parent._name;
                } else {
                    scope.formType = 'Registering new';
                    scope.name = _.startCase(scope.parent.substanceClass);
                }
            },
            templateUrl: baseurl + "assets/templates/forms/header-form.html"
        };
    });

    ginasForms.directive('mixtureComponentSelectForm', function () {
        return {
            restrict: 'E',
            replace: true,
            controller: 'formController',
            scope: {
                parent: '='
            },
            templateUrl: baseurl + "assets/templates/forms/mixture-component-select-form.html"
        };
    });

    ginasForms.directive('mixtureDetailsForm', function () {
        return {
            restrict: 'E',
            replace: true,
            scope: {
                parent: '='
            },
            templateUrl: baseurl + "assets/templates/forms/mixture-details-form.html"
        };
    });

    ginasForms.directive('moietyForm', function () {
        return {
            restrict: 'E',
            replace: true,
            scope: {
                parent: '='
            },
            templateUrl: baseurl + "assets/templates/forms/moiety-form.html"
        }
    });

    ginasForms.directive('nameForm', function ($q, substanceFactory, resolver, toggler, spinnerService) {
        return {
            restrict: 'E',
            replace: true,
            controller: 'formController',
            scope: {
                parent: '='
            },
            templateUrl: baseurl + "assets/templates/forms/name-form.html",
            link: function (scope) {

                scope.duplicateCheck = function (name) {
                    var errors = [];
                    console.log("Duplicate");
                    var result = angular.element(document.getElementsByClassName('nameForm'));
                    result.empty();
                    var resolve = resolver.resolve(name).then(function (response) {
                        if (response.data.length > 0) {
                            return response.data;
                        }
                        return [];
                    });
                    var duplicate = substanceFactory.getSubstances(name).then(function (response) {
                        var duplicate = [];
                        if (response.data.count > 0) {
                            errors.push({text: 'duplicate name', type: 'warning'});
                            _.forEach(response.data.content, function (sub) {
                                _.set(sub, 'refType', 'duplicate');
                                duplicate.push(sub);
                            });
                        }
                        return duplicate;
                    });
                    if (!_.isUndefined(name) && name !== "") {
                        spinnerService.show('nameSpinner');
                        var template;
                        $q.all([resolve, duplicate]).then(function (results) {
                            scope.data = [];
                            var temp = [];
                            _.forEach(results, function (result) {
                                if (!_.isUndefined(result)) {
                                    temp.push(result);
                                }
                                scope.data = _.flattenDeep(temp);
                            });
                        }).finally(function () {
                            if (_.isEmpty(scope.data)) {
                                scope.data.push("empty");
                            }
                            template = angular.element('<substance-viewer data = data parent = parent obj = name></substance-viewer>');
                            toggler.refresh(scope, 'nameForm', template);
                        });
                    } else {
                        spinnerService.hideAll();
                    }
                    return errors;
                };

            }
        };
    });

    ginasForms.directive('nameOrgForm', function () {
        return {
            restrict: 'E',
            replace: true,
            controller: 'formController',
            scope: {
                parent: '=',
                referenceobj: '=?'
            },
            templateUrl: baseurl + "assets/templates/forms/name-org-form.html"
        };
    });

    ginasForms.directive('noteForm', function () {
        return {
            restrict: 'E',
            replace: true,
            controller: 'formController',
            scope: {
                parent: '='
            },
            templateUrl: baseurl + "assets/templates/forms/note-form.html"
        };
    });

    ginasForms.directive('nucleicAcidDetailsForm', function () {
        return {
            restrict: 'E',
            replace: true,
            scope: {
                parent: '='
            },
            templateUrl: baseurl + "assets/templates/forms/nucleic-acid-details-form.html"
        };
    });

    ginasForms.directive('nucleicAcidSugarForm', function (siteList, siteAdder) {
        return {
            restrict: 'E',
            replace: true,
            controller: 'formController',
            scope: {
                parent: '=',
                referenceobj: '='
            },
            templateUrl: baseurl + "assets/templates/forms/nucleic-acid-sugar-form.html",
            link: function (scope, attrs, element) {
                console.log(scope);

                scope.getAllSites = function () {
                    return siteAdder.getCount(scope.parent.nucleicAcid.subunits);
                };

                scope.applyAll = function (obj) {
                    console.log(obj);
                    siteAdder.applyAll('sugar', scope.parent, obj);
                };


//this is called every time something is hovered over -- ned to fix
                scope.noSugars = function () {
                    var count = scope.getAllSites();
                    _.forEach(scope.parent.nucleicAcid.sugars, function (sugar) {
                        if(_.isArray(sugar.sites)) {
                            count -= sugar.sites.length
                        }
                    });
                    return count;
                }
            }
        };
    });

    ginasForms.directive('nucleicAcidLinkageForm', function (siteList, siteAdder) {
        return {
            restrict: 'E',
            replace: true,
            controller: 'formController',
            scope: {
                referenceobj: '=',
                parent: '='
            },
            templateUrl: baseurl + "assets/templates/forms/nucleic-acid-linkage-form.html",
            link: function (scope, attrs, element) {
                scope.linkage = {};
                scope.noLinkages = 0;
                if (!scope.parent.nucleicAcid.linkages) {
                    scope.parent.nucleicAcid.linkages = [];
                }

                scope.getAllSites = function () {
                    return siteAdder.getCount(scope.parent.nucleicAcid.subunits);
                };

                scope.applyAll = function () {
                    siteAdder.applyAll('linkage', scope.parent, scope.linkage, 'linkage');
                    scope.noLinkages = siteAdder.getAllSitesWithout('linkage', scope.parent.$$subunitDisplay).length;
                };

                scope.validate = function () {
                    _.forEach(scope.linkage.sites.sites, function (site) {
                        _.set(scope.parent.$$subunitDisplay[site.subunitIndex - 1][site.residueIndex - 1], 'linkage', true);
                    });
                    scope.parent.nucleicAcid.linkages.push(scope.linkage);
                    scope.noLinkages = siteAdder.getAllSitesWithout('linkage', scope.parent.$$subunitDisplay).length;
                    scope.linkage = {};
                    scope.linkageForm.$setPristine();
                };

                scope.deleteObj = function (obj) {
                    scope.parent.nucleicAcid.linkages.splice(scope.parent.nucleicAcid.linkages.indexOf(obj), 1);
                    siteAdder.clearSites('linkage', scope.parent, obj.sites);
                    scope.noLinkages = siteAdder.getAllSitesWithout('linkage', scope.parent.$$subunitDisplay).length;
                };

                scope.noLinkages = function () {
                    var count = scope.getAllSites('linkage') - 1;
                    _.forEach(scope.parent.nucleicAcid.linkages, function (link) {
                        count -= link.sites.length
                    });
                    return count;
                }
            }
        };
    });


    ginasForms.directive('otherLinksForm', function () {
        return {
            restrict: 'E',
            replace: true,
            controller: 'formController',
            scope: {
                parent: '=',
                referenceobj: '=',
                displayType: '='
            },
            templateUrl: baseurl + "assets/templates/forms/other-links-form.html",
            link: function (scope, element, attrs) {

                scope.validate = function () {
                    if (!scope.parent.protein.otherLinks) {
                        scope.parent.protein.otherLinks = [];
                    }
                    scope.parent.protein.otherLinks.push(scope.otherLink);
                    scope.otherLink = {};
                    scope.otherLinksForm.$setPristine();
                };

                scope.deleteObj = function (obj) {
                    scope.parent.protein.otherLinks.splice(scope.parent.protein.otherLinks.indexOf(obj), 1);
                };
            }
        };
    });

    ginasForms.directive('parameterForm', function () {
        return {
            restrict: 'E',
            replace: 'true',
            controller: 'formController',
            scope: {
                parent: '=',
                referenceobj: '=?'// this is the object from the form that is getting the parameter added to it
            },
            templateUrl: baseurl + "assets/templates/forms/parameter-form.html"
        };
    });

    ginasForms.directive('parentForm', function () {
        return {
            restrict: 'E',
            replace: true,
            scope: {
                parent: '='
            },
            templateUrl: baseurl + "assets/templates/forms/parent-form.html"
        };
    });

    ginasForms.directive('partForm', function () {
        return {
            restrict: 'E',
            replace: true,
            scope: {
                parent: '='
            },
            templateUrl: baseurl + "assets/templates/forms/part-form.html"
        };
    });

    ginasForms.directive('physicalModificationForm', function () {
        return {
            restrict: 'E',
            replace: true,
            controller: 'formController',
            scope: {
                parent: '=',
                referenceobj: '=?'// this is the object from the form that is getting the property added to it
            },
            templateUrl: baseurl + "assets/templates/forms/physical-modification-form.html",
            link: function(scope){
               
            }
        };
    });

    ginasForms.directive('physicalParameterForm', function () {
        return {
            restrict: 'E',
            replace: 'true',
            controller: 'formController',
            scope: {
                parent: '=',
                referenceobj: '=?'// this is the object from the form that is getting the parameter added to it
            },
            templateUrl: baseurl + "assets/templates/forms/physical-parameter-form.html",
        };
    });

    ginasForms.directive('polymerClassificationForm', function () {
        return {
            restrict: 'E',
            replace: true,
            scope: {
                parent: '='
            },
            templateUrl: baseurl + "assets/templates/forms/polymer-classification-form.html"
        };
    });

    ginasForms.directive('polymerMonomerForm', function () {
        return {
            restrict: 'E',
            replace: true,
            controller: 'formController',
            scope: {
                parent: '='
            },
            templateUrl: baseurl + "assets/templates/forms/polymer-monomer-form.html"
        };
    });

    ginasForms.directive('polymerSruForm', function (polymerUtils) {
        return {
            restrict: 'E',
            replace: true,
            controller: 'formController',
            scope: {
                parent: '='
            },
            templateUrl: baseurl + "assets/templates/forms/polymer-sru-form.html",
            link: function (scope) {
                scope.validateConnectivity = function (obj) {
                   // console.log(obj);
                    var map = polymerUtils.sruDisplayToConnectivity(obj);
                   // console.log(map);
                   return map.errors;
                }
            }
        };
    });

    ginasForms.directive('propertyForm', function () {
        return {
            restrict: 'E',
            replace: true,
            controller: 'formController',
            scope: {
                parent: '=',
                referenceobj: '=?'// this is the object from the form that is getting the property added to it
            },
            templateUrl: baseurl + "assets/templates/forms/property-form.html"
        };
    });

    ginasForms.directive('proteinDetailsForm', function () {
        return {
            restrict: 'E',
            replace: true,
            scope: {
                parent: '='
            },
            link: function (scope) {
                /* var fields =['sequenceType','proteinType','proteinSubType','sequenceOrigin'];
                 _.forEach(fields, function(field){
                 if(!_.isNull(scope.parent.protein[field])){
                 scope.iscollapsed=false;
                 }
                 });*/
            },
            templateUrl: baseurl + "assets/templates/forms/protein-details-form.html"
        };
    });

    ginasForms.directive('referenceForm', function (UUID) {
        return {
            restrict: 'E',
            replace: true,
            controller: 'formController',
            scope: {
                parent: '=',
                referenceobj: '=?'// this is the object from the form that is getting the references added to it
            },
            templateUrl: baseurl + "assets/templates/forms/reference-form.html",
            link: function (scope, element, attrs) {
                
                scope.addNewRef = function (mainform, list) {
                    //passes a new uuid for reference tracking
                    var obj = {
                        uuid: UUID.newID(),
                        apply: true
                    };
                    scope.addNew(mainform, list, obj);
                };

                scope.applyRefs = attrs.apply;

                //called on close of the modal reference form. saves all applied references to the array
                scope.$on('save', function (e) {
                    console.log("saviunig");
                    scope.validate();
                });

                scope.validate = function () {
                    console.log(scope);
                    //grabs an array of all the uuids of the references where apply checkbox is true
                    var objreferences = _
                        .chain(scope.parent.references)
                        .filter(function (ref) {
                            if (ref.apply) {
                                return ref;
                            }
                        })
                        .map('uuid')
                        .value();
                    console.log(objreferences);

                    //pushes this array to the reference object
                    console.log(scope.referenceobj);
                    if (_.has(scope.referenceobj, 'references') && scope.referenceobj.references.length > 0) {
                        _.union(scope.referenceobj.references, objreferences);
                    } else {
                        _.set(scope.referenceobj, 'references', objreferences);
                    }
                };

                //the delete button emits the delete object, which is used here to remove the reference form all arrays that use it
                scope.$on('delete', function (e) {
                    console.log(e.targetScope.obj);
                    console.log("form delete");
                    console.log(scope.obj);
                    var obj = scope.parent;
                    var del = e.targetScope.obj.uuid;
                    scope.deleteObj(obj, del);
                });

                //recursive object iteration method
                scope.deleteObj = function (obj, ref) {
                    _.forEach(_.keysIn(obj), function (field) {
                        if (_.isObject(obj[field])) {
                            if (_.isArray(obj[field])) {
                                _.forEach((obj[field]), function (value, key) {
                                    if (_.isObject(value)) {
                                        console.log(value);
                                        if (_.indexOf(_.keysIn(value), 'references') > -1) {
                                            scope.deleteObjectReferences(value, ref);
                                        } else {
                                            scope.deleteObj(value, ref);
                                        }
                                    }
                                });
                            } else {
                                scope.deleteObj(obj[field], ref);
                            }
                        }
                    });
                };

                //actual method to delete the reference uuid from an object references array
                scope.deleteObjectReferences = function (obj, id) {
                    var index;
                    var refs = _.get((obj), 'references');
                    index = _.findIndex(refs, function (o) {
                        return o == id;
                    });
                    if (index > -1) {
                        refs.splice(index, 1);
                        obj.references = refs;
                    }
                };
            }
        };
    });

    ginasForms.directive('relationshipForm', function () {
        return {
            restrict: 'E',
            replace: true,
            controller: 'formController',
            scope: {
                parent: '='
            },
            templateUrl: baseurl + "assets/templates/forms/relationship-form.html"
        };
    });

    ginasForms.directive('ssConstituentForm', function () {
        return {
            restrict: 'E',
            replace: true,
            controller: 'formController',
            scope: {
                parent: '='
            },
            templateUrl: baseurl + "assets/templates/forms/ss-constituent-form.html"
        };
    });

    ginasForms.directive('structuralModificationForm', function (CVFields) {
        return {
            restrict: 'E',
            replace: true,
            controller: 'formController',
            scope: {
                referenceobj: '=?',
                parent: '='
            },
            templateUrl: baseurl + "assets/templates/forms/structural-modifications-form.html",
            link: function (scope) {
                scope.getCV = function (mod) {
                    if (mod == "AMINO_ACID_SUBSTITUTION") {
                        return "AMINO_ACID_RESIDUE";
                    } else if (mod == "NUCLEOSIDE_SUBSTITUTION") {
                        return "NUCLEIC_ACID_BASE";
                    } else {
                        return null;
                    }
                };
            }
        };
    });

    ginasForms.directive('structureForm', function ($http, $templateRequest, $compile, molChanger) {
        return {
            restrict: 'E',
            replace: true,
            scope: {
                parent: '=',
                type: '@'
            },
            link: function (scope, element) {
                if (scope.parent.substanceClass === 'polymer') {
                    if (_.has(scope.parent.polymer.idealizedStructure)) {
                        _.set(scope.parent, 'structure', scope.parent.polymer.idealizedStructure);
                    } else {
                        _.set(scope.parent, 'structure', scope.parent.polymer.displayStructure);
                    }
                } else {
                    scope.structure = scope.parent.structure;
                }

                scope.checkDuplicateChemicalSubstance = function () {
                    var sub = scope.parent.$$flattenSubstance(angular.copy(scope.parent));
                    scope.structureErrorsArray = [];
                    $http.post(baseurl + 'register/duplicateCheck', sub).success(function (response) {
                        var arr = [];

                        for (var i in response) {
                            if (response[i].messageType != "INFO")
                                arr.push(response[i]);
                            if (response[i].messageType == "WARNING")
                                response[i].class = "alert-warning";
                            if (response[i].messageType == "ERROR")
                                response[i].class = "alert-danger";
                            if (response[i].messageType == "INFO")
                                response[i].class = "alert-info";
                            if (response[i].messageType == "SUCCESS")
                                response[i].class = "alert-success";


                        }
                        scope.structureErrorsArray = arr;
                    });
                };
                $templateRequest(baseurl + "assets/templates/forms/structure-form.html").then(function (html) {
                   var template = angular.element(html);
                    element.append(template);
                    $compile(template)(scope);
                });

            }
        };
    });

    ginasForms.directive('subunitForm', function ($compile, $templateRequest, $uibModal) {
        return {
            restrict: 'E',
            replace: true,
            controller: 'formController',
            scope: {
                parent: '='
            },
            templateUrl: baseurl + "assets/templates/forms/subunit-form.html",
            link: function (scope, element, attrs) {

                scope.substanceClass = scope.parent.$$getClass();
                scope.numbers = true;

                    scope.addNewSubunit= function (form) {
                        var r =  scope.substanceClass +'.subunits';
                        scope.addNew(form, r);
                    };



                scope.validate = function () {
                    if (scope.subunit.sequence.length > 10000) {
                        scope.open();
                    }
                    scope.subunit.subunitIndex = scope.parent[scope.parent.substanceClass].subunits.length + 1;
                    scope.parent[scope.parent.substanceClass].subunits.push(scope.subunit);
                    scope.subunit = {};
                    scope.subunitForm.$setPristine();
                };

                scope.close = function () {
                    var obj = _.last(scope.parent[scope.parent.substanceClass].subunits);
                    scope.deleteObj(obj);
                    modalInstance.close();
                };

                scope.open = function () {
                    modalInstance = $uibModal.open({
                        templateUrl: baseurl + "assets/templates/modals/subunit-warning.html",
                        scope: scope
                    });
                };
                /*$templateRequest(baseurl + "assets/templates/forms/subunit-form.html").then(function (html) {
                    var template = angular.element(html);
                    element.append(template);
                    $compile(template)(scope);
                });*/
            }
        };
    });




    ginasForms.directive('formmanager', function ($compile, $templateRequest, toggler) {
        return {
            controller: function ($scope) {
                this.scope = $scope;
                // this.referenceRetriever = referenceRetriever;
                $scope.addClass = [];
                this.setClass = function (index) {
                    $scope.addClass[index] = "success";
                };
                this.getClass = function (index) {
                    return $scope.addClass[index];
                };

                this.removeClass = function () {
                    $scope.addClass = [];
                };

                this.scrollTo = function () {
                    $scope.scrollTo('refs');
                };

                this.toggle = function (scope, divid) {
                    $scope.addClass = [];
                    var url = baseurl + "assets/templates/reference-table.html";
                    scope.references = _.sortBy(scope.objreferences, '$$index', function (ref) {
                        if (!scope.stage == false || _.isUndefined(scope.stage)) {
                            $scope.addClass[ref.$$index] = "success";
                        }
                    });
                    toggler.show(scope, divid, url);

                };
            }
        };
    });

    
    ginasForms.directive('cvForm', function ($compile, $uibModal, CVFields) {
        return {
            restrict: 'E',
            replace: 'true',
            scope: {},
            templateUrl: baseurl + "assets/templates/admin/cv-form.html",
            link: function (scope, element, attrs) {
                var formHolder;
                // scope.stage= true;
                CVFields.count().then(function (response) {
                    scope.count = response.data.total;
                });

                scope.edit = function () {
                    formHolder = '<edit-cv-form></edit-cv-form>';
                    scope.toggleStage();
                };

                scope.create = function () {
                    formHolder = '<new-cv-form></new-cv-form>';
                    scope.toggleStage();
                };

                scope.import = function () {
                    formHolder = '<load-cv-form></load-cv-form>';
                    scope.toggleStage();
                };

                scope.download = function () {
                    scope.cv = {};
                    CVFields.all(false).then(function (response) {
                        scope.cv = response.data.content;
                        formHolder = '<save-cv-form cv = cv></save-cv-form>';
                        scope.toggleStage();
                    });
                };


                scope.close = function () {
                    modalInstance.close();
                };

                scope.open = function (url) {
                    modalInstance = $uibModal.open({
                        templateUrl: url,
                        scope: scope
                    });
                };

                scope.toggleStage = function () {
                    var result = document.getElementsByClassName('cvForm');
                    var elementResult = angular.element(result);
                    elementResult.empty();
                    childScope = scope.$new();
                    var compiledDirective = $compile(formHolder);
                    var directiveElement = compiledDirective(childScope);
                    elementResult.append(directiveElement);
                };
            }
        };
    });




    ginasForms.directive('cvTermsForm', function (CVFields) {
        return {
            restrict: 'E',
            replace: true,
            /*scope: {
             terms: '=',
             domain: '='
             },*/
            templateUrl: baseurl + "assets/templates/admin/cv-terms.html",
            link: function (scope) {
                console.log(scope);
            }
        };
    });


    ginasForms.directive('editCvForm', function ($templateRequest, CVFields, toggler) {
        return {
            restrict: 'E',
            replace: true,
            scope: {
                parent: '='
            },
            templateUrl: baseurl + "assets/templates/admin/edit-cv-form.html",
            link: function (scope) {
                CVFields.all().then(function (response) {
                    scope.domains = response.data.content;
                });

                scope.stage = true;

                scope.flattenFields = function (fields) {
                    _.forEach(fields, function (value, key) {
                        if (!value.value && value.display) {
                            fields[key] = value.display;
                        } else if (!value.value && !value.display) {
                            fields[key] = value;
                        } else {
                            fields[key] = value.value;
                        }
                    });
                    return fields;
                };

                scope.getValues = function () {
                    CVFields.getCV(scope.vocab.display).then(function (data) {
                        scope.domain = data.data.content[0];
                    });
                    scope.create = true;
                };

                scope.deleteCV = function (obj) {
                    var r = confirm("Are you sure you want to delete this CV?");
                    if (r == true) {
                        var terms = scope.domain.terms.splice(scope.domain.terms.indexOf(obj), 1);
                        if (scope.domain.fields) {
                            scope.domain.fields = scope.flattenFields(scope.domain.fields);
                        }
                        CVFields.updateCV(scope.domain);
                    }
                };

                scope.addCV = function (term) {
                    scope.domain.terms.push(term);
                    if (scope.domain.fields) {
                        scope.domain.fields = scope.flattenFields(scope.domain.fields);
                    }
                    CVFields.updateCV(scope.domain).then(function (response) {
                        scope.domain.terms = response.data.terms;
                        scope.term = {};
                    });
                };


                scope.addDomain = function (domain) {
                    if (!domain.terms) {
                        _.set(domain, 'terms', []);
                    }
                    domain.fields = scope.flattenFields(domain.fields);
                    CVFields.addDomain(domain).then(function (response) {
                        // scope.domains.push(response.data);
                    });
                    scope.domain = {};
                };

                scope.updateCV = function (obj) {
                    if (obj) {
                        obj.fields = scope.flattenFields(obj.fields);
                        CVFields.updateCV(obj).then(function (response) {
                            _.forEach(response.data.fields, function (value, key) {
                                obj.fields[key] = {'value': value, 'display': value};
                            });
                        });
                    } else {
                        if (scope.domain.fields) {
                            scope.domain.fields = scope.flattenFields(scope.domain.fields);
                        }
                        CVFields.updateCV(scope.domain).then(function (response) {
                            _.forEach(response.data.fields, function (value, key) {
                                scope.domain.fields[key] = {'value': value, 'display': value};
                            });
                            scope.domain.terms = response.data.terms;
                        });
                    }
                };

                scope.showTerms = function (obj, divid) {
                    if (!obj.terms) {
                        _.set(obj, 'terms', []);
                    }
                    scope.domain = obj;

                    if (!divid) {
                        scope.type = obj.id;
                    }
                    // var formHolder = '<cv-terms-form domain = domain terms = {{terms}} ></cv-terms-form>';
                    var url = baseurl + "assets/templates/admin/cv-terms.html";
                    toggler.show(scope, divid, url);


                }

            }
        };
    });

    ginasForms.directive('loadCvForm', function (FileReader, CVFields) {
        return {
            restrict: 'E',
            replace: true,
            templateUrl: baseurl + "assets/templates/admin/load-cv-form.html",
            link: function (scope, element, attrs) {

                scope.cv = {};

                scope.getPos = function (item, arr) {
                    if (scope.cv.domains.indexOf(item) == -1
                        && scope.cv.value[arr.indexOf(item)] == true) {
                        scope.cv.domains.push(item)
                    }
                    if (scope.cv.domains.indexOf(item) != -1
                        && !scope.cv.value[arr.indexOf(item)]) {
                        scope.cv.domains.splice(scope.cv.domains.indexOf(item), 1)
                    }
                    return arr.indexOf(item);
                };

                scope.loadCVFile = function (file) {
                    var tempValues = [];
                    scope.values = [];
                    FileReader.readAsText(file, scope).then(function (response) {
                        scope.data = angular.fromJson(response);
                        for (var i = 0; i < 10; i++) {
                            var keys = _.keys(scope.data.substanceNames[i]);
                            tempValues = _.union(keys, tempValues);
                        }
                        _.forEach(tempValues, function (v) {
                            scope.values.push({display: v});
                        });
                        //  scope.values = tempValues;
                    });
                };

                scope.makeCV = function () {
                    var controlledVocab = {
                        domain: scope.cv.domainName,
                        terms: []
                    };
                    var tempTerms = [];
                    _.forEach(scope.data.substanceNames, function (term) {
                        var t = {
                            value: term[scope.cv.domain.display],
                            display: term[scope.cv.domain.display],
                            filters: []
                        };
                        if (scope.cv.dependencyField) {
                            var filter = scope.cv.dependencyField.display + '=' + term[scope.cv.dependencyField.display];
                            t.filters.push(filter);
                        }
                        tempTerms.push(t);
                    });

                    controlledVocab.terms = _.uniqWith(tempTerms, _.isEqual);
                    CVFields.addTerms(controlledVocab);
                };
            }
        };
    });

    ginasForms.directive('saveCvForm', function () {
        return {
            restrict: 'E',
            replace: true,
            scope: {
                cv: '='
            },
            templateUrl: baseurl + "assets/templates/admin/save-cv-form.html",
            link: function (scope, element, attrs) {


            }
        };
    });





    ginasForms.directive('newCvForm', function () {
        return {
            restrict: 'E',
            replace: true,
            scope: {
                parent: '='
            },
            templateUrl: baseurl + "assets/templates/admin/new-cv-form.html",
            link: function (scope) {

            }
        };
    });




    ginasForms.service('siteList', function () {

        //string to array hepler
        function siteDisplayToSite(site) {
            var subres = site.split("_");

            if (site.match(/^[0-9][0-9]*_[0-9][0-9]*$/g) === null) {
                throw "\"" + site + "\" is not a valid shorthand for a site. Must be of form \"{subunit}_{residue}\"";
            }

            return {
                subunitIndex: subres[0] - 0,
                residueIndex: subres[1] - 0
            };
        }

        //string to array
        this.siteList = function (slist) {
            var toks = slist.split(";");
            var sites = [];
            for (var i in toks) {
                var l = toks[i];
                if (l === "")continue;
                var rng = l.split("-");
                if (rng.length > 1) {
                    var site1 = siteDisplayToSite(rng[0]);
                    var site2 = siteDisplayToSite(rng[1]);
                    if (site1.subunitIndex != site2.subunitIndex) {
                        throw "\"" + rng + "\" is not a valid shorthand for a site range. Must be between the same subunits.";
                    }
                    if (site2.residueIndex <= site1.residueIndex) {
                        throw "\"" + rng + "\" is not a valid shorthand for a site range. Second residue index must be greater than first.";
                    }
                    sites.push(site1);
                    for (var j = site1.residueIndex + 1; j < site2.residueIndex; j++) {
                        sites.push({
                            subunitIndex: site1.subunitIndex,
                            residueIndex: j
                        });
                    }
                    sites.push(site2);
                } else {
                    sites.push(siteDisplayToSite(rng[0]));
                }
            }
            return sites;

        };

        //array to String
        this.siteString = function (sitest) {
            var sites = [];
            angular.extend(sites, sitest);
            sites.sort(function (site1, site2) {
                var d = site1.subunitIndex - site2.subunitIndex;
                if (d === 0) {
                    d = site1.residueIndex - site2.residueIndex;
                }
                return d;

            });
            var csub = 0;
            var cres = 0;
            var rres = 0;
            var finish = false;
            var disp = "";
            for (var i = 0; i < sites.length; i++) {

                var site = sites[i];
                if (site.subunitIndex == csub && site.residueIndex == cres)
                    continue;
                finish = false;
                if (site.subunitIndex == csub) {
                    if (site.residueIndex == cres + 1) {
                        if (rres === 0) {
                            rres = cres;
                        }
                    } else {
                        finish = true;
                    }
                } else {
                    finish = true;
                }
                if (finish && csub !== 0) {
                    if (rres !== 0) {
                        disp += csub + "_" + rres + "-" + csub + "_" + cres + ";";
                    } else {
                        disp += csub + "_" + cres + ";";
                    }
                    rres = 0;
                }
                csub = site.subunitIndex;
                cres = site.residueIndex;
            }
            if (sites.length > 0) {
                if (rres !== 0) {
                    disp += csub + "_" + rres + "-" + csub + "_" + cres;
                } else {
                    disp += csub + "_" + cres;
                }
            }
            return disp;
        };


        //make string from all indexes
        this.allSites = function (parent, type, linkage) {
            var sites = "";
            var subs = parent[type].subunits;
            console.log(subs);
            for (var i in subs) {
                var subunit = subs[i];
                if (sites !== "") {
                    sites += ";";
                }
                if (linkage) {
                    sites += subunit.subunitIndex + "_1-" + subunit.subunitIndex + "_" + (subunit.sequence.length - 1);
                } else {
                    sites += subunit.subunitIndex + "_1-" + subunit.subunitIndex + "_" + subunit.sequence.length;
                }
            }
            return sites;
        };
    });

    ginasForms.service('siteAdder', function (siteList) {

        this.getAll = function (type, display) {
            var temp = [];
           // _.forEach(display, function (arr) {
                _.forEach(display, function (subunit) {
                    temp = _.filter(subunit, function (su) {
                        return su[type];
                    });
            //    });
            });
            return temp;
        };

        this.getCount = function (subunits) {
            var count = 0;
            _.forEach(subunits, function (subunit) {
                _.forEach(subunit.subunitDisplay, function (chunk) {
                    count += chunk.length;
                });
            });
            return count;
        };


        this.getAllSitesWithout = function (type, display) {
            var temp = [];
            _.forEach(display, function (subunit) {
                _.forEach(subunit.subunitDisplay, function (chunk) {
                       console.log(chunk);
                    temp = _.reject(chunk, function (aa) {
                        //    console.log(su[type]);
                        return aa[type];
                    });
                });
            });
            if (type == 'linkage') {
                temp = _.dropRight(temp);
            }
            // console.log(temp);
            return temp;
        };

        this.applyAll = function (type, parent, obj) {
            var plural = type + "s";
            if (parent.nucleicAcid[plural].length == 0) {
                if (type == 'linkage') {
                    obj.$$displayString = siteList.allSites(parent, 'nucleicAcid', type);
                } else {
                    obj.$$displayString = siteList.allSites(parent, 'nucleicAcid');
                }
                obj.sites = siteList.siteList(obj.$$displayString);


            } else {
                console.log(obj);
                obj.$$displayString = siteList.siteString(this.getAllSitesWithout(type, parent.nucleicAcid.subunits));
                obj.sites = siteList.siteList(obj.$$displayString);
            }
            //this applies the sugar property to the display object
            _.forEach(obj.sites, function (site) {
                console.log(site);
                console.log(parent.nucleicAcid.subunits[site.subunitIndex - 1].subunitDisplay);
                console.log(site.residueIndex % 10);
                _.set(parent.nucleicAcid.subunits[site.subunitIndex - 1].subunitDisplay[site.residueIndex % 10][site.residueIndex - 1], type, true);
            });
        };

        this.clearSites = function (type, parent, obj) {
            _.forEach(obj, function (site) {
                parent.nucleicAcid.subunits[site.subunitIndex - 1][site.residueIndex - 1] = _.omit(parent.nucleicAcid.subunits[site.subunitIndex - 1][site.residueIndex - 1], type);
            });
        };
    });













    
     ginasForms.directive('siteStringForm', function ($compile, $templateRequest, siteList) {
        return {
            restrict: 'E',
            replace: true,
            scope: {
                referenceobj: '=',
                parent: '=',
                mode: '=',
                formtype: '=',
                residueregex: '=',
                field: '='
            },
            link: function (scope, element, attrs) {
                var template;
                scope.subunits = scope.parent[scope.parent.substanceClass].subunits;

                if (scope.formtype == "pair") {
                    $templateRequest(baseurl + "assets/templates/forms/site-dropdown-form.html").then(function (html) {
                        template = angular.element(html);
                        element.append(template);
                        $compile(template)(scope);
                    });
                } else {
                    $templateRequest(baseurl + "assets/templates/forms/site-string-form.html").then(function (html) {
                        template = angular.element(html);
                        element.append(template);
                        $compile(template)(scope);

                    });
                }

                ///////////////////////////////////////////////////////////////////////////////////////////////////////
                //this gets called when the siteform is open, and on hovering over subunits...
                scope.validResidues = function (su) {
                    if (!su)return [];
                    var list = [];
                    if (scope.residueregex) {
                        var ret = scope.parent[scope.parent.substanceClass].subunits[su - 1].$$cysteineIndices;
                        if (_.has(scope.parent.protein, 'disulfideLinks')) {
                            _.forEach(scope.parent.protein.disulfideLinks, function (siteList) {
                                _.forEach(siteList.sites, function (site) {
                                    var v;
                                    if (site.subunitIndex == (su)) {
                                        v = _.remove(ret, function (n) {
                                            return n == site.residueIndex
                                        });
                                        return v;
                                    }
                                });
                            });
                        }
                        return ret;
                    } else {
                        return _.range(1, scope.subunits[su - 1].sequence.length + 1);
                    }
                };

                scope.getSubunitRange = function () {
                    return _.range(1, scope.subunits.length + 1);
                };

                scope.makeSiteList = function () {
                    var temp = angular.copy(scope.referenceobj[scope.field].$$displayString);
                    _.set(scope.referenceobj, scope.field, siteList.siteList(scope.referenceobj[scope.field].$$displayString));
                    scope.referenceobj[scope.field].$$displayString = temp;
                };

                scope.redraw = function () {
                    scope.referenceobj.$$displayString = siteList.siteString(scope.referenceobj.sites);
                };

                scope.deleteObj = function (obj) {
                    scope.referenceobj.splice(scope.referenceobj.indexOf(obj), 1);
                };
            }
        };
    });

    
    ginasForms.directive('diversePlantForm', function (CVFields) {
        return {
            restrict: 'E',
            replace: true,
            scope: {
                parent: '='
            },
            templateUrl: baseurl + "assets/templates/forms/diverse-plant-form.html",
            link: function (scope) {
            }
        };
    });
    
    ginasForms.directive('isolateForm', [function () {
        return {
            restrict: 'A',
            require: '?form',
            link: function link(scope, element, iAttrs, formController) {

                if (!formController) {
                    return;
                }

                // Remove this form from parent controller
                formController.$$parentForm.$removeControl(formController);

                var _handler = formController.$setValidity;
                formController.$setValidity = function (validationErrorKey, isValid, cntrl) {
                    _handler(validationErrorKey, isValid, cntrl);
                    formController.$$parentForm.$setValidity(validationErrorKey, true, this);
                }
            }
        };
    }]);
})();