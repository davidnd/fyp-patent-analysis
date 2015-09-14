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
                console.log(res.length);
                //sorting data;
                // for(var i = 0; i<res.length; i++){
                //     res[i].values = res[i].values.sort(function(a,b){
                //         if(a[0]<b[0])
                //             return -1;
                //         if(a[0]>b[0])
                //             return 1;
                //         return 0;
                //     });
                // }
                Utils.normalizeChartData(res);
                $scope.timedata = res;
            }, function(err){
                console.log("Error loading time series data");
            });
        }
        
        $scope.getDisplayType = function(){
            return Utils.getDisplayType();
        }
        $scope.showSection = function(){
            $("#tab-div>div.active").removeClass('active');
            if($scope.getDisplayType() == "piechart"){
                $("#section-tab").addClass('active');
                var ctx = document.getElementById("section-canvas").getContext("2d");
                var chart = new Chart(ctx).Pie($scope.tabdata.sections);
            }else{
                
                nv.addGraph(function() {
                  var chart = nv.models.stackedAreaChart()
                  .x(function(d) { return d[0] })
                  .y(function(d) { return d[1] })
                  .clipEdge(true)
                  .useInteractiveGuideline(true)
                  ;

                  chart.xAxis
                  .showMaxMin(false)
                  .tickFormat(function(d) { return d3.time.format('%x')(new Date(d)) });

                  chart.yAxis
                  .tickFormat(d3.format(',.2f'));

                  d3.select('#tab-div svg')
                  .datum($scope.timedata)
                  .transition().duration(500).call(chart);

                  nv.utils.windowResize(chart.update);

                  return chart;
              });
            }
        }
        $scope.showSubsection = function(){
            $("#tab-div>div.active").removeClass('active');
            if($scope.getDisplayType() == "piechart"){
                $("#subsec-tab").addClass('active');
                var ctx = document.getElementById("subsec-canvas").getContext("2d");
                var chart = new Chart(ctx).Pie($scope.tabdata.subsecs);
            }
        }
        $scope.showClass = function(){
            $("#tab-div>div.active").removeClass('active');
            if($scope.getDisplayType() == "piechart"){
                $("#class-tab").addClass('active');
                var ctx = document.getElementById("class-canvas").getContext("2d");
                var chart = new Chart(ctx).Pie($scope.tabdata.classes);
            }
        }
        $scope.showSubclass = function(){
            $("#tab-div>div.active").removeClass('active');
            if($scope.getDisplayType() == "piechart"){
                $("#subclass-tab").addClass('active');
                var ctx = document.getElementById("subclass-canvas").getContext("2d");
                var chart = new Chart(ctx).Pie($scope.tabdata.subclasses);
            }
        }
    });

    angular.module('fyp').controller('RadioController', function($scope, Utils){
        $scope.setType = function(type){
            Utils.setDisplayType(type);
        }
    });
})();
