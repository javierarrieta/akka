/**
 * Copyright (C) 2009-2014 Typesafe Inc. <http://www.typesafe.com>
 */

package akka.http.client

import akka.http.parsing.ParserOutput.{ ResponseOutput, ResponseStart }
import org.reactivestreams.api.Producer
import akka.http.model.HttpResponse

/**
 * INTERNAL API
 */
private[http] object HttpClientPipeline {
  def constructResponse(responseStart: ResponseStart, entityParts: Producer[ResponseOutput]): HttpResponse = {
    import responseStart._
    HttpResponse(statusCode, headers, createEntity(entityParts), protocol)
  }
}