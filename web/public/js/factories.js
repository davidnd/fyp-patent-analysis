(function(){
    angular.module('fyp').factory('Utils', function($http){
        var displayType = "piechart";
        return {
            //util methods here
            generatePieChartData: generatePieChartData,
            normalizeChartData: normalizeChartData,
            setDisplayType: function (type) {
                displayType = type;
            },
            getDisplayType: function(){
                return displayType;
            },
            trimDescription: trimDescription
        };
        function generatePieChartData(data){
            var colors = [
                [0, 0, 255], //blue
                [0, 0, 128], //navy
                [0, 128, 0], //green
                [255, 165, 0], // orange
                [255, 0, 0], // red
                [128, 0, 128], // purple
                [128, 128, 0], //olive
                [0, 255, 0], //lime
                [128, 0, 0], //maroon
                [0, 255, 255], //aqua
                [0, 128, 128], //team
                [255, 0, 255], //fushua
                [128, 128, 128], //gray
                [255, 255, 0] //yellow
            ];
            var returneddata =[];
            var colorindex;
            for(var i=0;i<data.length;i++){
                var r, g, b, v;
                if(i>=colors.length){
                    r = Math.floor(Math.random() * 200);
                    g = Math.floor(Math.random() * 200);
                    b = Math.floor(Math.random() * 200);
                    v = Math.floor(Math.random() * 500);
                }else{
                    r = colors[i][0];
                    g = colors[i][1];
                    b = colors[i][2];
                }
                c = 'rgb(' + r + ', ' + g + ', ' + b + ')';
                h = 'rgb(' + (r+20) + ', ' + (g+20) + ', ' + (b+20) + ')';
                var temp = {};
                temp.value = data[i].count;
                temp.label = trimDescription(data[i].description);
                temp.color = c;
                temp.highlight = h;
                returneddata.push(temp);
            }
            return returneddata;
        };
        function normalizeChartData(data){
            var startingDate = 1199145600000;
            var timeArray = [];
            for(var i = 0; i<data.length; i++){
                var values = data[i].values;
                for(var j = 0; j<values.length; j++){
                    var point = values[j];
                    if(timeArray.indexOf(point[0])>-1 || parseInt(point[0]) < startingDate){
                        continue;
                    }
                    timeArray.push(point[0]);
                }
            }
            timeArray = timeArray.sort(function(x, y){
                return parseInt(x) - parseInt(y); 
            });
            for(var k=0; k<data.length; k++){
                data[k].key = trimDescription(data[k].key);
                var values = data[k].values;
                var normalizedValues = [];
                for(var j=0; j<timeArray.length; j++){
                    var checker = checkTimeInValues(timeArray[j], values);
                    if(checker.result){
                        normalizedValues.push([timeArray[j], values[checker.index][1]]);
                    }else{
                        normalizedValues.push([timeArray[j], 0]);
                    }
                }
                data[k].values = normalizedValues;
            }
        };
        function checkTimeInValues(time, values){
            for(var i=0; i<values.length;i++){
                var point = values[i];
                if(point[0]==time){
                    return {result: true, index: i};
                }
            }
            return {result:false, index: -1};
        };
        function trimDescription(description){
            var index1 = description.indexOf(',');
            var index2 = description.indexOf(';');
            index1 = (index1>-1 ? index1 : description.length);
            index2 = (index2>-1 ? index2 : description.length);
            return description.substring(0, Math.min(index2, index1));
        }
    });
    angular.module('fyp').factory('Section', function($resource){
        return $resource('/sections/:id', {id: '@id'});
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
