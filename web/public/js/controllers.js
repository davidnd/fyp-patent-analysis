(function(){
	angular.module('fyp').controller('IndexController', function($scope, $compile, Section, Subsection, Class, Subclass, Utils, $http){
        $scope.tabdata = {};
        $scope.timedata = [];
        $scope.svg;
        angular.element(document).ready(function(){
            $scope.pieChartContext = document.getElementById("piechart-canvas").getContext("2d");
            $scope.legendElement = $('#piechart-legend');
        });
        $scope.piechart;
        activate();

        function activate(){
            Section.query(function(sections){
                // var ctx = document.getElementById("sectioncv").getContext("2d");
                $scope.tabdata.sections = Utils.generatePieChartData(sections);
                $scope.showSection();
            });
            Subsection.query(function(subsecs){
                $scope.tabdata.subsecs = Utils.generatePieChartData(subsecs);
            });
            Class.query(function(classes){
                $scope.tabdata.classes = Utils.generatePieChartData(classes);
            });
            Subclass.query(function(subclasses){
                $scope.tabdata.subclasses = Utils.generatePieChartData(subclasses);
            });
            $http.get('/timeseries/subclasses').then(function(data){
                var res = data.data;
                Utils.normalizeChartData(res);
                $scope.timesubclass = res;
            }, function(err){
                console.log("Error loading time series data");
            });

            $http.get('/timeseries/classes').then(function(data){
                var res = data.data;
                Utils.normalizeChartData(res);
                $scope.timeclass = res;
            }, function(err){
                console.log("Error loading time series data");
            });

            $http.get('/timeseries/subsections').then(function(data){
                var res = data.data;
                Utils.normalizeChartData(res);
                $scope.timesubsection = res;
            }, function(err){
                console.log("Error loading time series data");
            });

            $http.get('/timeseries/sections').then(function(data){
                var res = data.data;
                Utils.normalizeChartData(res);
                $scope.timesection = res;
            }, function(err){
                console.log("Error loading time series data");
            });
        }
        $scope.getPiechartOption = function(){
            return {
                legendTemplate: "<ul class=\"<%=name.toLowerCase()%>-legend\">\
                                <% for (var i=0; i<segments.length; i++){%>\
                                    <li>\
                                        <span style=\"background-color:<%=segments[i].fillColor%>\"></span>\
                                        <a href ng-click=\"getSectionDetails(<%=i+1%>)\"><%if(segments[i].label){%><%=segments[i].label%><%}%></a>\
                                    </li><%}%>\
                                </ul>"
            }
        }
        $scope.getDisplayType = function(){
            return Utils.getDisplayType();
        }
        $scope.showSection = function(){
            if($scope.getDisplayType() == "piechart"){
                if(typeof $scope.piechart !='undefined'){
                    $scope.piechart.destroy();
                }
                $scope.piechart = new Chart($scope.pieChartContext).Pie($scope.tabdata.sections, $scope.getPiechartOption());
                var el = $('#piechart-legend');
                el.html($scope.piechart.generateLegend()).show();
                $compile(el.contents())($scope);
            }else{
                $scope.showTimeSeriesChart($scope.timesection);
            }
        }
        $scope.showSubsection = function(){
            if($scope.getDisplayType() == "piechart"){
                $scope.piechart.destroy();
                $scope.piechart = new Chart($scope.pieChartContext).Pie($scope.tabdata.subsecs);
                $scope.legendElement.html($scope.piechart.generateLegend()).show();
            }
            else{
                $scope.showTimeSeriesChart($scope.timesubsection);
            }
        }
        $scope.showClass = function(){
            if($scope.getDisplayType() == "piechart"){
                $scope.piechart.destroy();
                $scope.piechart = new Chart($scope.pieChartContext).Pie($scope.tabdata.classes);
                $scope.legendElement.html($scope.piechart.generateLegend()).show();
            }
            else{
                $scope.showTimeSeriesChart($scope.timeclass);
            }
        }
        $scope.showSubclass = function(){
            if($scope.getDisplayType() == "piechart"){
                $scope.piechart.destroy();
                $scope.piechart = new Chart($scope.pieChartContext).Pie($scope.tabdata.subclasses);
                $scope.legendElement.html($scope.piechart.generateLegend()).show();
            }
            else{
                $scope.showTimeSeriesChart($scope.timesubclass);
            }
        }
        $scope.showTimeSeriesChart = function(data){
            nv.addGraph(function() {
                var chart = nv.models.stackedAreaChart()
                    .x(function(d) { return d[0] })
                    .y(function(d) { return d[1] })
                    .clipEdge(true)
                    .useInteractiveGuideline(true);

                chart.xAxis
                    .showMaxMin(false)
                    .tickFormat(function(d) { return d3.time.format('%x')(new Date(d)) });

                chart.yAxis
                  .tickFormat(d3.format(',.2f'));
                d3.selectAll("svg > *").remove();
                d3.select('#time-series svg')
                    .datum(data)
                    .transition().duration(500).call(chart);
                nv.utils.windowResize(chart.update);

                return chart;
            });
        }
        $scope.getSectionDetails = function(id){
            $http.get('/sections/details/' + id).then(function(results){
                $scope.tabdata.sectionDetails = Utils.generatePieChartData(results.data);
                $scope.piechart.destroy();
                $scope.piechart = new Chart($scope.pieChartContext).Pie($scope.tabdata.sectionDetails);
                var el = $('#piechart-legend');
                el.html($scope.piechart.generateLegend()).show(); 
            });
        }
    });

    angular.module('fyp').controller('RadioController', function($scope, Utils){
        $scope.setType = function(type){
            Utils.setDisplayType(type);
        }
    });
})();
