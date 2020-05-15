package org.shaneking.spring.test;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import org.junit.runner.RunWith;
import org.shaneking.skava.io.FTN;
import org.shaneking.skava.lang.String0;
import org.shaneking.skava.util.UUID0;
import org.shaneking.test.SKUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import javax.servlet.http.Cookie;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
public class SKSpringMvcUnit extends SKUnit {
  @Autowired
  public MockMvc mockMvc;

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
    String reqStr = Joiner.on(String0.EMPTY).join(Files.readAllLines(reqJsonFile.toPath(), StandardCharsets.UTF_8)).replace("$UUID1$", uuid1);
    ResultActions resultActions = null;
    if (reqAttachFile == null) {
      resultActions = mockMvc.perform(appJson(cookie(post(url).content(reqStr))));
    } else {
      resultActions = mockMvc.perform(multiFormData(cookie(multipart(url).file(new MockMultipartFile("file", reqAttachFile.getName(), "text/plain", Files.readAllBytes(reqAttachFile.toPath()))).param("req", reqStr))));
    }
    resultActions.andExpect(status().isOk()).andDo(print());
    if (respJsonFile != null) {
      resultActions.andExpect(content().string(Joiner.on(String0.EMPTY).join(Files.readAllLines(respJsonFile.toPath(), StandardCharsets.UTF_8).stream().map(String::trim).collect(Collectors.toList())).replace(": ", ":").replace("$UUID1$", uuid1)));
    } else if (respTxtFile != null) {
      for (String s : Files.readAllLines(respTxtFile.toPath(), StandardCharsets.UTF_8)) {
        if (!Strings.isNullOrEmpty(s)) {
          resultActions.andExpect(content().string(containsString(s.replace("$UUID1$", uuid1))));
        }
      }
    }
  }

  public void performJFJ(String url) throws Exception {
    performJFJ(url, skTestITxtFiles());
  }

  public void performJFT(String url) throws Exception {
    performJFT(url, skTestITxtFiles());
  }

  public void performJFJ(String url, File reqAttachFile) throws Exception {
    perform(url, skTestIJsonFiles(), reqAttachFile, skTestOJsonFiles(), null);
  }

  public void performJFT(String url, File reqAttachFile) throws Exception {
    perform(url, skTestIJsonFiles(), reqAttachFile, null, skTestOTxtFiles());
  }

  public void performJJ(String url) throws Exception {
    perform(url, skTestIJsonFiles(), null, skTestOJsonFiles(), null);
  }

  public void performJT(String url) throws Exception {
    perform(url, skTestIJsonFiles(), null, null, skTestOTxtFiles());
  }

  public File skTestIJsonFiles() {
    return skTestIFiles(FTN.JSON);
  }

  public File skTestITxtFiles() {
    return skTestIFiles(FTN.TXT);
  }

  public File skTestOJsonFiles() {
    return skTestOFiles(FTN.JSON);
  }

  public File skTestOTxtFiles() {
    return skTestOFiles(FTN.TXT);
  }
}