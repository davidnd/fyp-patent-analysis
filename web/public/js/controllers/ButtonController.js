(function(){
    angular.module('fyp').controller('ButtonController', function($scope, ChartServices){
        $scope.setChartType = function(type) {
            ChartServices.setChartType(type);
        }
        $scope.getChartType = function(){
            return ChartServices.getChartType();
        }
        $scope.setCpcLevel = function(level) {
            ChartServices.setCpcLevel(level);
        }
        $scope.getCpcLevel = function() {
            return ChartServices.getCpcLevel();
        }
    });
})();