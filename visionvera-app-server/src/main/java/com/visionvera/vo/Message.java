package com.visionvera.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.visionvera.annotation.Content;

import java.util.Date;

public class Message {
    private Integer id;
    @Content(value="createDate")
    @JSONField(format = "yyyy-MM-dd ")
    private Date createDate;

    private Date modifyDate;

    private Integer version;

    private String titly;

    private String content;

    private String img;

    private String soup;

    private Integer type;

    private Integer beautifulPicture;

    private String src;

    private String alt;

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    public Integer getBeautifulPicture() {
        return beautifulPicture;
    }

    public void setBeautifulPicture(Integer beautifulPicture) {
        this.beautifulPicture = beautifulPicture;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getSoup() {
        return soup;
    }

    public void setSoup(String soup) {
        this.soup = soup;
    }

    public Message() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getTitly() {
        return titly;
    }

    public void setTitly(String titly) {
        this.titly = titly == null ? null : titly.trim();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img == null ? null : img.trim();
    }
}