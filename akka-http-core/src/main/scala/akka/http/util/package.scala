/**
 * Copyright (C) 2009-2014 Typesafe Inc. <http://www.typesafe.com>
 */

package akka.http

import language.implicitConversions
import java.nio.charset.Charset
import com.typesafe.config.Config
import org.reactivestreams.api.Producer
import akka.actor.{ ActorRefFactory, ActorContext, ActorSystem }
import akka.stream.scaladsl.Flow
import akka.stream.{ FlattenStrategy, FlowMaterializer }

package object util {
  private[http] val UTF8 = Charset.forName("UTF8")

  private[http] def actorSystem(implicit refFactory: ActorRefFactory): ActorSystem =
    refFactory match {
      case x: ActorContext ⇒ actorSystem(x.system)
      case x: ActorSystem  ⇒ x
      case _               ⇒ throw new IllegalStateException
    }

  private[http] implicit def enhanceByteArray(array: Array[Byte]): EnhancedByteArray = new EnhancedByteArray(array)
  private[http] implicit def enhanceConfig(config: Config): EnhancedConfig = new EnhancedConfig(config)
  private[http] implicit def enhanceString_(s: String): EnhancedString = new EnhancedString(s)

  private[http] implicit class FlowWithHeadAndTail[T](val underlying: Flow[Producer[T]]) {
    def headAndTail(materializer: FlowMaterializer): Flow[(T, Producer[T])] =
      underlying.map { p ⇒
        Flow(p).prefixAndTail(1).map { case (prefix, tail) ⇒ (prefix.head, tail) }.toProducer(materializer)
      }.flatten(FlattenStrategy.Concat())
  }
}

