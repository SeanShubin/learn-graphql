package com.seanshubin.learn.graphql.domain

import io.circe.Json
import sangria.ast.Document
import sangria.execution._
import sangria.macros._
import sangria.marshalling.circe._
import sangria.renderer.SchemaRenderer

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class Prototype extends Runnable {

  override def run(): Unit = {
    import SchemaDefinition._
    val query: Document =
      graphql"""
    query MyProduct {
      product(id: "2") {
        name
        description

        picture(size: 500) {
          width, height, url
        }
      }

      products {
        name
      }
    }
  """

    val result: Future[Json] =
      Executor.execute(schema, query, new ProductRepo)

    val json = Await.result(result, Duration.Inf)

    println(json)
    println(SchemaRenderer.renderSchema(schema))
  }
}
