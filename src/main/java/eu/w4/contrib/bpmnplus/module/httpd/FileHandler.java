package eu.w4.contrib.bpmnplus.module.httpd;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.net.URLDecoder;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NFileEntity;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.nio.protocol.BasicAsyncRequestConsumer;
import org.apache.http.nio.protocol.BasicAsyncResponseProducer;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.nio.protocol.HttpAsyncRequestConsumer;
import org.apache.http.nio.protocol.HttpAsyncRequestHandler;
import org.apache.http.protocol.HttpContext;

class FileHandler implements HttpAsyncRequestHandler<HttpRequest>
{

  private static final String[] INDEXES = { "index.htm", "index.html", "default.htm", "default.html" };
  private final File _root;
  private final MimeTypeRegistry _mimeTypeRegistry;

  public FileHandler(final File docRoot)
  {
    super();
    this._root = docRoot;
    this._mimeTypeRegistry = new MimeTypeRegistry();
  }

  public HttpAsyncRequestConsumer<HttpRequest> processRequest(final HttpRequest request,
                                                              final HttpContext context)
  {
    return new BasicAsyncRequestConsumer();
  }

  public void handle(final HttpRequest request,
                     final HttpAsyncExchange httpexchange,
                     final HttpContext context)
    throws HttpException, IOException
  {
    final HttpResponse response = httpexchange.getResponse();
    handleInternal(request, response, context);
    httpexchange.submitResponse(new BasicAsyncResponseProducer(response));
  }

  private void handleInternal(final HttpRequest request,
                              final HttpResponse response,
                              final HttpContext context)
    throws HttpException, IOException
  {

    final String method = request.getRequestLine().getMethod().toUpperCase();
    if (!method.equals("GET") && !method.equals("HEAD") && !method.equals("POST"))
    {
      throw new MethodNotSupportedException(method + " method not supported");
    }

    String target = request.getRequestLine().getUri();
    if (target.indexOf('?') >= 0)
    {
      target = target.substring(0, target.indexOf('?'));
    }
    final File file = new File(this._root, URLDecoder.decode(target, "UTF-8"));
    if (!file.exists())
    {
      error(context,
            response,
            HttpStatus.SC_NOT_FOUND,
            "File [" + target + "] not found");
    }
    else if (file.isDirectory())
    {
      if (!target.endsWith("/"))
      {
        response.addHeader("Location", target + "/");
        response.setStatusCode(HttpStatus.SC_MOVED_TEMPORARILY);
        return;
      }
      for (final String index : INDEXES)
      {
        final File indexFile = new File(file, index);
        if (indexFile.exists() && indexFile.isFile() && indexFile.canRead())
        {
          serve(context, response, indexFile);
          return;
        }
      }
      error(context, response, HttpStatus.SC_FORBIDDEN, "Access denied");
    }
    else if (!file.canRead())
    {
      error(context, response, HttpStatus.SC_FORBIDDEN, "Access denied");
    }
    else
    {
      serve(context, response, file);
    }
  }

  private void serve(final HttpContext context,
                     final HttpResponse response,
                     final File file)
  {
    response.setStatusCode(HttpStatus.SC_OK);
    final String mimeType = _mimeTypeRegistry.guessMimeType(file);
    final ContentType contentType = ContentType.create(mimeType);
    final NFileEntity body = new NFileEntity(file,
                                             contentType);
    response.setEntity(body);
  }

  private void error(final HttpContext context,
                     final HttpResponse response,
                     final int statusCode,
                     final String message)
    {
      response.setStatusCode(statusCode);
      final NStringEntity entity = new NStringEntity(
        "<html><body><h1>" + message + "</h1></body></html>",
        ContentType.create("text/html", "UTF-8"));
      response.setEntity(entity);
    }
}
