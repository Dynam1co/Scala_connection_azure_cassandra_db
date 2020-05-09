import com.datastax.driver.core._
import javax.net.ssl._
import java.io.File
import java.io.FileInputStream
import java.util.Properties
import java.security._
import com.datastax.driver.core.RemoteEndpointAwareJdkSSLOptions
import com.datastax.driver.core.Session
import java.security.KeyStore

class CassandraUtils (
                        var cluster: Cluster,
                        var cassandraHost: String,
                        var cassandraPort: String,
                        var cassandraUsername: String,
                        var cassandraPassword: String,
                        var sslKeyStoreFile: File,
                        var sslKeyStorePassword: String = "changeit") {

  def loadCassandraConnectionDetails(): Unit = {
    val prop = new Properties()
    prop.load(new FileInputStream("src/main/resources/default.properties"))

    cassandraHost = prop.getProperty("cassandra_host")
    cassandraPort = prop.getProperty("cassandra_port")
    cassandraUsername = prop.getProperty("cassandra_username")
    cassandraPassword = prop.getProperty("cassandra_password")
    var ssl_keystore_file_path = prop.getProperty("ssl_keystore_file_path")
    val ssl_keystore_password = prop.getProperty("ssl_keystore_password")

    // If ssl_keystore_file_path, build the path using JAVA_HOME directory.
    if (ssl_keystore_file_path == null || ssl_keystore_file_path.isEmpty) {
      val javaHomeDirectory = System.getenv("JAVA_HOME")
      if (javaHomeDirectory == null || javaHomeDirectory.isEmpty) throw new Exception("JAVA_HOME not set")

      ssl_keystore_file_path = new StringBuilder(javaHomeDirectory).append("/jre/lib/security/cacerts").toString
    }

    sslKeyStorePassword = if (ssl_keystore_password != null && !(ssl_keystore_password.isEmpty)) ssl_keystore_password
    else sslKeyStorePassword

    sslKeyStoreFile = new File(ssl_keystore_file_path)

    if (!sslKeyStoreFile.exists || !sslKeyStoreFile.canRead) throw new Exception(String.format("Unable to access the SSL Key Store file from %s", ssl_keystore_file_path))
  }

  def getCluster: Cluster = cluster

  def close(): Unit = {
    cluster.close()
  }

  def getSession: Session = {
    try {
      //Load cassandra endpoint details from config.properties
      loadCassandraConnectionDetails()

      val keyStore = KeyStore.getInstance("JKS")

      try {
        val is = new FileInputStream(sslKeyStoreFile)
        try keyStore.load(is, sslKeyStorePassword.toCharArray)
        finally if (is != null) is.close()
      }

      val kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm)
      kmf.init(keyStore, sslKeyStorePassword.toCharArray)

      val tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm)
      tmf.init(keyStore)

      // Creates a socket factory for HttpsURLConnection using JKS contents.
      val sc = SSLContext.getInstance("TLSv1.2")
      sc.init(kmf.getKeyManagers, tmf.getTrustManagers, new SecureRandom)

      val sslOptions = RemoteEndpointAwareJdkSSLOptions.builder.withSSLContext(sc).build
      cluster = Cluster.builder.addContactPoint(cassandraHost).withPort(cassandraPort.toInt).withCredentials(cassandraUsername, cassandraPassword).withSSL(sslOptions).build

    } catch {
      case ex: Exception =>
        ex.printStackTrace()
    }

    cluster.connect
  }
}
