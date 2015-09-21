(function(){
	angular.module('fyp').controller('IndexController', function($scope, Section, Subsection, Class, Subclass, Utils, $http){
        $scope.tabdata = {};
        $scope.timedata = [];
        $scope.svg;
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
                legendTemplate: "<ul class=\"<%=name.toLowerCase()%>-legend\"><% for (var i=0; i<segments.length; i++){%><li><span style=\"background-color:<%=segments[i].fillColor%>\"></span><%if(segments[i].label){%><%=segments[i].label%><%}%></li><%}%></ul>"
            }
        }
        $scope.getDisplayType = function(){
            return Utils.getDisplayType();
        }
        $scope.showSection = function(){
            $("#tab-div>div.active").removeClass('active');
            if($scope.getDisplayType() == "piechart"){
                $("#section-tab").addClass('active');
                var ctx = document.getElementById("section-canvas").getContext("2d");
                var chart = new Chart(ctx).Pie($scope.tabdata.sections, $scope.getPiechartOption);
                $('#section-legend').html(chart.generateLegend());
            }else{
                $scope.showTimeSeriesChart($scope.timesection);
            }
        }
        $scope.showSubsection = function(){
            $("#tab-div>div.active").removeClass('active');
            if($scope.getDisplayType() == "piechart"){
                $("#subsec-tab").addClass('active');
                var ctx = document.getElementById("subsec-canvas").getContext("2d");
                var chart = new Chart(ctx).Pie($scope.tabdata.subsecs);
            }
            else{
                $scope.showTimeSeriesChart($scope.timesubsection);
            }
        }
        $scope.showClass = function(){
            $("#tab-div>div.active").removeClass('active');
            if($scope.getDisplayType() == "piechart"){
                $("#class-tab").addClass('active');
                var ctx = document.getElementById("class-canvas").getContext("2d");
                var chart = new Chart(ctx).Pie($scope.tabdata.classes);
            }
            else{
                $scope.showTimeSeriesChart($scope.timeclass);
            }
        }
        $scope.showSubclass = function(){
            $("#tab-div>div.active").removeClass('active');
            if($scope.getDisplayType() == "piechart"){
                $("#subclass-tab").addClass('active');
                var ctx = document.getElementById("subclass-canvas").getContext("2d");
                var chart = new Chart(ctx).Pie($scope.tabdata.subclasses);
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
                d3.select('#tab-div svg')
                    .datum(data)
                    .transition().duration(500).call(chart);
                nv.utils.windowResize(chart.update);

                return chart;
            });
        }
    });

    angular.module('fyp').controller('RadioController', function($scope, Utils){
        $scope.setType = function(type){
            Utils.setDisplayType(type);
        }
    });
})();
