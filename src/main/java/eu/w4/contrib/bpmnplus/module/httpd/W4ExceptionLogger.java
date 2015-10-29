package eu.w4.contrib.bpmnplus.module.httpd;

import org.apache.http.ExceptionLogger;

import eu.w4.common.log.Logger;

public class W4ExceptionLogger implements ExceptionLogger
{

  private Logger _logger;

  public W4ExceptionLogger(final Logger logger)
  {
    _logger = logger;
  }

  @Override
  public void log(Exception e)
  {
    _logger.error(e);
  }

}
