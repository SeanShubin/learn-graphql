package com.seanshubin.learn.graphql.console

import com.seanshubin.learn.graphql.domain.Prototype

trait DependencyInjection {
  val runner:Runnable = new Prototype()
}
