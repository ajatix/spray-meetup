angular.module('SigmoidMeetup').controller('TrainsShowController', function(TrainService, $scope, $routeParams) {
    $scope.train = TrainService.get({id: $routeParams.id});
});