package helpers

import com.typesafe.config.Config
import config.PulsarConfig
import org.apache.pulsar.client.admin.PulsarAdmin
import org.apache.pulsar.common.policies.data.RetentionPolicies
import org.slf4j.LoggerFactory
import play.api.{Configuration, Environment}

import javax.inject.Inject

object CreatePulsarTopics {

  val logger = LoggerFactory.getLogger(this.getClass)

  def main (args: Array[String]): Unit = {

    val playConfig = play.api.Configuration.load(Environment.simple())
    val pulsarConfig: PulsarConfig = playConfig.get[PulsarConfig]("pulsar")

    // Pass auth-plugin class fully-qualified name if Pulsar-security enabled
    val authPluginClassName = "pulsar"
    // Pass auth-param if auth-plugin class requires it
    val authParams = "param1=value1"
    val useTls = false

    val tlsAllowInsecureConnection = true
    val tlsTrustCertsFilePath = null
    val admin = PulsarAdmin.builder()
      //authentication(authPluginClassName, authParams)
      .serviceHttpUrl(pulsarConfig.clientURL)
      .tlsTrustCertsFilePath(tlsTrustCertsFilePath)
      .allowTlsInsecureConnection(tlsAllowInsecureConnection).build()

    try {

      //admin.namespaces().createNamespace(pulsarConfig.namespace)
      admin.namespaces().setRetention(pulsarConfig.namespace, new RetentionPolicies(-1, -1))
      admin.topics().createNonPartitionedTopic(pulsarConfig.topic)

    } catch {
      case t: Throwable =>
        t.printStackTrace()
    } finally {
      admin.close()
    }
  }

}
