(function() {
    angular.module('fyp').controller('StackedAreaChartController', function($scope, ChartServices, Utils, $http){
        $scope.getChartType = function(){
            return ChartServices.getChartType();
        }
        $scope.$watch(function(){return ChartServices.getCpcLevel();}, function(newVal){
            if($scope.getChartType() != "Time-series"){
                return;
            }
            else displayChart(newVal);
        }, true);
        $scope.$watch(function(){return ChartServices.getChartType();}, function(newVal){
            if(newVal == "Time-series")
                displayChart($scope.getChartType());
        });
        function displayChart(newVal){
            if(newVal == "Classes"){
                $scope.showClass();
            }
            else if(newVal == "Subclasses"){
                $scope.showSubclass();
            }
            else if(newVal = "Section")
                $scope.showSection();
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

        $scope.showSection = function(){
            $scope.showTimeSeriesChart(ChartServices.timeseriesData().sectionData);
        }

        $scope.showClass = function(){
            $scope.showTimeSeriesChart(ChartServices.timeseriesData().classData);
        }

        $scope.showSubclass = function(){
            $scope.showTimeSeriesChart(ChartServices.timeseriesData().subclassData);
        }
    });
})();