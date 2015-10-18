# spray-meetup
A tutorial on Spray, Spark and AngularJS

### About the Application
This is an application for viewing train routes between two stations in India.

### Flow of the application
The application comprises of three main layers
1. **Processing the data** - Apache Spark
2. **Serving the data through a REST API** - Spray.io
3. **Consuming the data through a frontend** - AngularJS

### Requisite knowledge
+ Scala
+ Javascript
+ HTML
+ REST API concepts

[Optional]
+ Graph algorithms
+ Apache Spark
+ Actor model for concurrency
+ SQL

### Running the code
[Install sbt](http://www.scala-sbt.org)
```bash
$ sbt
# From sbt shell
>>> clean
>>> compile
>>> run

# Alternatively
$ sbt clean compile run
```

You should see the server running at [http://localhost:8000] from browser

API commands
Endpoint - [http://localhost:8000/v1/spark]
/train - List of trains
/train/:id - Train info
/station - List of stations
/station/:id - Station info
/calculate/train?depart=CODE&arrive=CODE - List of trains between two station codes
/calculate/station?depart=CODE&arrive=CODE - List of stops between two station codes

---

This project barely scratches the surface of the world of application development and is meant to help familiarize beginners by providing a foundation upon which one could work on their own idea.

Have fun hacking away. 

> Ajay Viswanathan
