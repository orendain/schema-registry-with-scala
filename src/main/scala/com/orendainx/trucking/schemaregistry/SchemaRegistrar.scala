package com.orendainx.trucking.schemaregistry

import java.util.Scanner

import com.hortonworks.registries.schemaregistry.avro.AvroSchemaProvider
import com.hortonworks.registries.schemaregistry.client.SchemaRegistryClient
import com.hortonworks.registries.schemaregistry.{SchemaCompatibility, SchemaMetadata, SchemaVersion}
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.Logger

import scala.collection.JavaConverters._

/**
  * Example of how to leverage Schema Registry's Java API with Scala.
  *
  * @author Edgar Orendain <edgar@orendainx.com>
  */
object SchemaRegistrar {

  // Create logger
  private val log = Logger(this.getClass)

  // Load default configuration file (application.conf in the resources folder)
  private val baseConfig = ConfigFactory.load()

  // Create a config object with the configuration properties to instantiate a SchemaRegistryClient
  private val clientConfig = Map[String, AnyRef](SchemaRegistryClient.Configuration.SCHEMA_REGISTRY_URL.name() -> baseConfig.getString("schema-registry.url"))
  private val schemaRegistryClient = new SchemaRegistryClient(clientConfig.asJava)

  /**
    * Entry point for our application.
    *
    * Registers all built-in schemas with the Schema Registry service.  These include the following schemas:
    * - TruckData
    * - TrafficData
    */
  def main(args: Array[String]): Unit = {
    setupSchema("schema.truck-data")
    setupSchema("schema.traffic-data")
  }


  /**
    * The steps involved:
    * - Retrieve configuration properties for the schema to register
    * - Build and register schema metadata
    * - Extract and submit schema content, creating a version of the schema and linking it the registered schema metadata
    *
    * @param schemaConfigPath Config path that holds schema registry configuration values.
    */
  def setupSchema(schemaConfigPath: String): Unit = {

    val config = baseConfig.getConfig(schemaConfigPath)

    // Retrieve configuration properties from the config file for the schema name, group name, and schema description
    val schemaName = config.getString("name")
    val schemaGroupName = config.getString("group-name")
    val schemaDescription = config.getString("description")
    val schemaTypeCompatibility = SchemaCompatibility.BACKWARD
    val schemaType = AvroSchemaProvider.TYPE

    // Build a new metadata for a schema using the properties extracted above
    val schemaMetadata = new SchemaMetadata.Builder(schemaName)
      .`type`(schemaType).schemaGroup(schemaGroupName)
      .description(schemaDescription)
      .compatibility(schemaTypeCompatibility)
      .build()

    // Resgister the new schema metadata using an instance of a Schema Registry Client
    val metadataRegistrationResult = schemaRegistryClient.registerSchemaMetadata(schemaMetadata)
    log.info(s"Schema metadata was registered with ID: $metadataRegistrationResult")


    // Get the filepath where we will find the schema text and read the entire file containing the Avro schema text
    val filepath = config.getString("avro.filepath")
    val scanner = new Scanner(getClass.getResourceAsStream(filepath)).useDelimiter("\\A")
    val avroSchemaContent = if (scanner.hasNext) scanner.next() else ""

    // Create a SchemaVersion object out of the Avro schema text we read in, then register it to Schema Registry using the client
    val schemaVersion = new SchemaVersion(avroSchemaContent, "Initial schema")
    val schemaVersionId = schemaRegistryClient.addSchemaVersion(schemaName, schemaVersion)

    log.info(s"Schema content: $avroSchemaContent")
    log.info(s"Schema version was registered with ID: $schemaVersionId")
  }
}
