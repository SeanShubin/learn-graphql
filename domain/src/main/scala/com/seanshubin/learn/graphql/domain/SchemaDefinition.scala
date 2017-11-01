package com.seanshubin.learn.graphql.domain

import sangria.macros.derive._
import sangria.schema.{Argument, Field, InterfaceType, ListType, ObjectType, OptionType, Schema, StringType, fields}

object SchemaDefinition {
  implicit val PictureType: ObjectType[Unit, Picture] =
    deriveObjectType[Unit, Picture](
      ObjectTypeDescription("The product picture"),
      DocumentField("url", "Picture CDN URL"))

  val IdentifiableType: InterfaceType[Unit, Identifiable] = InterfaceType(
    "Identifiable",
    "Entity that can be identified",

    fields[Unit, Identifiable](
      Field("id", StringType, resolve = _.value.id)))


  val ProductType: ObjectType[Unit, Product] =
    deriveObjectType[Unit, Product](
      Interfaces(IdentifiableType),
      IncludeMethods("picture"))

  val Id: Argument[String] = Argument("id", StringType)

  val QueryType: ObjectType[ProductRepo, Unit] = ObjectType("Query", fields[ProductRepo, Unit](
    Field("product", OptionType(ProductType),
      description = Some("Returns a product with specific `id`."),
      arguments = Id :: Nil,
      resolve = c ⇒ c.ctx.product(c arg Id)),

    Field("products", ListType(ProductType),
      description = Some("Returns a list of all available products."),
      resolve = _.ctx.products)))

  val schema: Schema[ProductRepo, Unit] = Schema(QueryType)
}
