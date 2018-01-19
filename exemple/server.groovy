#!/usr/bin/env groovy

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.*
import groovy.servlet.*
import org.magery.Compiler

@GrabConfig(systemClassLoader= true)
@Grapes([
  @Grab(group='org.magery', module='groovy-magery', version='0.1'),
  @Grab(group='org.eclipse.jetty.aggregate', module='jetty-all', version='7.6.15.v20140411')])

def startJetty() {
    def server = new Server(9090)

    def handler = new ServletContextHandler(ServletContextHandler.SESSIONS)
    handler.contextPath = '/'
    handler.resourceBase = '.'
    handler.addServlet(GroovyServlet, '/index.html')
    def filesHolder = handler.addServlet(DefaultServlet, '/')
    filesHolder.setInitParameter('resourceBase', './public')

    server.handler = handler
    server.start()
    println "Open browser on http://localhost:9090/index.html"
}

println "Starting Jetty, press Ctrl+C to stop."
startJetty()
