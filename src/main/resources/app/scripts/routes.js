angular.module('SigmoidMeetup').config(function($routeProvider) {
    $routeProvider
        .when('/', {
            redirectTo: '/trains'
        })
        .when('/trains', {
            templateUrl: "templates/trains/index.html",
            controller: "TrainsIndexController"
        })
        .when('/trains/:id', {
            templateUrl: "templates/trains/show.html",
            controller: "TrainsShowController"
        })
        .when('/stations', {
            templateUrl: "templates/stations/index.html",
            controller: "StationsIndexController"
        })
        .when('/stations/:id', {
            templateUrl: "templates/stations/show.html",
            controller: "StationsShowController"
        })
});