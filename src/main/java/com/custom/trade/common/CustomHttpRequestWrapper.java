package com.custom.trade.common;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class CustomHttpRequestWrapper extends HttpServletRequestWrapper {

    private byte[] bodyData;
    private String requestBody;

    public CustomHttpRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);

        InputStream inputStream = super.getInputStream();
        bodyData = toByteArray(inputStream);
        requestBody = new String(bodyData, StandardCharsets.UTF_8);
    }

    private byte[] toByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[16384];
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        return buffer.toByteArray();
    }

    @Override
    public String[] getParameterValues(String param){
        String[] values = super.getParameterValues(param);
        if (values == null) {
            return null;
        }
        int cnt = values.length;
        String[] encodedValues = new String[cnt];
        for (int i = 0; i < cnt; i++) {
            encodedValues[i] = cleanXSS(values[i]);
        }
        return encodedValues;

    }

    @Override
    public String getHeader(String name){
        String value = super.getHeader(name);
        return value == null || value.equals("") || value.isEmpty() ? null : cleanXSS(value);
    }

    private String cleanXSS(String value) {

        value = value.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
        value = value.replaceAll("\\(", "&#40;").replaceAll("\\)", "&#41;");
        value = value.replaceAll("'", "&#39;");
        value = value.replaceAll("eval\\((.*)\\)", "");
        //value = value.replaceAll("[\\\"\\'][\\s]*javascript:[\\s]*(.*)[\\\"\\']", "\"\"");

        return value;
    }

    public String getRequestBody() {
        return this.requestBody;
    }

    public ServletInputStream getInputStream() throws IOException {
        final ByteArrayInputStream bis = new ByteArrayInputStream(bodyData);
        return new ServletImpl(bis);
    }
}

class ServletImpl extends ServletInputStream {
    private InputStream is;

    public ServletImpl(InputStream bis){
        is = bis;
    }


    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public void setReadListener(ReadListener readListener) {}

    @Override
    public int read() throws IOException {
        return is.read();
    }

    @Override
    public int read(byte[] b) throws IOException {
        return is.read(b);
    }



}
