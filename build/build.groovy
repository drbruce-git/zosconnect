@groovy.transform.BaseScript com.ibm.dbb.groovy.ScriptLoader baseScript
import com.ibm.dbb.build.*
import com.ibm.dbb.dependency.*
import com.ibm.dbb.repository.*
import java.util.*
import java.nio.file.*

// Initialize build
def utils = loadScript(new File("utils.groovy"))
def properties = utils.init()

// Invoke sar creation script
String sourceDir = utils.getSourceDir().getPath()
String workDir = utils.getWorkDir().getPath()
String sarFile = "$workDir/${utils.getSourceDir().getName()}.sar"
String cmd = "$zosConnectBin/zconbt.zos -pd=$sourceDir -f=$sarFile"
StringBuffer stdout = new StringBuffer()
StringBuffer stderr = new StringBuffer()

Process process = cmd.execute()
process.waitForProcessOutput(stdout, stderr)
if (stderr) {
	print("*! Error executing shell command: $cmd error: $stderr")
}
else {
	print("** Successfully executed shell command: $cmd out: $stdout")
}

utils.finalize(stderr)




