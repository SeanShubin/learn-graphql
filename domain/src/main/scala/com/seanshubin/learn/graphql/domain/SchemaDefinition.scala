package com.seanshubin.learn.graphql.domain

import sangria.macros.derive._
import sangria.schema.{Argument, Field, InterfaceType, ListType, ObjectType, OptionType, Schema, StringType, fields}

object SchemaDefinition {

  case class Picture(width: Int, height: Int, url: Option[String])

  implicit val PictureType: ObjectType[Unit, Picture] =
    deriveObjectType[Unit, Picture](
      ObjectTypeDescription("The product picture"),
      DocumentField("url", "Picture CDN URL"))

  trait Identifiable {
    def id: String
  }

  val IdentifiableType: InterfaceType[Unit, Identifiable] = InterfaceType(
    "Identifiable",
    "Entity that can be identified",

    fields[Unit, Identifiable](
      Field("id", StringType, resolve = _.value.id)))

  case class Product(id: String, name: String, description: String) extends Identifiable {
    def picture(size: Int): Picture =
      Picture(width = size, height = size, url = Some(s"//cdn.com/$size/$id.jpg"))
  }

  val ProductType: ObjectType[Unit, Product] =
    deriveObjectType[Unit, Product](
      Interfaces(IdentifiableType),
      IncludeMethods("picture"))

  class ProductRepo {
    private val Products = List(
      Product("1", "Cheesecake", "Tasty"),
      Product("2", "Health Potion", "+50 HP"))

    def product(id: String): Option[Product] =
      Products find (_.id == id)

    def products: List[Product] = Products
  }

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
