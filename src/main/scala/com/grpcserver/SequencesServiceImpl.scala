package com.grpcserver

import akka.NotUsed
import akka.stream.Materializer
import akka.stream.scaladsl.Source
import com.grpcserver.grpc._

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
      case _ => if (n < 0) baseCaseReturnValue else _fibonacci(1)
    }
  }

  override def returnFactorial(input: SequenceRequest): Future[SequenceResponse] = {
    val tn = if (input.termNumber + 1 <= maxFactorialTermNumber) input.termNumber else maxFactorialTermNumber

    println(s"Calculate Factorial Sequence with input term number = ${input.termNumber} => f(${input.termNumber})")

    Future.successful(SequenceResponse(factorial(tn)))
  }

  override def streamFactorial(input: SequenceRequest): Source[SequenceResponse, NotUsed] = {
    val tn = if (input.termNumber + 1 <= maxFactorialTermNumber) input.termNumber else maxFactorialTermNumber
    val range = List.range(0, tn + 1)

    Source(range).map(n => SequenceResponse(factorial(n)))
  }

  override def returnFibonacci(input: SequenceRequest): Future[SequenceResponse] = {
    val tn = if (input.termNumber + 1 <= maxFibonacciTermNumber) input.termNumber else maxFibonacciTermNumber

    println(s"Calculate Fibonacci Sequence with input term number = ${input.termNumber} => f(${input.termNumber})")

    Future.successful(SequenceResponse(fibonacciSequence(tn)))
  }

  override def streamFibonacci(input: SequenceRequest): Source[SequenceResponse, NotUsed] = {
    val tn = if (input.termNumber + 1 <= maxFibonacciTermNumber) input.termNumber else maxFibonacciTermNumber
    val range = List.range(0, tn + 1)

    Source(range).map(n => SequenceResponse(fibonacciSequence(n)))
  }
}
