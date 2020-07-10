package com.grpcserver

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{ HttpRequest, HttpResponse }
import akka.http.scaladsl.{ Http, HttpConnectionContext }
import akka.stream.{ ActorMaterializer, Materializer }
import com.typesafe.config.ConfigFactory

import com.grpcserver.grpc._

import scala.concurrent.{ ExecutionContext, Future }

object Server {
    def main(args: Array[String]): Unit = {
        val conf = ConfigFactory
        .parseString("akka.http.server.preview.enable-http2 = on")
        .withFallback(ConfigFactory.defaultApplication())
        val system = ActorSystem("HelloWorld", conf)
        new Server(system).run()
    }
}

class Server(system: ActorSystem) {
    def run(): Future[Http.ServerBinding] = {
        implicit val sys: ActorSystem = system
        implicit val mat: Materializer = ActorMaterializer()
        implicit val ec: ExecutionContext = sys.dispatcher

        val service: HttpRequest => Future[HttpResponse] =
            SequencesServiceHandler(new SequencesServiceImpl())

        val binding = Http().bindAndHandleAsync(
            service,
            interface = "127.0.0.1",
            port = 9990,
            connectionContext = HttpConnectionContext())

        binding.foreach { binding => println(s"gRPC server bound to envoy at: ${binding.localAddress}") }

        binding
    }
}