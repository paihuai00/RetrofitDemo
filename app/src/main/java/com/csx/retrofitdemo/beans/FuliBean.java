package com.csx.retrofitdemo.beans;

import java.util.List;

/**
 * Date: 2020/5/9 create by cuishuxiang description:
 */
public class FuliBean {


  /**
   * error : false
   * results : [{"_id":"5ccdbc219d212239df927a93","createdAt":"2019-05-04T16:21:53.523Z","desc":"2019-05-05","publishedAt":"2019-05-04T16:21:59.733Z","source":"web","type":"福利","url":"https://img.lijinshan.site/images/70def23833844b0fa0e6e74041421e37","used":true,"who":"lijinshanmx"},{"_id":"5cc43919fc3326376038d233","createdAt":"2019-04-27T19:12:25.536Z","desc":"2019-04-27","publishedAt":"2019-04-27T19:12:51.865Z","source":"web","type":"福利","url":"https://img.lijinshan.site/images/999cf7f9728b45deacc0740de53aaff1","used":true,"who":"lijinshanmx"},{"_id":"5c6a4ae99d212226776d3256","createdAt":"2019-02-18T06:04:25.571Z","desc":"2019-02-18","publishedAt":"2019-04-10T00:00:00.0Z","source":"web","type":"福利","url":"https://img.lijinshan.site/images/300f3da4f8644deb8ae453bc63bdff7c","used":true,"who":"lijinshanmx"},{"_id":"5c2dabdb9d21226e068debf9","createdAt":"2019-01-03T06:29:47.895Z","desc":"2019-01-03","publishedAt":"2019-01-03T00:00:00.0Z","source":"web","type":"福利","url":"https://img.lijinshan.site/images/a72c784f6e6048f48916e13e35bbffe0","used":true,"who":"lijinshanmx"},{"_id":"5c25db189d21221e8ada8664","createdAt":"2018-12-28T08:13:12.688Z","desc":"2018-12-28","publishedAt":"2018-12-28T00:00:00.0Z","source":"web","type":"福利","url":"https://img.lijinshan.site/images/b98509f26b1e49679b8b89012b3a576b","used":true,"who":"lijinshanmx"},{"_id":"5c12216d9d21223f5a2baea2","createdAt":"2018-12-13T09:07:57.2Z","desc":"2018-12-13","publishedAt":"2018-12-13T00:00:00.0Z","source":"web","type":"福利","url":"https://img.lijinshan.site/images/3fa310fb9ada4b68a65254aa07c4bb9c","used":true,"who":"lijinshanmx"}]
   */

  private boolean error;
  private List<ResultsBean> results;

  public boolean isError() {
    return error;
  }

  public void setError(boolean error) {
    this.error = error;
  }

  public List<ResultsBean> getResults() {
    return results;
  }

  public void setResults(List<ResultsBean> results) {
    this.results = results;
  }

  public static class ResultsBean {

    /**
     * _id : 5ccdbc219d212239df927a93
     * createdAt : 2019-05-04T16:21:53.523Z
     * desc : 2019-05-05
     * publishedAt : 2019-05-04T16:21:59.733Z
     * source : web
     * type : 福利
     * url : https://img.lijinshan.site/images/70def23833844b0fa0e6e74041421e37
     * used : true
     * who : lijinshanmx
     */

    private String _id;
    private String createdAt;
    private String desc;
    private String publishedAt;
    private String source;
    private String type;
    private String url;
    private boolean used;
    private String who;

    public String get_id() {
      return _id;
    }

    public void set_id(String _id) {
      this._id = _id;
    }

    public String getCreatedAt() {
      return createdAt;
    }

    public void setCreatedAt(String createdAt) {
      this.createdAt = createdAt;
    }

    public String getDesc() {
      return desc;
    }

    public void setDesc(String desc) {
      this.desc = desc;
    }

    public String getPublishedAt() {
      return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
      this.publishedAt = publishedAt;
    }

    public String getSource() {
      return source;
    }

    public void setSource(String source) {
      this.source = source;
    }

    public String getType() {
      return type;
    }

    public void setType(String type) {
      this.type = type;
    }

    public String getUrl() {
      return url;
    }

    public void setUrl(String url) {
      this.url = url;
    }

    public boolean isUsed() {
      return used;
    }

    public void setUsed(boolean used) {
      this.used = used;
    }

    public String getWho() {
      return who;
    }

    public void setWho(String who) {
      this.who = who;
    }


    @Override
    public String toString() {
      return "ResultsBean{" +
          "_id='" + _id + '\'' +
          ", createdAt='" + createdAt + '\'' +
          ", desc='" + desc + '\'' +
          ", publishedAt='" + publishedAt + '\'' +
          ", source='" + source + '\'' +
          ", type='" + type + '\'' +
          ", url='" + url + '\'' +
          ", used=" + used +
          ", who='" + who + '\'' +
          '}';
    }
  }

  @Override
  public String toString() {
    return "FuliBean{" +
        "error=" + error +
        ", results=" + results +
        '}';
  }
}
