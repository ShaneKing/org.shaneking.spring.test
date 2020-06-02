package org.shaneking.spring.test;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import org.shaneking.skava.io.FTN;
import org.shaneking.skava.lang.String0;
import org.shaneking.skava.util.Map0;
import org.shaneking.skava.util.Randomizer;
import org.shaneking.skava.util.UUID0;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import javax.servlet.http.Cookie;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class SKSpringMvcUnit extends SKSpringUnit {
  @Autowired
  @Getter
  public MockMvc mockMvc;
  @Getter
  @Setter
  private Map<String, Randomizer<String>> tstReplaceMap = Map0.newHashMap(Lists.newArrayList("$UUID1$"), Lists.newArrayList((Randomizer<String>) UUID0::cMl33));
  @Getter
  @Setter
  private String tstUploadFileFieldName = "file";
  @Getter
  @Setter
  private String tstUploadParamFieldName = "req";
  @Getter
  @Setter
  private String tstUploadContentType = "text/plain";

  public MockHttpServletRequestBuilder cookie(MockHttpServletRequestBuilder mockHttpServletRequestBuilder) {
    return mockHttpServletRequestBuilder.cookie(new Cookie("skToken", "skTestToken"));
  }

  public MockHttpServletRequestBuilder appJson(MockHttpServletRequestBuilder mockHttpServletRequestBuilder) {
    return mockHttpServletRequestBuilder.contentType(MediaType.APPLICATION_JSON);
  }

  public MockHttpServletRequestBuilder multiFormData(MockHttpServletRequestBuilder mockHttpServletRequestBuilder) {
    return mockHttpServletRequestBuilder.contentType(MediaType.MULTIPART_FORM_DATA);
  }

  public void perform(String url, File reqJsonFile, File reqAttachFile, File respJsonFile, File respTxtFile) throws Exception {
    String uuid1 = UUID0.cMl33();
    Map<String, String> replaceMap = Maps.newHashMap();
    this.getTstReplaceMap().keySet().parallelStream().forEach(k -> replaceMap.put(k, this.getTstReplaceMap().get(k).next()));
    String reqStr = Joiner.on(String0.EMPTY).join(Files.readAllLines(reqJsonFile.toPath(), StandardCharsets.UTF_8));
    for (String key : replaceMap.keySet()) {
      reqStr = reqStr.replace(key, replaceMap.get(key));
    }
    ResultActions resultActions = null;
    if (reqAttachFile == null) {
      resultActions = mockMvc.perform(appJson(cookie(post(url).content(reqStr))));
    } else {
      resultActions = mockMvc.perform(multiFormData(cookie(multipart(url).file(new MockMultipartFile(this.getTstUploadFileFieldName(), reqAttachFile.getName(), this.getTstUploadContentType(), Files.readAllBytes(reqAttachFile.toPath()))).param(this.getTstUploadParamFieldName(), reqStr))));
    }
    resultActions.andExpect(status().isOk()).andDo(print());
    if (respJsonFile != null) {
      String respStr = Joiner.on(String0.EMPTY).join(Files.readAllLines(respJsonFile.toPath(), StandardCharsets.UTF_8).stream().map(String::trim).collect(Collectors.toList()));
      respStr = respStr.replace(": ", ":");
      for (String key : replaceMap.keySet()) {
        respStr = respStr.replace(key, replaceMap.get(key));
      }
      resultActions.andExpect(content().string(respStr));
    } else if (respTxtFile != null) {
      for (String s : Files.readAllLines(respTxtFile.toPath(), StandardCharsets.UTF_8)) {
        if (!Strings.isNullOrEmpty(s)) {
          for (String key : replaceMap.keySet()) {
            s = s.replace(key, replaceMap.get(key));
          }
          resultActions.andExpect(content().string(containsString(s)));
        }
      }
    }
  }

  public void performJFJ(String url) throws Exception {
    performJFJ(url, tstITxtFiles());
  }

  public void performJFT(String url) throws Exception {
    performJFT(url, tstITxtFiles());
  }

  public void performJFJ(String url, File reqAttachFile) throws Exception {
    perform(url, tstIJsonFiles(), reqAttachFile, tstOJsonFiles(), null);
  }

  public void performJFT(String url, File reqAttachFile) throws Exception {
    perform(url, tstIJsonFiles(), reqAttachFile, null, tstOTxtFiles());
  }

  public void performJJ(String url) throws Exception {
    perform(url, tstIJsonFiles(), null, tstOJsonFiles(), null);
  }

  public void performJT(String url) throws Exception {
    perform(url, tstIJsonFiles(), null, null, tstOTxtFiles());
  }

  public File tstIJsonFiles() {
    return tstIFiles(FTN.JSON);
  }

  public File tstITxtFiles() {
    return tstIFiles(FTN.TXT);
  }

  public File tstOJsonFiles() {
    return tstOFiles(FTN.JSON);
  }

  public File tstOTxtFiles() {
    return tstOFiles(FTN.TXT);
  }
}
