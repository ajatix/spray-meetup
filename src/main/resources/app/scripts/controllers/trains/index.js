angular.module('SigmoidMeetup').controller('TrainsIndexController', function(TrainService, $scope, $location) {
    $scope.trains = TrainService.query();

    $scope.showTrain = function(id) {
        $location.path('/trains/' + id);
    };
});