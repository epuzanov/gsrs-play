angular.module('filterListener', [])
    .factory('filterService', function (CVFields) {
        return {
            //this covers setting a variable based off of the codeSystemVocabularyTerm class
            _registerSystemCategoryFilter: function(scope, edit) {
                scope.$watch('filter', function (newValue) {
                    if (!_.isUndefined(newValue) && !_.isNull(newValue)) {
                        var obj = {};
                        if (!_.isUndefined(newValue.systemCategory)) {
                            CVFields.searchTags(scope.cv, newValue.systemCategory).then(function (response) {
                                obj = response[0];
                                    scope.obj = obj;
                                scope.edit = false;
                            });
                        }
                    }else{
                        console.log("clearing object");
                        scope.obj ={};
                    }
                });
            },

            _registerText: function(scope){
                var filter = scope.filter;
                scope.$watch('filter', function (newValue) {
                    if (!_.isUndefined(newValue)) {
                        if (scope.filterFunction) {
                            scope.filterFunction({model: newValue});
                        }
                    }
                });
            },

            _register: function (scope, edit) {
                if (scope.field == "$$systemCategory") {
                    this._registerSystemCategoryFilter(scope, edit);
                }else {
                    var filter = scope.filter;
                    scope.$watch('filter', function (newValue) {
                        console.log(newValue);
                        if (!_.isUndefined(newValue)) {
                            var obj = {};
                            //this returns the object to be filtered by the form
                            if (scope.filterFunction) {
                                var cv = scope.filterFunction({type: newValue});
                                CVFields.getCV(cv).then(function (response) {
                                    //obj = [];
                                    scope.values = response.data.content[0].terms;
                                    if (scope.values.length == 1) {
                                        obj = scope.values[0];
                                    }
                                    if (edit) {
                                        console.log("setting field object");

                                        scope.obj[scope.field] = obj;
                                    } else {
                                        console.log("setting object");

                                        scope.obj = obj;
                                    }
                                });
                            } else {
                                //this filters within the VocabularyTerm object
                                CVFields.getCV(scope.cv).then(function (response) {
                                    var filtered = [];
                                    var cv = response.data.content[0].terms;
                                    _.forEach(cv, function (term) {
                                        if (!_.isNull(term.filters)) {
                                            _.forEach(term.filters, function (f) {
                                                //this will capture one filter hit
                                                //need to figure out how to run compound filtering

                                                if (_.isEqual(newValue.value, f.split('=')[1])) {
                                                    filtered.push(term);
                                                }
                                            });
                                        }
                                    });
                                    if (filtered.length > 0) {
                                        scope.values = filtered;
                                    } else {
                                        // obj = {};
                                        if (response.data.content[0].editable == true) {
                                            cv = _.union(cv, other);
                                        }
                                        scope.values = cv;
                                    }
                                    if (scope.values.length == 1) {
                                        obj = scope.values[0];
                                    }
                                    if (edit) {
                                        scope.obj[scope.field] = obj;
                                    } else {
                                        scope.obj = obj;
                                    }
                                });
                            }

                        }
                    });
                }
            },

            _unregister: function (field) {
                if (filters.hasOwnProperty(field)) {
                    delete filters[field];
                }
            },
            _unregisterAll: function () {
                for (var field in filters) {
                    delete filters[field];
                }
            }
        };
    });

/*
angular.module('filterListener')
    .directive('filteredcv', function () {
        return {
            restrict: 'A',
            controller: ['$scope', 'filterService', function ($scope, filterService) {
                console.log("inside the filtered directive controller");
                console.log($scope);
                // Declare a mini-API to hand off to our service so the service
                // doesn't have a direct reference to this directive's scope.
                var api = {
                    filter: $scope.filter,
                    show: function () {
                        $scope.show = true;
                    },
                    hide: function () {
                        $scope.show = false;
                    }
                };

                // Register this filter with the filter service.
                    filterService._register(api);

            }]
        };
    });
*/
