package com.sigmoid.meetup.services

import akka.actor.Actor
import com.sigmoid.meetup._
import org.apache.spark.graphx.lib.PageRank
import org.apache.spark.graphx.{Edge, Graph}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkConf, SparkContext}

import scala.util.hashing.MurmurHash3

/**
 * Created by ajay on 17/10/15.
 */

/**
 * Define all the message objects to be passed around by the actors
 */
object SparkServiceActor {

  case class GetTrains(size: Int)
  case class GetTrain(id: Int)

  case class GetStations(size: Int)
  case class GetStation(id: String)

  case class TrainsBetween(depart: String, arrive: String)
  case class StopsBetween(depart: String, arrive: String)
  case class TrainRoute(train: String)
  case class CalculatePagerank(size: Int)

}

class SparkServiceActor extends Actor with Configuration {

  import SparkServiceActor._

  lazy val sparkConf: SparkConf = new SparkConf().setAppName(name).setMaster(master)

  lazy val sc: SparkContext = new SparkContext(sparkConf)
  lazy val sqlContext = new SQLContext(sc)

  // Loading the data from csv into SQL
  lazy val df = sqlContext.read.format(format).option("header", "true").option("inferSchema", "true").load(data)

  def getTrainsBetweenStations(from: String, to: String) = {
    df.select("train_no", "train_name", "station_code", "source_station_code", "source_station_name", "destination_station_code", "destination_station_name", "distance").where(s"source_station_code = '${from.padTo(4, ' ')}' and destination_station_code = '${to.padTo(4, ' ')}' and station_code = '${to.padTo(4, ' ')}'").distinct()
      .collect().map(row => Train(row.getString(0).split("'")(1).toInt, row.getString(1).trim, Station(row.getString(3).trim, row.getString(4).trim), Station(row.getString(5).trim, row.getString(6).trim), row.getInt(7)))
  }

  def getStopsBetweenStations(from: String, to: String) = {
    df.select("train_no", "train_name", "source_station_code", "destination_station_code", "station_code", "station_name", "isl_no", "distance").where(s"source_station_code = '${from.padTo(4, ' ')}' and destination_station_code = '${to.padTo(4, ' ')}'")
      .collect().groupBy(row => (row.getString(0), row.getString(1))).map(row => Journey(row._1._1.split("'")(1).toInt, row._1._2.trim, row._2.map(stop => Stop(stop.getString(4).trim, stop.getString(5).trim, stop.getInt(6), stop.getInt(7)))))
  }

  // Extracting data for the graph
  lazy val trains = df.select("train_no", "train_name", "station_code", "source_station_code", "source_station_name", "destination_station_code", "destination_station_name", "distance").where("station_code = destination_station_code")
    .map(row => Train(row.getString(0).split("'")(1).toInt, row.getString(1).trim, Station(row.getString(3).trim, row.getString(4).trim), Station(row.getString(5).trim, row.getString(6).trim), row.getInt(7))).distinct(numPartitions = 1)
  lazy val stations = trains.flatMap(train => Array(train.source, train.destination)).distinct(numPartitions = 1)

  lazy val stationCodes: RDD[(Long, Station)] = stations.map(row => (MurmurHash3.stringHash(row.id).toLong, row)).sortBy(key => key._2.id)
  lazy val stationEdges: RDD[Edge[Train]] = trains.map(row => Edge(MurmurHash3.stringHash(row.source.id).toLong, MurmurHash3.stringHash(row.destination.id).toLong, row)).sortBy(key => key.attr.id)

  // Creating the graph
  lazy val graph: Graph[Station, Train] = Graph(stationCodes, stationEdges)
  graph.persist()

  // Calculating incoming and outgoing trains
  lazy val inDegrees = graph.inDegrees
  lazy val outDegrees = graph.outDegrees
  lazy val pageRank = PageRank.run(graph, 5)

  lazy val incoming: RDD[Station] = inDegrees.sortBy(key => key._2, ascending = false, numPartitions = 1).map(degree =>
    stationCodes.filter(key => key._1 == degree._1).collect().head._2.copy(incoming = Some(degree._2))
  )

  lazy val outgoing: RDD[Station] = outDegrees.sortBy(key => key._2, ascending = false, numPartitions = 1).map(degree =>
    stationCodes.filter(key => key._1 == degree._1).collect().head._2.copy(outgoing = Some(degree._2))
  )

  lazy val pageRanked: RDD[Station] = pageRank.vertices.sortBy(key => key._2, ascending = false, numPartitions = 1).map(degree =>
    stationCodes.filter(key => key._1 == degree._1).collect().head._2.copy(pageRank = Some(degree._2))
  )

  def receive: Receive = {
    case GetTrains(size) =>
      sender ! trains.take(size).toSeq
    case GetTrain(id) =>
      sender ! trains.filter(train => train.id == id).collect().head
    case GetStations(size) =>
      sender ! stations.take(size).toSeq
    case GetStation(id) =>
      sender ! stations.filter(station => station.id == id).collect().head
    case TrainsBetween(depart, arrive) =>
      sender ! getTrainsBetweenStations(depart, arrive).toSeq
    case StopsBetween(depart, arrive) =>
      sender ! getStopsBetweenStations(depart, arrive).toSeq
    case CalculatePagerank(size) =>
      sender ! pageRanked.take(size).toSeq
  }


}
