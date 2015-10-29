package eu.w4.contrib.bpmnplus.module.httpd;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.concurrent.TimeUnit;

import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.nio.bootstrap.HttpServer;
import org.apache.http.impl.nio.bootstrap.ServerBootstrap;
import org.apache.http.impl.nio.reactor.IOReactorConfig;

import eu.w4.common.exception.CheckedException;
import eu.w4.common.log.Logger;
import eu.w4.common.log.LoggerFactory;
import eu.w4.engine.client.configuration.ConfigurationHelper;
import eu.w4.engine.core.module.external.ExternalModule;
import eu.w4.engine.core.module.external.ExternalModuleContext;

public class HttpServerModule implements ExternalModule
{
  private static final long TIME_BETWEEN_SHUTDOWN_AND_STARTUP = 1000;

  private static final String CONFIGURATION_KEY__PORT = "module.http.port";
  private static final String CONFIGURATION_KEY__ROOT = "module.http.root";

  private Logger _logger = LoggerFactory.getLogger(HttpServerModule.class.getName());

  private ExternalModuleContext _context;

  private HttpServer _server;

  @Override
  public void startup(final ExternalModuleContext context) throws CheckedException, RemoteException
  {
    _context = context;

    final int port = ConfigurationHelper.getIntValue(_context.getParameters(), CONFIGURATION_KEY__PORT);
    final String root = ConfigurationHelper.getStringValue(_context.getParameters(), CONFIGURATION_KEY__ROOT);

    final File rootFile = new File(root);

    if (!rootFile.exists())
    {
      _logger.error("Http root directory [" + root + "] does not exists");
      return;
    }
    if (!rootFile.isDirectory())
    {
      _logger.error("Http root directory [" + root + "] does not exists");
      return;
    }

    final IOReactorConfig config = IOReactorConfig.custom()
      .setSoTimeout(15000)
      .setTcpNoDelay(true)
      .setIoThreadCount(Runtime.getRuntime().availableProcessors() * 2)
      .build();

    _server = ServerBootstrap.bootstrap()
      .setListenerPort(port)
      .setServerInfo("BpmnPlusEngineHttpServerModule/1.0" )
      .setIOReactorConfig(config)
      .setExceptionLogger(new W4ExceptionLogger(_logger))
      .setConnectionReuseStrategy(new DefaultConnectionReuseStrategy())
      .registerHandler("*", new FileHandler(rootFile))
      .create();

    try
    {
      _server.start();
    }
    catch (final IOException e)
    {
      _logger.error("HTTP server could not start", e);
      _server = null;
    }

    if (_logger.isInfoEnabled())
    {
      _logger.info("HttpServer module successfully started");
    }
  }

  @Override
  public void shutdown() throws CheckedException, RemoteException
  {
    if (_server != null)
    {
      _server.shutdown(5, TimeUnit.SECONDS);
    }
  }

  @Override
  public long getShutdownStartupSleepTime() throws CheckedException, RemoteException
  {
    return TIME_BETWEEN_SHUTDOWN_AND_STARTUP;
  }

}
