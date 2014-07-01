package com.gimmie.model;

import com.gimmie.Configuration;

import org.json.JSONObject;

public class Mayorship extends IDBaseObject {

  public Mayorship(JSONObject object, Configuration configuration) {
    super(object, configuration);
  }

  public String getName() {
    return mObject.optString("name");
  }

  public String getImageURL() {
    String imageURL = mObject.optString("image_url_retina");

    if (imageURL.startsWith("//")) {
      return String.format("http:%s", imageURL);
    } else if (imageURL.startsWith("/")) {
      return String.format("http://gm.llun.in.th:3000%s", imageURL);
    } else {
      return imageURL;
    }
  }

}
