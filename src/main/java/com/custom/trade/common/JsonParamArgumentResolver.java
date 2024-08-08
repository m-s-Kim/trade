package com.custom.trade.common;

import com.custom.trade.exception.CustomException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;


@Slf4j
@Component
public class JsonParamArgumentResolver implements HandlerMethodArgumentResolver {



    private final ObjectMapper objectMapper;

    public JsonParamArgumentResolver(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
       // return parameter.getParameterType().equals(List.class) &&
       //         parameter.getGenericParameterType().getTypeName().startsWith("java.util.List<java.util.Map<java.lang.String, java.lang.Object>>");
        return Box.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter param, ModelAndViewContainer mavContainer
            , NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        Box box = new Box();
        String bodyStr = "";

        try {

            HttpServletRequestWrapper request = null;
            request = new HttpServletRequestWrapper((HttpServletRequest) webRequest.getNativeRequest());
            Map<String, Object> headerInfo = this.getHeaderInfo(request);

            // GET
            if(request.getMethod().equals("GET")){
                log.debug("GET");
                convertParamMapToBox(request.getParameterMap(), box);
            }else{
                // POST
                Box bodyBox = new Box();


                // form 데이터 일경우
                if(((String) headerInfo.get("content-type")).indexOf("form") != -1){
                    log.debug("Form DATA");
                    box = this.convertFormMaptoBox(request);
                }else{
                    log.debug("*********************JSON****************************");
                    bodyStr = getRequestBody(webRequest);
                    Map<String, Object> tMap = new ObjectMapper().readValue(bodyStr,Map.class);

                    if(tMap != null){
                        this.setMapToBox(box, tMap);
                    }

                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return box;
    }


    private String getRequestBody(NativeWebRequest webRequest) throws  IOException {
        HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        String jsonbody = (String) servletRequest.getAttribute("body");
        if(jsonbody == null){
            try{
                jsonbody = new String(toByteArray(servletRequest.getInputStream()), StandardCharsets.UTF_8);
            }catch (Exception e){
                throw new CustomException("ARGUMENTRESOLVER ERROR");
            }
        }
        return jsonbody;
    }

    private void setMapToBox(Box box, Map<String, Object> map) {
        Set set = map.keySet();
        Iterator it = set.iterator();
        log.debug("***************************************");
        while (it.hasNext()) {
            String key = (String) it.next();
            Object value = map.get(key);
            box.put(key, value);
            log.debug("key : {} value : {}", key, value);
            if (value instanceof String){
                box.put(key, cleanXSS((String)value));
            }else{
                box.put(key, value);
            }

        }
        log.debug("***************************************");
    }

    private Box convertFormMaptoBox(HttpServletRequestWrapper req) throws Exception {
        Map<String, String[]> formatData = req.getParameterMap();
        Box bodyBox = new Box();
        Set set = formatData.entrySet();
        Iterator it = set.iterator();

        while ( it.hasNext()){
            Map.Entry<String, String[]> entry = (Map.Entry<String, String[]>)it.next();

            String key = (String)entry.getKey();
            String[] values = (String[]) entry.getValue();
            bodyBox.put(key.toUpperCase(), values.length== 1 ? values[0] : new ArrayList<String>(Arrays.asList(values)));
        }

        return bodyBox;
    }

    private String getHttpBody(NativeWebRequest webRequest) {
        HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        String jsonBody = (String) servletRequest.getAttribute("body");

        if (jsonBody == null) {
            StringBuilder bodyBuilder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(servletRequest.getInputStream(), "UTF-8"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    bodyBuilder.append(line);
                }
                jsonBody = bodyBuilder.toString();
                servletRequest.setAttribute("body", jsonBody);
            } catch (IOException e) {
                throw new RuntimeException("Error reading request body", e);
            }
        }

        return jsonBody;


    }

    private String cleanXSS(String value) {

        value = value.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
        value = value.replaceAll("\\(", "&#40;").replaceAll("\\)", "&#41;");
        value = value.replaceAll("'", "&#39;");
        value = value.replaceAll("eval\\((.*)\\)", "");
        //value = value.replaceAll("[\\\"\\'][\\s]*javascript:[\\s]*(.*)[\\\"\\']", "\"\"");

        return value;
    }

    private void convertParamMapToBox(Map<String,String[]> map, Box box) {
        Iterator<Map.Entry<String, String[]>> entryIterator = map.entrySet().iterator();

        while(entryIterator.hasNext()){
            Map.Entry<String, String[]> entry = entryIterator.next();
            for(String value : entry.getValue()){
                box.put(entry.getKey(), cleanXSS(value));
            }
        }
    }



    public Map<String, Object> getHeaderInfo(HttpServletRequestWrapper request) {
        Map<String, Object> headerInfo = new HashMap<>();
        Enumeration<String> headerNames =  request.getHeaderNames();

        while (headerNames.hasMoreElements()){
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);

            headerInfo.put(key, cleanXSS(value));
        }
        return headerInfo;
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



}
