/*
 * The following allows grails to leverage a different url setting for maven central. This would
 * typically be passed along as a -D parameter to grails, ie: grails -Dmaven.central.url=http://...
 */
def mavenCentralUrl = 'http://repo1.maven.org/maven2/'
if (System.properties['maven.central.url']) {
    mavenCentralUrl = System.properties['maven.central.url']
}
println "Maven Central: ${mavenCentralUrl}"

grails.project.dependency.resolver = "ivy"

Boolean mavenCredsDefined = false
def mavenRealm
def mavenHost
def mavenUser
def mavenPassword

if (System.properties['maven.realm'] && System.properties['maven.host'] && System.properties['maven.user'] && System.properties['maven.password']) {
    mavenCredsDefined = true
    mavenRealm = System.properties['maven.realm']
    mavenHost = System.properties['maven.host']
    mavenUser = System.properties['maven.user']
    mavenPassword = System.properties['maven.password']

    println "Maven Credentials:\n\tRealm: ${mavenRealm}\n\tHost: ${mavenHost}\n\tUser: ${mavenUser}"
}

def grailsCentralUrl = 'http://grails.org/plugins'
if (System.properties['grails.central.url']) {
    grailsCentralUrl = System.properties['grails.central.url']
}
if(System.properties['disable.grails.central']) {
    println 'Grails Central: DISABLED'
} else {
    println "Grails Central: ${grailsCentralUrl}"
}

def grailsLocalRepo = 'grails-app/plugins'
def useFlatDir=false
if (System.properties['grails.local.repo']) {
        grailsLocalRepo = System.properties['grails.local.repo']
    useFlatDir=true
}
println "Grails Local Repo: ${grailsLocalRepo}"

grails.plugin.location.webrealms = 'webrealms'
grails.plugin.location.metricsweb = 'metricsweb'

grails.project.dependency.resolution = {
    inherits 'global' // inherit Grails' default dependencies
    log 'warn' // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    repositories {
        inherits false
        useOrigin true
        mavenLocal()
        if(useFlatDir) {
            flatDir name: 'grailsLocalRepo', dirs: "${grailsLocalRepo}"
        }
        grailsHome()
        grailsPlugins()
        mavenRepo mavenCentralUrl
        if(!System.properties['disable.grails.central']) {
            grailsRepo grailsCentralUrl
        }
    }

    if (mavenCredsDefined) {
        credentials {
            realm = mavenRealm
            host = mavenHost
            username = mavenUser
            password = mavenPassword
        }
    }


    rundeckVersion = System.getProperty("RUNDECK_VERSION", appVersion)
    println "Application Version: ${rundeckVersion}"

    plugins {
        test    ':code-coverage:2.0.3-3'
        compile (':less-asset-pipeline:2.0.8'){
            excludes 'groovy','groovy-templates','groovy-xml'
        }
        compile ':twitter-bootstrap:3.3.1'
        compile (':asset-pipeline:2.0.8'){
            excludes 'groovy','groovy-templates','groovy-xml'
        }
        runtime (":hibernate:3.6.10.18"){
            excludes ([ group: 'asm', name: 'asm'])
        }
        runtime ':mail:0.9', ':quartz:0.4.2', ':executor:0.3'
        provided ':codenarc:0.22'
        build   ':jetty:2.0.3'
    }

    dependencies {
        test 'org.yaml:snakeyaml:1.9', 'org.apache.ant:ant:1.7.1', 'org.apache.ant:ant-jsch:1.7.1', 
             'com.jcraft:jsch:0.1.50', 'log4j:log4j:1.2.16', 'commons-collections:commons-collections:3.2.1',
             'commons-codec:commons-codec:1.5', 'com.fasterxml.jackson.core:jackson-databind:2.0.2',
             'com.google.guava:guava:15.0'
        test("org.rundeck:rundeck-core:${rundeckVersion}"){
            changing=true
        }
             
        compile 'org.yaml:snakeyaml:1.9', 'org.apache.ant:ant:1.7.1', 'org.apache.ant:ant-jsch:1.7.1', 
                'com.jcraft:jsch:0.1.50', 'log4j:log4j:1.2.16', 'commons-collections:commons-collections:3.2.1',
                'commons-codec:commons-codec:1.5', 'com.fasterxml.jackson.core:jackson-databind:2.0.2',
                'com.codahale.metrics:metrics-core:3.0.1', 'com.google.guava:guava:15.0',
                'org.owasp.encoder:encoder:1.1.1', 'org.quartz-scheduler:quartz:1.7.3',
                'org.markdownj:markdownj-core:0.4',
                'com.googlecode.owasp-java-html-sanitizer:owasp-java-html-sanitizer:r239'

        if(useFlatDir) {
            // These are the dependencies of the grails plugins specified above.  When a flatDir repo is used to provide
            // grails plugins, it appears that the dependencies of the plugins are *not* evaluated.
            compile 'org.mozilla:rhino:1.7R4'
            compile('net.sourceforge.cobertura:cobertura:2.0.3') {
                excludes 'jetty', 'jetty-util'
            }
            //compile 'org.eclipse.jetty.aggregate:jetty-all:7.6.0.v20120127',
            compile 'org.eclipse.jdt.core.compiler:ecj:3.7.2'
        }

//        runtime 'org.codehaus.groovy:groovy:2.1.9', 'org.codehaus.groovy:groovy-xml:2.1.9', 'org.codehaus.groovy:groovy-templates:2.1.9'

//        compile('org.grails.plugins:asset-pipeline:2.0.8'){
//            excludes 'groovy-templates'
//        }

        compile("org.rundeck:rundeck-core:${rundeckVersion}") {
            changing = true
            excludes("xalan","xercesImpl")
        }
        compile("org.rundeck:rundeck-storage-filesys:${rundeckVersion}")

        runtime 'org.yaml:snakeyaml:1.9', 'org.apache.ant:ant:1.7.1', 'org.apache.ant:ant-launcher:1.7.1',
                'org.apache.ant:ant-jsch:1.7.1', 'com.jcraft:jsch:0.1.50', 'org.springframework:spring-test:3.0.5.RELEASE',
                'log4j:log4j:1.2.16', 'commons-collections:commons-collections:3.2.1', 'commons-codec:commons-codec:1.5',
                'com.fasterxml.jackson.core:jackson-databind:2.0.2', 'postgresql:postgresql:9.1-901.jdbc4',
                'com.google.guava:guava:15.0', 'org.owasp.encoder:encoder:1.1.1',
                'org.markdownj:markdownj-core:0.4',
                'com.googlecode.owasp-java-html-sanitizer:owasp-java-html-sanitizer:r239'
        runtime("org.rundeck:rundeck-core:${rundeckVersion}") {
            changing = true
            excludes("xalan","xercesImpl")
        }
        //runtime("org.rundeck:rundeck-jetty-server:${rundeckVersion}") {
        //    changing = true
        //}
    }
}
grails.war.resources = { stagingDir, args ->
    delete(file: "${stagingDir}/WEB-INF/lib/jetty-all-7.6.0.v20120127.jar")
    delete(file: "${stagingDir}/WEB-INF/lib/rundeck-jetty-server-${rundeckVersion}.jar")
    delete(file: "${stagingDir}/WEB-INF/lib/servlet-api-2.5.jar")
    if(System.getProperty('rundeck.war.additional')!=null){
        copy(todir: stagingDir ){
            fileset(dir: System.getProperty('rundeck.war.additional'))
        }
    }
}
