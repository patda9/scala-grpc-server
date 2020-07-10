package com.grpcserver

import akka.NotUsed
import akka.stream.Materializer
import akka.stream.scaladsl.Source
import com.grpcserver.grpc._

import scala.concurrent.duration._
import scala.concurrent.Future

class SequencesServiceImpl(implicit mat: Materializer) extends SequencesService {
  val maxFactorialTermNumber = 15
  val maxFibonacciTermNumber = 40

  def factorial(n: Int): List[Int] = {
    val baseCaseReturnValue = List(1)

    def _factorial(_n: Int,
                   acc: List[Int] = baseCaseReturnValue): List[Int] = {
      if (_n < n) _factorial(_n + 1, acc ++ List(acc.last * _n))
      else acc
    }

    n match {
      case 0 => baseCaseReturnValue
      case _ => if (n < 0) baseCaseReturnValue else _factorial(1)
    }
  }

  def fibonacciSequence(n: Int): List[Int] = {
    val baseCaseReturnValue = List(0)

    def _fibonacci(_n: Int,
                   fn1: Int = 1,
                   fn2: Int = 0,
                   acc: List[Int] = baseCaseReturnValue): List[Int] = {
      if (_n < n) {
        return _fibonacci(_n + 1, fn1 + fn2, fn1, acc ++ List(fn1 + fn2))
      }

      acc
    }

    n match {
      case 0 => baseCaseReturnValue
      case _ => if (n < 0) baseCaseReturnValue else _fibonacci(0)
    }
  }

  override def returnCalculationResult(in: SequenceRequest): Future[SequenceResponse] = {
    val calculationType = in.`type`
    val maxTermNumber = if (calculationType.equals(0)) maxFactorialTermNumber else maxFibonacciTermNumber

    val tn = if (in.termNumber <= maxTermNumber) in.termNumber else maxTermNumber

    println(s"Calculate ${calculationType} Sequence with input term number = ${in.termNumber} => f(${in.termNumber})")

    Future.successful(SequenceResponse(if (calculationType.equals(0)) factorial(tn) else fibonacciSequence(tn)))
  }

    override def streamCalculationResults(in: SequenceRequest): Source[SequenceResponse, NotUsed] = {
      val calculationType = in.`type`

      println(s"Stream ${calculationType} Sequence with input from term number from 0 ${in.termNumber}")

      val maxTermNumber = if (calculationType.equals(0)) maxFactorialTermNumber else maxFibonacciTermNumber

      println(maxTermNumber)

      val tn = if (in.termNumber <= maxTermNumber) in.termNumber else maxTermNumber

      val range = List.range(0, tn + 1)

      Source(range)
        .throttle(1, .5.seconds)
        .map(n => SequenceResponse(if (calculationType.equals(0)) factorial(n) else fibonacciSequence(n)))
    }
}
