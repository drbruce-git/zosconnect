@groovy.transform.BaseScript com.ibm.dbb.groovy.ScriptLoader baseScript
import com.ibm.dbb.repository.*
import com.ibm.dbb.dependency.*
import com.ibm.dbb.build.*
import groovy.transform.*

def init() {
	// Start up messages
	def startTime = new Date().format("yyyyMMdd.hhmmss")
	println("** Build start at $startTime")
	println("java.version="+System.getProperty("java.runtime.version"))
	println("java.home="+System.getProperty("java.home"))
	println("user.dir="+System.getProperty("user.dir"))
	
	// Load/Set build properties
	def properties = BuildProperties.getInstance()
	properties.load(new File("${getScriptDir()}/build.properties"))
	validateRequiredProperties(properties)
	properties.buildGroup = "${getSourceDir().getName()}" as String
	properties.buildLabel = "build.${startTime}" as String
	println("** Build properties at startup:")
	println(properties.list())
	
	// Create Build Result
	def buildResult = getRepoClient().createBuildResult(properties.buildGroup, properties.buildLabel)
	buildResult.setState(buildResult.PROCESSING)
	buildResult.save()
	println("** Build result created at ${buildResult.getUrl()}")	

	return properties
}

def validateRequiredProperties(BuildProperties properties) {
   [zosConnectBin:properties.zosConnectBin, url:properties.'dbb.RepositoryClient.url', userId:properties.'dbb.RepositoryClient.userId', pwFile:properties.'dbb.RepositoryClient.passwordFile'].each { key, value ->
	  if (!value)
	  {
		 println "Missing required build property: $key"
		 System.exit(2)
	  }
}

def getRepoClient() {
	def repoClient = new RepositoryClient()
	repoClient.setForceSSLTrusted(true))
    return repoClient
}

def getSourceDir() {
	File sourceDir = new File(${getScriptDir()}).getParent()
	return sourceDir
}

def getWorkDir() {
	File workDir = new File(getSourceDir(), "out")
	if (!workDir.exists())
		workDir.mkdir()
	return workDir
}

def finalize(StringBuffer stderr) {
	def properties = BuildProperties.getInstance()
	
	// Update Build Result 
	def buildResult = getRepoClient().getBuildResult(properties.buildGroup, properties.buildLabel)
	if (stderr) {
		buildResult.setStatus(buildResult.ERROR)
		buildResult.addProperty("error", stderr.toString())
	}
	buildResult.setState(buildResult.COMPLETE)
	buildResult.save()	
}
