package eu.w4.contrib.bpmnplus.module.httpd;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MimeTypeRegistry
{

  private static final String DEFAULT_MIME_TYPE = "application/octet-stream";
  private Map<String, String> _mimeByExtension = new HashMap<String, String>();

  public MimeTypeRegistry()
  {
    _mimeByExtension.put("txt", "text/plain");
    _mimeByExtension.put("htm", "text/html");
    _mimeByExtension.put("html", "text/html");
    _mimeByExtension.put("css", "text/css");
    _mimeByExtension.put("js", "text/javascript ");
    _mimeByExtension.put("png", "image/png");
    _mimeByExtension.put("jpg", "image/jpeg");
    _mimeByExtension.put("jpeg", "image/jpeg");
    _mimeByExtension.put("tif", "image/tiff");
    _mimeByExtension.put("tiff", "image/tiff");
    _mimeByExtension.put("bmp", "image/x-windows-bmp");
    _mimeByExtension.put("pdf", "application/pdf");
  }

  public String guessMimeType(final File file)
  {
    final String filename = file.getName();
    final int dotIndex = filename.lastIndexOf('.');
    if (dotIndex <= 0 || dotIndex >= filename.length() - 1)
    {
      return DEFAULT_MIME_TYPE;
    }
    final String extension = filename.substring(dotIndex + 1);
    final String mimeType = _mimeByExtension.get(extension);
    if (mimeType == null)
    {
      return DEFAULT_MIME_TYPE;
    }
    else
    {
      return mimeType;
    }
  }
}
