(function(){
    angular.module('fyp').factory('Utils', function($http){
        var displayType = "piechart";
        return {
            //util methods here
            generatePieChartData: generatePieChartData,
            setDisplayType: function (type) {
                displayType = type;
            },
            getDisplayType: function(){
                return displayType;
            }
        };
        function generatePieChartData(data){
            var returneddata =[];
            for(var i=0;i<data.length;i++){
                r = Math.floor(Math.random() * 200);
                g = Math.floor(Math.random() * 200);
                b = Math.floor(Math.random() * 200);
                v = Math.floor(Math.random() * 500);
                c = 'rgb(' + r + ', ' + g + ', ' + b + ')';
                h = 'rgb(' + (r+20) + ', ' + (g+20) + ', ' + (b+20) + ')';
                var temp = {};
                temp.value = data[i].count;
                temp.label = data[i].description;
                temp.color = c;
                temp.highlight = h;
                returneddata.push(temp);
            }
            return returneddata;
        };
    });
    angular.module('fyp').factory('Section', function($resource){
        return $resource('/sections');
    });
    angular.module('fyp').factory('Subsection', function($resource){
    	return $resource('/subsections');
    });
    angular.module('fyp').factory('Class', function($resource){
        return $resource('/classes');
    });
    angular.module('fyp').factory('Subclass', function($resource){
        return $resource('/subclasses');
    });
})();
