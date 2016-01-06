(function(){
    angular.module('classifier').controller('ClassificationController', function($scope, $http){
        $scope.done = false;
        $scope.classes = [];
        $scope.classify = function () {
            $scope.reset();
            $http.post('/classification/', {text: $scope.text}).then(function(res) {
                console.log(res.data);
                var results = res.data.results;
                process(results);
                $scope.done = true;
            }, function(res) {
                if(res.status == 400){
                    alert("Patent text cannot be empty");
                }
                else if(res.status == 500){
                    alert("Server error!");
                }
            });
        }
        function process(results){
            var range;
            var len = results.length;
            var temp = [];
            for(var i = 0; i < len; i++){
                results[i].probability = Math.abs(results[i].probability);
            }
            range = results[len - 1].probability - results[0].probability;
            var threshold = 0.3 * range + results[0].probability;
            var min = results[0].probability;
            var size = 0;
            for (var i = 0; i < results.length; i++) {
                var label = results[i];
                if(label.probability <= threshold){
                    var confidence = (label.probability - min)/(1.0 * range);
                    if(confidence <= 0.1)
                        label.star = 3;
                    else if(confidence <= 0.2)
                        label.star = 2;
                    else if(confidence <= 0.3)
                        label.star = 1;
                    temp.push(label);
                    size++;
                }
            }
            while(temp.length < 5){
                var label = results[size++];
                label.star = 1;
                temp.push(label);
            }
            $scope.classes = temp;
        }
        $scope.reset = function(){ 
            $scope.done = false;
            results = [];
        };
    });
})();