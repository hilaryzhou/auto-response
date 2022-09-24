package com.hilary.web.utils;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Objects;

/**
 * StreamUtils
 *
 */
@Slf4j
public class RequestUtils {

    public static byte[] getRequestPostBytes(HttpServletRequest request)
            throws IOException {
        int contentLength = request.getContentLength();
        if (contentLength < 0) {
            return null;
        }
        byte[] buffer = new byte[contentLength];
        for (int i = 0; i < contentLength; ) {
            int len = request.getInputStream().read(buffer, i,
                    contentLength - i);
            if (len == -1) {
                break;
            }
            i += len;
        }
        return buffer;
    }

    public static String getRequestBodyStr(HttpServletRequest request) {
        try {
            byte[] buffer = getRequestPostBytes(request);
            if (Objects.isNull(buffer)) {
                return "";
            }
            String charEncoding = request.getCharacterEncoding();
            if (charEncoding == null) {
                charEncoding = "UTF-8";
            }
            return new String(buffer, charEncoding);
        } catch (IOException e) {
            log.error(String.format("getRequestBodyStr error, uri= %s ", request.getRequestURI()), e);
            return "";
        }
    }

}
